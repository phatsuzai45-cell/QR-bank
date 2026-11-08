package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.BrightnessHigh
import androidx.compose.material.icons.outlined.BrightnessLow
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.ui.components.PhatPayVectorLogo
import com.example.ui.components.UltraFintechBackdrop
import com.example.ui.components.pressScaleEffect
import com.example.ui.components.staggeredEntrance
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.util.ActiveQrSession
import kotlinx.coroutines.launch

@Composable
fun QrDisplayScreen(
    session: ActiveQrSession?,
    keepScreenOn: Boolean,
    darkTheme: Boolean = false,
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
        UltraFintechBackdrop(headerHeightDp = 210.dp, darkTheme = darkTheme) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(26.dp),
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
                                    imageVector = Icons.Outlined.QrCode2,
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
                            text = "Quay lại nhập số tiền",
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            showTrailingChevron = false,
                            onClick = onCreateNewQr,
                            modifier = Modifier.testTag("empty_qr_back_home_button")
                        )
                    }
                }
            }
        }
        return
    }

    val matchedBank = remember(session.bankBin) {
        VietnameseBanks.findByBin(session.bankBin)
    }

    UltraFintechBackdrop(
        headerHeightDp = 215.dp,
        darkTheme = darkTheme
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. TOP HEADER BAR TRÊN NỀN SÓNG XANH: "<   Mã QR của bạn   [⛶]"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(0),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onCreateNewQr,
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .pressScaleEffect()
                            .size(40.dp)
                            .testTag("qr_back_button")
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
                        text = "Mã QR của bạn",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = Color.White
                    )

                    Surface(
                        onClick = onOpenFullscreen,
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .pressScaleEffect()
                            .size(40.dp)
                            .testTag("open_fullscreen_qr_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Fullscreen,
                                contentDescription = "Hiển thị toàn màn hình",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // 2. THẺ TRẮNG LỚN CHÍNH GIỮA: "✔ Sẵn sàng quét" + Số tiền nổi bật + QR sắc nét + Ngân hàng | Chủ TK + Nội dung pill
                UltraQrWhiteReceiptCard(
                    session = session,
                    brandColorHex = matchedBank?.brandColorHex ?: 0xFF00A86B,
                    onCopyAccount = {
                        copyToClipboard(context, "Số tài khoản", session.accountNumber)
                        onShowMessage("Đã sao chép số tài khoản ${session.accountNumber}")
                    },
                    onTapQrFullscreen = onOpenFullscreen,
                    modifier = Modifier.staggeredEntrance(1)
                )

                // 3. HÀNG 3 NÚT THAO TÁC DẠNG THẺ BO GÓC: [⬇ Lưu ảnh]  [🔗 Chia sẻ]  [↻ Tạo mới]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QrActionSquareButton(
                        icon = Icons.Outlined.Download,
                        label = "Lưu ảnh",
                        enabled = !isSavingOrSharing,
                        onClick = onSaveQrToGallery,
                        testTag = "save_qr_button",
                        modifier = Modifier.weight(1f)
                    )

                    QrActionSquareButton(
                        icon = Icons.Outlined.Share,
                        label = "Chia sẻ",
                        enabled = !isSavingOrSharing,
                        onClick = onShareQr,
                        testTag = "share_qr_button",
                        modifier = Modifier.weight(1f)
                    )

                    QrActionSquareButton(
                        icon = Icons.Outlined.Refresh,
                        label = "Tạo mới",
                        enabled = true,
                        onClick = onClearAndReenterAmount,
                        testTag = "new_qr_button",
                        modifier = Modifier.weight(1f)
                    )
                }

                // 4. NÚT QUAY LẠI LỚN DẠNG PILL GRADIENT XANH DƯƠNG: "< Quay lại"
                GradientPrimaryButton(
                    text = "Quay lại",
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    showTrailingChevron = false,
                    onClick = onCreateNewQr,
                    modifier = Modifier
                        .staggeredEntrance(3)
                        .testTag("clear_reenter_amount_button")
                )

                // 5. Chuỗi kiểm tra EMVCo / CRC-16 rút gọn
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(4)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Chuẩn EMVCo VietQR • BIN ${session.bankBin} • CRC16: ${session.crcHex}",
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = {
                                copyToClipboard(context, "VietQR Payload", session.vietQrPayload)
                                onShowMessage("Đã sao chép chuỗi EMVCo VietQR")
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
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
            brandColorHex = matchedBank?.brandColorHex ?: 0xFF00A86B,
            brightness = screenBrightness,
            onBrightnessChange = onBrightnessChange,
            onAdjustBrightnessDelta = onAdjustBrightnessDelta,
            onDismiss = onCloseFullscreen
        )
    }
}

@Composable
private fun UltraQrWhiteReceiptCard(
    session: ActiveQrSession,
    brandColorHex: Long,
    onCopyAccount: () -> Unit,
    onTapQrFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Hiệu ứng xuất hiện mượt mà cho mã QR (One-shot fade + scale, không biến dạng hay che khuất dữ liệu QR)
    val qrAlpha = remember(session.vietQrPayload) { Animatable(0f) }
    val qrScale = remember(session.vietQrPayload) { Animatable(0.92f) }

    LaunchedEffect(session.vietQrPayload) {
        launch {
            qrAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
            )
        }
        launch {
            qrScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    // Luôn giữ nền trắng tinh khiết (#FFFFFF) cho Card hiển thị QR để 100% ngân hàng quét nhạy ngay cả khi bật Dark Mode
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = RoyalBluePrimary.copy(alpha = 0.25f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.25f)
            )
            .testTag("vietqr_main_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, Color(0xFFD6E4FF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Huy hiệu xanh ngọc: "✔ Sẵn sàng quét"
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFE6F8F2),
                border = BorderStroke(1.dp, SuccessEmerald.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = SuccessEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Sẵn sàng quét",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFF065F46)
                    )
                }
            }

            // 2. Số tiền cực kỳ nổi bật ở chính giữa
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = CurrencyUtils.formatNumberDots(session.amountVnd),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = RoyalBluePrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("qr_amount_headline")
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "VNĐ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = RoyalBluePrimary
                )
            }

            // 3. Khung chứa Mã QR chính giữa: Sắc nét tuyệt đối (FilterQuality.None), không che khuất, nền trắng chuẩn
            Surface(
                onClick = onTapQrFullscreen,
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                border = BorderStroke(2.dp, Color(0xFFEAF2FF)),
                shadowElevation = 3.dp,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = qrAlpha.value
                        scaleX = qrScale.value
                        scaleY = qrScale.value
                    }
                    .widthIn(max = 276.dp)
                    .fillMaxWidth(0.84f)
                    .aspectRatio(1f)
                    .testTag("qr_bitmap_box")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = session.qrBitmap.asImageBitmap(),
                        contentDescription = "Mã QR chuyển khoản ${session.bankShortName} số tiền ${CurrencyUtils.formatVnd(session.amountVnd)}",
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.None,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Huy hiệu logo PHATPAY nhỏ ở tâm (< 5% diện tích QR, nằm gọn trong ngưỡng ErrorCorrectionLevel.M 15%)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        border = BorderStroke(2.dp, Color.White),
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            PhatPayVectorLogo(
                                size = 34.dp,
                                isDarkBackground = true,
                                showRoundedBox = true
                            )
                        }
                    }
                }
            }

            // 4. Hàng thông tin Ngân hàng + Số tài khoản | Tên chủ tài khoản (Chia cột tinh tế như thiết kế mẫu)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    BankLogoBadge(
                        bankShortName = session.bankShortName,
                        brandColorHex = brandColorHex,
                        size = 38.dp
                    )
                    Column {
                        Text(
                            text = session.bankShortName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = TextPrimaryLight
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = session.accountNumber,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextSecondaryLight
                            )
                            Surface(
                                onClick = onCopyAccount,
                                shape = CircleShape,
                                color = Color(0xFFEAF2FF)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Sao chép STK",
                                    tint = RoyalBluePrimary,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(11.dp)
                                )
                            }
                        }
                    }
                }

                VerticalDivider(
                    color = Color(0xFFD6E4FF),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(0.9f)
                ) {
                    Text(
                        text = session.accountHolder.uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = TextPrimaryLight,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 5. Pill nội dung chuyển khoản màu xanh nhạt
            if (session.transferMessage.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFEAF2FF),
                    border = BorderStroke(1.dp, Color(0xFFD6E4FF)),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = RoyalBluePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Nội dung: ${session.transferMessage}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimaryLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QrActionSquareButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.85f)),
        shadowElevation = 4.dp,
        modifier = modifier
            .pressScaleEffect(0.93f)
            .height(74.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = RoyalBluePrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
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
                            Color(0xFF061638),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CHẾ ĐỘ THU TIỀN TOÀN MÀN HÌNH",
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
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Đóng toàn màn hình",
                                tint = Color.White
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFE6F8F2)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessEmerald,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Sẵn sàng quét",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF065F46)
                                )
                            }
                        }

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

                        Text(
                            text = "${session.bankShortName} • ${session.accountNumber} • ${session.accountHolder}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimaryLight,
                            textAlign = TextAlign.Center
                        )

                        if (session.transferMessage.isNotBlank()) {
                            Text(
                                text = "Nội dung: ${session.transferMessage}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = TextSecondaryLight,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

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
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("decrease_brightness_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.BrightnessLow,
                                    contentDescription = "Giảm độ sáng",
                                    tint = Color.White
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
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("increase_brightness_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.BrightnessHigh,
                                    contentDescription = "Tăng độ sáng",
                                    tint = Color.White
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
