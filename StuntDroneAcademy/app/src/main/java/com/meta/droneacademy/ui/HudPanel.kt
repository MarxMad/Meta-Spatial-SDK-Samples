package com.meta.droneacademy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HudPanel(
    isFlying: Boolean,
    throttle: Float,
    health: Int,
    speed: Float,
    onTogglePower: () -> Unit,
    onAdjustThrottle: (Float) -> Unit,
    onResetPosition: () -> Unit,
    onExitApp: () -> Unit
) {
  val dashboardBg = Color(0xF2101828) // Deep translucent dark slate
  val textPrimary = Color(0xFFF8FAFC)
  val textSecondary = Color(0xFF94A3B8)
  val accentNeonGreen = Color(0xFF10B981)
  val accentNeonRed = Color(0xFFEF4444)
  val accentNeonBlue = Color(0xFF3B82F6)

  Box(
      modifier = Modifier
          .fillMaxSize()
          .background(dashboardBg, RoundedCornerShape(24.dp))
          .border(2.dp, accentNeonBlue.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
          .padding(24.dp)
  ) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      
      // ── LEFT SECTION: PILOT CONTROLS ───────────────────────────────────────
      Column(
          modifier = Modifier
              .weight(0.4f)
              .fillMaxHeight(),
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
              "DRONE CONTROLLER",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = textPrimary,
              letterSpacing = 1.5.sp
          )
          Text(
              "STUNT DRONE ACADEMY",
              fontSize = 10.sp,
              color = accentNeonBlue,
              letterSpacing = 2.sp
          )
        }

        // Engine Power Switch Button
        Button(
            onClick = onTogglePower,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFlying) accentNeonRed else accentNeonGreen
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
          Text(
              text = if (isFlying) "DISARM ENGINES" else "ARM ENGINES",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = Color.White
          )
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Button(
              onClick = onResetPosition,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f).height(44.dp)
          ) {
            Text("RESET", color = textPrimary, fontSize = 12.sp)
          }

          IconButton(
              onClick = onExitApp,
              modifier = Modifier
                  .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                  .size(44.dp)
          ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = textPrimary)
          }
        }
      }

      // Vertical Divider
      Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(Color(0xFF334155)))

      // ── RIGHT SECTION: TELEMETRY DISPLAY ───────────────────────────────────
      Column(
          modifier = Modifier
              .weight(0.6f)
              .fillMaxHeight(),
          verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
            "TELEMETRY DATA",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = textSecondary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          // Speed gauge card
          Card(
              modifier = Modifier.weight(1f),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text("VELOCIDAD", fontSize = 11.sp, color = textSecondary)
              Text("%.1f m/s".format(speed), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
            }
          }

          // Health card
          Card(
              modifier = Modifier.weight(1f),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
              Text("ESTADO DRON", fontSize = 11.sp, color = textSecondary)
              Text("$health%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (health > 30) accentNeonGreen else accentNeonRed)
            }
          }
        }

        // Throttle adjuster
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
              Text("ACELERADOR (THROTTLE)", fontSize = 11.sp, color = textSecondary)
              Text("${(throttle * 100).toInt()}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accentNeonBlue)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
              Button(
                  onClick = { onAdjustThrottle(-0.1f) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.height(36.dp)
              ) {
                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
              }

              LinearProgressIndicator(
                  progress = throttle.coerceIn(0f, 1f),
                  color = accentNeonBlue,
                  trackColor = Color(0xFF334155),
                  modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape)
              )

              Button(
                  onClick = { onAdjustThrottle(0.1f) },
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.height(36.dp)
              ) {
                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
