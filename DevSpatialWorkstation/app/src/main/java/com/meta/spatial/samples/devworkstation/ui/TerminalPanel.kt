package com.meta.spatial.samples.devworkstation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.samples.devworkstation.ui.theme.CyberCyan
import com.meta.spatial.samples.devworkstation.ui.theme.CyberPink
import com.meta.spatial.samples.devworkstation.ui.theme.CyberSurface
import com.meta.spatial.samples.devworkstation.ui.theme.CyberText
import com.meta.spatial.samples.devworkstation.ui.theme.CyberpunkGlassCard
import com.meta.spatial.samples.devworkstation.ui.theme.CyberpunkTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalPanel() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var inputVal by remember { mutableStateOf("") }
    var outputVal by remember { mutableStateOf("NEO-SH v1.0 // DEVELOPER WORKSTATION\nConnected to local Horizon OS shell.\nType 'help' for custom commands.\n\n$ ") }
    val scrollState = rememberScrollState()

    var currentDir by remember { mutableStateOf(context.getExternalFilesDir(null) ?: context.filesDir) }

    // Auto-scroll terminal output to the bottom
    LaunchedEffect(outputVal) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    fun executeShell(commandStr: String) {
        if (commandStr.trim().isEmpty()) return

        val cmd = commandStr.trim()
        outputVal += "$cmd\n"

        if (cmd == "clear") {
            outputVal = "$ "
            inputVal = ""
            return
        }

        if (cmd == "help") {
            outputVal += """
                |Comandos disponibles:
                |  ls           Listar archivos en el directorio actual
                |  pwd          Ver directorio de trabajo actual
                |  cd <dir>     Cambiar de directorio
                |  cat <file>   Mostrar el contenido de un archivo
                |  mkdir <dir>  Crear un directorio
                |  rm <file>    Borrar un archivo
                |  uname -a     Ver info del sistema y kernel
                |  clear        Limpiar pantalla
                |
            """.trimMargin() + "\n$ "
            inputVal = ""
            return
        }

        scope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val parts = cmd.split("\\s+".toRegex())
                    val baseCmd = parts[0]

                    if (baseCmd == "cd") {
                        val path = if (parts.size > 1) parts[1] else ""
                        val targetDir = if (path == "..") {
                            currentDir.parentFile ?: currentDir
                        } else if (path.startsWith("/")) {
                            File(path)
                        } else {
                            File(currentDir, path)
                        }

                        if (targetDir.exists() && targetDir.isDirectory) {
                            currentDir = targetDir
                            "Directorio cambiado a: ${targetDir.absolutePath}"
                        } else {
                            "cd: no existe el directorio o no es válido: $path"
                        }
                    } else {
                        // Execute other shell command
                        val processBuilder = ProcessBuilder(*parts.toTypedArray())
                        processBuilder.directory(currentDir)
                        processBuilder.redirectErrorStream(true)
                        val process = processBuilder.start()

                        val reader = BufferedReader(InputStreamReader(process.inputStream))
                        val output = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            output.append(line).append("\n")
                        }
                        process.waitFor()
                        output.toString()
                    }
                } catch (e: Exception) {
                    "Error al ejecutar: ${e.message}\n"
                }
            }
            outputVal += result + "\n$ "
        }
        inputVal = ""
    }

    CyberpunkTheme {
        CyberpunkGlassCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            borderGlowBrush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(CyberPink, CyberCyan))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEO-SHELL // LOCAL_SH",
                        color = CyberPink,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Button(
                        onClick = { outputVal = "$ " },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPink.copy(alpha = 0.2f), contentColor = CyberPink),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberPink)
                    ) {
                        Text("CLEAR", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Output log
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = outputVal,
                        color = Color(0xFF33FF33), // Retro terminal green
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input bar
                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = CyberText,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPink,
                        unfocusedBorderColor = CyberPink.copy(alpha = 0.3f),
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface
                    ),
                    placeholder = {
                        Text("Escribe un comando...", color = CyberText.copy(alpha = 0.3f), fontFamily = FontFamily.Monospace)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        executeShell(inputVal)
                    })
                )
            }
        }
    }
}
