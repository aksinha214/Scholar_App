package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun ProjectBuilderScreen(viewModel: ScholarViewModel) {
    var promptInput by remember { mutableStateOf("I want a computer vision project") }
    val currentProject by viewModel.currentGeneratedProject.collectAsStateWithLifecycle()
    val savedProjects by viewModel.generatedProjects.collectAsStateWithLifecycle()

    val quickPrompts = listOf(
        "I want a computer vision project",
        "Edge AI TinyML industrial defect detection",
        "3D LiDAR point cloud autonomous sensing",
        "High-performance C++ OS kernel tool"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "AI Project Specification Generator",
                subtitle = "Progressively advanced systems & computer vision blueprints"
            )
        }

        // Generator Input Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        label = { Text("What project do you want to build?") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.generateProject(promptInput) }) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "Generate", tint = ScholarCyan)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickPrompts) { qp ->
                            SuggestionChip(
                                onClick = {
                                    promptInput = qp
                                    viewModel.generateProject(qp)
                                },
                                label = { Text(qp, fontSize = 11.sp, maxLines = 1) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.generateProject(promptInput) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary, contentColor = DarkOnPrimary)
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Full Engineering Blueprint")
                    }
                }
            }
        }

        // Generated Project Details View
        if (currentProject != null) {
            val p = currentProject!!
            item {
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
                            FactBadge(p.difficulty)
                            Button(
                                onClick = { viewModel.saveCurrentGeneratedProject() },
                                colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan, contentColor = Color(0xFF0A1128))
                            ) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save to Portfolio", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = p.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = p.category,
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        DetailSection("Research / Engineering Value", p.researchValue, ScholarGold)
                        DetailSection("Prerequisites", p.prerequisites, Color(0xFFCBD5E1))
                        DetailSection("Recommended Datasets", p.dataset, ScholarCyan)
                        DetailSection("Technologies & Stack", p.technologies, DarkPrimary)
                        DetailSection("System Architecture", p.architecture, ScholarGoldLight)
                        DetailSection("6-Week Implementation Milestones", p.milestones, Color(0xFF6EE7B7))
                        DetailSection("GitHub Repository Structure", p.githubStructure, Color(0xFF93C5FD))
                        DetailSection("README Preview", p.readmeMarkdown, Color(0xFFE2E8F0))
                        DetailSection("Experiments & Evaluation Plan", p.experimentsPlan, ScholarGold)
                        DetailSection("Defense Presentation Outline", p.presentationOutline, ScholarCyan)
                        DetailSection("CV Impact Bullet Points", p.cvDescription, ScholarGreen)
                    }
                }
            }
        }

        // Saved Projects List
        if (savedProjects.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Saved Project Portfolio",
                    subtitle = "Archived project specifications"
                )
            }
            items(savedProjects) { sp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(sp.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            IconButton(onClick = { viewModel.deleteGeneratedProject(sp) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(sp.category, style = MaterialTheme.typography.bodySmall, color = DarkPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(sp.cvDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: String, titleColor: Color) {
    if (content.isBlank()) return
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                color = titleColor
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
