package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = RoyalBluePrimary,
    onPrimary = PureWhiteCard,
    primaryContainer = SoftBlueTint,
    onPrimaryContainer = RoyalBluePrimary,
    secondary = SecondaryBlue,
    onSecondary = PureWhiteCard,
    secondaryContainer = Color(0xFFDFECFF),
    onSecondaryContainer = TextPrimaryLight,
    tertiary = SuccessEmerald,
    onTertiary = PureWhiteCard,
    tertiaryContainer = SuccessContainerLight,
    onTertiaryContainer = SuccessEmerald,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = PureWhiteCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFEEF4FF),
    onSurfaceVariant = TextSecondaryLight,
    outline = DividerLight,
    error = DangerCrimson,
    onError = PureWhiteCard,
    errorContainer = DangerContainerLight,
    onErrorContainer = DangerCrimson
)

private val DarkColorScheme = darkColorScheme(
    primary = RoyalBluePrimary,
    onPrimary = PureWhiteCard,
    primaryContainer = Color(0xFF193266),
    onPrimaryContainer = Color(0xFF93C5FD),
    secondary = SecondaryBlue,
    onSecondary = PureWhiteCard,
    secondaryContainer = DarkNavyElevated,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = SuccessEmerald,
    onTertiary = PureWhiteCard,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFF6EE7B7),
    background = DarkNavyBackground,
    onBackground = TextPrimaryDark,
    surface = DarkNavyCard,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkNavyElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = DividerDark,
    error = Color(0xFFF87171),
    onError = DarkNavyBackground,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA)
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SmartQrPaymentTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
