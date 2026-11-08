package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessEmerald
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Hiệu ứng nhấn thu phóng nhẹ (Micro-interaction Scale Bounce) cho các nút và thẻ
 */
fun Modifier.pressScaleEffect(
    pressedScale: Float = 0.96f
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "press_scale"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}

/**
 * Hiệu ứng xuất hiện tuần tự nhẹ nhàng cho các Card (One-shot entrance animation)
 */
fun Modifier.staggeredEntrance(
    index: Int,
    baseDelayMs: Int = 35
): Modifier = composed {
    val alphaAnim = remember { Animatable(0f) }
    val offsetYAnim = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        delay((index * baseDelayMs).toLong())
        launch {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            )
        }
        launch {
            offsetYAnim.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    this.graphicsLayer {
        alpha = alphaAnim.value
        translationY = offsetYAnim.value
    }
}

/**
 * Biểu tượng Logo Vector PHATPAY (Chữ P cách điệu + Vương miện vàng + QR Matrix + Vòng sáng Neon)
 * Hỗ trợ cả phiên bản Sáng (trên nền trắng) và Tối (trên nền xanh Navy)
 */
@Composable
fun PhatPayVectorLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    isDarkBackground: Boolean = true,
    showRoundedBox: Boolean = false
) {
    val drawableRes = if (isDarkBackground || showRoundedBox) {
        R.drawable.ic_phatpay_logo_dark
    } else {
        R.drawable.ic_phatpay_logo_light
    }

    if (showRoundedBox) {
        Surface(
            shape = RoundedCornerShape(size * 0.28f),
            color = Color(0xFF061539),
            border = BorderStroke(1.2.dp, Color(0xFF00E5FF).copy(alpha = 0.65f)),
            shadowElevation = 8.dp,
            modifier = modifier.size(size)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF051338),
                                Color(0xFF0C2E7A),
                                RoyalBluePrimary
                            )
                        )
                    )
                    .padding(size * 0.08f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = drawableRes),
                    contentDescription = "PHATPAY Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    } else {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = "PHATPAY Logo",
            contentScale = ContentScale.Fit,
            modifier = modifier.size(size)
        )
    }
}

/**
 * Background Neon Blue City chuẩn nhận diện PHATPAY:
 * - Thành phố công nghệ tương lai với các tòa nhà phát sáng & đường sáng xanh chạy nhẹ
 * - Tự động cân chỉnh lớp phủ tương phản cho cả Light Mode và Dark Mode
 */
@Composable
fun UltraFintechBackdrop(
    headerHeightDp: Dp = 230.dp,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseBg = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(baseBg)
    ) {
        // 1. Hình nền Neon Blue City được tối ưu độ tương phản cho Light Mode & Dark Mode
        Image(
            painter = painterResource(id = R.drawable.img_phatpay_neon_city_1794134628146),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = if (darkTheme) 0.36f else 0.16f,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Lớp phủ Gradient bảo vệ độ tương phản tuyệt đối của chữ và Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = if (darkTheme) {
                            arrayOf(
                                0.0f to Color(0xFF040D26).copy(alpha = 0.88f),
                                0.35f to Color(0xFF07163B).copy(alpha = 0.78f),
                                0.75f to Color(0xFF080F1E).copy(alpha = 0.82f),
                                1.0f to Color(0xFF050C1A).copy(alpha = 0.90f)
                            )
                        } else {
                            arrayOf(
                                0.0f to Color(0xFF04143D).copy(alpha = 0.92f),
                                0.26f to Color(0xFF0D3B9E).copy(alpha = 0.85f),
                                0.42f to Color(0xFFEAF2FF).copy(alpha = 0.92f),
                                1.0f to Color(0xFFF3F8FF).copy(alpha = 0.90f)
                            )
                        }
                    )
                )
        )

        // 3. Sóng Header Royal Blue & Đường sáng Neon xanh chạy nhẹ
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeightDp + 70.dp)
        ) {
            val w = size.width
            val h = headerHeightDp.toPx()

            val wavePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.82f)
                cubicTo(
                    w * 0.72f, h * 1.05f,
                    w * 0.28f, h * 0.95f,
                    0f, h * 0.84f
                )
                close()
            }

            drawPath(
                path = wavePath,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF041030),
                        Color(0xFF0B2F80),
                        RoyalBluePrimary
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(w, h)
                )
            )

            // Hào quang ánh sáng Electric Blue góc trên
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.22f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.85f, h * 0.22f),
                    radius = w * 0.48f
                ),
                center = Offset(w * 0.85f, h * 0.22f),
                radius = w * 0.48f
            )

            // Đường sáng Neon xanh chạy nhẹ dọc theo biên sóng
            val neonLine = Path().apply {
                moveTo(0f, h * 0.84f)
                cubicTo(
                    w * 0.28f, h * 0.95f,
                    w * 0.72f, h * 1.05f,
                    w, h * 0.82f
                )
            }
            drawPath(
                path = neonLine,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF00E5FF).copy(alpha = 0.55f),
                        Color(0xFF4C8DFF).copy(alpha = 0.45f),
                        Color.Transparent
                    )
                ),
                style = Stroke(width = 2.2.dp.toPx())
            )
        }

        content()
    }
}

/**
 * Nút chính PHATPAY: Futuristic Neon Blue Gradient, viền sáng nhẹ, chữ trắng nổi bật
 */
@Composable
fun GradientPrimaryButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    showTrailingChevron: Boolean = true,
    height: Dp = 58.dp
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = if (enabled) {
            listOf(
                Color(0xFF0A46C4),
                RoyalBluePrimary,
                Color(0xFF00B4D8)
            )
        } else {
            listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1))
        }
    )

    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(50),
        color = Color.Transparent,
        border = if (enabled) {
            BorderStroke(1.dp, Color(0xFF7DD3FC).copy(alpha = 0.55f))
        } else null,
        modifier = modifier
            .pressScaleEffect(0.965f)
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (enabled) 14.dp else 0.dp,
                shape = RoundedCornerShape(50),
                ambientColor = Color(0xFF00B4D8).copy(alpha = 0.45f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.45f)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "PHATPAY đang tạo mã QR...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp)
                ) {
                    Spacer(modifier = Modifier.width(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = text,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                letterSpacing = 0.3.sp
                            ),
                            color = Color.White
                        )
                    }

                    if (showTrailingChevron) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.92f),
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
        }
    }
}

/**
 * Biểu tượng khiên / logo ngân hàng phong cách Fintech cao cấp
 */
@Composable
fun BankLogoBadge(
    bankShortName: String,
    brandColorHex: Long = 0xFF1769FF,
    size: Dp = 44.dp,
    lightBackground: Boolean = false
) {
    val code = remember(bankShortName) {
        val clean = bankShortName.trim().uppercase()
        when {
            clean.startsWith("VIETCOM") -> "VCB"
            clean.startsWith("TECHCOM") -> "TCB"
            clean.startsWith("VIETIN") -> "CTG"
            clean.startsWith("MB") -> "MB"
            clean.startsWith("AGRI") -> "VBA"
            clean.startsWith("SACOM") -> "STB"
            clean.startsWith("VPBANK") -> "VPB"
            clean.startsWith("TPBANK") -> "TPB"
            clean.startsWith("HDBANK") -> "HDB"
            clean.startsWith("SEABANK") -> "SSB"
            clean.startsWith("LPBANK") -> "LPB"
            clean.startsWith("EXIM") -> "EIB"
            else -> clean.take(3)
        }
    }

    val badgeColor = Color(brandColorHex)

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (lightBackground) Color.White.copy(alpha = 0.16f) else badgeColor.copy(alpha = 0.14f),
        border = BorderStroke(
            1.dp,
            if (lightBackground) Color.White.copy(alpha = 0.32f) else badgeColor.copy(alpha = 0.3f)
        ),
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = if (lightBackground) Color(0xFF34D399) else badgeColor,
                modifier = Modifier.size(size * 0.72f)
            )
            Text(
                text = code,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (size.value * 0.22f).sp
                ),
                color = Color.White
            )
        }
    }
}

/**
 * Nhãn trạng thái VietQR / Sẵn sàng quét
 */
@Composable
fun VietQrVerifiedBadge(
    text: String = "VietQR",
    isLightOnBlue: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isLightOnBlue) {
            Color.White.copy(alpha = 0.16f)
        } else {
            SuccessEmerald.copy(alpha = 0.12f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (isLightOnBlue) {
                Color.White.copy(alpha = 0.28f)
            } else {
                SuccessEmerald.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(if (isLightOnBlue) Color(0xFF34D399) else SuccessEmerald)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = if (isLightOnBlue) Color.White else SuccessEmerald
            )
        }
    }
}

/**
 * Màn hình khởi động (Splash Screen) PHATPAY:
 * - Background thành phố tương lai về đêm (Neon Blue City)
 * - Logo PHATPAY ở trung tâm kèm hiệu ứng ánh sáng xanh chạy quanh logo
 * - Slogan "Tạo QR nhanh – Thu tiền dễ dàng" bên dưới
 * - Thanh tiến trình "Đang khởi động..." chuyển mượt vào Trang chủ
 */
@Composable
fun SmartQrSplashScreen() {
    val logoScale = remember { Animatable(0.76f) }
    val logoAlpha = remember { Animatable(0f) }
    val sweepAngleAnim = remember { Animatable(0f) }
    val progressAnim = remember { Animatable(0.08f) }

    LaunchedEffect(Unit) {
        launch {
            logoAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            sweepAngleAnim.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 720, easing = LinearEasing)
            )
        }
        launch {
            progressAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF040C24))
    ) {
        // 1. Background thành phố tương lai về đêm
        Image(
            painter = painterResource(id = R.drawable.img_phatpay_neon_city_1794134628146),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Lớp phủ Gradient tăng chiều sâu cho Logo trung tâm và Thành phố bên dưới
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFF030B22).copy(alpha = 0.72f),
                            0.45f to Color(0xFF061844).copy(alpha = 0.55f),
                            0.80f to Color(0xFF041030).copy(alpha = 0.65f),
                            1.0f to Color(0xFF02081A).copy(alpha = 0.88f)
                        )
                    )
                )
        )

        // 2. Cụm Logo PHATPAY trung tâm + Ánh sáng xanh chạy quanh logo + Slogan
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 56.dp)
                .graphicsLayer {
                    scaleX = logoScale.value
                    scaleY = logoScale.value
                    alpha = logoAlpha.value
                }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(168.dp)
            ) {
                // Hiệu ứng ánh sáng xanh Electric Blue chạy quanh logo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeW = 3.5.dp.toPx()
                    val pad = 10.dp.toPx()
                    val arcSize = Size(size.width - pad * 2, size.height - pad * 2)

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00E5FF).copy(alpha = 0.28f),
                                Color(0xFF1769FF).copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        ),
                        radius = size.width * 0.52f
                    )

                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF1769FF),
                                Color(0xFF00E5FF),
                                Color.White
                            )
                        ),
                        startAngle = -90f + sweepAngleAnim.value * 0.35f,
                        sweepAngle = sweepAngleAnim.value.coerceAtMost(290f),
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }

                PhatPayVectorLogo(
                    size = 136.dp,
                    isDarkBackground = true,
                    showRoundedBox = false
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PHATPAY",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 2.sp,
                    fontSize = 36.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tạo QR nhanh – Thu tiền dễ dàng",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.4.sp
                ),
                color = Color(0xFFBAE6FD)
            )
        }

        // 3. Thanh tiến trình khởi động Neon ở phía dưới màn hình
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 48.dp, vertical = 42.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Đang khởi động...",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White.copy(alpha = 0.9f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.18f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnim.value)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    RoyalBluePrimary,
                                    Color(0xFF00E5FF)
                                )
                            )
                        )
                )
            }
        }
    }
}
