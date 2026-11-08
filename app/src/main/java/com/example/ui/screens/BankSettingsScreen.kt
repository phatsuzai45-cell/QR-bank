package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankAccountConfig
import com.example.domain.CurrencyUtils
import com.example.domain.VietnameseBank
import com.example.domain.VietnameseBanks
import com.example.ui.components.BankLogoBadge
import com.example.ui.components.GradientPrimaryButton
import com.example.ui.components.UltraFintechBackdrop
import com.example.ui.components.VietQrVerifiedBadge
import com.example.ui.components.pressScaleEffect
import com.example.ui.components.staggeredEntrance
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankSettingsScreen(
    currentConfig: BankAccountConfig,
    darkTheme: Boolean = false,
    onSaveBankConfig: (bankBin: String, accountNumber: String, accountHolder: String, defaultMessage: String) -> Unit,
    onNavigateBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBackHome()
    }

    var selectedBankBin by remember(currentConfig.bankBin) { mutableStateOf(currentConfig.bankBin) }
    var accountNumber by remember(currentConfig.accountNumber) { mutableStateOf(currentConfig.accountNumber) }
    var accountHolder by remember(currentConfig.accountHolder) { mutableStateOf(currentConfig.accountHolder) }
    var defaultMessage by remember(currentConfig.defaultTransferMessage) {
        mutableStateOf(currentConfig.defaultTransferMessage)
    }

    var showBankPickerSheet by remember { mutableStateOf(false) }
    var bankSearchQuery by remember { mutableStateOf("") }

    LaunchedEffect(currentConfig) {
        selectedBankBin = currentConfig.bankBin
        accountNumber = currentConfig.accountNumber
        accountHolder = currentConfig.accountHolder
        defaultMessage = currentConfig.defaultTransferMessage
    }

    val selectedBank = remember(selectedBankBin) {
        VietnameseBanks.findByBin(selectedBankBin) ?: VietnameseBanks.supportedBanks.first()
    }

    UltraFintechBackdrop(
        headerHeightDp = 195.dp,
        darkTheme = darkTheme
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp, bottom = 108.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. TOP HEADER BAR TRÊN NỀN SÓNG XANH
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(0),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onNavigateBackHome,
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .pressScaleEffect()
                            .size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Quay lại",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Text(
                        text = "Tài khoản ngân hàng",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White
                    )

                    VietQrVerifiedBadge(
                        text = "NAPAS 247",
                        isLightOnBlue = true
                    )
                }

                // 2. Thẻ Ngân hàng trực quan cao cấp
                BankVirtualFintechCard(
                    bank = selectedBank,
                    accountNumber = accountNumber.ifBlank { "CHƯA NHẬP SỐ TK" },
                    accountHolder = CurrencyUtils.normalizeAccountHolderName(accountHolder).ifBlank { "TEN CHU TAI KHOAN" },
                    defaultMessage = CurrencyUtils.sanitizeTransferMessage(defaultMessage),
                    modifier = Modifier.staggeredEntrance(1)
                )

                // 3. Form Cài đặt tài khoản thụ hưởng
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(2),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountBalance,
                                contentDescription = null,
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Thiết lập tài khoản nhận tiền",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Chọn ngân hàng
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Ngân hàng thụ hưởng",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Surface(
                                onClick = {
                                    bankSearchQuery = ""
                                    showBankPickerSheet = true
                                },
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                border = BorderStroke(1.dp, RoyalBluePrimary.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .pressScaleEffect(0.98f)
                                    .fillMaxWidth()
                                    .testTag("bank_selector_field")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        BankLogoBadge(
                                            bankShortName = selectedBank.shortName,
                                            brandColorHex = selectedBank.brandColorHex,
                                            size = 42.dp
                                        )
                                        Column {
                                            Text(
                                                text = "${selectedBank.shortName} • BIN ${selectedBank.bin}",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = selectedBank.fullName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Chọn ngân hàng",
                                        tint = RoyalBluePrimary
                                    )
                                }
                            }
                        }

                        // Số tài khoản
                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { input ->
                                accountNumber = input.filter { it.isLetterOrDigit() }.take(25)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bank_account_number_input"),
                            label = { Text("Số tài khoản ngân hàng") },
                            placeholder = { Text("Nhập chính xác số tài khoản nhận tiền") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CreditCard,
                                    contentDescription = null,
                                    tint = RoyalBluePrimary
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBluePrimary
                            )
                        )

                        // Tên chủ tài khoản
                        OutlinedTextField(
                            value = accountHolder,
                            onValueChange = { accountHolder = it.take(50) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bank_account_holder_input"),
                            label = { Text("Tên chủ tài khoản (Không dấu)") },
                            placeholder = { Text("VD: NGUYEN VAN AN") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = null,
                                    tint = RoyalBluePrimary
                                )
                            },
                            supportingText = {
                                Text(
                                    "Chuẩn hóa hiển thị: ${CurrencyUtils.normalizeAccountHolderName(accountHolder).ifBlank { "..." }}"
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBluePrimary
                            )
                        )

                        // Nội dung chuyển khoản mặc định
                        OutlinedTextField(
                            value = defaultMessage,
                            onValueChange = { defaultMessage = it.take(50) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bank_default_message_input"),
                            label = { Text("Nội dung chuyển khoản mặc định") },
                            placeholder = { Text("VD: THANH TOAN DON HANG") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = RoyalBluePrimary
                                )
                            },
                            supportingText = {
                                Text(
                                    "Tự động chuẩn hóa ASCII NAPAS: ${CurrencyUtils.sanitizeTransferMessage(defaultMessage).ifBlank { "Không có" }}"
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoyalBluePrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        GradientPrimaryButton(
                            text = "Lưu tài khoản ngân hàng",
                            icon = Icons.Outlined.Save,
                            onClick = {
                                onSaveBankConfig(
                                    selectedBank.bin,
                                    accountNumber,
                                    accountHolder,
                                    defaultMessage
                                )
                            },
                            modifier = Modifier.testTag("save_bank_settings_button")
                        )
                    }
                }

                // 4. Cam kết bảo mật
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(3)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = RoyalBluePrimary,
                            modifier = Modifier.size(26.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Lưu trữ cục bộ an toàn (DataStore)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Chỉ sử dụng Số tài khoản công khai và Mã BIN ngân hàng để tạo mã nhận tiền VietQR. Tuyệt đối không yêu cầu mật khẩu hay OTP.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBankPickerSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val filteredBanks = remember(bankSearchQuery) {
            VietnameseBanks.search(bankSearchQuery)
        }

        ModalBottomSheet(
            onDismissRequest = { showBankPickerSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Chọn ngân hàng thụ hưởng (${VietnameseBanks.supportedBanks.size} ngân hàng NAPAS)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                OutlinedTextField(
                    value = bankSearchQuery,
                    onValueChange = { bankSearchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_search_input"),
                    placeholder = { Text("Tìm tên ngân hàng hoặc mã BIN (VD: Vietcombank, MB, ACB...)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredBanks, key = { it.bin }) { bank ->
                        val isSelected = bank.bin == selectedBankBin
                        Surface(
                            onClick = {
                                selectedBankBin = bank.bin
                                showBankPickerSheet = false
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            },
                            border = if (isSelected) {
                                BorderStroke(1.5.dp, RoyalBluePrimary)
                            } else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("bank_option_${bank.bin}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    BankLogoBadge(
                                        bankShortName = bank.shortName,
                                        brandColorHex = bank.brandColorHex,
                                        size = 40.dp
                                    )
                                    Column {
                                        Text(
                                            text = bank.shortName,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "${bank.fullName} • BIN: ${bank.bin}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Đã chọn",
                                        tint = RoyalBluePrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BankVirtualFintechCard(
    bank: VietnameseBank,
    accountNumber: String,
    accountHolder: String,
    defaultMessage: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = Color(0xFF04143A).copy(alpha = 0.45f),
                spotColor = Color(0xFF04143A).copy(alpha = 0.45f)
            ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF062360),
                            Color(0xFF0C3B9E),
                            Color(0xFF1769FF)
                        )
                    )
                )
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.85f, size.height * 0.2f),
                            radius = size.width * 0.5f
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.2f),
                        radius = size.width * 0.5f
                    )
                }
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BankLogoBadge(
                            bankShortName = bank.shortName,
                            brandColorHex = bank.brandColorHex,
                            size = 42.dp,
                            lightBackground = true
                        )
                        Column {
                            Text(
                                text = bank.shortName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "BIN ${bank.bin} • SWIFT ${bank.swiftCode}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    VietQrVerifiedBadge(
                        text = "VIETQR 24/7",
                        isLightOnBlue = true
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "SỐ TÀI KHOẢN THỤ HƯỞNG",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.8.sp
                        ),
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Text(
                        text = accountNumber,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "CHỦ TÀI KHOẢN",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Text(
                            text = accountHolder,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }

                    if (defaultMessage.isNotBlank()) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "NỘI DUNG MẶC ĐỊNH",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                            Text(
                                text = defaultMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
