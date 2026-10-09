package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.ai.*
import com.example.data.model.*
import com.example.ui.components.FactBadge
import com.example.ui.components.ModuleTabBar
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusTag
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversityScreen(viewModel: ScholarViewModel) {
    val profile by viewModel.universityProfile.collectAsStateWithLifecycle()
    val courses by viewModel.courses.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val timetable by viewModel.timetableClasses.collectAsStateWithLifecycle()
    val studySessions by viewModel.studySessions.collectAsStateWithLifecycle()
    val examPlans by viewModel.examPlans.collectAsStateWithLifecycle()
    val presentationPlans by viewModel.presentationPlans.collectAsStateWithLifecycle()

    val pendingTimetableImport by viewModel.pendingTimetableImport.collectAsStateWithLifecycle()
    val activeBreakdown by viewModel.activeAssignmentBreakdown.collectAsStateWithLifecycle()
    val pendingExtractedAssignments by viewModel.pendingExtractedAssignments.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf(
        "Today's Plan",
        "Weekly Timetable",
        "Courses & Attendance",
        "Assignments & Extraction",
        "Study Planner",
        "Exams & Presentations",
        "Weekly Review"
    )

    // Dialog controllers
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var selectedCourseForDetail by remember { mutableStateOf<CourseEntity?>(null) }
    var courseToDelete by remember { mutableStateOf<CourseEntity?>(null) }
    var courseToEdit by remember { mutableStateOf<CourseEntity?>(null) }
    var taskToDelete by remember { mutableStateOf<AcademicTaskEntity?>(null) }
    var taskToEdit by remember { mutableStateOf<AcademicTaskEntity?>(null) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showImportTimetableDialog by remember { mutableStateOf(false) }
    var showBreakdownDialog by remember { mutableStateOf(false) }
    var showExtractDocDialog by remember { mutableStateOf(false) }
    var showAddExamDialog by remember { mutableStateOf(false) }
    var showAddPresentationDialog by remember { mutableStateOf(false) }
    var courseForAttendanceEdit by remember { mutableStateOf<CourseEntity?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            when (selectedTab) {
                0, 1 -> {
                    FloatingActionButton(
                        onClick = { showImportTimetableDialog = true },
                        containerColor = DarkPrimary,
                        contentColor = DarkOnPrimary
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = "Import Timetable")
                    }
                }
                2 -> {
                    FloatingActionButton(
                        onClick = { showAddCourseDialog = true },
                        containerColor = DarkPrimary,
                        contentColor = DarkOnPrimary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Course")
                    }
                }
                3 -> {
                    FloatingActionButton(
                        onClick = { showAddTaskDialog = true },
                        containerColor = DarkPrimary,
                        contentColor = DarkOnPrimary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Assignment")
                    }
                }
                4 -> {
                    FloatingActionButton(
                        onClick = { viewModel.regenerateStudyPlan() },
                        containerColor = DarkPrimary,
                        contentColor = DarkOnPrimary
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Regenerate Study Plan")
                    }
                }
                5 -> {
                    FloatingActionButton(
                        onClick = { showAddExamDialog = true },
                        containerColor = DarkPrimary,
                        contentColor = DarkOnPrimary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Exam")
                    }
                }
                else -> {}
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Bar with University Profile quick status
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profile?.university ?: "Yanshan University (燕山大学)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${profile?.department ?: "School of Info Science"} • ${profile?.semester ?: "Fall 2026"}",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontSize = 11.sp),
                            maxLines = 1
                        )
                    }
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Config", fontSize = 11.sp)
                    }
                }
            }

            // Unified Tab Row for all 7 modules
            ModuleTabBar(
                tabs = tabTitles,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Main Tab Content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> TodayPlanTab(
                        courses = courses,
                        timetable = timetable,
                        tasks = tasks,
                        studySessions = studySessions,
                        onLogAttendance = { id, action -> viewModel.logAttendance(id, action) },
                        onToggleTask = { id, comp -> viewModel.toggleTask(id, comp) },
                        onToggleStudy = { id, comp -> viewModel.toggleStudySession(id, comp) },
                        onOpenImportTimetable = { showImportTimetableDialog = true }
                    )
                    1 -> WeeklyTimetableTab(
                        timetable = timetable,
                        studySessions = studySessions,
                        tasks = tasks,
                        examPlans = examPlans,
                        presentationPlans = presentationPlans,
                        onImportClick = { showImportTimetableDialog = true },
                        onDeleteClass = { viewModel.deleteTimetableClass(it) }
                    )
                    2 -> CoursesAndAttendanceTab(
                        courses = courses,
                        onCourseClick = { selectedCourseForDetail = it },
                        onLogAttendance = { id, act -> viewModel.logAttendance(id, act) },
                        onEditAttendanceClick = { courseForAttendanceEdit = it },
                        onEditCourse = { courseToEdit = it },
                        onDeleteCourse = { courseToDelete = it },
                        onAddCourseClick = { showAddCourseDialog = true }
                    )
                    3 -> AssignmentsAndExtractionTab(
                        tasks = tasks,
                        courses = courses,
                        onToggleTask = { id, comp -> viewModel.toggleTask(id, comp) },
                        onUpdateStatus = { id, status -> viewModel.updateTaskStatus(id, status) },
                        onEditTask = { taskToEdit = it },
                        onDeleteTask = { taskToDelete = it },
                        onAddTaskClick = { showAddTaskDialog = true },
                        onBreakdownClick = { showBreakdownDialog = true },
                        onExtractDocClick = { showExtractDocDialog = true }
                    )
                    4 -> StudyPlannerTab(
                        profile = profile,
                        timetable = timetable,
                        studySessions = studySessions,
                        tasks = tasks,
                        onToggleSession = { id, comp -> viewModel.toggleStudySession(id, comp) },
                        onRegeneratePlan = { viewModel.regenerateStudyPlan() },
                        onDeleteSession = { viewModel.deleteStudySession(it) }
                    )
                    5 -> ExamsAndPresentationsTab(
                        examPlans = examPlans,
                        presentationPlans = presentationPlans,
                        onAddExamClick = { showAddExamDialog = true },
                        onAddPresentationClick = { showAddPresentationDialog = true },
                        onDeleteExam = { viewModel.deleteExamPlan(it) },
                        onDeletePresentation = { viewModel.deletePresentationPlan(it) },
                        onTogglePresStage = { plan, stage, comp -> viewModel.togglePresentationStage(plan, stage, comp) }
                    )
                    6 -> WeeklyReviewTab(
                        courses = courses,
                        tasks = tasks,
                        studySessions = studySessions,
                        examPlans = examPlans,
                        presentationPlans = presentationPlans
                    )
                }
            }
        }
    }

    // --- MODALS & DIALOGS ---

    // 1. Edit University Profile Dialog
    if (showEditProfileDialog) {
        EditUniversityProfileDialog(
            currentProfile = profile ?: UniversityProfileEntity(),
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.updateUniversityProfile(updated)
                showEditProfileDialog = false
            }
        )
    }

    // 2. Add Course Dialog
    if (showAddCourseDialog) {
        AddCourseDialog(
            defaultTarget = profile?.attendanceTarget ?: 80.0f,
            onDismiss = { showAddCourseDialog = false },
            onConfirm = { course ->
                viewModel.insertCourse(course)
                showAddCourseDialog = false
            }
        )
    }

    // 2b. Edit Course Dialog
    courseToEdit?.let { course ->
        EditCourseDialog(
            course = course,
            onDismiss = { courseToEdit = null },
            onConfirm = { updated ->
                viewModel.updateCourse(updated)
                courseToEdit = null
            }
        )
    }

    // 3. Course Detail Dialog
    selectedCourseForDetail?.let { course ->
        CourseDetailDialog(
            course = course,
            tasks = tasks.filter { it.courseName.contains(course.name, ignoreCase = true) || it.courseName.contains(course.code, ignoreCase = true) },
            timetable = timetable.filter { it.courseCode.equals(course.code, ignoreCase = true) },
            onDismiss = { selectedCourseForDetail = null },
            onLogAttendance = { act -> viewModel.logAttendance(course.id, act) },
            onEditAttendance = {
                courseForAttendanceEdit = course
                selectedCourseForDetail = null
            }
        )
    }

    // 4. Add Task Dialog
    if (showAddTaskDialog) {
        AddTaskDialog(
            courses = courses,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { task ->
                viewModel.insertTask(task)
                showAddTaskDialog = false
            }
        )
    }

    // 4b. Edit Task Dialog
    taskToEdit?.let { task ->
        EditTaskDialog(
            task = task,
            courses = courses,
            onDismiss = { taskToEdit = null },
            onConfirm = { updated ->
                viewModel.updateTask(updated)
                taskToEdit = null
            }
        )
    }

    // 5. Import Timetable Dialog (Input)
    if (showImportTimetableDialog) {
        ImportTimetableInputDialog(
            onDismiss = { showImportTimetableDialog = false },
            onParse = { text ->
                viewModel.parseTimetableInput(text)
                showImportTimetableDialog = false
            }
        )
    }

    // 6. Timetable Confirmation Screen / Dialog
    pendingTimetableImport?.let { result ->
        TimetableConfirmationDialog(
            result = result,
            onDismiss = { viewModel.dismissTimetableConfirmation() },
            onConfirm = { verifiedClasses ->
                viewModel.confirmAndSaveTimetable(verifiedClasses)
            }
        )
    }

    // 7. Assignment Breakdown Dialog
    if (showBreakdownDialog) {
        AssignmentBreakdownDialog(
            courses = courses,
            breakdownResult = activeBreakdown,
            onGenerate = { prompt -> viewModel.breakDownAssignment(prompt) },
            onConvertStep = { step, courseName -> viewModel.convertBreakdownStepToTask(step, courseName) },
            onDismiss = {
                viewModel.dismissAssignmentBreakdown()
                showBreakdownDialog = false
            }
        )
    }

    // 8. Extract Deliverables from Syllabus Dialog
    if (showExtractDocDialog) {
        ExtractSyllabusDeliverablesDialog(
            onDismiss = { showExtractDocDialog = false },
            onExtract = { text, name ->
                viewModel.extractAssignmentsFromDoc(text, name)
                showExtractDocDialog = false
            }
        )
    }

    // Pending Extracted Assignments Confirmation Dialog
    pendingExtractedAssignments?.let { list ->
        ConfirmExtractedAssignmentsDialog(
            proposed = list,
            onDismiss = { viewModel.dismissExtractedAssignments() },
            onConfirm = { confirmedList ->
                viewModel.confirmAndSaveExtractedAssignments(confirmedList)
            }
        )
    }

    // 9. Add Exam Dialog
    if (showAddExamDialog) {
        AddExamDialog(
            courses = courses,
            onDismiss = { showAddExamDialog = false },
            onConfirm = { exam ->
                viewModel.insertExamPlan(exam)
                showAddExamDialog = false
            }
        )
    }

    // 10. Add Presentation Dialog
    if (showAddPresentationDialog) {
        AddPresentationDialog(
            courses = courses,
            onDismiss = { showAddPresentationDialog = false },
            onConfirm = { pres ->
                viewModel.insertPresentationPlan(pres)
                showAddPresentationDialog = false
            }
        )
    }

    // 11. Edit Attendance Dialog
    courseForAttendanceEdit?.let { course ->
        EditAttendanceDialog(
            course = course,
            onDismiss = { courseForAttendanceEdit = null },
            onSave = { attended, absent, excused ->
                viewModel.updateCourseAttendanceDirect(course.id, attended, absent, excused)
                courseForAttendanceEdit = null
            }
        )
    }

    // 12. Delete Confirmations
    if (courseToDelete != null) {
        com.example.ui.components.DeleteConfirmationDialog(
            title = "Delete Course",
            message = "Are you sure you want to remove '${courseToDelete!!.name}' (${courseToDelete!!.code}) from your enrolled curriculum?",
            onConfirm = {
                viewModel.deleteCourse(courseToDelete!!)
                courseToDelete = null
            },
            onDismiss = { courseToDelete = null }
        )
    }

    if (taskToDelete != null) {
        com.example.ui.components.DeleteConfirmationDialog(
            title = "Delete Assignment / Task",
            message = "Are you sure you want to remove assignment '${taskToDelete!!.title}'?",
            onConfirm = {
                viewModel.deleteTask(taskToDelete!!)
                taskToDelete = null
            },
            onDismiss = { taskToDelete = null }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: TODAY'S PLAN
// -------------------------------------------------------------

@Composable
fun TodayPlanTab(
    courses: List<CourseEntity>,
    timetable: List<TimetableClassEntity>,
    tasks: List<AcademicTaskEntity>,
    studySessions: List<StudyPlanSessionEntity>,
    onLogAttendance: (Long, String) -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    onToggleStudy: (Long, Boolean) -> Unit,
    onOpenImportTimetable: () -> Unit
) {
    val calendar = Calendar.getInstance()
    val dayOfWeekName = SimpleDateFormat("EEEE", Locale.US).format(calendar.time)
    val dateFormatted = SimpleDateFormat("MMMM d, yyyy", Locale.US).format(calendar.time)

    val todayClasses = timetable.filter { it.dayOfWeek.equals(dayOfWeekName, ignoreCase = true) }
        .sortedBy { it.startTime }
    val todaySessions = studySessions.filter { it.dayOfWeek.equals(dayOfWeekName, ignoreCase = true) }
        .sortedBy { it.startTime }
    val pendingTasks = tasks.filter { !it.isCompleted }

    // Detect free time slots for today
    val freeSlots = remember(timetable, tasks) {
        IntelligentStudyPlannerEngine.detectFreeTimeSlots(timetable, tasks)
            .filter { it.dayOfWeek.equals(dayOfWeekName, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Today Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dayOfWeekName.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarCyan,
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = dateFormatted,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.LightGray)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Today's Academic Flight Plan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "${todayClasses.size} scheduled classes • ${todaySessions.size} planned study blocks • ${pendingTasks.size} active tasks",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                    )
                }
            }
        }

        // Section: Today's Classes with quick 1-tap Attendance
        item {
            SectionHeader(
                title = "Today's Lectures & Attendance",
                subtitle = "Log attendance with 1 tap or view classroom info"
            )
        }

        if (todayClasses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No fixed university classes scheduled for today ($dayOfWeekName).",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Ideal uninterrupted window for research lab experiments or independent coding.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(todayClasses) { cls ->
                val matchedCourse = courses.find { it.code.equals(cls.courseCode, ignoreCase = true) }
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
                            Text(
                                text = "${cls.courseCode} • ${cls.startTime} - ${cls.endTime}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPrimary
                                )
                            )
                            if (cls.isUncertain) {
                                Text(
                                    text = "Needs Review",
                                    color = ScholarGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = cls.courseName,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Room, contentDescription = null, modifier = Modifier.size(14.dp), tint = ScholarCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(cls.classroom, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = ScholarGold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(cls.teacher, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Quick Attendance Action Bar
                        matchedCourse?.let { c ->
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Record (${c.attendedClasses} att / ${c.absentClasses} abs):",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilledTonalButton(
                                        onClick = { onLogAttendance(c.id, "PRESENT") },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Present", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                    FilledTonalButton(
                                        onClick = { onLogAttendance(c.id, "ABSENT") },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Absent", fontSize = 11.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                    }
                                    FilledTonalButton(
                                        onClick = { onLogAttendance(c.id, "EXCUSED") },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Excused", fontSize = 11.sp, color = ScholarGold, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: Free-Time Detection Today
        if (freeSlots.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Free-Time Detection Today",
                    subtitle = "Automatically identified periods between scheduled commitments"
                )
            }
            items(freeSlots) { slot ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(DarkPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = DarkPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Available: ${slot.startTime} – ${slot.endTime} (${slot.durationMinutes / 60}h ${slot.durationMinutes % 60}m)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPrimary
                                )
                            )
                            Text(
                                text = "Suggested: ${slot.suggestedActivity}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Focus: ${slot.priorityGoal}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }

        // Section: Today's Study Sessions
        item {
            SectionHeader(
                title = "Today's Study & Research Blocks",
                subtitle = "Personalized schedule generated by intelligent planner"
            )
        }

        if (todaySessions.isEmpty()) {
            item {
                Text(
                    text = "No study blocks scheduled for today yet. Use the Study Planner tab to generate your weekly schedule.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(todaySessions) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = session.isCompleted,
                            onCheckedChange = { onToggleStudy(session.id, it) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${session.startTime} – ${session.endTime}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = session.category,
                                    style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold, fontSize = 10.sp)
                                )
                            }
                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (session.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            if (session.notes.isNotBlank()) {
                                Text(
                                    text = session.notes,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Pending Deliverables
        item {
            SectionHeader(
                title = "Upcoming Deliverables",
                subtitle = "Assignments, lab reports, and milestones"
            )
        }

        items(pendingTasks.take(4)) { task ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { onToggleTask(task.id, it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(task.courseName, style = MaterialTheme.typography.labelSmall, color = DarkPrimary)
                            StatusTag(text = task.deadline, isCompleted = task.isCompleted)
                        }
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "Status: ${task.status} • Est: ${task.estimatedHours}h",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: WEEKLY TIMETABLE CALENDAR
// -------------------------------------------------------------

@Composable
fun WeeklyTimetableTab(
    timetable: List<TimetableClassEntity>,
    studySessions: List<StudyPlanSessionEntity>,
    tasks: List<AcademicTaskEntity>,
    examPlans: List<ExamPlanEntity>,
    presentationPlans: List<PresentationPlanEntity>,
    onImportClick: () -> Unit,
    onDeleteClass: (TimetableClassEntity) -> Unit
) {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    var selectedDayFilter by remember { mutableStateOf("All") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Weekly Academic Schedule",
                    subtitle = "Lectures, study sessions, exams, and milestones"
                )
                Button(
                    onClick = onImportClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Import", fontSize = 12.sp)
                }
            }
        }

        // Day Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedDayFilter == "All",
                        onClick = { selectedDayFilter = "All" },
                        label = { Text("All Days") }
                    )
                }
                items(days) { day ->
                    FilterChip(
                        selected = selectedDayFilter == day,
                        onClick = { selectedDayFilter = day },
                        label = { Text(day.take(3)) }
                    )
                }
            }
        }

        val displayedDays = if (selectedDayFilter == "All") days else listOf(selectedDayFilter)

        items(displayedDays) { day ->
            val dayClasses = timetable.filter { it.dayOfWeek.equals(day, ignoreCase = true) }
            val daySessions = studySessions.filter { it.dayOfWeek.equals(day, ignoreCase = true) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarCyan
                            )
                        )
                        Text(
                            text = "${dayClasses.size} Classes • ${daySessions.size} Study Sessions",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (dayClasses.isEmpty() && daySessions.isEmpty()) {
                        Text(
                            text = "No scheduled lectures or study blocks.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Display Classes
                        dayClasses.forEach { cls ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(DarkPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "${cls.startTime}–${cls.endTime} • ${cls.courseCode} ${cls.courseName}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "${cls.classroom} • ${cls.teacher} (${cls.scheduleType})",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = ScholarGold)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onDeleteClass(cls) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        // Display Study Sessions
                        daySessions.forEach { sess ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "${sess.startTime}–${sess.endTime} • [${sess.category}] ${sess.title}",
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
        }
    }
}

// -------------------------------------------------------------
// TAB 3: COURSES & ATTENDANCE TRACKER & FORECAST
// -------------------------------------------------------------

@Composable
fun CoursesAndAttendanceTab(
    courses: List<CourseEntity>,
    onCourseClick: (CourseEntity) -> Unit,
    onLogAttendance: (Long, String) -> Unit,
    onEditAttendanceClick: (CourseEntity) -> Unit,
    onEditCourse: (CourseEntity) -> Unit,
    onDeleteCourse: (CourseEntity) -> Unit,
    onAddCourseClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Course Management & Attendance",
                    subtitle = "Automated calculations, attendance forecast, and course notes"
                )
                Button(
                    onClick = onAddCourseClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Course", fontSize = 12.sp)
                }
            }
        }

        items(courses) { course ->
            val forecast = remember(course) {
                IntelligentStudyPlannerEngine.calculateAttendanceForecast(course)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCourseClick(course) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title and Delete
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = course.code,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = DarkPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${course.credits} Credits",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Row {
                            IconButton(
                                onClick = { onEditCourse(course) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Course", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { onDeleteCourse(course) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Course", modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Text(
                        text = course.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Instructor: ${course.professorName} • ${course.classroom}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Attendance Numbers Card
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Current Attendance", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                    Text(
                                        text = "${String.format("%.1f", forecast.currentRatePercent)}%",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (forecast.currentRatePercent >= course.attendanceTarget) Color(0xFF10B981) else Color(0xFFEF4444)
                                        )
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Configured Target", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                    Text(
                                        text = "${course.attendanceTarget.toInt()}%",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DarkPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (forecast.currentRatePercent / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = if (forecast.currentRatePercent >= course.attendanceTarget) Color(0xFF10B981) else Color(0xFFEF4444),
                                trackColor = MaterialTheme.colorScheme.outlineVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Scheduled: ${course.scheduledClasses} | Attended: ${course.attendedClasses} | Absent: ${course.absentClasses} | Excused: ${course.excusedClasses}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Attendance Mathematical Forecast Badge
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, contentDescription = null, tint = ScholarCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Attendance Forecast: ${forecast.safetyStatus}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Remaining classes: ${forecast.remainingClasses} • Maximum additional allowable absences: ${forecast.maxAdditionalAbsencesAllowed}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.White)
                            )
                            Text(
                                text = "[Mathematical calculation based on user data. Not a university legal claim.]",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color.LightGray)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onEditAttendanceClick(course) },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Record", fontSize = 11.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { onLogAttendance(course.id, "PRESENT") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("Present", fontSize = 11.sp, color = Color.White)
                            }
                            Button(
                                onClick = { onLogAttendance(course.id, "ABSENT") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                            ) {
                                Text("Absent", fontSize = 11.sp, color = Color.White)
                            }
                            Button(
                                onClick = { onLogAttendance(course.id, "EXCUSED") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ScholarGold)
                            ) {
                                Text("Excused", fontSize = 11.sp, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: ASSIGNMENTS & EXTRACTION
// -------------------------------------------------------------

@Composable
fun AssignmentsAndExtractionTab(
    tasks: List<AcademicTaskEntity>,
    courses: List<CourseEntity>,
    onToggleTask: (Long, Boolean) -> Unit,
    onUpdateStatus: (Long, String) -> Unit,
    onEditTask: (AcademicTaskEntity) -> Unit,
    onDeleteTask: (AcademicTaskEntity) -> Unit,
    onAddTaskClick: () -> Unit,
    onBreakdownClick: () -> Unit,
    onExtractDocClick: () -> Unit
) {
    var statusFilter by remember { mutableStateOf("All") }
    val statuses = listOf("All", "Not Started", "In Progress", "Completed")

    val filteredTasks = when (statusFilter) {
        "All" -> tasks
        "Completed" -> tasks.filter { it.isCompleted || it.status.equals("Completed", ignoreCase = true) }
        else -> tasks.filter { it.status.equals(statusFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Actions Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Intelligent Assignment Tools",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Break down large projects into manageable steps or auto-extract from syllabus notices.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onBreakdownClick,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                        ) {
                            Icon(Icons.Default.Splitscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Break Down", fontSize = 11.sp)
                        }
                        Button(
                            onClick = onExtractDocClick,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan)
                        ) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Extract Syllabus", fontSize = 11.sp, color = Color.Black)
                        }
                        Button(
                            onClick = onAddTaskClick,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ScholarGold)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Task", fontSize = 11.sp, color = Color.Black)
                        }
                    }
                }
            }
        }

        // Status Filter Chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statuses.forEach { status ->
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { statusFilter = status },
                        label = { Text(status, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Task Items List
        items(filteredTasks) { task ->
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
                        Text(task.courseName, style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontWeight = FontWeight.Bold))
                        Row {
                            IconButton(
                                onClick = { onEditTask(task) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Task", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { onDeleteTask(task) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { onToggleTask(task.id, it) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Deadline: ${task.deadline} • Est: ${task.estimatedHours}h • Type: ${task.type}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status: ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        listOf("Not Started", "In Progress", "Completed").forEach { s ->
                            val isSelected = task.status.equals(s, ignoreCase = true)
                            AssistChip(
                                onClick = { onUpdateStatus(task.id, s) },
                                label = { Text(s, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) DarkPrimary.copy(alpha = 0.2f) else Color.Transparent
                                ),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: STUDY PLANNER & FREE TIME
// -------------------------------------------------------------

@Composable
fun StudyPlannerTab(
    profile: UniversityProfileEntity?,
    timetable: List<TimetableClassEntity>,
    studySessions: List<StudyPlanSessionEntity>,
    tasks: List<AcademicTaskEntity>,
    onToggleSession: (Long, Boolean) -> Unit,
    onRegeneratePlan: () -> Unit,
    onDeleteSession: (StudyPlanSessionEntity) -> Unit
) {
    val freeSlots = remember(timetable, tasks) {
        IntelligentStudyPlannerEngine.detectFreeTimeSlots(timetable, tasks)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Intelligent Study Planner",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Button(
                            onClick = onRegeneratePlan,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Re-Plan", fontSize = 11.sp, color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Target Study: ${profile?.availableDailyStudyHours ?: 4.0}h/day • Study Window: ${profile?.preferredStudyStartTime ?: "18:00"}–${profile?.preferredStudyEndTime ?: "22:00"} • Sleep: ${profile?.sleepStartTime ?: "23:30"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    )
                }
            }
        }

        // Free Time Insights
        item {
            SectionHeader(
                title = "Detected Free-Time Windows Across the Week",
                subtitle = "Available blocks between fixed lectures for deep work"
            )
        }

        items(freeSlots) { slot ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ScholarGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ScholarGold, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${slot.dayOfWeek}: ${slot.startTime} – ${slot.endTime} (${slot.durationMinutes / 60}h ${slot.durationMinutes % 60}m)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = DarkPrimary)
                        )
                        Text(
                            text = slot.suggestedActivity,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Recommended Focus: ${slot.priorityGoal}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }

        // Active Study Plan Sessions
        item {
            SectionHeader(
                title = "Generated Weekly Study Sessions",
                subtitle = "Time-blocked daily tasks avoiding lectures and sleep"
            )
        }

        items(studySessions) { session ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = session.isCompleted,
                        onCheckedChange = { onToggleSession(session.id, it) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${session.dayOfWeek} • ${session.startTime}–${session.endTime}",
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = session.category,
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold, fontSize = 10.sp)
                            )
                        }
                        Text(
                            text = session.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (session.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (session.notes.isNotBlank()) {
                            Text(
                                text = session.notes,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                    IconButton(
                        onClick = { onDeleteSession(session) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 6: EXAMS & PRESENTATIONS
// -------------------------------------------------------------

@Composable
fun ExamsAndPresentationsTab(
    examPlans: List<ExamPlanEntity>,
    presentationPlans: List<PresentationPlanEntity>,
    onAddExamClick: () -> Unit,
    onAddPresentationClick: () -> Unit,
    onDeleteExam: (ExamPlanEntity) -> Unit,
    onDeletePresentation: (PresentationPlanEntity) -> Unit,
    onTogglePresStage: (PresentationPlanEntity, Int, Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Midterm & Final Exam Preparation",
                    subtitle = "Structured multi-phase review schedules"
                )
                Button(
                    onClick = onAddExamClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Exam", fontSize = 11.sp)
                }
            }
        }

        items(examPlans) { exam ->
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
                            text = "${exam.courseCode} • Target Date: ${exam.examDate}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                        )
                        IconButton(
                            onClick = { onDeleteExam(exam) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(
                        text = exam.courseName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Difficulty: ${exam.difficulty} • Current Prep Level: ${exam.prepLevelPercent}%",
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontSize = 11.sp)
                    )
                    LinearProgressIndicator(
                        progress = { (exam.prepLevelPercent / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(4.dp).padding(vertical = 4.dp),
                        color = ScholarGold,
                        trackColor = Color(0x33FFFFFF)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Key Topics:\n${exam.topics}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    )
                    if (exam.generatedSchedule.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Phased Schedule:\n${exam.generatedSchedule}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        // Section: Presentation Planner
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Presentation & Defense Planner",
                    subtitle = "5 structured stages: Research, Slides, Speaking, Q&A, Rehearsal"
                )
                Button(
                    onClick = onAddPresentationClick,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Presentation", fontSize = 11.sp, color = Color.Black)
                }
            }
        }

        items(presentationPlans) { pres ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Target Date: ${pres.targetDate}",
                            style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontWeight = FontWeight.Bold)
                        )
                        IconButton(
                            onClick = { onDeletePresentation(pres) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(
                        text = pres.presentationTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = pres.courseName,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Preparation Stages:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))

                    val stages = listOf(
                        Triple(1, "Stage 1: Topic Research & Outline", pres.stage1Completed),
                        Triple(2, "Stage 2: Slide Creation & Figures", pres.stage2Completed),
                        Triple(3, "Stage 3: Speaking Practice & Pronunciation", pres.stage3Completed),
                        Triple(4, "Stage 4: Anticipated Questions & Q&A Prep", pres.stage4Completed),
                        Triple(5, "Stage 5: Final Timed Dress Rehearsal", pres.stage5Completed)
                    )

                    stages.forEach { (num, label, completed) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = completed,
                                onCheckedChange = { onTogglePresStage(pres, num, it) }
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (completed) FontWeight.Normal else FontWeight.Medium,
                                    color = if (completed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 7: WEEKLY REVIEW
// -------------------------------------------------------------

@Composable
fun WeeklyReviewTab(
    courses: List<CourseEntity>,
    tasks: List<AcademicTaskEntity>,
    studySessions: List<StudyPlanSessionEntity>,
    examPlans: List<ExamPlanEntity>,
    presentationPlans: List<PresentationPlanEntity>
) {
    val totalAttended = courses.sumOf { it.attendedClasses }
    val totalAbsent = courses.sumOf { it.absentClasses }
    val completedTasks = tasks.filter { it.isCompleted }
    val unfinishedTasks = tasks.filter { !it.isCompleted }
    val completedStudy = studySessions.filter { it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "End-of-Week Academic Audit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        text = "Weekly summary of class attendance, deliverables, study hours, and upcoming obligations.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                    )
                }
            }
        }

        // Summary Metric Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Classes Logged", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Text(
                            text = "$totalAttended Present",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                        )
                        Text(
                            text = "$totalAbsent Absences",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFFEF4444))
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Study Blocks", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Text(
                            text = "${completedStudy.size} Done",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkPrimary)
                        )
                        Text(
                            text = "of ${studySessions.size} scheduled",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Tasks Done", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        Text(
                            text = "${completedTasks.size} Finished",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                        )
                        Text(
                            text = "${unfinishedTasks.size} Remaining",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                        )
                    }
                }
            }
        }

        // Attendance Breakdown by Course
        item {
            SectionHeader(
                title = "Attendance Rates by Course",
                subtitle = "Ensuring all courses stay above configured safety thresholds"
            )
        }

        items(courses) { course ->
            val totalLogged = course.attendedClasses + course.absentClasses
            val rate = if (totalLogged > 0) (course.attendedClasses.toFloat() / totalLogged) * 100f else 100f
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("${course.code} ${course.name}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = "${course.attendedClasses} attended / ${course.absentClasses} absent (${course.excusedClasses} excused)",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Text(
                        text = "${String.format("%.1f", rate)}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (rate >= course.attendanceTarget) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                    )
                }
            }
        }

        // Unfinished Tasks Carried Over
        if (unfinishedTasks.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Unfinished Tasks Carried Forward",
                    subtitle = "High priority items to tackle first next week"
                )
            }
            items(unfinishedTasks) { t ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(t.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                            Text("${t.courseName} • Due: ${t.deadline}", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = DarkPrimary))
                        }
                        StatusTag(text = t.status, isCompleted = false)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT DIALOGS
// -------------------------------------------------------------

@Composable
fun EditUniversityProfileDialog(
    currentProfile: UniversityProfileEntity,
    onDismiss: () -> Unit,
    onSave: (UniversityProfileEntity) -> Unit
) {
    var uni by remember { mutableStateOf(currentProfile.university) }
    var dept by remember { mutableStateOf(currentProfile.department) }
    var degree by remember { mutableStateOf(currentProfile.degree) }
    var sem by remember { mutableStateOf(currentProfile.semester) }
    var year by remember { mutableStateOf(currentProfile.academicYear) }
    var startDate by remember { mutableStateOf(currentProfile.semesterStartDate) }
    var endDate by remember { mutableStateOf(currentProfile.semesterEndDate) }
    var duration by remember { mutableStateOf(currentProfile.defaultClassDurationMinutes.toString()) }
    var target by remember { mutableStateOf(currentProfile.attendanceTarget.toString()) }
    var studyHours by remember { mutableStateOf(currentProfile.availableDailyStudyHours.toString()) }
    var studyStart by remember { mutableStateOf(currentProfile.preferredStudyStartTime) }
    var studyEnd by remember { mutableStateOf(currentProfile.preferredStudyEndTime) }
    var sleepStart by remember { mutableStateOf(currentProfile.sleepStartTime) }
    var sleepEnd by remember { mutableStateOf(currentProfile.sleepEndTime) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("University Academic Profile", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = uni, onValueChange = { uni = it }, label = { Text("University") }) }
                item { OutlinedTextField(value = dept, onValueChange = { dept = it }, label = { Text("Department / School") }) }
                item { OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Degree") }) }
                item { OutlinedTextField(value = sem, onValueChange = { sem = it }, label = { Text("Current Semester") }) }
                item { OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Academic Year") }) }
                item { OutlinedTextField(value = startDate, onValueChange = { startDate = it }, label = { Text("Semester Start (YYYY-MM-DD)") }) }
                item { OutlinedTextField(value = endDate, onValueChange = { endDate = it }, label = { Text("Semester End (YYYY-MM-DD)") }) }
                item { OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Default Class Duration (Minutes)") }) }
                item { OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Default Attendance Target (%)") }) }
                item { OutlinedTextField(value = studyHours, onValueChange = { studyHours = it }, label = { Text("Available Daily Study Hours") }) }
                item { OutlinedTextField(value = studyStart, onValueChange = { studyStart = it }, label = { Text("Preferred Study Start (HH:mm)") }) }
                item { OutlinedTextField(value = studyEnd, onValueChange = { studyEnd = it }, label = { Text("Preferred Study End (HH:mm)") }) }
                item { OutlinedTextField(value = sleepStart, onValueChange = { sleepStart = it }, label = { Text("Sleep Start (HH:mm)") }) }
                item { OutlinedTextField(value = sleepEnd, onValueChange = { sleepEnd = it }, label = { Text("Wake Time (HH:mm)") }) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentProfile.copy(
                            university = uni,
                            department = dept,
                            degree = degree,
                            semester = sem,
                            academicYear = year,
                            semesterStartDate = startDate,
                            semesterEndDate = endDate,
                            defaultClassDurationMinutes = duration.toIntOrNull() ?: 95,
                            attendanceTarget = target.toFloatOrNull() ?: 80.0f,
                            availableDailyStudyHours = studyHours.toFloatOrNull() ?: 4.0f,
                            preferredStudyStartTime = studyStart,
                            preferredStudyEndTime = studyEnd,
                            sleepStartTime = sleepStart,
                            sleepEndTime = sleepEnd
                        )
                    )
                }
            ) { Text("Save Settings") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddCourseDialog(
    defaultTarget: Float,
    onDismiss: () -> Unit,
    onConfirm: (CourseEntity) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var prof by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("3") }
    var classroom by remember { mutableStateOf("") }
    var schedule by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var target by remember { mutableStateOf(defaultTarget.toString()) }
    var scheduledClasses by remember { mutableStateOf("32") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add University Course", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Course Code (e.g. CS309)") }) }
                item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Course Name") }) }
                item { OutlinedTextField(value = prof, onValueChange = { prof = it }, label = { Text("Professor Name") }) }
                item { OutlinedTextField(value = classroom, onValueChange = { classroom = it }, label = { Text("Classroom (e.g. East Campus 4-302)") }) }
                item { OutlinedTextField(value = credits, onValueChange = { credits = it }, label = { Text("Credits") }) }
                item { OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Schedule (e.g. Mon 08:00–09:35)") }) }
                item { OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Attendance Target (%)") }) }
                item { OutlinedTextField(value = scheduledClasses, onValueChange = { scheduledClasses = it }, label = { Text("Total Scheduled Classes in Semester") }) }
                item { OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Course Notes / Syllabus Summary") }) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && name.isNotBlank()) {
                        onConfirm(
                            CourseEntity(
                                code = code,
                                name = name,
                                professorName = prof,
                                professorContact = contact,
                                credits = credits.toIntOrNull() ?: 3,
                                classroom = classroom,
                                scheduleTime = schedule,
                                syllabusSummary = notes,
                                attendanceTarget = target.toFloatOrNull() ?: defaultTarget,
                                scheduledClasses = scheduledClasses.toIntOrNull() ?: 32,
                                attendedClasses = 0,
                                absentClasses = 0,
                                excusedClasses = 0
                            )
                        )
                    }
                }
            ) { Text("Create Course") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditCourseDialog(
    course: CourseEntity,
    onDismiss: () -> Unit,
    onConfirm: (CourseEntity) -> Unit
) {
    var code by remember { mutableStateOf(course.code) }
    var name by remember { mutableStateOf(course.name) }
    var prof by remember { mutableStateOf(course.professorName) }
    var contact by remember { mutableStateOf(course.professorContact) }
    var credits by remember { mutableStateOf(course.credits.toString()) }
    var classroom by remember { mutableStateOf(course.classroom) }
    var schedule by remember { mutableStateOf(course.scheduleTime) }
    var notes by remember { mutableStateOf(course.syllabusSummary) }
    var target by remember { mutableStateOf(course.attendanceTarget.toString()) }
    var scheduledClasses by remember { mutableStateOf(course.scheduledClasses.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit University Course", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Course Code (e.g. CS309)") }) }
                item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Course Name") }) }
                item { OutlinedTextField(value = prof, onValueChange = { prof = it }, label = { Text("Professor Name") }) }
                item { OutlinedTextField(value = classroom, onValueChange = { classroom = it }, label = { Text("Classroom (e.g. East Campus 4-302)") }) }
                item { OutlinedTextField(value = credits, onValueChange = { credits = it }, label = { Text("Credits") }) }
                item { OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Schedule (e.g. Mon 08:00–09:35)") }) }
                item { OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Attendance Target (%)") }) }
                item { OutlinedTextField(value = scheduledClasses, onValueChange = { scheduledClasses = it }, label = { Text("Total Scheduled Classes in Semester") }) }
                item { OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Course Notes / Syllabus Summary") }) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank() && name.isNotBlank()) {
                        onConfirm(
                            course.copy(
                                code = code,
                                name = name,
                                professorName = prof,
                                professorContact = contact,
                                credits = credits.toIntOrNull() ?: course.credits,
                                classroom = classroom,
                                scheduleTime = schedule,
                                syllabusSummary = notes,
                                attendanceTarget = target.toFloatOrNull() ?: course.attendanceTarget,
                                scheduledClasses = scheduledClasses.toIntOrNull() ?: course.scheduledClasses
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

@Composable
fun CourseDetailDialog(
    course: CourseEntity,
    tasks: List<AcademicTaskEntity>,
    timetable: List<TimetableClassEntity>,
    onDismiss: () -> Unit,
    onLogAttendance: (String) -> Unit,
    onEditAttendance: () -> Unit
) {
    val forecast = remember(course) {
        IntelligentStudyPlannerEngine.calculateAttendanceForecast(course)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(course.code, style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontWeight = FontWeight.Bold))
                    Text(course.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Professor: ${course.professorName}\nClassroom: ${course.classroom}\nCredits: ${course.credits}\nSchedule: ${course.scheduleTime}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Mathematical Attendance Forecast",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = forecast.mathematicalExplanation,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp, lineHeight = 16.sp)
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Log Attendance Today:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilledTonalButton(onClick = { onLogAttendance("PRESENT") }) { Text("Present", fontSize = 11.sp) }
                            FilledTonalButton(onClick = { onLogAttendance("ABSENT") }) { Text("Absent", fontSize = 11.sp) }
                            FilledTonalButton(onClick = { onLogAttendance("EXCUSED") }) { Text("Excused", fontSize = 11.sp) }
                        }
                    }
                }

                if (course.syllabusSummary.isNotBlank()) {
                    item {
                        Text("Syllabus & Course Notes:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Text(course.syllabusSummary, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }

                if (tasks.isNotEmpty()) {
                    item {
                        Text("Assignments for this Course (${tasks.size}):", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        tasks.forEach { t ->
                            Text("• ${t.title} (Due: ${t.deadline})", style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onEditAttendance) { Text("Edit Record") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun EditAttendanceDialog(
    course: CourseEntity,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit
) {
    var attended by remember { mutableStateOf(course.attendedClasses.toString()) }
    var absent by remember { mutableStateOf(course.absentClasses.toString()) }
    var excused by remember { mutableStateOf(course.excusedClasses.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Correct Attendance: ${course.code}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = attended, onValueChange = { attended = it }, label = { Text("Classes Attended") })
                OutlinedTextField(value = absent, onValueChange = { absent = it }, label = { Text("Classes Absent") })
                OutlinedTextField(value = excused, onValueChange = { excused = it }, label = { Text("Classes Excused") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        attended.toIntOrNull() ?: course.attendedClasses,
                        absent.toIntOrNull() ?: course.absentClasses,
                        excused.toIntOrNull() ?: course.excusedClasses
                    )
                }
            ) { Text("Save Record") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ImportTimetableInputDialog(
    onDismiss: () -> Unit,
    onParse: (String) -> Unit
) {
    var rawText by remember { mutableStateOf("") }

    val sampleTimetable = """
        Monday
        08:00–09:35 CS301 Advanced Algorithms & Optimization Room 4-302 Prof. Wang Zhi Weeks 1-16
        16:00–17:35 CH302 Advanced Technical Chinese Room 204 Teacher Li Hua Weeks 1-16
        Tuesday
        10:15–11:50 CS305 Computer Vision & Pattern Recognition Science Hall 5-201 Prof. Zhang Lin
        Wednesday
        08:00–09:35 CS301 Advanced Algorithms & Optimization Room 4-302
        Thursday
        10:15–11:50 CS305 Computer Vision Room 5-201
        Friday
        09:50–12:15 CS308 Operating Systems Internals Room 3-101 Prof. Chen Gang
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import University Timetable", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Paste your timetable text, OCR scan, or course notice below. The parser extracts schedule entries and presents a confirmation screen for your review.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    label = { Text("Timetable Text or OCR") },
                    modifier = Modifier.fillMaxWidth().height(160.dp)
                )
                TextButton(
                    onClick = { rawText = sampleTimetable },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Load Sample YSU Schedule", fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (rawText.isNotBlank()) onParse(rawText)
                }
            ) { Text("Analyze Schedule") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TimetableConfirmationDialog(
    result: ParsedTimetableResult,
    onDismiss: () -> Unit,
    onConfirm: (List<TimetableClassEntity>) -> Unit
) {
    var editableClasses by remember { mutableStateOf(result.classes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Confirm Extracted Timetable", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                    text = "Extraction confidence: ${(result.confidenceScore * 100).toInt()}% • Review and correct entries before saving",
                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontSize = 11.sp)
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (result.warnings.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ScholarGold.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                result.warnings.forEach { w ->
                                    Text("⚠ $w", style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontSize = 11.sp))
                                }
                            }
                        }
                    }
                }

                items(editableClasses.indices.toList()) { index ->
                    val item = editableClasses[index]
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isUncertain) ScholarGold.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.dayOfWeek} • ${item.startTime}–${item.endTime}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DarkPrimary))
                                if (item.isUncertain) {
                                    Text("Uncertain", style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold, fontWeight = FontWeight.Bold))
                                }
                            }
                            Text(
                                text = "${item.courseCode} ${item.courseName}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Room: ${item.classroom} | Teacher: ${item.teacher} | Type: ${item.scheduleType}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            if (item.uncertaintyReason.isNotBlank()) {
                                Text(
                                    text = "Review note: ${item.uncertaintyReason}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = ScholarGold)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(editableClasses) }) {
                Text("Confirm & Save Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Discard") }
        }
    )
}

@Composable
fun AssignmentBreakdownDialog(
    courses: List<CourseEntity>,
    breakdownResult: List<AssignmentBreakdownStep>?,
    onGenerate: (String) -> Unit,
    onConvertStep: (AssignmentBreakdownStep, String) -> Unit,
    onDismiss: () -> Unit
) {
    var prompt by remember { mutableStateOf("I have a 3000-word computer vision research report due Friday.") }
    var selectedCourse by remember { mutableStateOf(courses.firstOrNull()?.name ?: "Computer Science") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Break Down Assignment", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "Enter any complex academic task (e.g., '3000-word report due Friday' or 'xv6 copy-on-write fork due next week'):",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { prompt = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { onGenerate(prompt) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Decompose Into Phased Milestones")
                    }
                }

                breakdownResult?.let { steps ->
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Phased Execution Roadmap:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
                    }
                    items(steps) { step ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(step.suggestedDay, style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontWeight = FontWeight.Bold))
                                    Text("Est: ${step.estimatedHours}h", style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold))
                                }
                                Text(step.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text(step.description, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    OutlinedButton(
                                        onClick = { onConvertStep(step, selectedCourse) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("+ Add to Tasks", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ExtractSyllabusDeliverablesDialog(
    onDismiss: () -> Unit,
    onExtract: (String, String) -> Unit
) {
    var docName by remember { mutableStateOf("Course Syllabus 2026") }
    var textContent by remember { mutableStateOf("") }

    val sampleSyllabus = """
        CS305 Computer Vision & Pattern Recognition - Syllabus
        Grading Structure:
        - Midterm Written Exam: Week 9 (30%)
        - Lab Assignment 1: Self-Attention module implementation due in 7 days (15%)
        - Term Project Oral Presentation: Week 14 10-minute presentation (20%)
        - Final Course Project: Deep architecture benchmark due Week 16 (35%)
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Extract Syllabus Deliverables", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Upload or paste a syllabus or notice. The AI will extract all assignments, presentation dates, and exams for your confirmation.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                OutlinedTextField(
                    value = docName,
                    onValueChange = { docName = it },
                    label = { Text("Document Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = textContent,
                    onValueChange = { textContent = it },
                    label = { Text("Syllabus Content") },
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                )
                TextButton(
                    onClick = { textContent = sampleSyllabus },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Load Sample Syllabus", fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (textContent.isNotBlank()) onExtract(textContent, docName)
                }
            ) { Text("Extract Deliverables") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ConfirmExtractedAssignmentsDialog(
    proposed: List<ProposedAssignment>,
    onDismiss: () -> Unit,
    onConfirm: (List<ProposedAssignment>) -> Unit
) {
    var selectedItems by remember { mutableStateOf(proposed) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Confirm Extracted Deliverables", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text("Review and confirm before adding to your academic tracker", style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontSize = 11.sp))
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedItems) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.type, style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontWeight = FontWeight.Bold))
                                Text("Weight: ${item.gradingWeight}", style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold))
                            }
                            Text(item.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text(
                                text = "Course: ${item.courseName} • Due: ${item.deadline}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Text(
                                text = "Requirements: ${item.requirements}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedItems) }) {
                Text("Add to Academic Tracker")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddExamDialog(
    courses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onConfirm: (ExamPlanEntity) -> Unit
) {
    var courseCode by remember { mutableStateOf(courses.firstOrNull()?.code ?: "CS301") }
    var courseName by remember { mutableStateOf(courses.firstOrNull()?.name ?: "Advanced Algorithms") }
    var date by remember { mutableStateOf("Nov 15, 2026") }
    var topics by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Moderate") }
    var prepLevel by remember { mutableStateOf("40") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Exam Preparation Plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = courseCode, onValueChange = { courseCode = it }, label = { Text("Course Code") })
                OutlinedTextField(value = courseName, onValueChange = { courseName = it }, label = { Text("Course Name") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Exam Date") })
                OutlinedTextField(value = topics, onValueChange = { topics = it }, label = { Text("Topics Covered") })
                OutlinedTextField(value = difficulty, onValueChange = { difficulty = it }, label = { Text("Difficulty (Easy, Moderate, Hard)") })
                OutlinedTextField(value = prepLevel, onValueChange = { prepLevel = it }, label = { Text("Current Prep Level (%)") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        ExamPlanEntity(
                            courseCode = courseCode,
                            courseName = courseName,
                            examDate = date,
                            topics = topics,
                            difficulty = difficulty,
                            prepLevelPercent = prepLevel.toIntOrNull() ?: 40,
                            generatedSchedule = "Phase 1: Conceptual review & lecture notes\nPhase 2: Mock problems & past papers\nPhase 3: Formula sheet formulation"
                        )
                    )
                }
            ) { Text("Create Exam Plan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddPresentationDialog(
    courses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onConfirm: (PresentationPlanEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf(courses.firstOrNull()?.name ?: "Computer Science Seminar") }
    var date by remember { mutableStateOf("Nov 20, 2026") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Presentation Planner") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Presentation Title") })
                OutlinedTextField(value = courseName, onValueChange = { courseName = it }, label = { Text("Course or Venue") })
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Target Presentation Date") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            PresentationPlanEntity(
                                courseName = courseName,
                                presentationTitle = title,
                                targetDate = date,
                                stagesSummary = "Structured 5-stage preparation leading up to defense."
                            )
                        )
                    }
                }
            ) { Text("Create Planner") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddTaskDialog(
    courses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onConfirm: (AcademicTaskEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf(courses.firstOrNull()?.name ?: "General Computer Science") }
    var type by remember { mutableStateOf("Assignment") }
    var deadline by remember { mutableStateOf("In 7 Days") }
    var hours by remember { mutableStateOf("3.0") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Academic Deliverable") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title") })
                OutlinedTextField(value = courseName, onValueChange = { courseName = it }, label = { Text("Course Name") })
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (Assignment, Exam, Project)") })
                OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline") })
                OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Estimated Hours") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Requirements / Description") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            AcademicTaskEntity(
                                courseName = courseName,
                                title = title,
                                type = type,
                                deadline = deadline,
                                priority = "High",
                                description = desc,
                                status = "Not Started",
                                estimatedHours = hours.toFloatOrNull() ?: 3.0f
                            )
                        )
                    }
                }
            ) { Text("Save Task") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditTaskDialog(
    task: AcademicTaskEntity,
    courses: List<CourseEntity>,
    onDismiss: () -> Unit,
    onConfirm: (AcademicTaskEntity) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var courseName by remember { mutableStateOf(task.courseName) }
    var type by remember { mutableStateOf(task.type) }
    var deadline by remember { mutableStateOf(task.deadline) }
    var hours by remember { mutableStateOf(task.estimatedHours.toString()) }
    var desc by remember { mutableStateOf(task.description) }
    var status by remember { mutableStateOf(task.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Academic Deliverable") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Title") })
                OutlinedTextField(value = courseName, onValueChange = { courseName = it }, label = { Text("Course Name") })
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type (Assignment, Exam, Project)") })
                OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline") })
                OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Estimated Hours") })
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status (Not Started, In Progress, Completed)") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Requirements / Description") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            task.copy(
                                courseName = courseName,
                                title = title,
                                type = type,
                                deadline = deadline,
                                description = desc,
                                status = status,
                                estimatedHours = hours.toFloatOrNull() ?: task.estimatedHours,
                                isCompleted = status.equals("Completed", ignoreCase = true)
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
