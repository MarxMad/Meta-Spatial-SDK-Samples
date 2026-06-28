package com.meta.spatial.samples.devworkstation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.samples.devworkstation.ui.theme.CyberCyan
import com.meta.spatial.samples.devworkstation.ui.theme.CyberPink
import com.meta.spatial.samples.devworkstation.ui.theme.CyberPurple
import com.meta.spatial.samples.devworkstation.ui.theme.CyberText
import com.meta.spatial.samples.devworkstation.ui.theme.CyberpunkGlassCard
import com.meta.spatial.samples.devworkstation.ui.theme.CyberpunkTheme

enum class EnvType {
    PASSTHROUGH,
    CYBER_CAFE,
    NEON_OFFICE,
    ZEN_NATURE
}

@Composable
fun WorkspaceDockPanel(
    editorOpen: Boolean,
    terminalOpen: Boolean,
    browserOpen: Boolean,
    currentEnv: EnvType,
    onToggleEditor: () -> Unit,
    onToggleTerminal: () -> Unit,
    onToggleBrowser: () -> Unit,
    onSelectEnv: (EnvType) -> Unit
) {
    CyberpunkTheme {
        CyberpunkGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header
                Text(
                    text = "NEO-STATION // CONTROL DOCK",
                    color = CyberCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Windows Section
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "WORKSPACES",
                            color = CyberPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            DockButton(
                                label = "CODE EDITOR",
                                isActive = editorOpen,
                                onClick = onToggleEditor,
                                activeColor = CyberCyan
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DockButton(
                                label = "TERMINAL",
                                isActive = terminalOpen,
                                onClick = onToggleTerminal,
                                activeColor = CyberPink
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DockButton(
                                label = "BROWSER",
                                isActive = browserOpen,
                                onClick = onToggleBrowser,
                                activeColor = CyberPurple
                            )
                        }
                    }

                    // Environments Section
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ENVIRONMENTS",
                            color = CyberPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            DockButton(
                                label = "PASSTHROUGH",
                                isActive = currentEnv == EnvType.PASSTHROUGH,
                                onClick = { onSelectEnv(EnvType.PASSTHROUGH) },
                                activeColor = CyberCyan
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DockButton(
                                label = "CYBER CAFE",
                                isActive = currentEnv == EnvType.CYBER_CAFE,
                                onClick = { onSelectEnv(EnvType.CYBER_CAFE) },
                                activeColor = CyberPurple
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DockButton(
                                label = "NEON OFFICE",
                                isActive = currentEnv == EnvType.NEON_OFFICE,
                                onClick = { onSelectEnv(EnvType.NEON_OFFICE) },
                                activeColor = CyberPink
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            DockButton(
                                label = "ZEN NATURE",
                                isActive = currentEnv == EnvType.ZEN_NATURE,
                                onClick = { onSelectEnv(EnvType.ZEN_NATURE) },
                                activeColor = Color.Green
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DockButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    activeColor: Color
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) activeColor.copy(alpha = 0.3f) else Color.Transparent,
            contentColor = if (isActive) activeColor else CyberText
        ),
        modifier = Modifier.padding(2.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isActive) activeColor else CyberText.copy(alpha = 0.3f)
        ),
        shape = androidx.compose.foundation.shape.CutCornerShape(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
