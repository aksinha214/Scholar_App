package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.FactBadge
import com.example.ui.components.MetricStatCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusTag
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun DashboardScreen(viewModel: ScholarViewModel) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val timetable by viewModel.timetableClasses.collectAsStateWithLifecycle()
    val research by viewModel.researchProjects.collectAsStateWithLifecycle()
    val milestones by viewModel.allResearchMilestones.collectAsStateWithLifecycle()
    val experiments by viewModel.allResearchExperiments.collectAsStateWithLifecycle()
    val careerGoals by viewModel.careerGoals.collectAsStateWithLifecycle()
    val skillPlans by viewModel.skillDevelopmentPlans.collectAsStateWithLifecycle()
    val applications by viewModel.jobApplications.collectAsStateWithLifecycle()
    val chinesePhrases by viewModel.chinesePhrases.collectAsStateWithLifecycle()
    val immigrationProfile by viewModel.immigrationProfile.collectAsStateWithLifecycle()
    val permitAudit by viewModel.residencePermitExpirationAudit.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                    // Try to load hero image or gradient fallback
                    Image(
                        painter = painterResource(id = R.drawable.cs_scholar_hero_1790889053362),
                        contentDescription = "CS Scholar OS Hero Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xCC090E17),
                                        Color(0xFF090E17)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(ScholarCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CS SCHOLAR OS • ONLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = ScholarCyan
                                )
                            )
                        }
                        Text(
                            text = profile?.name ?: "International Scholar",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${profile?.university ?: "Yanshan University"} • ${profile?.degree ?: "CS & Tech"}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1)
                            )
                        )
                    }
                }
            }
        }

        // STAGE 7: UNIFIED AI COMMAND CENTER HERO BANNER
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.COMMAND_CENTER) }
                    .testTag("dashboard_command_center_banner"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(ScholarCyan.copy(alpha = 0.5f))
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ScholarCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ScholarCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Command Center",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ScholarGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "UNIFIED OS",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ScholarGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Text(
                            text = "Daily briefing, cross-module priorities & intelligent Q&A",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Open Command Center",
                        tint = ScholarCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Quick Action Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    AssistChip(
                        onClick = { viewModel.navigateTo(AppScreen.COMMAND_CENTER) },
                        label = { Text("Command Center", fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ScholarCyan)
                        }
                    )
                }
                item {
                    AssistChip(
                        onClick = { viewModel.navigateTo(AppScreen.ROADMAP) },
                        label = { Text("What Am I Missing?") },
                        leadingIcon = {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = ScholarGold)
                        }
                    )
                }
                item {
                    AssistChip(
                        onClick = { viewModel.navigateTo(AppScreen.ROADMAP) },
                        label = { Text("Ask Future Self") },
                        leadingIcon = {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ScholarCyan)
                        }
                    )
                }
                item {
                    AssistChip(
                        onClick = { viewModel.navigateTo(AppScreen.AI_MENTOR) },
                        label = { Text("AI Mentor") },
                        leadingIcon = {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = DarkPrimary)
                        }
                    )
                }
                item {
                    AssistChip(
                        onClick = { viewModel.navigateTo(AppScreen.PROJECT_BUILDER) },
                        label = { Text("Generate Project") },
                        leadingIcon = {
                            Icon(Icons.Default.Build, contentDescription = null, tint = ScholarPurple)
                        }
                    )
                }
            }
        }

        // Metrics Grid (2x2)
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricStatCard(
                    title = "COURSES",
                    value = "${courses.size} Active",
                    subtitle = "YSU Info School",
                    icon = Icons.Default.School,
                    accentColor = DarkPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "DEADLINES",
                    value = "${tasks.count { !it.isCompleted }} Pending",
                    subtitle = "This Month",
                    icon = Icons.Default.Schedule,
                    accentColor = ScholarGold,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricStatCard(
                    title = "RESEARCH",
                    value = "CVPR 2027",
                    subtitle = "Point Cloud LiDAR",
                    icon = Icons.Default.Science,
                    accentColor = ScholarCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricStatCard(
                    title = "CHINESE",
                    value = "HSK 4 -> 5",
                    subtitle = "Academic & Tech",
                    icon = Icons.Default.Translate,
                    accentColor = ScholarPurple,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Today's University Lectures & Attendance
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.UNIVERSITY) },
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = ScholarCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "University Life & Timetable",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                        Text(
                            text = "Open Hub →",
                            style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scheduled classes, intelligent study planning, attendance forecast, and exam readiness.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = Color(0x22FFFFFF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Weekly Lectures", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color.LightGray))
                                Text("${timetable.size} Sessions", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
                            }
                        }
                        Surface(
                            color = Color(0x22FFFFFF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Attendance Target", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color.LightGray))
                                Text("80.0% Min", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = ScholarGold))
                            }
                        }
                    }
                }
            }
        }

        // Active Academic Tasks & Deadlines
        item {
            SectionHeader(
                title = "Today's Academic Deliverables",
                subtitle = "Active coursework & laboratory commitments",
                actionText = "Manage All",
                onActionClick = { viewModel.navigateTo(AppScreen.UNIVERSITY) }
            )
        }

        items(tasks.take(3)) { task ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleTask(task.id, !task.isCompleted) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { viewModel.toggleTask(task.id, it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = task.courseName,
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkPrimary
                            )
                            StatusTag(text = task.deadline, isCompleted = task.isCompleted)
                        }
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Research Lab Spotlight
        item {
            SectionHeader(
                title = "Active Research Project",
                subtitle = "Empirical computer science investigation & milestones",
                actionText = "Open Lab",
                onActionClick = { viewModel.navigateTo(AppScreen.RESEARCH_LAB) }
            )
        }

        item {
            val activeResearch = research.firstOrNull { it.isActive } ?: research.firstOrNull()
            if (activeResearch != null) {
                val nextMilestone = milestones.firstOrNull { it.projectId == activeResearch.id && !it.isCompleted }
                    ?: milestones.firstOrNull { !it.isCompleted }
                val recentExp = experiments.firstOrNull { it.projectId == activeResearch.id }
                    ?: experiments.firstOrNull()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(AppScreen.RESEARCH_LAB) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FactBadge(type = "RESEARCH LAB")
                            Surface(
                                color = ScholarGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Stage: ${activeResearch.currentStatus}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ScholarGold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = activeResearch.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Next Research Milestone
                        if (nextMilestone != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = ScholarCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Next Milestone: ${nextMilestone.title}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            ),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Target: ${nextMilestone.deadline}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = ScholarCyan
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Recent Experiment Result
                        if (recentExp != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Science,
                                        contentDescription = null,
                                        tint = ScholarGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Latest Run: ${recentExp.experimentId} (${recentExp.model})",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = ScholarGreen
                                            )
                                        )
                                        Text(
                                            text = recentExp.results,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Venue: ${activeResearch.targetVenue}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Workspace →",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DarkPrimary
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Career & Job Assistant Spotlight (Stage 4)
        item {
            val primaryGoal = careerGoals.firstOrNull { it.isPrimary } ?: careerGoals.firstOrNull()
            val activePlansCount = skillPlans.count { it.status == "In Progress" }
            val activeAppsCount = applications.count { it.status in listOf("Applied", "Interview", "Technical Round") }
            val upcomingInterview = applications.firstOrNull { it.interviewDate.isNotBlank() }

            SectionHeader(
                title = "Career & Job Assistant",
                subtitle = "Verifiable profile, skill gaps & recruitment roadmap",
                actionText = "Career Center",
                onActionClick = { viewModel.navigateTo(AppScreen.CAREER_CENTER) }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.CAREER_CENTER) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ScholarNavySurface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = DarkPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "TARGET ROLE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPrimary
                                )
                            )
                        }
                        Text(
                            text = "${skillPlans.size} Skill Plans • ${applications.size} Apps",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = primaryGoal?.targetRole ?: "Computer Vision Algorithm Engineer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Target: ${primaryGoal?.city ?: "Beijing / Shanghai"}, ${primaryGoal?.country ?: "China"} • Window: ${primaryGoal?.targetApplicationDate ?: "Spring 2027"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (upcomingInterview != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = ScholarGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Interview Scheduled: ${upcomingInterview.company} (${upcomingInterview.interviewDate})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = ScholarGold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Dev: $activePlansCount in progress | Tracked: $activeAppsCount active",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ScholarCyan
                            )
                        )
                        Text(
                            text = "Open Center →",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPrimary
                            )
                        )
                    }
                }
            }
        }

        // Daily Technical Chinese Spotlight
        item {
            SectionHeader(
                title = "Daily CS & Academic Chinese",
                subtitle = "Yanshan University lab & campus communications",
                actionText = "All Phrases",
                onActionClick = { viewModel.navigateTo(AppScreen.CHINESE_LANGUAGE) }
            )
        }

        item {
            val phrase = chinesePhrases.firstOrNull()
            if (phrase != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ScholarNavySurface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = phrase.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = ScholarCyan
                            )
                            IconButton(onClick = { viewModel.speakChinese(phrase.hanzi) }) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Pronunciation",
                                    tint = ScholarCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = phrase.hanzi,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = phrase.pinyin,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ScholarGoldLight,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = phrase.english,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFE2E8F0)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0x33000000),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "例句: ${phrase.exampleZh}\n${phrase.exampleEn}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Stage 6: China Student & Work Transition Spotlight
        item {
            SectionHeader(
                title = "China Work & Legal Transition",
                subtitle = "Study → Graduation → Work permit & immigration pathway",
                actionText = "Open Assistant",
                onActionClick = { viewModel.navigateTo(AppScreen.CHINA_WORK) }
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(AppScreen.CHINA_WORK) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ScholarNavySurface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = ScholarCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Target: ${immigrationProfile?.targetEmploymentCity ?: "Shanghai"} (${immigrationProfile?.targetJobField?.split("/")?.firstOrNull() ?: "AI Engineering"})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                        FactBadge(type = permitAudit.urgencyLevel)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Current Status: ${immigrationProfile?.currentStudentStatus ?: "Enrolled Full-time"} • Degree: ${immigrationProfile?.degreeLevel ?: "M.Sc."}",
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontSize = 11.5.sp)
                    )
                    Text(
                        text = "Study Residence Permit: ${permitAudit.expirationDate} (${permitAudit.daysRemaining ?: "--"} days left)",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Source-driven: Official NIA & SAFEA Guidelines",
                            style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan, fontSize = 10.sp)
                        )
                        Text(
                            text = "Explore Options →",
                            style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
