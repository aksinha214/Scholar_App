package com.example.ai

import com.example.data.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Unified Context Layer for CS Scholar OS.
 * Aggregates information from all modules without duplicating database tables.
 */
data class UnifiedScholarContext(
    val userProfile: UserProfileEntity?,
    val universityProfile: UniversityProfileEntity?,
    val courses: List<CourseEntity> = emptyList(),
    val tasks: List<AcademicTaskEntity> = emptyList(),
    val timetableClasses: List<TimetableClassEntity> = emptyList(),
    val studySessions: List<StudyPlanSessionEntity> = emptyList(),
    val examPlans: List<ExamPlanEntity> = emptyList(),
    val presentationPlans: List<PresentationPlanEntity> = emptyList(),
    val researchProjects: List<ResearchProjectEntity> = emptyList(),
    val researchMilestones: List<ResearchMilestoneEntity> = emptyList(),
    val researchPapers: List<ResearchPaperEntity> = emptyList(),
    val researchExperiments: List<ResearchExperimentEntity> = emptyList(),
    val careerProfile: CareerProfileEntity? = null,
    val careerGoals: List<CareerGoalEntity> = emptyList(),
    val skills: List<SkillInventoryEntity> = emptyList(),
    val jobPostings: List<JobPostingEntity> = emptyList(),
    val jobApplications: List<JobApplicationEntity> = emptyList(),
    val chinesePhrases: List<ChinesePhraseEntity> = emptyList(),
    val chineseVocab: List<ChineseVocabularyEntity> = emptyList(),
    val chineseStudySessions: List<ChineseStudySessionEntity> = emptyList(),
    val chineseWeeklyProgress: ChineseWeeklyProgressEntity? = null,
    val knowledgeItems: List<KnowledgeItemEntity> = emptyList(),
    val immigrationProfile: ImmigrationProfileEntity? = null,
    val officialSources: List<OfficialSourceEntity> = emptyList(),
    val cityPolicies: List<CityImmigrationPolicyEntity> = emptyList(),
    val graduationChecklist: List<GraduationChecklistItemEntity> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Determines today's day of week (e.g. "Monday", "Tuesday")
     */
    val currentDayOfWeek: String
        get() {
            val sdf = SimpleDateFormat("EEEE", Locale.ENGLISH)
            return sdf.format(Date(timestamp))
        }

    val currentDateStr: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

    /**
     * Classes scheduled for today
     */
    val todayClasses: List<TimetableClassEntity>
        get() = timetableClasses.filter { it.dayOfWeek.equals(currentDayOfWeek, ignoreCase = true) }

    /**
     * Pending tasks
     */
    val pendingTasks: List<AcademicTaskEntity>
        get() = tasks.filter { !it.isCompleted }

    /**
     * Overdue or due soon tasks
     */
    val urgentTasks: List<AcademicTaskEntity>
        get() = pendingTasks.filter { task ->
            task.deadline.isNotBlank() && (task.deadline <= currentDateStr || task.priority.equals("High", ignoreCase = true))
        }

    /**
     * Active research projects
     */
    val activeResearchProjects: List<ResearchProjectEntity>
        get() = researchProjects.filter { it.isActive }

    /**
     * Active research milestones
     */
    val pendingMilestones: List<ResearchMilestoneEntity>
        get() = researchMilestones.filter { !it.isCompleted }

    /**
     * Pending graduation checklist items
     */
    val pendingGraduationTasks: List<GraduationChecklistItemEntity>
        get() = graduationChecklist.filter { !it.isCompleted }

    /**
     * Chinese vocab due for review
     */
    val chineseVocabDue: List<ChineseVocabularyEntity>
        get() = chineseVocab.filter { it.nextReviewDate.isNotBlank() && it.nextReviewDate <= currentDateStr }
}

object UnifiedContextProvider {

    /**
     * Formats the collected context into a concise textual summary for AI grounding.
     */
    fun summarizeForModule(context: UnifiedScholarContext, targetModule: String): String {
        return buildString {
            when (targetModule.uppercase()) {
                "UNIVERSITY", "TIMETABLE", "ATTENDANCE" -> {
                    appendLine("=== UNIVERSITY & TIMETABLE CONTEXT ===")
                    appendLine("Current Day: ${context.currentDayOfWeek} (${context.currentDateStr})")
                    appendLine("Today's Classes (${context.todayClasses.size}):")
                    if (context.todayClasses.isEmpty()) {
                        appendLine("  • No classes scheduled on timetable for ${context.currentDayOfWeek}.")
                    } else {
                        context.todayClasses.forEach { cls ->
                            appendLine("  • ${cls.courseCode} ${cls.courseName} | ${cls.startTime}-${cls.endTime} | ${cls.classroom} | Teacher: ${cls.teacher}")
                        }
                    }
                    appendLine("Course Attendance Records:")
                    context.courses.forEach { c ->
                        val total = c.attendedClasses + c.absentClasses + c.excusedClasses
                        val rate = if (total > 0) (c.attendedClasses.toFloat() / total * 100).toInt() else 100
                        appendLine("  • ${c.code} ${c.name}: Attended ${c.attendedClasses}/$total (${rate}%), Target: ${c.attendanceTarget.toInt()}%")
                    }
                }

                "RESEARCH", "RESEARCH_LAB" -> {
                    appendLine("=== RESEARCH LAB CONTEXT ===")
                    appendLine("Active Projects (${context.activeResearchProjects.size}):")
                    context.activeResearchProjects.forEach { proj ->
                        appendLine("  • Project: ${proj.title} (Area: ${proj.researchArea}, Target: ${proj.targetVenue}, Progress: ${proj.progressPercent}%)")
                        appendLine("    Description: ${proj.description.take(150)}")
                    }
                    appendLine("Pending Milestones (${context.pendingMilestones.size}):")
                    context.pendingMilestones.forEach { m ->
                        appendLine("  • [Deadline: ${m.deadline}] ${m.title} (Status: ${m.status})")
                    }
                    appendLine("Active Experiments: ${context.researchExperiments.size} recorded.")
                    appendLine("Literature Matrix: ${context.researchPapers.size} papers analyzed.")
                }

                "STUDY", "STUDY_PLANNER", "TASKS" -> {
                    appendLine("=== STUDY & TASKS CONTEXT ===")
                    appendLine("Pending Tasks (${context.pendingTasks.size}):")
                    if (context.pendingTasks.isEmpty()) {
                        appendLine("  • No pending academic tasks recorded.")
                    } else {
                        context.pendingTasks.forEach { t ->
                            appendLine("  • [Due: ${t.deadline}] ${t.courseName}: ${t.title} (Priority: ${t.priority}, Type: ${t.type})")
                        }
                    }
                    appendLine("Upcoming Exams (${context.examPlans.size}):")
                    context.examPlans.forEach { exp ->
                        appendLine("  • ${exp.courseName} Exam on ${exp.examDate} (Prep level: ${exp.prepLevelPercent}%)")
                    }
                    appendLine("Presentations (${context.presentationPlans.size}):")
                    context.presentationPlans.forEach { pres ->
                        appendLine("  • ${pres.presentationTitle} for ${pres.courseName} (Target: ${pres.targetDate})")
                    }
                }

                "CAREER", "JOBS" -> {
                    appendLine("=== CAREER & JOB CONTEXT ===")
                    val profile = context.careerProfile
                    appendLine("Target Roles: ${profile?.targetJobTitles ?: "Software / AI Engineer"}")
                    appendLine("Target Cities: ${profile?.targetCities ?: "Shanghai / Beijing / Shenzhen"}")
                    appendLine("Target Industries: ${profile?.targetIndustries ?: "Autonomous Driving / AI"}")
                    appendLine("Job Applications (${context.jobApplications.size}):")
                    context.jobApplications.forEach { app ->
                        appendLine("  • ${app.company} - ${app.position} | Status: ${app.status} | Location: ${app.location}")
                    }
                    appendLine("Skills Inventory (${context.skills.size}):")
                    context.skills.take(8).forEach { s ->
                        appendLine("  • ${s.skillName} (${s.category}) - Level: ${s.currentLevel} (Evidence: ${s.evidence.take(40)})")
                    }
                }

                "CHINESE", "LANGUAGE" -> {
                    appendLine("=== CHINESE COACH CONTEXT ===")
                    val prof = context.userProfile?.chineseProficiency ?: "HSK 4"
                    appendLine("Proficiency: $prof")
                    appendLine("Vocabulary Bank: ${context.chineseVocab.size} items recorded.")
                    appendLine("Due for Review: ${context.chineseVocabDue.size} flashcards.")
                    if (context.chineseVocabDue.isNotEmpty()) {
                        val sample = context.chineseVocabDue.take(4).joinToString { "${it.hanzi} (${it.pinyin})" }
                        appendLine("Sample due: $sample")
                    }
                }

                "CHINA_WORK", "IMMIGRATION", "VISA" -> {
                    appendLine("=== CHINA WORK & IMMIGRATION CONTEXT ===")
                    val imm = context.immigrationProfile
                    if (imm != null) {
                        appendLine("University: ${imm.currentUniversity} (${imm.universityCityProvince})")
                        appendLine("Degree: ${imm.degreeLevel} in ${imm.major}")
                        appendLine("Graduation: ${imm.expectedGraduationDate}")
                        appendLine("Permit Expiration: ${imm.residencePermitExpirationDate}")
                        appendLine("Target Work City: ${imm.targetEmploymentCity}")
                        appendLine("Remain in China: ${imm.intendsToRemainInChina}")
                    } else {
                        appendLine("Immigration profile: Not configured.")
                    }
                    appendLine("Graduation Checklist: ${context.graduationChecklist.count { it.isCompleted }}/${context.graduationChecklist.size} completed.")
                    appendLine("Official Sources Registered: ${context.officialSources.size}")
                }

                else -> {
                    appendLine("=== GLOBAL SCHOLAR OVERVIEW ===")
                    appendLine("Scholar: ${context.userProfile?.name ?: "Alexei Chen"}")
                    appendLine("University: ${context.universityProfile?.university ?: "Yanshan University"}")
                    appendLine("Active Tasks: ${context.pendingTasks.size} pending.")
                    appendLine("Today's Classes: ${context.todayClasses.size} scheduled.")
                    appendLine("Research Projects: ${context.activeResearchProjects.size} active.")
                }
            }
        }
    }
}
