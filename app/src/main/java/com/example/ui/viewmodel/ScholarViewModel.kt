package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.*
import com.example.data.auth.ScholarAccount
import com.example.data.auth.ScholarAuthManager
import com.example.export.*
import com.example.voice.*
import com.example.data.db.AppDatabase
import com.example.data.db.InitialDataPopulator
import com.example.data.model.*
import com.example.data.repository.ScholarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

enum class AppScreen(val id: String, val title: String, val iconName: String) {
    DASHBOARD("dashboard", "Dashboard", "Dashboard"),
    COMMAND_CENTER("command_center", "AI Command Center", "AutoAwesome"),
    AI_MENTOR("mentor", "AI Mentor", "Psychology"),
    UNIVERSITY("university", "University", "School"),
    RESEARCH_LAB("research", "Research Lab", "Science"),
    CS_SKILLS("skills", "CS Skills", "Terminal"),
    PROJECT_BUILDER("projects", "Project Builder", "Build"),
    CAREER_CENTER("career", "Career & China Jobs", "Work"),
    CHINESE_LANGUAGE("chinese", "Chinese & Life", "Translate"),
    DOCUMENTS("documents", "Doc Intel & KB", "Description"),
    ROADMAP("roadmap", "Roadmap & Audit", "Timeline"),
    CHINA_WORK("china_work", "China Work & Visa", "FlightTakeoff"),
    PROFILE("profile", "Scholar Profile", "Person"),
    SETTINGS("settings", "Settings", "Settings")
}

class ScholarViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val db = AppDatabase.getInstance(application)
    val repository = ScholarRepository(db)
    val authManager = ScholarAuthManager(application)
    private val prefs = application.getSharedPreferences("cs_scholar_session", Context.MODE_PRIVATE)

    // Authentication Session State
    private val _isAuthenticated = MutableStateFlow(prefs.getBoolean("is_authenticated", false))
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUserEmail = MutableStateFlow(prefs.getString("user_email", "") ?: "")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    private val _currentStudentId = MutableStateFlow(prefs.getString("student_id", "") ?: "")
    val currentStudentId: StateFlow<String> = _currentStudentId.asStateFlow()

    // Text to Speech
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _toastEvent = MutableStateFlow<String?>(null)
    val toastEvent: StateFlow<String?> = _toastEvent.asStateFlow()

    init {
        // Initialize TTS
        try {
            tts = TextToSpeech(application, this)
        } catch (_: Exception) {}

        // Ensure database is populated with seed data on first boot or restore current authenticated user's profile
        viewModelScope.launch(Dispatchers.IO) {
            val savedEmail = prefs.getString("user_email", null)
            if (savedEmail != null) {
                val account = authManager.getAccount(savedEmail)
                if (account != null) {
                    repository.updateProfile(account.profile)
                }
            } else {
                val existing = repository.getUserProfileOnce()
                if (existing == null) {
                    InitialDataPopulator.populate(db)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val res = tts?.setLanguage(Locale.CHINESE)
            isTtsReady = res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun speakChinese(text: String) {
        _toastEvent.value = "Pronouncing: $text"
        if (isTtsReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_chinese")
        }
    }

    fun clearToast() {
        _toastEvent.value = null
    }

    fun signIn(emailOrId: String, password: String): Result<ScholarAccount> {
        val result = authManager.signIn(emailOrId, password)
        if (result.isSuccess) {
            val account = result.getOrThrow()
            prefs.edit()
                .putBoolean("is_authenticated", true)
                .putString("user_email", account.email)
                .putString("student_id", account.studentId)
                .putString("user_name", account.fullName)
                .apply()

            _currentUserEmail.value = account.email
            _currentStudentId.value = account.studentId
            _isAuthenticated.value = true
            _currentScreen.value = AppScreen.DASHBOARD

            viewModelScope.launch(Dispatchers.IO) {
                repository.updateProfile(account.profile)
            }
            _toastEvent.value = "Welcome back, ${account.fullName}!"
        }
        return result
    }

    fun signUp(
        name: String,
        email: String,
        studentId: String,
        password: String,
        confirmPassword: String,
        university: String = "Yanshan University (燕山大学)",
        department: String = "School of Information Science and Engineering",
        degree: String = "Bachelor of Engineering in CS & Technology",
        nationality: String = "International Student"
    ): Result<ScholarAccount> {
        val result = authManager.signUp(
            name, email, studentId, password, confirmPassword,
            university, department, degree, nationality
        )
        if (result.isSuccess) {
            val account = result.getOrThrow()
            prefs.edit()
                .putBoolean("is_authenticated", true)
                .putString("user_email", account.email)
                .putString("student_id", account.studentId)
                .putString("user_name", account.fullName)
                .apply()

            _currentUserEmail.value = account.email
            _currentStudentId.value = account.studentId
            _isAuthenticated.value = true
            _currentScreen.value = AppScreen.DASHBOARD

            viewModelScope.launch(Dispatchers.IO) {
                repository.updateProfile(account.profile)
            }
            _toastEvent.value = "Welcome to CS Scholar OS, ${account.fullName}!"
        }
        return result
    }

    fun loginDemo() {
        signIn("alexei.chen@ysu.edu.cn", "ysu_scholar_2026")
    }

    fun login(email: String, studentId: String) {
        val account = authManager.getAccount(email) ?: authManager.getAccount(studentId)
        if (account != null) {
            signIn(account.email, "ysu_scholar_2026")
        } else {
            signUp(
                name = if (email.contains("alexei", true)) "Alexei Chen-Kovalenko" else email.substringBefore("@"),
                email = email.ifBlank { "alexei.chen@ysu.edu.cn" },
                studentId = studentId.ifBlank { "2024CS0892" },
                password = "ysu_scholar_2026",
                confirmPassword = "ysu_scholar_2026",
                university = "Yanshan University (燕山大学)",
                department = "School of Information Science and Engineering",
                degree = "Bachelor of Engineering in CS & Technology",
                nationality = "International Student"
            )
        }
    }

    fun logout() {
        prefs.edit()
            .putBoolean("is_authenticated", false)
            .remove("user_email")
            .remove("student_id")
            .remove("user_name")
            .apply()

        _currentUserEmail.value = ""
        _currentStudentId.value = ""
        _isAuthenticated.value = false
        _currentScreen.value = AppScreen.DASHBOARD
        _toastEvent.value = "Signed out successfully"
    }

    fun resetDatabaseToDefaults() {
        viewModelScope.launch(Dispatchers.IO) {
            InitialDataPopulator.populate(db)
            _toastEvent.value = "Database restored to standard YSU dataset"
        }
    }

    // Current Screen
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // Profile
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.updateProfile(profile)
            val email = _currentUserEmail.value
            if (email.isNotBlank()) {
                authManager.updateAccountProfile(email, profile)
            }
            _toastEvent.value = "Profile updated successfully"
        }
    }

    // University Profile
    val universityProfile: StateFlow<UniversityProfileEntity?> = repository.universityProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateUniversityProfile(profile: UniversityProfileEntity) {
        viewModelScope.launch {
            repository.updateUniversityProfile(profile)
            _toastEvent.value = "University profile updated"
        }
    }

    // Courses & Tasks
    val courses: StateFlow<List<CourseEntity>> = repository.courses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tasks: StateFlow<List<AcademicTaskEntity>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertCourse(course: CourseEntity) {
        viewModelScope.launch {
            repository.insertCourse(course)
            _toastEvent.value = "Course '${course.code}' added"
        }
    }

    fun updateCourse(course: CourseEntity) {
        viewModelScope.launch {
            repository.updateCourse(course)
            _toastEvent.value = "Course '${course.code}' updated"
        }
    }

    fun deleteCourse(course: CourseEntity) {
        viewModelScope.launch {
            repository.deleteCourse(course)
            _toastEvent.value = "Course removed"
        }
    }

    fun logAttendance(courseId: Long, action: String) {
        viewModelScope.launch {
            val course = courses.value.find { it.id == courseId } ?: return@launch
            var newAttended = course.attendedClasses
            var newAbsent = course.absentClasses
            var newExcused = course.excusedClasses
            val newScheduled = course.scheduledClasses

            when (action.uppercase()) {
                "PRESENT" -> {
                    newAttended += 1
                    _toastEvent.value = "Logged: Present for ${course.code} (+1 attended)"
                }
                "ABSENT" -> {
                    newAbsent += 1
                    _toastEvent.value = "Logged: Absent for ${course.code} (+1 absence)"
                }
                "EXCUSED" -> {
                    newExcused += 1
                    _toastEvent.value = "Logged: Excused for ${course.code} (+1 excused)"
                }
            }

            repository.updateCourseAttendance(courseId, newAttended, newAbsent, newExcused)
        }
    }

    fun updateCourseAttendanceDirect(courseId: Long, attended: Int, absent: Int, excused: Int) {
        viewModelScope.launch {
            repository.updateCourseAttendance(courseId, attended, absent, excused)
            _toastEvent.value = "Attendance record updated"
        }
    }

    fun insertTask(task: AcademicTaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
            _toastEvent.value = "Task '${task.title}' added"
        }
    }

    fun updateTask(task: AcademicTaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            _toastEvent.value = "Task updated"
        }
    }

    fun updateTaskStatus(taskId: Long, status: String) {
        viewModelScope.launch {
            val isCompleted = status.equals("Completed", ignoreCase = true)
            repository.updateTaskStatus(taskId, status, isCompleted)
            _toastEvent.value = "Task marked $status"
        }
    }

    fun toggleTask(id: Long, completed: Boolean) {
        viewModelScope.launch {
            val newStatus = if (completed) "Completed" else "In Progress"
            repository.updateTaskStatus(id, newStatus, completed)
        }
    }

    fun deleteTask(task: AcademicTaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _toastEvent.value = "Task deleted"
        }
    }

    // Timetable Classes
    val timetableClasses: StateFlow<List<TimetableClassEntity>> = repository.timetableClasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertTimetableClass(item: TimetableClassEntity) {
        viewModelScope.launch {
            repository.insertTimetableClass(item)
            _toastEvent.value = "Schedule added for ${item.courseCode}"
        }
    }

    fun updateTimetableClass(item: TimetableClassEntity) {
        viewModelScope.launch {
            repository.updateTimetableClass(item)
            _toastEvent.value = "Schedule updated"
        }
    }

    fun deleteTimetableClass(item: TimetableClassEntity) {
        viewModelScope.launch {
            repository.deleteTimetableClass(item)
            _toastEvent.value = "Schedule item deleted"
        }
    }

    // Timetable Import & Confirmation
    private val _pendingTimetableImport = MutableStateFlow<ParsedTimetableResult?>(null)
    val pendingTimetableImport: StateFlow<ParsedTimetableResult?> = _pendingTimetableImport.asStateFlow()

    fun parseTimetableInput(text: String) {
        val result = TimetableIntelligenceEngine.parseTimetableText(text)
        _pendingTimetableImport.value = result
    }

    fun confirmAndSaveTimetable(classes: List<TimetableClassEntity>) {
        viewModelScope.launch {
            repository.clearTimetable()
            repository.insertTimetableClasses(classes)
            _pendingTimetableImport.value = null
            _toastEvent.value = "Saved ${classes.size} verified timetable entries"
        }
    }

    fun dismissTimetableConfirmation() {
        _pendingTimetableImport.value = null
    }

    // Study Plan Sessions
    val studySessions: StateFlow<List<StudyPlanSessionEntity>> = repository.studySessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertStudySession(session: StudyPlanSessionEntity) {
        viewModelScope.launch {
            repository.insertStudySession(session)
            _toastEvent.value = "Study session added"
        }
    }

    fun updateStudySession(session: StudyPlanSessionEntity) {
        viewModelScope.launch {
            repository.updateStudySession(session)
            _toastEvent.value = "Session updated"
        }
    }

    fun toggleStudySession(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleStudySessionCompleted(id, completed)
        }
    }

    fun deleteStudySession(session: StudyPlanSessionEntity) {
        viewModelScope.launch {
            repository.deleteStudySession(session)
            _toastEvent.value = "Study session removed"
        }
    }

    fun regenerateStudyPlan() {
        viewModelScope.launch {
            val prof = universityProfile.value
            val currentClasses = timetableClasses.value
            val currentTasks = tasks.value
            val currentExams = examPlans.value
            val currentPres = presentationPlans.value

            val generated = IntelligentStudyPlannerEngine.generateStudyPlan(
                prof, currentClasses, currentTasks, currentExams, currentPres
            )

            repository.clearStudySessions()
            repository.insertStudySessions(generated)
            _toastEvent.value = "Generated ${generated.size} personalized study sessions for this week"
        }
    }

    // Exam Plans
    val examPlans: StateFlow<List<ExamPlanEntity>> = repository.examPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertExamPlan(plan: ExamPlanEntity) {
        viewModelScope.launch {
            repository.insertExamPlan(plan)
            _toastEvent.value = "Exam plan created for ${plan.courseCode}"
        }
    }

    fun updateExamPlan(plan: ExamPlanEntity) {
        viewModelScope.launch {
            repository.updateExamPlan(plan)
            _toastEvent.value = "Exam plan updated"
        }
    }

    fun deleteExamPlan(plan: ExamPlanEntity) {
        viewModelScope.launch {
            repository.deleteExamPlan(plan)
            _toastEvent.value = "Exam plan removed"
        }
    }

    // Presentation Plans
    val presentationPlans: StateFlow<List<PresentationPlanEntity>> = repository.presentationPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertPresentationPlan(plan: PresentationPlanEntity) {
        viewModelScope.launch {
            repository.insertPresentationPlan(plan)
            _toastEvent.value = "Presentation plan created"
        }
    }

    fun updatePresentationPlan(plan: PresentationPlanEntity) {
        viewModelScope.launch {
            repository.updatePresentationPlan(plan)
            _toastEvent.value = "Presentation plan updated"
        }
    }

    fun togglePresentationStage(plan: PresentationPlanEntity, stageNumber: Int, completed: Boolean) {
        val updated = when (stageNumber) {
            1 -> plan.copy(stage1Completed = completed)
            2 -> plan.copy(stage2Completed = completed)
            3 -> plan.copy(stage3Completed = completed)
            4 -> plan.copy(stage4Completed = completed)
            5 -> plan.copy(stage5Completed = completed)
            else -> plan
        }
        viewModelScope.launch { repository.updatePresentationPlan(updated) }
    }

    fun deletePresentationPlan(plan: PresentationPlanEntity) {
        viewModelScope.launch {
            repository.deletePresentationPlan(plan)
            _toastEvent.value = "Presentation plan deleted"
        }
    }

    // Assignment Breakdown
    private val _activeAssignmentBreakdown = MutableStateFlow<List<AssignmentBreakdownStep>?>(null)
    val activeAssignmentBreakdown: StateFlow<List<AssignmentBreakdownStep>?> = _activeAssignmentBreakdown.asStateFlow()

    fun breakDownAssignment(prompt: String) {
        val steps = IntelligentStudyPlannerEngine.breakDownAssignment(prompt)
        _activeAssignmentBreakdown.value = steps
        _toastEvent.value = "Generated ${steps.size}-step execution breakdown"
    }

    fun dismissAssignmentBreakdown() {
        _activeAssignmentBreakdown.value = null
    }

    fun convertBreakdownStepToTask(step: AssignmentBreakdownStep, courseName: String) {
        viewModelScope.launch {
            repository.insertTask(
                AcademicTaskEntity(
                    courseName = courseName,
                    title = step.title,
                    type = "Assignment",
                    deadline = step.suggestedDay,
                    priority = "High",
                    isCompleted = false,
                    description = step.description,
                    status = "Not Started",
                    estimatedHours = step.estimatedHours
                )
            )
            _toastEvent.value = "Added '${step.title}' to your tasks"
        }
    }

    // Automatic Assignment & Deadline Extraction from uploaded document
    private val _pendingExtractedAssignments = MutableStateFlow<List<ProposedAssignment>?>(null)
    val pendingExtractedAssignments: StateFlow<List<ProposedAssignment>?> = _pendingExtractedAssignments.asStateFlow()

    fun extractAssignmentsFromDoc(text: String, docName: String) {
        val extracted = IntelligentStudyPlannerEngine.extractAssignmentsFromDocument(text, docName)
        _pendingExtractedAssignments.value = extracted
    }

    fun confirmAndSaveExtractedAssignments(list: List<ProposedAssignment>) {
        viewModelScope.launch {
            val entities = list.map { prop ->
                AcademicTaskEntity(
                    courseName = prop.courseName,
                    title = prop.title,
                    type = prop.type,
                    deadline = prop.deadline,
                    priority = prop.priority,
                    isCompleted = false,
                    description = prop.requirements,
                    gradingWeight = prop.gradingWeight,
                    status = "Not Started",
                    estimatedHours = prop.estimatedHours
                )
            }
            repository.insertAllTasks(entities)
            _pendingExtractedAssignments.value = null
            _toastEvent.value = "Added ${entities.size} confirmed deliverables to academic planner"
        }
    }

    fun dismissExtractedAssignments() {
        _pendingExtractedAssignments.value = null
    }

    // Research Projects
    val researchProjects: StateFlow<List<ResearchProjectEntity>> = repository.researchProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedResearchProjectId = MutableStateFlow<Long?>(null)
    val selectedResearchProjectId: StateFlow<Long?> = _selectedResearchProjectId.asStateFlow()

    fun selectResearchProject(id: Long?) {
        _selectedResearchProjectId.value = id
    }

    val activeResearchProject: StateFlow<ResearchProjectEntity?> = combine(
        researchProjects,
        _selectedResearchProjectId
    ) { projects, selectedId ->
        if (projects.isEmpty()) null
        else if (selectedId != null) projects.find { it.id == selectedId } ?: projects.firstOrNull { it.isActive } ?: projects.first()
        else projects.firstOrNull { it.isActive } ?: projects.first()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun insertResearchProject(project: ResearchProjectEntity) {
        viewModelScope.launch {
            val id = repository.insertResearchProject(project)
            _selectedResearchProjectId.value = id
            _toastEvent.value = "Research project saved: ${project.title.take(30)}..."
        }
    }

    fun updateResearchProject(project: ResearchProjectEntity) {
        viewModelScope.launch {
            repository.updateResearchProject(project)
            _toastEvent.value = "Research project updated"
        }
    }

    fun toggleProjectActive(id: Long, isActive: Boolean) {
        viewModelScope.launch {
            repository.setProjectActive(id, isActive)
            _toastEvent.value = if (isActive) "Marked project active" else "Marked project inactive"
        }
    }

    fun deleteResearchProject(project: ResearchProjectEntity) {
        viewModelScope.launch {
            repository.deleteResearchProject(project)
            if (_selectedResearchProjectId.value == project.id) {
                _selectedResearchProjectId.value = null
            }
            _toastEvent.value = "Project removed"
        }
    }

    // Research Milestones
    val allResearchMilestones: StateFlow<List<ResearchMilestoneEntity>> = repository.allMilestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectMilestones: StateFlow<List<ResearchMilestoneEntity>> = combine(
        activeResearchProject,
        allResearchMilestones
    ) { project, all ->
        if (project == null) emptyList()
        else all.filter { it.projectId == project.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertMilestone(milestone: ResearchMilestoneEntity) {
        viewModelScope.launch {
            repository.insertMilestone(milestone)
            _toastEvent.value = "Milestone added: ${milestone.title}"
        }
    }

    fun updateMilestone(milestone: ResearchMilestoneEntity) {
        viewModelScope.launch {
            repository.updateMilestone(milestone)
            _toastEvent.value = "Milestone updated"
        }
    }

    fun toggleMilestone(id: Long, completed: Boolean) {
        viewModelScope.launch {
            val status = if (completed) "Completed" else "In Progress"
            repository.toggleMilestone(id, completed, status)
            _toastEvent.value = "Milestone marked $status"
        }
    }

    fun deleteMilestone(milestone: ResearchMilestoneEntity) {
        viewModelScope.launch {
            repository.deleteMilestone(milestone)
            _toastEvent.value = "Milestone deleted"
        }
    }

    fun syncMilestoneToAcademicPlanner(milestone: ResearchMilestoneEntity, projectTitle: String) {
        viewModelScope.launch {
            repository.insertTask(
                AcademicTaskEntity(
                    courseName = "Research: ${projectTitle.take(24)}",
                    title = milestone.title,
                    type = "Milestone",
                    deadline = milestone.deadline,
                    priority = "High",
                    isCompleted = milestone.isCompleted,
                    description = milestone.notes.ifBlank { "Research milestone for $projectTitle" },
                    status = if (milestone.isCompleted) "Completed" else "In Progress",
                    estimatedHours = 8.0f
                )
            )
            _toastEvent.value = "Synced milestone to Academic Tasks"
        }
    }

    // Research Papers & Literature Matrix
    val allResearchPapers: StateFlow<List<ResearchPaperEntity>> = repository.allPapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectPapers: StateFlow<List<ResearchPaperEntity>> = combine(
        activeResearchProject,
        allResearchPapers
    ) { project, all ->
        if (project == null) all
        else all.filter { it.projectId == project.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertResearchPaper(paper: ResearchPaperEntity) {
        viewModelScope.launch {
            repository.insertPaper(paper)
            _toastEvent.value = "Paper added to Literature Library"
        }
    }

    fun updateResearchPaper(paper: ResearchPaperEntity) {
        viewModelScope.launch {
            repository.updatePaper(paper)
            _toastEvent.value = "Paper details updated"
        }
    }

    fun deleteResearchPaper(paper: ResearchPaperEntity) {
        viewModelScope.launch {
            repository.deletePaper(paper)
            _toastEvent.value = "Paper removed"
        }
    }

    // Paper Analysis
    private val _activePaperAnalysis = MutableStateFlow<ExtractedPaperAnalysis?>(null)
    val activePaperAnalysis: StateFlow<ExtractedPaperAnalysis?> = _activePaperAnalysis.asStateFlow()

    fun analyzePaperFromText(text: String, fileName: String) {
        val analysis = ResearchIntelligenceEngine.analyzeResearchPaper(
            rawText = text,
            fileName = fileName,
            project = activeResearchProject.value
        )
        _activePaperAnalysis.value = analysis
        _toastEvent.value = "Analyzed '${analysis.title.take(30)}...'"
    }

    fun saveAnalyzedPaperToProject(analysis: ExtractedPaperAnalysis, projectId: Long, docId: Long? = null) {
        viewModelScope.launch {
            val entity = ResearchPaperEntity(
                projectId = projectId,
                knowledgeBaseDocId = docId,
                title = analysis.title,
                authors = analysis.authors,
                year = analysis.year,
                venue = analysis.venue,
                doi = analysis.doi,
                url = analysis.url,
                abstractText = analysis.abstractText,
                researchProblem = analysis.researchProblem,
                method = analysis.methodology,
                dataset = analysis.dataset,
                evaluationMetrics = analysis.evaluationMetrics,
                mainFindings = analysis.results,
                limitations = analysis.limitations,
                relevanceToProject = analysis.relevanceToProject,
                researchGapContribution = analysis.mainContribution,
                personalNotes = "Extracted from research document",
                sourceContentSnippet = analysis.sourceContentSnippet,
                aiInterpretationSnippet = analysis.aiInterpretationSnippet,
                bibtex = analysis.bibtex
            )
            repository.insertPaper(entity)
            _activePaperAnalysis.value = null
            _toastEvent.value = "Paper saved to project literature"
        }
    }

    fun dismissPaperAnalysis() {
        _activePaperAnalysis.value = null
    }

    // Research Gap Synthesis
    private val _synthesizedGaps = MutableStateFlow<SynthesizedGapAnalysis?>(null)
    val synthesizedGaps: StateFlow<SynthesizedGapAnalysis?> = _synthesizedGaps.asStateFlow()

    fun synthesizeResearchGapsForProject() {
        val papers = projectPapers.value
        val project = activeResearchProject.value
        val result = ResearchIntelligenceEngine.synthesizeResearchGaps(papers, project)
        _synthesizedGaps.value = result
        _toastEvent.value = "Synthesized research gap matrix from ${papers.size} papers"
    }

    fun dismissSynthesizedGaps() {
        _synthesizedGaps.value = null
    }

    // Research Experiments & Computational Tracking
    val allResearchExperiments: StateFlow<List<ResearchExperimentEntity>> = repository.allExperiments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projectExperiments: StateFlow<List<ResearchExperimentEntity>> = combine(
        activeResearchProject,
        allResearchExperiments
    ) { project, all ->
        if (project == null) all
        else all.filter { it.projectId == project.id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertResearchExperiment(experiment: ResearchExperimentEntity) {
        viewModelScope.launch {
            repository.insertExperiment(experiment)
            _toastEvent.value = "Experiment ${experiment.experimentId} logged"
        }
    }

    fun updateResearchExperiment(experiment: ResearchExperimentEntity) {
        viewModelScope.launch {
            repository.updateExperiment(experiment)
            _toastEvent.value = "Experiment ${experiment.experimentId} updated"
        }
    }

    fun deleteResearchExperiment(experiment: ResearchExperimentEntity) {
        viewModelScope.launch {
            repository.deleteExperiment(experiment)
            _toastEvent.value = "Experiment removed"
        }
    }

    // Manuscript Sections
    val manuscriptSections: StateFlow<List<ManuscriptSectionEntity>> = activeResearchProject
        .flatMapLatest { project ->
            if (project == null) flowOf(emptyList())
            else repository.getManuscriptSections(project.id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateManuscriptSection(section: ManuscriptSectionEntity) {
        viewModelScope.launch {
            repository.updateManuscriptSection(section)
            _toastEvent.value = "Updated Section: ${section.sectionName}"
        }
    }

    fun insertManuscriptSection(section: ManuscriptSectionEntity) {
        viewModelScope.launch {
            repository.insertManuscriptSection(section)
        }
    }

    // Reproducibility Checklist
    val reproducibilityItems: StateFlow<List<ReproducibilityItemEntity>> = activeResearchProject
        .flatMapLatest { project ->
            if (project == null) flowOf(emptyList())
            else repository.getReproducibilityItems(project.id)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleReproducibilityItem(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleReproducibilityItem(id, completed)
        }
    }

    // Advisor Brief
    private val _advisorBrief = MutableStateFlow<AdvisorBrief?>(null)
    val advisorBrief: StateFlow<AdvisorBrief?> = _advisorBrief.asStateFlow()

    fun generateAdvisorBrief(userNotes: String = "") {
        val project = activeResearchProject.value ?: return
        val milestones = projectMilestones.value
        val experiments = projectExperiments.value
        val papers = projectPapers.value

        val brief = ResearchIntelligenceEngine.generateAdvisorBrief(
            project = project,
            milestones = milestones,
            experiments = experiments,
            papers = papers,
            userNotes = userNotes
        )
        _advisorBrief.value = brief
        _toastEvent.value = "Advisor meeting brief generated"
    }

    fun dismissAdvisorBrief() {
        _advisorBrief.value = null
    }

    fun exportProjectBibTeX(): String {
        val project = activeResearchProject.value ?: return "% No active research project selected\n"
        val papers = projectPapers.value
        return ResearchIntelligenceEngine.exportProjectBibliography(project, papers)
    }

    // Skills
    val skills: StateFlow<List<SkillItemEntity>> = repository.skills
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSkill(id: Long, completed: Boolean) {
        viewModelScope.launch { repository.toggleSkill(id, completed) }
    }

    fun insertSkill(skill: SkillItemEntity) {
        viewModelScope.launch {
            repository.insertSkill(skill)
            _toastEvent.value = "Skill added"
        }
    }

    fun updateSkill(skill: SkillItemEntity) {
        viewModelScope.launch {
            repository.updateSkill(skill)
            _toastEvent.value = "Skill updated"
        }
    }

    fun deleteSkill(skill: SkillItemEntity) {
        viewModelScope.launch {
            repository.deleteSkill(skill)
            _toastEvent.value = "Skill removed"
        }
    }

    // Generated Projects
    val generatedProjects: StateFlow<List<GeneratedProjectEntity>> = repository.generatedProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentGeneratedProject = MutableStateFlow<ProjectSpecification?>(null)
    val currentGeneratedProject: StateFlow<ProjectSpecification?> = _currentGeneratedProject.asStateFlow()

    fun generateProject(prompt: String) {
        val spec = ProjectBuilderEngine.generateProject(prompt, userProfile.value)
        _currentGeneratedProject.value = spec
        _toastEvent.value = "Generated blueprint: ${spec.title}"
    }

    fun saveCurrentGeneratedProject() {
        val spec = _currentGeneratedProject.value ?: return
        viewModelScope.launch {
            repository.saveGeneratedProject(ProjectBuilderEngine.toEntity(spec))
            _toastEvent.value = "Project saved to your portfolio"
        }
    }

    fun deleteGeneratedProject(project: GeneratedProjectEntity) {
        viewModelScope.launch {
            repository.deleteGeneratedProject(project)
            _toastEvent.value = "Project removed from portfolio"
        }
    }

    // ==========================================
    // STAGE 5: CHINESE LANGUAGE COACH INTEGRATION
    // ==========================================

    val chinesePhrases: StateFlow<List<ChinesePhraseEntity>> = repository.chinesePhrases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleChineseMastered(id: Long, mastered: Boolean) {
        viewModelScope.launch { repository.toggleChineseMastered(id, mastered) }
    }

    // Chinese Language Profile
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chineseProfile: StateFlow<ChineseLanguageProfileEntity?> = _currentUserEmail
        .flatMapLatest { email -> repository.getChineseProfile(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateChineseProfile(profile: ChineseLanguageProfileEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateChineseProfile(profile)
            _toastEvent.value = "Chinese Language Profile updated"
        }
    }

    fun toggleChineseNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateChineseNotifications(_currentUserEmail.value, enabled)
            _toastEvent.value = if (enabled) "Chinese study reminders enabled" else "Reminders muted"
        }
    }

    // Vocabulary & Spaced Repetition
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chineseVocabulary: StateFlow<List<ChineseVocabularyEntity>> = _currentUserEmail
        .flatMapLatest { email -> repository.getAllChineseVocabulary(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chineseVocabularyDue: StateFlow<List<ChineseVocabularyEntity>> = _currentUserEmail
        .flatMapLatest { email -> repository.getChineseVocabularyDue(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addChineseVocabulary(item: ChineseVocabularyEntity) {
        viewModelScope.launch {
            repository.insertChineseVocabulary(item.copy(userEmail = _currentUserEmail.value))
            _toastEvent.value = "Added '${item.hanzi}' to Vocabulary"
        }
    }

    fun updateChineseVocabulary(item: ChineseVocabularyEntity) {
        viewModelScope.launch {
            repository.updateChineseVocabulary(item)
            _toastEvent.value = "Updated '${item.hanzi}'"
        }
    }

    fun deleteChineseVocabulary(item: ChineseVocabularyEntity) {
        viewModelScope.launch {
            repository.deleteChineseVocabulary(item)
            _toastEvent.value = "Removed '${item.hanzi}'"
        }
    }

    fun toggleChineseVocabFavorite(id: Long, favorite: Boolean) {
        viewModelScope.launch { repository.toggleChineseVocabFavorite(id, favorite) }
    }

    fun toggleChineseVocabKnown(id: Long, known: Boolean) {
        viewModelScope.launch { repository.toggleChineseVocabKnown(id, known) }
    }

    fun toggleChineseVocabDifficult(id: Long, difficult: Boolean) {
        viewModelScope.launch { repository.toggleChineseVocabDifficult(id, difficult) }
    }

    fun reviewVocabularyItem(vocab: ChineseVocabularyEntity, isCorrect: Boolean, difficulty: String = "Medium") {
        viewModelScope.launch {
            val result = ChineseCoachEngine.calculateSpacedRepetition(
                currentReviewCount = vocab.reviewCount,
                currentInterval = vocab.intervalDays,
                currentEase = vocab.easeFactor,
                isCorrect = isCorrect,
                difficulty = difficulty
            )
            repository.updateSpacedRepetition(
                id = vocab.id,
                reviewStatus = result.newReviewStatus,
                lastReviewed = System.currentTimeMillis(),
                nextReviewDate = result.nextReviewDate,
                count = result.newReviewCount,
                correct = vocab.correctAnswers + result.newCorrectCount,
                incorrect = vocab.incorrectAnswers + result.newIncorrectCount,
                interval = result.newIntervalDays,
                ease = result.newEaseFactor
            )
            _toastEvent.value = if (isCorrect) "Recall verified! Next review in ${result.newIntervalDays} days" else "Review marked. Scheduled for tomorrow."
        }
    }

    // Chinese Study Sessions & Integration with Planner
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chineseStudySessions: StateFlow<List<ChineseStudySessionEntity>> = _currentUserEmail
        .flatMapLatest { email -> repository.getChineseStudySessions(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun generateChineseStudyPlan(
        availableMinutes: Int,
        daysPerWeek: Int,
        preferredTime: String,
        currentLevel: String,
        targetLevel: String,
        targetDate: String
    ) {
        viewModelScope.launch {
            val plan = ChineseCoachEngine.generateDailyStudyPlan(
                availableMinutes = availableMinutes,
                daysPerWeek = daysPerWeek,
                preferredTime = preferredTime,
                currentLevel = currentLevel,
                targetLevel = targetLevel,
                targetDate = targetDate,
                userEmail = _currentUserEmail.value
            )
            repository.clearChineseStudySessions(_currentUserEmail.value)
            repository.insertAllChineseStudySessions(plan.sessions)
            _toastEvent.value = "Generated ${plan.sessions.size} Chinese study sessions (${plan.weeklyMinutes} min/wk)"
        }
    }

    fun toggleChineseStudySession(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleChineseStudySessionCompleted(id, isCompleted)
        }
    }

    fun deleteChineseStudySession(session: ChineseStudySessionEntity) {
        viewModelScope.launch {
            repository.deleteChineseStudySession(session)
            _toastEvent.value = "Study session removed"
        }
    }

    fun syncChineseSessionsToMainPlanner() {
        val sessions = chineseStudySessions.value
        if (sessions.isEmpty()) {
            _toastEvent.value = "No Chinese study sessions to sync"
            return
        }
        viewModelScope.launch {
            val converted = sessions.map { cs ->
                val timeParts = cs.timeSlot.split("-").map { it.trim() }
                val start = if (timeParts.isNotEmpty()) timeParts[0] else "08:00"
                val end = if (timeParts.size > 1) timeParts[1] else "08:30"
                StudyPlanSessionEntity(
                    dayOfWeek = cs.dayOfWeek,
                    startTime = start,
                    endTime = end,
                    title = "Chinese: ${cs.title}",
                    category = "Chinese Practice",
                    courseOrTopic = "HSK Coaching & Academic Chinese",
                    isCompleted = cs.isCompleted,
                    notes = cs.description
                )
            }
            repository.insertStudySessions(converted)
            _toastEvent.value = "Synchronized ${converted.size} Chinese study sessions with Academic Study Planner"
        }
    }

    // Conversation Partner Practice
    private val _selectedScenarioId = MutableStateFlow("advisor_meeting")
    val selectedScenarioId: StateFlow<String> = _selectedScenarioId.asStateFlow()

    fun selectConversationScenario(id: String) {
        _selectedScenarioId.value = id
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chineseConversationMessages: StateFlow<List<ChineseConversationMessageEntity>> = combine(
        _currentUserEmail,
        _selectedScenarioId
    ) { email, scenario ->
        email to scenario
    }.flatMapLatest { (email, scenario) ->
        repository.getChineseConversationMessages(email, scenario)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun sendChineseConversationMessage(scenarioId: String, userText: String) {
        if (userText.isBlank()) return
        val email = _currentUserEmail.value
        viewModelScope.launch {
            // Save user message
            repository.insertChineseConversationMessage(
                ChineseConversationMessageEntity(
                    userEmail = email,
                    scenarioId = scenarioId,
                    sender = "USER",
                    hanzi = userText
                )
            )

            // AI partner generates reply with corrections
            val partnerResponse = when (scenarioId) {
                "advisor_meeting" -> {
                    ChineseConversationMessageEntity(
                        userEmail = email,
                        scenarioId = scenarioId,
                        sender = "AI_PARTNER",
                        hanzi = "你的实验数据整理得挺清晰。但论文引言部分，针对雾天激光衰减的物理模型机理，还需要结合 Koschmieder 公式再补充两段论述。",
                        pinyin = "Nǐ de shíyàn shùjù zhěnglǐ de tǐng qīngxī. Dàn lùnwén yǐnyán bùfèn...",
                        english = "Your experimental data is organized quite clearly. However, in the introduction, you need to add two more paragraphs on the physical LiDAR attenuation mechanism using Koschmieder's law.",
                        correctionZh = if (!userText.contains("您") && !userText.contains("老师")) "建议：称呼导师时请使用 '张老师'，代词请使用 '您' (nín) 代替 '你' (nǐ)。" else "",
                        correctionNotes = "In Chinese academic culture, honorifics like '张老师' and '您' are mandatory signifiers of respect."
                    )
                }
                "restaurant" -> {
                    ChineseConversationMessageEntity(
                        userEmail = email,
                        scenarioId = scenarioId,
                        sender = "AI_PARTNER",
                        hanzi = "好的同学，宫保鸡丁盖饭一份，不放香菜少放辣。一共十五块钱，直接扫窗口旁边的二维码支付就行！",
                        pinyin = "Hǎo de tóngxué, gōngbǎo jīdīng gàifàn yí fèn, bú fàng xiāngcài shǎo fàng là...",
                        english = "Alright student, one Kung Pao chicken over rice, no cilantro, less spicy. That's 15 yuan total; just scan the QR code next to the window!",
                        correctionZh = "",
                        correctionNotes = "Natural campus canteen response."
                    )
                }
                "hospital" -> {
                    ChineseConversationMessageEntity(
                        userEmail = email,
                        scenarioId = scenarioId,
                        sender = "AI_PARTNER",
                        hanzi = "请先在这边测一下体温并出示医保卡或护照。三十八度五需要先做血常规化验，化验单半小时后在自助机打印。",
                        pinyin = "Qǐng xiān zài zhèbiān cè yíxià tǐwēn bìng chūshì yībǎokǎ huò hùzhào...",
                        english = "Please take your temperature here and present your medical insurance card or passport. A temperature of 38.5°C requires a routine blood test; results print at the kiosk in 30 mins.",
                        correctionZh = "",
                        correctionNotes = "Standard fever clinic triage protocol."
                    )
                }
                else -> {
                    ChineseConversationMessageEntity(
                        userEmail = email,
                        scenarioId = scenarioId,
                        sender = "AI_PARTNER",
                        hanzi = "收到你的回复了。表达非常清晰自然！我们继续针对下一个环节进行讨论。",
                        pinyin = "Shōudào nǐ de huífù le. Biǎodá fēicháng qīngxī zìrán! ...",
                        english = "Received your reply. Your expression is clear and natural! Let's continue to the next part.",
                        correctionZh = "",
                        correctionNotes = ""
                    )
                }
            }
            repository.insertChineseConversationMessage(partnerResponse)
        }
    }

    fun clearConversation(scenarioId: String) {
        viewModelScope.launch {
            repository.clearChineseConversationMessages(_currentUserEmail.value, scenarioId)
            _toastEvent.value = "Conversation history cleared"
        }
    }

    // Speaking Practice / Transcription Comparison
    private val _speakingEvaluation = MutableStateFlow<ChineseCoachEngine.SpeakingEvaluation?>(null)
    val speakingEvaluation: StateFlow<ChineseCoachEngine.SpeakingEvaluation?> = _speakingEvaluation.asStateFlow()

    fun evaluateSpeaking(expected: String, transcribed: String) {
        val eval = ChineseCoachEngine.evaluateSpeechTranscription(expected, transcribed)
        _speakingEvaluation.value = eval
    }

    fun clearSpeakingEvaluation() {
        _speakingEvaluation.value = null
    }

    // Listening Exercises
    val chineseListeningExercises: StateFlow<List<ChineseListeningExerciseEntity>> = repository.chineseListeningExercises
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun submitListeningAnswer(exerciseId: Long, selectedIndex: Int) {
        viewModelScope.launch {
            val exercise = chineseListeningExercises.value.find { it.id == exerciseId } ?: return@launch
            val isCorrect = selectedIndex == exercise.correctOptionIndex
            repository.answerChineseListeningExercise(exerciseId, selectedIndex, isCorrect)
            _toastEvent.value = if (isCorrect) "Correct answer!" else "Incorrect. Check the explanation."
        }
    }

    // Grammar Points
    val chineseGrammarPoints: StateFlow<List<ChineseGrammarPointEntity>> = repository.chineseGrammarPoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly Progress
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val chineseWeeklyProgress: StateFlow<ChineseWeeklyProgressEntity?> = _currentUserEmail
        .flatMapLatest { email -> repository.getChineseWeeklyProgress(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Translation Assistant
    private val _translationResult = MutableStateFlow<ChineseCoachEngine.TranslationResult?>(null)
    val translationResult: StateFlow<ChineseCoachEngine.TranslationResult?> = _translationResult.asStateFlow()

    fun translateChineseText(input: String, direction: String, formality: String) {
        val res = ChineseCoachEngine.translateAndExplain(input, direction, formality)
        _translationResult.value = res
    }

    fun clearTranslationResult() {
        _translationResult.value = null
    }

    // Document-based Chinese Extraction
    private val _extractedDocumentChinese = MutableStateFlow<List<ChineseCoachEngine.ExtractedChineseItem>>(emptyList())
    val extractedDocumentChinese: StateFlow<List<ChineseCoachEngine.ExtractedChineseItem>> = _extractedDocumentChinese.asStateFlow()

    fun extractChineseFromDocument(doc: PersonalDocumentEntity) {
        val items = ChineseCoachEngine.extractChineseFromDocument(
            documentContent = doc.content,
            documentName = doc.fileName,
            category = doc.category
        )
        _extractedDocumentChinese.value = items
        _toastEvent.value = if (items.isNotEmpty()) "Extracted ${items.size} Chinese vocabulary items from '${doc.fileName}'" else "No matching Chinese vocabulary found in document"
    }

    fun saveExtractedChineseWord(item: ChineseCoachEngine.ExtractedChineseItem) {
        viewModelScope.launch {
            val entity = ChineseVocabularyEntity(
                userEmail = _currentUserEmail.value,
                hanzi = item.hanzi,
                pinyin = item.pinyin,
                english = item.english,
                exampleSentenceZh = item.contextSentence,
                exampleSentenceEn = "Extracted from '${item.sourceDocument}'",
                category = item.category,
                source = "Document: ${item.sourceDocument}",
                difficulty = "Medium",
                hskLevel = "HSK 4",
                reviewStatus = "Due",
                nextReviewDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            )
            repository.insertChineseVocabulary(entity)
            _toastEvent.value = "Saved '${item.hanzi}' (${item.pinyin}) to Vocabulary"
        }
    }

    fun clearExtractedChinese() {
        _extractedDocumentChinese.value = emptyList()
    }

    // Weakness Analysis
    private val _weaknessReport = MutableStateFlow<ChineseCoachEngine.WeaknessAnalysisReport?>(null)
    val weaknessReport: StateFlow<ChineseCoachEngine.WeaknessAnalysisReport?> = _weaknessReport.asStateFlow()

    fun refreshWeaknessAnalysis() {
        val report = ChineseCoachEngine.analyzeLearningWeaknesses(
            profile = chineseProfile.value,
            vocabList = chineseVocabulary.value,
            listeningExercises = chineseListeningExercises.value
        )
        _weaknessReport.value = report
    }

    // AI Chinese Tutor
    private val _tutorResponse = MutableStateFlow<ChineseCoachEngine.TutorResponse?>(null)
    val tutorResponse: StateFlow<ChineseCoachEngine.TutorResponse?> = _tutorResponse.asStateFlow()

    fun queryChineseTutor(command: String) {
        val res = ChineseCoachEngine.handleTutorCommand(command, chineseProfile.value, _currentUserEmail.value)
        _tutorResponse.value = res
    }

    fun clearTutorResponse() {
        _tutorResponse.value = null
    }

    fun saveTutorVocabulary(words: List<ChineseVocabularyEntity>) {
        viewModelScope.launch {
            repository.insertAllChineseVocabulary(words)
            _toastEvent.value = "Saved ${words.size} vocabulary items to your learning deck"
        }
    }


    // Knowledge Base
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val knowledgeItems: StateFlow<List<KnowledgeItemEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.knowledgeItems
            else repository.searchKnowledge(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertKnowledge(item: KnowledgeItemEntity) {
        viewModelScope.launch {
            repository.insertKnowledge(item)
            _toastEvent.value = "Note added to Knowledge Base"
        }
    }

    fun deleteKnowledge(item: KnowledgeItemEntity) {
        viewModelScope.launch {
            repository.deleteKnowledge(item)
            _toastEvent.value = "Note deleted"
        }
    }

    fun updateKnowledge(item: KnowledgeItemEntity) {
        viewModelScope.launch {
            repository.insertKnowledge(item)
            _toastEvent.value = "Knowledge note updated"
        }
    }

    // Roadmap Goals
    val roadmapGoals: StateFlow<List<RoadmapGoalEntity>> = repository.roadmapGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleGoal(id: Long, completed: Boolean) {
        viewModelScope.launch { repository.toggleGoal(id, completed) }
    }

    fun insertGoal(goal: RoadmapGoalEntity) {
        viewModelScope.launch {
            repository.insertGoal(goal)
            _toastEvent.value = "Goal saved"
        }
    }

    fun updateGoal(goal: RoadmapGoalEntity) {
        viewModelScope.launch {
            repository.insertGoal(goal)
            _toastEvent.value = "Goal updated"
        }
    }

    fun deleteGoal(goal: RoadmapGoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
            _toastEvent.value = "Goal removed"
        }
    }

    // AI Mentor Chat
    private val _activeMentorMode = MutableStateFlow(MentorMode.PROFESSOR)
    val activeMentorMode: StateFlow<MentorMode> = _activeMentorMode.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessageEntity>> = _activeMentorMode
        .flatMapLatest { mode -> repository.getMessagesForMode(mode.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _lastMentorResponse = MutableStateFlow<MentorResponse?>(null)
    val lastMentorResponse: StateFlow<MentorResponse?> = _lastMentorResponse.asStateFlow()

    fun setMentorMode(mode: MentorMode) {
        _activeMentorMode.value = mode
    }

    fun sendMentorMessage(userText: String, documentContext: String? = null) {
        if (userText.isBlank()) return
        val mode = _activeMentorMode.value
        val profile = userProfile.value

        viewModelScope.launch {
            repository.insertChatMessage(
                ChatMessageEntity(
                    modeId = mode.id,
                    sender = "user",
                    content = userText,
                    factType = "USER_QUERY"
                )
            )

            _isGenerating.value = true
            val response = GeminiApiClient.consultMentor(mode, userText, profile, documentContext)
            _lastMentorResponse.value = response

            repository.insertChatMessage(
                ChatMessageEntity(
                    modeId = mode.id,
                    sender = "mentor",
                    content = response.rawText,
                    factType = if (response.verifiedFacts.isNotEmpty()) "VERIFIED_FACT" else "AI_SUGGESTION"
                )
            )
            _isGenerating.value = false
        }
    }

    fun clearChat(modeId: String) {
        viewModelScope.launch {
            repository.clearChat(modeId)
            _toastEvent.value = "Conversation cleared"
        }
    }

    // Career Center & Job Assistant (Stage 4)
    val careerProfile: StateFlow<CareerProfileEntity?> = repository.careerProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateCareerProfile(profile: CareerProfileEntity) {
        viewModelScope.launch {
            repository.updateCareerProfile(profile)
            _toastEvent.value = "Career profile updated"
        }
    }

    val careerGoals: StateFlow<List<CareerGoalEntity>> = repository.careerGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertCareerGoal(goal: CareerGoalEntity) {
        viewModelScope.launch {
            repository.insertCareerGoal(goal)
            _toastEvent.value = "Added career goal: ${goal.targetRole}"
        }
    }

    fun updateCareerGoal(goal: CareerGoalEntity) {
        viewModelScope.launch {
            repository.updateCareerGoal(goal)
            _toastEvent.value = "Career goal updated"
        }
    }

    fun deleteCareerGoal(goal: CareerGoalEntity) {
        viewModelScope.launch {
            repository.deleteCareerGoal(goal)
            _toastEvent.value = "Career goal removed"
        }
    }

    val skillInventory: StateFlow<List<SkillInventoryEntity>> = repository.skillInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertSkillInventory(skill: SkillInventoryEntity) {
        viewModelScope.launch {
            repository.insertSkillInventory(skill)
            _toastEvent.value = "Added skill: ${skill.skillName}"
        }
    }

    fun updateSkillInventory(skill: SkillInventoryEntity) {
        viewModelScope.launch {
            repository.updateSkillInventory(skill)
            _toastEvent.value = "Skill updated: ${skill.skillName}"
        }
    }

    fun deleteSkillInventory(skill: SkillInventoryEntity) {
        viewModelScope.launch {
            repository.deleteSkillInventory(skill)
            _toastEvent.value = "Skill removed from inventory"
        }
    }

    // Job Description Analyzer
    private val _parsedJobDescription = MutableStateFlow<ParsedJobDescription?>(null)
    val parsedJobDescription: StateFlow<ParsedJobDescription?> = _parsedJobDescription.asStateFlow()

    fun analyzeJobDescriptionText(rawText: String) {
        val parsed = CareerIntelligenceEngine.analyzeJobDescription(rawText)
        _parsedJobDescription.value = parsed
        _toastEvent.value = "Analyzed job posting: ${parsed.jobTitle}"
    }

    fun clearParsedJob() {
        _parsedJobDescription.value = null
    }

    fun saveParsedJobAsPosting(
        job: ParsedJobDescription,
        country: String = "China",
        remote: String = "On-site",
        industry: String = "Technology & AI"
    ) {
        viewModelScope.launch {
            val entity = JobPostingEntity(
                jobTitle = job.jobTitle,
                company = job.company,
                location = job.location,
                country = country,
                remotePolicy = remote,
                industry = industry,
                requiredDegree = job.requiredDegree,
                requiredExperience = job.requiredExperience,
                requiredSkills = job.requiredSkills.joinToString(", "),
                preferredSkills = job.preferredSkills.joinToString(", "),
                programmingLanguages = job.programmingLanguages.joinToString(", "),
                frameworks = job.frameworks.joinToString(", "),
                tools = job.tools.joinToString(", "),
                researchRequirements = job.researchRequirements,
                languageRequirements = job.languageRequirements,
                responsibilities = job.responsibilities.joinToString("\n• "),
                applicationDeadline = job.applicationDeadline,
                salary = job.salary,
                source = "Parsed from Job Description",
                dateChecked = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(java.util.Date()),
                isVerified = true,
                rawDescription = "",
                isSaved = true
            )
            repository.insertJobPosting(entity)
            _toastEvent.value = "Saved '${job.jobTitle}' to Verified Jobs"
        }
    }

    // Career Gap Analyzer
    private val _careerGapReport = MutableStateFlow<CareerGapReport?>(null)
    val careerGapReport: StateFlow<CareerGapReport?> = _careerGapReport.asStateFlow()

    fun analyzeGapForJob(job: ParsedJobDescription) {
        val report = CareerIntelligenceEngine.analyzeCareerGap(
            job = job,
            profile = careerProfile.value,
            skills = skillInventory.value
        )
        _careerGapReport.value = report
        _toastEvent.value = "Gap analysis generated for ${job.jobTitle}"
    }

    fun clearGapReport() {
        _careerGapReport.value = null
    }

    // Skill Development Plans
    val skillDevelopmentPlans: StateFlow<List<SkillDevelopmentPlanEntity>> = repository.skillDevelopmentPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertSkillPlan(plan: SkillDevelopmentPlanEntity) {
        viewModelScope.launch {
            repository.insertSkillPlan(plan)
            _toastEvent.value = "Development plan added: ${plan.skillName}"
        }
    }

    fun updateSkillPlan(plan: SkillDevelopmentPlanEntity) {
        viewModelScope.launch {
            repository.updateSkillPlan(plan)
            _toastEvent.value = "Plan updated: ${plan.skillName}"
        }
    }

    fun updateSkillPlanStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateSkillPlanStatus(id, status)
            _toastEvent.value = "Plan status: $status"
        }
    }

    fun deleteSkillPlan(plan: SkillDevelopmentPlanEntity) {
        viewModelScope.launch {
            repository.deleteSkillPlan(plan)
            _toastEvent.value = "Plan deleted"
        }
    }

    fun syncSkillPlanToStudyPlanner(plan: SkillDevelopmentPlanEntity) {
        viewModelScope.launch {
            repository.insertTask(
                AcademicTaskEntity(
                    courseName = "Career Skill: ${plan.skillName}",
                    title = "Practice: ${plan.practiceTask.take(45)}",
                    type = "CareerPrep",
                    deadline = plan.targetDate,
                    priority = "High",
                    isCompleted = plan.status == "Completed",
                    description = "Project: ${plan.suggestedProject}. Why: ${plan.whyItMatters}",
                    status = plan.status,
                    estimatedHours = 6.0f
                )
            )
            _toastEvent.value = "Synced skill plan to University Planner"
        }
    }

    // CV / Resume Assistant
    private val _resumeAnalysis = MutableStateFlow<ResumeAnalysisReport?>(null)
    val resumeAnalysis: StateFlow<ResumeAnalysisReport?> = _resumeAnalysis.asStateFlow()

    fun analyzeResumeText(cvText: String) {
        val analysis = CareerIntelligenceEngine.analyzeResume(cvText)
        _resumeAnalysis.value = analysis
        _toastEvent.value = "CV audit complete (${analysis.skillsIdentified.size} skills detected)"
    }

    fun clearResumeAnalysis() {
        _resumeAnalysis.value = null
    }

    private val _jobSpecificCvMatch = MutableStateFlow<JobSpecificCvMatchReport?>(null)
    val jobSpecificCvMatch: StateFlow<JobSpecificCvMatchReport?> = _jobSpecificCvMatch.asStateFlow()

    fun analyzeJobSpecificCV(cvText: String, job: ParsedJobDescription) {
        val match = CareerIntelligenceEngine.analyzeJobSpecificCV(cvText, job)
        _jobSpecificCvMatch.value = match
        _toastEvent.value = "Targeted CV audit generated for ${job.jobTitle}"
    }

    fun clearJobSpecificCvMatch() {
        _jobSpecificCvMatch.value = null
    }

    // Interview Preparation
    val interviewQuestions: StateFlow<List<InterviewPrepQuestionEntity>> = repository.interviewQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeInterviewFeedback = MutableStateFlow<InterviewEvaluationResult?>(null)
    val activeInterviewFeedback: StateFlow<InterviewEvaluationResult?> = _activeInterviewFeedback.asStateFlow()

    fun insertInterviewQuestion(q: InterviewPrepQuestionEntity) {
        viewModelScope.launch {
            repository.insertInterviewQuestion(q)
            _toastEvent.value = "Added interview question"
        }
    }

    fun updateInterviewQuestion(q: InterviewPrepQuestionEntity) {
        viewModelScope.launch {
            repository.updateInterviewQuestion(q)
            _toastEvent.value = "Updated answer & feedback"
        }
    }

    fun deleteInterviewQuestion(q: InterviewPrepQuestionEntity) {
        viewModelScope.launch {
            repository.deleteInterviewQuestion(q)
            _toastEvent.value = "Question deleted"
        }
    }

    fun evaluateInterviewResponse(
        question: String,
        userAnswer: String,
        category: String,
        projectContext: String? = null
    ) {
        val eval = CareerIntelligenceEngine.evaluateInterviewAnswer(
            question = question,
            userAnswer = userAnswer,
            category = category,
            projectContext = projectContext
        )
        _activeInterviewFeedback.value = eval
    }

    fun clearInterviewFeedback() {
        _activeInterviewFeedback.value = null
    }

    fun generateProjectInterviewQuestions(project: ResearchProjectEntity) {
        viewModelScope.launch {
            val questions = listOf(
                InterviewPrepQuestionEntity(
                    category = "Research",
                    targetRoleOrProject = project.title,
                    question = "Explain your '${project.title}' project. What was the central research gap you targeted?",
                    userAnswer = "",
                    aiFeedback = "",
                    improvedAnswer = "Ground your response in: Research Problem: ${project.researchProblem.take(120)}... Research Gap: ${project.researchGap.take(120)}.",
                    isPracticed = false
                ),
                InterviewPrepQuestionEntity(
                    category = "Computer Vision",
                    targetRoleOrProject = project.title,
                    question = "Why did you select '${project.modelsAlgorithms.take(60)}' and what were your baselines?",
                    userAnswer = "",
                    aiFeedback = "",
                    improvedAnswer = "Reference baseline comparison: ${project.baselines.take(100)}. Detail why your architecture addresses the dataset constraints (${project.datasets.take(80)}).",
                    isPracticed = false
                ),
                InterviewPrepQuestionEntity(
                    category = "Technical",
                    targetRoleOrProject = project.title,
                    question = "How did you prevent data leakage and evaluate your models on '${project.metrics.take(50)}'?",
                    userAnswer = "",
                    aiFeedback = "",
                    improvedAnswer = "Explain sequence-isolated train/validation splitting, seed fixing, and evaluation under adverse metrics: ${project.metrics}.",
                    isPracticed = false
                )
            )
            repository.insertInterviewQuestions(questions)
            _toastEvent.value = "Generated 3 project-grounded questions for '${project.title.take(20)}...'"
        }
    }

    // Job Postings & International Job Search
    val jobPostings: StateFlow<List<JobPostingEntity>> = repository.jobPostings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedJobPostings: StateFlow<List<JobPostingEntity>> = repository.savedJobPostings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // International filters
    private val _jobFilterCountry = MutableStateFlow("ALL")
    val jobFilterCountry: StateFlow<String> = _jobFilterCountry.asStateFlow()

    private val _jobFilterRemote = MutableStateFlow("ALL")
    val jobFilterRemote: StateFlow<String> = _jobFilterRemote.asStateFlow()

    private val _jobFilterSearch = MutableStateFlow("")
    val jobFilterSearch: StateFlow<String> = _jobFilterSearch.asStateFlow()

    fun setJobFilterCountry(country: String) { _jobFilterCountry.value = country }
    fun setJobFilterRemote(policy: String) { _jobFilterRemote.value = policy }
    fun setJobFilterSearch(query: String) { _jobFilterSearch.value = query }

    val filteredJobPostings: StateFlow<List<JobPostingEntity>> = combine(
        jobPostings,
        _jobFilterCountry,
        _jobFilterRemote,
        _jobFilterSearch
    ) { jobs, country, remote, search ->
        jobs.filter { job ->
            val matchCountry = country == "ALL" || job.country.contains(country, ignoreCase = true)
            val matchRemote = remote == "ALL" || job.remotePolicy.contains(remote, ignoreCase = true)
            val matchSearch = search.isBlank() ||
                job.jobTitle.contains(search, ignoreCase = true) ||
                job.company.contains(search, ignoreCase = true) ||
                job.location.contains(search, ignoreCase = true) ||
                job.requiredSkills.contains(search, ignoreCase = true)
            matchCountry && matchRemote && matchSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSaveJob(id: Long, isSaved: Boolean) {
        viewModelScope.launch {
            repository.toggleSaveJob(id, isSaved)
            _toastEvent.value = if (isSaved) "Job saved to bookmarks" else "Job un-saved"
        }
    }

    fun insertJobPosting(job: JobPostingEntity) {
        viewModelScope.launch {
            repository.insertJobPosting(job)
            _toastEvent.value = "Added job posting: ${job.jobTitle}"
        }
    }

    fun deleteJobPosting(job: JobPostingEntity) {
        viewModelScope.launch {
            repository.deleteJobPosting(job)
            _toastEvent.value = "Job posting removed"
        }
    }

    // Job Applications Tracker
    val jobApplications: StateFlow<List<JobApplicationEntity>> = repository.jobApplications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun insertJobApplication(app: JobApplicationEntity) {
        viewModelScope.launch {
            repository.insertJobApplication(app)
            _toastEvent.value = "Application tracked: ${app.company} (${app.position})"
        }
    }

    fun updateJobApplication(app: JobApplicationEntity) {
        viewModelScope.launch {
            repository.updateJobApplication(app)
            _toastEvent.value = "Application updated: ${app.company}"
        }
    }

    fun deleteJobApplication(app: JobApplicationEntity) {
        viewModelScope.launch {
            repository.deleteJobApplication(app)
            _toastEvent.value = "Application removed"
        }
    }

    // Career Roadmap Milestones
    val careerRoadmapMilestones: StateFlow<List<CareerRoadmapMilestoneEntity>> = repository.careerRoadmapMilestones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleRoadmapMilestone(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleRoadmapMilestone(id, completed)
            _toastEvent.value = if (completed) "Milestone marked completed" else "Milestone in progress"
        }
    }

    fun insertRoadmapMilestone(m: CareerRoadmapMilestoneEntity) {
        viewModelScope.launch {
            repository.insertRoadmapMilestone(m)
            _toastEvent.value = "Added roadmap milestone: ${m.title}"
        }
    }

    fun deleteRoadmapMilestone(m: CareerRoadmapMilestoneEntity) {
        viewModelScope.launch {
            repository.deleteRoadmapMilestone(m)
            _toastEvent.value = "Milestone removed"
        }
    }

    fun syncRoadmapMilestoneToStudyPlanner(m: CareerRoadmapMilestoneEntity) {
        viewModelScope.launch {
            repository.insertTask(
                AcademicTaskEntity(
                    courseName = "Career Roadmap: ${m.stage}",
                    title = m.title,
                    type = "CareerMilestone",
                    deadline = m.targetDate,
                    priority = "High",
                    isCompleted = m.isCompleted,
                    description = m.notes.ifBlank { "Career milestone: ${m.title}" },
                    status = if (m.isCompleted) "Completed" else "In Progress",
                    estimatedHours = 10.0f
                )
            )
            _toastEvent.value = "Synced roadmap milestone to University Planner"
        }
    }

    // Legacy Career Center Target Role
    private val _selectedRole = MutableStateFlow(CareerAnalyzerEngine.TARGET_ROLES.first().title)
    val selectedRole: StateFlow<String> = _selectedRole.asStateFlow()

    val careerAnalysis: StateFlow<CareerAnalysisResult?> = combine(_selectedRole, userProfile) { role, profile ->
        CareerAnalyzerEngine.analyzeRole(role, profile)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setSelectedRole(roleTitle: String) {
        _selectedRole.value = roleTitle
    }

    // Document Intelligence
    private val _docAnalysisResult = MutableStateFlow<DocumentAnalysisResult?>(null)
    val docAnalysisResult: StateFlow<DocumentAnalysisResult?> = _docAnalysisResult.asStateFlow()

    fun analyzeDocumentContent(title: String, content: String) {
        val result = DocumentIntelligenceEngine.analyzeDocument(title, content)
        _docAnalysisResult.value = result
        _toastEvent.value = "Analyzed document: $title"
    }

    fun convertExtractedDeadlinesToTasks() {
        val result = _docAnalysisResult.value ?: return
        val tasksToInsert = DocumentIntelligenceEngine.toAcademicTasks(result)
        viewModelScope.launch {
            for (t in tasksToInsert) {
                repository.insertTask(t)
            }
            _toastEvent.value = "Added ${tasksToInsert.size} extracted tasks to University Planner"
        }
    }

    // Personal Audit ("What Am I Missing?")
    val auditReport: StateFlow<PersonalAuditEngine.AuditReport> = userProfile
        .map { profile -> PersonalAuditEngine.runAudit(profile) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PersonalAuditEngine.runAudit(null)
        )

    // Future Self
    private val _futureSelfAnswer = MutableStateFlow<String?>(null)
    val futureSelfAnswer: StateFlow<String?> = _futureSelfAnswer.asStateFlow()

    fun askFutureSelf(question: String) {
        val ans = FutureSelfEngine.askFutureSelf(question, userProfile.value)
        _futureSelfAnswer.value = ans
    }

    fun clearFutureSelf() {
        _futureSelfAnswer.value = null
    }

    // Personal Document Library & "My Knowledge"
    private val _docCategoryFilter = MutableStateFlow("ALL")
    val docCategoryFilter: StateFlow<String> = _docCategoryFilter.asStateFlow()

    fun setDocCategoryFilter(cat: String) {
        _docCategoryFilter.value = cat
    }

    private val _docSearchQuery = MutableStateFlow("")
    val docSearchQuery: StateFlow<String> = _docSearchQuery.asStateFlow()

    fun setDocSearchQuery(query: String) {
        _docSearchQuery.value = query
    }

    val userDocuments: StateFlow<List<PersonalDocumentEntity>> = combine(
        _currentUserEmail,
        _docCategoryFilter,
        _docSearchQuery
    ) { email, category, query ->
        Triple(email, category, query)
    }.flatMapLatest { (email, category, query) ->
        val effectiveEmail = email.ifBlank { "alexei.chen@ysu.edu.cn" }
        if (query.isNotBlank()) {
            repository.searchDocuments(effectiveEmail, query)
        } else if (category != "ALL") {
            repository.getDocumentsByCategory(effectiveEmail, category)
        } else {
            repository.getDocumentsForUser(effectiveEmail)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun uploadDocument(
        fileName: String,
        fileType: String,
        category: String,
        content: String,
        tags: String,
        notes: String,
        courseOrProject: String,
        isBinaryUnsupported: Boolean = false,
        fileUriString: String = ""
    ) {
        viewModelScope.launch {
            val effectiveEmail = _currentUserEmail.value.ifBlank { "alexei.chen@ysu.edu.cn" }
            val doc = PersonalDocumentEntity(
                userEmail = effectiveEmail,
                fileName = fileName,
                fileType = fileType.uppercase(),
                category = category.uppercase(),
                uploadDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
                tags = tags,
                userNotes = notes,
                courseOrProject = courseOrProject,
                content = content,
                isBinaryUnsupported = isBinaryUnsupported,
                fileUriString = fileUriString
            )
            repository.insertDocument(doc)
            _toastEvent.value = "Saved '$fileName' to $category"
        }
    }

    fun updateDocument(doc: PersonalDocumentEntity) {
        viewModelScope.launch {
            repository.updateDocument(doc)
            _toastEvent.value = "Updated '${doc.fileName}'"
        }
    }

    fun deleteDocument(doc: PersonalDocumentEntity) {
        viewModelScope.launch {
            repository.deleteDocument(doc)
            _toastEvent.value = "Deleted '${doc.fileName}'"
        }
    }

    // Structured Summary
    private val _activeDocSummary = MutableStateFlow<StructuredDocumentSummary?>(null)
    val activeDocSummary: StateFlow<StructuredDocumentSummary?> = _activeDocSummary.asStateFlow()

    fun summarizeDocument(doc: PersonalDocumentEntity) {
        val summary = DocumentIntelligenceEngine.generateStructuredSummary(doc)
        _activeDocSummary.value = summary
        _toastEvent.value = "Generated summary for ${doc.fileName}"
    }

    fun clearActiveDocSummary() {
        _activeDocSummary.value = null
    }

    // Ask My Documents
    private val _askDocumentsResult = MutableStateFlow<AskDocumentsResponse?>(null)
    val askDocumentsResult: StateFlow<AskDocumentsResponse?> = _askDocumentsResult.asStateFlow()

    fun askMyDocuments(query: String) {
        if (query.isBlank()) return
        val currentDocs = userDocuments.value
        val result = DocumentIntelligenceEngine.askMyDocuments(query, currentDocs, userProfile.value)
        _askDocumentsResult.value = result
    }

    fun clearAskDocumentsResult() {
        _askDocumentsResult.value = null
    }

    fun createTaskFromAction(action: ExtractedAction) {
        viewModelScope.launch {
            repository.insertTask(
                AcademicTaskEntity(
                    courseName = action.courseOrProject,
                    title = action.title,
                    type = "Assignment",
                    deadline = action.dueDate,
                    priority = action.priority,
                    isCompleted = false,
                    description = "Extracted from '${action.sourceDocument}'"
                )
            )
            _toastEvent.value = "Task created: ${action.title}"
        }
    }

    // ========================================================
    // STAGE 6: CHINA STUDENT -> GRADUATION -> WORK & IMMIGRATION
    // ========================================================

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val immigrationProfile: StateFlow<ImmigrationProfileEntity?> = _currentUserEmail
        .flatMapLatest { email -> repository.getImmigrationProfile(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateImmigrationProfile(profile: ImmigrationProfileEntity) {
        viewModelScope.launch {
            repository.insertOrUpdateImmigrationProfile(profile.copy(userEmail = _currentUserEmail.value, lastUpdated = System.currentTimeMillis()))
            _toastEvent.value = "Immigration Profile updated"
        }
    }

    // Official Sources
    private val _jurisdictionFilter = MutableStateFlow("ALL")
    val jurisdictionFilter: StateFlow<String> = _jurisdictionFilter.asStateFlow()

    fun setJurisdictionFilter(jur: String) {
        _jurisdictionFilter.value = jur
    }

    val officialSources: StateFlow<List<OfficialSourceEntity>> = repository.officialSources
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // City Policies
    private val _cityPolicyFilter = MutableStateFlow("ALL")
    val cityPolicyFilter: StateFlow<String> = _cityPolicyFilter.asStateFlow()

    fun setCityPolicyFilter(city: String) {
        _cityPolicyFilter.value = city
    }

    val cityImmigrationPolicies: StateFlow<List<CityImmigrationPolicyEntity>> = repository.cityImmigrationPolicies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCityPolicy(policy: CityImmigrationPolicyEntity) {
        viewModelScope.launch {
            repository.insertCityPolicy(policy)
            _toastEvent.value = "Added policy for ${policy.city}"
        }
    }

    // Graduation Checklist
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val graduationChecklist: StateFlow<List<GraduationChecklistItemEntity>> = _currentUserEmail
        .flatMapLatest { email -> repository.getGraduationChecklist(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addGraduationChecklistItem(item: GraduationChecklistItemEntity) {
        viewModelScope.launch {
            repository.insertGraduationChecklistItem(item.copy(userEmail = _currentUserEmail.value))
            _toastEvent.value = "Checklist item added: ${item.title}"
        }
    }

    fun toggleGraduationChecklistItem(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleGraduationChecklistItem(id, completed)
        }
    }

    fun deleteGraduationChecklistItem(item: GraduationChecklistItemEntity) {
        viewModelScope.launch {
            repository.deleteGraduationChecklistItem(item)
            _toastEvent.value = "Removed checklist item"
        }
    }

    // Immigration Documents Organizer
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val immigrationDocuments: StateFlow<List<ImmigrationDocumentEntity>> = _currentUserEmail
        .flatMapLatest { email -> repository.getImmigrationDocuments(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addImmigrationDocument(doc: ImmigrationDocumentEntity) {
        viewModelScope.launch {
            repository.insertImmigrationDocument(doc.copy(userEmail = _currentUserEmail.value))
            _toastEvent.value = "Saved '${doc.documentName}' to Document Organizer"
        }
    }

    fun updateImmigrationDocumentStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateImmigrationDocumentStatus(id, status)
            _toastEvent.value = "Document status updated to $status"
        }
    }

    fun deleteImmigrationDocument(doc: ImmigrationDocumentEntity) {
        viewModelScope.launch {
            repository.deleteImmigrationDocument(doc)
            _toastEvent.value = "Removed '${doc.documentName}'"
        }
    }

    // Official Contacts
    val officialContacts: StateFlow<List<OfficialContactEntity>> = repository.officialContacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Personal Immigration Reminders
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val immigrationReminders: StateFlow<List<ImmigrationReminderEntity>> = _currentUserEmail
        .flatMapLatest { email -> repository.getActiveImmigrationReminders(email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun dismissImmigrationReminder(id: Long) {
        viewModelScope.launch {
            repository.dismissImmigrationReminder(id)
            _toastEvent.value = "Reminder dismissed"
        }
    }

    fun addImmigrationReminder(reminder: ImmigrationReminderEntity) {
        viewModelScope.launch {
            repository.insertImmigrationReminder(reminder.copy(userEmail = _currentUserEmail.value))
            _toastEvent.value = "Personal reminder scheduled"
        }
    }

    fun deleteImmigrationReminder(reminder: ImmigrationReminderEntity) {
        viewModelScope.launch {
            repository.deleteImmigrationReminder(reminder)
        }
    }

    // Source-first Q&A Assistant
    private val _structuredImmigrationAnswer = MutableStateFlow<ImmigrationIntelligenceEngine.StructuredImmigrationAnswer?>(null)
    val structuredImmigrationAnswer: StateFlow<ImmigrationIntelligenceEngine.StructuredImmigrationAnswer?> = _structuredImmigrationAnswer.asStateFlow()

    fun askImmigrationAssistant(query: String) {
        if (query.isBlank()) return
        val answer = ImmigrationIntelligenceEngine.answerImmigrationQuestion(query)
        _structuredImmigrationAnswer.value = answer
    }

    fun clearImmigrationAnswer() {
        _structuredImmigrationAnswer.value = null
    }

    // Job + Immigration Bridge (Stage 4 Career Assistant <-> Stage 6 China Work)
    private val _jobImmigrationAnalysis = MutableStateFlow<ImmigrationIntelligenceEngine.JobImmigrationAnalysis?>(null)
    val jobImmigrationAnalysis: StateFlow<ImmigrationIntelligenceEngine.JobImmigrationAnalysis?> = _jobImmigrationAnalysis.asStateFlow()

    fun analyzeJobImmigration(job: JobPostingEntity) {
        val analysis = ImmigrationIntelligenceEngine.analyzeJobImmigrationFit(job, immigrationProfile.value)
        _jobImmigrationAnalysis.value = analysis
    }

    fun clearJobImmigrationAnalysis() {
        _jobImmigrationAnalysis.value = null
    }

    // Research + Career + China Bridge
    val researchCareerChinaBridge: StateFlow<ImmigrationIntelligenceEngine.ResearchCareerChinaBridge> = combine(
        activeResearchProject,
        careerProfile,
        immigrationProfile
    ) { proj, career, imm ->
        ImmigrationIntelligenceEngine.buildResearchCareerChinaBridge(proj, career, imm)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ImmigrationIntelligenceEngine.buildResearchCareerChinaBridge(null, null, null)
    )

    // Expiration Audits for Passport and Residence Permit
    val passportExpirationAudit: StateFlow<ImmigrationIntelligenceEngine.ExpirationAudit> = immigrationProfile
        .map { profile ->
            ImmigrationIntelligenceEngine.auditExpiration(
                profile?.passportExpirationDate ?: "Not provided",
                "Passport"
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ImmigrationIntelligenceEngine.auditExpiration("Not provided", "Passport")
        )

    val residencePermitExpirationAudit: StateFlow<ImmigrationIntelligenceEngine.ExpirationAudit> = immigrationProfile
        .map { profile ->
            ImmigrationIntelligenceEngine.auditExpiration(
                profile?.residencePermitExpirationDate ?: "Not provided",
                "Residence Permit"
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ImmigrationIntelligenceEngine.auditExpiration("Not provided", "Residence Permit")
        )

    // ========================================================
    // STAGE 7: UNIFIED AI COMMAND CENTER
    // ========================================================

    private val _commandCenterQuery = MutableStateFlow("")
    val commandCenterQuery: StateFlow<String> = _commandCenterQuery.asStateFlow()

    private val _commandCenterResponse = MutableStateFlow<CommandCenterResponse?>(null)
    val commandCenterResponse: StateFlow<CommandCenterResponse?> = _commandCenterResponse.asStateFlow()

    private val _isCommandCenterProcessing = MutableStateFlow(false)
    val isCommandCenterProcessing: StateFlow<Boolean> = _isCommandCenterProcessing.asStateFlow()

    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    fun getUnifiedContext(): UnifiedScholarContext {
        return UnifiedScholarContext(
            userProfile = userProfile.value,
            universityProfile = universityProfile.value,
            courses = courses.value,
            tasks = tasks.value,
            timetableClasses = timetableClasses.value,
            studySessions = studySessions.value,
            examPlans = examPlans.value,
            presentationPlans = presentationPlans.value,
            researchProjects = researchProjects.value,
            researchMilestones = allResearchMilestones.value,
            researchPapers = allResearchPapers.value,
            researchExperiments = allResearchExperiments.value,
            careerProfile = careerProfile.value,
            careerGoals = careerGoals.value,
            skills = skillInventory.value,
            jobPostings = jobPostings.value,
            jobApplications = jobApplications.value,
            chinesePhrases = chinesePhrases.value,
            chineseVocab = chineseVocabulary.value,
            chineseStudySessions = chineseStudySessions.value,
            chineseWeeklyProgress = chineseWeeklyProgress.value,
            knowledgeItems = knowledgeItems.value,
            immigrationProfile = immigrationProfile.value,
            officialSources = officialSources.value,
            cityPolicies = cityImmigrationPolicies.value,
            graduationChecklist = graduationChecklist.value
        )
    }

    val todayBriefing: StateFlow<TodayBriefing> = combine(
        tasks,
        timetableClasses,
        allResearchMilestones,
        chineseVocabulary
    ) { _, _, _, _ ->
        val briefing = CommandCenterEngine.generateTodayBriefing(getUnifiedContext())
        try {
            com.example.widget.ScholarAppWidgetProvider.updateAllWidgets(getApplication())
        } catch (_: Exception) {}
        briefing
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CommandCenterEngine.generateTodayBriefing(getUnifiedContext())
    )

    val dailyPriorities: StateFlow<DailyPriorities> = combine(
        tasks,
        timetableClasses,
        allResearchMilestones,
        examPlans
    ) { _, _, _, _ ->
        PriorityEngine.calculatePriorities(getUnifiedContext())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PriorityEngine.calculatePriorities(getUnifiedContext())
    )

    val proactiveSuggestions: StateFlow<List<ProactiveSuggestion>> = combine(
        tasks,
        allResearchMilestones,
        chineseVocabulary,
        jobApplications
    ) { _, _, _, _ ->
        CommandCenterEngine.generateProactiveSuggestions(getUnifiedContext())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CommandCenterEngine.generateProactiveSuggestions(getUnifiedContext())
    )

    val recentActivities: StateFlow<List<ScholarActivity>> = combine(
        researchProjects,
        knowledgeItems,
        tasks,
        jobApplications
    ) { _, _, _, _ ->
        CommandCenterEngine.collectRecentActivity(getUnifiedContext())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CommandCenterEngine.collectRecentActivity(getUnifiedContext())
    )

    val globalSearchResults: StateFlow<List<GlobalSearchResult>> = _globalSearchQuery
        .map { query ->
            GlobalSearchEngine.search(getUnifiedContext(), query)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun setCommandCenterQuery(query: String) {
        _commandCenterQuery.value = query
    }

    fun setGlobalSearchQuery(query: String) {
        _globalSearchQuery.value = query
    }

    fun clearCommandCenterResponse() {
        _commandCenterResponse.value = null
        _commandCenterQuery.value = ""
    }

    fun executeCommandCenterQuery(queryText: String) {
        val q = queryText.ifBlank { _commandCenterQuery.value }.trim()
        if (q.isBlank()) return
        _commandCenterQuery.value = q
        _isCommandCenterProcessing.value = true

        viewModelScope.launch {
            val context = getUnifiedContext()
            val answer = CommandCenterEngine.answerQuery(q, context)
            _commandCenterResponse.value = answer
            _isCommandCenterProcessing.value = false
        }
    }

    fun executeQuickAction(actionPrompt: String, targetScreen: AppScreen? = null) {
        if (targetScreen != null && actionPrompt.isBlank()) {
            _currentScreen.value = targetScreen
            return
        }
        _commandCenterQuery.value = actionPrompt
        executeCommandCenterQuery(actionPrompt)
    }

    // ========================================================
    // STAGE 8A: VOICE INPUT & DAILY BRIEFING EXPORT
    // ========================================================

    private val voiceController: SpeechRecognitionController by lazy {
        AndroidSpeechRecognizerController(getApplication())
    }

    val voiceInputState: StateFlow<VoiceInputState> by lazy {
        voiceController.state
    }

    fun isVoiceInputAvailable(): Boolean = voiceController.isAvailable()

    fun startVoiceDictation() {
        voiceController.startListening { recognizedText ->
            if (recognizedText.isNotBlank()) {
                _commandCenterQuery.value = recognizedText
                _toastEvent.value = "Voice captured. Review or edit before sending."
            }
        }
    }

    fun stopVoiceDictation() {
        voiceController.stopListening()
    }

    fun cancelVoiceDictation() {
        voiceController.cancel()
    }

    fun resetVoiceState() {
        voiceController.reset()
    }

    fun getBriefingMarkdown(): String {
        return BriefingExporter.formatMarkdown(todayBriefing.value, getUnifiedContext())
    }

    fun getBriefingPlainText(): String {
        return BriefingExporter.formatPlainText(todayBriefing.value, getUnifiedContext())
    }

    fun shareBriefingMarkdown(context: Context) {
        val md = getBriefingMarkdown()
        BriefingExporter.shareText(context, md, "CS Scholar OS Today's Briefing (Markdown)")
    }

    fun shareBriefingPlainText(context: Context) {
        val txt = getBriefingPlainText()
        BriefingExporter.shareText(context, txt, "CS Scholar OS Today's Briefing")
    }

    fun exportAndShareBriefingPdf(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val file = BriefingExporter.createPdfDocument(context, todayBriefing.value, getUnifiedContext())
            if (file != null && file.exists()) {
                BriefingExporter.sharePdf(context, file)
            } else {
                _toastEvent.value = "PDF generation failed. Sharing text briefing instead."
                shareBriefingPlainText(context)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            voiceController.cancel()
        } catch (_: Exception) {}
        tts?.stop()
        tts?.shutdown()
    }
}
