package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.RoadmapGoalEntity
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun RoadmapScreen(viewModel: ScholarViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Long-Term Timeline", "What Am I Missing?", "Ask Future Self", "Goals Checklist")

    val goals by viewModel.roadmapGoals.collectAsStateWithLifecycle()
    val auditReport by viewModel.auditReport.collectAsStateWithLifecycle()
    val futureSelfAnswer by viewModel.futureSelfAnswer.collectAsStateWithLifecycle()

    var futureQuestion by remember { mutableStateOf("Should I do a PhD in China/Singapore or pursue a direct R&D algorithm engineering job?") }
    var showAddGoalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = ScholarCyan
        ) {
            tabs.forEachIndexed { i, title ->
                Tab(
                    selected = selectedTab == i,
                    onClick = { selectedTab = i },
                    text = { Text(title, fontSize = 11.5.sp, fontWeight = if (selectedTab == i) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedTab) {
            0 -> LongTermTimelineView()
            1 -> PersonalAuditView(auditReport)
            2 -> FutureSelfView(
                question = futureQuestion,
                answer = futureSelfAnswer,
                onQuestionChange = { futureQuestion = it },
                onAsk = { viewModel.askFutureSelf(futureQuestion) }
            )
            3 -> GoalsChecklistView(
                goals = goals,
                onToggle = { id, comp -> viewModel.toggleGoal(id, comp) },
                onAddGoal = { showAddGoalDialog = true }
            )
        }
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { goal ->
                viewModel.insertGoal(goal)
                showAddGoalDialog = false
            }
        )
    }
}

@Composable
fun LongTermTimelineView() {
    val timelineNodes = listOf(
        TimelineNode("1. Bachelor Foundations (Year 1-2)", "Yanshan University", "Algorithms, Data Structures, Modern C++, Linear Algebra, HSK 4 Certification.", true),
        TimelineNode("2. Advanced Specialization (Year 3 - CURRENT)", "YSU AI Lab", "3D Point Cloud Perception, PyTorch, Operating Systems Internals, HSK 5 preparation.", true),
        TimelineNode("3. Research Breakthrough (Fall 2026 / Spring 2027)", "Target Venue: CVPR 2027", "First-author or co-author paper submission; open-source reproducible benchmark release.", false),
        TimelineNode("4. Undergraduate Thesis & Defense (June 2027)", "Yanshan University", "First-Class Honors Bachelor of Engineering graduation; thesis oral defense in Chinese.", false),
        TimelineNode("5. Direct PhD / Top Lab R&D (2027 - 2031)", "Tsinghua / ZJU / NUS / DeepSeek", "Full scholarship doctoral fellowship or High-Level Algorithm Engineer (Category B/A Work Permit).", false),
        TimelineNode("6. Principal AI Research Scientist (2032+)", "Global / Greater China AI", "Leadership in multimodal 3D perception, international research collaborations, mentorship.", false)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Long-Term Academic & Career Trajectory",
                subtitle = "From Yanshan University to Top-Tier AI Research Scientist"
            )
        }

        items(timelineNodes) { node ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (node.isCurrent) ScholarNavySurface else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(if (node.isCurrent) ScholarCyan else DarkPrimary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = node.stage,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            if (node.isCurrent) {
                                FactBadge("CURRENT STAGE")
                            }
                        }
                        Text(node.institution, style = MaterialTheme.typography.labelSmall, color = ScholarGold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = node.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalAuditView(report: com.example.ai.PersonalAuditEngine.AuditReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "'WHAT AM I MISSING?' — Personal Gap Audit",
                subtitle = "Transparent evaluation rubric across 14 academic and career dimensions"
            )
        }

        item {
            Card(
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
                            text = "Overall Readiness: ${report.overallReadinessScore}/100",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarCyan
                            )
                        )
                        FactBadge("RUBRIC AUDIT")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = report.summaryStatement,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 18.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "TOP THREE PRIORITIES:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                    )
                    report.topThreePriorities.forEach { p ->
                        Text(
                            text = p,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFDE68A), lineHeight = 17.sp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                }
            }
        }

        items(report.dimensions) { dim ->
            val statusColor = when (dim.status) {
                "Strong" -> ScholarGreen
                "On Track" -> ScholarCyan
                "Needs Focus" -> ScholarGold
                else -> Color(0xFFEF4444)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dim.dimensionName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            color = statusColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${dim.scoreOutOfTen}/10 • ${dim.status}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Status: ${dim.concreteEvidence}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Action: ${dim.immediateActionItem}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = ScholarCyan)
                    )
                }
            }
        }
    }
}

@Composable
fun FutureSelfView(
    question: String,
    answer: String?,
    onQuestionChange: (String) -> Unit,
    onAsk: () -> Unit
) {
    val quickQuestions = listOf(
        "Should I do a PhD in China/Singapore or pursue a direct R&D algorithm engineering job?",
        "How important is mastering technical CS Chinese for an international researcher?",
        "My paper got harsh reviewer critiques—how did we handle it?",
        "What routines worked best during our time at Yanshan University?"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Ask My Future Self (Class of 2027 -> 2032)",
                subtitle = "Foresight simulation from a Senior CS Researcher & YSU Alumni"
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = question,
                        onValueChange = onQuestionChange,
                        label = { Text("Ask your experienced future self...") },
                        modifier = Modifier.fillMaxWidth().height(90.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAsk,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary, contentColor = DarkOnPrimary)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Receive Future Foresight")
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("COMMON STRATEGIC DILEMMAS:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
                quickQuestions.forEach { qq ->
                    SuggestionChip(
                        onClick = {
                            onQuestionChange(qq)
                            onAsk()
                        },
                        label = { Text(qq, fontSize = 11.sp, maxLines = 1) }
                    )
                }
            }
        }

        if (answer != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        FactBadge("PERSONAL FORESIGHT (NOT LEGAL FACT)")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = answer,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFE2E8F0),
                                lineHeight = 21.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalsChecklistView(
    goals: List<RoadmapGoalEntity>,
    onToggle: (Long, Boolean) -> Unit,
    onAddGoal: () -> Unit
) {
    val timeframes = listOf("Daily", "Weekly", "Semester", "Long-Term")

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Execution Goals & Milestones",
                subtitle = "Granular daily actions cascading into long-term impact",
                actionText = "+ Add Goal",
                onActionClick = onAddGoal
            )
        }

        timeframes.forEach { tf ->
            val matching = goals.filter { it.timeframe.equals(tf, ignoreCase = true) }
            if (matching.isNotEmpty()) {
                item {
                    Text(
                        text = "$tf Goals".uppercase(),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ScholarCyan,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                items(matching) { goal ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onToggle(goal.id, !goal.isCompleted) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (goal.isCompleted) ScholarNavySurface else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = goal.isCompleted, onCheckedChange = { onToggle(goal.id, it) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = goal.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (goal.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = "Target: ${goal.targetDate} • Priority: ${goal.priority}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ScholarGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class TimelineNode(
    val stage: String,
    val institution: String,
    val description: String,
    val isCurrent: Boolean
)

@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (RoadmapGoalEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var timeframe by remember { mutableStateOf("Weekly") }
    var date by remember { mutableStateOf("This Week") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Execution Goal") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Goal Title") })
                OutlinedTextField(value = timeframe, onValueChange = { timeframe = it }, label = { Text("Timeframe (Daily, Weekly, Semester, Long-Term)") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Target Date") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            RoadmapGoalEntity(
                                title = title,
                                timeframe = timeframe,
                                targetDate = date
                            )
                        )
                    }
                }
            ) { Text("Save Goal") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
