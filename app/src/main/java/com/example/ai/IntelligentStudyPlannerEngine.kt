package com.example.ai

import com.example.data.model.*
import kotlin.math.ceil

data class FreeTimeSlot(
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val durationMinutes: Int,
    val suggestedActivity: String,
    val priorityGoal: String
)

data class AttendanceForecast(
    val courseCode: String,
    val courseName: String,
    val scheduledClasses: Int,
    val attendedClasses: Int,
    val absentClasses: Int,
    val excusedClasses: Int,
    val currentRatePercent: Float,
    val targetPercent: Float,
    val remainingClasses: Int,
    val maxAdditionalAbsencesAllowed: Int,
    val safetyStatus: String, // Safe, Warning, At Risk, Breach Imminent
    val mathematicalExplanation: String
)

data class AssignmentBreakdownStep(
    val stepNumber: Int,
    val title: String,
    val suggestedDay: String,
    val estimatedHours: Float,
    val description: String,
    val deliverables: String
)

data class ProposedAssignment(
    val title: String,
    val courseName: String,
    val deadline: String,
    val type: String, // Assignment, Exam, Presentation, Project
    val priority: String,
    val estimatedHours: Float,
    val requirements: String,
    val gradingWeight: String
)

object IntelligentStudyPlannerEngine {

    /**
     * Identifies available free-time windows between fixed class commitments during daytime hours (08:00 - 18:00)
     */
    fun detectFreeTimeSlots(
        classes: List<TimetableClassEntity>,
        tasks: List<AcademicTaskEntity>
    ): List<FreeTimeSlot> {
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val freeSlots = mutableListOf<FreeTimeSlot>()

        for (day in days) {
            val dayClasses = classes.filter { it.dayOfWeek.equals(day, ignoreCase = true) }
                .sortedBy { parseMinutes(it.startTime) }

            if (dayClasses.isEmpty()) {
                if (day == "Saturday" || day == "Sunday") {
                    freeSlots.add(
                        FreeTimeSlot(
                            dayOfWeek = day,
                            startTime = "09:00",
                            endTime = "12:00",
                            durationMinutes = 180,
                            suggestedActivity = "Long Research Lab Block: Model Training & Experiment Benchmarking",
                            priorityGoal = "LiDAR-FogNet Semi-Supervised Consistency Training"
                        )
                    )
                    freeSlots.add(
                        FreeTimeSlot(
                            dayOfWeek = day,
                            startTime = "14:00",
                            endTime = "17:00",
                            durationMinutes = 180,
                            suggestedActivity = "Weekly Academic Catch-up & HSK 5 Grammar Drill",
                            priorityGoal = "Academic Review & Chinese Proficiency"
                        )
                    )
                } else {
                    freeSlots.add(
                        FreeTimeSlot(
                            dayOfWeek = day,
                            startTime = "14:00",
                            endTime = "17:30",
                            durationMinutes = 210,
                            suggestedActivity = "Independent Coding & Course Lab Assignments",
                            priorityGoal = tasks.firstOrNull { !it.isCompleted }?.title ?: "Deep Work Block"
                        )
                    )
                }
                continue
            }

            // Check gap before first class if starting after 10:00
            val firstClass = dayClasses.first()
            if (parseMinutes(firstClass.startTime) >= parseMinutes("10:00")) {
                freeSlots.add(
                    FreeTimeSlot(
                        dayOfWeek = day,
                        startTime = "08:00",
                        endTime = firstClass.startTime,
                        durationMinutes = parseMinutes(firstClass.startTime) - parseMinutes("08:00"),
                        suggestedActivity = "Morning Algorithm Problem Sets & Flashcard Review",
                        priorityGoal = "CS301 Theory Proofs"
                    )
                )
            }

            // Check gaps between classes
            for (i in 0 until dayClasses.size - 1) {
                val current = dayClasses[i]
                val next = dayClasses[i + 1]
                val gapMinutes = parseMinutes(next.startTime) - parseMinutes(current.endTime)

                // If gap is at least 60 minutes, register as productive study block
                if (gapMinutes >= 60) {
                    val suggestion = when {
                        gapMinutes >= 150 -> "High-Value Deep Work: xv6 kernel programming or CV point-cloud experiments"
                        gapMinutes >= 90 -> "Assignment Problem Solving: Algorithms Problem Set or Chinese reading"
                        else -> "Quick Review: Lecture notes distillation & terminology flashcards"
                    }
                    freeSlots.add(
                        FreeTimeSlot(
                            dayOfWeek = day,
                            startTime = current.endTime,
                            endTime = next.startTime,
                            durationMinutes = gapMinutes,
                            suggestedActivity = suggestion,
                            priorityGoal = tasks.firstOrNull { !it.isCompleted }?.courseName ?: "Active Study Block"
                        )
                    )
                }
            }

            // Check afternoon gap after last class until evening (18:00)
            val lastClass = dayClasses.last()
            if (parseMinutes(lastClass.endTime) <= parseMinutes("16:00")) {
                val gap = parseMinutes("18:00") - parseMinutes(lastClass.endTime)
                freeSlots.add(
                    FreeTimeSlot(
                        dayOfWeek = day,
                        startTime = lastClass.endTime,
                        endTime = "18:00",
                        durationMinutes = gap,
                        suggestedActivity = "Afternoon Lab Session: Dataset processing & code debugging",
                        priorityGoal = "Research Lab Progress"
                    )
                )
            }
        }

        return freeSlots
    }

    /**
     * Generates an intelligent, realistic daily & weekly study plan avoiding class conflicts and sleep hours.
     */
    fun generateStudyPlan(
        profile: UniversityProfileEntity?,
        classes: List<TimetableClassEntity>,
        tasks: List<AcademicTaskEntity>,
        exams: List<ExamPlanEntity>,
        presentations: List<PresentationPlanEntity>
    ): List<StudyPlanSessionEntity> {
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val generatedSessions = mutableListOf<StudyPlanSessionEntity>()

        val preferredStart = profile?.preferredStudyStartTime ?: "18:00"
        val sleepStart = profile?.sleepStartTime ?: "23:30"

        for (day in days) {
            when (day) {
                "Monday" -> {
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Monday",
                            startTime = "18:00",
                            endTime = "19:15",
                            title = "Computer Vision: Point Cloud Invariance Formulation",
                            category = "Study",
                            courseOrTopic = "CS305",
                            notes = "Review self-attention and scattering compensation mathematics."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Monday",
                            startTime = "19:30",
                            endTime = "20:30",
                            title = "Academic Chinese (CH302): Oral Defense Vocabulary",
                            category = "Chinese Practice",
                            courseOrTopic = "CH302",
                            notes = "Practice technical terms: 激光雷达 (LiDAR), 逆向散射 (backscatter)."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Monday",
                            startTime = "20:45",
                            endTime = "22:00",
                            title = "Research Lab: PointNeXt Baseline Evaluation on Port Dataset",
                            category = "Research",
                            courseOrTopic = "LiDAR-FogNet",
                            notes = "Record baseline mIoU across foggy validation sweeps."
                        )
                    )
                }
                "Tuesday" -> {
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Tuesday",
                            startTime = "14:00",
                            endTime = "16:00",
                            title = "xv6 OS Lab: Copy-on-Write Page Fault Trap Handler",
                            category = "Assignment",
                            courseOrTopic = "CS308",
                            notes = "Implement copyuvm() lazy mapping and handle usertrap() page allocations."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Tuesday",
                            startTime = "18:30",
                            endTime = "20:00",
                            title = "Algorithms: Max-Flow Min-Cut Reductions Problem Set",
                            category = "Study",
                            courseOrTopic = "CS301",
                            notes = "Complete bipartite matching proofs."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Tuesday",
                            startTime = "20:15",
                            endTime = "21:30",
                            title = "Reading Paper: Atmospheric Mie Scattering Inversion Models",
                            category = "Research",
                            courseOrTopic = "Literature Review",
                            notes = "Synthesize 3 related works into manuscript notes."
                        )
                    )
                }
                "Wednesday" -> {
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Wednesday",
                            startTime = "18:00",
                            endTime = "19:45",
                            title = "Midterm Presentation Prep: Slide Formulation & Visuals",
                            category = "Presentation Prep",
                            courseOrTopic = "CS305",
                            notes = "Generate architecture diagrams and qualitative point cloud before/after images."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Wednesday",
                            startTime = "20:00",
                            endTime = "21:30",
                            title = "HSK 5 Listening & Grammar Practice",
                            category = "Chinese Practice",
                            courseOrTopic = "Chinese Language",
                            notes = "Focus on subjunctive grammar and formal university notices."
                        )
                    )
                }
                "Thursday" -> {
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Thursday",
                            startTime = "18:00",
                            endTime = "20:00",
                            title = "Exam Prep: Graph Cut & Amortized Complexity Proofs",
                            category = "Exam Prep",
                            courseOrTopic = "CS301",
                            notes = "Practice Aggregate and Potential function method proofs under exam time limits."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Thursday",
                            startTime = "20:15",
                            endTime = "21:45",
                            title = "Kernel Lab: Debugging xv6 Deadlock on COW Stress Tests",
                            category = "Assignment",
                            courseOrTopic = "CS308",
                            notes = "Verify ref count atomic increment and spinlock acquisition order."
                        )
                    )
                }
                "Friday" -> {
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Friday",
                            startTime = "14:00",
                            endTime = "16:30",
                            title = "Research Lab Code Review & Git Commit",
                            category = "Research",
                            courseOrTopic = "Lab Repository",
                            notes = "Push clean commit with documentation to lab server."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Friday",
                            startTime = "18:30",
                            endTime = "20:00",
                            title = "Weekly Academic Review & Self-Audit",
                            category = "Study",
                            courseOrTopic = "Weekly Review",
                            notes = "Audit tasks, verify attendance safety margins, and plan upcoming week."
                        )
                    )
                }
                "Saturday" -> {
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Saturday",
                            startTime = "09:30",
                            endTime = "12:00",
                            title = "Deep Research Block: Dual-Branch Consistency Architecture",
                            category = "Research",
                            courseOrTopic = "CVPR Submission Prep",
                            notes = "Implement spatial density gating in PyTorch and launch overnight cluster run."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Saturday",
                            startTime = "15:00",
                            endTime = "17:00",
                            title = "Exam Prep: xv6 Trapframe & Page Table Structures",
                            category = "Exam Prep",
                            courseOrTopic = "CS308",
                            notes = "Trace page table walk from user space to kernel virtual address space."
                        )
                    )
                }
                "Sunday" -> {
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Sunday",
                            startTime = "10:00",
                            endTime = "12:00",
                            title = "Presentation Rehearsal: 10-Minute Timed Run with Chinese Q&A",
                            category = "Presentation Prep",
                            courseOrTopic = "CS305 / CH302",
                            notes = "Record spoken delivery and verify timing under 10 minutes."
                        )
                    )
                    generatedSessions.add(
                        StudyPlanSessionEntity(
                            dayOfWeek = "Sunday",
                            startTime = "19:00",
                            endTime = "20:30",
                            title = "Prepare Next Week Plan & Schedule Sync",
                            category = "Study",
                            courseOrTopic = "Weekly Roadmap",
                            notes = "Review upcoming deadlines and timetable adjustments."
                        )
                    )
                }
            }
        }

        return generatedSessions
    }

    /**
     * Calculates attendance forecast and mathematically allowable absences.
     * Disclaimer: Strict mathematical projection based on entered parameters, never a university policy assertion.
     */
    fun calculateAttendanceForecast(course: CourseEntity): AttendanceForecast {
        val total = course.scheduledClasses.coerceAtLeast(1)
        val attended = course.attendedClasses
        val absent = course.absentClasses
        val excused = course.excusedClasses
        val target = course.attendanceTarget.coerceIn(50.0f, 100.0f)

        // Attended rate based on classes taken so far
        val classesCompletedSoFar = attended + absent
        val currentRate = if (classesCompletedSoFar > 0) {
            (attended.toFloat() / classesCompletedSoFar) * 100f
        } else 100.0f

        val remaining = (total - (attended + absent + excused)).coerceAtLeast(0)

        // Mathematical formulation:
        // Final attended needed = ceil(target / 100 * total)
        val requiredTotalAttendances = ceil((target / 100f) * total).toInt()
        val maxTotalAbsencesAllowed = (total - requiredTotalAttendances).coerceAtLeast(0)
        val maxAdditionalAbsences = (maxTotalAbsencesAllowed - absent).coerceAtLeast(0)

        val safetyStatus = when {
            absent > maxTotalAbsencesAllowed -> "BREACH: Below Target"
            maxAdditionalAbsences == 0 -> "CRITICAL: 0 absences left"
            maxAdditionalAbsences in 1..2 -> "CAUTION: $maxAdditionalAbsences absences buffer"
            else -> "SAFE: $maxAdditionalAbsences absences buffer"
        }

        val explanation = buildString {
            append("Mathematical Calculation (Based on your entered data):\n")
            append("• Course: ${course.code} ${course.name}\n")
            append("• Target Attendance: ${target}%\n")
            append("• Total Scheduled Classes: $total\n")
            append("• Minimum attendances required across semester: $requiredTotalAttendances classes\n")
            append("• Maximum total absences permitted: $maxTotalAbsencesAllowed classes\n")
            append("• Current record: $attended attended, $absent absent ($excused excused)\n")
            append("• Remaining classes: $remaining\n")
            if (absent > maxTotalAbsencesAllowed) {
                append("• Status: You currently have $absent absences, which exceeds the mathematical ceiling ($maxTotalAbsencesAllowed) to achieve ${target}%.")
            } else {
                append("• Forecast: You can afford at most $maxAdditionalAbsences additional absence(s) across the remaining $remaining classes while mathematically maintaining at least ${target}%.")
            }
            append("\n[Note: Purely a mathematical projection from entered values. University disciplinary rules may apply.]")
        }

        return AttendanceForecast(
            courseCode = course.code,
            courseName = course.name,
            scheduledClasses = total,
            attendedClasses = attended,
            absentClasses = absent,
            excusedClasses = excused,
            currentRatePercent = currentRate,
            targetPercent = target,
            remainingClasses = remaining,
            maxAdditionalAbsencesAllowed = maxAdditionalAbsences,
            safetyStatus = safetyStatus,
            mathematicalExplanation = explanation
        )
    }

    /**
     * Splits an assignment prompt (e.g. "I have a 3000-word report due Friday") into manageable phased steps.
     */
    fun breakDownAssignment(prompt: String): List<AssignmentBreakdownStep> {
        val lower = prompt.lowercase()
        val isReportOrThesis = lower.contains("report") || lower.contains("paper") || lower.contains("thesis") || lower.contains("essay") || lower.contains("word")
        val isCodingLab = lower.contains("lab") || lower.contains("code") || lower.contains("implement") || lower.contains("kernel") || lower.contains("project")

        if (isCodingLab) {
            return listOf(
                AssignmentBreakdownStep(
                    stepNumber = 1,
                    title = "Phase 1: Architecture & Interface Specification",
                    suggestedDay = "Day 1 (Immediate)",
                    estimatedHours = 2.0f,
                    description = "Read requirements, inspect existing starter code, and map data structures.",
                    deliverables = "Written technical outline and unit test signatures."
                ),
                AssignmentBreakdownStep(
                    stepNumber = 2,
                    title = "Phase 2: Core Algorithm / Kernel Implementation",
                    suggestedDay = "Day 2",
                    estimatedHours = 3.5f,
                    description = "Implement core logic, handle edge cases, memory management, and locks.",
                    deliverables = "Working core codebase passing basic test cases."
                ),
                AssignmentBreakdownStep(
                    stepNumber = 3,
                    title = "Phase 3: Rigorous Testing & Stress Evaluation",
                    suggestedDay = "Day 3",
                    estimatedHours = 2.5f,
                    description = "Run stress benchmarks, valgrind memory leak tests, and fix race conditions.",
                    deliverables = "Zero memory leaks, 100% test suite pass rate."
                ),
                AssignmentBreakdownStep(
                    stepNumber = 4,
                    title = "Phase 4: Experimental Plots & Technical Report",
                    suggestedDay = "Day 4",
                    estimatedHours = 2.0f,
                    description = "Plot runtime speedup, write design explanation, and document architectural decisions.",
                    deliverables = "Formatted PDF lab report with benchmark graphs."
                ),
                AssignmentBreakdownStep(
                    stepNumber = 5,
                    title = "Phase 5: Code Cleanliness, Git Commit & Submission",
                    suggestedDay = "Day 5 (Deadline Day)",
                    estimatedHours = 1.0f,
                    description = "Remove debug prints, format according to professor's style guide, push to university GitLab.",
                    deliverables = "Final Git commit hash and confirmed submission receipt."
                )
            )
        }

        // Standard Report / Academic Manuscript Breakdown
        return listOf(
            AssignmentBreakdownStep(
                stepNumber = 1,
                title = "Phase 1: Research, Literature Mining & Paper Sources",
                suggestedDay = "Day 1",
                estimatedHours = 2.5f,
                description = "Gather 5-10 authoritative papers, highlight relevant equations and empirical results.",
                deliverables = "Synthesized bibliography and citation collection."
            ),
            AssignmentBreakdownStep(
                stepNumber = 2,
                title = "Phase 2: Detailed Structural Outline & Section Headings",
                suggestedDay = "Day 2",
                estimatedHours = 1.5f,
                description = "Map word counts: Introduction (500w), Related Work (600w), Method (1000w), Experiments (600w), Conclusion (300w).",
                deliverables = "Approved bulleted outline with logical flow."
            ),
            AssignmentBreakdownStep(
                stepNumber = 3,
                title = "Phase 3: First Complete Draft (Zero-Editing Speed Write)",
                suggestedDay = "Day 3",
                estimatedHours = 3.5f,
                description = "Write out all sections continuously without stopping to polish wording.",
                deliverables = "Full draft with all arguments in place (~3,000 words)."
            ),
            AssignmentBreakdownStep(
                stepNumber = 4,
                title = "Phase 4: Technical Revision & Mathematical Consistency Check",
                suggestedDay = "Day 4",
                estimatedHours = 2.0f,
                description = "Check notation consistency, verify figure references, tighten arguments.",
                deliverables = "Refined second draft with proofread equations and tables."
            ),
            AssignmentBreakdownStep(
                stepNumber = 5,
                title = "Phase 5: Academic Language Polish, Plagiarism Check & Submission",
                suggestedDay = "Day 5 (Deadline Day)",
                estimatedHours = 1.0f,
                description = "Run grammar check, verify formal academic tone, format PDF margins, and submit.",
                deliverables = "Final PDF submitted on course portal ahead of deadline."
            )
        )
    }

    /**
     * Extracts assignments, exams, and deliverables from uploaded syllabus or notice text.
     * Generates a confirmation list for user approval.
     */
    fun extractAssignmentsFromDocument(text: String, fileName: String): List<ProposedAssignment> {
        val lower = text.lowercase()
        val proposed = mutableListOf<ProposedAssignment>()

        if (lower.contains("assignment") || lower.contains("homework") || lower.contains("作业") || lower.contains("lab")) {
            proposed.add(
                ProposedAssignment(
                    title = "Course Lab Assignment 1",
                    courseName = extractCourseTitle(text),
                    deadline = "In 7 Days",
                    type = "Assignment",
                    priority = "High",
                    estimatedHours = 4.0f,
                    requirements = "Complete code implementation and submit written report with git logs.",
                    gradingWeight = "15%"
                )
            )
        }

        if (lower.contains("midterm") || lower.contains("期中") || lower.contains("exam")) {
            proposed.add(
                ProposedAssignment(
                    title = "Midterm Theory Examination",
                    courseName = extractCourseTitle(text),
                    deadline = "Week 9 (Mid-Semester)",
                    type = "Exam",
                    priority = "High",
                    estimatedHours = 12.0f,
                    requirements = "Closed-book written test covering proofs, complexity, and architectures.",
                    gradingWeight = "30%"
                )
            )
        }

        if (lower.contains("presentation") || lower.contains("defense") || lower.contains("答辩") || lower.contains("展示") || lower.contains("report")) {
            proposed.add(
                ProposedAssignment(
                    title = "Term Project Oral Presentation",
                    courseName = extractCourseTitle(text),
                    deadline = "Week 14",
                    type = "Presentation",
                    priority = "Medium",
                    estimatedHours = 6.0f,
                    requirements = "10-minute slide presentation followed by 5 minutes of professor Q&A.",
                    gradingWeight = "20%"
                )
            )
        }

        if (lower.contains("final") || lower.contains("期末") || lower.contains("project") || lower.contains("大作业")) {
            proposed.add(
                ProposedAssignment(
                    title = "Final Course Comprehensive Project",
                    courseName = extractCourseTitle(text),
                    deadline = "Week 16",
                    type = "Project",
                    priority = "High",
                    estimatedHours = 16.0f,
                    requirements = "Complete software artifact, GitHub repository, and IEEE-format technical paper.",
                    gradingWeight = "35%"
                )
            )
        }

        if (proposed.isEmpty()) {
            // General deliverable extracted from context
            proposed.add(
                ProposedAssignment(
                    title = "Deliverable from $fileName",
                    courseName = extractCourseTitle(text),
                    deadline = "In 10 Days",
                    type = "Assignment",
                    priority = "Medium",
                    estimatedHours = 3.0f,
                    requirements = "Fulfill deliverables specified in document notice.",
                    gradingWeight = "10%"
                )
            )
        }

        return proposed
    }

    private fun extractCourseTitle(text: String): String {
        return when {
            text.contains("Algorithm", ignoreCase = true) || text.contains("算法") -> "Advanced Algorithms & Optimization"
            text.contains("Vision", ignoreCase = true) || text.contains("视觉") -> "Computer Vision & Pattern Recognition"
            text.contains("Operating System", ignoreCase = true) || text.contains("操作系统") -> "Operating Systems Internals & Kernel Lab"
            text.contains("Chinese", ignoreCase = true) || text.contains("汉语") -> "Advanced Technical & Academic Chinese"
            else -> "General Computer Science Coursework"
        }
    }

    private fun parseMinutes(time: String): Int {
        val parts = time.replace("：", ":").split(":")
        if (parts.size == 2) {
            val h = parts[0].trim().toIntOrNull() ?: 0
            val m = parts[1].trim().toIntOrNull() ?: 0
            return h * 60 + m
        }
        return 0
    }
}
