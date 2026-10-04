package com.example

import com.example.ai.*
import com.example.data.model.*
import com.example.export.BriefingExporter
import com.example.ui.viewmodel.AppScreen
import com.example.voice.SpeechRecognitionController
import com.example.voice.VoiceInputState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit testable speech recognition mock implementing SpeechRecognitionController interface.
 */
class TestSpeechRecognitionController(
    private var isAvailableFlag: Boolean = true
) : SpeechRecognitionController {

    private val _state = MutableStateFlow<VoiceInputState>(VoiceInputState.Idle)
    override val state: StateFlow<VoiceInputState> = _state.asStateFlow()

    var lastCapturedCallback: ((String) -> Unit)? = null
    var isStarted = false
    var isCancelled = false

    override fun isAvailable(): Boolean = isAvailableFlag

    fun setAvailable(available: Boolean) {
        this.isAvailableFlag = available
    }

    override fun startListening(onResult: (String) -> Unit) {
        if (!isAvailableFlag) {
            _state.value = VoiceInputState.Error("Voice input is unavailable on this device. You can continue using text input.")
            return
        }
        isStarted = true
        isCancelled = false
        lastCapturedCallback = onResult
        _state.value = VoiceInputState.Listening(0f)
    }

    fun simulateRmsChanged(rmsDb: Float) {
        if (_state.value is VoiceInputState.Listening) {
            _state.value = VoiceInputState.Listening(rmsDb)
        }
    }

    fun simulateSpeechEnded() {
        if (_state.value is VoiceInputState.Listening) {
            _state.value = VoiceInputState.Processing
        }
    }

    fun simulateSuccessResult(text: String) {
        if (text.isNotBlank()) {
            _state.value = VoiceInputState.Idle
            lastCapturedCallback?.invoke(text)
        } else {
            _state.value = VoiceInputState.Error("No words were recognized. Please try speaking again.")
        }
    }

    fun simulateError(message: String, isPermissionDenied: Boolean = false) {
        _state.value = VoiceInputState.Error(message, isPermissionDenied)
    }

    override fun stopListening() {
        if (_state.value is VoiceInputState.Listening) {
            _state.value = VoiceInputState.Processing
        }
    }

    override fun cancel() {
        isCancelled = true
        isStarted = false
        lastCapturedCallback = null
        _state.value = VoiceInputState.Idle
    }

    override fun reset() {
        _state.value = VoiceInputState.Idle
    }
}

class Stage8AVoiceAndExportTest {

    private fun createTestContext(): UnifiedScholarContext {
        return UnifiedScholarContext(
            userProfile = UserProfileEntity(
                name = "Alexei Chen",
                nationality = "Russian",
                university = "Yanshan University",
                department = "Computer Science",
                degree = "Master of Science",
                currentSemester = "Semester 3",
                expectedGraduation = "2026-06-30",
                researchInterests = "Computer Vision",
                technicalSkills = "PyTorch, C++",
                programmingLanguages = "Python, C++",
                aiMlSkills = "Deep Learning",
                cvSkills = "Point Cloud",
                researchExperience = "Perception Lab",
                publications = "CVPR Paper",
                projects = "PointFog-SAM",
                githubUrl = "https://github.com",
                certifications = "NVIDIA",
                chineseProficiency = "HSK 4",
                englishProficiency = "Fluent",
                careerGoals = "Algorithm Engineer",
                targetIndustries = "Autonomous Driving",
                targetCountries = "China",
                targetCompanies = "DJI",
                targetUniversities = "YSU",
                targetVenues = "CVPR",
                currentAcademicTasks = "Task 1",
                currentResearchProjects = "Project 1"
            ),
            universityProfile = UniversityProfileEntity(),
            tasks = listOf(
                AcademicTaskEntity(
                    id = 1,
                    courseName = "Computer Vision",
                    title = "Coursework 2: Point Cloud Filtering",
                    type = "Assignment",
                    deadline = "2026-10-04",
                    priority = "High",
                    isCompleted = false
                )
            ),
            timetableClasses = listOf(
                TimetableClassEntity(
                    id = 2,
                    courseCode = "CS305",
                    courseName = "Computer Vision",
                    teacher = "Prof. Zhang",
                    dayOfWeek = "Sunday",
                    startTime = "08:00",
                    endTime = "09:35",
                    classroom = "East 4-302"
                )
            ),
            researchProjects = listOf(
                ResearchProjectEntity(
                    id = 3,
                    title = "Robust 3D LiDAR Segmentation",
                    targetVenue = "CVPR 2027",
                    progressPercent = 75,
                    isActive = true
                )
            ),
            researchMilestones = listOf(
                ResearchMilestoneEntity(
                    id = 4,
                    projectId = 3,
                    title = "Ablation Experiments",
                    deadline = "2026-10-06",
                    isCompleted = false
                )
            ),
            knowledgeItems = listOf(
                KnowledgeItemEntity(
                    id = 5,
                    title = "Koschmieder Law Notes",
                    category = "Notes",
                    content = "Atmospheric scattering attenuation",
                    tags = "Optics"
                )
            ),
            timestamp = 1791100000000L
        )
    }

    // 1. Voice Input State Transitions
    @Test
    fun testVoiceInput_NormalStateTransitions() {
        val controller = TestSpeechRecognitionController()
        assertEquals(VoiceInputState.Idle, controller.state.value)

        var recognizedText = ""
        controller.startListening { text ->
            recognizedText = text
        }

        assertTrue(controller.state.value is VoiceInputState.Listening)

        controller.simulateRmsChanged(3.5f)
        val listeningState = controller.state.value as VoiceInputState.Listening
        assertEquals(3.5f, listeningState.rmsDb, 0.01f)

        controller.simulateSpeechEnded()
        assertEquals(VoiceInputState.Processing, controller.state.value)

        controller.simulateSuccessResult("What should I study today?")
        assertEquals(VoiceInputState.Idle, controller.state.value)
        assertEquals("What should I study today?", recognizedText)
    }

    // 2. Empty Speech Result Handling
    @Test
    fun testVoiceInput_EmptySpeechResultHandling() {
        val controller = TestSpeechRecognitionController()
        var textCaptured = false

        controller.startListening {
            textCaptured = true
        }

        controller.simulateSuccessResult("")
        assertTrue(controller.state.value is VoiceInputState.Error)
        val err = controller.state.value as VoiceInputState.Error
        assertTrue(err.message.contains("No words were recognized", ignoreCase = true))
        assertFalse("Callback must not be invoked on empty result", textCaptured)
    }

    // 3. Voice Cancellation
    @Test
    fun testVoiceInput_Cancellation() {
        val controller = TestSpeechRecognitionController()
        controller.startListening { }
        assertTrue(controller.state.value is VoiceInputState.Listening)

        controller.cancel()
        assertTrue(controller.isCancelled)
        assertEquals(VoiceInputState.Idle, controller.state.value)
    }

    // 4. Permission-Denied Handling
    @Test
    fun testVoiceInput_PermissionDeniedHandling() {
        val controller = TestSpeechRecognitionController()
        controller.startListening { }

        controller.simulateError("Microphone permission is required.", isPermissionDenied = true)
        assertTrue(controller.state.value is VoiceInputState.Error)
        val err = controller.state.value as VoiceInputState.Error
        assertTrue(err.isPermissionDenied)
        assertTrue(err.message.contains("Microphone permission", ignoreCase = true))
    }

    // 5. Speech Recognizer Unavailable Handling
    @Test
    fun testVoiceInput_UnavailableHandling() {
        val controller = TestSpeechRecognitionController(isAvailableFlag = false)
        assertFalse(controller.isAvailable())

        controller.startListening { }
        assertTrue(controller.state.value is VoiceInputState.Error)
        val err = controller.state.value as VoiceInputState.Error
        assertTrue(err.message.contains("unavailable on this device", ignoreCase = true))
    }

    // 6. Briefing Markdown Generation
    @Test
    fun testBriefingMarkdownGeneration() {
        val ctx = createTestContext()
        val briefing = CommandCenterEngine.generateTodayBriefing(ctx)
        val md = BriefingExporter.formatMarkdown(briefing, ctx)

        assertTrue(md.contains("# CS Scholar OS — Today's Briefing"))
        assertTrue(md.contains("## High Priority") || md.contains("## Summary & Priorities"))
        assertTrue(md.contains("Alexei Chen"))
        assertTrue(md.contains("Yanshan University"))
        assertTrue(md.contains("Point Cloud Filtering"))
        assertTrue(md.contains("CS305 Computer Vision"))
    }

    // 7. Briefing Plain-Text Generation
    @Test
    fun testBriefingPlainTextGeneration() {
        val ctx = createTestContext()
        val briefing = CommandCenterEngine.generateTodayBriefing(ctx)
        val txt = BriefingExporter.formatPlainText(briefing, ctx)

        assertTrue(txt.contains("CS SCHOLAR OS — TODAY'S BRIEFING"))
        assertTrue(txt.contains("Alexei Chen"))
        assertTrue(txt.contains("[SUMMARY]"))
        assertTrue(txt.contains("Computer Vision"))
    }

    // 8. Empty Briefing Handling (No events invented)
    @Test
    fun testEmptyBriefingHandling() {
        val emptyCtx = UnifiedScholarContext(
            userProfile = null,
            universityProfile = null,
            tasks = emptyList(),
            timetableClasses = emptyList(),
            researchProjects = emptyList()
        )
        val briefing = CommandCenterEngine.generateTodayBriefing(emptyCtx)
        assertTrue(briefing.isEmptyState)

        val md = BriefingExporter.formatMarkdown(briefing, emptyCtx)
        assertTrue(md.contains("No urgent tasks or events scheduled for today."))
        assertFalse("Must not invent fake lectures", md.contains("Machine Learning @"))

        val txt = BriefingExporter.formatPlainText(briefing, emptyCtx)
        assertTrue(txt.contains("No urgent priorities recorded for today."))
    }

    // 9. Correct Date Formatting
    @Test
    fun testBriefingDateFormatting() {
        val ctx = createTestContext()
        val briefing = CommandCenterEngine.generateTodayBriefing(ctx)

        assertNotNull(briefing.dateSummary)
        val md = BriefingExporter.formatMarkdown(briefing, ctx)
        assertTrue(md.contains(briefing.dateSummary))

        val txt = BriefingExporter.formatPlainText(briefing, ctx)
        assertTrue(txt.contains(briefing.dateSummary))
    }

    // 10. Export Content Contains Only Actual Data & No Fabrication
    @Test
    fun testExportContentContainsOnlyActualData() {
        val ctx = createTestContext()
        val briefing = CommandCenterEngine.generateTodayBriefing(ctx)
        val md = BriefingExporter.formatMarkdown(briefing, ctx)

        // Present real data
        assertTrue(md.contains("Point Cloud Filtering"))
        assertTrue(md.contains("Robust 3D LiDAR Segmentation"))

        // Fabricated queries must NEVER appear
        assertFalse(md.contains("Quantum Computing Exam"))
        assertFalse(md.contains("Biology 101"))
        assertFalse(md.contains("Fake Company Offer"))
    }

    // 11. Existing Command Center Query Flow Remains Functional
    @Test
    fun testCommandCenterQueryFlowRemainsFunctional() {
        val ctx = createTestContext()
        val query = "What should I study today?"
        val response = CommandCenterEngine.answerQuery(query, ctx)

        assertEquals(CommandIntent.STUDY_TODAY, response.intentResult.primaryIntent)
        assertTrue(response.dataSection.isNotEmpty())
        assertTrue(response.suggestionSection.isNotEmpty())
        assertTrue(response.officialSection.isNotEmpty())
        assertTrue(response.sourceAttributions.isNotEmpty())
    }
}
