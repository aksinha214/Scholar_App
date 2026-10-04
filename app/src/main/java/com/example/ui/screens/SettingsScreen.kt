package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.ai.GeminiApiClient
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun SettingsScreen(viewModel: ScholarViewModel) {
    var selectedModel by remember { mutableStateOf(GeminiApiClient.activeModel) }
    val models = listOf("gemini-2.5-flash", "gemini-3.5-flash", "gemini-3.1-pro-preview")

    val apiKeyConfigured = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "CS Scholar OS Configuration",
                subtitle = "System runtime, Gemini API, and security settings"
            )
        }

        // Gemini API Status Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gemini API Connection",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        FactBadge(if (apiKeyConfigured) "LIVE KEY ACTIVE" else "OFFLINE BACKEND ACTIVE")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (apiKeyConfigured)
                            "• Live Gemini API key detected via AI Studio Secrets.\n• REST calls active with 60s timeout handling."
                        else
                            "• Live key not yet configured in AI Studio Secrets panel.\n• Grounded offline expert rules engine is actively serving all 10 mentor modes, project generation, and document extraction seamlessly without errors.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 18.sp)
                    )
                }
            }
        }

        // Model Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Active AI Model",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    models.forEach { m ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedModel == m,
                                onClick = {
                                    selectedModel = m
                                    GeminiApiClient.activeModel = m
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(m, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                                Text(
                                    text = if (m.contains("pro")) "Complex STEM reasoning & research" else "Fast low-latency inference",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Data Management & Session Actions
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Data Persistence & Session",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Manage local Room SQLite database and authentication session.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { viewModel.resetDatabaseToDefaults() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restore Standard YSU Dataset")
                    }
                    Button(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ScholarRed, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign Out of Scholar OS")
                    }
                }
            }
        }

        // About CS Scholar OS Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About CS Scholar OS",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Version: 1.0.0 (Release Build)\nDesigned for: International CS & Tech Students in China\nInitial Anchor: Yanshan University (燕山大学, Qinhuangdao)\nData Persistence: Android Jetpack Room Database (Local SQLite with zero cloud lock-in)\nArchitecture: Clean Architecture + MVVM + Jetpack Compose M3.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
