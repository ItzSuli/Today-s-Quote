package com.itzsuli.todaysquote.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF070910)
private val Surface1 = Color(0xFF10141D)
private val Surface2 = Color(0xFF171D28)
private val Outline = Color(0xFF29313F)
private val Steel = Color(0xFF9FB3D4)
private val Bone = Color(0xFFE9EDF4)
private val Muted = Color(0xFF8A94A6)

private val DarkScheme = darkColorScheme(
    primary = Steel,
    onPrimary = Ink,
    primaryContainer = Surface2,
    onPrimaryContainer = Bone,
    secondary = Muted,
    background = Ink,
    onBackground = Bone,
    surface = Surface1,
    onSurface = Bone,
    surfaceVariant = Surface2,
    onSurfaceVariant = Muted,
    outline = Outline,
    outlineVariant = Color(0xFF1D242F),
    error = Color(0xFFE2857B)
)

private val LightScheme = lightColorScheme(
    primary = Color(0xFF3A4A66),
    background = Color(0xFFF6F7FA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFEDEFF4),
    onSurfaceVariant = Color(0xFF5C6474),
    outline = Color(0xFFD3D8E2)
)

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp,
        lineHeight = 40.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.4.sp
    )
)

@Composable
fun TodaysQuoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = AppTypography,
        content = content
    )
}
