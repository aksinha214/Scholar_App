package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.*
import com.example.data.model.*
import com.example.ui.components.FactBadge
import com.example.ui.components.ModuleTabBar
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CareerScreen(viewModel: ScholarViewModel) {
    val careerProfile by viewModel.careerProfile.collectAsStateWithLifecycle()
    val careerGoals by viewModel.careerGoals.collectAsStateWithLifecycle()
    val skillInventory by viewModel.skillInventory.collectAsStateWithLifecycle()
    val filteredJobs by viewModel.filteredJobPostings.collectAsStateWithLifecycle()
    val skillPlans by viewModel.skillDevelopmentPlans.collectAsStateWithLifecycle()
    val applications by viewModel.jobApplications.collectAsStateWithLifecycle()
    val interviewQuestions by viewModel.interviewQuestions.collectAsStateWithLifecycle()
    val roadmapMilestones by viewModel.careerRoadmapMilestones.collectAsStateWithLifecycle()
    val researchProjects by viewModel.researchProjects.collectAsStateWithLifecycle()
    val activeResearch by viewModel.activeResearchProject.collectAsStateWithLifecycle()

    val parsedJob by viewModel.parsedJobDescription.collectAsStateWithLifecycle()
    val gapReport by viewModel.careerGapReport.collectAsStateWithLifecycle()
    val resumeAnalysis by viewModel.resumeAnalysis.collectAsStateWithLifecycle()
    val jobCvMatch by viewModel.jobSpecificCvMatch.collectAsStateWithLifecycle()
    val interviewFeedback by viewModel.activeInterviewFeedback.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Dashboard",
        "Profile & Goals",
        "Skill Inventory",
        "Job Analyzer & Gaps",
        "CV Assistant",
        "Interview Prep",
        "Applications & Roadmap"
    )

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddSkillDialog by remember { mutableStateOf(false) }
    var showAddPlanDialog by remember { mutableStateOf(false) }
    var showAddAppDialog by remember { mutableStateOf(false) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Screen Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Career & Job Assistant",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Factual qualifications, verifiable skill gaps & international tech careers",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                IconButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier.testTag("career_edit_profile_btn")
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Career Profile",
                        tint = DarkPrimary
                    )
                }
            }

            // Unified Tab Row
            ModuleTabBar(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Tab Content
            when (selectedTab) {
                0 -> CareerDashboardTab(
                    profile = careerProfile,
                    goals = careerGoals,
                    skills = skillInventory,
                    plans = skillPlans,
                    jobs = filteredJobs,
                    applications = applications,
                    milestones = roadmapMilestones,
                    onNavigateTab = { selectedTab = it },
                    onEditProfile = { showEditProfileDialog = true }
                )
                1 -> CareerProfileAndGoalsTab(
                    profile = careerProfile,
                    goals = careerGoals,
                    researchProjects = researchProjects,
                    onEditProfile = { showEditProfileDialog = true },
                    onAddGoal = { showAddGoalDialog = true },
                    onDeleteGoal = { viewModel.deleteCareerGoal(it) }
                )
                2 -> SkillInventoryAndPlansTab(
                    skills = skillInventory,
                    plans = skillPlans,
                    onAddSkill = { showAddSkillDialog = true },
                    onDeleteSkill = { viewModel.deleteSkillInventory(it) },
                    onAddPlan = { showAddPlanDialog = true },
                    onUpdatePlanStatus = { id, status -> viewModel.updateSkillPlanStatus(id, status) },
                    onDeletePlan = { viewModel.deleteSkillPlan(it) },
                    onSyncPlanToPlanner = { viewModel.syncSkillPlanToStudyPlanner(it) }
                )
                3 -> JobAnalyzerAndGapsTab(
                    viewModel = viewModel,
                    parsedJob = parsedJob,
                    gapReport = gapReport,
                    jobs = filteredJobs,
                    onAnalyzeText = { viewModel.analyzeJobDescriptionText(it) },
                    onClearParsed = { viewModel.clearParsedJob() },
                    onAnalyzeGap = { viewModel.analyzeGapForJob(it) },
                    onClearGap = { viewModel.clearGapReport() },
                    onSaveJob = { job -> viewModel.saveParsedJobAsPosting(job) },
                    onToggleSave = { id, saved -> viewModel.toggleSaveJob(id, saved) },
                    onCreatePlanFromMissing = { missingSkill ->
                        viewModel.insertSkillPlan(
                            SkillDevelopmentPlanEntity(
                                skillName = missingSkill,
                                currentLevel = "Beginner",
                                targetLevel = "Intermediate",
                                whyItMatters = "Identified as missing requirement for target role.",
                                learningResources = "Official documentation, open-source repositories.",
                                practiceTask = "Implement minimal reproducible demo using $missingSkill.",
                                suggestedProject = "Integrate $missingSkill into portfolio project.",
                                targetDate = "Next Month",
                                status = "In Progress"
                            )
                        )
                    }
                )
                4 -> CvAssistantTab(
                    resumeAnalysis = resumeAnalysis,
                    jobCvMatch = jobCvMatch,
                    parsedJob = parsedJob,
                    onAnalyzeCv = { viewModel.analyzeResumeText(it) },
                    onClearCv = { viewModel.clearResumeAnalysis() },
                    onAnalyzeJobSpecificCv = { cv, job -> viewModel.analyzeJobSpecificCV(cv, job) },
                    onClearJobSpecific = { viewModel.clearJobSpecificCvMatch() }
                )
                5 -> InterviewPrepTab(
                    questions = interviewQuestions,
                    activeResearch = activeResearch,
                    feedback = interviewFeedback,
                    onAddQuestion = { showAddQuestionDialog = true },
                    onDeleteQuestion = { viewModel.deleteInterviewQuestion(it) },
                    onEvaluateAnswer = { q, ans, cat, ctx ->
                        viewModel.evaluateInterviewResponse(q, ans, cat, ctx)
                    },
                    onClearFeedback = { viewModel.clearInterviewFeedback() },
                    onGenerateFromResearch = {
                        activeResearch?.let { viewModel.generateProjectInterviewQuestions(it) }
                    },
                    onSaveFeedback = { q, ans, fb, imp ->
                        viewModel.updateInterviewQuestion(
                            q.copy(userAnswer = ans, aiFeedback = fb, improvedAnswer = imp, isPracticed = true)
                        )
                    }
                )
                6 -> ApplicationsAndRoadmapTab(
                    applications = applications,
                    milestones = roadmapMilestones,
                    onAddApplication = { showAddAppDialog = true },
                    onDeleteApplication = { viewModel.deleteJobApplication(it) },
                    onUpdateAppStatus = { app, status ->
                        viewModel.updateJobApplication(app.copy(status = status))
                    },
                    onAddMilestone = { showAddMilestoneDialog = true },
                    onToggleMilestone = { id, comp -> viewModel.toggleRoadmapMilestone(id, comp) },
                    onDeleteMilestone = { viewModel.deleteRoadmapMilestone(it) },
                    onSyncMilestone = { viewModel.syncRoadmapMilestoneToStudyPlanner(it) }
                )
            }
        }
    }

    // Dialogs
    if (showEditProfileDialog && careerProfile != null) {
        EditCareerProfileDialog(
            profile = careerProfile!!,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.updateCareerProfile(updated)
                showEditProfileDialog = false
            }
        )
    }

    if (showAddGoalDialog) {
        AddCareerGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onSave = { goal ->
                viewModel.insertCareerGoal(goal)
                showAddGoalDialog = false
            }
        )
    }

    if (showAddSkillDialog) {
        AddSkillInventoryDialog(
            onDismiss = { showAddSkillDialog = false },
            onSave = { skill ->
                viewModel.insertSkillInventory(skill)
                showAddSkillDialog = false
            }
        )
    }

    if (showAddPlanDialog) {
        AddSkillPlanDialog(
            onDismiss = { showAddPlanDialog = false },
            onSave = { plan ->
                viewModel.insertSkillPlan(plan)
                showAddPlanDialog = false
            }
        )
    }

    if (showAddAppDialog) {
        AddJobApplicationDialog(
            onDismiss = { showAddAppDialog = false },
            onSave = { app ->
                viewModel.insertJobApplication(app)
                showAddAppDialog = false
            }
        )
    }

    if (showAddQuestionDialog) {
        AddInterviewQuestionDialog(
            onDismiss = { showAddQuestionDialog = false },
            onSave = { q ->
                viewModel.insertInterviewQuestion(q)
                showAddQuestionDialog = false
            }
        )
    }

    if (showAddMilestoneDialog) {
        AddRoadmapMilestoneDialog(
            onDismiss = { showAddMilestoneDialog = false },
            onSave = { m ->
                viewModel.insertRoadmapMilestone(m)
                showAddMilestoneDialog = false
            }
        )
    }
}

// ==========================================
// 1. CAREER DASHBOARD TAB (Requirement 16)
// ==========================================
@Composable
fun CareerDashboardTab(
    profile: CareerProfileEntity?,
    goals: List<CareerGoalEntity>,
    skills: List<SkillInventoryEntity>,
    plans: List<SkillDevelopmentPlanEntity>,
    jobs: List<JobPostingEntity>,
    applications: List<JobApplicationEntity>,
    milestones: List<CareerRoadmapMilestoneEntity>,
    onNavigateTab: (Int) -> Unit,
    onEditProfile: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Primary Goal & Role Card
        item {
            val primaryGoal = goals.firstOrNull { it.isPrimary } ?: goals.firstOrNull()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRIMARY CAREER GOAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarGold,
                                letterSpacing = 1.sp
                            )
                        )
                        FactBadge(type = "VERIFIED")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = primaryGoal?.targetRole ?: (profile?.targetJobTitles?.split(",")?.firstOrNull() ?: "AI / Computer Vision Engineer"),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Target Locations: ${primaryGoal?.city ?: profile?.targetCities ?: "Beijing / Shanghai / Singapore"} • Industry: ${primaryGoal?.industry ?: profile?.targetIndustries ?: "Autonomous Systems"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onEditProfile,
                            modifier = Modifier.weight(1f).testTag("dashboard_edit_profile_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontSize = 12.sp)
                        }
                        OutlinedButton(
                            onClick = { onNavigateTab(3) },
                            modifier = Modifier.weight(1f).testTag("dashboard_scan_job_btn"),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Job", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Factual KPI Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Verified Skills",
                    value = skills.size.toString(),
                    subtitle = "${skills.count { it.currentLevel in listOf("Advanced", "Expert") }} Adv/Expert",
                    icon = Icons.Default.CheckCircle,
                    color = ScholarGreen,
                    onClick = { onNavigateTab(2) }
                )
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Active Plans",
                    value = plans.count { it.status == "In Progress" }.toString(),
                    subtitle = "${plans.size} Total Plans",
                    icon = Icons.Default.TrendingUp,
                    color = ScholarCyan,
                    onClick = { onNavigateTab(2) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Applications",
                    value = applications.size.toString(),
                    subtitle = "${applications.count { it.status in listOf("Applied", "Interview", "Technical Round") }} Active",
                    icon = Icons.Default.Send,
                    color = ScholarGold,
                    onClick = { onNavigateTab(6) }
                )
                DashboardMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Roadmap",
                    value = "${milestones.count { it.isCompleted }}/${milestones.size}",
                    subtitle = "Milestones Done",
                    icon = Icons.Default.Flag,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onNavigateTab(6) }
                )
            }
        }

        // Active Application Spotlight
        item {
            val pendingApp = applications.firstOrNull { it.status in listOf("Interview", "Technical Round", "Preparing", "Applied") }
            SectionHeader(
                title = "Application Spotlight",
                subtitle = "Active recruitment process",
                actionText = "All (${applications.size})",
                onActionClick = { onNavigateTab(6) }
            )
            if (pendingApp != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = pendingApp.company,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            StatusBadge(status = pendingApp.status)
                        }
                        Text(
                            text = "${pendingApp.position} • ${pendingApp.location}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        if (pendingApp.interviewDate.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Event, contentDescription = null, tint = ScholarGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Interview: ${pendingApp.interviewDate}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold, fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "No active applications currently in flight.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Active Development Plan Spotlight
        item {
            val activePlan = plans.firstOrNull { it.status == "In Progress" }
            SectionHeader(
                title = "Active Skill Development",
                subtitle = "Closing verified capability gaps",
                actionText = "All Plans",
                onActionClick = { onNavigateTab(2) }
            )
            if (activePlan != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activePlan.skillName,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPrimary
                                )
                            )
                            Text(
                                text = "Target: ${activePlan.targetDate}",
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Task: ${activePlan.practiceTask}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Why: ${activePlan.whyItMatters}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            } else {
                Text(
                    text = "No skill development plan currently in progress.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // CV Readiness Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CV & Resume Readiness",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Audit structural clarity, weak phrasing & job terminology match",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Button(
                        onClick = { onNavigateTab(4) },
                        colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Audit CV", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            )
        }
    }
}

// ==========================================
// 2. PROFILE & GOALS TAB (Requirements 1, 2, 7)
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerProfileAndGoalsTab(
    profile: CareerProfileEntity?,
    goals: List<CareerGoalEntity>,
    researchProjects: List<ResearchProjectEntity>,
    onEditProfile: () -> Unit,
    onAddGoal: () -> Unit,
    onDeleteGoal: (CareerGoalEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Profile Summary Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Verifiable Career Profile",
                    subtitle = "Zero-invention academic & technical credentials"
                )
                Button(
                    onClick = onEditProfile,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Profile", fontSize = 11.sp)
                }
            }
        }

        if (profile != null) {
            // Education & Credentials
            item {
                ProfileSectionCard(title = "Education & Institution", icon = Icons.Default.School) {
                    ProfileFieldItem("Degree", profile.degree)
                    ProfileFieldItem("University", profile.university)
                    ProfileFieldItem("Department", profile.department)
                    ProfileFieldItem("Graduation Date", profile.graduationDate)
                    ProfileFieldItem("Research Areas", profile.researchAreas)
                }
            }

            // Technical Capabilities
            item {
                ProfileSectionCard(title = "Technical Stack & Frameworks", icon = Icons.Default.Terminal) {
                    ProfileFieldItem("Programming Languages", profile.programmingLanguages)
                    ProfileFieldItem("AI / ML Frameworks", profile.frameworks)
                    ProfileFieldItem("AI / ML Skills", profile.aiMlSkills)
                    ProfileFieldItem("Computer Vision", profile.cvSkills)
                    ProfileFieldItem("Software Engineering", profile.seSkills)
                    ProfileFieldItem("Databases & Cloud", "${profile.databases} • ${profile.cloud}")
                    ProfileFieldItem("Data Science & Metrics", profile.dataScience)
                    ProfileFieldItem("Research Capabilities", profile.researchSkills)
                }
            }

            // Experience, Projects, Publications
            item {
                ProfileSectionCard(title = "Publications & Lab Experience", icon = Icons.Default.Science) {
                    ProfileFieldItem("Publications", profile.publications)
                    ProfileFieldItem("Key Projects", profile.projects)
                    ProfileFieldItem("Internships", profile.internships)
                    ProfileFieldItem("Work Experience", profile.workExperience)
                    ProfileFieldItem("Certifications", profile.certifications)
                }
            }

            // Links & Languages
            item {
                ProfileSectionCard(title = "Online Presence & Languages", icon = Icons.Default.Public) {
                    ProfileFieldItem("GitHub", profile.githubUrl)
                    ProfileFieldItem("Portfolio", profile.portfolioUrl)
                    ProfileFieldItem("LinkedIn", profile.linkedinUrl)
                    ProfileFieldItem("English Proficiency", profile.englishProficiency)
                    ProfileFieldItem("Chinese Proficiency", profile.chineseProficiency)
                }
            }
        }

        // Target Career Goals Section (Requirement 2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Target Career Goals",
                    subtitle = "Selected industry & academic pathways"
                )
                IconButton(onClick = onAddGoal) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Goal", tint = DarkPrimary)
                }
            }
        }

        items(goals) { goal ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (goal.isPrimary) ScholarNavySurface else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = goal.targetRole,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (goal.isPrimary) {
                                Surface(
                                    color = ScholarGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Primary",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                            IconButton(onClick = { onDeleteGoal(goal) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Location: ${goal.city}, ${goal.country} • Industry: ${goal.industry}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "Target Level: ${goal.desiredExperienceLevel} • Application Window: ${goal.targetApplicationDate}",
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                    )
                    if (goal.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Notes: ${goal.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }

        // Connected Project Portfolio (Requirement 7)
        item {
            SectionHeader(
                title = "Connected Project Portfolio",
                subtitle = "Verified skills demonstrated by research & campus projects"
            )
        }

        items(researchProjects) { proj ->
            val demonstratedSkills = CareerIntelligenceEngine.deriveProjectDemonstratedSkills(
                title = proj.title,
                description = proj.description,
                methodology = proj.methodology,
                dataset = proj.datasets,
                models = proj.modelsAlgorithms,
                metrics = proj.metrics
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = proj.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DarkPrimary),
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = ScholarGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Research Lab",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarGreen, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = proj.description,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Demonstrated Skills (Supported by Project Information):",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        demonstratedSkills.forEach { sk ->
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "→ $sk",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = DarkPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ProfileFieldItem(label: String, value: String) {
    if (value.isNotBlank()) {
        Column(modifier = Modifier.padding(vertical = 3.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}

// ==========================================
// 3. SKILL INVENTORY & PLANS (Requirements 3, 6)
// ==========================================
@Composable
fun SkillInventoryAndPlansTab(
    skills: List<SkillInventoryEntity>,
    plans: List<SkillDevelopmentPlanEntity>,
    onAddSkill: () -> Unit,
    onDeleteSkill: (SkillInventoryEntity) -> Unit,
    onAddPlan: () -> Unit,
    onUpdatePlanStatus: (Long, String) -> Unit,
    onDeletePlan: (SkillDevelopmentPlanEntity) -> Unit,
    onSyncPlanToPlanner: (SkillDevelopmentPlanEntity) -> Unit
) {
    var filterCategory by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "Languages", "AI/ML", "Computer Vision", "Tools")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Skill Inventory Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Skill Inventory (${skills.size})",
                    subtitle = "Evidence-based technical capabilities (No unverified levels)"
                )
                IconButton(onClick = onAddSkill) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Skill", tint = DarkPrimary)
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = filterCategory == cat,
                        onClick = { filterCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Skills List
        val filteredSkills = if (filterCategory == "ALL") skills else skills.filter { it.category.equals(filterCategory, ignoreCase = true) }
        items(filteredSkills) { skill ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = skill.skillName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = skill.category,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            SkillLevelBadge(level = skill.currentLevel)
                            IconButton(onClick = { onDeleteSkill(skill) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Documented Evidence: ${skill.evidence}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Project: ${skill.relatedProject} • Last Practiced: ${skill.lastPracticed}",
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                    )
                }
            }
        }

        // Skill Development Plans Section (Requirement 6)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Skill Development Plans (${plans.size})",
                    subtitle = "Actionable plans to close missing or weak requirements"
                )
                IconButton(onClick = onAddPlan) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Plan", tint = DarkPrimary)
                }
            }
        }

        items(plans) { plan ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${plan.skillName} (${plan.currentLevel} → ${plan.targetLevel})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkPrimary
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = plan.status)
                            IconButton(onClick = { onDeletePlan(plan) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Why it matters: ${plan.whyItMatters}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Practice Task: ${plan.practiceTask}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = ScholarGold
                        )
                    )
                    Text(
                        text = "Suggested Project: ${plan.suggestedProject} • Target: ${plan.targetDate}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = "Resources: ${plan.learningResources}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (plan.status != "Completed") {
                                OutlinedButton(
                                    onClick = { onUpdatePlanStatus(plan.id, "Completed") },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Mark Done", fontSize = 10.sp)
                                }
                            }
                            if (plan.status != "In Progress") {
                                OutlinedButton(
                                    onClick = { onUpdatePlanStatus(plan.id, "In Progress") },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Mark In Progress", fontSize = 10.sp)
                                }
                            }
                        }
                        Button(
                            onClick = { onSyncPlanToPlanner(plan) },
                            colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync to Planner", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillLevelBadge(level: String) {
    val (bgColor, textColor) = when (level.lowercase()) {
        "expert" -> ScholarGold.copy(alpha = 0.2f) to ScholarGold
        "advanced" -> ScholarGreen.copy(alpha = 0.2f) to ScholarGreen
        "intermediate" -> DarkPrimary.copy(alpha = 0.2f) to DarkPrimary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = level,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(color = textColor, fontWeight = FontWeight.Bold)
        )
    }
}

// ==========================================
// 4. JOB ANALYZER & GAPS TAB (Requirements 4, 5, 12, 13)
// ==========================================
@Composable
fun JobAnalyzerAndGapsTab(
    viewModel: ScholarViewModel,
    parsedJob: ParsedJobDescription?,
    gapReport: CareerGapReport?,
    jobs: List<JobPostingEntity>,
    onAnalyzeText: (String) -> Unit,
    onClearParsed: () -> Unit,
    onAnalyzeGap: (ParsedJobDescription) -> Unit,
    onClearGap: () -> Unit,
    onSaveJob: (ParsedJobDescription) -> Unit,
    onToggleSave: (Long, Boolean) -> Unit,
    onCreatePlanFromMissing: (String) -> Unit
) {
    var rawJobText by remember {
        mutableStateOf(
            """Job Title: Computer Vision / 3D Perception Algorithm Engineer
Company: DeepDrive Robotics Lab
Location: Beijing, China
Required:
• Master's or Bachelor's in Computer Science, Robotics, or AI
• Strong Python and PyTorch proficiency
• Hands-on experience with 3D Point Clouds and LiDAR segmentation
• Linux, Git, and Docker development workflow
Preferred:
• Experience with CUDA kernel optimization and TensorRT
• Publications in CVPR, ICCV, or ECCV
Responsibilities:
• Design real-time point cloud perception architectures
• Benchmark against state-of-the-art baselines on embedded hardware
Salary: ¥30,000 - ¥45,000 / month
Deadline: 2027-05-30"""
        )
    }

    val context = LocalContext.current
    var countryFilter by remember { mutableStateOf("ALL") }
    var remoteFilter by remember { mutableStateOf("ALL") }
    val countries = listOf("ALL", "China", "Singapore", "Japan", "Europe", "United States", "Remote")
    val remoteOptions = listOf("ALL", "On-site", "Hybrid", "Remote")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Job Input & Analyzer Section
        item {
            SectionHeader(
                title = "Job Description Analyzer",
                subtitle = "Paste raw posting or upload description to extract requirements"
            )
        }

        item {
            OutlinedTextField(
                value = rawJobText,
                onValueChange = { rawJobText = it },
                label = { Text("Job Description Raw Text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("job_description_input"),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onAnalyzeText(rawJobText) },
                    modifier = Modifier.weight(1f).testTag("analyze_job_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Analyze Job Posting", fontSize = 12.sp)
                }
                if (parsedJob != null) {
                    OutlinedButton(onClick = onClearParsed) {
                        Text("Clear", fontSize = 12.sp)
                    }
                }
            }
        }

        // Parsed Job Card
        if (parsedJob != null) {
            val pj = parsedJob
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PARSED REQUIREMENTS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                            )
                            Button(
                                onClick = { onSaveJob(pj) },
                                colors = ButtonDefaults.buttonColors(containerColor = ScholarGreen),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Job", fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${pj.jobTitle} • ${pj.company}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Location: ${pj.location} • Degree: ${pj.requiredDegree} • Experience: ${pj.requiredExperience}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        if (pj.salary.isNotBlank()) {
                            Text(
                                text = "Salary: ${pj.salary} • Deadline: ${pj.applicationDeadline}",
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Required Skills:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGreen)
                        )
                        pj.requiredSkills.forEach {
                            Text("• $it (REQUIRED)", style = MaterialTheme.typography.bodySmall)
                        }

                        if (pj.preferredSkills.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Preferred Skills:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                            )
                            pj.preferredSkills.forEach {
                                Text("• $it (PREFERRED)", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onAnalyzeGap(pj) },
                            modifier = Modifier.fillMaxWidth().testTag("compare_career_gap_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                        ) {
                            Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Career Gap Analysis Against My Profile", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Gap Report Section (Requirement 5: Factual gap summary, NO overall score/ranking)
        if (gapReport != null) {
            val gr = gapReport
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "FACTUAL CAREER GAP SUMMARY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                            )
                            IconButton(onClick = onClearGap, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = gr.factualSummary,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = gr.disclaimer,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Matches
                        if (gr.matches.isNotEmpty()) {
                            Text(
                                text = "MATCHES (${gr.matches.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGreen)
                            )
                            gr.matches.forEach { item ->
                                GapItemRow(item = item, onAction = null)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Partial Matches
                        if (gr.partialMatches.isNotEmpty()) {
                            Text(
                                text = "PARTIAL MATCHES (${gr.partialMatches.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                            )
                            gr.partialMatches.forEach { item ->
                                GapItemRow(item = item, onAction = { onCreatePlanFromMissing(item.requirementName) })
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        // Missing
                        if (gr.missing.isNotEmpty()) {
                            Text(
                                text = "MISSING REQUIREMENTS (${gr.missing.size})",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarRed)
                            )
                            gr.missing.forEach { item ->
                                GapItemRow(item = item, onAction = { onCreatePlanFromMissing(item.requirementName) })
                            }
                        }
                    }
                }
            }
        }

        // International Job Search (Requirements 12, 13)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Verified International Job Postings (${jobs.size})",
                subtitle = "Official sources only • Verified dates • No fabricated postings"
            )
        }

        // Country & Remote Filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(countries) { c ->
                        FilterChip(
                            selected = countryFilter == c,
                            onClick = {
                                countryFilter = c
                                viewModel.setJobFilterCountry(c)
                            },
                            label = { Text(c, fontSize = 11.sp) }
                        )
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(remoteOptions) { r ->
                        FilterChip(
                            selected = remoteFilter == r,
                            onClick = {
                                remoteFilter = r
                                viewModel.setJobFilterRemote(r)
                            },
                            label = { Text(r, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Verified Job Cards
        items(jobs) { job ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = job.jobTitle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPrimary),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { onToggleSave(job.id, !job.isSaved) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                if (job.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = if (job.isSaved) ScholarGold else Color.Gray
                            )
                        }
                    }
                    Text(
                        text = "${job.company} • ${job.location} (${job.country}) • ${job.remotePolicy}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Salary: ${job.salary} • Deadline: ${job.applicationDeadline}",
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Required: ${job.requiredSkills}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                    if (job.preferredSkills.isNotBlank()) {
                        Text(
                            text = "Preferred: ${job.preferredSkills}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Source: ${job.source} (Checked: ${job.dateChecked})",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        if (job.applicationUrl.isNotBlank()) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(job.applicationUrl))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("Apply Link", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GapItemRow(item: RequirementGapItem, onAction: (() -> Unit)?) {
    val (statusText, color) = when (item.status) {
        MatchStatus.MATCH -> "MATCH" to ScholarGreen
        MatchStatus.PARTIAL_MATCH -> "PARTIAL MATCH" to ScholarGold
        MatchStatus.MISSING -> "MISSING" to ScholarRed
        MatchStatus.UNKNOWN -> "UNKNOWN" to Color.Gray
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.requirementName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Surface(
                    color = color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(color = color, fontWeight = FontWeight.Bold)
                    )
                }
            }
            Text(
                text = item.explanation,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            if (item.userEvidence.isNotBlank()) {
                Text(
                    text = "Profile Record: ${item.userEvidence}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, color = ScholarCyan)
                )
            }
            if (onAction != null && item.status in listOf(MatchStatus.MISSING, MatchStatus.PARTIAL_MATCH)) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = onAction,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Create Skill Plan", fontSize = 10.sp)
                }
            }
        }
    }
}

// ==========================================
// 5. CV / RESUME ASSISTANT TAB (Requirements 8, 9)
// ==========================================
@Composable
fun CvAssistantTab(
    resumeAnalysis: ResumeAnalysisReport?,
    jobCvMatch: JobSpecificCvMatchReport?,
    parsedJob: ParsedJobDescription?,
    onAnalyzeCv: (String) -> Unit,
    onClearCv: () -> Unit,
    onAnalyzeJobSpecificCv: (String, ParsedJobDescription) -> Unit,
    onClearJobSpecific: () -> Unit
) {
    var cvText by remember {
        mutableStateOf(
            """ALEXEI CHEN-KOVALENKO
Email: alexei.chen@ysu.edu.cn | GitHub: https://github.com/alexei-ysu-cs
Education:
Yanshan University - Bachelor of Engineering in Computer Science (2023 - 2027)
Skills:
Python, PyTorch, C++, Linux, Git, Docker, OpenCV
Projects:
PointFog-SAM Maritime Coastal LiDAR Perception:
• Worked on point cloud semantic segmentation under fog.
• Implemented atmospheric scattering inversion layer in PyTorch.
• Achieved 64.8% mIoU on synthetic fog and tested on NVIDIA Jetson AGX Orin.
xv6 Copy-on-Write Memory Fork:
• Responsible for kernel page table faults and physical page reference counting."""
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "CV & Resume Assistant",
                subtitle = "Factual clarity audit & job alignment (Never invents numbers or accomplishments)"
            )
        }

        item {
            OutlinedTextField(
                value = cvText,
                onValueChange = { cvText = it },
                label = { Text("Paste Resume / CV Plain Text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .testTag("cv_text_input"),
                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onAnalyzeCv(cvText) },
                    modifier = Modifier.weight(1f).testTag("audit_cv_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Audit CV Clarity", fontSize = 12.sp)
                }
                if (parsedJob != null) {
                    Button(
                        onClick = { onAnalyzeJobSpecificCv(cvText, parsedJob) },
                        modifier = Modifier.weight(1f).testTag("compare_cv_job_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan)
                    ) {
                        Text("Compare to Job", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // General CV Audit Report (Requirement 8)
        if (resumeAnalysis != null) {
            val r = resumeAnalysis
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CV STRUCTURE & CLARITY AUDIT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                            )
                            IconButton(onClick = onClearCv, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // Checklist
                        Text(
                            text = "Structure Checklist:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        r.structureChecklist.forEach { (section, present) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (present) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = null,
                                    tint = if (present) ScholarGreen else ScholarRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = section,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (present) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }

                        // Missing Warnings
                        if (r.missingInformationWarnings.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Missing Information Warnings:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarRed)
                            )
                            r.missingInformationWarnings.forEach {
                                Text("• $it", style = MaterialTheme.typography.bodySmall.copy(color = ScholarRed))
                            }
                        }

                        // Weak Descriptions & Replacements
                        if (r.weakDescriptions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Weak Phrasing & Active Verb Recommendations:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                            )
                            r.weakDescriptions.forEach { (original, replacement) ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "Found: \"$original\"",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                        Text(
                                            text = "→ Suggestion: $replacement",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = DarkPrimary)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = r.disclaimer,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Job-Specific CV Analysis Report (Requirement 9)
        if (jobCvMatch != null) {
            val jm = jobCvMatch
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "JOB-SPECIFIC CV ALIGNMENT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                            )
                            IconButton(onClick = onClearJobSpecific, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Relevant Skills Already Present in CV:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGreen)
                        )
                        Text(
                            text = jm.relevantSkillsPresent.joinToString(", ").ifEmpty { "None explicitly matched" },
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Missing Job Terminology:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                        )
                        Text(
                            text = jm.missingTerminology.joinToString(", ").ifEmpty { "All core terms mentioned" },
                            style = MaterialTheme.typography.bodySmall
                        )

                        if (jm.requirementsNotSupportedByCv.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Job Requirements Not Supported by CV:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarRed)
                            )
                            jm.requirementsNotSupportedByCv.forEach {
                                Text("• $it", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        if (jm.areasNeedingClearerExplanation.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Areas Needing Clearer Explanation:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                            )
                            jm.areasNeedingClearerExplanation.forEach {
                                Text("• $it", style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = jm.disclaimer,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. INTERVIEW PREPARATION TAB (Requirements 10, 11)
// ==========================================
@Composable
fun InterviewPrepTab(
    questions: List<InterviewPrepQuestionEntity>,
    activeResearch: ResearchProjectEntity?,
    feedback: InterviewEvaluationResult?,
    onAddQuestion: () -> Unit,
    onDeleteQuestion: (InterviewPrepQuestionEntity) -> Unit,
    onEvaluateAnswer: (String, String, String, String?) -> Unit,
    onClearFeedback: () -> Unit,
    onGenerateFromResearch: () -> Unit,
    onSaveFeedback: (InterviewPrepQuestionEntity, String, String, String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "Technical", "Coding", "Machine Learning", "Computer Vision", "Research", "System Design", "Behavioral", "HR")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Interview Preparation",
                    subtitle = "Project-grounded & algorithmic technical interview practice"
                )
                IconButton(onClick = onAddQuestion) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Question", tint = DarkPrimary)
                }
            }
        }

        // Project Questions Generator Button (Requirement 11)
        if (activeResearch != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Project-Based Technical Questions",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                            )
                            Text(
                                text = "Generate 3 questions grounded in '${activeResearch.title}'",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Button(
                            onClick = onGenerateFromResearch,
                            colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Generate", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Category Filter
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Feedback Card if open
        if (feedback != null) {
            val fb = feedback
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ANSWER EVALUATION FEEDBACK",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                            )
                            IconButton(onClick = onClearFeedback, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Question: ${fb.question}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Concrete Strengths Identified:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGreen)
                        )
                        fb.concreteStrengths.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Concrete Weaknesses Identified:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                        )
                        fb.concreteWeaknesses.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Improved Answer Recommendation:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DarkPrimary)
                        )
                        Text(
                            text = fb.improvedAnswerRecommendation,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = fb.disclaimer,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        // Questions List
        val filteredQuestions = if (selectedCategory == "ALL") questions else questions.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        items(filteredQuestions) { q ->
            InterviewQuestionCard(
                question = q,
                onDelete = { onDeleteQuestion(q) },
                onEvaluate = { answer ->
                    onEvaluateAnswer(q.question, answer, q.category, q.targetRoleOrProject)
                }
            )
        }
    }
}

@Composable
fun InterviewQuestionCard(
    question: InterviewPrepQuestionEntity,
    onDelete: () -> Unit,
    onEvaluate: (String) -> Unit
) {
    var answerText by remember { mutableStateOf(question.userAnswer) }
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = DarkPrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = question.category,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontWeight = FontWeight.Bold)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (question.isPracticed) {
                        Surface(
                            color = ScholarGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Practiced",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarGreen, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            )
            if (question.targetRoleOrProject.isNotBlank()) {
                Text(
                    text = "Context: ${question.targetRoleOrProject}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = answerText,
                onValueChange = { answerText = it },
                label = { Text("Your Answer (STAR approach)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (question.aiFeedback.isNotBlank() || question.improvedAnswer.isNotBlank()) {
                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(if (isExpanded) "Hide Model Answer" else "View Model Answer", fontSize = 11.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                Button(
                    onClick = { onEvaluate(answerText) },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Evaluate", fontSize = 11.sp)
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(6.dp))
                if (question.aiFeedback.isNotBlank()) {
                    Text(
                        text = "Saved Feedback: ${question.aiFeedback}",
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarGreen)
                    )
                }
                if (question.improvedAnswer.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Model Answer / Recommendation: ${question.improvedAnswer}",
                        style = MaterialTheme.typography.bodySmall.copy(color = DarkPrimary)
                    )
                }
            }
        }
    }
}

// ==========================================
// 7. APPLICATIONS & ROADMAP TAB (Requirements 14, 15)
// ==========================================
@Composable
fun ApplicationsAndRoadmapTab(
    applications: List<JobApplicationEntity>,
    milestones: List<CareerRoadmapMilestoneEntity>,
    onAddApplication: () -> Unit,
    onDeleteApplication: (JobApplicationEntity) -> Unit,
    onUpdateAppStatus: (JobApplicationEntity, String) -> Unit,
    onAddMilestone: () -> Unit,
    onToggleMilestone: (Long, Boolean) -> Unit,
    onDeleteMilestone: (CareerRoadmapMilestoneEntity) -> Unit,
    onSyncMilestone: (CareerRoadmapMilestoneEntity) -> Unit
) {
    val statuses = listOf("Saved", "Preparing", "Applied", "Interview", "Technical Round", "Offer", "Rejected", "Withdrawn", "Closed")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Application Tracker Section (Requirement 15)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Job Application Tracker (${applications.size})",
                    subtitle = "Stages: Saved → Applied → Interview → Offer"
                )
                IconButton(onClick = onAddApplication) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Application", tint = DarkPrimary)
                }
            }
        }

        items(applications) { app ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.company,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPrimary)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            StatusBadge(status = app.status)
                            IconButton(onClick = { onDeleteApplication(app) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Text(
                        text = "${app.position} • ${app.location}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Applied: ${app.dateApplied.ifBlank { "Not yet" }} • CV: ${app.cvVersionUsed}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    if (app.interviewDate.isNotBlank()) {
                        Text(
                            text = "Interview Scheduled: ${app.interviewDate}",
                            style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold, fontWeight = FontWeight.Bold)
                        )
                    }
                    if (app.notes.isNotBlank()) {
                        Text(
                            text = "Notes: ${app.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Dropdown / Quick buttons
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(statuses) { st ->
                            FilterChip(
                                selected = app.status == st,
                                onClick = { onUpdateAppStatus(app, st) },
                                label = { Text(st, fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Career Roadmap Section (Requirement 14)
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Career Roadmap (${milestones.count { it.isCompleted }}/${milestones.size} Completed)",
                    subtitle = "Skills → Gaps → Projects → Experience → CV → Interviews → Applications"
                )
                IconButton(onClick = onAddMilestone) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Milestone", tint = DarkPrimary)
                }
            }
        }

        items(milestones) { m ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (m.isCompleted) MaterialTheme.colorScheme.surfaceVariant else ScholarNavySurface
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = m.isCompleted,
                        onCheckedChange = { onToggleMilestone(m.id, it) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = DarkPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = m.stage,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Target: ${m.targetDate}",
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = m.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (m.isCompleted) Color.Gray else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (m.notes.isNotBlank()) {
                            Text(
                                text = m.notes,
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                    IconButton(
                        onClick = { onSyncMilestone(m) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Sync to Planner", tint = ScholarCyan, modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { onDeleteMilestone(m) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "Offer" -> ScholarGreen to status
        "Interview", "Technical Round" -> ScholarGold to status
        "Applied" -> ScholarCyan to status
        "Preparing" -> DarkPrimary to status
        "Rejected" -> ScholarRed to status
        else -> Color.Gray to status
    }
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(color = color, fontWeight = FontWeight.Bold)
        )
    }
}

// ==========================================
// DIALOGS: ADD / EDIT
// ==========================================

@Composable
fun EditCareerProfileDialog(
    profile: CareerProfileEntity,
    onDismiss: () -> Unit,
    onSave: (CareerProfileEntity) -> Unit
) {
    var degree by remember { mutableStateOf(profile.degree) }
    var university by remember { mutableStateOf(profile.university) }
    var department by remember { mutableStateOf(profile.department) }
    var graduationDate by remember { mutableStateOf(profile.graduationDate) }
    var researchAreas by remember { mutableStateOf(profile.researchAreas) }
    var progLangs by remember { mutableStateOf(profile.programmingLanguages) }
    var frameworks by remember { mutableStateOf(profile.frameworks) }
    var aiMlSkills by remember { mutableStateOf(profile.aiMlSkills) }
    var cvSkills by remember { mutableStateOf(profile.cvSkills) }
    var seSkills by remember { mutableStateOf(profile.seSkills) }
    var databases by remember { mutableStateOf(profile.databases) }
    var cloud by remember { mutableStateOf(profile.cloud) }
    var dataScience by remember { mutableStateOf(profile.dataScience) }
    var researchSkills by remember { mutableStateOf(profile.researchSkills) }
    var publications by remember { mutableStateOf(profile.publications) }
    var projects by remember { mutableStateOf(profile.projects) }
    var internships by remember { mutableStateOf(profile.internships) }
    var workExp by remember { mutableStateOf(profile.workExperience) }
    var certifications by remember { mutableStateOf(profile.certifications) }
    var github by remember { mutableStateOf(profile.githubUrl) }
    var portfolio by remember { mutableStateOf(profile.portfolioUrl) }
    var linkedin by remember { mutableStateOf(profile.linkedinUrl) }
    var english by remember { mutableStateOf(profile.englishProficiency) }
    var chinese by remember { mutableStateOf(profile.chineseProficiency) }
    var targetTitles by remember { mutableStateOf(profile.targetJobTitles) }
    var targetCountries by remember { mutableStateOf(profile.targetCountries) }
    var targetCities by remember { mutableStateOf(profile.targetCities) }
    var targetIndustries by remember { mutableStateOf(profile.targetIndustries) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Edit Verifiable Career Profile",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "All 28 verifiable qualifications • Zero invention rule",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item { OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Degree") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = university, onValueChange = { university = it }, label = { Text("University") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = department, onValueChange = { department = it }, label = { Text("Department") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = graduationDate, onValueChange = { graduationDate = it }, label = { Text("Graduation Date") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = researchAreas, onValueChange = { researchAreas = it }, label = { Text("Research Areas") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = progLangs, onValueChange = { progLangs = it }, label = { Text("Programming Languages") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = frameworks, onValueChange = { frameworks = it }, label = { Text("Frameworks") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = aiMlSkills, onValueChange = { aiMlSkills = it }, label = { Text("AI / ML Skills") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = cvSkills, onValueChange = { cvSkills = it }, label = { Text("Computer Vision Skills") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = seSkills, onValueChange = { seSkills = it }, label = { Text("Software Engineering Skills") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = databases, onValueChange = { databases = it }, label = { Text("Databases") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = cloud, onValueChange = { cloud = it }, label = { Text("Cloud / Server Infrastructure") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = dataScience, onValueChange = { dataScience = it }, label = { Text("Data Science & Evaluation Metrics") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = researchSkills, onValueChange = { researchSkills = it }, label = { Text("Research Skills") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = publications, onValueChange = { publications = it }, label = { Text("Publications") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = projects, onValueChange = { projects = it }, label = { Text("Projects") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = internships, onValueChange = { internships = it }, label = { Text("Internships") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = workExp, onValueChange = { workExp = it }, label = { Text("Work Experience") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = certifications, onValueChange = { certifications = it }, label = { Text("Certifications") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = github, onValueChange = { github = it }, label = { Text("GitHub URL") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = portfolio, onValueChange = { portfolio = it }, label = { Text("Portfolio URL") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = linkedin, onValueChange = { linkedin = it }, label = { Text("LinkedIn URL") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = english, onValueChange = { english = it }, label = { Text("English Proficiency") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = chinese, onValueChange = { chinese = it }, label = { Text("Chinese Proficiency") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = targetTitles, onValueChange = { targetTitles = it }, label = { Text("Target Job Titles") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = targetCountries, onValueChange = { targetCountries = it }, label = { Text("Target Countries") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = targetCities, onValueChange = { targetCities = it }, label = { Text("Target Cities") }, modifier = Modifier.fillMaxWidth()) }
                    item { OutlinedTextField(value = targetIndustries, onValueChange = { targetIndustries = it }, label = { Text("Target Industries") }, modifier = Modifier.fillMaxWidth()) }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                profile.copy(
                                    degree = degree,
                                    university = university,
                                    department = department,
                                    graduationDate = graduationDate,
                                    researchAreas = researchAreas,
                                    programmingLanguages = progLangs,
                                    frameworks = frameworks,
                                    aiMlSkills = aiMlSkills,
                                    cvSkills = cvSkills,
                                    seSkills = seSkills,
                                    databases = databases,
                                    cloud = cloud,
                                    dataScience = dataScience,
                                    researchSkills = researchSkills,
                                    publications = publications,
                                    projects = projects,
                                    internships = internships,
                                    workExperience = workExp,
                                    certifications = certifications,
                                    githubUrl = github,
                                    portfolioUrl = portfolio,
                                    linkedinUrl = linkedin,
                                    englishProficiency = english,
                                    chineseProficiency = chinese,
                                    targetJobTitles = targetTitles,
                                    targetCountries = targetCountries,
                                    targetCities = targetCities,
                                    targetIndustries = targetIndustries
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                    ) {
                        Text("Save Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun AddCareerGoalDialog(
    onDismiss: () -> Unit,
    onSave: (CareerGoalEntity) -> Unit
) {
    var role by remember { mutableStateOf("Computer Vision Engineer") }
    var country by remember { mutableStateOf("China") }
    var city by remember { mutableStateOf("Beijing / Shanghai") }
    var industry by remember { mutableStateOf("Autonomous Driving") }
    var expLevel by remember { mutableStateOf("Entry-Level / Master Graduate") }
    var targetDate by remember { mutableStateOf("Spring 2027") }
    var isPrimary by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Target Career Goal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Target Role") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = country, onValueChange = { country = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = industry, onValueChange = { industry = it }, label = { Text("Industry") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = expLevel, onValueChange = { expLevel = it }, label = { Text("Desired Experience Level") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = targetDate, onValueChange = { targetDate = it }, label = { Text("Target Application Date") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPrimary, onCheckedChange = { isPrimary = it })
                    Text("Set as Primary Target Goal", style = MaterialTheme.typography.bodySmall)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (role.isNotBlank()) {
                                onSave(
                                    CareerGoalEntity(
                                        targetRole = role,
                                        country = country,
                                        city = city,
                                        industry = industry,
                                        desiredExperienceLevel = expLevel,
                                        targetApplicationDate = targetDate,
                                        isPrimary = isPrimary
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Add Goal")
                    }
                }
            }
        }
    }
}

@Composable
fun AddSkillInventoryDialog(
    onDismiss: () -> Unit,
    onSave: (SkillInventoryEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("AI/ML") }
    var level by remember { mutableStateOf("Intermediate") }
    var evidence by remember { mutableStateOf("") }
    var lastPracticed by remember { mutableStateOf("2026-10-01") }
    var project by remember { mutableStateOf("PointFog-SAM") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Verifiable Skill to Inventory", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Skill Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Languages, AI/ML, CV, Tools)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("Level (Beginner, Intermediate, Advanced, Expert)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = evidence, onValueChange = { evidence = it }, label = { Text("Documented Evidence") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = project, onValueChange = { project = it }, label = { Text("Related Project") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = lastPracticed, onValueChange = { lastPracticed = it }, label = { Text("Last Practiced Date") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(
                                    SkillInventoryEntity(
                                        skillName = name,
                                        category = category,
                                        currentLevel = level,
                                        evidence = evidence.ifBlank { "Explicit user confirmed profile input" },
                                        lastPracticed = lastPracticed,
                                        relatedProject = project
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Add Skill")
                    }
                }
            }
        }
    }
}

@Composable
fun AddSkillPlanDialog(
    onDismiss: () -> Unit,
    onSave: (SkillDevelopmentPlanEntity) -> Unit
) {
    var skill by remember { mutableStateOf("") }
    var currentLevel by remember { mutableStateOf("Beginner") }
    var targetLevel by remember { mutableStateOf("Advanced") }
    var why by remember { mutableStateOf("Crucial for autonomous driving edge perception") }
    var task by remember { mutableStateOf("") }
    var project by remember { mutableStateOf("PointFog-SAM") }
    var resources by remember { mutableStateOf("Official documentation & GitHub examples") }
    var date by remember { mutableStateOf("2026-12-15") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Create Skill Development Plan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                OutlinedTextField(value = skill, onValueChange = { skill = it }, label = { Text("Skill Name") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = currentLevel, onValueChange = { currentLevel = it }, label = { Text("Current Level") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = targetLevel, onValueChange = { targetLevel = it }, label = { Text("Target Level") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = why, onValueChange = { why = it }, label = { Text("Why It Matters") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = task, onValueChange = { task = it }, label = { Text("Practice Task") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = project, onValueChange = { project = it }, label = { Text("Suggested Project") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = resources, onValueChange = { resources = it }, label = { Text("Learning Resources") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Target Date") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (skill.isNotBlank()) {
                                onSave(
                                    SkillDevelopmentPlanEntity(
                                        skillName = skill,
                                        currentLevel = currentLevel,
                                        targetLevel = targetLevel,
                                        whyItMatters = why,
                                        learningResources = resources,
                                        practiceTask = task,
                                        suggestedProject = project,
                                        targetDate = date,
                                        status = "In Progress"
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Create Plan")
                    }
                }
            }
        }
    }
}

@Composable
fun AddJobApplicationDialog(
    onDismiss: () -> Unit,
    onSave: (JobApplicationEntity) -> Unit
) {
    var company by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("Computer Vision Engineer") }
    var location by remember { mutableStateOf("Beijing") }
    var status by remember { mutableStateOf("Preparing") }
    var url by remember { mutableStateOf("") }
    var dateApplied by remember { mutableStateOf("2026-10-02") }
    var interviewDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Track Job Application", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = position, onValueChange = { position = it }, label = { Text("Position Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status (Preparing, Applied, Interview, Offer)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Application URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dateApplied, onValueChange = { dateApplied = it }, label = { Text("Date Applied") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = interviewDate, onValueChange = { interviewDate = it }, label = { Text("Interview Date (if scheduled)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes / Next Steps") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (company.isNotBlank()) {
                                onSave(
                                    JobApplicationEntity(
                                        company = company,
                                        position = position,
                                        location = location,
                                        applicationUrl = url,
                                        dateApplied = dateApplied,
                                        status = status,
                                        interviewDate = interviewDate,
                                        notes = notes
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Save Application")
                    }
                }
            }
        }
    }
}

@Composable
fun AddInterviewQuestionDialog(
    onDismiss: () -> Unit,
    onSave: (InterviewPrepQuestionEntity) -> Unit
) {
    var category by remember { mutableStateOf("Technical") }
    var project by remember { mutableStateOf("PointFog-SAM") }
    var question by remember { mutableStateOf("") }
    var modelAns by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Interview Practice Question", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Technical, Coding, ML, CV, Research, HR)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = project, onValueChange = { project = it }, label = { Text("Related Role or Project Context") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = question, onValueChange = { question = it }, label = { Text("Question Text") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = modelAns, onValueChange = { modelAns = it }, label = { Text("Key Technical Points to Include") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (question.isNotBlank()) {
                                onSave(
                                    InterviewPrepQuestionEntity(
                                        category = category,
                                        targetRoleOrProject = project,
                                        question = question,
                                        improvedAnswer = modelAns
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Add Question")
                    }
                }
            }
        }
    }
}

@Composable
fun AddRoadmapMilestoneDialog(
    onDismiss: () -> Unit,
    onSave: (CareerRoadmapMilestoneEntity) -> Unit
) {
    var stage by remember { mutableStateOf("Skills") }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("2026-12-01") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add Career Roadmap Milestone", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                OutlinedTextField(value = stage, onValueChange = { stage = it }, label = { Text("Stage (Skills, Gaps, Projects, Experience, CV, Interviews, Applications)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Milestone Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Target Date") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes / Success Criteria") }, modifier = Modifier.fillMaxWidth())

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSave(
                                    CareerRoadmapMilestoneEntity(
                                        stage = stage,
                                        title = title,
                                        targetDate = date,
                                        notes = notes
                                    )
                                )
                            }
                        }
                    ) {
                        Text("Add Milestone")
                    }
                }
            }
        }
    }
}
