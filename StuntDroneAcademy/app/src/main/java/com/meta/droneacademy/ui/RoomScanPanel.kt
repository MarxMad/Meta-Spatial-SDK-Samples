package com.meta.droneacademy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.droneacademy.ui.theme.DroneAcademyTheme // We reuse the theme colors

enum class ScanState {
  IDLE,
  SCANNING,
  DONE,
  ERROR
}

@Composable
fun RoomScanPanel(
    scanState: ScanState,
    onStartScan: () -> Unit,
    onGrantPermission: () -> Unit,
    onCancelScan: (() -> Unit)? = null,
    onExitApp: () -> Unit
) {
  val dashboardBg = Color(0xF2101828)
  val textPrimary = Color(0xFFF8FAFC)
  val textSecondary = Color(0xFF94A3B8)
  val accentNeonBlue = Color(0xFF3B82F6)

  DroneAcademyTheme {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dashboardBg, RoundedCornerShape(24.dp))
            .border(2.dp, accentNeonBlue.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
      Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(20.dp),
          modifier = Modifier.widthIn(max = 450.dp)
    ) {
      Text(
          text = "CONFIGURACIÓN DEL ESPACIO",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = textPrimary,
          letterSpacing = 1.5.sp
      )

      Text(
          text = "Stunt Drone Academy requiere escanear tu habitación para proyectar los colisionadores de tus paredes y muebles.",
          fontSize = 13.sp,
          color = textSecondary,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
      )

      when (scanState) {
        ScanState.IDLE -> {
          Row(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
                onClick = onStartScan,
                colors = ButtonDefaults.buttonColors(containerColor = accentNeonBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
              Text("Iniciar escaneo", color = Color.White, fontWeight = FontWeight.Bold)
            }
            if (onCancelScan != null) {
              Button(
                  onClick = onCancelScan,
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.height(48.dp)
              ) {
                Text("Cancelar", color = textPrimary)
              }
            }
            Button(
                onClick = onExitApp,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
              Text("Salir", color = Color.White)
            }
          }
        }
        ScanState.SCANNING -> {
          Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            CircularProgressIndicator(
                color = accentNeonBlue,
                modifier = Modifier.size(48.dp),
                strokeWidth = 3.dp
            )
            Text(
                "Detectando paredes, piso y muebles físicos...",
                fontSize = 12.sp,
                color = textSecondary,
                textAlign = TextAlign.Center
            )
          }
        }
        ScanState.DONE -> {
          Button(
              onClick = { /* Controlled by Activity */ },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.height(48.dp)
          ) {
            Text("Entrar a la Academia", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
        ScanState.ERROR -> {
          Row(
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
                onClick = onGrantPermission,
                colors = ButtonDefaults.buttonColors(containerColor = accentNeonBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
              Text("Conceder permiso", color = Color.White)
            }
            Button(
                onClick = onExitApp,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
              Text("Salir", color = Color.White)
            }
          }
        }
      }
    }
  }
}
