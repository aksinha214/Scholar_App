package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.*
import com.example.data.model.*
import com.example.ui.components.FactBadge
import com.example.ui.components.ModuleTabBar
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun ResearchLabScreen(viewModel: ScholarViewModel) {
    val projects by viewModel.researchProjects.collectAsStateWithLifecycle()
    val activeProject by viewModel.activeResearchProject.collectAsStateWithLifecycle()
    val milestones by viewModel.projectMilestones.collectAsStateWithLifecycle()
    val papers by viewModel.projectPapers.collectAsStateWithLifecycle()
    val experiments by viewModel.projectExperiments.collectAsStateWithLifecycle()
    val manuscriptSections by viewModel.manuscriptSections.collectAsStateWithLifecycle()
    val reproducibilityItems by viewModel.reproducibilityItems.collectAsStateWithLifecycle()
    val activePaperAnalysis by viewModel.activePaperAnalysis.collectAsStateWithLifecycle()
    val synthesizedGaps by viewModel.synthesizedGaps.collectAsStateWithLifecycle()
    val advisorBrief by viewModel.advisorBrief.collectAsStateWithLifecycle()
    val userDocs by viewModel.userDocuments.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Overview & Projects",
        "Milestones & Tasks",
        "Paper Library",
        "Literature Matrix",
        "Experiments & Loss",
        "Manuscript & Brief"
    )

    // Dialog states
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showEditProjectDialog by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<ResearchProjectEntity?>(null) }
    var experimentToDelete by remember { mutableStateOf<ResearchExperimentEntity?>(null) }
    var showAddMilestoneDialog by remember { mutableStateOf(false) }
    var showAddPaperDialog by remember { mutableStateOf(false) }
    var showAddExperimentDialog by remember { mutableStateOf(false) }
    var showAnalyzeDocDialog by remember { mutableStateOf(false) }
    var showBibtexExportDialog by remember { mutableStateOf(false) }
    var showAdvisorBriefDialog by remember { mutableStateOf(false) }
    var selectedPaperForDetail by remember { mutableStateOf<ResearchPaperEntity?>(null) }
    var selectedExperimentForDetail by remember { mutableStateOf<ResearchExperimentEntity?>(null) }
    var selectedSectionForEdit by remember { mutableStateOf<ManuscriptSectionEntity?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            when (selectedTab) {
                0 -> {
                    FloatingActionButton(
                        onClick = { showNewProjectDialog = true },
                        containerColor = DarkPrimary,
                        contentColor = DarkOnPrimary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Create Project")
                    }
                }
                1 -> {
                    FloatingActionButton(
                        onClick = { showAddMilestoneDialog = true },
                        containerColor = ScholarCyan,
                        contentColor = Color.Black
                    ) {
                        Icon(Icons.Default.AddTask, contentDescription = "Add Milestone")
                    }
                }
                2 -> {
                    ExtendedFloatingActionButton(
                        onClick = { showAnalyzeDocDialog = true },
                        containerColor = DarkPrimary,
                        contentColor = DarkOnPrimary,
                        icon = { Icon(Icons.Default.DocumentScanner, contentDescription = null) },
                        text = { Text("Analyze Paper", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }
                4 -> {
                    FloatingActionButton(
                        onClick = { showAddExperimentDialog = true },
                        containerColor = ScholarGreen,
                        contentColor = Color.Black
                    ) {
                        Icon(Icons.Default.Science, contentDescription = "Log Experiment")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Unified Tab Row for all 6 tabs
            ModuleTabBar(
                tabs = tabs,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Tab Content
            when (selectedTab) {
                0 -> ProjectsOverviewView(
                    projects = projects,
                    activeProject = activeProject,
                    milestones = milestones,
                    papers = papers,
                    experiments = experiments,
                    onSelectProject = { viewModel.selectResearchProject(it) },
                    onToggleActive = { id, act -> viewModel.toggleProjectActive(id, act) },
                    onEditProject = { showEditProjectDialog = true },
                    onDeleteProject = { projectToDelete = it },
                    onNewProject = { showNewProjectDialog = true }
                )
                1 -> MilestonesView(
                    milestones = milestones,
                    project = activeProject,
                    onToggle = { id, comp -> viewModel.toggleMilestone(id, comp) },
                    onSyncToPlanner = { m ->
                        activeProject?.let { p ->
                            viewModel.syncMilestoneToAcademicPlanner(m, p.title)
                        }
                    },
                    onDelete = { viewModel.deleteMilestone(it) },
                    onAddMilestone = { showAddMilestoneDialog = true }
                )
                2 -> PapersLibraryView(
                    papers = papers,
                    project = activeProject,
                    onSelectPaper = { selectedPaperForDetail = it },
                    onAnalyzeNew = { showAnalyzeDocDialog = true },
                    onAddManual = { showAddPaperDialog = true },
                    onDeletePaper = { viewModel.deleteResearchPaper(it) }
                )
                3 -> LiteratureMatrixView(
                    papers = papers,
                    project = activeProject,
                    onSelectPaper = { selectedPaperForDetail = it },
                    onSynthesizeGaps = { viewModel.synthesizeResearchGapsForProject() }
                )
                4 -> ExperimentsTrackingView(
                    experiments = experiments,
                    project = activeProject,
                    onSelectExperiment = { selectedExperimentForDetail = it },
                    onAddExperiment = { showAddExperimentDialog = true },
                    onDeleteExperiment = { experimentToDelete = it }
                )
                5 -> ManuscriptAndBriefView(
                    project = activeProject,
                    manuscriptSections = manuscriptSections,
                    reproducibilityItems = reproducibilityItems,
                    onEditSection = { selectedSectionForEdit = it },
                    onToggleReproducibility = { id, comp -> viewModel.toggleReproducibilityItem(id, comp) },
                    onGenerateAdvisorBrief = { showAdvisorBriefDialog = true },
                    onExportBibTeX = { showBibtexExportDialog = true }
                )
            }
        }
    }

    // --- MODALS & DIALOGS ---

    // 1. Create Research Project Dialog
    if (showNewProjectDialog) {
        CreateResearchProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onConfirm = { project ->
                viewModel.insertResearchProject(project)
                showNewProjectDialog = false
            }
        )
    }

    // 2. Edit Research Project Dialog
    if (showEditProjectDialog && activeProject != null) {
        EditResearchProjectDialog(
            project = activeProject!!,
            onDismiss = { showEditProjectDialog = false },
            onConfirm = { updated ->
                viewModel.updateResearchProject(updated)
                showEditProjectDialog = false
            }
        )
    }

    // 3. Add Research Milestone Dialog
    if (showAddMilestoneDialog && activeProject != null) {
        AddMilestoneDialog(
            projectId = activeProject!!.id,
            onDismiss = { showAddMilestoneDialog = false },
            onConfirm = { milestone ->
                viewModel.insertMilestone(milestone)
                showAddMilestoneDialog = false
            }
        )
    }

    // 4. Analyze Paper from PKB / Document text
    if (showAnalyzeDocDialog) {
        AnalyzePaperDialog(
            userDocs = userDocs,
            onDismiss = { showAnalyzeDocDialog = false },
            onAnalyzeText = { text, name ->
                viewModel.analyzePaperFromText(text, name)
                showAnalyzeDocDialog = false
            }
        )
    }

    // 5. Active Paper Analysis Result Screen / Modal
    if (activePaperAnalysis != null && activeProject != null) {
        PaperAnalysisResultDialog(
            analysis = activePaperAnalysis!!,
            project = activeProject!!,
            onDismiss = { viewModel.dismissPaperAnalysis() },
            onSave = {
                viewModel.saveAnalyzedPaperToProject(activePaperAnalysis!!, activeProject!!.id)
            }
        )
    }

    // 6. Paper Detail & BibTeX Modal
    if (selectedPaperForDetail != null) {
        PaperDetailDialog(
            paper = selectedPaperForDetail!!,
            onDismiss = { selectedPaperForDetail = null },
            onUpdate = { updated ->
                viewModel.updateResearchPaper(updated)
                selectedPaperForDetail = null
            }
        )
    }

    // 7. Add Manual Paper Dialog
    if (showAddPaperDialog && activeProject != null) {
        AddManualPaperDialog(
            projectId = activeProject!!.id,
            onDismiss = { showAddPaperDialog = false },
            onConfirm = { paper ->
                viewModel.insertResearchPaper(paper)
                showAddPaperDialog = false
            }
        )
    }

    // 8. Add Experiment Dialog
    if (showAddExperimentDialog && activeProject != null) {
        AddExperimentDialog(
            projectId = activeProject!!.id,
            existingCount = experiments.size,
            onDismiss = { showAddExperimentDialog = false },
            onConfirm = { exp ->
                viewModel.insertResearchExperiment(exp)
                showAddExperimentDialog = false
            }
        )
    }

    // 9. Experiment Detail Dialog
    if (selectedExperimentForDetail != null) {
        ExperimentDetailDialog(
            experiment = selectedExperimentForDetail!!,
            onDismiss = { selectedExperimentForDetail = null },
            onUpdate = { updated ->
                viewModel.updateResearchExperiment(updated)
                selectedExperimentForDetail = null
            }
        )
    }

    // 10. Synthesized Gaps Modal
    if (synthesizedGaps != null) {
        SynthesizedGapsDialog(
            gaps = synthesizedGaps!!,
            onDismiss = { viewModel.dismissSynthesizedGaps() }
        )
    }

    // 11. Edit Manuscript Section
    if (selectedSectionForEdit != null) {
        EditManuscriptSectionDialog(
            section = selectedSectionForEdit!!,
            onDismiss = { selectedSectionForEdit = null },
            onSave = { updated ->
                viewModel.updateManuscriptSection(updated)
                selectedSectionForEdit = null
            }
        )
    }

    // 12. Advisor Brief Dialog
    if (showAdvisorBriefDialog) {
        AdvisorBriefGeneratorDialog(
            brief = advisorBrief,
            onGenerate = { notes -> viewModel.generateAdvisorBrief(notes) },
            onDismiss = {
                viewModel.dismissAdvisorBrief()
                showAdvisorBriefDialog = false
            }
        )
    }

    // 13. Project BibTeX Export Dialog
    if (showBibtexExportDialog) {
        BibTeXExportDialog(
            bibtexContent = viewModel.exportProjectBibTeX(),
            onDismiss = { showBibtexExportDialog = false }
        )
    }

    if (projectToDelete != null) {
        com.example.ui.components.DeleteConfirmationDialog(
            title = "Delete Research Project",
            message = "Are you sure you want to delete '${projectToDelete!!.title}'? This will remove all associated lab notes and matrix entries.",
            onConfirm = {
                viewModel.deleteResearchProject(projectToDelete!!)
                projectToDelete = null
            },
            onDismiss = { projectToDelete = null }
        )
    }

    if (experimentToDelete != null) {
        com.example.ui.components.DeleteConfirmationDialog(
            title = "Delete Experiment Record",
            message = "Are you sure you want to remove experiment '${experimentToDelete!!.experimentId}: ${experimentToDelete!!.name}'?",
            onConfirm = {
                viewModel.deleteResearchExperiment(experimentToDelete!!)
                experimentToDelete = null
            },
            onDismiss = { experimentToDelete = null }
        )
    }
}

// ==============================================================================
// 1. OVERVIEW & PROJECTS VIEW
// ==============================================================================
@Composable
fun ProjectsOverviewView(
    projects: List<ResearchProjectEntity>,
    activeProject: ResearchProjectEntity?,
    milestones: List<ResearchMilestoneEntity>,
    papers: List<ResearchPaperEntity>,
    experiments: List<ResearchExperimentEntity>,
    onSelectProject: (Long) -> Unit,
    onToggleActive: (Long, Boolean) -> Unit,
    onEditProject: () -> Unit,
    onDeleteProject: (ResearchProjectEntity) -> Unit,
    onNewProject: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            SectionHeader(
                title = "Research Lab Portfolio",
                subtitle = "Active investigations, publications & lab commitments",
                actionText = "New Project",
                onActionClick = onNewProject
            )
        }

        // Active Project Overview Card
        if (activeProject != null) {
            item {
                ActiveProjectDashboardCard(
                    project = activeProject,
                    milestones = milestones,
                    paperCount = papers.size,
                    experimentCount = experiments.size,
                    onEdit = onEditProject
                )
            }
        }

        // Project Switcher Header
        item {
            Text(
                text = "All Research Projects (${projects.size})",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Project Cards
        items(projects) { project ->
            val isSelected = project.id == activeProject?.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectProject(project.id) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, ScholarCyan) else null
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (project.isActive) {
                                Surface(
                                    color = ScholarGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = ScholarGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Surface(
                                color = ScholarGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = project.currentStatus,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ScholarGold,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onToggleActive(project.id, !project.isActive) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (project.isActive) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "Toggle Active",
                                    tint = if (project.isActive) ScholarGold else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            IconButton(
                                onClick = { onDeleteProject(project) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Delete",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Area: ${project.researchArea} • Target: ${project.targetVenue}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    if (project.researchProblem.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Problem: ${project.researchProblem}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ActiveProjectDashboardCard(
    project: ResearchProjectEntity,
    milestones: List<ResearchMilestoneEntity>,
    paperCount: Int,
    experimentCount: Int,
    onEdit: () -> Unit
) {
    val pendingMilestone = milestones.firstOrNull { !it.isCompleted }
    val completedMilestonesCount = milestones.count { it.isCompleted }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FactBadge(type = "CURRENT PROJECT")
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0x3300D2FF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = project.currentStatus.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ScholarCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Project",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Target Venue: ${project.targetVenue} • Manuscript: ${project.manuscriptStatus}",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Project Completion Progress",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                )
                Text(
                    text = "${project.progressPercent}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ScholarCyan,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { project.progressPercent / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = ScholarCyan,
                trackColor = Color(0x33FFFFFF)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Stats Counters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ResearchStatBox(
                    label = "Papers",
                    value = "$paperCount",
                    color = ScholarCyan,
                    modifier = Modifier.weight(1f)
                )
                ResearchStatBox(
                    label = "Experiments",
                    value = "$experimentCount",
                    color = ScholarGreen,
                    modifier = Modifier.weight(1f)
                )
                ResearchStatBox(
                    label = "Milestones",
                    value = "$completedMilestonesCount/${milestones.size}",
                    color = ScholarGold,
                    modifier = Modifier.weight(1f)
                )
                ResearchStatBox(
                    label = "Stage",
                    value = project.currentStatus.take(8),
                    color = DarkPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            // Next Milestone banner
            if (pendingMilestone != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0x22FFFFFF),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = null,
                            tint = ScholarCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Next Milestone: ${pendingMilestone.title}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Deadline: ${pendingMilestone.deadline}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ScholarCyan
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResearchStatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0x1AFFFFFF),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            )
        }
    }
}

// ==============================================================================
// 2. MILESTONES & PLANNER VIEW
// ==============================================================================
@Composable
fun MilestonesView(
    milestones: List<ResearchMilestoneEntity>,
    project: ResearchProjectEntity?,
    onToggle: (Long, Boolean) -> Unit,
    onSyncToPlanner: (ResearchMilestoneEntity) -> Unit,
    onDelete: (ResearchMilestoneEntity) -> Unit,
    onAddMilestone: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Research Milestones Roadmap",
                subtitle = "From literature review to CVPR / TPAMI submission",
                actionText = "Add Milestone",
                onActionClick = onAddMilestone
            )
        }

        if (milestones.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Flag, contentDescription = null, tint = ScholarCyan, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No milestones yet for this project", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(onClick = onAddMilestone) { Text("Create First Milestone") }
                    }
                }
            }
        }

        items(milestones) { milestone ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (milestone.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = milestone.isCompleted,
                            onCheckedChange = { onToggle(milestone.id, it) }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = milestone.title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (milestone.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Target: ${milestone.deadline}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (milestone.isCompleted) ScholarGreen else ScholarCyan
                                    )
                                )
                                Text(
                                    text = "• Status: ${milestone.status}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                        IconButton(onClick = { onDelete(milestone) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                        }
                    }

                    if (milestone.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = milestone.notes,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(start = 44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 44.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { onSyncToPlanner(milestone) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync to Academic Tasks", fontSize = 10.5.sp)
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// ==============================================================================
// 3. PAPER LIBRARY VIEW
// ==============================================================================
@Composable
fun PapersLibraryView(
    papers: List<ResearchPaperEntity>,
    project: ResearchProjectEntity?,
    onSelectPaper: (ResearchPaperEntity) -> Unit,
    onAnalyzeNew: () -> Unit,
    onAddManual: () -> Unit,
    onDeletePaper: (ResearchPaperEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Research Paper Library",
                subtitle = "Integrated with Personal Knowledge Base",
                actionText = "+ Add Manual",
                onActionClick = onAddManual
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { onAnalyzeNew() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = ScholarCyan, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Analyze Paper from Knowledge Base / Text",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "Extracts problem, method, dataset, metrics & generates BibTeX with zero invented facts.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }
        }

        item {
            Text(
                text = "Associated Papers (${papers.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(papers) { paper ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelectPaper(paper) },
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
                            color = ScholarCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "${paper.venue} (${paper.year})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = ScholarCyan
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(onClick = { onDeletePaper(paper) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = paper.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Authors: ${paper.authors}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("Method", fontSize = 9.sp, color = DarkPrimary, fontWeight = FontWeight.Bold)
                                Text(paper.method, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Text("Dataset", fontSize = 9.sp, color = ScholarGold, fontWeight = FontWeight.Bold)
                                Text(paper.dataset, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap for full structured analysis & BibTeX →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ScholarCyan,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// ==============================================================================
// 4. LITERATURE MATRIX VIEW
// ==============================================================================
@Composable
fun LiteratureMatrixView(
    papers: List<ResearchPaperEntity>,
    project: ResearchProjectEntity?,
    onSelectPaper: (ResearchPaperEntity) -> Unit,
    onSynthesizeGaps: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var sortByYearDesc by remember { mutableStateOf(true) }

    val filteredPapers = remember(papers, searchQuery, sortByYearDesc) {
        val list = if (searchQuery.isBlank()) papers
        else papers.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.method.contains(searchQuery, ignoreCase = true) ||
                    it.dataset.contains(searchQuery, ignoreCase = true) ||
                    it.venue.contains(searchQuery, ignoreCase = true)
        }
        if (sortByYearDesc) list.sortedByDescending { it.year }
        else list.sortedBy { it.year }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Literature Review Matrix",
                subtitle = "Multi-paper empirical comparison table",
                actionText = "Synthesize Gaps",
                onActionClick = onSynthesizeGaps
            )
        }

        // Search & Filter controls
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter matrix by keyword...", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                OutlinedButton(
                    onClick = { sortByYearDesc = !sortByYearDesc },
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(
                        if (sortByYearDesc) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (sortByYearDesc) "Year ↓" else "Year ↑", fontSize = 11.sp)
                }
            }
        }

        // Mobile Friendly Literature Matrix Horizontal Scroller Table
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(12.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MatrixHeaderCell("Paper Title", 160.dp)
                        MatrixHeaderCell("Year", 60.dp)
                        MatrixHeaderCell("Problem", 140.dp)
                        MatrixHeaderCell("Method", 130.dp)
                        MatrixHeaderCell("Dataset", 110.dp)
                        MatrixHeaderCell("Metrics", 90.dp)
                        MatrixHeaderCell("Main Result", 140.dp)
                        MatrixHeaderCell("Limitation", 140.dp)
                        MatrixHeaderCell("Research Gap", 140.dp)
                        MatrixHeaderCell("Relevance", 130.dp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    filteredPapers.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectPaper(p) }
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MatrixTextCell(p.title, 160.dp, isBold = true)
                            MatrixTextCell(p.year, 60.dp)
                            MatrixTextCell(p.researchProblem, 140.dp)
                            MatrixTextCell(p.method, 130.dp)
                            MatrixTextCell(p.dataset, 110.dp)
                            MatrixTextCell(p.evaluationMetrics, 90.dp)
                            MatrixTextCell(p.mainFindings, 140.dp)
                            MatrixTextCell(p.limitations, 140.dp)
                            MatrixTextCell(p.researchGapContribution.ifBlank { p.limitations }, 140.dp)
                            MatrixTextCell(p.relevanceToProject, 130.dp)
                        }
                        HorizontalDivider(color = Color(0x1AFFFFFF))
                    }
                }
            }
        }

        item {
            Button(
                onClick = onSynthesizeGaps,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan, contentColor = Color.Black)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesize Cross-Paper Research Gap", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun MatrixHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = ScholarCyan,
            fontSize = 10.5.sp
        ),
        modifier = Modifier.width(width).padding(horizontal = 4.dp)
    )
}

@Composable
fun MatrixTextCell(text: String, width: androidx.compose.ui.unit.Dp, isBold: Boolean = false) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.SemiBold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        ),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.width(width).padding(horizontal = 4.dp)
    )
}

// ==============================================================================
// 5. EXPERIMENTS & COMPUTATIONAL TRACKING
// ==============================================================================
@Composable
fun ExperimentsTrackingView(
    experiments: List<ResearchExperimentEntity>,
    project: ResearchProjectEntity?,
    onSelectExperiment: (ResearchExperimentEntity) -> Unit,
    onAddExperiment: () -> Unit,
    onDeleteExperiment: (ResearchExperimentEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Computational Experiment Tracking",
                subtitle = "Empirical benchmark metrics, losses & ablation runs",
                actionText = "Log Run",
                onActionClick = onAddExperiment
            )
        }

        items(experiments) { exp ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelectExperiment(exp) },
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
                            Surface(
                                color = ScholarGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = exp.experimentId,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ScholarGreen,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = exp.date,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        IconButton(onClick = { onDeleteExperiment(exp) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = exp.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Model: ${exp.model} • Baseline: ${exp.baseline}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Quantitative Results:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ScholarCyan)
                                if (!exp.customMetricValue.isNullOrBlank()) {
                                    Text(
                                        text = "${exp.customMetricName}: ${exp.customMetricValue}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ScholarGreen
                                    )
                                }
                            }
                            Text(
                                text = exp.results,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    if (exp.failureAnalysis.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Failure Analysis: ${exp.failureAnalysis}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = ScholarGold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hardware: ${exp.hardware}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = "Tap for full metrics & loss →",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = ScholarCyan
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// ==============================================================================
// 6. MANUSCRIPT & ADVISOR VIEW
// ==============================================================================
@Composable
fun ManuscriptAndBriefView(
    project: ResearchProjectEntity?,
    manuscriptSections: List<ManuscriptSectionEntity>,
    reproducibilityItems: List<ReproducibilityItemEntity>,
    onEditSection: (ManuscriptSectionEntity) -> Unit,
    onToggleReproducibility: (Long, Boolean) -> Unit,
    onGenerateAdvisorBrief: () -> Unit,
    onExportBibTeX: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Manuscript & Meeting Prep",
                subtitle = "Scientific writing, bibliography & advisor updates"
            )
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onGenerateAdvisorBrief,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Advisor Brief", fontSize = 12.sp)
                }

                Button(
                    onClick = onExportBibTeX,
                    colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan, contentColor = Color.Black),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export BibTeX", fontSize = 12.sp)
                }
            }
        }

        // Canonical 10 Manuscript Sections
        item {
            Text(
                text = "Manuscript Sections (${manuscriptSections.size}/10)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(manuscriptSections) { sec ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onEditSection(sec) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sec.sectionName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Words: ${sec.wordCount} / ${sec.targetWordCount} target",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Surface(
                        color = when (sec.status) {
                            "Completed" -> ScholarGreen.copy(alpha = 0.2f)
                            "Review" -> ScholarGold.copy(alpha = 0.2f)
                            "Drafting" -> ScholarCyan.copy(alpha = 0.2f)
                            else -> Color.Gray.copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = sec.status,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = when (sec.status) {
                                    "Completed" -> ScholarGreen
                                    "Review" -> ScholarGold
                                    "Drafting" -> ScholarCyan
                                    else -> Color.Gray
                                },
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // Open Science / Reproducibility Checklist
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader(
                title = "Reproducibility Checklist",
                subtitle = "Artifact badging criteria for IEEE & ACM"
            )
        }

        items(reproducibilityItems) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = { onToggleReproducibility(item.id, it) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(item.title, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold))
                        if (item.details.isNotBlank()) {
                            Text(item.details, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// ==============================================================================
// DIALOGS & INTERACTIVE MODALS
// ==============================================================================

@Composable
fun CreateResearchProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (ResearchProjectEntity) -> Unit
) {
    var title by remember { mutableStateOf("Autonomous Perception in Coastal Fog") }
    var area by remember { mutableStateOf("Computer Vision & 3D LiDAR") }
    var description by remember { mutableStateOf("Physics-informed point cloud perception in northern Chinese ports.") }
    var problem by remember { mutableStateOf("Backscatter noise causing 30%+ mIoU drop.") }
    var questions by remember { mutableStateOf("How to model depth-dependent attenuation?") }
    var objectives by remember { mutableStateOf("Construct port benchmark and deploy on Jetson Orin.") }
    var gap by remember { mutableStateOf("Existing models assume clear weather.") }
    var methodology by remember { mutableStateOf("Koschmieder scattering inversion layer + SAM attention.") }
    var dataset by remember { mutableStateOf("SemanticKITTI + Qinhuangdao Port LiDAR") }
    var models by remember { mutableStateOf("PointFog-SAM, PointNeXt") }
    var metrics by remember { mutableStateOf("mIoU, Precision, Recall, FPS") }
    var baselines by remember { mutableStateOf("PointNeXt, SphereFormer, Cylinder3D") }
    var status by remember { mutableStateOf("Implementation") }
    var venue by remember { mutableStateOf("CVPR 2027") }
    var notes by remember { mutableStateOf("Supervised by Prof. Zhang Lin") }

    val statusOptions = listOf(
        "Idea", "Planning", "Literature Review", "Implementation",
        "Experiments", "Analysis", "Writing", "Submitted", "Revision", "Completed"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Research Project", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Project Title *") })
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Research Area") })
                OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Target Publication Venue") })

                Text("Status:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(statusOptions) { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(value = problem, onValueChange = { problem = it }, label = { Text("Research Problem") })
                OutlinedTextField(value = questions, onValueChange = { questions = it }, label = { Text("Research Questions") })
                OutlinedTextField(value = gap, onValueChange = { gap = it }, label = { Text("Research Gap") })
                OutlinedTextField(value = methodology, onValueChange = { methodology = it }, label = { Text("Methodology") })
                OutlinedTextField(value = dataset, onValueChange = { dataset = it }, label = { Text("Dataset") })
                OutlinedTextField(value = models, onValueChange = { models = it }, label = { Text("Models / Algorithms") })
                OutlinedTextField(value = baselines, onValueChange = { baselines = it }, label = { Text("Baselines") })
                OutlinedTextField(value = metrics, onValueChange = { metrics = it }, label = { Text("Evaluation Metrics") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notes") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            ResearchProjectEntity(
                                title = title,
                                researchArea = area,
                                description = description,
                                researchProblem = problem,
                                researchQuestion = questions,
                                objectives = objectives,
                                researchGap = gap,
                                methodology = methodology,
                                datasets = dataset,
                                modelsAlgorithms = models,
                                metrics = metrics,
                                baselines = baselines,
                                currentStatus = status,
                                targetVenue = venue,
                                targetPublication = venue,
                                notes = notes,
                                isActive = true
                            )
                        )
                    }
                }
            ) { Text("Create Project") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditResearchProjectDialog(
    project: ResearchProjectEntity,
    onDismiss: () -> Unit,
    onConfirm: (ResearchProjectEntity) -> Unit
) {
    var title by remember { mutableStateOf(project.title) }
    var area by remember { mutableStateOf(project.researchArea) }
    var problem by remember { mutableStateOf(project.researchProblem) }
    var gap by remember { mutableStateOf(project.researchGap) }
    var status by remember { mutableStateOf(project.currentStatus) }
    var venue by remember { mutableStateOf(project.targetVenue) }
    var progress by remember { mutableStateOf(project.progressPercent.toString()) }

    val statusOptions = listOf(
        "Idea", "Planning", "Literature Review", "Implementation",
        "Experiments", "Analysis", "Writing", "Submitted", "Revision", "Completed"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Research Project", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Research Area") })
                OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Target Venue") })
                OutlinedTextField(value = progress, onValueChange = { progress = it }, label = { Text("Progress % (0-100)") })

                Text("Current Status:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(statusOptions) { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(value = problem, onValueChange = { problem = it }, label = { Text("Problem") })
                OutlinedTextField(value = gap, onValueChange = { gap = it }, label = { Text("Identified Gap") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val pInt = progress.toIntOrNull()?.coerceIn(0, 100) ?: project.progressPercent
                    onConfirm(
                        project.copy(
                            title = title,
                            researchArea = area,
                            researchProblem = problem,
                            researchGap = gap,
                            currentStatus = status,
                            targetVenue = venue,
                            targetPublication = venue,
                            progressPercent = pInt,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                }
            ) { Text("Save Changes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddMilestoneDialog(
    projectId: Long,
    onDismiss: () -> Unit,
    onConfirm: (ResearchMilestoneEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("In 2 Weeks") }
    var notes by remember { mutableStateOf("") }

    val presetMilestones = listOf(
        "Literature review completed",
        "Dataset prepared & calibrated",
        "Baseline implemented & verified",
        "Experiments & ablations completed",
        "Analysis & tables completed",
        "First manuscript draft",
        "Internal supervisor review",
        "Final camera-ready submission"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Research Milestone", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Select standard milestone or enter custom:", fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(presetMilestones) { preset ->
                        FilterChip(
                            selected = title == preset,
                            onClick = { title = preset },
                            label = { Text(preset, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Milestone Title *") })
                OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Target Deadline") })
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Deliverable Notes") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            ResearchMilestoneEntity(
                                projectId = projectId,
                                title = title,
                                deadline = deadline,
                                status = "Pending",
                                notes = notes,
                                isCompleted = false
                            )
                        )
                    }
                }
            ) { Text("Add Milestone") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AnalyzePaperDialog(
    userDocs: List<PersonalDocumentEntity>,
    onDismiss: () -> Unit,
    onAnalyzeText: (String, String) -> Unit
) {
    var rawText by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("uploaded_paper.pdf") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Analyze Research Paper", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select from Personal Knowledge Base documents or paste paper text:",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (userDocs.isNotEmpty()) {
                    Text("From Knowledge Base:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(userDocs.take(5)) { doc ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    rawText = doc.content
                                    fileName = doc.fileName
                                },
                                label = { Text(doc.fileName, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("File Name / Identifier") }
                )

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    label = { Text("Paper Text / Abstract / Content *") },
                    placeholder = { Text("Paste paper text or excerpt here...") },
                    modifier = Modifier.height(180.dp)
                )

                Text(
                    text = "Rule: Engine will NEVER invent authors, venue, results, or DOI. Missing fields are strictly labeled 'Not available in the document.'",
                    fontSize = 10.sp,
                    color = ScholarGold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (rawText.isNotBlank()) {
                        onAnalyzeText(rawText, fileName)
                    }
                }
            ) { Text("Analyze Paper") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PaperAnalysisResultDialog(
    analysis: ExtractedPaperAnalysis,
    project: ResearchProjectEntity,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Paper Analysis", fontWeight = FontWeight.Bold)
                Surface(
                    color = ScholarCyan.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Zero-Invention Verified",
                        fontSize = 9.sp,
                        color = ScholarCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = analysis.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Authors: ${analysis.authors} (${analysis.year})",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                    text = "Venue: ${analysis.venue} • DOI: ${analysis.doi}",
                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold)
                )

                // SOURCE CONTENT vs AI INTERPRETATION BADGES
                Surface(
                    color = Color(0x1A00D2FF),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, ScholarCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "SOURCE CONTENT (VERIFIED DIRECT FROM DOCUMENT)",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScholarCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = analysis.sourceContentSnippet,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    color = Color(0x1AE28743),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().border(1.dp, ScholarGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "AI INTERPRETATION & RELEVANCE TO '${project.targetVenue}'",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScholarGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = analysis.aiInterpretationSnippet,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (analysis.missingFieldsWarning.isNotEmpty()) {
                    Text(
                        text = "Missing in source: ${analysis.missingFieldsWarning.joinToString(", ")} (Not invented)",
                        fontSize = 10.5.sp,
                        color = Color.LightGray
                    )
                }

                // BibTeX Block
                Text("Generated BibTeX Citation:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Surface(
                    color = Color(0x22000000),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = analysis.bibtex,
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onSave) { Text("Add to Project Library") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
fun PaperDetailDialog(
    paper: ResearchPaperEntity,
    onDismiss: () -> Unit,
    onUpdate: (ResearchPaperEntity) -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var notes by remember { mutableStateOf(paper.personalNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(paper.title, fontWeight = FontWeight.Bold, maxLines = 2) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Authors: ${paper.authors}", style = MaterialTheme.typography.bodySmall)
                Text("Venue: ${paper.venue} • Year: ${paper.year}", style = MaterialTheme.typography.bodySmall, color = ScholarCyan)
                Text("DOI: ${paper.doi}", style = MaterialTheme.typography.bodySmall, color = ScholarGold)

                HorizontalDivider()
                Text("Research Problem: ${paper.researchProblem}", fontSize = 11.5.sp)
                Text("Method: ${paper.method}", fontSize = 11.5.sp)
                Text("Dataset: ${paper.dataset}", fontSize = 11.5.sp)
                Text("Metrics: ${paper.evaluationMetrics}", fontSize = 11.5.sp)
                Text("Results: ${paper.mainFindings}", fontSize = 11.5.sp)
                Text("Limitations: ${paper.limitations}", fontSize = 11.5.sp)
                Text("Relevance to Project: ${paper.relevanceToProject}", fontSize = 11.5.sp)

                HorizontalDivider()
                Text("BibTeX Citation:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Surface(
                    color = Color(0x22000000),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = paper.bibtex.ifBlank { "No BibTeX recorded" },
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { clipboard.setText(AnnotatedString(paper.bibtex)) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy BibTeX", fontSize = 10.sp)
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Personal Notes") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onUpdate(paper.copy(personalNotes = notes)) }) {
                Text("Save Notes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun AddManualPaperDialog(
    projectId: Long,
    onDismiss: () -> Unit,
    onConfirm: (ResearchPaperEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var authors by remember { mutableStateOf("Not available in the document.") }
    var year by remember { mutableStateOf("2026") }
    var venue by remember { mutableStateOf("CVPR") }
    var doi by remember { mutableStateOf("Not available in the document.") }
    var method by remember { mutableStateOf("Not available in the document.") }
    var dataset by remember { mutableStateOf("Not available in the document.") }
    var metrics by remember { mutableStateOf("Not available in the document.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Paper to Library", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Paper Title *") })
                OutlinedTextField(value = authors, onValueChange = { authors = it }, label = { Text("Authors") })
                OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Year") })
                OutlinedTextField(value = venue, onValueChange = { venue = it }, label = { Text("Venue / Journal") })
                OutlinedTextField(value = doi, onValueChange = { doi = it }, label = { Text("DOI") })
                OutlinedTextField(value = method, onValueChange = { method = it }, label = { Text("Methodology") })
                OutlinedTextField(value = dataset, onValueChange = { dataset = it }, label = { Text("Dataset") })
                OutlinedTextField(value = metrics, onValueChange = { metrics = it }, label = { Text("Metrics") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val bib = ResearchIntelligenceEngine.generateBibTeXString(
                            title = title,
                            authors = authors,
                            year = year,
                            venue = venue,
                            doi = doi,
                            url = ""
                        ).bibtex
                        onConfirm(
                            ResearchPaperEntity(
                                projectId = projectId,
                                title = title,
                                authors = authors,
                                year = year,
                                venue = venue,
                                doi = doi,
                                method = method,
                                dataset = dataset,
                                evaluationMetrics = metrics,
                                bibtex = bib
                            )
                        )
                    }
                }
            ) { Text("Save Paper") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddExperimentDialog(
    projectId: Long,
    existingCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (ResearchExperimentEntity) -> Unit
) {
    val expId = "EXP-%03d".format(existingCount + 1)
    var name by remember { mutableStateOf("PointFog-SAM Ablation Run") }
    var model by remember { mutableStateOf("PointFog-SAM") }
    var dataset by remember { mutableStateOf("Qinhuangdao Port LiDAR") }
    var hypothesis by remember { mutableStateOf("Removing scattering inversion layer will degrade port fog mIoU by >5%.") }
    var results by remember { mutableStateOf("Clean: 70.8% mIoU | Real Fog: 58.4% mIoU") }
    var metricVal by remember { mutableStateOf("58.4%") }
    var metricName by remember { mutableStateOf("mIoU Real Fog") }
    var failureAnalysis by remember { mutableStateOf("Increased False Positives on wet crane metallic structures.") }
    var nextSteps by remember { mutableStateOf("Introduce intensity threshold constraint for high reflectivity metallic returns.") }
    var hardware by remember { mutableStateOf("NVIDIA RTX 4090 / Jetson AGX Orin") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Computational Experiment", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Experiment ID: $expId", fontWeight = FontWeight.Bold, color = ScholarCyan)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Experiment Name *") })
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Model Version") })
                OutlinedTextField(value = dataset, onValueChange = { dataset = it }, label = { Text("Dataset Used") })
                OutlinedTextField(value = hypothesis, onValueChange = { hypothesis = it }, label = { Text("Hypothesis") })
                OutlinedTextField(value = results, onValueChange = { results = it }, label = { Text("Quantitative Results") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = metricName, onValueChange = { metricName = it }, label = { Text("Metric") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = metricVal, onValueChange = { metricVal = it }, label = { Text("Score") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = failureAnalysis, onValueChange = { failureAnalysis = it }, label = { Text("Failure Analysis") })
                OutlinedTextField(value = nextSteps, onValueChange = { nextSteps = it }, label = { Text("Next Steps") })
                OutlinedTextField(value = hardware, onValueChange = { hardware = it }, label = { Text("Hardware / Platform") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            ResearchExperimentEntity(
                                projectId = projectId,
                                experimentId = expId,
                                name = name,
                                date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                                dataset = dataset,
                                model = model,
                                hypothesis = hypothesis,
                                results = results,
                                customMetricName = metricName,
                                customMetricValue = metricVal,
                                failureAnalysis = failureAnalysis,
                                nextSteps = nextSteps,
                                hardware = hardware
                            )
                        )
                    }
                }
            ) { Text("Save Experiment") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ExperimentDetailDialog(
    experiment: ResearchExperimentEntity,
    onDismiss: () -> Unit,
    onUpdate: (ResearchExperimentEntity) -> Unit
) {
    var notes by remember { mutableStateOf(experiment.notes) }
    var failureAnalysis by remember { mutableStateOf(experiment.failureAnalysis) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("${experiment.experimentId}: ${experiment.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Date: ${experiment.date} • Model: ${experiment.model}", style = MaterialTheme.typography.bodySmall, color = ScholarCyan)
                Text("Dataset: ${experiment.dataset}", style = MaterialTheme.typography.bodySmall)
                if (experiment.hypothesis.isNotBlank()) {
                    Text("Hypothesis: ${experiment.hypothesis}", style = MaterialTheme.typography.bodySmall, color = ScholarGold)
                }
                Text("Quantitative Results: ${experiment.results}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Text("Hardware: ${experiment.hardware}", style = MaterialTheme.typography.bodySmall)

                HorizontalDivider()
                OutlinedTextField(
                    value = failureAnalysis,
                    onValueChange = { failureAnalysis = it },
                    label = { Text("Failure Analysis & Bottlenecks") }
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Ablation & Hyperparameter Notes") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onUpdate(experiment.copy(notes = notes, failureAnalysis = failureAnalysis)) }) {
                Text("Save Updates")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun SynthesizedGapsDialog(
    gaps: SynthesizedGapAnalysis,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Cross-Paper Gap Synthesis", fontWeight = FontWeight.Bold)
                Text(gaps.disclaimer, fontSize = 9.sp, color = ScholarGold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(gaps.summaryText, style = MaterialTheme.typography.bodySmall)

                Text("Common Assumptions in Literature:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ScholarCyan)
                gaps.commonAssumptions.forEach {
                    Text("• $it", fontSize = 11.sp)
                }

                Text("Identified Weaknesses & Bottlenecks:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ScholarGold)
                gaps.weaknessesInCurrentApproaches.forEach {
                    Text("• $it", fontSize = 11.sp)
                }

                Text("Open Opportunities for Your Research:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = ScholarGreen)
                gaps.openOpportunitiesForUserWork.forEach {
                    Text("• $it", fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
fun EditManuscriptSectionDialog(
    section: ManuscriptSectionEntity,
    onDismiss: () -> Unit,
    onSave: (ManuscriptSectionEntity) -> Unit
) {
    var status by remember { mutableStateOf(section.status) }
    var wordCount by remember { mutableStateOf(section.wordCount.toString()) }
    var notes by remember { mutableStateOf(section.notes) }

    val statusList = listOf("Not Started", "Drafting", "Review", "Completed")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Section: ${section.sectionName}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Target Word Count: ${section.targetWordCount}", fontSize = 11.sp)

                Text("Status:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(statusList) { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = wordCount,
                    onValueChange = { wordCount = it },
                    label = { Text("Current Word Count") }
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Draft Notes & Structural Outline") },
                    modifier = Modifier.height(120.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val wc = wordCount.toIntOrNull() ?: section.wordCount
                    onSave(section.copy(status = status, wordCount = wc, notes = notes))
                }
            ) { Text("Save Section") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdvisorBriefGeneratorDialog(
    brief: AdvisorBrief?,
    onGenerate: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var userNotes by remember { mutableStateOf("") }
    val clipboard = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Advisor Meeting Preparation", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (brief == null) {
                    Text(
                        text = "Synthesizes a 5-part professional meeting brief for your supervisor (Prof. Zhang Lin) grounded in actual logged experiments and milestone progress.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = userNotes,
                        onValueChange = { userNotes = it },
                        label = { Text("Specific observations or challenges to include") },
                        placeholder = { Text("e.g. GPU cluster scheduling, loss spikes...") }
                    )
                    Button(
                        onClick = { onGenerate(userNotes) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Advisor Brief")
                    }
                } else {
                    Surface(
                        color = Color(0x22000000),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = brief.formattedMarkdown,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                clipboard.setText(AnnotatedString(brief.formattedMarkdown))
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Full Brief")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (brief != null) {
                Button(onClick = onDismiss) { Text("Done") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun BibTeXExportDialog(
    bibtexContent: String,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Project BibTeX Bibliography", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Standard BibTeX export for all papers linked to this project. Missing fields are preserved without invention.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    color = Color(0x22000000),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = bibtexContent,
                        fontSize = 10.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    clipboard.setText(AnnotatedString(bibtexContent))
                    onDismiss()
                }
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Copy BibTeX")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
