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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AppSettingsConfig
import com.example.data.BankAccountConfig
import com.example.domain.VietQrGenerator
import com.example.domain.VietQrValidationResult
import com.example.ui.components.VietQrVerifiedBadge
import com.example.ui.components.pressScaleEffect
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessEmerald

@Composable
fun AppSettingsScreen(
    settings: AppSettingsConfig,
    bankAccount: BankAccountConfig,
    onToggleKeepScreenOn: (Boolean) -> Unit,
    onToggleAutoMaxBrightness: (Boolean) -> Unit,
    onToggleHapticFeedback: (Boolean) -> Unit,
    onToggleAutoSaveHistory: (Boolean) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit,
    onNavigateBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBackHome()
    }

    val sampleDiagnostic = remember(bankAccount) {
        VietQrGenerator.validateAndBuildPayload(
            bankBin = bankAccount.bankBin,
            accountNumber = bankAccount.accountNumber,
            amountVnd = 150_000L,
            transferMessage = bankAccount.defaultTransferMessage
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. CHẾ ĐỘ GIAO DIỆN (LIGHT MODE / DARK MODE SELECTOR CARD)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Giao diện ứng dụng (Theme Mode)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ThemeModeSelectorButton(
                            title = "Light Mode",
                            subtitle = "Xanh Royal & Trắng",
                            icon = Icons.Default.LightMode,
                            selected = !settings.darkThemeEnabled,
                            onClick = { onToggleDarkTheme(false) },
                            modifier = Modifier.weight(1f)
                        )

                        ThemeModeSelectorButton(
                            title = "Dark Mode",
                            subtitle = "Navy Đậm Cao Cấp",
                            icon = Icons.Default.DarkMode,
                            selected = settings.darkThemeEnabled,
                            onClick = { onToggleDarkTheme(true) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("switch_dark_theme")
                        )
                    }
                }
            }

            // 2. CÀI ĐẶT HIỂN THỊ & TRẢI NGHIỆM
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Tùy chỉnh hiển thị & thao tác",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    SettingToggleRow(
                        icon = Icons.Default.ScreenLockPortrait,
                        title = "Giữ màn hình luôn sáng khi mở QR",
                        subtitle = "Ngăn điện thoại tự tắt màn hình khi khách đang quét mã",
                        checked = settings.keepScreenOnDuringQr,
                        onCheckedChange = onToggleKeepScreenOn,
                        testTag = "switch_keep_screen_on"
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    SettingToggleRow(
                        icon = Icons.Default.BrightnessHigh,
                        title = "Tự động tăng tối đa độ sáng (100%)",
                        subtitle = "Tối ưu độ tương phản khi mở Chế độ hiển thị nhanh toàn màn hình",
                        checked = settings.autoMaxBrightnessInFullscreen,
                        onCheckedChange = onToggleAutoMaxBrightness,
                        testTag = "switch_auto_brightness"
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    SettingToggleRow(
                        icon = Icons.Default.Vibration,
                        title = "Rung phản hồi bàn phím số",
                        subtitle = "Phản hồi xúc giác nhẹ khi bấm chọn số tiền bằng một tay",
                        checked = settings.hapticFeedbackEnabled,
                        onCheckedChange = onToggleHapticFeedback,
                        testTag = "switch_haptic"
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))

                    SettingToggleRow(
                        icon = Icons.Default.History,
                        title = "Tự động lưu lịch sử mã QR",
                        subtitle = "Lưu các mã QR đã tạo vào Room Database trên thiết bị",
                        checked = settings.autoSaveQrHistory,
                        onCheckedChange = onToggleAutoSaveHistory,
                        testTag = "switch_auto_history"
                    )
                }
            }

            // 3. KIỂM ĐỊNH CHUẨN KỸ THUẬT VIETQR / NAPAS 247
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = SuccessEmerald,
                            modifier = Modifier.size(26.dp)
                        )
                        Column {
                            Text(
                                text = "Chuẩn kỹ thuật VietQR / NAPAS 247",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Hoạt động 100% Offline • EMVCo CRC-16/CCITT-FALSE",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = SuccessEmerald
                            )
                        }
                    }

                    if (sampleDiagnostic is VietQrValidationResult.Valid) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Kiểm tra tự động mẫu 150.000 VNĐ (${bankAccount.bankShortName}):",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = sampleDiagnostic.payload,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = JetBrainsMonoFontFamily
                                    ),
                                    color = RoyalBluePrimary
                                )
                                Text(
                                    text = "• AID: A000000727 • Service: QRIBFTTA • Currency: 704 • CRC16: ${sampleDiagnostic.crcHex}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 4. MÀN HÌNH PHỤ: GIỚI THIỆU ỨNG DỤNG (ABOUT SMART QR PAYMENT)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(RoyalBlueDark, RoyalBluePrimary, SecondaryBlue)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Smart QR Payment",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = Color.White
                                    )
                                    VietQrVerifiedBadge(
                                        text = "v2.0 Fintech Edition",
                                        isLightOnBlue = true
                                    )
                                }
                                Text(
                                    text = "Giải pháp tạo mã QR chuyển khoản chính xác cho cửa hàng & cá nhân",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Text(
                            text = "Ứng dụng tạo mã VietQR chuẩn EMVCo/NAPAS 247 độc lập, hoạt động 100% offline không cần đăng nhập tài khoản hay máy chủ trung gian. Tương thích với tất cả ứng dụng ngân hàng tại Việt Nam.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }
            }

            // 5. HƯỚNG DẪN TẢI & CÀI ĐẶT FILE APK
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = null,
                            tint = RoyalBluePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Hướng dẫn xuất & cài đặt file APK",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Text(
                        text = "• Từ Google AI Studio: Nhấn biểu tượng Settings / Export ở góc trên -> Chọn \"Download APK\".\n" +
                            "• Từ Android Studio / Terminal: Chạy lệnh ./gradlew assembleDebug -> Nhận file tại app/build/outputs/apk/debug/app-debug.apk.\n" +
                            "• Cài đặt trên điện thoại: Mở file APK vừa tải -> Cho phép cài đặt từ nguồn này -> Nhấn Cài đặt.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeModeSelectorButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) {
            RoyalBluePrimary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        },
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) RoyalBluePrimary else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier.pressScaleEffect(0.96f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) RoyalBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (selected) RoyalBluePrimary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = RoyalBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = RoyalBluePrimary
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
