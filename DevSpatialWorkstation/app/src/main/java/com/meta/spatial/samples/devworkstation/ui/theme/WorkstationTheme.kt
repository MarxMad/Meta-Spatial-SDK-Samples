package com.meta.spatial.samples.devworkstation.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Cyberpunk neon colors
val CyberCyan = Color(0xFF00F0FF)
val CyberPurple = Color(0xFFBD00FF)
val CyberPink = Color(0xFFFF007A)
val CyberBackground = Color(0xCC09090E) // Translucent dark color for glassmorphism background
val CyberSurface = Color(0x66151522)    // Even lighter translucent for surface elements
val CyberText = Color(0xFFECECF4)
val CyberSubText = Color(0xFF9090A0)

private val CyberColorScheme = darkColorScheme(
    primary = CyberCyan,
    secondary = CyberPurple,
    tertiary = CyberPink,
    background = CyberBackground,
    surface = CyberSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = CyberText,
    onSurface = CyberText
)

val CyberTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp,
        color = CyberText
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.5.sp,
        color = CyberCyan
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = CyberPink
    )
)

@Composable
fun CyberpunkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = CyberTypography,
        content = content
    )
}

// Custom Glassmorphic cyber card wrapper
@Composable
fun CyberpunkGlassCard(
    modifier: Modifier = Modifier,
    borderGlowBrush: Brush = Brush.linearGradient(listOf(CyberCyan, CyberPurple)),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CyberBackground)
            .border(
                width = 1.5.dp,
                brush = borderGlowBrush,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}
