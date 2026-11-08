package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.BrightnessLow
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.domain.CurrencyUtils
import com.example.domain.VietnameseBanks
import com.example.ui.components.BankLogoBadge
import com.example.ui.components.GradientPrimaryButton
import com.example.ui.components.VietQrVerifiedBadge
import com.example.ui.components.pressScaleEffect
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.util.ActiveQrSession

@Composable
fun QrDisplayScreen(
    session: ActiveQrSession?,
    keepScreenOn: Boolean,
    isFullscreenOpen: Boolean,
    screenBrightness: Float,
    isSavingOrSharing: Boolean,
    onSaveQrToGallery: () -> Unit,
    onShareQr: () -> Unit,
    onCreateNewQr: () -> Unit,
    onClearAndReenterAmount: () -> Unit,
    onOpenFullscreen: () -> Unit,
    onCloseFullscreen: () -> Unit,
    onBrightnessChange: (Float) -> Unit,
    onAdjustBrightnessDelta: (Float) -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(session) {
        isVisible = true
    }

    BackHandler {
        if (isFullscreenOpen) {
            onCloseFullscreen()
        } else {
            onCreateNewQr()
        }
    }

    // Giữ màn hình không tự tắt khi đang hiển thị mã QR
    DisposableEffect(keepScreenOn, session) {
        val window = (context as? Activity)?.window
        if (keepScreenOn && session != null) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    if (session == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier
                    .widthIn(max = 420.dp)
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(28.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Text(
                        text = "Chưa có mã QR nào đang hiển thị",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Hãy nhập số tiền cần nhận ở màn hình Tạo QR để khởi tạo mã thanh toán VietQR chính xác.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    GradientPrimaryButton(
                        text = "QUAY LẠI NHẬP SỐ TIỀN",
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onCreateNewQr,
                        modifier = Modifier.testTag("empty_qr_back_home_button")
                    )
                }
            }
        }
        return
    }

    val matchedBank = remember(session.bankBin) {
        VietnameseBanks.findByBin(session.bankBin)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.96f, animationSpec = tween(240))
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 104.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Top bar: Nút Quay lại + Nhãn VietQR + Nút Toàn màn hình
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onCreateNewQr,
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .pressScaleEffect()
                            .testTag("qr_back_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Quay lại",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    VietQrVerifiedBadge(text = "Chuẩn VietQR • NAPAS 247")

                    Surface(
                        onClick = onOpenFullscreen,
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, RoyalBluePrimary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .pressScaleEffect()
                            .testTag("open_fullscreen_qr_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Phóng to toàn màn hình",
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Phóng to",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = RoyalBluePrimary
                            )
                        }
                    }
                }

                // 2. CARD TRẮNG BO GÓC LỚN CHÍNH GIỮA (Luôn giữ nền trắng tinh khiết để 100% app ngân hàng quét nhạy ngay cả khi bật Dark Mode)
                VietQrPremiumReceiptCard(
                    session = session,
                    brandColorHex = matchedBank?.brandColorHex ?: 0xFF1769FF,
                    onCopyAccount = {
                        copyToClipboard(context, "Số tài khoản", session.accountNumber)
                        onShowMessage("Đã sao chép số tài khoản ${session.accountNumber}")
                    },
                    onTapQrFullscreen = onOpenFullscreen
                )

                // 3. HÀNG NÚT HÀNH ĐỘNG CHÍNH: Lưu ảnh QR & Chia sẻ QR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSaveQrToGallery,
                        enabled = !isSavingOrSharing,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoyalBluePrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .pressScaleEffect()
                            .weight(1f)
                            .height(54.dp)
                            .testTag("save_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Lưu ảnh QR",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lưu ảnh QR",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Button(
                        onClick = onShareQr,
                        enabled = !isSavingOrSharing,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SecondaryBlue,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .pressScaleEffect()
                            .weight(1f)
                            .height(54.dp)
                            .testTag("share_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Chia sẻ QR",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chia sẻ QR",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // 4. HÀNG NÚT PHỤ: Tạo mã mới & Xóa nhập lại
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onCreateNewQr,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.5.dp, RoyalBluePrimary),
                        modifier = Modifier
                            .pressScaleEffect()
                            .weight(1f)
                            .height(50.dp)
                            .testTag("new_qr_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "Tạo mã mới",
                            tint = RoyalBluePrimary,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tạo mã mới",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = RoyalBluePrimary
                        )
                    }

                    OutlinedButton(
                        onClick = onClearAndReenterAmount,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .pressScaleEffect()
                            .weight(1f)
                            .height(50.dp)
                            .testTag("clear_reenter_amount_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Xóa & Nhập lại",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Xóa & Nhập lại",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // 5. Chuỗi kiểm tra EMVCo / CRC-16 rút gọn
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Chuẩn EMVCo VietQR • CRC16: ${session.crcHex}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = session.vietQrPayload,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 10.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = {
                                copyToClipboard(context, "VietQR Payload", session.vietQrPayload)
                                onShowMessage("Đã sao chép chuỗi EMVCo VietQR")
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Sao chép mã EMVCo",
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // CHẾ ĐỘ HIỂN THỊ NHANH TOÀN MÀN HÌNH
    if (isFullscreenOpen) {
        QuickFullscreenQrModal(
            session = session,
            brandColorHex = matchedBank?.brandColorHex ?: 0xFF1769FF,
            brightness = screenBrightness,
            onBrightnessChange = onBrightnessChange,
            onAdjustBrightnessDelta = onAdjustBrightnessDelta,
            onDismiss = onCloseFullscreen
        )
    }
}

@Composable
private fun VietQrPremiumReceiptCard(
    session: ActiveQrSession,
    brandColorHex: Long,
    onCopyAccount: () -> Unit,
    onTapQrFullscreen: () -> Unit
) {
    // Card hiển thị QR luôn có nền trắng tinh khiết (#FFFFFF) để đảm bảo độ tương phản tuyệt đối cho máy ảnh ngân hàng quét
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = RoyalBluePrimary.copy(alpha = 0.22f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.22f)
            )
            .testTag("vietqr_main_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, Color(0xFFD6E4FF))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Gradient Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(RoyalBlueDark, RoyalBluePrimary, SecondaryBlue)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(19.dp)
                        )
                        Text(
                            text = "SMART QR PAYMENT • VIETQR",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = Color.White
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "BIN ${session.bankBin}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Số tiền cực kỳ nổi bật ở trên mã QR
                Text(
                    text = "SỐ TIỀN CHUYỂN KHOẢN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = TextSecondaryLight
                )

                Text(
                    text = CurrencyUtils.formatVnd(session.amountVnd),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = RoyalBluePrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("qr_amount_headline")
                )

                Text(
                    text = CurrencyUtils.numberToVietnameseWords(session.amountVnd),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight,
                    textAlign = TextAlign.Center
                )

                // Khung chứa Mã QR chính giữa: Nền trắng tinh, không che khuất, không biến dạng (FilterQuality.None)
                Surface(
                    onClick = onTapQrFullscreen,
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    border = BorderStroke(2.dp, Color(0xFFEAF2FF)),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .widthIn(max = 280.dp)
                        .fillMaxWidth(0.84f)
                        .aspectRatio(1f)
                        .testTag("qr_bitmap_box")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = session.qrBitmap.asImageBitmap(),
                            contentDescription = "Mã QR chuyển khoản ${session.bankShortName} số tiền ${CurrencyUtils.formatVnd(session.amountVnd)}",
                            contentScale = ContentScale.Fit,
                            filterQuality = FilterQuality.None,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Text(
                    text = "Mở ứng dụng Ngân hàng bất kỳ để quét • Chạm vào QR để phóng to",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryLight,
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    color = Color(0xFFE6ECF5),
                    modifier = Modifier.padding(vertical = 6.dp)
                )

                // Thông tin Ngân hàng & Chủ tài khoản đồng bộ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF5F8FF))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BankLogoBadge(
                        bankShortName = session.bankShortName,
                        brandColorHex = brandColorHex,
                        size = 44.dp
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = session.accountHolder.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "${session.bankShortName} • ${session.accountNumber}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = RoyalBluePrimary
                        )
                    }

                    Surface(
                        onClick = onCopyAccount,
                        shape = RoundedCornerShape(10.dp),
                        color = RoyalBluePrimary.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Sao chép STK",
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Sao chép",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = RoyalBluePrimary
                            )
                        }
                    }
                }

                if (session.transferMessage.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nội dung CK",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryLight
                        )
                        Text(
                            text = session.transferMessage,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimaryLight,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickFullscreenQrModal(
    session: ActiveQrSession,
    brandColorHex: Long,
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    onAdjustBrightnessDelta: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(brightness) {
        val window = (context as? Activity)?.window
        val originalBrightness = window?.attributes?.screenBrightness ?: WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window?.attributes = window.attributes?.apply {
            screenBrightness = brightness.coerceIn(0.15f, 1.0f)
        }
        onDispose {
            window?.attributes = window.attributes?.apply {
                screenBrightness = originalBrightness
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF071636),
                            RoyalBlueDark,
                            Color(0xFF0A2458)
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 24.dp)
                .testTag("fullscreen_qr_modal"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "HIỂN THỊ NHANH TẠI QUẦY",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Màn hình luôn bật • Độ tương phản tối đa",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Surface(
                        onClick = onDismiss,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.18f),
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("close_fullscreen_qr_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng toàn màn hình",
                                tint = Color.White
                            )
                        }
                    }
                }

                // White QR Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BankLogoBadge(
                                bankShortName = session.bankShortName,
                                brandColorHex = brandColorHex,
                                size = 32.dp
                            )
                            Text(
                                text = "${session.bankShortName} • ${session.accountNumber}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimaryLight
                            )
                        }

                        Text(
                            text = session.accountHolder.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = TextPrimaryLight,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = CurrencyUtils.formatVnd(session.amountVnd),
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = RoyalBluePrimary,
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(22.dp))
                                .border(2.5.dp, Color(0xFFD6E4FF), RoundedCornerShape(22.dp))
                                .background(Color.White)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = session.qrBitmap.asImageBitmap(),
                                contentDescription = "Mã QR toàn màn hình",
                                contentScale = ContentScale.Fit,
                                filterQuality = FilterQuality.None,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        if (session.transferMessage.isNotBlank()) {
                            Text(
                                text = "Nội dung: ${session.transferMessage}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = TextSecondaryLight,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Brightness control bar
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.14f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Độ sáng màn hình",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                            Text(
                                text = "${(brightness * 100).toInt()}%",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onAdjustBrightnessDelta(-0.15f) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("decrease_brightness_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BrightnessLow,
                                    contentDescription = "Giảm độ sáng"
                                )
                            }

                            Slider(
                                value = brightness,
                                onValueChange = onBrightnessChange,
                                valueRange = 0.15f..1.0f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.White,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                )
                            )

                            OutlinedButton(
                                onClick = { onAdjustBrightnessDelta(0.15f) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("increase_brightness_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BrightnessHigh,
                                    contentDescription = "Tăng độ sáng"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
}
