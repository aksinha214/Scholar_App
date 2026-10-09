package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.ai.AcademicLearningAdvisor
import com.example.ai.LearningRecommendation
import com.example.data.model.SkillItemEntity
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun SkillsScreen(viewModel: ScholarViewModel) {
    val allSkills by viewModel.skills.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()

    val categories = listOf("All", "Programming", "Core CS", "AI", "Computer Vision", "Research Skills", "Systems")
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddSkillDialog by remember { mutableStateOf(false) }
    var skillToEdit by remember { mutableStateOf<SkillItemEntity?>(null) }
    var skillToDelete by remember { mutableStateOf<SkillItemEntity?>(null) }

    // Dismissed recommendations list
    var dismissedRecIds by remember { mutableStateOf(setOf<String>()) }

    val recommendations = remember(userProfile, courses, allSkills) {
        AcademicLearningAdvisor.generateCourseAndSkillRecommendations(
            profile = userProfile,
            courses = courses,
            skills = allSkills,
            researchInterests = userProfile?.researchInterests ?: "Computer Vision, 3D Deep Learning",
            targetHskLevel = userProfile?.chineseProficiency ?: "HSK 5"
        )
    }.filter { it.id !in dismissedRecIds }

    val filteredSkills = remember(allSkills, selectedCategory) {
        if (selectedCategory == "All") allSkills
        else allSkills.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val completedCount = allSkills.count { it.isCompleted }
    val progress = if (allSkills.isNotEmpty()) completedCount.toFloat() / allSkills.size else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Computer Science Skills Roadmap",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Competency mastery & AI recommendations",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Button(
                    onClick = { showAddSkillDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Skill", fontSize = 11.5.sp)
                }
            }
        }

        // Overall Mastery Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Overall CS Mastery",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% ($completedCount/${allSkills.size})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarCyan
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = ScholarCyan,
                        trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }
            }
        }

        // AI Personalized Recommendations Section
        if (recommendations.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ScholarGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AI Course & Skill Recommendations",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FactBadge("CONTEXT-AWARE")
                }
            }

            items(recommendations) { rec ->
                RecommendationCard(
                    rec = rec,
                    onAccept = {
                        rec.skillToAdd?.let { skill ->
                            viewModel.insertSkill(skill)
                        }
                        dismissedRecIds = dismissedRecIds + rec.id
                    },
                    onDismiss = {
                        dismissedRecIds = dismissedRecIds + rec.id
                    }
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSel = cat == selectedCategory
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkPrimary,
                            selectedLabelColor = DarkOnPrimary
                        )
                    )
                }
            }
        }

        if (filteredSkills.isEmpty()) {
            item {
                com.example.ui.components.EmptyStateView(
                    icon = Icons.Default.School,
                    title = "No Competencies in '$selectedCategory'",
                    subtitle = "Add your first technical skill, language, or systems tool.",
                    actionText = "+ Add Competency",
                    onAction = { showAddSkillDialog = true }
                )
            }
        } else {
            items(filteredSkills) { skill ->
                SkillItemRow(
                    skill = skill,
                    onToggle = { viewModel.toggleSkill(skill.id, !skill.isCompleted) },
                    onEdit = { skillToEdit = skill },
                    onDelete = { skillToDelete = skill }
                )
            }
        }
    }

    if (showAddSkillDialog) {
        AddSkillItemDialog(
            onDismiss = { showAddSkillDialog = false },
            onConfirm = { skill ->
                viewModel.insertSkill(skill)
                showAddSkillDialog = false
            }
        )
    }

    if (skillToEdit != null) {
        EditSkillItemDialog(
            skill = skillToEdit!!,
            onDismiss = { skillToEdit = null },
            onConfirm = { updated ->
                viewModel.updateSkill(updated)
                skillToEdit = null
            }
        )
    }

    if (skillToDelete != null) {
        com.example.ui.components.DeleteConfirmationDialog(
            title = "Delete Competency",
            message = "Are you sure you want to remove '${skillToDelete!!.name}' from your skills matrix?",
            onConfirm = {
                viewModel.deleteSkill(skillToDelete!!)
                skillToDelete = null
            },
            onDismiss = { skillToDelete = null }
        )
    }
}

@Composable
fun RecommendationCard(
    rec: LearningRecommendation,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    val priorityColor = when (rec.priority) {
        "High" -> ScholarCyan
        "Medium" -> ScholarGold
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = priorityColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "${rec.priority.uppercase()} PRIORITY • ${rec.category}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = priorityColor,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "~${rec.estimatedDurationWeeks} Weeks",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = rec.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Why Recommended: ${rec.whyRecommended}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 18.sp)
            )

            if (rec.prerequisites.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Prerequisites: ${rec.prerequisites}",
                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontSize = 11.5.sp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Next Action: ${rec.suggestedAction}",
                style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontSize = 11.5.sp)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Dismiss", color = Color(0xFF94A3B8), fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Learning Plan", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SkillItemRow(
    skill: SkillItemEntity,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val levelColor = when (skill.level) {
        "Research-Ready" -> ScholarCyan
        "Advanced" -> ScholarGreen
        "Intermediate" -> ScholarGold
        else -> DarkPrimary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (skill.isCompleted) ScholarNavySurface else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = skill.isCompleted,
                onCheckedChange = { onToggle() }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = skill.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Surface(
                        color = levelColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = skill.level,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = levelColor
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = skill.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AddSkillItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (SkillItemEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Core CS") }
    var level by remember { mutableStateOf("Intermediate") }
    var desc by remember { mutableStateOf("") }

    val categories = listOf("Programming", "Core CS", "AI", "Computer Vision", "Research Skills", "Systems")
    val levels = listOf("Beginner", "Intermediate", "Advanced", "Research-Ready")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Competency / Skill", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Competency Name *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description & Evidence") }, modifier = Modifier.fillMaxWidth())

                Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Proficiency Level:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(levels) { lvl ->
                        FilterChip(
                            selected = level == lvl,
                            onClick = { level = lvl },
                            label = { Text(lvl, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            SkillItemEntity(
                                name = name.trim(),
                                category = category,
                                level = level,
                                description = desc.ifBlank { "Tracked competence in $name." },
                                isCompleted = false
                            )
                        )
                    }
                }
            ) { Text("Save Skill") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditSkillItemDialog(
    skill: SkillItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (SkillItemEntity) -> Unit
) {
    var name by remember { mutableStateOf(skill.name) }
    var category by remember { mutableStateOf(skill.category) }
    var level by remember { mutableStateOf(skill.level) }
    var desc by remember { mutableStateOf(skill.description) }

    val categories = listOf("Programming", "Core CS", "AI", "Computer Vision", "Research Skills", "Systems")
    val levels = listOf("Beginner", "Intermediate", "Advanced", "Research-Ready")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Competency", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Competency Name *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description & Evidence") }, modifier = Modifier.fillMaxWidth())

                Text("Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 11.sp) }
                        )
                    }
                }

                Text("Proficiency Level:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(levels) { lvl ->
                        FilterChip(
                            selected = level == lvl,
                            onClick = { level = lvl },
                            label = { Text(lvl, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            skill.copy(
                                name = name.trim(),
                                category = category,
                                level = level,
                                description = desc.trim()
                            )
                        )
                    }
                }
            ) { Text("Save Changes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
