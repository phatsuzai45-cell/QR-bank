package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettingsConfig
import com.example.data.BankAccountConfig
import com.example.data.QrHistoryEntity
import com.example.data.QrPaymentRepository
import com.example.domain.CurrencyUtils
import com.example.domain.VietQrGenerator
import com.example.domain.VietQrValidationResult
import com.example.domain.VietnameseBanks
import com.example.util.ActiveQrSession
import com.example.util.QrImageExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppScreen(val route: String) {
    HOME("home"),
    QR_DISPLAY("qr_display"),
    BANK_SETTINGS("bank_settings"),
    HISTORY("history"),
    APP_SETTINGS("app_settings")
}

data class HomeUiState(
    val currentScreen: AppScreen = AppScreen.HOME,
    val amountVnd: Long = 150_000L,
    val customMessage: String = "",
    val isAdditiveQuickChipMode: Boolean = false,
    val validationError: String? = null,
    val snackbarMessage: String? = null,
    val isGeneratingQr: Boolean = false,
    val isSavingOrSharing: Boolean = false,
    val activeQrSession: ActiveQrSession? = null,
    val isFullscreenQrOpen: Boolean = false,
    val screenBrightness: Float = 0.9f
)

class SmartQrViewModel(
    private val repository: QrPaymentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val bankAccountState: StateFlow<BankAccountConfig> = repository.bankAccountFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BankAccountConfig(
                bankBin = "970436",
                bankShortName = "Vietcombank",
                bankFullName = "Ngân hàng TMCP Ngoại thương Việt Nam",
                accountNumber = "1029384756",
                accountHolder = "NGUYEN VAN AN",
                defaultTransferMessage = "THANH TOAN DON HANG"
            )
        )

    val appSettingsState: StateFlow<AppSettingsConfig> = repository.appSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettingsConfig()
        )

    val historyListState: StateFlow<List<QrHistoryEntity>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun navigateTo(screen: AppScreen) {
        _uiState.update {
            it.copy(
                currentScreen = screen,
                validationError = null
            )
        }
    }

    fun onAmountTextInputChanged(rawInput: String) {
        val parsed = CurrencyUtils.parseDigitsToAmount(rawInput)
        val clamped = parsed.coerceAtMost(999_999_999L)
        _uiState.update {
            it.copy(
                amountVnd = clamped,
                validationError = null
            )
        }
    }

    fun onKeypadAppendDigits(digits: String) {
        val current = _uiState.value.amountVnd
        val currentStr = if (current == 0L) "" else current.toString()
        val combined = (currentStr + digits).trimStart('0')
        if (combined.length > 9) {
            _uiState.update {
                it.copy(validationError = "Số tiền tối đa mỗi giao dịch NAPAS 247 là 499.999.999 VNĐ")
            }
            return
        }
        val nextAmount = combined.toLongOrNull() ?: 0L
        _uiState.update {
            it.copy(
                amountVnd = nextAmount,
                validationError = null
            )
        }
    }

    fun onKeypadBackspace() {
        val current = _uiState.value.amountVnd
        val nextAmount = current / 10L
        _uiState.update {
            it.copy(
                amountVnd = nextAmount,
                validationError = null
            )
        }
    }

    fun onClearAmount() {
        _uiState.update {
            it.copy(
                amountVnd = 0L,
                validationError = null
            )
        }
    }

    fun toggleQuickChipMode() {
        _uiState.update {
            it.copy(isAdditiveQuickChipMode = !it.isAdditiveQuickChipMode)
        }
    }

    fun onQuickAmountSelected(quickAmount: Long) {
        val current = _uiState.value
        val nextAmount = if (current.isAdditiveQuickChipMode) {
            (current.amountVnd + quickAmount).coerceAtMost(VietQrGenerator.MAX_VALID_AMOUNT)
        } else {
            quickAmount
        }
        _uiState.update {
            it.copy(
                amountVnd = nextAmount,
                validationError = null
            )
        }
    }

    fun onCustomMessageChanged(message: String) {
        _uiState.update {
            it.copy(customMessage = message.take(50))
        }
    }

    fun generateQrCode() {
        val currentUi = _uiState.value
        val bank = bankAccountState.value
        val settings = appSettingsState.value

        if (!bank.isConfigured) {
            _uiState.update {
                it.copy(
                    validationError = "Vui lòng kiểm tra và lưu thông tin tài khoản ngân hàng trước.",
                    currentScreen = AppScreen.BANK_SETTINGS
                )
            }
            return
        }

        val effectiveMessage = currentUi.customMessage.ifBlank { bank.defaultTransferMessage }
        val validation = VietQrGenerator.validateAndBuildPayload(
            bankBin = bank.bankBin,
            accountNumber = bank.accountNumber,
            amountVnd = currentUi.amountVnd,
            transferMessage = effectiveMessage
        )

        when (validation) {
            is VietQrValidationResult.Invalid -> {
                _uiState.update {
                    it.copy(validationError = validation.errorMessage)
                }
            }
            is VietQrValidationResult.Valid -> {
                _uiState.update { it.copy(isGeneratingQr = true, validationError = null) }
                viewModelScope.launch {
                    val bitmap = withContext(Dispatchers.Default) {
                        VietQrGenerator.generateHighResQrBitmap(
                            payload = validation.payload,
                            sizePx = 1024
                        )
                    }

                    val session = ActiveQrSession(
                        amountVnd = currentUi.amountVnd,
                        bankBin = bank.bankBin,
                        bankShortName = bank.bankShortName,
                        bankFullName = bank.bankFullName,
                        accountNumber = bank.accountNumber.trim(),
                        accountHolder = CurrencyUtils.normalizeAccountHolderName(bank.accountHolder),
                        transferMessage = validation.sanitizedMessage,
                        vietQrPayload = validation.payload,
                        crcHex = validation.crcHex,
                        qrBitmap = bitmap
                    )

                    if (settings.autoSaveQrHistory) {
                        repository.insertHistory(
                            QrHistoryEntity(
                                amountVnd = session.amountVnd,
                                bankBin = session.bankBin,
                                bankShortName = session.bankShortName,
                                bankFullName = session.bankFullName,
                                accountNumber = session.accountNumber,
                                accountHolder = session.accountHolder,
                                transferMessage = session.transferMessage,
                                vietQrPayload = session.vietQrPayload,
                                crcHex = session.crcHex,
                                createdAt = session.createdAt
                            )
                        )
                    }

                    _uiState.update {
                        it.copy(
                            isGeneratingQr = false,
                            activeQrSession = session,
                            currentScreen = AppScreen.QR_DISPLAY,
                            screenBrightness = if (settings.autoMaxBrightnessInFullscreen) 1.0f else 0.85f
                        )
                    }
                }
            }
        }
    }

    fun openHistoryItem(entity: QrHistoryEntity) {
        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                VietQrGenerator.generateHighResQrBitmap(
                    payload = entity.vietQrPayload,
                    sizePx = 1024
                )
            }
            val session = ActiveQrSession(
                amountVnd = entity.amountVnd,
                bankBin = entity.bankBin,
                bankShortName = entity.bankShortName,
                bankFullName = entity.bankFullName,
                accountNumber = entity.accountNumber,
                accountHolder = entity.accountHolder,
                transferMessage = entity.transferMessage,
                vietQrPayload = entity.vietQrPayload,
                crcHex = entity.crcHex,
                qrBitmap = bitmap,
                createdAt = entity.createdAt
            )
            _uiState.update {
                it.copy(
                    amountVnd = entity.amountVnd,
                    customMessage = entity.transferMessage,
                    activeQrSession = session,
                    currentScreen = AppScreen.QR_DISPLAY
                )
            }
        }
    }

    fun startNewQr(clearAmount: Boolean = true) {
        _uiState.update {
            it.copy(
                amountVnd = if (clearAmount) 0L else it.amountVnd,
                customMessage = "",
                isFullscreenQrOpen = false,
                currentScreen = AppScreen.HOME,
                validationError = null
            )
        }
    }

    fun openFullscreenQr() {
        val settings = appSettingsState.value
        _uiState.update {
            it.copy(
                isFullscreenQrOpen = true,
                screenBrightness = if (settings.autoMaxBrightnessInFullscreen) 1.0f else it.screenBrightness
            )
        }
    }

    fun closeFullscreenQr() {
        _uiState.update {
            it.copy(isFullscreenQrOpen = false)
        }
    }

    fun setScreenBrightness(value: Float) {
        _uiState.update {
            it.copy(screenBrightness = value.coerceIn(0.15f, 1.0f))
        }
    }

    fun adjustScreenBrightness(delta: Float) {
        _uiState.update {
            it.copy(screenBrightness = (it.screenBrightness + delta).coerceIn(0.15f, 1.0f))
        }
    }

    fun saveBankAccount(
        bankBin: String,
        accountNumber: String,
        accountHolder: String,
        defaultTransferMessage: String
    ) {
        val bank = VietnameseBanks.findByBin(bankBin) ?: VietnameseBanks.supportedBanks.first()
        val cleanAccount = accountNumber.trim().replace(" ", "")
        val normalizedHolder = CurrencyUtils.normalizeAccountHolderName(accountHolder)
        val cleanDefaultMsg = CurrencyUtils.sanitizeTransferMessage(defaultTransferMessage)

        if (cleanAccount.length < 4) {
            showSnackbar("Số tài khoản phải có ít nhất 4 ký tự.")
            return
        }
        if (normalizedHolder.length < 2) {
            showSnackbar("Vui lòng nhập tên chủ tài khoản hợp lệ.")
            return
        }

        viewModelScope.launch {
            repository.saveBankAccount(
                BankAccountConfig(
                    bankBin = bank.bin,
                    bankShortName = bank.shortName,
                    bankFullName = bank.fullName,
                    accountNumber = cleanAccount,
                    accountHolder = normalizedHolder,
                    defaultTransferMessage = cleanDefaultMsg
                )
            )
            showSnackbar("Đã lưu tài khoản ${bank.shortName} ($cleanAccount) thành công!")
        }
    }

    fun updateAppSettings(
        keepScreenOn: Boolean? = null,
        autoMaxBrightness: Boolean? = null,
        hapticFeedback: Boolean? = null,
        autoSaveHistory: Boolean? = null,
        darkTheme: Boolean? = null
    ) {
        viewModelScope.launch {
            repository.updateAppSettings(
                keepScreenOn = keepScreenOn,
                autoMaxBrightness = autoMaxBrightness,
                hapticFeedback = hapticFeedback,
                autoSaveHistory = autoSaveHistory,
                darkTheme = darkTheme
            )
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryById(id)
            showSnackbar("Đã xóa mã QR khỏi lịch sử.")
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
            showSnackbar("Đã xóa toàn bộ lịch sử mã QR.")
        }
    }

    fun saveActiveQrToGallery(context: Context) {
        val session = _uiState.value.activeQrSession ?: return
        _uiState.update { it.copy(isSavingOrSharing = true) }
        viewModelScope.launch {
            val result = QrImageExporter.saveQrPosterToGallery(context, session)
            _uiState.update { it.copy(isSavingOrSharing = false) }
            result.onSuccess { fileName ->
                showSnackbar("Đã lưu ảnh mã QR vào Thư viện ảnh ($fileName)")
            }.onFailure { err ->
                showSnackbar("Không thể lưu ảnh: ${err.localizedMessage ?: "Lỗi không xác định"}")
            }
        }
    }

    fun shareActiveQr(context: Context) {
        val session = _uiState.value.activeQrSession ?: return
        _uiState.update { it.copy(isSavingOrSharing = true) }
        viewModelScope.launch {
            val result = QrImageExporter.shareQrPosterImage(context, session)
            _uiState.update { it.copy(isSavingOrSharing = false) }
            result.onFailure { err ->
                showSnackbar("Không thể chia sẻ mã QR: ${err.localizedMessage ?: "Lỗi không xác định"}")
            }
        }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun consumeSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    companion object {
        fun provideFactory(repository: QrPaymentRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SmartQrViewModel(repository) as T
                }
            }
    }
}
