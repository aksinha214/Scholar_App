package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val nationality: String,
    val university: String,
    val department: String,
    val degree: String,
    val currentSemester: String,
    val expectedGraduation: String,
    val researchInterests: String,
    val technicalSkills: String,
    val programmingLanguages: String,
    val aiMlSkills: String,
    val cvSkills: String,
    val researchExperience: String,
    val publications: String,
    val projects: String,
    val githubUrl: String,
    val certifications: String,
    val chineseProficiency: String,
    val englishProficiency: String,
    val careerGoals: String,
    val targetIndustries: String,
    val targetCountries: String,
    val targetCompanies: String,
    val targetUniversities: String,
    val targetVenues: String,
    val currentAcademicTasks: String,
    val currentResearchProjects: String
)

@Entity(tableName = "university_profile")
data class UniversityProfileEntity(
    @PrimaryKey val id: Int = 1,
    val university: String = "Yanshan University (燕山大学)",
    val department: String = "School of Information Science and Engineering (信息科学与工程学院)",
    val degree: String = "Bachelor of Engineering in Computer Science & Technology (计算机科学与技术)",
    val semester: String = "Year 3, Semester 1 (Fall 2026)",
    val academicYear: String = "2026-2027",
    val semesterStartDate: String = "2026-09-01",
    val semesterEndDate: String = "2027-01-15",
    val defaultClassDurationMinutes: Int = 95,
    val attendanceTarget: Float = 80.0f,
    val availableDailyStudyHours: Float = 4.0f,
    val preferredStudyStartTime: String = "18:00",
    val preferredStudyEndTime: String = "22:00",
    val sleepStartTime: String = "23:30",
    val sleepEndTime: String = "07:00"
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val professorName: String,
    val professorContact: String,
    val credits: Int,
    val classroom: String,
    val scheduleTime: String,
    val syllabusSummary: String = "",
    val notes: String = "",
    val attendanceTarget: Float = 80.0f,
    val scheduledClasses: Int = 32,
    val attendedClasses: Int = 28,
    val absentClasses: Int = 2,
    val excusedClasses: Int = 0,
    val colorHex: String = "#00D2FF"
)

@Entity(tableName = "timetable_classes")
data class TimetableClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseCode: String,
    val courseName: String,
    val teacher: String,
    val dayOfWeek: String, // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    val startTime: String, // e.g. "08:00"
    val endTime: String,   // e.g. "09:35"
    val classroom: String, // e.g. "East Campus 4-302"
    val weekRange: String = "Weeks 1-16",
    val scheduleType: String = "Weekly", // Weekly, Odd Weeks, Even Weeks, Specific Date, One-Time, Make-Up
    val notes: String = "",
    val isUncertain: Boolean = false,
    val uncertaintyReason: String = ""
)

@Entity(tableName = "study_plan_sessions")
data class StudyPlanSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String, // Monday..Sunday
    val dateStr: String = "",
    val startTime: String, // "18:00"
    val endTime: String,   // "19:30"
    val title: String,
    val category: String, // Study, Assignment, Exam Prep, Presentation Prep, Research, Chinese Practice, Free Time
    val courseOrTopic: String = "",
    val isCompleted: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "exam_plans")
data class ExamPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseName: String,
    val courseCode: String,
    val examDate: String,
    val topics: String,
    val difficulty: String = "Moderate", // Easy, Moderate, Hard, Extreme
    val prepLevelPercent: Int = 40,
    val generatedSchedule: String = ""
)

@Entity(tableName = "presentation_plans")
data class PresentationPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseName: String,
    val presentationTitle: String,
    val targetDate: String,
    val stagesSummary: String = "",
    val stage1Completed: Boolean = false, // Topic & Research
    val stage2Completed: Boolean = false, // Slide creation
    val stage3Completed: Boolean = false, // Speaking practice
    val stage4Completed: Boolean = false, // Q&A prep
    val stage5Completed: Boolean = false  // Rehearsal
)

@Entity(tableName = "academic_tasks")
data class AcademicTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val courseId: Long = 0,
    val courseName: String,
    val title: String,
    val type: String, // Assignment, Exam, Presentation, Project, Milestone
    val deadline: String,
    val priority: String = "Medium", // High, Medium, Low
    val isCompleted: Boolean = false,
    val description: String = "",
    val gradingWeight: String = "15%",
    val status: String = "Not Started", // Not Started, In Progress, Completed
    val estimatedHours: Float = 3.0f,
    val attachedDocument: String = ""
)

@Entity(tableName = "research_projects")
data class ResearchProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val researchArea: String = "Computer Vision & Deep Learning",
    val description: String = "",
    val researchProblem: String = "",
    val researchQuestion: String = "",
    val objectives: String = "",
    val researchGap: String = "",
    val hypotheses: String = "",
    val datasets: String = "",
    val methodology: String = "",
    val modelsAlgorithms: String = "",
    val experiments: String = "",
    val baselines: String = "",
    val metrics: String = "",
    val results: String = "",
    val codeRepo: String = "",
    val papersSummary: String = "",
    val currentStatus: String = "Implementation", // Idea, Planning, Literature Review, Implementation, Experiments, Analysis, Writing, Submitted, Revision, Completed
    val manuscriptStatus: String = "Drafting",
    val targetVenue: String = "CVPR 2027",
    val targetPublication: String = "CVPR 2027",
    val notes: String = "",
    val isActive: Boolean = true,
    val progressPercent: Int = 65,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "research_milestones")
data class ResearchMilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val title: String,
    val deadline: String,
    val status: String = "Pending", // Pending, In Progress, Completed
    val notes: String = "",
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0
)

@Entity(tableName = "research_papers")
data class ResearchPaperEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val knowledgeBaseDocId: Long? = null,
    val title: String,
    val authors: String = "Not available in the document.",
    val year: String = "Not available in the document.",
    val venue: String = "Not available in the document.",
    val doi: String = "Not available in the document.",
    val url: String = "",
    val abstractText: String = "",
    val researchProblem: String = "Not available in the document.",
    val method: String = "Not available in the document.",
    val dataset: String = "Not available in the document.",
    val evaluationMetrics: String = "Not available in the document.",
    val mainFindings: String = "Not available in the document.",
    val limitations: String = "Not available in the document.",
    val relevanceToProject: String = "Not available in the document.",
    val researchGapContribution: String = "",
    val personalNotes: String = "",
    val sourceContentSnippet: String = "",
    val aiInterpretationSnippet: String = "",
    val bibtex: String = "",
    val addedDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "research_experiments")
data class ResearchExperimentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val experimentId: String, // e.g. EXP-001
    val name: String,
    val date: String,
    val dataset: String,
    val features: String = "Not recorded.",
    val model: String,
    val baseline: String = "Not recorded.",
    val hyperparameters: String = "Not recorded.",
    val randomSeed: String = "42",
    val trainValTestStrategy: String = "Not recorded.",
    val evaluationMetrics: String = "mIoU, F1, Accuracy",
    val results: String = "Not recorded.",
    val metricF1: Float? = null,
    val metricMcc: Float? = null,
    val metricPrecision: Float? = null,
    val metricRecall: Float? = null,
    val metricAuc: Float? = null,
    val customMetricName: String = "",
    val customMetricValue: String = "",
    val notes: String = "",
    val codeVersionRef: String = "git commit HEAD",
    val hypothesis: String = "",
    val hardware: String = "NVIDIA RTX 4090 / Jetson AGX Orin",
    val trainingTimeOrSpeed: String = "38.2 FPS / 12h training",
    val qualitativeObservations: String = "",
    val failureAnalysis: String = "",
    val nextSteps: String = "",
    val artifactLink: String = "",
    val trainingLoss: String = "",
    val validationLoss: String = "",
    val overfittingNotes: String = "",
    val ablationNotes: String = ""
)

@Entity(tableName = "manuscript_sections")
data class ManuscriptSectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val sectionName: String, // Title, Abstract, Introduction, Related Work, Methodology, Experimental Setup, Results, Discussion, Limitations, Conclusion, References
    val status: String = "Not Started", // Not Started, Drafting, Review, Completed
    val wordCount: Int = 0,
    val targetWordCount: Int = 500,
    val notes: String = "",
    val contentDraft: String = "",
    val keyPromptsOrChecklist: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "reproducibility_items")
data class ReproducibilityItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val itemKey: String,
    val title: String,
    val isCompleted: Boolean = false,
    val details: String = "",
    val category: String = "Data & Splits"
)

@Entity(tableName = "cs_skills")
data class SkillItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // Programming, Core CS, AI, Computer Vision, Research Skills
    val name: String,
    val level: String, // Beginner, Intermediate, Advanced, Research-Ready
    val isCompleted: Boolean = false,
    val description: String = "",
    val keyTopics: String = ""
)

@Entity(tableName = "generated_projects")
data class GeneratedProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val difficulty: String,
    val researchValue: String,
    val prerequisites: String,
    val dataset: String,
    val technologies: String,
    val architecture: String,
    val milestones: String,
    val githubStructure: String,
    val experimentsPlan: String,
    val cvDescription: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chinese_phrases")
data class ChinesePhraseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // Technical CS, Academic, Campus Yanshan, Daily Life, Visa & Admin
    val hanzi: String,
    val pinyin: String,
    val english: String,
    val exampleZh: String,
    val exampleEn: String,
    val notes: String = "",
    val isMastered: Boolean = false
)

@Entity(tableName = "knowledge_items")
data class KnowledgeItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Notes, Paper Summary, Campus Guide, Visa/Immigration, Career
    val content: String,
    val tags: String,
    val sourceRef: String = "",
    val isVerified: Boolean = true,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "roadmap_goals")
data class RoadmapGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timeframe: String, // Daily, Weekly, Monthly, Semester, Long-Term
    val title: String,
    val description: String = "",
    val targetDate: String = "",
    val isCompleted: Boolean = false,
    val priority: String = "Normal"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val modeId: String,
    val sender: String, // "user" or "mentor"
    val content: String,
    val factType: String = "AI_SUGGESTION", // VERIFIED_FACT, DOCUMENT_INFO, CURRENT_WEB, AI_SUGGESTION
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_documents")
data class PersonalDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val fileName: String,
    val fileType: String, // TXT, MD, PDF, DOCX, PPTX, IMAGE, OTHER
    val category: String, // ACADEMIC, RESEARCH, PROJECTS, CAREER, CHINESE, UNIVERSITY, PERSONAL, OTHER
    val uploadDate: String,
    val tags: String = "",
    val userNotes: String = "",
    val courseOrProject: String = "",
    val content: String = "",
    val isBinaryUnsupported: Boolean = false,
    val fileUriString: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "career_profiles")
data class CareerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val degree: String = "Bachelor of Engineering in CS",
    val university: String = "Yanshan University (燕山大学)",
    val department: String = "School of Information Science & Engineering",
    val graduationDate: String = "June 2027",
    val researchAreas: String = "Computer Vision, 3D LiDAR Perception, Deep Learning",
    val programmingLanguages: String = "Python, C++, C, Java, SQL",
    val frameworks: String = "PyTorch, TorchVision, Open3D, NumPy, SciPy",
    val aiMlSkills: String = "Supervised Learning, Semi-Supervised Learning, Loss Formulation, Attention Mechanisms",
    val cvSkills: String = "3D Point Cloud Segmentation, Object Detection, Depth Estimation, Optical Backscattering Models",
    val seSkills: String = "Git, Linux, OOP, Design Patterns, Data Structures & Algorithms, Valgrind, GDB",
    val databases: String = "SQLite, PostgreSQL, MySQL",
    val cloud: String = "Docker, Linux Server Administration, Remote GPU Cluster SSH",
    val dataScience: String = "Pandas, Matplotlib, Seaborn, Evaluation Metrics (mIoU, Precision, Recall, F1)",
    val researchSkills: String = "LaTeX, Paper Writing, Ablation Design, Empirical Benchmarking, Literature Matrix Synthesis",
    val publications: String = "Robust Semi-Supervised 3D LiDAR Semantic Segmentation in Maritime Coastal Fog (CVPR 2027 Submission)",
    val projects: String = "PointFog-SAM, xv6 OS Kernel COW Fork, YSU Timetable AI Parser",
    val internships: String = "Autonomous Driving Lab Research Intern (Yanshan University AI Lab)",
    val workExperience: String = "Graduate Research Assistant (Perception Lab, YSU)",
    val certifications: String = "NVIDIA Deep Learning Institute (DLI) Fundamentals, CS61A/B Completion",
    val githubUrl: String = "https://github.com/alexei-ysu-cs",
    val portfolioUrl: String = "https://alexei-scholar.dev",
    val linkedinUrl: String = "https://linkedin.com/in/alexei-chen-kovalenko",
    val englishProficiency: String = "Fluent / Professional Working (IELTS 7.5 equivalent)",
    val chineseProficiency: String = "HSK 4 Certified (320 words mastered, preparing HSK 5)",
    val targetJobTitles: String = "Computer Vision Engineer, AI Algorithm Engineer, Machine Learning Engineer, AI Researcher",
    val targetCountries: String = "China, Singapore, Japan, Europe, Remote",
    val targetCities: String = "Beijing, Shanghai, Shenzhen, Hangzhou, Singapore, Berlin, Tokyo",
    val targetIndustries: String = "Autonomous Driving, Robotics, AI R&D, Intelligent Port Logistics"
)

@Entity(tableName = "career_goals")
data class CareerGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val targetRole: String, // e.g. "Computer Vision Engineer"
    val country: String,    // e.g. "China"
    val city: String,       // e.g. "Beijing / Shanghai"
    val industry: String,   // e.g. "Autonomous Driving / Robotics"
    val desiredExperienceLevel: String = "Entry-Level / New Graduate", // Entry-Level, Junior, Mid, Senior, PhD
    val targetApplicationDate: String = "Spring 2027",
    val isPrimary: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "skill_inventory")
data class SkillInventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val skillName: String,
    val category: String, // Languages, AI/ML, Computer Vision, Software Engineering, Systems, Tools
    val currentLevel: String, // Beginner, Intermediate, Advanced, Expert
    val evidence: String, // e.g. "Implemented Koschmieder layer in PyTorch for EXP-002"
    val lastPracticed: String, // e.g. "2026-10-01"
    val relatedProject: String // e.g. "PointFog-SAM"
)

@Entity(tableName = "job_postings")
data class JobPostingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jobTitle: String,
    val company: String,
    val location: String,
    val country: String = "China",
    val remotePolicy: String = "On-site", // On-site, Hybrid, Remote
    val industry: String = "Autonomous Driving & AI",
    val jobType: String = "Full-time", // Full-time, Internship, Research Fellow
    val experienceLevel: String = "Entry-Level / Master Graduate",
    val requiredDegree: String = "Master's or Bachelor's in CS / AI",
    val requiredExperience: String = "0-2 years (internship/lab counts)",
    val requiredSkills: String, // comma-separated
    val preferredSkills: String, // comma-separated
    val programmingLanguages: String = "Python, C++",
    val frameworks: String = "PyTorch, OpenCV",
    val tools: String = "Linux, Git, Docker",
    val researchRequirements: String = "Experience with 3D perception or deep learning papers",
    val languageRequirements: String = "English working proficiency, basic Chinese conversational",
    val responsibilities: String = "Design 3D point cloud perception algorithms and optimize for onboard inference.",
    val applicationDeadline: String = "2027-04-30",
    val salary: String = "¥28,000 - ¥42,000 / month",
    val source: String = "Official Campus Recruitment Portal",
    val dateChecked: String = "2026-10-01",
    val applicationUrl: String = "https://careers.company.com/job/cv-algo-2027",
    val isVerified: Boolean = true,
    val rawDescription: String = "",
    val isSaved: Boolean = false
)

@Entity(tableName = "skill_development_plans")
data class SkillDevelopmentPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val skillName: String,
    val currentLevel: String = "Beginner",
    val targetLevel: String = "Advanced",
    val whyItMatters: String, // e.g. "Required for 75% of autonomous driving perception roles"
    val learningResources: String, // e.g. "PyTorch C++ frontend docs, TensorRT tutorial"
    val practiceTask: String, // e.g. "Write custom TensorRT plugin for point voxelization"
    val suggestedProject: String, // e.g. "PointFog-SAM inference optimization"
    val targetDate: String, // e.g. "Nov 30, 2026"
    val status: String = "In Progress", // Not Started, In Progress, Completed
    val targetRoleId: Long = 0
)

@Entity(tableName = "job_applications")
data class JobApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val company: String,
    val position: String,
    val location: String,
    val applicationUrl: String = "",
    val dateApplied: String = "",
    val status: String = "Preparing", // Saved, Preparing, Applied, Interview, Technical Round, Offer, Rejected, Withdrawn, Closed
    val interviewDate: String = "",
    val followUpDate: String = "",
    val notes: String = "",
    val jobDescription: String = "",
    val cvVersionUsed: String = "CV_Alexei_CVPR_English_v2.pdf"
)

@Entity(tableName = "interview_questions")
data class InterviewPrepQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // Technical, Coding, Machine Learning, Computer Vision, Research, System Design, Behavioral, HR
    val targetRoleOrProject: String,
    val question: String,
    val userAnswer: String = "",
    val aiFeedback: String = "",
    val improvedAnswer: String = "",
    val isPracticed: Boolean = false
)

@Entity(tableName = "career_roadmap_milestones")
data class CareerRoadmapMilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stage: String, // Skills, Gaps, Projects, Experience, CV, Interviews, Applications
    val title: String,
    val targetDate: String,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val orderIndex: Int = 0
)

// ==========================================
// STAGE 5: CHINESE LANGUAGE COACH ENTITIES
// ==========================================

@Entity(tableName = "chinese_language_profile")
data class ChineseLanguageProfileEntity(
    @PrimaryKey val id: Long = 1,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val currentLevel: String = "Not assessed", // "Not assessed", "Beginner", "Elementary (HSK 1-2)", "Intermediate (HSK 3-4)", "Upper Intermediate (HSK 5)", "Advanced (HSK 6)"
    val hskLevel: String = "Not assessed",    // "Not assessed", "HSK 1", "HSK 2", "HSK 3", "HSK 4", "HSK 5", "HSK 6"
    val targetHskLevel: String = "HSK 5",
    val learningGoal: String = "Academic lab meetings, thesis defense in Chinese, campus & hospital/banking fluency",
    val dailyStudyMinutes: Int = 30,
    val preferredStudyTime: String = "Morning (08:00 - 08:30)",
    val daysPerWeek: Int = 5,
    val targetDate: String = "2027-06-30",
    val speakingConfidence: Int = 2,
    val listeningConfidence: Int = 3,
    val readingConfidence: Int = 4,
    val writingConfidence: Int = 2,
    val vocabularyConfidence: Int = 3,
    val grammarConfidence: Int = 3,
    val currentStreak: Int = 7,
    val longestStreak: Int = 14,
    val daysStudiedCount: Int = 28,
    val weeklyGoalMinutes: Int = 150,
    val notificationsEnabled: Boolean = true
)

@Entity(tableName = "chinese_vocabulary")
data class ChineseVocabularyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val hanzi: String,
    val pinyin: String,
    val english: String,
    val partOfSpeech: String = "Noun", // Noun, Verb, Adjective, Adverb, Measure Word, Idiom, Conjunction, Particle, Technical Term
    val exampleSentenceZh: String = "",
    val exampleSentenceEn: String = "",
    val userNotes: String = "",
    val difficulty: String = "Medium", // Easy, Medium, Hard
    val hskLevel: String = "HSK 4",
    val category: String = "Technical CS",
    val source: String = "Manual Entry", // e.g. "University Notice", "Research Lab: EXP-002", "AI Conversation", "Translation Tool", "HSK Core"
    val isFavorite: Boolean = false,
    val isKnown: Boolean = false,
    val isDifficult: Boolean = false,
    val reviewStatus: String = "New", // New, Due, Upcoming, Mastered
    val lastReviewedTimestamp: Long = 0L,
    val nextReviewDate: String = "", // YYYY-MM-DD
    val reviewCount: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val intervalDays: Int = 1,
    val easeFactor: Float = 2.5f
)

@Entity(tableName = "chinese_study_sessions")
data class ChineseStudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val dayOfWeek: String, // Monday, Tuesday, etc.
    val timeSlot: String,  // e.g. "08:00 - 08:30"
    val moduleType: String, // Vocabulary, Grammar, Listening, Speaking, Reading, Writing, Review
    val title: String,
    val description: String,
    val durationMinutes: Int = 30,
    val isCompleted: Boolean = false
)

@Entity(tableName = "chinese_conversation_messages")
data class ChineseConversationMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val scenarioId: String,
    val sender: String, // "USER" or "AI_PARTNER"
    val hanzi: String,
    val pinyin: String = "",
    val english: String = "",
    val correctionZh: String = "",
    val correctionNotes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chinese_listening_exercises")
data class ChineseListeningExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val title: String,
    val category: String = "Campus & Academic",
    val chineseText: String,
    val pinyinText: String,
    val englishTranslation: String,
    val question: String,
    val optionsJson: String, // pipe separated "A) ...|B) ...|C) ...|D) ..."
    val correctOptionIndex: Int,
    val explanation: String,
    val userSelectedOptionIndex: Int = -1,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false
)

@Entity(tableName = "chinese_grammar_points")
data class ChineseGrammarPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hskLevel: String = "HSK 4",
    val pattern: String,
    val explanation: String,
    val exampleZh: String,
    val examplePinyin: String,
    val exampleEn: String,
    val commonMistakes: String = "",
    val practicePrompt: String = ""
)

@Entity(tableName = "chinese_weekly_progress")
data class ChineseWeeklyProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val weekLabel: String = "Current Week",
    val studyMinutes: Int = 120,
    val wordsReviewed: Int = 45,
    val wordsLearned: Int = 18,
    val exercisesCompleted: Int = 12,
    val listeningPracticeCount: Int = 5,
    val speakingPracticeCount: Int = 3,
    val readingPracticeCount: Int = 6,
    val writingPracticeCount: Int = 2,
    val conversationSessionsCount: Int = 4
)

// ========================================================
// STAGE 6: CHINA STUDENT -> GRADUATION -> WORK & IMMIGRATION
// ========================================================

@Entity(tableName = "immigration_profiles")
data class ImmigrationProfileEntity(
    @PrimaryKey val id: Long = 1,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val nationality: String = "Not provided",
    val currentUniversity: String = "Not provided",
    val universityCityProvince: String = "Not provided",
    val degreeLevel: String = "Not provided",
    val major: String = "Not provided",
    val currentStudentStatus: String = "Not provided",
    val expectedGraduationDate: String = "Not provided",
    val currentVisaCategory: String = "Not provided",
    val currentResidencePermitType: String = "Not provided",
    val residencePermitExpirationDate: String = "Not provided",
    val passportExpirationDate: String = "Not provided",
    val passportNumberOptional: String = "Not provided",
    val targetEmploymentCity: String = "Not provided",
    val targetJobField: String = "Not provided",
    val targetEmployerType: String = "Not provided",
    val intendsToRemainInChina: String = "Not provided",
    val isConsideringEntrepreneurship: String = "Not provided",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "official_sources")
data class OfficialSourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceTitle: String,
    val organization: String,
    val url: String,
    val jurisdiction: String, // "National", "Shanghai", "Beijing", "Hebei / Qinhuangdao", etc.
    val topic: String,
    val publicationDate: String = "Publication/update date not available.",
    val lastCheckedDate: String,
    val status: String = "Active & Verified",
    val summary: String,
    val contentSnippet: String
)

@Entity(tableName = "city_immigration_policies")
data class CityImmigrationPolicyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val city: String,
    val provinceOrMunicipality: String,
    val policyTitle: String,
    val policyCategory: String, // "Foreign Graduate Direct Employment", "Innovation & Entrepreneurship Residence", "Internship Endorsement", "Work Permit Class B"
    val eligibility: String,
    val requirements: String,
    val requiredDocuments: String,
    val applicationAuthority: String,
    val officialSourceUrl: String,
    val sourceOrganization: String,
    val publicationDate: String = "Publication/update date not available.",
    val lastVerifiedDate: String,
    val verificationStatus: String = "Verified recently", // "Verified recently" or "Verification may be outdated"
    val isLocalSpecific: Boolean = true,
    val importantUncertaintyWarning: String = "Policies can vary by district and case. Confirm with local immigration and HR bureau."
)

@Entity(tableName = "graduation_checklist_items")
data class GraduationChecklistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val category: String, // University, Academic, Documents, Residence status, Career, Employment, China departure, Post-graduation options
    val title: String,
    val description: String,
    val responsibleParty: String = "Student",
    val deadline: String = "",
    val isCompleted: Boolean = false,
    val isCustom: Boolean = false,
    val sourceNote: String = "Official University / Exit-Entry Guidelines"
)

@Entity(tableName = "immigration_documents")
data class ImmigrationDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val documentName: String,
    val requiredFor: String,
    val status: String = "Not Obtained", // Not Obtained, In Progress, Obtained / Ready, Expired
    val expirationDate: String = "",
    val sourceRequirement: String = "Requirement depends on application category and location.",
    val responsibleParty: String = "Student",
    val notes: String = ""
)

@Entity(tableName = "official_contacts")
data class OfficialContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organization: String,
    val location: String,
    val category: String, // National Immigration Administration, Local Exit-Entry Administration, University ISO, Human Resources
    val officialWebsite: String,
    val officialPhoneOrEmail: String,
    val purpose: String,
    val lastVerifiedDate: String
)

@Entity(tableName = "immigration_reminders")
data class ImmigrationReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String = "alexei.chen@ysu.edu.cn",
    val reminderType: String, // Passport Expiration, Residence Permit Expiration, Graduation Transition, Policy Re-check
    val title: String,
    val dueDate: String,
    val isDismissed: Boolean = false,
    val label: String = "Personal reminder"
)


