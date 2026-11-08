package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.QrPaymentRepository
import com.example.data.UserPreferencesRepository
import com.example.ui.AppScreen
import com.example.ui.SmartQrViewModel
import com.example.ui.components.VietQrVerifiedBadge
import com.example.ui.screens.AppSettingsScreen
import com.example.ui.screens.BankSettingsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.QrDisplayScreen
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SmartQrPaymentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val userPrefs = UserPreferencesRepository(applicationContext)
        val repository = QrPaymentRepository(
            qrHistoryDao = database.qrHistoryDao(),
            userPreferencesRepository = userPrefs
        )

        setContent {
            val viewModel: SmartQrViewModel = viewModel(
                factory = SmartQrViewModel.provideFactory(repository)
            )
            val appSettings by viewModel.appSettingsState.collectAsStateWithLifecycle()

            SmartQrPaymentTheme(darkTheme = appSettings.darkThemeEnabled) {
                SmartQrPaymentApp(viewModel = viewModel)
            }
        }
    }
}

private data class BottomNavDestination(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartQrPaymentApp(
    viewModel: SmartQrViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bankAccount by viewModel.bankAccountState.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettingsState.collectAsStateWithLifecycle()
    val historyItems by viewModel.historyListState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    val navItems = remember {
        listOf(
            BottomNavDestination(
                screen = AppScreen.HOME,
                label = "Tạo QR",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                testTag = "nav_tab_home"
            ),
            BottomNavDestination(
                screen = AppScreen.QR_DISPLAY,
                label = "Mã QR",
                selectedIcon = Icons.Filled.QrCode2,
                unselectedIcon = Icons.Outlined.QrCode2,
                testTag = "nav_tab_qr"
            ),
            BottomNavDestination(
                screen = AppScreen.BANK_SETTINGS,
                label = "Ngân hàng",
                selectedIcon = Icons.Filled.AccountBalance,
                unselectedIcon = Icons.Outlined.AccountBalance,
                testTag = "nav_tab_bank"
            ),
            BottomNavDestination(
                screen = AppScreen.HISTORY,
                label = "Lịch sử",
                selectedIcon = Icons.Filled.History,
                unselectedIcon = Icons.Outlined.History,
                testTag = "nav_tab_history"
            ),
            BottomNavDestination(
                screen = AppScreen.APP_SETTINGS,
                label = "Cài đặt",
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings,
                testTag = "nav_tab_settings"
            )
        )
    }

    val subScreenTitle = when (uiState.currentScreen) {
        AppScreen.HOME -> null // Trang chủ tự hiển thị Compact Header tích hợp
        AppScreen.QR_DISPLAY -> "Mã QR Thanh Toán"
        AppScreen.BANK_SETTINGS -> "Cài Đặt Ngân Hàng"
        AppScreen.HISTORY -> "Lịch Sử Mã QR"
        AppScreen.APP_SETTINGS -> "Cài Đặt & Giới Thiệu"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 80.dp)
            )
        },
        topBar = {
            AnimatedVisibility(visible = subScreenTitle != null) {
                subScreenTitle?.let { title ->
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            VietQrVerifiedBadge(text = "VietQR")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Smooth Page Transition Animation between tabs
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = {
                    fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(160))
                },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.HOME -> {
                        HomeScreen(
                            uiState = uiState,
                            bankAccount = bankAccount,
                            hapticEnabled = appSettings.hapticFeedbackEnabled,
                            onAmountTextChanged = viewModel::onAmountTextInputChanged,
                            onKeypadDigit = viewModel::onKeypadAppendDigits,
                            onKeypadBackspace = viewModel::onKeypadBackspace,
                            onClearAmount = viewModel::onClearAmount,
                            onQuickAmountSelected = viewModel::onQuickAmountSelected,
                            onToggleQuickChipMode = viewModel::toggleQuickChipMode,
                            onCustomMessageChanged = viewModel::onCustomMessageChanged,
                            onGenerateQrClicked = viewModel::generateQrCode,
                            onEditBankClicked = { viewModel.navigateTo(AppScreen.BANK_SETTINGS) },
                            onQuickSettingsClicked = { viewModel.navigateTo(AppScreen.APP_SETTINGS) }
                        )
                    }

                    AppScreen.QR_DISPLAY -> {
                        QrDisplayScreen(
                            session = uiState.activeQrSession,
                            keepScreenOn = appSettings.keepScreenOnDuringQr,
                            isFullscreenOpen = uiState.isFullscreenQrOpen,
                            screenBrightness = uiState.screenBrightness,
                            isSavingOrSharing = uiState.isSavingOrSharing,
                            onSaveQrToGallery = { viewModel.saveActiveQrToGallery(context) },
                            onShareQr = { viewModel.shareActiveQr(context) },
                            onCreateNewQr = { viewModel.startNewQr(clearAmount = false) },
                            onClearAndReenterAmount = { viewModel.startNewQr(clearAmount = true) },
                            onOpenFullscreen = viewModel::openFullscreenQr,
                            onCloseFullscreen = viewModel::closeFullscreenQr,
                            onBrightnessChange = viewModel::setScreenBrightness,
                            onAdjustBrightnessDelta = viewModel::adjustScreenBrightness,
                            onShowMessage = viewModel::showSnackbar
                        )
                    }

                    AppScreen.BANK_SETTINGS -> {
                        BankSettingsScreen(
                            currentConfig = bankAccount,
                            onSaveBankConfig = viewModel::saveBankAccount,
                            onNavigateBackHome = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                    }

                    AppScreen.HISTORY -> {
                        HistoryScreen(
                            historyItems = historyItems,
                            onOpenHistoryQr = viewModel::openHistoryItem,
                            onDeleteItem = viewModel::deleteHistoryItem,
                            onClearAllHistory = viewModel::clearAllHistory,
                            onNavigateBackHome = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                    }

                    AppScreen.APP_SETTINGS -> {
                        AppSettingsScreen(
                            settings = appSettings,
                            bankAccount = bankAccount,
                            onToggleKeepScreenOn = { viewModel.updateAppSettings(keepScreenOn = it) },
                            onToggleAutoMaxBrightness = { viewModel.updateAppSettings(autoMaxBrightness = it) },
                            onToggleHapticFeedback = { viewModel.updateAppSettings(hapticFeedback = it) },
                            onToggleAutoSaveHistory = { viewModel.updateAppSettings(autoSaveHistory = it) },
                            onToggleDarkTheme = { viewModel.updateAppSettings(darkTheme = it) },
                            onNavigateBackHome = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                    }
                }
            }

            // FLOATING BOTTOM NAVIGATION BAR (Có khoảng cách an toàn với thanh điều hướng Android)
            FloatingFintechBottomBar(
                items = navItems,
                currentScreen = uiState.currentScreen,
                onSelectScreen = viewModel::navigateTo,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun FloatingFintechBottomBar(
    items: List<BottomNavDestination>,
    currentScreen: AppScreen,
    onSelectScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.8f)),
            shadowElevation = 14.dp,
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = currentScreen == item.screen
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1.04f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "tab_scale"
                    )
                    val pillColor by animateColorAsState(
                        targetValue = if (selected) {
                            RoyalBluePrimary.copy(alpha = 0.14f)
                        } else {
                            Color.Transparent
                        },
                        animationSpec = tween(180),
                        label = "tab_pill_color"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (selected) {
                            RoyalBluePrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        animationSpec = tween(180),
                        label = "tab_content_color"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .clip(RoundedCornerShape(20.dp))
                            .background(pillColor)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onSelectScreen(item.screen) }
                            )
                            .padding(vertical = 8.dp)
                            .testTag(item.testTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = contentColor,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = 11.sp
                                ),
                                color = contentColor,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
