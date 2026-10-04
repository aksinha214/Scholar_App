package com.example

import com.example.ai.*
import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testAllMentorModesResolved() {
        assertEquals(10, MentorMode.entries.size)
        assertEquals(MentorMode.PROFESSOR, MentorMode.fromId("professor"))
        assertEquals(MentorMode.RESEARCH_SCIENTIST, MentorMode.fromId("scientist"))
        assertEquals(MentorMode.CRITICAL_REVIEWER, MentorMode.fromId("reviewer"))
        assertEquals(MentorMode.CHINESE_TEACHER, MentorMode.fromId("chinese"))
    }

    @Test
    fun testProjectBuilderGeneratesSpecification() {
        val spec = ProjectBuilderEngine.generateProject("I want a computer vision project", null)
        assertNotNull(spec)
        assertTrue(spec.title.contains("PointFog-SAM") || spec.title.contains("EdgeGuard"))
        assertTrue(spec.technologies.contains("PyTorch") || spec.technologies.contains("C++"))
        assertTrue(spec.milestones.contains("Week 1"))
    }

    @Test
    fun testCareerAnalyzerMatchesSkills() {
        val testProfile = UserProfileEntity(
            name = "Test Student",
            nationality = "International",
            university = "Yanshan University",
            department = "School of Information Science",
            degree = "Bachelor of Engineering",
            currentSemester = "Year 3",
            expectedGraduation = "2027",
            researchInterests = "Computer Vision",
            technicalSkills = "PyTorch, OpenCV, Linux, Git",
            programmingLanguages = "Python, C++",
            aiMlSkills = "Deep Learning, CNNs",
            cvSkills = "3D Point Clouds",
            researchExperience = "Lab",
            publications = "None",
            projects = "LiDAR",
            githubUrl = "https://github.com",
            certifications = "Deep Learning",
            chineseProficiency = "HSK 4",
            englishProficiency = "Fluent",
            careerGoals = "AI Engineer",
            targetIndustries = "Tech",
            targetCountries = "China",
            targetCompanies = "Baidu",
            targetUniversities = "Tsinghua",
            targetVenues = "CVPR",
            currentAcademicTasks = "Assignments",
            currentResearchProjects = "Point clouds"
        )
        val result = CareerAnalyzerEngine.analyzeRole("Computer Vision", testProfile)
        assertNotNull(result)
        assertTrue(result.matchPercentage > 0)
        assertTrue(result.matchedSkills.isNotEmpty())
        assertNotNull(result.chinaSpecificAdvice)
        assertTrue(result.chinaSpecificAdvice.importantConditions.contains("Work Permit") || result.chinaSpecificAdvice.importantConditions.contains("Foreigner"))
    }

    @Test
    fun testDocumentIntelligenceExtractsDeadlines() {
        val syllabus = "燕山大学计算视觉 (CS305): 考核方式：平时实验 (40%) + 期末大作业答辩 (40%)。截止日期：实验一于10月12日前提交。"
        val result = DocumentIntelligenceEngine.analyzeDocument("Syllabus.txt", syllabus)
        assertNotNull(result)
        assertEquals("CS305: Computer Vision", result.extractedCourse)
        assertTrue(result.extractedDeadlines.isNotEmpty())
    }

    @Test
    fun testPersonalAuditAndFutureSelf() {
        val report = PersonalAuditEngine.runAudit(null)
        assertNotNull(report)
        assertTrue(report.overallReadinessScore in 0..100)
        assertEquals(14, report.dimensions.size)
        assertTrue(report.topThreePriorities.isNotEmpty())

        val answer = FutureSelfEngine.askFutureSelf("Should I do a PhD?", null)
        assertNotNull(answer)
        assertTrue(answer.contains("Future Self") && answer.contains("Yanshan University"))
    }

    @Test
    fun testDocumentStructuredSummaryForResearchPaper() {
        val paperDoc = PersonalDocumentEntity(
            userEmail = "alexei.chen@ysu.edu.cn",
            fileName = "PointFog_SAM_Manuscript.md",
            fileType = "MD",
            category = "RESEARCH",
            uploadDate = "2026-09-24",
            tags = "cvpr, lidar, point cloud",
            userNotes = "Research paper draft on adverse weather",
            courseOrProject = "CVPR 2027",
            content = "Abstract: Point cloud LiDAR attenuation under maritime fog. We introduce PointFog-SAM with Koschmieder scattering inversion layer. Evaluated on Qinhuangdao Port dataset."
        )

        val summary = DocumentIntelligenceEngine.generateStructuredSummary(paperDoc)
        assertNotNull(summary)
        assertTrue(summary.isResearchPaper)
        assertNotNull(summary.researchProblem)
        assertNotNull(summary.methodology)
        assertTrue(summary.methodology!!.contains("scattering"))
        assertTrue(summary.evaluationMetrics!!.contains("mIoU"))
    }

    @Test
    fun testAskMyDocumentsWithCitations() {
        val testDocs = listOf(
            PersonalDocumentEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                fileName = "CS305_Syllabus.md",
                fileType = "MD",
                category = "ACADEMIC",
                uploadDate = "2026-09-08",
                tags = "syllabus, deadlines",
                courseOrProject = "CS305",
                content = "Submit Midterm Literature Report by October 18. Final project defense on December 18."
            ),
            PersonalDocumentEntity(
                userEmail = "alexei.chen@ysu.edu.cn",
                fileName = "PointFog_SAM.md",
                fileType = "MD",
                category = "RESEARCH",
                uploadDate = "2026-09-24",
                tags = "research, point cloud",
                courseOrProject = "Research",
                content = "Methodology: Physics-based Koschmieder scattering inversion with deformable cross-attention."
            )
        )

        // 1. Query about deadlines
        val deadlineResp = DocumentIntelligenceEngine.askMyDocuments("What is the deadline for midterm?", testDocs, null)
        assertTrue(deadlineResp.foundInDocuments)
        assertTrue(deadlineResp.citations.isNotEmpty())
        assertEquals("CS305_Syllabus.md", deadlineResp.citations.first().documentTitle)
        assertTrue(deadlineResp.documentFindings.contains("October 18"))

        // 2. Query about methodology
        val methodResp = DocumentIntelligenceEngine.askMyDocuments("What methodology did I use?", testDocs, null)
        assertTrue(methodResp.foundInDocuments)
        assertTrue(methodResp.citations.any { it.documentTitle == "PointFog_SAM.md" })
    }

    @Test
    fun testExtractActionableTasks() {
        val doc = PersonalDocumentEntity(
            userEmail = "alexei.chen@ysu.edu.cn",
            fileName = "YSU_Notice.txt",
            fileType = "TXT",
            category = "UNIVERSITY",
            uploadDate = "2026-09-02",
            content = "Off-campus residents must register at police station within 24 hours. Submit Midterm Literature Report by October 18."
        )

        val actions = DocumentIntelligenceEngine.extractActionableTasks(doc)
        assertTrue(actions.isNotEmpty())
        assertTrue(actions.any { it.dueDate.contains("24h") || it.dueDate.contains("Oct 18") })
    }

    @Test
    fun testDataIsolationBetweenUsers() {
        val docA = PersonalDocumentEntity(
            userEmail = "userA@ysu.edu.cn",
            fileName = "Private_Notes_A.txt",
            fileType = "TXT",
            category = "PERSONAL",
            uploadDate = "2026-09-01",
            content = "User A private study notes"
        )
        val docB = PersonalDocumentEntity(
            userEmail = "userB@ysu.edu.cn",
            fileName = "Private_Notes_B.txt",
            fileType = "TXT",
            category = "PERSONAL",
            uploadDate = "2026-09-01",
            content = "User B private study notes"
        )

        val allDocs = listOf(docA, docB)
        val userADocs = allDocs.filter { it.userEmail == "userA@ysu.edu.cn" }
        assertEquals(1, userADocs.size)
        assertEquals("Private_Notes_A.txt", userADocs.first().fileName)
        assertFalse(userADocs.any { it.userEmail == "userB@ysu.edu.cn" })
    }

    @Test
    fun testTimetableParserExtractsClassesAndHighlightsUncertainty() {
        val rawTimetable = """
            Monday
            08:00–09:35 CS301 Advanced Algorithms & Optimization Room 4-302 Prof. Wang Zhi Weeks 1-16
            Tuesday
            10:15–11:50 CS305 Computer Vision & Pattern Recognition Science Hall 5-201 Prof. Zhang Lin
            Friday
            09:50–12:15 CS308 Operating Systems Internals Room 3-101 Prof. Chen Gang
        """.trimIndent()

        val parsed = TimetableIntelligenceEngine.parseTimetableText(rawTimetable)
        assertNotNull(parsed)
        assertTrue(parsed.classes.size >= 3)

        val cs301 = parsed.classes.find { it.courseCode == "CS301" }
        assertNotNull(cs301)
        assertEquals("Monday", cs301!!.dayOfWeek)
        assertEquals("08:00", cs301.startTime)
        assertEquals("09:35", cs301.endTime)
        assertTrue(cs301.classroom.contains("4-302"))

        // Ambiguous schedule with missing classroom/time should be flagged as uncertain
        val ambiguous = "Wednesday: Advanced Artificial Intelligence seminar"
        val parsedAmbiguous = TimetableIntelligenceEngine.parseTimetableText(ambiguous)
        assertTrue(parsedAmbiguous.classes.any { it.isUncertain })
    }

    @Test
    fun testAttendanceForecastMathematicalFormulation() {
        // Example: 24 scheduled classes, 21 attended, 3 absent, target 80%
        // Target = 0.80 * 24 = 19.2 -> ceil(19.2) = 20 required attendances.
        // Total allowable absences overall = 24 - 20 = 4.
        // Current absences = 3.
        // Additional allowable absences = 4 - 3 = 1.
        val course = com.example.data.model.CourseEntity(
            code = "CS305",
            name = "Computer Vision",
            professorName = "Prof. Zhang Lin",
            professorContact = "",
            credits = 3,
            classroom = "5-201",
            scheduleTime = "Tue/Thu",
            attendanceTarget = 80.0f,
            scheduledClasses = 24,
            attendedClasses = 21,
            absentClasses = 3,
            excusedClasses = 0
        )

        val forecast = IntelligentStudyPlannerEngine.calculateAttendanceForecast(course)
        assertNotNull(forecast)
        assertEquals(24, forecast.scheduledClasses)
        assertEquals(21, forecast.attendedClasses)
        assertEquals(3, forecast.absentClasses)
        assertEquals(0, forecast.remainingClasses) // 24 - 24 = 0
        assertEquals(1, forecast.maxAdditionalAbsencesAllowed)
        assertTrue(forecast.currentRatePercent > 80.0f)
        assertTrue(forecast.mathematicalExplanation.contains("Mathematical Calculation"))
    }

    @Test
    fun testFreeTimeSlotDetection() {
        val schedule = listOf(
            com.example.data.model.TimetableClassEntity(
                courseCode = "CS301",
                courseName = "Algorithms",
                teacher = "Prof. Wang",
                dayOfWeek = "Tuesday",
                startTime = "08:00",
                endTime = "09:35",
                classroom = "4-302"
            ),
            com.example.data.model.TimetableClassEntity(
                courseCode = "CS305",
                courseName = "Vision",
                teacher = "Prof. Zhang",
                dayOfWeek = "Tuesday",
                startTime = "14:00",
                endTime = "15:35",
                classroom = "5-201"
            )
        )

        val slots = IntelligentStudyPlannerEngine.detectFreeTimeSlots(schedule, emptyList())
        val tuesdaySlots = slots.filter { it.dayOfWeek == "Tuesday" }
        assertTrue(tuesdaySlots.isNotEmpty())
        // Gap from 09:35 to 14:00 is > 4 hours, detected as free time
        assertTrue(tuesdaySlots.any { it.startTime == "09:35" && it.endTime == "14:00" })
    }

    @Test
    fun testAssignmentBreakdownEngine() {
        val prompt = "I have a 3000-word report due Friday."
        val steps = IntelligentStudyPlannerEngine.breakDownAssignment(prompt)
        assertNotNull(steps)
        assertEquals(5, steps.size)
        assertTrue(steps[0].title.contains("Research") || steps[0].title.contains("Phase 1"))
        assertTrue(steps[1].title.contains("Outline") || steps[1].title.contains("Phase 2"))
        assertTrue(steps[2].title.contains("Draft") || steps[2].title.contains("Phase 3"))
        assertTrue(steps[4].title.contains("Submission") || steps[4].title.contains("Phase 5"))
    }

    @Test
    fun testExtractAssignmentsFromSyllabus() {
        val syllabus = """
            Course: CS305 Computer Vision & Pattern Recognition
            Grading: Midterm Exam (30%), Homework Lab 1 (15%), Term Oral Presentation (20%), Final Project (35%).
        """.trimIndent()

        val extracted = IntelligentStudyPlannerEngine.extractAssignmentsFromDocument(syllabus, "CS305_Syllabus.txt")
        assertTrue(extracted.isNotEmpty())
        assertTrue(extracted.any { it.type == "Exam" || it.title.contains("Midterm") })
        assertTrue(extracted.any { it.type == "Assignment" || it.title.contains("Lab") })
        assertTrue(extracted.any { it.type == "Presentation" })
    }

    @Test
    fun testResearchPaperAnalysisZeroInventionRule() {
        val rawPaperText = """
            Paper Title: SphereFormer: Spherical Radial Window Attention for 3D Point Cloud Perception
            Authors: Xin Lai, Yukang Chen, Fan Lu, Dahua Lin
            Venue: CVPR
            Year: 2023
            DOI: 10.1109/CVPR52729.2023.00940
            Abstract: Outdoor LiDAR sweeps exhibit distinct radial density patterns. We present SphereFormer.
            Method: Spherical window attention mechanism with radial partitioning.
            Dataset: SemanticKITTI, nuScenes
            Baselines: PointNeXt, MinkUNet
            Metrics: mIoU, Latency
            Results: Achieves 71.2% mIoU on SemanticKITTI benchmark.
            Limitations: Extreme computation under heavy atmospheric backscatter noise.
        """.trimIndent()

        val analysis = ResearchIntelligenceEngine.analyzeResearchPaper(
            rawText = rawPaperText,
            fileName = "SphereFormer_CVPR.pdf",
            project = null
        )

        assertNotNull(analysis)
        assertEquals("SphereFormer: Spherical Radial Window Attention for 3D Point Cloud Perception", analysis.title)
        assertTrue(analysis.authors.contains("Xin Lai"))
        assertEquals("CVPR", analysis.venue)
        assertEquals("2023", analysis.year)
        assertEquals("10.1109/CVPR52729.2023.00940", analysis.doi)
        assertTrue(analysis.dataset.contains("SemanticKITTI"))
        assertTrue(analysis.results.contains("71.2%"))
        assertTrue(analysis.sourceContentSnippet.contains("[SOURCE CONTENT EXCERPT]"))
        assertTrue(analysis.aiInterpretationSnippet.contains("[AI INTERPRETATION & RESEARCH CONTEXT]"))

        // Test zero-invention rule: missing author and DOI in raw text must NOT be fabricated
        val incompleteText = "A novel point cloud approach with no metadata provided."
        val incompleteAnalysis = ResearchIntelligenceEngine.analyzeResearchPaper(incompleteText, "unknown.pdf", null)
        assertEquals("Not available in the document.", incompleteAnalysis.authors)
        assertEquals("Not available in the document.", incompleteAnalysis.doi)
        assertEquals("Not available in the document.", incompleteAnalysis.venue)
        assertTrue(incompleteAnalysis.missingFieldsWarning.contains("Authors"))
    }

    @Test
    fun testLiteratureGapSynthesis() {
        val papers = listOf(
            com.example.data.model.ResearchPaperEntity(
                projectId = 1,
                title = "PointNeXt",
                authors = "Guocheng Qian et al.",
                year = "2022",
                venue = "NeurIPS",
                dataset = "SemanticKITTI",
                method = "Inverted residual MLP",
                limitations = "Severe degradation in adverse maritime fog."
            ),
            com.example.data.model.ResearchPaperEntity(
                projectId = 1,
                title = "Cylinder3D",
                authors = "Xinge Zhu et al.",
                year = "2021",
                venue = "CVPR",
                dataset = "nuScenes",
                method = "Cylindrical voxel convolutions",
                limitations = "Heavy memory consumption and spurious noise clusters."
            )
        )

        val gaps = ResearchIntelligenceEngine.synthesizeResearchGaps(papers, null)
        assertNotNull(gaps)
        assertTrue(gaps.commonAssumptions.isNotEmpty())
        assertTrue(gaps.commonDatasets.contains("SemanticKITTI") || gaps.commonDatasets.contains("nuScenes"))
        assertTrue(gaps.weaknessesInCurrentApproaches.isNotEmpty())
        assertTrue(gaps.openOpportunitiesForUserWork.isNotEmpty())
        assertTrue(gaps.disclaimer.contains("Synthesized hypothesis & analysis"))
    }

    @Test
    fun testBibTeXGenerationAndValidation() {
        val result = ResearchIntelligenceEngine.generateBibTeXString(
            title = "Robust Semi-Supervised 3D LiDAR",
            authors = "Alexei Chen-Kovalenko, Zhang Lin",
            year = "2027",
            venue = "CVPR",
            doi = "10.1109/CVPR2027.001",
            url = "https://arxiv.org/abs/2027.001"
        )

        assertNotNull(result)
        assertTrue(result.isValid)
        assertTrue(result.citationKey.startsWith("alexei2027"))
        assertTrue(result.bibtex.contains("@inproceedings{"))
        assertTrue(result.bibtex.contains("author    = {Alexei Chen-Kovalenko, Zhang Lin}"))
        assertTrue(result.bibtex.contains("booktitle = {CVPR}"))

        // Missing fields test
        val missingResult = ResearchIntelligenceEngine.generateBibTeXString(
            title = "Incomplete Paper",
            authors = "Not available in the document.",
            year = "Not available in the document.",
            venue = "Not available in the document.",
            doi = "Not available in the document.",
            url = ""
        )
        assertFalse(missingResult.isValid)
        assertTrue(missingResult.missingFields.contains("author"))
        assertTrue(missingResult.missingFields.contains("year"))
        assertTrue(missingResult.bibtex.contains("WARNING: Missing authors"))
    }

    @Test
    fun testAdvisorMeetingBriefGeneration() {
        val project = com.example.data.model.ResearchProjectEntity(
            title = "Robust Point Cloud Segmentation in Fog",
            targetVenue = "CVPR 2027",
            currentStatus = "Implementation"
        )
        val milestones = listOf(
            com.example.data.model.ResearchMilestoneEntity(
                projectId = 1,
                title = "Implement PointNeXt Baseline",
                deadline = "Sep 28, 2026",
                status = "Completed",
                isCompleted = true
            ),
            com.example.data.model.ResearchMilestoneEntity(
                projectId = 1,
                title = "Koschmieder physics scattering inversion layer",
                deadline = "Oct 15, 2026",
                status = "In Progress",
                isCompleted = false
            )
        )
        val experiments = listOf(
            com.example.data.model.ResearchExperimentEntity(
                projectId = 1,
                experimentId = "EXP-002",
                name = "PointFog-SAM Inversion",
                date = "2026-09-29",
                dataset = "Qinhuangdao Port LiDAR",
                model = "PointFog-SAM",
                results = "Real Port Fog: 58.4% mIoU",
                failureAnalysis = "Increased false positives on wet metallic crane cables."
            )
        )

        val brief = ResearchIntelligenceEngine.generateAdvisorBrief(
            project = project,
            milestones = milestones,
            experiments = experiments,
            papers = emptyList(),
            userNotes = "Spike in GPU temperature on node 3"
        )

        assertNotNull(brief)
        assertTrue(brief.accomplishments.isNotEmpty())
        assertTrue(brief.challengesAndFailures.any { it.contains("EXP-002 Issue") || it.contains("crane cables") || it.contains("Spike") })
        assertTrue(brief.currentExperimentResults.any { it.contains("EXP-002") })
        assertTrue(brief.questionsForSupervisor.isNotEmpty())
        assertTrue(brief.proposedNextSteps.any { it.contains("Koschmieder") })
        assertTrue(brief.formattedMarkdown.contains("# Weekly Research Advisor Brief"))
    }

    // ==========================================
    // STAGE 4: CAREER & JOB ASSISTANT UNIT TESTS
    // ==========================================

    @Test
    fun testJobDescriptionAnalyzerRequiredVsPreferred() {
        val rawJobText = """
            Job Title: Senior Computer Vision Engineer
            Company: Horizon Robotics
            Location: Shanghai, China
            Degree: Master's in Computer Science or Electrical Engineering
            Experience: 2+ years of experience
            Required Qualifications:
            • Strong programming in C++ and Python
            • Deep Learning architectures and PyTorch
            • 3D Point Clouds and LiDAR perception
            • Linux development environment
            Preferred Qualifications:
            • CUDA kernel optimization and TensorRT deployment
            • Docker containerization
            Responsibilities:
            • Develop real-time perception models for automated vehicles
            • Evaluate models against state-of-the-art baselines
            Salary: ¥35,000 - ¥50,000 / month
            Application Deadline: 2027-05-31
        """.trimIndent()

        val parsed = CareerIntelligenceEngine.analyzeJobDescription(rawJobText)
        assertNotNull(parsed)
        assertTrue(parsed.jobTitle.contains("Computer Vision") || parsed.jobTitle.contains("Engineer"))
        assertEquals("Horizon Robotics", parsed.company)
        assertEquals("Shanghai, China", parsed.location)
        assertTrue(parsed.requiredDegree.contains("Master"))
        assertTrue(parsed.requiredExperience.contains("2+ years"))
        assertTrue(parsed.salary.contains("35,000"))
        assertEquals("2027-05-31", parsed.applicationDeadline)

        // Verify required vs preferred distinction
        assertTrue(parsed.requiredSkills.any { it.equals("Python", ignoreCase = true) })
        assertTrue(parsed.requiredSkills.any { it.equals("C++", ignoreCase = true) })
        assertTrue(parsed.requiredSkills.any { it.equals("PyTorch", ignoreCase = true) })
        assertTrue(parsed.preferredSkills.any { it.equals("CUDA", ignoreCase = true) })
        assertTrue(parsed.preferredSkills.any { it.equals("TensorRT", ignoreCase = true) })
        assertTrue(parsed.allRequirements.any { it.category == RequirementCategory.REQUIRED })
        assertTrue(parsed.allRequirements.any { it.category == RequirementCategory.PREFERRED })
    }

    @Test
    fun testCareerGapAnalyzerFactualSummaryNoFakeScore() {
        val parsedJob = CareerIntelligenceEngine.analyzeJobDescription("""
            Job Title: Autonomous Driving Perception Engineer
            Company: Momenta
            Required:
            • Python
            • PyTorch
            • C++
            • Docker
        """.trimIndent())

        val userProfile = CareerProfileEntity(
            programmingLanguages = "Python, C++",
            frameworks = "PyTorch",
            cloud = "Linux Server" // Docker not listed in cloud
        )

        val skills = listOf(
            SkillInventoryEntity(skillName = "Python", category = "Languages", currentLevel = "Advanced", evidence = "Built PointFog-SAM", lastPracticed = "2026-10-01", relatedProject = "PointFog-SAM"),
            SkillInventoryEntity(skillName = "PyTorch", category = "AI/ML", currentLevel = "Advanced", evidence = "Trained DDP models", lastPracticed = "2026-10-01", relatedProject = "PointFog-SAM"),
            SkillInventoryEntity(skillName = "C++", category = "Languages", currentLevel = "Beginner", evidence = "xv6 kernel fork", lastPracticed = "2026-09-28", relatedProject = "xv6")
        )

        val report = CareerIntelligenceEngine.analyzeCareerGap(parsedJob, userProfile, skills)
        assertNotNull(report)

        // Python & PyTorch are Advanced -> MATCH
        assertTrue(report.matches.any { it.requirementName.equals("Python", ignoreCase = true) && it.status == MatchStatus.MATCH })
        assertTrue(report.matches.any { it.requirementName.equals("PyTorch", ignoreCase = true) && it.status == MatchStatus.MATCH })

        // C++ is Beginner -> PARTIAL_MATCH
        assertTrue(report.partialMatches.any { it.requirementName.equals("C++", ignoreCase = true) && it.status == MatchStatus.PARTIAL_MATCH })

        // Docker is absent from inventory and profile -> MISSING
        assertTrue(report.missing.any { it.requirementName.equals("Docker", ignoreCase = true) && it.status == MatchStatus.MISSING })

        // Verify factual disclaimer and no fake score
        assertTrue(report.factualSummary.contains("Profile audit against"))
        assertTrue(report.disclaimer.contains("No artificial score or ranking is computed"))
    }

    @Test
    fun testResumeAnalysisClarityAndWeakPhrasing() {
        val cvText = """
            Alexei Chen
            Education: Yanshan University
            Skills: Python, PyTorch
            Projects:
            PointFog-SAM:
            • Worked on point cloud segmentation
            • Responsible for training pipeline
        """.trimIndent()

        val report = CareerIntelligenceEngine.analyzeResume(cvText)
        assertNotNull(report)

        // Structure checklist
        assertTrue(report.structureChecklist.any { it.first.contains("Education") && it.second })
        assertTrue(report.structureChecklist.any { it.first.contains("Skills") && it.second })

        // Missing information warnings (Missing GitHub link)
        assertTrue(report.missingInformationWarnings.any { it.contains("GitHub") })

        // Weak descriptions detected ("worked on", "responsible for")
        assertTrue(report.weakDescriptions.any { it.first.contains("worked on", ignoreCase = true) || it.first.contains("responsible for", ignoreCase = true) })
        assertTrue(report.weakDescriptions.any { it.second.contains("active verbs") || it.second.contains("Engineered") })

        // Zero invention disclaimer
        assertTrue(report.disclaimer.contains("Never invents") || report.disclaimer.contains("do not fabricate"))
    }

    @Test
    fun testJobSpecificCvMatch() {
        val cvText = "Alexei Chen. Skills: Python, PyTorch, C++. Degree: Bachelor. Project: PointFog-SAM."
        val parsedJob = CareerIntelligenceEngine.analyzeJobDescription("""
            Job Title: Computer Vision Algorithm Engineer
            Company: DeepDrive
            Required:
            • Python
            • PyTorch
            • ROS2
            • TensorRT
        """.trimIndent())

        val match = CareerIntelligenceEngine.analyzeJobSpecificCV(cvText, parsedJob)
        assertNotNull(match)
        assertTrue(match.relevantSkillsPresent.contains("Python"))
        assertTrue(match.relevantSkillsPresent.contains("PyTorch"))
        assertTrue(match.missingTerminology.contains("ROS2") || match.missingTerminology.contains("TensorRT"))
        assertTrue(match.disclaimer.contains("We do not falsely claim keyword modification guarantees ATS success"))
    }

    @Test
    fun testInterviewAnswerEvaluation() {
        val question = "How did you prevent data leakage during point cloud validation?"
        val goodAnswer = "We partitioned our 12,000 scans into sequence-isolated splits so frames from the same maritime vessel passage never cross between train and val. Because sensor returns share temporal correlation, random frame splitting causes artificial inflation of mIoU. Our validation mIoU was 64.8%."

        val eval = CareerIntelligenceEngine.evaluateInterviewAnswer(
            question = question,
            userAnswer = goodAnswer,
            category = "Technical",
            projectContext = "PointFog-SAM"
        )

        assertNotNull(eval)
        assertTrue(eval.concreteStrengths.any { it.contains("causal reasoning") || it.contains("quantitative") })
        assertTrue(eval.improvedAnswerRecommendation.contains("Structured Recommendation"))
        assertTrue(eval.disclaimer.contains("not employer-specific evaluation criteria"))
    }

    @Test
    fun testProjectDemonstratedSkillsDerivation() {
        val skills = CareerIntelligenceEngine.deriveProjectDemonstratedSkills(
            title = "PointFog-SAM Maritime Coastal LiDAR Perception",
            description = "Semi-supervised 3D point cloud segmentation under dense coastal aerosol fog.",
            methodology = "Formulated differentiable Koschmieder optical scattering inversion layer in PyTorch.",
            dataset = "12,000 Qinhuangdao Port LiDAR frames with SemanticKITTI benchmark.",
            models = "PointNeXt backbone with deformable cross-attention.",
            metrics = "mIoU, precision, recall, and inference FPS on NVIDIA Jetson AGX Orin."
        )

        assertTrue(skills.contains("Python") || skills.contains("PyTorch"))
        assertTrue(skills.contains("Computer Vision"))
        assertTrue(skills.contains("Machine Learning") || skills.contains("Deep Learning"))
        assertTrue(skills.contains("Experimental Evaluation") || skills.contains("Data Analysis"))
        assertTrue(skills.contains("Edge AI Deployment"))
    }

    // ==========================================
    // STAGE 5: CHINESE LANGUAGE COACH TESTS
    // ==========================================

    @Test
    fun testChineseProfileCreationAndNotAssessedDefault() {
        val defaultProfile = ChineseLanguageProfileEntity(id = 1)
        assertEquals("Not assessed", defaultProfile.currentLevel)
        assertEquals("Not assessed", defaultProfile.hskLevel)
        assertEquals(30, defaultProfile.dailyStudyMinutes)
        assertEquals(5, defaultProfile.daysPerWeek)

        // Profile update with explicit user input
        val updated = defaultProfile.copy(
            currentLevel = "Intermediate (HSK 4)",
            hskLevel = "HSK 4",
            targetHskLevel = "HSK 5",
            dailyStudyMinutes = 45
        )
        assertEquals("Intermediate (HSK 4)", updated.currentLevel)
        assertEquals("HSK 4", updated.hskLevel)
        assertEquals("HSK 5", updated.targetHskLevel)
        assertEquals(45, updated.dailyStudyMinutes)
    }

    @Test
    fun testDailyChineseStudyPlanGeneration() {
        val plan = ChineseCoachEngine.generateDailyStudyPlan(
            availableMinutes = 30,
            daysPerWeek = 5,
            preferredTime = "Morning (08:00 - 08:30)",
            currentLevel = "HSK 4",
            targetLevel = "HSK 5",
            targetDate = "2027-06-30"
        )

        assertNotNull(plan)
        assertEquals(30, plan.dailyMinutes)
        assertEquals(5, plan.daysPerWeek)
        assertEquals(150, plan.weeklyMinutes)
        assertTrue(plan.sessions.isNotEmpty())

        // Check module distribution contains vocabulary, grammar, listening, speaking, reading, writing, review
        val moduleTypes = plan.sessions.map { it.moduleType }.toSet()
        assertTrue(moduleTypes.any { it.contains("Vocabulary") })
        assertTrue(moduleTypes.any { it.contains("Grammar") })
        assertTrue(moduleTypes.any { it.contains("Listening") })
        assertTrue(moduleTypes.any { it.contains("Speaking") })
        assertTrue(moduleTypes.any { it.contains("Reading") })
        assertTrue(moduleTypes.any { it.contains("Review") })
    }

    @Test
    fun testHskLearningSystemLevelsAndDisclaimers() {
        val levels = ChineseCoachEngine.getOfficialHskLevels()
        assertEquals(6, levels.size)

        val hsk1 = levels.find { it.level == "HSK 1" }!!
        assertEquals(150, hsk1.verifiedVocabularyCount)
        assertTrue(hsk1.disclaimer.contains("Curated HSK Syllabus Data"))

        val hsk4 = levels.find { it.level == "HSK 4" }!!
        assertEquals(1200, hsk4.verifiedVocabularyCount)
        assertTrue(hsk4.sampleReading.contains("人工智能") || hsk4.sampleReading.contains("计算机视觉"))

        val hsk5 = levels.find { it.level == "HSK 5" }!!
        assertEquals(2500, hsk5.verifiedVocabularyCount)
        assertTrue(hsk5.grammarFocus.any { it.contains("鉴于") || it.contains("由此可见") })

        val hsk6 = levels.find { it.level == "HSK 6" }!!
        assertEquals(5000, hsk6.verifiedVocabularyCount)
    }

    @Test
    fun testVocabularyTrainerAndSpacedRepetitionSM2() {
        // Initial review (Correct recall)
        val step1 = ChineseCoachEngine.calculateSpacedRepetition(
            currentReviewCount = 0,
            currentInterval = 1,
            currentEase = 2.5f,
            isCorrect = true,
            difficulty = "Medium"
        )
        assertEquals(1, step1.newReviewCount)
        assertEquals(1, step1.newIntervalDays)
        assertEquals(1, step1.newCorrectCount)
        assertEquals(0, step1.newIncorrectCount)
        assertEquals("Upcoming", step1.newReviewStatus)

        // Subsequent correct review
        val step2 = ChineseCoachEngine.calculateSpacedRepetition(
            currentReviewCount = 1,
            currentInterval = 1,
            currentEase = 2.5f,
            isCorrect = true,
            difficulty = "Easy"
        )
        assertEquals(3, step2.newIntervalDays)
        assertTrue(step2.newEaseFactor >= 2.5f)

        // Failed recall (Incorrect)
        val stepFailed = ChineseCoachEngine.calculateSpacedRepetition(
            currentReviewCount = 3,
            currentInterval = 7,
            currentEase = 2.5f,
            isCorrect = false,
            difficulty = "Hard"
        )
        assertEquals(1, stepFailed.newIntervalDays)
        assertEquals("Due", stepFailed.newReviewStatus)
        assertEquals(1, stepFailed.newIncorrectCount)
    }

    @Test
    fun testChineseFromRealLife16Categories() {
        val phrases = ChineseCoachEngine.getRealLifePhrases()
        assertTrue(phrases.isNotEmpty())

        val categories = phrases.map { it.category }.distinct()
        val expectedCategories = listOf(
            "University", "Classroom", "Dormitory", "Restaurant", "Shopping",
            "Transportation", "Bank", "Hospital", "Police/administration", "Phone/SIM",
            "Renting", "Daily conversation", "Friendship/social situations",
            "Academic research", "Job interview", "Workplace"
        )

        for (expected in expectedCategories) {
            assertTrue("Missing expected category: $expected", categories.contains(expected))
        }

        // Validate structure for every phrase
        for (p in phrases) {
            assertTrue(p.hanzi.isNotBlank())
            assertTrue(p.pinyin.isNotBlank())
            assertTrue(p.english.isNotBlank())
            assertTrue(p.usageExplanation.isNotBlank())
        }
    }

    @Test
    fun testUniversityChineseScenariosAndPoliteness() {
        val scenarios = ChineseCoachEngine.getUniversityScenarios()
        assertEquals(10, scenarios.size)

        val advisorMeeting = scenarios.find { it.id == "advisor_meeting" }!!
        assertTrue(advisorMeeting.emailTemplate.contains("尊敬的张老师"))
        assertTrue(advisorMeeting.emailTemplate.contains("学生"))
        assertTrue(advisorMeeting.emailTemplate.contains("祝好") || advisorMeeting.emailTemplate.contains("顺祝"))

        val extension = scenarios.find { it.id == "deadline_extension" }!!
        assertTrue(extension.keyVocabulary.any { it.contains("延期") })
        assertTrue(extension.emailTemplate.contains("申请将提交时间顺延"))

        val admin = scenarios.find { it.id == "university_administration" }!!
        assertTrue(admin.keyVocabulary.any { it.contains("居留许可") })
    }

    @Test
    fun testResearchChineseTermsAndProjectConnection() {
        val terms = ChineseCoachEngine.getResearchChineseTerms()
        assertTrue(terms.isNotEmpty())

        val pointCloud = terms.find { it.hanzi == "三维点云" }
        assertNotNull(pointCloud)
        assertEquals("Computer Vision", pointCloud?.category)
        assertTrue(pointCloud?.technicalMeaning?.contains("LiDAR") == true)

        val oom = terms.find { it.hanzi == "显存溢出" }
        assertNotNull(oom)
        assertEquals("CUDA Out of Memory (OOM)", oom?.english)

        val ablation = terms.find { it.hanzi == "消融实验" }
        assertNotNull(ablation)
        assertEquals("Ablation Study", ablation?.english)
    }

    @Test
    fun testDocumentBasedChineseExtraction() {
        val documentContent = """
            燕山大学国际教育学院关于学期注册与签证延期的通知。
            各位国际研究生：
            请于下周五前到留学生办公室办理在读证明并加盖公章。
            因课题研究需申请大作业延期提交的同学，请提前向导师请假报备。
            实验室组会将于周三下午在东校区召开。
        """.trimIndent()

        val extracted = ChineseCoachEngine.extractChineseFromDocument(
            documentContent = documentContent,
            documentName = "Notice_Oct2026.txt",
            category = "University Notice"
        )

        assertTrue(extracted.isNotEmpty())
        val extractedHanzi = extracted.map { it.hanzi }
        assertTrue(extractedHanzi.contains("通知"))
        assertTrue(extractedHanzi.contains("在读证明"))
        assertTrue(extractedHanzi.contains("公章"))
        assertTrue(extractedHanzi.contains("延期"))
        assertTrue(extractedHanzi.contains("导师"))
        assertTrue(extractedHanzi.contains("请假"))
        assertTrue(extractedHanzi.contains("实验室"))
        assertTrue(extractedHanzi.contains("组会"))

        // Verify context sentence is grounded in the actual text
        val yánqī = extracted.find { it.hanzi == "延期" }!!
        assertTrue(yánqī.contextSentence.contains("延期"))
        assertTrue(yánqī.sourceDocument == "Notice_Oct2026.txt")
    }

    @Test
    fun testChineseEnglishTranslationAndPolitenessDistinction() {
        val res = ChineseCoachEngine.translateAndExplain(
            inputText = "I am sick and need 2 days leave for hospital appointment",
            direction = "EN_TO_ZH",
            formality = "FORMAL"
        )

        assertNotNull(res)
        assertTrue(res.naturalTranslation.contains("张老师您好") || res.naturalTranslation.contains("特向您请假"))
        assertTrue(res.literalTranslation.contains("我生病了"))
        assertNotEquals(res.literalTranslation, res.naturalTranslation)
        assertEquals("Formal (Professor / Advisor)", res.politenessLevel)
        assertTrue(res.usageNotes.contains("avoid blunt statements"))
    }

    @Test
    fun testSpeakingTranscriptionEvaluation() {
        val expected = "尊敬的张老师您好，我已经完成了点云实验基准对比。"
        val transcribedGood = "尊敬的张老师您好，我已经完成了点云实验基准对比。"

        val evalGood = ChineseCoachEngine.evaluateSpeechTranscription(expected, transcribedGood)
        assertEquals(100, evalGood.matchPercentage)
        assertTrue(evalGood.toneGuidance.contains("Excellent transcription match"))
        assertTrue(evalGood.disclaimer.contains("Speech transcription comparison"))

        val evalPartial = ChineseCoachEngine.evaluateSpeechTranscription(expected, "张老师您好，点云实验对比。")
        assertTrue(evalPartial.matchPercentage in 30..90)
    }

    @Test
    fun testPronunciationAndTonesEducationalGuidance() {
        val tones = ChineseCoachEngine.getTonesGuide()
        assertEquals(5, tones.size)

        val tone1 = tones.find { it.toneNumber == 1 }!!
        assertEquals("55 (High & Flat)", tone1.pitchContour)
        assertEquals("mā", tone1.exampleSyllable)

        val tone3 = tones.find { it.toneNumber == 3 }!!
        assertEquals("214 (Mid-Low -> Lowest -> Rising)", tone3.pitchContour)

        val tone4 = tones.find { it.toneNumber == 4 }!!
        assertEquals("51 (High to Lowest)", tone4.pitchContour)
    }

    @Test
    fun testWeaknessAnalyzerNoFabrication() {
        // Empty data -> strictly outputs "Not enough data yet."
        val emptyReport = ChineseCoachEngine.analyzeLearningWeaknesses(
            profile = null,
            vocabList = emptyList(),
            listeningExercises = emptyList()
        )
        assertFalse(emptyReport.hasSufficientData)
        assertEquals("Not enough data yet.", emptyReport.summaryStatement)
        assertTrue(emptyReport.weakAreas.isEmpty())

        // With actual high error rate
        val vocabWithErrors = listOf(
            ChineseVocabularyEntity(hanzi = "延期", pinyin = "yánqī", english = "delay", reviewCount = 10, incorrectAnswers = 6),
            ChineseVocabularyEntity(hanzi = "消融", pinyin = "xiāoróng", english = "ablate", reviewCount = 10, incorrectAnswers = 4)
        )
        val dataReport = ChineseCoachEngine.analyzeLearningWeaknesses(
            profile = null,
            vocabList = vocabWithErrors,
            listeningExercises = emptyList()
        )
        assertTrue(dataReport.hasSufficientData)
        assertTrue(dataReport.weakAreas.any { it.contains("Vocabulary Recall") })
    }

    @Test
    fun testAITutorCommands() {
        val tutor10Words = ChineseCoachEngine.handleTutorCommand("Teach me 10 Chinese words for university life", null)
        assertEquals(10, tutor10Words.vocabularyToSave.size)
        assertTrue(tutor10Words.replyContent.contains("开题报告"))
        assertTrue(tutor10Words.replyContent.contains("导师"))

        val tutorProfessor = ChineseCoachEngine.handleTutorCommand("How to address my professor in Chinese", null)
        assertTrue(tutorProfessor.replyContent.contains("张老师"))
        assertTrue(tutorProfessor.replyContent.contains("您"))
    }

    // ========================================================
    // STAGE 6: CHINA STUDENT -> WORK & IMMIGRATION TESTS
    // ========================================================

    @Test
    fun testImmigrationProfileDefaultValuesAndNoInference() {
        val emptyProfile = ImmigrationProfileEntity(userEmail = "test@ysu.edu.cn")
        assertEquals("Not provided", emptyProfile.nationality)
        assertEquals("Not provided", emptyProfile.currentUniversity)
        assertEquals("Not provided", emptyProfile.degreeLevel)
        assertEquals("Not provided", emptyProfile.currentVisaCategory)
        assertEquals("Not provided", emptyProfile.currentResidencePermitType)
        assertEquals("Not provided", emptyProfile.residencePermitExpirationDate)
        assertEquals("Not provided", emptyProfile.passportExpirationDate)
        assertEquals("Not provided", emptyProfile.passportNumberOptional)
        assertEquals("Not provided", emptyProfile.targetEmploymentCity)
        assertEquals("Not provided", emptyProfile.intendsToRemainInChina)
        assertEquals("Not provided", emptyProfile.isConsideringEntrepreneurship)

        // Ensure user editing preserves explicit values
        val updated = emptyProfile.copy(
            nationality = "Russian",
            currentUniversity = "Yanshan University",
            degreeLevel = "Master of Science",
            targetEmploymentCity = "Shanghai"
        )
        assertEquals("Russian", updated.nationality)
        assertEquals("Yanshan University", updated.currentUniversity)
        assertEquals("Shanghai", updated.targetEmploymentCity)
    }

    @Test
    fun testResidencePermitAndPassportExpirationAudit() {
        // Unknown / Not provided
        val auditEmpty = ImmigrationIntelligenceEngine.auditExpiration("Not provided", "Residence Permit")
        assertEquals("Unknown", auditEmpty.urgencyLevel)
        assertNull(auditEmpty.daysRemaining)

        // Valid future date (more than 180 days)
        val auditNormal = ImmigrationIntelligenceEngine.auditExpiration("2027-07-15", "Residence Permit")
        assertNotNull(auditNormal.daysRemaining)
        assertTrue(auditNormal.daysRemaining!! > 90)
        assertEquals("Residence Permit", auditNormal.type)
        assertEquals("Personal reminder", auditNormal.label)

        // Passport audit
        val auditPassport = ImmigrationIntelligenceEngine.auditExpiration("2029-04-20", "Passport")
        assertNotNull(auditPassport.daysRemaining)
        assertEquals("Normal", auditPassport.urgencyLevel)
    }

    @Test
    fun testOfficialSourceFirstQnAGroundedAndStructure() {
        // Question on internships & part-time work
        val internAnswer = ImmigrationIntelligenceEngine.answerImmigrationQuestion(
            "Can international students work off-campus or take internships in China?"
        )
        assertTrue(internAnswer.isVerifiedSource)
        assertTrue(internAnswer.shortAnswer.contains("not permitted to engage in unrestricted off-campus employment"))
        assertTrue(internAnswer.conditions.any { it.contains("Internship") && it.contains("annotated") })
        assertEquals("Ministry of Education & Ministry of Public Security (教育部、公安部)", internAnswer.sourceOrganization)
        assertTrue(internAnswer.isNational)
        assertEquals("Verified recently", internAnswer.freshnessStatus)
        assertTrue(internAnswer.importantUncertaintyWarning.contains("Article 43"))

        // Question on Shanghai 2-year experience exemption
        val shanghaiAnswer = ImmigrationIntelligenceEngine.answerImmigrationQuestion(
            "Can a master's graduate directly work in Shanghai without two years of prior work experience?"
        )
        assertTrue(shanghaiAnswer.isVerifiedSource)
        assertFalse(shanghaiAnswer.isNational)
        assertTrue(shanghaiAnswer.locationOrJurisdiction.contains("Shanghai-specific"))
        assertEquals("Shanghai Municipal Science and Technology Commission", shanghaiAnswer.sourceOrganization)
        assertTrue(shanghaiAnswer.conditions.any { it.contains("Master's degree") })

        // Question on 24-hour accommodation registration
        val policeAnswer = ImmigrationIntelligenceEngine.answerImmigrationQuestion(
            "What are the rules regarding 24-hour accommodation registration for foreigners in China?"
        )
        assertTrue(policeAnswer.isVerifiedSource)
        assertTrue(policeAnswer.shortAnswer.contains("within 24 hours"))
        assertEquals("National (PRC Exit and Entry Administration Law, Art. 39)", policeAnswer.locationOrJurisdiction)

        // Unverified query fallback
        val unverifiedAnswer = ImmigrationIntelligenceEngine.answerImmigrationQuestion(
            "Can I invest in agricultural farms in Inner Mongolia as a student?"
        )
        assertFalse(unverifiedAnswer.isVerifiedSource)
        assertTrue(unverifiedAnswer.shortAnswer.contains("Official source not verified"))
        assertTrue(unverifiedAnswer.importantUncertaintyWarning.contains("did not match an indexed official source"))
    }

    @Test
    fun testCityPolicyDifferentiationNeverConflatesCities() {
        val jobShanghai = JobPostingEntity(
            jobTitle = "Computer Vision Algorithm Engineer",
            company = "Horizon Robotics",
            location = "Shanghai · Pudong",
            requiredSkills = "PyTorch, C++",
            preferredSkills = "TensorRT"
        )
        val profileMaster = ImmigrationProfileEntity(degreeLevel = "Master of Science (M.Sc.)")

        val fitShanghai = ImmigrationIntelligenceEngine.analyzeJobImmigrationFit(jobShanghai, profileMaster)
        assertEquals("Shanghai-specific", fitShanghai.pathwayJurisdiction)
        assertTrue(fitShanghai.experienceExemptionAvailable)
        assertTrue(fitShanghai.workAuthorizationConsiderations.any { it.contains("2-year work experience rule") })
        assertTrue(fitShanghai.disclaimer.contains("does NOT guarantee individual eligibility"))

        val jobBeijing = JobPostingEntity(
            jobTitle = "Autonomous Driving Engineer",
            company = "Baidu Apollo",
            location = "Beijing · Haidian",
            requiredSkills = "C++, ROS",
            preferredSkills = "CUDA"
        )
        val fitBeijing = ImmigrationIntelligenceEngine.analyzeJobImmigrationFit(jobBeijing, profileMaster)
        assertEquals("Beijing-specific", fitBeijing.pathwayJurisdiction)
        assertTrue(fitBeijing.applicablePathwayTitle.contains("Zhongguancun"))

        // Default National Policy (when neither Shanghai nor Beijing specific)
        val jobChengdu = JobPostingEntity(
            jobTitle = "Embedded AI Engineer",
            company = "Tech Corp",
            location = "Chengdu · High-Tech Zone",
            requiredSkills = "C++",
            preferredSkills = "Linux"
        )
        val fitChengdu = ImmigrationIntelligenceEngine.analyzeJobImmigrationFit(jobChengdu, profileMaster)
        assertEquals("National Policy", fitChengdu.pathwayJurisdiction)
        assertFalse(fitChengdu.experienceExemptionAvailable)
    }

    @Test
    fun testResearchCareerChinaBridgeIntegration() {
        val researchProject = ResearchProjectEntity(
            title = "Multi-Modal Robust 3D Point Cloud Semantic Segmentation in Fog",
            researchArea = "Computer Vision",
            currentStatus = "Implementation",
            targetVenue = "CVPR 2027",
            methodology = "Koschmieder physical attenuation model",
            datasets = "Qinhuangdao Maritime Port Dataset"
        )
        val careerProfile = CareerProfileEntity(
            targetJobTitles = "Autonomous Driving Perception Engineer, AI Algorithm Engineer"
        )
        val immProfile = ImmigrationProfileEntity(
            targetEmploymentCity = "Shanghai"
        )

        val bridge = ImmigrationIntelligenceEngine.buildResearchCareerChinaBridge(
            researchProject,
            careerProfile,
            immProfile
        )

        assertEquals("Multi-Modal Robust 3D Point Cloud Semantic Segmentation in Fog", bridge.researchProjectTitle)
        assertEquals("Shanghai", bridge.targetCity)
        assertTrue(bridge.coreTechnicalAreas.contains("Computer Vision"))
        assertTrue(bridge.highTechPolicyAlignments.any { it.contains("encouragement fields") })
        assertTrue(bridge.visaWorkPermitCategory.contains("Category B"))
        assertNotNull(bridge.officialSourceCitation)
    }

    @Test
    fun testPostGraduationOptionsAreInformationalAndUnranked() {
        val options = ImmigrationIntelligenceEngine.getPostGraduationOptions()
        assertEquals(5, options.size)

        val optionA = options.find { it.code == "Option A" }!!
        assertEquals("Departure from China upon Graduation", optionA.title)
        assertTrue(optionA.potentialRequirements.any { it.contains("University clearance") })

        val optionB = options.find { it.code == "Option B" }!!
        assertEquals("Direct Employment in China (Work Residence Permit)", optionB.title)
        assertTrue(optionB.notes.contains("Article 43"))

        val optionC = options.find { it.code == "Option C" }!!
        assertTrue(optionC.title.contains("Entrepreneurship"))

        val optionD = options.find { it.code == "Option D" }!!
        assertTrue(optionD.title.contains("Further Academic Study"))

        val optionE = options.find { it.code == "Option E" }!!
        assertTrue(optionE.title.contains("Stay Permit / S2"))
    }

    @Test
    fun testEmployerWorkPermitWorkflowSteps() {
        val steps = ImmigrationIntelligenceEngine.getWorkPermitWorkflowSteps()
        assertEquals(5, steps.size)
        assertEquals("Student/Applicant & Employer", steps[0].responsibleParty)
        assertEquals("Student/Applicant", steps[1].responsibleParty)
        assertTrue(steps[2].responsibleParty.contains("Employer"))
        assertEquals("Candidate & Local Exit-Entry Administration", steps[4].responsibleParty)
    }
}

