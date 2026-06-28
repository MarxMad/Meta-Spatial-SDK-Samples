package com.meta.spatial.samples.devworkstation.ui

import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.meta.spatial.samples.devworkstation.ui.theme.CyberCyan
import com.meta.spatial.samples.devworkstation.ui.theme.CyberPurple
import com.meta.spatial.samples.devworkstation.ui.theme.CyberSurface
import com.meta.spatial.samples.devworkstation.ui.theme.CyberText
import com.meta.spatial.samples.devworkstation.ui.theme.CyberpunkGlassCard
import com.meta.spatial.samples.devworkstation.ui.theme.CyberpunkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebPanel() {
    var url by remember { mutableStateOf("https://devdocs.io/") }
    var inputUrl by remember { mutableStateOf("https://devdocs.io/") }
    var webViewInstance: WebView? by remember { mutableStateOf(null) }

    CyberpunkTheme {
        CyberpunkGlassCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            borderGlowBrush = androidx.compose.ui.graphics.Brush.linearGradient(listOf(CyberPurple, CyberCyan))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Address Bar Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { webViewInstance?.goBack() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPurple.copy(alpha = 0.2f), contentColor = CyberPurple),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberPurple),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    ) {
                        Text("<", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }

                    Button(
                        onClick = { webViewInstance?.goForward() },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPurple.copy(alpha = 0.2f), contentColor = CyberPurple),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberPurple),
                        modifier = Modifier.padding(horizontal = 2.dp)
                    ) {
                        Text(">", fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
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
                        onClick = { 
                            var target = inputUrl.trim()
                            if (!target.startsWith("http://") && !target.startsWith("https://")) {
                                target = "https://$target"
                            }
                            url = target
                            inputUrl = target
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.2f), contentColor = CyberCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan)
                    ) {
                        Text("GO", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // WebView Container
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, urlStr: String?) {
                                    super.onPageFinished(view, urlStr)
                                    urlStr?.let {
                                        inputUrl = it
                                        url = it
                                    }
                                }
                            }
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.useWideViewPort = true
                            settings.loadWithOverviewMode = true
                            loadUrl(url)
                            webViewInstance = this
                        }
                    },
                    update = { webView ->
                        if (webView.url != url) {
                            webView.loadUrl(url)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
        }
    }
}
