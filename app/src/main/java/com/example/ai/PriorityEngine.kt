package com.example.ai

import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.*

enum class PriorityLevel(val label: String, val baseScore: Int) {
    OVERDUE("Overdue", 100),
    DUE_TODAY("Due Today", 85),
    DUE_TOMORROW("Due Tomorrow", 70),
    THIS_WEEK("This Week", 50),
    FUTURE("Future", 25)
}

data class PrioritizedItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val module: AppScreen,
    val moduleLabel: String,
    val priorityLevel: PriorityLevel,
    val score: Int,
    val urgencyReason: String,
    val dueDate: String,
    val category: String,
    val isCompleted: Boolean = false
)

data class DailyPriorities(
    val highPriority: List<PrioritizedItem>,
    val recommended: List<PrioritizedItem>,
    val optional: List<PrioritizedItem>,
    val totalPendingCount: Int,
    val overdueCount: Int
)

object PriorityEngine {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Deterministically calculates and orders priorities across all CS Scholar OS modules.
     */
    fun calculatePriorities(
        context: UnifiedScholarContext,
        referenceDateStr: String? = null
    ): DailyPriorities {
        val todayStr = referenceDateStr ?: context.currentDateStr
        val todayDate = try { dateFormat.parse(todayStr) } catch (_: Exception) { Date() } ?: Date()

        val calendar = Calendar.getInstance().apply { time = todayDate }
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowStr = dateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, 6)
        val endOfWeekStr = dateFormat.format(calendar.time)

        val items = mutableListOf<PrioritizedItem>()

        // 1. Timetable Classes Today (Scheduled commitments are high priority)
        context.todayClasses.forEach { cls ->
            items.add(
                PrioritizedItem(
                    id = "class_${cls.id}",
                    title = "Class: ${cls.courseCode} ${cls.courseName}",
                    subtitle = "${cls.startTime} - ${cls.endTime} @ ${cls.classroom} (${cls.teacher})",
                    module = AppScreen.UNIVERSITY,
                    moduleLabel = "University Life",
                    priorityLevel = PriorityLevel.DUE_TODAY,
                    score = 88,
                    urgencyReason = "Scheduled lecture today at ${cls.startTime}",
                    dueDate = todayStr,
                    category = "Lecture"
                )
            )
        }

        // 2. Academic Tasks & Assignments
        context.tasks.filter { !it.isCompleted }.forEach { task ->
            val (level, baseScore, reason) = evaluateDeadline(task.deadline, todayStr, tomorrowStr, endOfWeekStr)
            val userBonus = when (task.priority.lowercase()) {
                "high" -> 15
                "medium" -> 5
                else -> 0
            }
            val finalScore = baseScore + userBonus

            items.add(
                PrioritizedItem(
                    id = "task_${task.id}",
                    title = "Task: ${task.title}",
                    subtitle = "${task.courseName} • Weight: ${task.gradingWeight}",
                    module = AppScreen.UNIVERSITY,
                    moduleLabel = "Study Planner",
                    priorityLevel = level,
                    score = finalScore,
                    urgencyReason = reason,
                    dueDate = task.deadline,
                    category = task.type
                )
            )
        }

        // 3. Research Milestones
        context.researchMilestones.filter { !it.isCompleted }.forEach { milestone ->
            val (level, baseScore, reason) = evaluateDeadline(milestone.deadline, todayStr, tomorrowStr, endOfWeekStr)
            val project = context.researchProjects.find { it.id == milestone.projectId }
            val projTitle = project?.title ?: "Research Project"

            items.add(
                PrioritizedItem(
                    id = "milestone_${milestone.id}",
                    title = "Research Milestone: ${milestone.title}",
                    subtitle = "$projTitle • Status: ${milestone.status}",
                    module = AppScreen.RESEARCH_LAB,
                    moduleLabel = "Research Lab",
                    priorityLevel = level,
                    score = baseScore + 10, // Research carries institutional weight
                    urgencyReason = reason,
                    dueDate = milestone.deadline,
                    category = "Research"
                )
            )
        }

        // 4. Exams approaching
        context.examPlans.forEach { exam ->
            val (level, baseScore, reason) = evaluateDeadline(exam.examDate, todayStr, tomorrowStr, endOfWeekStr)
            if (level != PriorityLevel.FUTURE || exam.prepLevelPercent < 50) {
                items.add(
                    PrioritizedItem(
                        id = "exam_${exam.id}",
                        title = "Exam Prep: ${exam.courseName}",
                        subtitle = "Exam date: ${exam.examDate} • Prep: ${exam.prepLevelPercent}%",
                        module = AppScreen.UNIVERSITY,
                        moduleLabel = "Exam Prep",
                        priorityLevel = level,
                        score = baseScore + (if (exam.prepLevelPercent < 50) 12 else 5),
                        urgencyReason = reason,
                        dueDate = exam.examDate,
                        category = "Exam"
                    )
                )
            }
        }

        // 5. Chinese Vocabulary SRS Review
        if (context.chineseVocabDue.isNotEmpty()) {
            val count = context.chineseVocabDue.size
            items.add(
                PrioritizedItem(
                    id = "chinese_srs_due",
                    title = "Chinese Practice: Review $count Vocab Cards",
                    subtitle = "Spaced Repetition review scheduled for today",
                    module = AppScreen.CHINESE_LANGUAGE,
                    moduleLabel = "Chinese Coach",
                    priorityLevel = PriorityLevel.DUE_TODAY,
                    score = 80,
                    urgencyReason = "SRS memory retention optimal when reviewed on time",
                    dueDate = todayStr,
                    category = "Language"
                )
            )
        }

        // 6. Immigration / Residence Permit Warning
        val imm = context.immigrationProfile
        if (imm != null && imm.residencePermitExpirationDate.isNotBlank()) {
            val permitExpiry = imm.residencePermitExpirationDate
            val (level, baseScore, reason) = evaluateDeadline(permitExpiry, todayStr, tomorrowStr, endOfWeekStr)
            if (level == PriorityLevel.OVERDUE || level == PriorityLevel.DUE_TODAY || level == PriorityLevel.DUE_TOMORROW || level == PriorityLevel.THIS_WEEK) {
                items.add(
                    PrioritizedItem(
                        id = "imm_permit_expiry",
                        title = "Immigration: Residence Permit Expiry",
                        subtitle = "Expires $permitExpiry • PSB Exit-Entry extension required",
                        module = AppScreen.CHINA_WORK,
                        moduleLabel = "China Work & Visa",
                        priorityLevel = level,
                        score = baseScore + 20, // Critical legal compliance
                        urgencyReason = "Immigration legal compliance deadline",
                        dueDate = permitExpiry,
                        category = "Legal Compliance"
                    )
                )
            }
        }

        // Sort strictly by deterministic score descending
        val sortedItems = items.sortedByDescending { it.score }

        val highPriority = sortedItems.filter { it.score >= 80 }
        val recommended = sortedItems.filter { it.score in 50..79 }
        val optional = sortedItems.filter { it.score < 50 }

        val overdueCount = sortedItems.count { it.priorityLevel == PriorityLevel.OVERDUE }

        return DailyPriorities(
            highPriority = highPriority,
            recommended = recommended,
            optional = optional,
            totalPendingCount = sortedItems.size,
            overdueCount = overdueCount
        )
    }

    private fun evaluateDeadline(
        deadline: String,
        todayStr: String,
        tomorrowStr: String,
        endOfWeekStr: String
    ): Triple<PriorityLevel, Int, String> {
        if (deadline.isBlank()) {
            return Triple(PriorityLevel.FUTURE, PriorityLevel.FUTURE.baseScore, "No fixed deadline specified")
        }

        return when {
            deadline < todayStr -> {
                Triple(PriorityLevel.OVERDUE, PriorityLevel.OVERDUE.baseScore, "Overdue since $deadline")
            }
            deadline == todayStr -> {
                Triple(PriorityLevel.DUE_TODAY, PriorityLevel.DUE_TODAY.baseScore, "Due today ($todayStr)")
            }
            deadline == tomorrowStr -> {
                Triple(PriorityLevel.DUE_TOMORROW, PriorityLevel.DUE_TOMORROW.baseScore, "Due tomorrow ($tomorrowStr)")
            }
            deadline <= endOfWeekStr -> {
                Triple(PriorityLevel.THIS_WEEK, PriorityLevel.THIS_WEEK.baseScore, "Due this week ($deadline)")
            }
            else -> {
                Triple(PriorityLevel.FUTURE, PriorityLevel.FUTURE.baseScore, "Scheduled for $deadline")
            }
        }
    }
}
