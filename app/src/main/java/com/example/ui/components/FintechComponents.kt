package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessEmerald

/**
 * Hiệu ứng nhấn thu phóng mượt mà (Tactile Spring Bounce) cho các nút bấm và thẻ Fintech
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
 * Nút chính có Gradient Xanh Dương (#1769FF -> #4C8DFF), chữ trắng nổi bật và hiệu ứng nhấn mượt mà
 */
@Composable
fun GradientPrimaryButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    height: Dp = 60.dp
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = if (enabled) {
            listOf(RoyalBluePrimary, SecondaryBlue)
        } else {
            listOf(Color(0xFF94A3B8), Color(0xFFCBD5E1))
        }
    )

    Box(
        modifier = modifier
            .pressScaleEffect(0.97f)
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (enabled) 10.dp else 0.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = RoyalBluePrimary.copy(alpha = 0.35f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(gradientBrush)
            .clickable(enabled = enabled && !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.5.dp,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "ĐANG TẠO MÃ VIETQR...",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 20.dp)
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 0.3.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Logo đại diện ngân hàng đồng bộ, sắc nét
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

    Surface(
        shape = CircleShape,
        color = if (lightBackground) Color.White else Color(brandColorHex),
        shadowElevation = 2.dp,
        modifier = Modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = code,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (size.value * 0.31f).sp
                ),
                color = if (lightBackground) RoyalBluePrimary else Color.White
            )
        }
    }
}

/**
 * Nhãn VietQR / NAPAS 247 nhỏ gọn, tinh tế
 */
@Composable
fun VietQrVerifiedBadge(
    text: String = "VietQR",
    isLightOnBlue: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isLightOnBlue) {
            Color.White.copy(alpha = 0.18f)
        } else {
            SuccessEmerald.copy(alpha = 0.12f)
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
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
