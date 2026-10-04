package com.example.ui.screens.chinese

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.ChineseCoachEngine
import com.example.data.model.*
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

/**
 * Chinese Sub-views & Interactive Practice Components
 */

@Composable
fun CharacterPracticeCanvas(
    character: String,
    pinyin: String,
    meaning: String,
    strokeDescription: String,
    onSpeak: () -> Unit
) {
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = character,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarGoldLight
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = pinyin,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = ScholarCyan
                                )
                            )
                            Text(
                                text = meaning,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
                IconButton(onClick = onSpeak) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Pronounce",
                        tint = ScholarCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "笔画顺序 (Stroke order): $strokeDescription",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Handwriting canvas grid box (Tian Zi Ge / 米字格)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val newPath = Path().apply { moveTo(offset.x, offset.y) }
                                    currentPath = newPath
                                    paths.add(newPath)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                },
                                onDragEnd = {
                                    currentPath = null
                                }
                            )
                        }
                ) {
                    // Draw guidelines (Tian Zi Ge lines)
                    val w = size.width
                    val h = size.height
                    drawLine(Color(0x33FFFFFF), Offset(0f, h / 2), Offset(w, h / 2), strokeWidth = 1.dp.toPx())
                    drawLine(Color(0x33FFFFFF), Offset(w / 2, 0f), Offset(w / 2, h), strokeWidth = 1.dp.toPx())
                    drawLine(Color(0x1AFFFFFF), Offset(0f, 0f), Offset(w, h), strokeWidth = 1.dp.toPx())
                    drawLine(Color(0x1AFFFFFF), Offset(w, 0f), Offset(0f, h), strokeWidth = 1.dp.toPx())

                    // Draw user strokes
                    for (p in paths) {
                        drawPath(
                            path = p,
                            color = Color(0xFF38BDF8),
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = { paths.clear() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear Pad", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ℹ️ Interactive stroke tracing pad for manual handwriting practice.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B), fontSize = 11.sp)
            )
        }
    }
}

@Composable
fun SpeakingEvaluationCard(
    expectedPhrase: String,
    onEvaluateTranscription: (String) -> Unit,
    evaluation: ChineseCoachEngine.SpeakingEvaluation?,
    onClearEvaluation: () -> Unit
) {
    var manualInput by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }

    // Android Speech Recognizer Intent Launcher
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isListening = false
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = matches?.firstOrNull() ?: ""
            if (recognizedText.isNotBlank()) {
                manualInput = recognizedText
                onEvaluateTranscription(recognizedText)
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPEAKING PRACTICE & SPEECH TRANSCRIPTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = ScholarCyan
                    )
                )
                FactBadge("SPEECH-TO-TEXT")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Target phrase to speak aloud:",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
            )
            Text(
                text = expectedPhrase,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Speech button or manual text transcription fallback
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        isListening = true
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak in Chinese: $expectedPhrase")
                        }
                        try {
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            isListening = false
                            // Fallback to manual entry if device lacks Google Speech recognizer
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = "Record Speech")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isListening) "Listening..." else "Record Speech", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        if (manualInput.isNotBlank()) {
                            onEvaluateTranscription(manualInput)
                        }
                    },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Compare Text", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = manualInput,
                onValueChange = { manualInput = it },
                label = { Text("Transcribed / Input Chinese Text") },
                placeholder = { Text("Speech recognition transcription will appear here...") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ScholarCyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                textStyle = MaterialTheme.typography.bodySmall
            )

            // Result
            if (evaluation != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33000000), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Transcription Match: ${evaluation.matchPercentage}%",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (evaluation.matchPercentage >= 70) ScholarGreen else ScholarGold
                                )
                            )
                            IconButton(onClick = onClearEvaluation, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = evaluation.toneGuidance,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFE2E8F0),
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "ℹ️ ${evaluation.disclaimer}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
