package com.example

import com.example.ai.*
import com.example.data.model.*
import com.example.ui.viewmodel.AppScreen
import com.example.widget.ScholarAppWidgetProvider
import org.junit.Assert.*
import org.junit.Test

class ScholarWidgetAndIntegrationTest {

    private fun createFullContext(): UnifiedScholarContext {
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
                    deadline = "2026-10-06",
                    priority = "High",
                    isCompleted = false
                ),
                AcademicTaskEntity(
                    id = 2,
                    courseName = "Distributed Systems",
                    title = "Systems Project Proposal",
                    type = "Project",
                    deadline = "2026-10-12",
                    priority = "Medium",
                    isCompleted = false
                )
            ),
            timetableClasses = listOf(
                TimetableClassEntity(
                    id = 1,
                    courseCode = "CS305",
                    courseName = "Computer Vision",
                    teacher = "Prof. Zhang",
                    dayOfWeek = "Monday",
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
                    deadline = "2026-10-08",
                    isCompleted = false
                )
            ),
            chineseVocab = listOf(
                ChineseVocabularyEntity(
                    id = 1,
                    hanzi = "研究",
                    pinyin = "yán jiū",
                    english = "research",
                    reviewStatus = "Due",
                    nextReviewDate = "2026-10-01",
                    isKnown = false
                ),
                ChineseVocabularyEntity(
                    id = 2,
                    hanzi = "论文",
                    pinyin = "lùn wén",
                    english = "paper/thesis",
                    reviewStatus = "Mastered",
                    nextReviewDate = "2026-10-20",
                    isKnown = true
                )
            ),
            knowledgeItems = listOf(
                KnowledgeItemEntity(
                    id = 5,
                    title = "Transformer Attention Mechanism Notes",
                    category = "Notes",
                    content = "Atmospheric scattering and multi-head attention mechanics",
                    tags = "Optics,Attention"
                )
            ),
            timestamp = 1791100000000L
        )
    }

    // --- 1. Widget Intent Constants ---
    @Test
    fun testWidgetConstants() {
        assertEquals("NAV_TARGET", ScholarAppWidgetProvider.EXTRA_NAV_TARGET)
        assertEquals("com.example.widget.ACTION_REFRESH_WIDGET", ScholarAppWidgetProvider.ACTION_REFRESH_WIDGET)
    }

    // --- 2. Timetable -> Daily Briefing Integration ---
    @Test
    fun testTimetableIntegrationWithDailyBriefing() {
        val ctx = createFullContext()
        val briefing = CommandCenterEngine.generateTodayBriefing(ctx)

        assertNotNull(briefing)
        assertFalse(briefing.isEmptyState)
        assertNotNull(briefing.dateSummary)
        assertTrue(briefing.prioritiesOverview.isNotEmpty())
    }

    // --- 3. Academic Tasks -> PriorityEngine Integration ---
    @Test
    fun testTasksPriorityEngineIntegration() {
        val ctx = createFullContext()
        val priorities = PriorityEngine.calculatePriorities(ctx)

        assertTrue(priorities.totalPendingCount >= 2)
        val titles = (priorities.highPriority + priorities.recommended + priorities.optional).map { it.title }
        assertTrue(titles.any { it.contains("Coursework 2: Point Cloud Filtering") })
        assertTrue(titles.any { it.contains("Ablation Experiments") })
    }

    // --- 4. Chinese SRS Vocab -> Command Center Context Integration ---
    @Test
    fun testChineseVocabIntegration() {
        val ctx = createFullContext()
        assertEquals(2, ctx.chineseVocab.size)
        assertTrue(ctx.chineseVocabDue.any { it.hanzi == "研究" })

        val response = CommandCenterEngine.answerQuery("What Chinese should I practice today?", ctx)
        assertEquals(CommandIntent.CHINESE_PRACTICE, response.intentResult.primaryIntent)
        assertTrue(response.dataSection.any { it.contains("研究") || it.contains("flashcard") || it.contains("due") })
    }

    // --- 5. Research Milestones -> Command Center Integration ---
    @Test
    fun testResearchMilestonesIntegration() {
        val ctx = createFullContext()
        val response = CommandCenterEngine.answerQuery("Show my research progress", ctx)
        assertEquals(CommandIntent.RESEARCH_PROGRESS, response.intentResult.primaryIntent)
        assertTrue(response.dataSection.any { it.contains("Robust 3D LiDAR") })
        assertTrue(response.sourceAttributions.any { it.contains("Research Lab") })
    }

    // --- 6. China Work & Immigration Integration ---
    @Test
    fun testChinaWorkIntegration() {
        val ctx = createFullContext()
        val response = CommandCenterEngine.answerQuery("What documents are important for my current China work transition?", ctx)
        assertEquals(CommandIntent.CHINA_WORK_VISA, response.intentResult.primaryIntent)
        assertTrue(response.sourceAttributions.any { it.contains("China Work & Visa") })
    }

    // --- 7. Career Center Integration ---
    @Test
    fun testCareerCenterIntegration() {
        val ctx = createFullContext()
        val response = CommandCenterEngine.answerQuery("What skills should I improve for my target job?", ctx)
        assertEquals(CommandIntent.CAREER_SKILLS, response.intentResult.primaryIntent)
        assertTrue(response.sourceAttributions.any { it.contains("Career Center") })
        assertTrue(response.suggestionSection.isNotEmpty())
    }

    // --- 8. Knowledge Base -> Global Search Integration ---
    @Test
    fun testKnowledgeBaseGlobalSearchIntegration() {
        val ctx = createFullContext()
        val searchResults = GlobalSearchEngine.search(ctx, "transformer")

        assertTrue(searchResults.isNotEmpty())
        val match = searchResults.firstOrNull { it.title.contains("Transformer") }
        assertNotNull(match)
        assertEquals(AppScreen.DOCUMENTS, match?.module)
    }

    // --- 9. Multi-Module Query Integration ---
    @Test
    fun testMultiModuleQueryIntegration() {
        val ctx = createFullContext()
        val response = CommandCenterEngine.answerQuery("How should I prepare for my upcoming research presentation?", ctx)
        assertTrue(
            response.intentResult.primaryIntent == CommandIntent.MULTI_MODULE ||
            response.intentResult.primaryIntent == CommandIntent.RESEARCH_PROGRESS ||
            response.intentResult.primaryIntent == CommandIntent.PRESENTATION_PREP
        )
        assertTrue(response.sourceAttributions.isNotEmpty())
        assertTrue(response.suggestionSection.isNotEmpty())
    }

    // --- 10. Graceful Empty Context Handling ---
    @Test
    fun testEmptyContextHandling() {
        val emptyCtx = UnifiedScholarContext(
            userProfile = null,
            universityProfile = null,
            tasks = emptyList(),
            timetableClasses = emptyList(),
            researchProjects = emptyList(),
            researchMilestones = emptyList(),
            chineseVocab = emptyList()
        )

        val priorities = PriorityEngine.calculatePriorities(emptyCtx)
        assertEquals(0, priorities.totalPendingCount)
        assertTrue(priorities.highPriority.isEmpty())

        val briefing = CommandCenterEngine.generateTodayBriefing(emptyCtx)
        assertTrue(briefing.isEmptyState)

        val search = GlobalSearchEngine.search(emptyCtx, "any query")
        assertTrue(search.isEmpty())
    }
}
