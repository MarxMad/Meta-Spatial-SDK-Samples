package com.meta.spatial.samples.devworkstation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.spatial.samples.devworkstation.ui.theme.CyberCyan
import com.meta.spatial.samples.devworkstation.ui.theme.CyberPink
import com.meta.spatial.samples.devworkstation.ui.theme.CyberSurface
import com.meta.spatial.samples.devworkstation.ui.theme.CyberText
import com.meta.spatial.samples.devworkstation.ui.theme.CyberpunkGlassCard
import com.meta.spatial.samples.devworkstation.ui.theme.CyberpunkTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorPanel() {
    val context = LocalContext.current
    var fileName by remember { mutableStateOf("workspace_code.txt") }
    var textContent by remember { mutableStateOf("// Escribe tu código o notas aquí...\n\nfun main() {\n    println(\"Hello Neo-Station!\")\n}") }

    // Load file on startup
    LaunchedEffect(Unit) {
        val file = File(context.getExternalFilesDir(null), fileName)
        if (file.exists()) {
            try {
                textContent = file.readText()
            } catch (e: Exception) {
                // Ignore or handle
            }
        }
    }

    fun saveFile() {
        try {
            val file = File(context.getExternalFilesDir(null), fileName)
            file.writeText(textContent)
            Toast.makeText(context, "Archivo guardado en: ${file.name}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun loadFile() {
        try {
            val file = File(context.getExternalFilesDir(null), fileName)
            if (file.exists()) {
                textContent = file.readText()
                Toast.makeText(context, "Archivo cargado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "El archivo no existe", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al cargar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    CyberpunkTheme {
        CyberpunkGlassCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Title and File Name Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEO-EDIT //",
                        color = CyberCyan,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            color = CyberText,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = CyberCyan.copy(alpha = 0.5f),
                            focusedContainerColor = CyberSurface,
                            unfocusedContainerColor = CyberSurface
                        ),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = { saveFile() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.2f), contentColor = CyberCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                    ) {
                        Text("SAVE", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { loadFile() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPink.copy(alpha = 0.2f), contentColor = CyberPink),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberPink)
                    ) {
                        Text("LOAD", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Editor Text Area
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    textStyle = TextStyle(
                        color = CyberText,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPink,
                        unfocusedBorderColor = CyberPink.copy(alpha = 0.3f),
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface
                    ),
                    placeholder = {
                        Text("// Empieza a escribir...", color = CyberText.copy(alpha = 0.3f), fontFamily = FontFamily.Monospace)
                    }
                )
            }
        }
    }
}
