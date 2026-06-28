package com.meta.roomcast.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.roomcast.ui.theme.*
import kotlinx.coroutines.delay

/** Estados posibles del proceso de escaneo MRUK */
enum class ScanState {
  IDLE,        // Pantalla de bienvenida, esperando que el usuario inicie
  SCANNING,    // Escaneando activamente la habitación
  DONE,        // Escaneo completado con éxito
  ERROR,       // Error de permisos u otro problema
}

/**
 * Panel de bienvenida / progreso de escaneo de habitación.
 * Se muestra al inicio de la app antes de que aparezca el catálogo.
 */
@Composable
fun RoomScanPanel(
    scanState: ScanState,
    onStartScan: () -> Unit,
    onGrantPermission: () -> Unit,
) {
  RoomCastTheme {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xCC0A0A18),
                        Color(0xCC12122A),
                    )
                )
            )
            .border(1.dp, RCBorder, RoundedCornerShape(24.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {

        // Animated logo / icon
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn(spring(Spring.DampingRatioMediumBouncy)),
        ) {
          Box(
              modifier = Modifier
                  .size(80.dp)
                  .background(
                      brush = Brush.radialGradient(listOf(RCPrimary, Color(0xFF3D2E8F))),
                      shape = CircleShape,
                  )
                  .border(2.dp, RCBorder, CircleShape),
              contentAlignment = Alignment.Center,
          ) {
            Text("🏠", fontSize = 36.sp)
          }
        }

        // Title
        Text(
            text = "RoomCast",
            style = HeadingStyle.copy(fontSize = 30.sp),
            textAlign = TextAlign.Center,
        )

        // Subtitle / description
        Text(
            text = when (scanState) {
              ScanState.IDLE     -> "Ve cómo quedaría cualquier mueble en tu cuarto ANTES de comprarlo — con medidas exactas y escala 1:1."
              ScanState.SCANNING -> "Escaneando tu habitación con los sensores del Quest 3…\nNo te muevas del lugar."
              ScanState.DONE     -> "¡Tu habitación está lista!\nAhora puedes explorar el catálogo y colocar muebles."
              ScanState.ERROR    -> "RoomCast necesita permiso para escanear tu habitación.\nEs solo para ver tus paredes y el piso."
            },
            style = BodyStyle.copy(textAlign = TextAlign.Center),
            color = RCTextSecondary,
        )

        Spacer(Modifier.height(8.dp))

        when (scanState) {
          ScanState.IDLE -> {
            GlassmorphicButton(
                text = "Iniciar escaneo",
                onClick = onStartScan,
                primary = true,
            )
          }
          ScanState.SCANNING -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
              CircularProgressIndicator(
                  color = RCPrimary,
                  modifier = Modifier.size(48.dp),
                  strokeWidth = 3.dp,
              )
              Text(
                  "Detectando paredes, piso y objetos…",
                  style = CaptionStyle.copy(textAlign = TextAlign.Center),
              )
            }
          }
          ScanState.DONE -> {
            GlassmorphicButton(
                text = "Ver catálogo →",
                onClick = { /* Controlled by activity */ },
                primary = true,
            )
          }
          ScanState.ERROR -> {
            GlassmorphicButton(
                text = "Conceder permiso",
                onClick = onGrantPermission,
                primary = false,
            )
          }
        }

        // Footer
        Text(
            text = "Powered by Meta MRUK · Quest 3",
            style = CaptionStyle,
            color = RCTextMuted,
        )
      }
    }
  }
}

/** Botón con estilo glassmorphic */
@Composable
fun GlassmorphicButton(
    text: String,
    onClick: () -> Unit,
    primary: Boolean = true,
    modifier: Modifier = Modifier,
) {
  val bgBrush = if (primary)
    Brush.horizontalGradient(listOf(RCPrimary, Color(0xFF5A3ECC)))
  else
    Brush.horizontalGradient(listOf(Color(0xFF333355), Color(0xFF222244)))

  Button(
      onClick = onClick,
      modifier = modifier
          .widthIn(min = 180.dp)
          .height(52.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
      contentPadding = PaddingValues(0.dp),
      shape = RoundedCornerShape(16.dp),
  ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = bgBrush, shape = RoundedCornerShape(16.dp))
            .border(1.dp, if (primary) RCPrimary.copy(alpha = 0.5f) else RCBorder, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
      Text(
          text = text,
          style = BodyStyle.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
          color = if (primary) Color.White else RCTextSecondary,
      )
    }
  }
}
