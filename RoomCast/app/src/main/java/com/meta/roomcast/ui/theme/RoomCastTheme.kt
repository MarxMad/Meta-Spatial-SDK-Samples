package com.meta.roomcast.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── RoomCast Color Palette ────────────────────────────────────────────────────
// Deep dark backgrounds with purple/violet electric accents

val RCBackground   = Color(0xFF0A0A12)
val RCBackgroundAlt = Color(0xFF12121F)
val RCSurface      = Color(0xFF1A1A2E)
val RCGlass        = Color(0x99121220)   // ~60% opacity for glassmorphism panels
val RCBorder       = Color(0x33FFFFFF)   // subtle white borders

val RCPrimary      = Color(0xFF7B61FF)   // electric violet
val RCPrimaryLight = Color(0xFFAA99FF)
val RCAccent       = Color(0xFF00E5FF)   // cyan accent
val RCSuccess      = Color(0xFF00E676)   // fit = green
val RCWarning      = Color(0xFFFF6B35)   // no fit = orange

val RCTextPrimary   = Color(0xFFF0F0F8)
val RCTextSecondary = Color(0xFFAAAAAA)
val RCTextMuted     = Color(0xFF666680)

// ── Dark Color Scheme ─────────────────────────────────────────────────────────

private val RCDarkColorScheme = darkColorScheme(
    primary          = RCPrimary,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF3D2E8F),
    secondary        = RCAccent,
    onSecondary      = Color.Black,
    background       = RCBackground,
    onBackground     = RCTextPrimary,
    surface          = RCSurface,
    onSurface        = RCTextPrimary,
    surfaceVariant   = RCBackgroundAlt,
    onSurfaceVariant = RCTextSecondary,
    error            = RCWarning,
    onError          = Color.White,
)

// ── Typography shortcuts ──────────────────────────────────────────────────────

val HeadingStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
    color = RCTextPrimary,
    letterSpacing = (-0.5).sp,
)

val SubheadStyle = TextStyle(
    fontWeight = FontWeight.SemiBold,
    fontSize = 15.sp,
    color = RCTextSecondary,
)

val BodyStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    color = RCTextPrimary,
    lineHeight = 20.sp,
)

val CaptionStyle = TextStyle(
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    color = RCTextMuted,
)

val PriceStyle = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 20.sp,
    color = RCAccent,
)

// ── Theme Composable ──────────────────────────────────────────────────────────

@Composable
fun RoomCastTheme(content: @Composable () -> Unit) {
  MaterialTheme(
      colorScheme = RCDarkColorScheme,
      content = content,
  )
}
