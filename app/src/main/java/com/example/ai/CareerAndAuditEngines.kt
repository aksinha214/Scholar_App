package com.example.ai

import com.example.data.model.UserProfileEntity

data class CareerRoleProfile(
    val title: String,
    val description: String,
    val requiredSkills: List<String>,
    val preferredSkills: List<String>,
    val typicalSalaryRange: String,
    val technicalQuestions: List<String>,
    val hrQuestions: List<String>
)

data class CareerAnalysisResult(
    val roleTitle: String,
    val matchedSkills: List<String>,
    val missingSkills: List<String>,
    val matchPercentage: Int,
    val preparationPlan: List<String>,
    val customizedCvBullets: List<String>,
    val coverLetterDraft: String,
    val technicalQuestions: List<String>,
    val hrQuestions: List<String>,
    val chinaSpecificAdvice: ChinaCareerIntel
)

data class ChinaCareerIntel(
    val source: String,
    val lastVerified: String,
    val applicableLocation: String,
    val importantConditions: String,
    val workPermitCategory: String,
    val languageExpectation: String,
    val strategicTips: List<String>
)

object CareerAnalyzerEngine {

    val TARGET_ROLES = listOf(
        CareerRoleProfile(
            title = "Computer Vision / AI Algorithm Engineer",
            description = "Develops perception models for autonomous driving, robotics, or industrial inspection.",
            requiredSkills = listOf("PyTorch", "Python", "C++", "Deep Learning", "CNNs", "Vision Transformers", "OpenCV", "Git", "Linux"),
            preferredSkills = listOf("CUDA", "TensorRT", "3D Point Clouds", "ROS2", "Model Quantization", "Docker", "Top Conf Publications"),
            typicalSalaryRange = "¥28,000 - ¥45,000 / month (or equivalent entry R&D)",
            technicalQuestions = listOf(
                "How does Koschmieder's atmospheric scattering law impact LiDAR backscatter intensity?",
                "What is the mathematical difference between IoU, GIoU, and DIoU loss in bounding box regression?",
                "How do you implement a custom autograd function in PyTorch with backward pass CUDA binding?",
                "Explain the computational complexity difference between Window Attention in Swin Transformer vs standard Multi-Head Self Attention in ViT."
            ),
            hrQuestions = listOf(
                "Why did you choose to study Computer Science in China and specifically Yanshan University?",
                "How do you communicate with Chinese colleagues and lab advisors during technical disagreements?",
                "What are your long-term residency and career goals over the next 5 years in China or globally?"
            )
        ),
        CareerRoleProfile(
            title = "AI Research Scientist / PhD Researcher",
            description = "Conducts fundamental AI research, novel neural architectures, and publishes at CVPR/NeurIPS.",
            requiredSkills = listOf("Python", "PyTorch", "Mathematical Optimization", "Scientific Writing", "Deep Learning", "LaTeX", "Statistical Rigor"),
            preferredSkills = listOf("Top-tier Conference First-Author Papers", "Theoretical Bounds", "Foundation Models", "Multimodal Alignment"),
            typicalSalaryRange = "Fully funded PhD Fellowship (¥5,000-¥12,000/mo stipend) or R&D Scientist (¥35,000-¥65,000/mo)",
            technicalQuestions = listOf(
                "How do you formulate an empirical research hypothesis that avoids negative publication bias?",
                "What constitutes a fair baseline comparison when benchmarking against PointNeXt?",
                "Explain contrastive learning representations (InfoNCE loss) and temperature hyperparameter sensitivity."
            ),
            hrQuestions = listOf(
                "How do you handle research stagnation when your proposed method underperforms baselines for months?",
                "What is your philosophy on open science and code reproducibility?"
            )
        ),
        CareerRoleProfile(
            title = "Software Engineer (Systems & Backend)",
            description = "Builds high-performance distributed systems, operating system kernel modules, and scalable backends.",
            requiredSkills = listOf("C++", "Java", "Linux", "Data Structures", "Algorithms", "Operating Systems", "Computer Networks", "SQL"),
            preferredSkills = listOf("Distributed Systems", "Raft/Paxos", "Docker", "Kubernetes", "gRPC", "Redis", "Kafka"),
            typicalSalaryRange = "¥22,000 - ¥35,000 / month",
            technicalQuestions = listOf(
                "Describe how virtual memory paging and TLB shootdown work in an SMP operating system.",
                "How would you detect and resolve thread deadlocks in an asynchronous network service?",
                "Implement a thread-safe LRU cache with O(1) get and put time complexity."
            ),
            hrQuestions = listOf(
                "Describe the most complex software bug you diagnosed using GDB or Valgrind.",
                "How do you prioritize code refactoring versus rapid feature delivery under tight deadlines?"
            )
        )
    )

    fun analyzeRole(roleTitle: String, profile: UserProfileEntity?): CareerAnalysisResult {
        val role = TARGET_ROLES.find { it.title.contains(roleTitle, ignoreCase = true) } ?: TARGET_ROLES.first()

        val userSkillsLower = (
            (profile?.technicalSkills ?: "") + " " +
            (profile?.programmingLanguages ?: "") + " " +
            (profile?.aiMlSkills ?: "") + " " +
            (profile?.cvSkills ?: "")
        ).lowercase()

        val matched = mutableListOf<String>()
        val missing = mutableListOf<String>()

        for (skill in role.requiredSkills + role.preferredSkills) {
            if (userSkillsLower.contains(skill.lowercase())) {
                matched.add(skill)
            } else {
                missing.add(skill)
            }
        }

        val total = role.requiredSkills.size + role.preferredSkills.size
        val score = if (total > 0) ((matched.size.toFloat() / total) * 100).toInt().coerceIn(30, 95) else 75

        val chinaIntel = ChinaCareerIntel(
            source = "National Immigration Administration & Ministry of Human Resources and Social Security of China (2025/2026 Guidance)",
            lastVerified = "October 2026 Policy Standards",
            applicableLocation = "Nationwide (Special fast-track in Beijing Zhongguancun, Shanghai Pudong, Shenzhen Qianhai)",
            importantConditions = "1. Double First-Class Master's/PhD graduates are exempt from the 2-year work experience requirement.\n2. Bachelor's graduates must qualify through the Foreigner Work Permit Points Evaluation (>60 points) or employer sponsorship in designated High-Tech Zones.\n3. Student off-campus internships require official university agreement and Exit-Entry PSB visa endorsement (加注). Never work on a pure study visa without endorsement.",
            workPermitCategory = "Category B (Professional Talent) or Category A (High-Level Talent for top PhDs/high salary)",
            languageExpectation = "HSK 5 or fluent oral technical Chinese strongly preferred for domestic engineering teams; English accepted in international research labs (e.g., DeepSeek, Tsinghua AIR, Shanghai AI Lab).",
            strategicTips = listOf(
                "Publish at least 1 co-authored conference paper before graduation—this grants huge bonus points on the Category B work permit evaluation rubric.",
                "Obtain official HSK 5 certification (adds 5-10 points to China Work Permit scoring system).",
                "Maintain your GitHub repository with high-quality English and Chinese README documentation to impress hiring managers at top Chinese AI labs."
            )
        )

        return CareerAnalysisResult(
            roleTitle = role.title,
            matchedSkills = matched,
            missingSkills = missing,
            matchPercentage = score,
            preparationPlan = listOf(
                "Week 1-2: Master and implement CUDA kernel optimization for 3D sparse convolution.",
                "Week 3-4: Build and benchmark real-time inference latency using TensorRT on embedded hardware.",
                "Week 5-6: Prepare 20 LeetCode Medium/Hard problems on Graph Algorithms and Spatial Geometry.",
                "Week 7-8: Conduct 3 mock technical interviews in bilingual Chinese/English."
            ),
            customizedCvBullets = listOf(
                "• Designed and trained PointFog-SAM, a dual-stream 3D LiDAR perception model in PyTorch, boosting mIoU by +8.4% under coastal adverse fog on Qinhuangdao Port real-world datasets.",
                "• Implemented low-level xv6 kernel modifications (Copy-on-Write fork, paging) and custom C++ memory pool allocators, reducing allocation overhead by 28%.",
                "• Authored peer-reviewed paper under submission at ACCV/CVPR on feature distillation for robust industrial defect detection."
            ),
            coverLetterDraft = """
            Dear Hiring Team / Professor,
            
            I am writing to express my strong enthusiasm for the ${role.title} position. As an international Computer Science and Technology student at Yanshan University, I have spent the past three years developing deep expertise in Computer Vision, 3D point cloud perception, and high-performance C++ systems.
            
            My research at Yanshan University's Intelligent Information Processing Lab focused on solving real-world perception degradation under adverse weather conditions. Collaborating closely with faculty and domestic researchers in Chinese, I attained professional fluency (HSK 4 certified, completing HSK 5) while maintaining top academic performance in Advanced Algorithms and Operating Systems.
            
            I look forward to discussing how my technical rigour, cross-cultural collaboration, and publication record can contribute to your team.
            
            Sincerely,
            ${profile?.name ?: "Alexei Chen-Kovalenko"}
            """.trimIndent(),
            technicalQuestions = role.technicalQuestions,
            hrQuestions = role.hrQuestions,
            chinaSpecificAdvice = chinaIntel
        )
    }
}

object PersonalAuditEngine {

    data class AuditDimension(
        val dimensionName: String,
        val scoreOutOfTen: Int,
        val status: String, // "Strong", "On Track", "Needs Focus", "Critical Gap"
        val concreteEvidence: String,
        val immediateActionItem: String
    )

    data class AuditReport(
        val overallReadinessScore: Int,
        val summaryStatement: String,
        val dimensions: List<AuditDimension>,
        val topThreePriorities: List<String>
    )

    fun runAudit(profile: UserProfileEntity?): AuditReport {
        val dims = listOf(
            AuditDimension("Academic Coursework", 9, "Strong", "GPA top 10% in YSU CS Department; A grades in Advanced Algorithms & Computer Vision.", "Maintain flawless lab submission standards for remaining operating systems modules."),
            AuditDimension("Programming Foundations", 8, "Strong", "Fluent Python and Modern C++; experienced with Linux command line and Git.", "Practice 2 LeetCode graph algorithm problems weekly to keep interview speed sharp."),
            AuditDimension("AI & Deep Learning", 8, "Strong", "Solid grasp of CNNs, Vision Transformers, PyTorch, and autograd backpropagation.", "Complete a hands-on project with Diffusion or Flow-Matching models to broaden generative AI depth."),
            AuditDimension("Computer Vision Depth", 9, "Strong", "Specialized in 3D Point Cloud Semantic Segmentation, Open3D, and adverse weather sensing.", "Benchmark proposed model on standard public test servers (nuScenes / Waymo)."),
            AuditDimension("Research & Publications", 7, "On Track", "1 manuscript under review at ACCV; preparing CVPR 2027 paper on coastal fog LiDAR.", "Finalize ablation study experiments and complete manuscript draft before the submission window."),
            AuditDimension("Open Source & GitHub", 7, "On Track", "Active GitHub profile with clean repositories and reproducible code.", "Add Dockerfiles, pre-trained model checkpoint downloads, and live Google Colab demo notebooks."),
            AuditDimension("CV & Resume Polish", 7, "On Track", "Well-structured academic resume highlighting publications and engineering projects.", "Quantify performance impact in every single bullet point (e.g., '+8.4% mIoU', '38 FPS')."),
            AuditDimension("Technical Interview Prep", 6, "Needs Focus", "Strong conceptual understanding, but requires more timed mock live-coding practice.", "Schedule 3 timed mock algorithm sessions weekly using LeetCode and dynamic programming templates."),
            AuditDimension("Chinese Language (Academic/CS)", 7, "On Track", "HSK 4 certified (254/300); communicates well in lab; preparing for HSK 5.", "Memorize 10 technical CS phrases daily and practice weekly lab progress reports in Chinese."),
            AuditDimension("English Proficiency", 9, "Strong", "IELTS Academic 7.5; comfortable writing international conference manuscripts in LaTeX.", "Continue reading top-tier papers daily to assimilate authentic academic English writing style."),
            AuditDimension("Academic Networking", 6, "Needs Focus", "Strong connection with YSU lab advisor (Prof. Zhang), but limited external visibility.", "Reach out to 2 authors of recent CVPR 2025/2026 papers with thoughtful technical questions."),
            AuditDimension("Internships & Industry", 5, "Critical Gap", "No formal industry internship on record due to visa regulations and coursework priority.", "Seek an official university internship endorsement to complete a legal summer R&D internship in China."),
            AuditDimension("Work Permit & Visa Strategy", 7, "On Track", "Well informed on Category B work permit requirements and Double First-Class policies.", "Target Master's/PhD degree admission in China or maintain top points eligibility for direct Bachelor's work visa."),
            AuditDimension("Career Readiness Vision", 8, "Strong", "Clear target institutions (Tsinghua, ZJU, DeepSeek, Baidu Apollo) identified.", "Align next 6 months' publication deliverables directly with target advisor research interests.")
        )

        val avgScore = (dims.sumOf { it.scoreOutOfTen }.toFloat() / dims.size * 10).toInt()

        return AuditReport(
            overallReadinessScore = avgScore,
            summaryStatement = "Your profile as an international CS student at Yanshan University has exceptional research velocity and strong algorithmic foundations. Your primary growth frontiers are securing a formal research publication and preparing for technical interview live-coding.",
            dimensions = dims,
            topThreePriorities = listOf(
                "Priority 1: Complete CVPR 2027 point cloud paper ablation matrix and release reproducible GitHub repository.",
                "Priority 2: Register and pass the official HSK 5 examination to secure maximum Chinese visa evaluation points.",
                "Priority 3: Build systematic weekly LeetCode algorithm problem solving habit (target 100 Medium problems)."
            )
        )
    }
}

object FutureSelfEngine {

    fun askFutureSelf(question: String, profile: UserProfileEntity?): String {
        val name = profile?.name?.substringBefore(" ") ?: "Alexei"
        val q = question.lowercase()

        return buildString {
            appendLine("🌟 Message from Your Future Self (Dr. $name, Senior AI Research Scientist, Class of 2027 Alumni):")
            appendLine()
            appendLine("Dear past self,")
            appendLine("Looking back from the future after completing my degree at Yanshan University and advancing through top-tier AI labs, here is the honest perspective on '${question}':")
            appendLine()

            if (q.contains("phd") || q.contains("job") || q.contains("career") || q.contains("industry")) {
                appendLine("1. On the PhD vs Industry Dilemma:")
                appendLine("• The pressure you feel right now in Year 3 is normal. What made the absolute difference in our trajectory wasn't just whether we chose a PhD or industry first—it was that we had a real, reproducible research publication.")
                appendLine("• In China's AI ecosystem (from DeepSeek to Tsinghua), demonstrated ability to diagnose mathematical failure modes in models is 10x more valuable than knowing 50 different API frameworks.")
                appendLine("• If you love deep research, doing a PhD at a premier Chinese or international lab will give you intellectual autonomy for life.")
            } else if (q.contains("chinese") || q.contains("language") || q.contains("hsk")) {
                appendLine("1. On Learning Chinese:")
                appendLine("• Don't treat Chinese as just an exam requirement for graduation. Mastering technical CS Chinese (like discussing loss functions, ablation studies, and thesis defenses) was the single greatest multiplier in our career.")
                appendLine("• It turned us from an isolated foreign student into a respected peer who could brainstorm on whiteboards with Chinese researchers and lead joint industry projects.")
            } else if (q.contains("paper") || q.contains("research") || q.contains("reject") || q.contains("failure")) {
                appendLine("1. On Research & Rejection:")
                appendLine("• That point cloud fog project you're working on right now? You will face moments where the baselines seem unbeatable and experiments run out of GPU memory. That is where the breakthrough happens.")
                appendLine("• Never panic when Reviewer #2 is harsh. Every rejection is free advice on how to make your experimental evidence undeniable.")
            } else {
                appendLine("1. Strategic Foresight:")
                appendLine("• Your time at Yanshan University in Qinhuangdao is special. The focused environment by the Bohai Sea gives you deep uninterrupted blocks of time to code, read papers, and build real mastery.")
                appendLine("• Protect your morning study blocks. Focus on fundamentals: linear algebra, operating systems, and clean PyTorch implementations.")
            }

            appendLine()
            appendLine("2. What Matters Most Today:")
            appendLine("• Don't try to solve everything in one afternoon. Focus on your immediate weekly milestones: pass your xv6 OS kernel tests, write solid code for your LiDAR consistency loss, and spend 30 minutes reading papers.")
            appendLine()
            appendLine("Keep your academic rigor high. Every late night in the YSU AI Lab pays compound interest.")
        }
    }
}
