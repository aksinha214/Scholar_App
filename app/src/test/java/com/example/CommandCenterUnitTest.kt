package com.example

import com.example.ai.*
import com.example.data.model.*
import com.example.ui.viewmodel.AppScreen
import org.junit.Assert.*
import org.junit.Test

class CommandCenterUnitTest {

    private fun createSampleContext(): UnifiedScholarContext {
        return UnifiedScholarContext(
            userProfile = UserProfileEntity(
                name = "Alexei Chen",
                nationality = "Russian",
                university = "Yanshan University",
                department = "School of Information Science & Engineering",
                degree = "Master of Science in Computer Science",
                currentSemester = "Semester 3",
                expectedGraduation = "2026-06-30",
                researchInterests = "Computer Vision, Point Cloud Segmentation",
                technicalSkills = "Python, PyTorch, C++",
                programmingLanguages = "Python, C++",
                aiMlSkills = "Deep Learning, SAM, PointNeXt",
                cvSkills = "3D LiDAR, Point Cloud",
                researchExperience = "Perception Lab YSU",
                publications = "CVPR 2027 Preprint",
                projects = "PointFog-SAM",
                githubUrl = "https://github.com/alexei",
                certifications = "NVIDIA DLI",
                chineseProficiency = "HSK 4",
                englishProficiency = "Fluent",
                careerGoals = "AI Algorithm Engineer",
                targetIndustries = "Autonomous Driving",
                targetCountries = "China",
                targetCompanies = "Baidu, DJI",
                targetUniversities = "YSU",
                targetVenues = "CVPR, ICCV",
                currentAcademicTasks = "Finish CS305 Project",
                currentResearchProjects = "PointFog-SAM"
            ),
            universityProfile = UniversityProfileEntity(),
            courses = listOf(
                CourseEntity(
                    id = 1,
                    code = "CS305",
                    name = "Computer Vision",
                    professorName = "Prof. Zhang",
                    professorContact = "zhang@ysu.edu.cn",
                    credits = 3,
                    classroom = "East 4-302",
                    scheduleTime = "Mon 08:00",
                    attendedClasses = 28,
                    absentClasses = 2,
                    excusedClasses = 0
                )
            ),
            tasks = listOf(
                AcademicTaskEntity(
                    id = 101,
                    courseName = "Computer Vision",
                    title = "Coursework 2: Point Cloud Filtering",
                    type = "Assignment",
                    deadline = "2026-10-04", // Today
                    priority = "High",
                    isCompleted = false
                ),
                AcademicTaskEntity(
                    id = 102,
                    courseName = "Advanced Algorithms",
                    title = "Graph Cut Implementation",
                    type = "Project",
                    deadline = "2026-10-03", // Overdue
                    priority = "Medium",
                    isCompleted = false
                )
            ),
            timetableClasses = listOf(
                TimetableClassEntity(
                    id = 201,
                    courseCode = "CS305",
                    courseName = "Computer Vision",
                    teacher = "Prof. Zhang",
                    dayOfWeek = "Sunday",
                    startTime = "08:00",
                    endTime = "09:35",
                    classroom = "East Campus 4-302"
                )
            ),
            researchProjects = listOf(
                ResearchProjectEntity(
                    id = 301,
                    title = "Robust 3D LiDAR Segmentation in Coastal Fog",
                    researchArea = "Computer Vision",
                    currentStatus = "Experiments",
                    targetVenue = "CVPR 2027",
                    isActive = true,
                    progressPercent = 70,
                    lastUpdated = 1728000000000L
                )
            ),
            researchMilestones = listOf(
                ResearchMilestoneEntity(
                    id = 401,
                    projectId = 301,
                    title = "Complete Koschmieder Layer Ablation Matrix",
                    deadline = "2026-10-05",
                    status = "In Progress",
                    isCompleted = false
                )
            ),
            researchPapers = listOf(
                ResearchPaperEntity(
                    id = 501,
                    projectId = 301,
                    title = "PointNeXt: Revisiting PointNet++ with Improved Training and Architecture",
                    authors = "Qian et al.",
                    year = "2022",
                    venue = "NeurIPS",
                    abstractText = "Modern point cloud segmentation baseline with inverted residuals.",
                    researchProblem = "Computational scalability of hierarchical point feature learning"
                )
            ),
            careerProfile = CareerProfileEntity(
                targetJobTitles = "AI Algorithm Engineer, Computer Vision Engineer",
                targetCities = "Shanghai, Beijing, Shenzhen"
            ),
            skills = listOf(
                SkillInventoryEntity(
                    id = 601,
                    skillName = "PyTorch 3D Point Cloud",
                    category = "AI/ML",
                    currentLevel = "Advanced",
                    evidence = "Built PointFog-SAM",
                    lastPracticed = "2026-10-01",
                    relatedProject = "PointFog-SAM"
                )
            ),
            jobPostings = listOf(
                JobPostingEntity(
                    id = 701,
                    jobTitle = "Autonomous Driving Perception Algorithm Engineer",
                    company = "AutoDrive AI",
                    location = "Shanghai",
                    requiredSkills = "PyTorch, C++, Point Cloud",
                    preferredSkills = "CUDA, ROS",
                    salary = "¥30,000 - ¥45,000 / month"
                )
            ),
            jobApplications = listOf(
                JobApplicationEntity(
                    id = 801,
                    company = "DeepSeek AI",
                    position = "Research Intern",
                    location = "Beijing",
                    status = "Interview",
                    dateApplied = "2026-09-25"
                )
            ),
            chineseVocab = listOf(
                ChineseVocabularyEntity(
                    id = 901,
                    hanzi = "点云分割",
                    pinyin = "diǎn yún fēn gē",
                    english = "point cloud segmentation",
                    category = "Technical CS",
                    hskLevel = "HSK 5",
                    nextReviewDate = "2026-10-04" // Due today
                )
            ),
            knowledgeItems = listOf(
                KnowledgeItemEntity(
                    id = 1001,
                    title = "Koschmieder Law and Aerosol Noise Modeling",
                    category = "Notes",
                    content = "Optical transmission model for maritime coastal fog attenuation: I = I_0 * exp(-gamma * R).",
                    tags = "Physics, CV, Scattering",
                    dateAdded = 1727900000000L
                )
            ),
            immigrationProfile = ImmigrationProfileEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                nationality = "Russian",
                currentUniversity = "Yanshan University",
                universityCityProvince = "Qinhuangdao, Hebei",
                degreeLevel = "Master of Science",
                major = "Computer Science",
                expectedGraduationDate = "2026-06-30",
                residencePermitExpirationDate = "2026-07-31",
                targetEmploymentCity = "Shanghai",
                intendsToRemainInChina = "Yes"
            ),
            timestamp = 1791100000000L
        )
    }

    // 1. Intent Routing Tests
    @Test
    fun testIntentRouting_Study() {
        val res = IntentRouter.routeQuery("What should I study today?")
        assertEquals(CommandIntent.STUDY_TODAY, res.primaryIntent)
        assertTrue(res.targetModules.contains(AppScreen.UNIVERSITY))
    }

    @Test
    fun testIntentRouting_Research() {
        val res = IntentRouter.routeQuery("Show my research progress")
        assertEquals(CommandIntent.RESEARCH_PROGRESS, res.primaryIntent)
        assertTrue(res.targetModules.contains(AppScreen.RESEARCH_LAB))
    }

    @Test
    fun testIntentRouting_Deadlines() {
        val res = IntentRouter.routeQuery("What are my upcoming deadlines?")
        assertEquals(CommandIntent.DEADLINES_TASKS, res.primaryIntent)
    }

    @Test
    fun testIntentRouting_ImmigrationWork() {
        val res = IntentRouter.routeQuery("What documents are important for my current China work transition?")
        assertEquals(CommandIntent.CHINA_WORK_VISA, res.primaryIntent)
        assertTrue(res.targetModules.contains(AppScreen.CHINA_WORK))
    }

    @Test
    fun testIntentRouting_Chinese() {
        val res = IntentRouter.routeQuery("What Chinese should I practice today?")
        assertEquals(CommandIntent.CHINESE_PRACTICE, res.primaryIntent)
        assertTrue(res.targetModules.contains(AppScreen.CHINESE_LANGUAGE))
    }

    @Test
    fun testIntentRouting_MultiModulePresentation() {
        val res = IntentRouter.routeQuery("How should I prepare for my upcoming research presentation?")
        assertEquals(CommandIntent.MULTI_MODULE, res.primaryIntent)
        assertTrue(res.isMultiModule)
        assertTrue(res.targetModules.contains(AppScreen.RESEARCH_LAB))
        assertTrue(res.targetModules.contains(AppScreen.DOCUMENTS))
    }

    // 2. Cross-Module Context Aggregation
    @Test
    fun testCrossModuleContextAggregation() {
        val ctx = createSampleContext()
        assertNotNull(ctx.userProfile)
        assertEquals("Alexei Chen", ctx.userProfile?.name)
        assertEquals(1, ctx.courses.size)
        assertEquals(2, ctx.tasks.size)
        assertEquals(1, ctx.researchProjects.size)
        assertEquals(1, ctx.chineseVocab.size)

        val summary = UnifiedContextProvider.summarizeForModule(ctx, "RESEARCH")
        assertTrue(summary.contains("Robust 3D LiDAR Segmentation"))
        assertTrue(summary.contains("CVPR 2027"))
    }

    // 3. Priority Engine & Calculation
    @Test
    fun testPriorityCalculation_DeterministicOrdering() {
        val ctx = createSampleContext()
        val priorities = PriorityEngine.calculatePriorities(ctx, referenceDateStr = "2026-10-04")

        // Overdue item (deadline 2026-10-03) must score in highPriority (score >= 80)
        assertTrue(priorities.highPriority.isNotEmpty())
        val highest = priorities.highPriority.first()
        // Overdue or due today
        assertTrue(highest.priorityLevel == PriorityLevel.OVERDUE || highest.priorityLevel == PriorityLevel.DUE_TODAY)
        assertTrue(priorities.overdueCount >= 1)
    }

    // 4. Missing Data Handling & No-Fabrication
    @Test
    fun testMissingDataHandling_DoesNotFabricate() {
        val emptyCtx = UnifiedScholarContext(
            userProfile = null,
            universityProfile = null,
            courses = emptyList(),
            tasks = emptyList(),
            timetableClasses = emptyList(),
            studySessions = emptyList()
        )

        val response = CommandCenterEngine.answerQuery("What should I study today?", emptyCtx)
        // Must indicate missing data and not invent fake classes
        assertTrue(response.missingDataSection.isNotEmpty())
        assertTrue(
            response.missingDataSection.any { it.contains("not configured", ignoreCase = true) || it.contains("enough information", ignoreCase = true) }
        )
    }

    // 5. Today's Briefing Generation
    @Test
    fun testTodayBriefingGeneration() {
        val ctx = createSampleContext()
        val briefing = CommandCenterEngine.generateTodayBriefing(ctx)

        assertNotNull(briefing.greeting)
        assertTrue(briefing.greeting.contains("Alexei") || briefing.greeting.contains("Scholar"))
        assertFalse(briefing.isEmptyState)
        assertTrue(briefing.prioritiesOverview.isNotEmpty())
    }

    @Test
    fun testTodayBriefing_EmptyState() {
        val emptyCtx = UnifiedScholarContext(
            userProfile = null,
            universityProfile = null,
            tasks = emptyList(),
            timetableClasses = emptyList()
        )
        val briefing = CommandCenterEngine.generateTodayBriefing(emptyCtx)
        assertTrue(briefing.isEmptyState)
        assertTrue(briefing.emptyStateNotice.contains("No classes, tasks, or milestones"))
    }

    // 6. Global Search Across Heterogeneous Modules
    @Test
    fun testGlobalSearch() {
        val ctx = createSampleContext()

        // Search task
        val taskResults = GlobalSearchEngine.search(ctx, "Point Cloud Filtering")
        assertTrue(taskResults.isNotEmpty())
        assertEquals(AppScreen.UNIVERSITY, taskResults.first().module)

        // Search research
        val researchResults = GlobalSearchEngine.search(ctx, "LiDAR")
        assertTrue(researchResults.isNotEmpty())
        assertTrue(researchResults.any { it.module == AppScreen.RESEARCH_LAB })

        // Search Chinese vocab
        val chineseResults = GlobalSearchEngine.search(ctx, "diǎn yún")
        assertTrue(chineseResults.isNotEmpty())
        assertEquals(AppScreen.CHINESE_LANGUAGE, chineseResults.first().module)

        // Search Knowledge base
        val kbResults = GlobalSearchEngine.search(ctx, "Koschmieder")
        assertTrue(kbResults.isNotEmpty())
        assertTrue(kbResults.any { it.module == AppScreen.DOCUMENTS })

        // Empty query
        val emptyResults = GlobalSearchEngine.search(ctx, "")
        assertTrue(emptyResults.isEmpty())
    }

    // 7. Source Attribution Verification
    @Test
    fun testSourceAttribution() {
        val ctx = createSampleContext()
        val res = CommandCenterEngine.answerQuery("Show my research progress", ctx)
        assertTrue(res.sourceAttributions.isNotEmpty())
        assertTrue(res.sourceAttributions.any { it.contains("Research Lab") })

        val resCareer = CommandCenterEngine.answerQuery("What skills should I improve for my target job?", ctx)
        assertTrue(resCareer.sourceAttributions.any { it.contains("Career Center") })
    }

    // 8. Structured 4-Section Response Partitioning
    @Test
    fun testStructuredResponsePartitioning() {
        val ctx = createSampleContext()
        val res = CommandCenterEngine.answerQuery("What should I study today?", ctx)

        // Must have section A, B, and C
        assertTrue("Section A must contain verified data", res.dataSection.isNotEmpty())
        assertTrue("Section B must contain AI suggestions", res.suggestionSection.isNotEmpty())
        assertTrue("Section C must contain official information", res.officialSection.isNotEmpty())
    }

    // 9. Recent Activity Collection
    @Test
    fun testRecentActivityCollection() {
        val ctx = createSampleContext()
        val activities = CommandCenterEngine.collectRecentActivity(ctx)
        assertTrue(activities.isNotEmpty())
        assertTrue(activities.any { it.module == AppScreen.RESEARCH_LAB })
        assertTrue(activities.any { it.module == AppScreen.DOCUMENTS })
    }

    // 10. Proactive Suggestions
    @Test
    fun testProactiveSuggestions() {
        val ctx = createSampleContext()
        val suggestions = CommandCenterEngine.generateProactiveSuggestions(ctx)
        assertTrue(suggestions.isNotEmpty())
        // Should suggest research or study or chinese review
        assertTrue(suggestions.any { it.iconCategory == "Research" || it.iconCategory == "Study" || it.iconCategory == "Chinese" })
    }
}
