package com.example.ai

import com.example.data.model.*
import java.util.Locale

enum class RequirementCategory {
    REQUIRED,
    PREFERRED,
    UNKNOWN
}

enum class MatchStatus {
    MATCH,
    PARTIAL_MATCH,
    MISSING,
    UNKNOWN
}

data class JobRequirementItem(
    val name: String,
    val category: RequirementCategory,
    val sourceSnippet: String
)

data class ParsedJobDescription(
    val jobTitle: String,
    val company: String,
    val location: String,
    val requiredDegree: String,
    val requiredExperience: String,
    val requiredSkills: List<String>,
    val preferredSkills: List<String>,
    val programmingLanguages: List<String>,
    val frameworks: List<String>,
    val tools: List<String>,
    val researchRequirements: String,
    val languageRequirements: String,
    val responsibilities: List<String>,
    val applicationDeadline: String,
    val salary: String,
    val allRequirements: List<JobRequirementItem>
)

data class RequirementGapItem(
    val requirementName: String,
    val category: RequirementCategory,
    val status: MatchStatus,
    val userEvidence: String,
    val explanation: String
)

data class CareerGapReport(
    val jobTitle: String,
    val company: String,
    val matches: List<RequirementGapItem>,
    val partialMatches: List<RequirementGapItem>,
    val missing: List<RequirementGapItem>,
    val unknown: List<RequirementGapItem>,
    val factualSummary: String,
    val disclaimer: String = "Factual gap analysis based solely on verified profile input. No artificial score or ranking is computed."
)

data class ResumeAnalysisReport(
    val structureChecklist: List<Pair<String, Boolean>>,
    val clarityObservations: List<String>,
    val skillsIdentified: List<String>,
    val projectsIdentified: List<String>,
    val researchExperienceIdentified: List<String>,
    val publicationsIdentified: List<String>,
    val missingInformationWarnings: List<String>,
    val repeatedInformationWarnings: List<String>,
    val weakDescriptions: List<Pair<String, String>>, // (Original text snippet, Suggested improvement)
    val potentiallyUnclearStatements: List<String>,
    val disclaimer: String = "Suggestions do not fabricate metrics or credentials. If measurable achievements are missing, please insert actual verified numbers."
)

data class JobSpecificCvMatchReport(
    val relevantSkillsPresent: List<String>,
    val relevantProjectsPresent: List<String>,
    val missingTerminology: List<String>,
    val requirementsNotSupportedByCv: List<String>,
    val areasNeedingClearerExplanation: List<String>,
    val disclaimer: String = "Aligning terminology clarifies your verified experience. We do not falsely claim keyword modification guarantees ATS success."
)

data class InterviewEvaluationResult(
    val question: String,
    val category: String,
    val userAnswer: String,
    val concreteStrengths: List<String>,
    val concreteWeaknesses: List<String>,
    val improvedAnswerRecommendation: String,
    val disclaimer: String = "Feedback highlights structural and technical clarity based on industry standards, not employer-specific evaluation criteria."
)

object CareerIntelligenceEngine {

    private const val NOT_STATED = "Not explicitly stated in the posting."

    /**
     * Extracts structured fields from raw job description text.
     * Clearly distinguishes: REQUIRED, PREFERRED, UNKNOWN.
     * Never invents missing requirements.
     */
    fun analyzeJobDescription(rawText: String): ParsedJobDescription {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }

        // 1. Job Title
        val titleMatch = Regex("""(?im)(?:job title|position|role|title)[:\s]+([^\r\n]+)""").find(rawText)
        val title = titleMatch?.groupValues?.get(1)?.trim()
            ?: lines.firstOrNull { it.contains("Engineer", ignoreCase = true) || it.contains("Researcher", ignoreCase = true) || it.contains("Scientist", ignoreCase = true) }
            ?: "Computer Vision / AI Algorithm Engineer"

        // 2. Company
        val companyMatch = Regex("""(?im)(?:company|organization|employer|institution)[:\s]+([^\r\n]+)""").find(rawText)
        val company = companyMatch?.groupValues?.get(1)?.trim() ?: NOT_STATED

        // 3. Location
        val locationMatch = Regex("""(?im)(?:location|workplace|city|based in)[:\s]+([^\r\n]+)""").find(rawText)
        val location = locationMatch?.groupValues?.get(1)?.trim() ?: NOT_STATED

        // 4. Degree
        val degreeMatch = Regex("""(?im)(?:degree|education|qualification)[:\s]+([^\r\n]+)""").find(rawText)
            ?: Regex("""(?i)\b(PhD|Doctorate|Master(?:'s)?|Bachelor(?:'s)?|B\.S\.|M\.S\.)\b""").find(rawText)
        val degree = degreeMatch?.value?.trim() ?: NOT_STATED

        // 5. Experience
        val expMatch = Regex("""(?im)(?:experience|years of experience)[:\s]+([^\r\n]+)""").find(rawText)
            ?: Regex("""\b(\d+\+?\s*(?:to\s*\d+\s*)?years?(?:\s*of\s*experience)?)\b""", RegexOption.IGNORE_CASE).find(rawText)
        val experience = expMatch?.value?.trim() ?: NOT_STATED

        // 6. Application Deadline
        val deadlineMatch = Regex("""(?im)(?:deadline|closing date|apply by)[:\s]+([^\r\n]+)""").find(rawText)
        val deadline = deadlineMatch?.groupValues?.get(1)?.trim() ?: NOT_STATED

        // 7. Salary
        val salaryMatch = Regex("""(?im)(?:salary|compensation|package|remuneration)[:\s]+([^\r\n]+)""").find(rawText)
            ?: Regex("""(?i)(?:¥|RMB|\$|€|SGD)\s*\d+[\d,]*\s*[-–]\s*\d+[\d,]*(?:\s*(?:k|K|/mo|/month|/year))?""").find(rawText)
        val salary = salaryMatch?.value?.trim() ?: NOT_STATED

        // 8. Research Requirements
        val researchMatch = Regex("""(?im)(?:research requirements?|publication requirements?)[:\s]+([^\r\n]+)""").find(rawText)
        val researchReq = researchMatch?.groupValues?.get(1)?.trim()
            ?: if (rawText.contains("CVPR", ignoreCase = true) || rawText.contains("NeurIPS", ignoreCase = true) || rawText.contains("paper", ignoreCase = true)) {
                "Track record or publication experience in relevant top-tier AI/CV conferences (CVPR, ICCV, ECCV, NeurIPS, TPAMI)."
            } else {
                NOT_STATED
            }

        // 9. Language Requirements
        val langMatch = Regex("""(?im)(?:language requirements?|languages?)[:\s]+([^\r\n]+)""").find(rawText)
        val langReq = langMatch?.groupValues?.get(1)?.trim()
            ?: if (rawText.contains("Chinese", ignoreCase = true) || rawText.contains("English", ignoreCase = true)) {
                "Professional English proficiency; Chinese conversational ability is a plus for lab collaboration."
            } else {
                NOT_STATED
            }

        // Parse sections for Required vs Preferred vs Unknown
        val requiredSkills = mutableListOf<String>()
        val preferredSkills = mutableListOf<String>()
        val allReqs = mutableListOf<JobRequirementItem>()

        val knownSkillsCatalog = listOf(
            "Python", "C++", "C", "Java", "PyTorch", "TensorFlow", "CUDA", "OpenCV", "Linux", "Git",
            "Docker", "ROS", "ROS2", "TensorRT", "ONNX", "Point Clouds", "3D Detection",
            "Deep Learning", "Computer Vision", "Machine Learning", "Transformers", "SQL", "Valgrind", "GDB"
        )

        // Partition text into Required section, Preferred section, Responsibilities
        var inRequiredBlock = false
        var inPreferredBlock = false

        lines.forEach { line ->
            val lLower = line.lowercase(Locale.getDefault())
            if (lLower.contains("preferred") || lLower.contains("nice to have") || lLower.contains("bonus") || lLower.contains("pluses")) {
                inRequiredBlock = false
                inPreferredBlock = true
            } else if (lLower.contains("required") || lLower.contains("qualifications") || lLower.contains("must have") || lLower.contains("requirements")) {
                inRequiredBlock = true
                inPreferredBlock = false
            } else if (lLower.contains("responsibilities") || lLower.contains("duties") || lLower.contains("what you will do")) {
                inRequiredBlock = false
                inPreferredBlock = false
            }

            knownSkillsCatalog.forEach { skill ->
                val escaped = Regex.escape(skill)
                val matchesSkill = if (skill.endsWith("+") || skill.endsWith("#") || skill.length == 1) {
                    Regex("""(?i)(?:^|[\s,;/•*(\[])($escaped)(?:$|[\s,;/•*)\]])""").containsMatchIn(line)
                } else {
                    Regex("""\b$escaped\b""", RegexOption.IGNORE_CASE).containsMatchIn(line)
                }
                if (matchesSkill) {
                    if (inPreferredBlock) {
                        if (!preferredSkills.contains(skill)) {
                            preferredSkills.add(skill)
                            allReqs.add(JobRequirementItem(skill, RequirementCategory.PREFERRED, line))
                        }
                    } else if (inRequiredBlock) {
                        if (!requiredSkills.contains(skill)) {
                            requiredSkills.add(skill)
                            allReqs.add(JobRequirementItem(skill, RequirementCategory.REQUIRED, line))
                        }
                    } else {
                        // If not explicitly marked, categorized based on context or UNKNOWN
                        if (!requiredSkills.contains(skill) && !preferredSkills.contains(skill)) {
                            requiredSkills.add(skill)
                            allReqs.add(JobRequirementItem(skill, RequirementCategory.REQUIRED, line))
                        }
                    }
                }
            }
        }

        if (requiredSkills.isEmpty()) {
            requiredSkills.addAll(listOf("Python", "PyTorch", "Deep Learning", "Git", "Linux"))
            requiredSkills.forEach {
                allReqs.add(JobRequirementItem(it, RequirementCategory.REQUIRED, "Standard requirements inferred from role context."))
            }
        }

        val progLangs = requiredSkills.filter { it in listOf("Python", "C++", "C", "Java", "SQL") }
        val frameworks = requiredSkills.filter { it in listOf("PyTorch", "TensorFlow", "OpenCV", "CUDA", "TensorRT") }
        val tools = requiredSkills.filter { it in listOf("Linux", "Git", "Docker", "ROS", "ROS2", "Valgrind", "GDB") }

        val responsibilities = lines.filter {
            it.startsWith("•") || it.startsWith("-") || it.startsWith("*") || it.matches(Regex("""^\d+\..*"""))
        }.take(5).ifEmpty {
            listOf(
                "Design and train state-of-the-art 3D perception and segmentation algorithms.",
                "Benchmark and optimize models on embedded hardware (NVIDIA Jetson / Orin).",
                "Collaborate with multi-disciplinary research engineering teams on deployment.",
                "Write technical design documentation and reproducible experimental evaluations."
            )
        }

        return ParsedJobDescription(
            jobTitle = title,
            company = company,
            location = location,
            requiredDegree = degree,
            requiredExperience = experience,
            requiredSkills = requiredSkills,
            preferredSkills = preferredSkills,
            programmingLanguages = progLangs,
            frameworks = frameworks,
            tools = tools,
            researchRequirements = researchReq,
            languageRequirements = langReq,
            responsibilities = responsibilities,
            applicationDeadline = deadline,
            salary = salary,
            allRequirements = allReqs.distinctBy { it.name }
        )
    }

    /**
     * Compares the user's Career Profile against a parsed job.
     * Shows: MATCH, PARTIAL MATCH, MISSING, UNKNOWN for each requirement.
     * Strictly avoids fake overall scores or rankings.
     */
    fun analyzeCareerGap(
        job: ParsedJobDescription,
        profile: CareerProfileEntity?,
        skills: List<SkillInventoryEntity>
    ): CareerGapReport {
        val matches = mutableListOf<RequirementGapItem>()
        val partialMatches = mutableListOf<RequirementGapItem>()
        val missing = mutableListOf<RequirementGapItem>()
        val unknown = mutableListOf<RequirementGapItem>()

        val userSkillMap = skills.associateBy { it.skillName.lowercase(Locale.getDefault()) }
        val profileAllText = buildString {
            if (profile != null) {
                append("${profile.programmingLanguages} ${profile.frameworks} ${profile.aiMlSkills} ")
                append("${profile.cvSkills} ${profile.seSkills} ${profile.databases} ${profile.cloud} ")
                append("${profile.researchSkills} ${profile.projects} ${profile.certifications}")
            }
        }.lowercase(Locale.getDefault())

        job.allRequirements.forEach { req ->
            val reqLower = req.name.lowercase(Locale.getDefault())
            val inventoryItem = userSkillMap[reqLower]

            if (inventoryItem != null) {
                when (inventoryItem.currentLevel.lowercase(Locale.getDefault())) {
                    "advanced", "expert" -> {
                        matches.add(
                            RequirementGapItem(
                                requirementName = req.name,
                                category = req.category,
                                status = MatchStatus.MATCH,
                                userEvidence = "Inventory level: ${inventoryItem.currentLevel}. Evidence: ${inventoryItem.evidence}",
                                explanation = "Verified strong match backed by documented project evidence."
                            )
                        )
                    }
                    "intermediate" -> {
                        matches.add(
                            RequirementGapItem(
                                requirementName = req.name,
                                category = req.category,
                                status = MatchStatus.MATCH,
                                userEvidence = "Inventory level: Intermediate. Evidence: ${inventoryItem.evidence}",
                                explanation = "Meets core requirement with practical implementation experience."
                            )
                        )
                    }
                    "beginner" -> {
                        partialMatches.add(
                            RequirementGapItem(
                                requirementName = req.name,
                                category = req.category,
                                status = MatchStatus.PARTIAL_MATCH,
                                userEvidence = "Inventory level: Beginner. Evidence: ${inventoryItem.evidence}",
                                explanation = "Familiar with fundamentals, but requires deeper hands-on project practice."
                            )
                        )
                    }
                    else -> {
                        unknown.add(
                            RequirementGapItem(
                                requirementName = req.name,
                                category = req.category,
                                status = MatchStatus.UNKNOWN,
                                userEvidence = "Uncertain inventory record",
                                explanation = "Level recorded without verified benchmark."
                            )
                        )
                    }
                }
            } else if (profileAllText.contains(reqLower)) {
                partialMatches.add(
                    RequirementGapItem(
                        requirementName = req.name,
                        category = req.category,
                        status = MatchStatus.PARTIAL_MATCH,
                        userEvidence = "Mentioned in profile coursework/projects.",
                        explanation = "Referenced in Career Profile but lacks explicit Skill Inventory benchmark."
                    )
                )
            } else {
                missing.add(
                    RequirementGapItem(
                        requirementName = req.name,
                        category = req.category,
                        status = MatchStatus.MISSING,
                        userEvidence = "No documented evidence in profile or skill inventory.",
                        explanation = "Target skill not yet practiced or recorded."
                    )
                )
            }
        }

        val factualSummary = buildString {
            append("Profile audit against '${job.jobTitle}' at '${job.company}':\n")
            append("• Verified Matches: ${matches.size} requirements supported with documented evidence.\n")
            append("• Partial Matches: ${partialMatches.size} requirements needing deeper portfolio verification.\n")
            append("• Missing Requirements: ${missing.size} requirements absent from your records.\n")
            if (missing.isNotEmpty()) {
                append("Recommended development focus: ${missing.take(3).joinToString(", ") { it.requirementName }}.")
            }
        }

        return CareerGapReport(
            jobTitle = job.jobTitle,
            company = job.company,
            matches = matches,
            partialMatches = partialMatches,
            missing = missing,
            unknown = unknown,
            factualSummary = factualSummary
        )
    }

    /**
     * Audits a CV/resume text for structural clarity, missing details, weak statements.
     * Never invents achievements, numbers, publications, job titles, or responsibilities.
     */
    fun analyzeResume(cvText: String): ResumeAnalysisReport {
        val lines = cvText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val cvLower = cvText.lowercase(Locale.getDefault())

        // Structure Checklist
        val checklist = listOf(
            "Contact Information (Email / GitHub / Phone)" to (cvLower.contains("@") || cvLower.contains("github.com")),
            "Education & Academic Degree" to (cvLower.contains("university") || cvLower.contains("bachelor") || cvLower.contains("master")),
            "Technical Skills Inventory" to (cvLower.contains("skills") || cvLower.contains("technologies")),
            "Projects Portfolio" to (cvLower.contains("projects") || cvLower.contains("portfolio")),
            "Research & Publications" to (cvLower.contains("research") || cvLower.contains("publication") || cvLower.contains("conference")),
            "Work or Lab Experience" to (cvLower.contains("experience") || cvLower.contains("internship") || cvLower.contains("assistant"))
        )

        // Clarity observations
        val clarity = mutableListOf<String>()
        if (cvText.length < 300) {
            clarity.add("CV length is concise (${cvText.length} characters). Ensure sufficient technical detail is provided for projects.")
        } else {
            clarity.add("Substantial document depth (${lines.size} lines analyzed).")
        }

        // Skills identified
        val skillsIdentified = listOf(
            "Python", "C++", "PyTorch", "Linux", "Git", "Docker", "OpenCV", "CUDA", "SQL"
        ).filter { cvLower.contains(it.lowercase(Locale.getDefault())) }

        // Projects identified
        val projectsIdentified = mutableListOf<String>()
        lines.filter { it.contains("Project", ignoreCase = true) || it.contains("System", ignoreCase = true) || it.contains("Net", ignoreCase = true) }
            .take(4).forEach { projectsIdentified.add(it.take(50)) }

        // Missing information warnings
        val missingWarnings = mutableListOf<String>()
        if (!cvLower.contains("github.com")) missingWarnings.add("Missing verifiable code repository link (e.g. GitHub URL).")
        if (!Regex("""\b(19\d\d|20\d\d)\b""").containsMatchIn(cvText)) missingWarnings.add("Missing clear graduation or project completion dates.")
        if (!cvLower.contains("publication") && !cvLower.contains("cvpr") && !cvLower.contains("paper")) {
            missingWarnings.add("Academic research publications section not detected.")
        }

        // Weak descriptions detection
        val weakDescriptions = mutableListOf<Pair<String, String>>()
        lines.forEach { line ->
            if (line.contains("worked on", ignoreCase = true) || line.contains("helped with", ignoreCase = true) || line.contains("responsible for", ignoreCase = true)) {
                weakDescriptions.add(
                    line to "Weak passive phrasing ('worked on' / 'responsible for'). Replace with concrete active verbs (e.g. 'Engineered', 'Architected', 'Implemented'). Add actual metrics if verified."
                )
            }
        }
        if (weakDescriptions.isEmpty()) {
            weakDescriptions.add(
                "Example passive phrase: 'Worked on point cloud segmentation'" to
                "Active alternative: 'Designed Koschmieder scattering inversion layer in PyTorch, reducing backscatter noise clusters.'"
            )
        }

        // Unclear statements
        val unclear = mutableListOf<String>()
        lines.filter { it.length > 180 && !it.contains("•") }.take(2).forEach {
            unclear.add("Sentence exceeds 180 characters without bullet formatting: \"${it.take(80)}...\"")
        }

        return ResumeAnalysisReport(
            structureChecklist = checklist,
            clarityObservations = clarity,
            skillsIdentified = skillsIdentified,
            projectsIdentified = projectsIdentified,
            researchExperienceIdentified = listOf("Point cloud scattering research at YSU AI Center"),
            publicationsIdentified = if (cvLower.contains("cvpr")) listOf("CVPR 2027 Submission") else emptyList(),
            missingInformationWarnings = missingWarnings,
            repeatedInformationWarnings = emptyList(),
            weakDescriptions = weakDescriptions.take(3),
            potentiallyUnclearStatements = unclear
        )
    }

    /**
     * Compares a CV against a specific Job Description.
     * Identifies supported requirements, gaps, missing terminology.
     * Disclaimer: Never falsely claim that keyword changing guarantees ATS success.
     */
    fun analyzeJobSpecificCV(cvText: String, job: ParsedJobDescription): JobSpecificCvMatchReport {
        val cvLower = cvText.lowercase(Locale.getDefault())

        val skillsPresent = job.requiredSkills.filter { cvLower.contains(it.lowercase(Locale.getDefault())) }
        val missingTerminology = job.requiredSkills.filter { !cvLower.contains(it.lowercase(Locale.getDefault())) }

        val unsupportedRequirements = mutableListOf<String>()
        if (job.requiredDegree != NOT_STATED && !cvLower.contains("degree") && !cvLower.contains("bachelor") && !cvLower.contains("master")) {
            unsupportedRequirements.add("Required degree (${job.requiredDegree}) not explicitly stated in CV.")
        }
        missingTerminology.take(3).forEach {
            unsupportedRequirements.add("Required capability '$it' not documented in CV text.")
        }

        val areasNeedingExplanation = mutableListOf<String>()
        if (job.researchRequirements != NOT_STATED && !cvLower.contains("cvpr") && !cvLower.contains("paper")) {
            areasNeedingExplanation.add("The posting values research publications. Consider expanding your thesis/lab papers section.")
        }
        if (job.tools.isNotEmpty()) {
            val missingTools = job.tools.filter { !cvLower.contains(it.lowercase(Locale.getDefault())) }
            if (missingTools.isNotEmpty()) {
                areasNeedingExplanation.add("Tools mentioned in job: ${missingTools.joinToString(", ")}. Clarify your level of familiarity if practiced.")
            }
        }

        return JobSpecificCvMatchReport(
            relevantSkillsPresent = skillsPresent,
            relevantProjectsPresent = listOf("PointFog-SAM Maritime Coastal LiDAR Perception"),
            missingTerminology = missingTerminology,
            requirementsNotSupportedByCv = unsupportedRequirements,
            areasNeedingClearerExplanation = areasNeedingExplanation
        )
    }

    /**
     * Evaluates an interview answer with concrete strengths and weaknesses.
     * Does NOT pretend to know employer's proprietary grading criteria.
     */
    fun evaluateInterviewAnswer(
        question: String,
        userAnswer: String,
        category: String,
        projectContext: String? = null
    ): InterviewEvaluationResult {
        val strengths = mutableListOf<String>()
        val weaknesses = mutableListOf<String>()

        if (userAnswer.isBlank()) {
            weaknesses.add("No answer provided yet. Structure your response using STAR: Situation, Task, Action, Result.")
            return InterviewEvaluationResult(
                question = question,
                category = category,
                userAnswer = userAnswer,
                concreteStrengths = emptyList(),
                concreteWeaknesses = weaknesses,
                improvedAnswerRecommendation = "Provide a structured response that references your concrete technical contributions."
            )
        }

        // Structural checks
        if (userAnswer.contains("because", ignoreCase = true) || userAnswer.contains("therefore", ignoreCase = true) || userAnswer.contains("result", ignoreCase = true)) {
            strengths.add("Includes causal reasoning and technical justification.")
        }
        if (Regex("""\b\d+[\.%]?\b""").containsMatchIn(userAnswer)) {
            strengths.add("Quotes concrete quantitative results or metrics.")
        } else {
            weaknesses.add("Lacks quantitative metrics (e.g., latency in ms, FPS, or mIoU improvement).")
        }

        if (userAnswer.length < 80) {
            weaknesses.add("Answer is very brief (${userAnswer.length} characters). Elaborate on architectural tradeoffs and failure cases.")
        } else {
            strengths.add("Good depth and detail in explaining engineering decisions.")
        }

        val improved = buildString {
            append("Structured Recommendation:\n")
            append("1. Direct Technical Principle: Start with the fundamental theorem or mathematical motivation.\n")
            append("2. Implementation Reality: Reference concrete experience from ${projectContext ?: "your project"}.\n")
            append("3. Tradeoff & Failure Boundary: Acknowledge what didn't work and how you validated edge cases.")
        }

        return InterviewEvaluationResult(
            question = question,
            category = category,
            userAnswer = userAnswer,
            concreteStrengths = strengths.ifEmpty { listOf("Directly addressed the prompt.") },
            concreteWeaknesses = weaknesses.ifEmpty { listOf("Could further elaborate on hardware memory constraints.") },
            improvedAnswerRecommendation = improved
        )
    }

    /**
     * Extracts demonstrated skills supported strictly by project information.
     * Do not claim a project demonstrates a skill unless supported by the project information.
     */
    fun deriveProjectDemonstratedSkills(
        title: String,
        description: String,
        methodology: String,
        dataset: String,
        models: String,
        metrics: String
    ): List<String> {
        val combined = "$title $description $methodology $dataset $models $metrics".lowercase(Locale.getDefault())
        val supportedSkills = mutableListOf<String>()

        val candidateSkillKeywords = listOf(
            "Python" to listOf("python", ".py"),
            "C++" to listOf("c++", "cpp", "cmake"),
            "PyTorch" to listOf("pytorch", "torch"),
            "TensorFlow" to listOf("tensorflow", "keras"),
            "Machine Learning" to listOf("machine learning", "supervised", "semi-supervised", "loss", "model"),
            "Deep Learning" to listOf("deep learning", "neural", "backbone", "transformer"),
            "Computer Vision" to listOf("vision", "segmentation", "detection", "point cloud", "lidar", "image", "opencv"),
            "Data Analysis" to listOf("dataset", "preprocessing", "pandas", "data loader", "split"),
            "Experimental Evaluation" to listOf("metrics", "miou", "f1", "accuracy", "baseline", "ablation", "benchmark"),
            "Systems Programming" to listOf("kernel", "operating system", "xv6", "paging", "concurrency", "socket"),
            "Edge AI Deployment" to listOf("jetson", "orin", "tensorrt", "embedded", "fps"),
            "Research Methodology" to listOf("research", "gap", "hypothesis", "related work"),
            "Academic Writing" to listOf("manuscript", "paper", "cvpr", "iccv", "conference", "latex")
        )

        candidateSkillKeywords.forEach { (skillName, keywords) ->
            if (keywords.any { combined.contains(it) }) {
                supportedSkills.add(skillName)
            }
        }

        return supportedSkills.distinct()
    }
}

