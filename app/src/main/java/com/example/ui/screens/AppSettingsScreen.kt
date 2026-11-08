package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.ScreenLockPortrait
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettingsConfig
import com.example.data.BankAccountConfig
import com.example.domain.CurrencyUtils
import com.example.domain.VietQrGenerator
import com.example.domain.VietQrValidationResult
import com.example.domain.VietnameseBanks
import com.example.ui.components.BankLogoBadge
import com.example.ui.components.PhatPayVectorLogo
import com.example.ui.components.UltraFintechBackdrop
import com.example.ui.components.pressScaleEffect
import com.example.ui.components.staggeredEntrance
import com.example.ui.theme.JetBrainsMonoFontFamily
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
    onNavigateToBankSettings: () -> Unit = {},
    onNavigateBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBackHome()
    }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showFavoriteDenominationsDialog by remember { mutableStateOf(false) }

    val matchedBank = remember(bankAccount.bankBin) {
        VietnameseBanks.findByBin(bankAccount.bankBin)
    }

    val sampleDiagnostic = remember(bankAccount) {
        VietQrGenerator.validateAndBuildPayload(
            bankBin = bankAccount.bankBin,
            accountNumber = bankAccount.accountNumber,
            amountVnd = 150_000L,
            transferMessage = bankAccount.defaultTransferMessage
        )
    }

    UltraFintechBackdrop(
        headerHeightDp = 180.dp,
        darkTheme = settings.darkThemeEnabled
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
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
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cài đặt",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White
                    )
                }

                // 2. THẺ THÔNG TIN ỨNG DỤNG: Smart QR Payment • Phiên bản 1.0.0 >
                Card(
                    onClick = { showAboutDialog = true },
                    modifier = Modifier
                        .pressScaleEffect(0.985f)
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(22.dp),
                            ambientColor = RoyalBluePrimary.copy(alpha = 0.16f),
                            spotColor = RoyalBluePrimary.copy(alpha = 0.16f)
                        )
                        .staggeredEntrance(1),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            PhatPayVectorLogo(
                                size = 50.dp,
                                isDarkBackground = true,
                                showRoundedBox = true
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "PHATPAY",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontStyle = FontStyle.Italic,
                                        letterSpacing = 0.6.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Phiên bản 1.0.0 • Tạo QR nhanh – Thu tiền dễ dàng",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Giới thiệu ứng dụng",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 3. MỤC TÀI KHOẢN NGÂN HÀNG + NÚT CHỈNH SỬA (Giống hệt thiết kế mẫu)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tài khoản ngân hàng",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Chỉnh sửa",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = RoyalBluePrimary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onNavigateToBankSettings() }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Card(
                        onClick = onNavigateToBankSettings,
                        modifier = Modifier
                            .pressScaleEffect(0.985f)
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(20.dp),
                                ambientColor = RoyalBluePrimary.copy(alpha = 0.12f),
                                spotColor = RoyalBluePrimary.copy(alpha = 0.12f)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                BankLogoBadge(
                                    bankShortName = bankAccount.bankShortName,
                                    brandColorHex = matchedBank?.brandColorHex ?: 0xFF00A86B,
                                    size = 44.dp
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = bankAccount.bankShortName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${bankAccount.accountNumber} - ${bankAccount.accountHolder}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Chỉnh sửa ngân hàng",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 4. NHÓM CÀI ĐẶT THU TIỀN & TƯƠNG TÁC (Giống bố cục màn hình Cài đặt mẫu)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(3),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Mệnh giá yêu thích
                        SettingsNavigationRow(
                            icon = Icons.Outlined.Paid,
                            iconTint = RoyalBluePrimary,
                            title = "Mệnh giá yêu thích",
                            trailingValue = "10K - 500K",
                            onClick = { showFavoriteDenominationsDialog = true }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))

                        // Chế độ thu tiền toàn màn hình
                        SettingsSwitchRow(
                            icon = Icons.Outlined.Fullscreen,
                            iconTint = RoyalBluePrimary,
                            title = "Chế độ thu tiền toàn màn hình",
                            subtitle = "Tự động đẩy sáng 100% khi phóng to QR",
                            checked = settings.autoMaxBrightnessInFullscreen,
                            onCheckedChange = onToggleAutoMaxBrightness,
                            testTag = "switch_auto_brightness"
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))

                        // Giữ màn hình luôn sáng
                        SettingsSwitchRow(
                            icon = Icons.Outlined.ScreenLockPortrait,
                            iconTint = RoyalBluePrimary,
                            title = "Giữ màn hình luôn sáng khi mở QR",
                            subtitle = "Ngăn điện thoại tự tắt khi khách đang quét",
                            checked = settings.keepScreenOnDuringQr,
                            onCheckedChange = onToggleKeepScreenOn,
                            testTag = "switch_keep_screen_on"
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))

                        // Tự động lưu lịch sử giao dịch QR
                        SettingsSwitchRow(
                            icon = Icons.Outlined.History,
                            iconTint = RoyalBluePrimary,
                            title = "Tự động lưu lịch sử tạo QR",
                            subtitle = "Lưu vào Room Database để thống kê thu tiền",
                            checked = settings.autoSaveQrHistory,
                            onCheckedChange = onToggleAutoSaveHistory,
                            testTag = "switch_auto_history"
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))

                        // Rung khi tạo QR & bấm phím
                        SettingsSwitchRow(
                            icon = Icons.Outlined.VolumeUp,
                            iconTint = RoyalBluePrimary,
                            title = "Rung phản hồi khi tạo QR",
                            subtitle = "Phản hồi xúc giác nhẹ khi bấm phím số",
                            checked = settings.hapticFeedbackEnabled,
                            onCheckedChange = onToggleHapticFeedback,
                            testTag = "switch_haptic"
                        )
                    }
                }

                // 5. NHÓM GIAO DIỆN, NGÔN NGỮ & CHẾ ĐỘ TỐI (DARK MODE)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(4),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        SettingsNavigationRow(
                            icon = Icons.Outlined.LightMode,
                            iconTint = Color(0xFFF59E0B),
                            title = "Giao diện",
                            trailingValue = if (settings.darkThemeEnabled) "Tối (Navy)" else "Sáng",
                            onClick = { onToggleDarkTheme(!settings.darkThemeEnabled) }
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))

                        SettingsNavigationRow(
                            icon = Icons.Outlined.Language,
                            iconTint = Color(0xFFEF4444),
                            title = "Ngôn ngữ",
                            trailingValue = "Tiếng Việt",
                            onClick = {}
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))

                        SettingsSwitchRow(
                            icon = Icons.Outlined.DarkMode,
                            iconTint = Color(0xFF475569),
                            title = "Chế độ tối (Dark Mode)",
                            subtitle = "Nền xanh đen & thẻ Navy cao cấp",
                            checked = settings.darkThemeEnabled,
                            onCheckedChange = onToggleDarkTheme,
                            testTag = "switch_dark_theme"
                        )
                    }
                }

                // 6. KIỂM ĐỊNH CHUẨN VIETQR EMVCo & HƯỚNG DẪN CÀI ĐẶT APK
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(5),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Verified,
                                contentDescription = null,
                                tint = SuccessEmerald,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = "Chuẩn EMVCo VietQR • Hoạt động 100% Offline",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (sampleDiagnostic is VietQrValidationResult.Valid) {
                            Text(
                                text = "AID: A000000727 • QRIBFTTA • BIN: ${bankAccount.bankBin} • CRC16: ${sampleDiagnostic.crcHex}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = SuccessEmerald
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Android,
                                contentDescription = null,
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Xuất & Cài đặt APK: Chọn Settings/Export -> Download APK hoặc chạy ./gradlew assembleDebug.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "PHATPAY v1.0.0",
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PhatPayVectorLogo(
                            size = 54.dp,
                            isDarkBackground = true,
                            showRoundedBox = true
                        )
                        PhatPayVectorLogo(
                            size = 54.dp,
                            isDarkBackground = false,
                            showRoundedBox = true
                        )
                        Column {
                            Text(
                                text = "Logo Sáng & Tối",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Tạo QR nhanh – Thu tiền dễ dàng",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        "Ứng dụng PHATPAY chuyên tạo mã QR chuyển khoản ngân hàng chuẩn VietQR / NAPAS 247 với số tiền chính xác.\n\n" +
                            "• Phong cách: Futuristic Neon Blue\n" +
                            "• Hoạt động độc lập 100% Offline\n" +
                            "• Lưu trữ an toàn bằng Jetpack DataStore & Room Database"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Đóng", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showFavoriteDenominationsDialog) {
        AlertDialog(
            onDismissRequest = { showFavoriteDenominationsDialog = false },
            title = {
                Text(
                    text = "Mệnh giá chọn nhanh",
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Text(
                    "Các nút mệnh giá nhanh trên Trang chủ đã được tối ưu sẵn cho thanh toán tại Việt Nam:\n\n" +
                        CurrencyUtils.defaultQuickAmounts.joinToString("  •  ") {
                            CurrencyUtils.formatShortDong(it)
                        } +
                        "\n\nBạn có thể chuyển đổi linh hoạt giữa chế độ \"Chọn mức\" và \"Cộng dồn (+)\" ngay trên màn hình Tạo QR."
                )
            },
            confirmButton = {
                TextButton(onClick = { showFavoriteDenominationsDialog = false }) {
                    Text("Đã hiểu", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    trailingValue: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (trailingValue.isNotEmpty()) {
                Text(
                    text = trailingValue,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
