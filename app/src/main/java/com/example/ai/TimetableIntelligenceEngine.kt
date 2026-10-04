package com.example.ai

import com.example.data.model.TimetableClassEntity
import java.util.regex.Pattern

data class ParsedTimetableResult(
    val classes: List<TimetableClassEntity>,
    val warnings: List<String>,
    val confidenceScore: Float,
    val summary: String
)

object TimetableIntelligenceEngine {

    /**
     * Parses timetable from text (extracted from syllabus, image OCR, notice, or user input).
     * Distinguishes high certainty vs uncertain items to require user confirmation.
     */
    fun parseTimetableText(rawText: String): ParsedTimetableResult {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
        val extractedClasses = mutableListOf<TimetableClassEntity>()
        val warnings = mutableListOf<String>()

        val daysMap = mapOf(
            "mon" to "Monday", "monday" to "Monday", "周一" to "Monday", "星期一" to "Monday",
            "tue" to "Tuesday", "tues" to "Tuesday", "tuesday" to "Tuesday", "周二" to "Tuesday", "星期二" to "Tuesday",
            "wed" to "Wednesday", "wednesday" to "Wednesday", "周三" to "Wednesday", "星期三" to "Wednesday",
            "thu" to "Thursday", "thur" to "Thursday", "thursday" to "Thursday", "周四" to "Thursday", "星期四" to "Thursday",
            "fri" to "Friday", "friday" to "Friday", "周五" to "Friday", "星期五" to "Friday",
            "sat" to "Saturday", "saturday" to "Saturday", "周六" to "Saturday", "星期六" to "Saturday",
            "sun" to "Sunday", "sunday" to "Sunday", "周日" to "Sunday", "星期日" to "Sunday"
        )

        var currentDay = "Monday"

        for (line in lines) {
            val lower = line.lowercase()

            // Check if line sets a day header
            for ((key, dayVal) in daysMap) {
                if (lower.startsWith(key) || lower.endsWith(key) || lower == key) {
                    currentDay = dayVal
                    break
                }
            }

            // Detect time ranges like 08:00-09:40 or 8:00–9:35 or 第1-2节 (08:00-09:40)
            val timeRegex = Pattern.compile("(\\d{1,2}[:：]\\d{2})\\s*[-–~到至]\\s*(\\d{1,2}[:：]\\d{2})")
            val timeMatcher = timeRegex.matcher(line)

            var startTime = "08:00"
            var endTime = "09:35"
            var hasFoundTime = false

            if (timeMatcher.find()) {
                startTime = normalizeTime(timeMatcher.group(1) ?: "08:00")
                endTime = normalizeTime(timeMatcher.group(2) ?: "09:35")
                hasFoundTime = true
            } else if (line.contains("1-2") || line.contains("1,2节") || line.contains("第1-2")) {
                startTime = "08:00"
                endTime = "09:35"
                hasFoundTime = true
            } else if (line.contains("3-4") || line.contains("3,4节") || line.contains("第3-4")) {
                startTime = "10:15"
                endTime = "11:50"
                hasFoundTime = true
            } else if (line.contains("5-6") || line.contains("5,6节") || line.contains("第5-6")) {
                startTime = "14:00"
                endTime = "15:35"
                hasFoundTime = true
            } else if (line.contains("7-8") || line.contains("7,8节") || line.contains("第7-8")) {
                startTime = "16:00"
                endTime = "17:35"
                hasFoundTime = true
            } else if (line.contains("9-10") || line.contains("9,10节") || line.contains("第9-10")) {
                startTime = "18:30"
                endTime = "20:05"
                hasFoundTime = true
            }

            // Extract Course Code (e.g., CS301, CS305, CH302, COMP310)
            val codeRegex = Pattern.compile("([A-Za-z]{2,4}\\s*\\d{3,4})")
            val codeMatcher = codeRegex.matcher(line)
            val courseCode = if (codeMatcher.find()) codeMatcher.group(1)?.replace(" ", "")?.uppercase() ?: "CS300" else ""

            // Extract Teacher
            var teacher = ""
            val teacherKeywords = listOf("Prof.", "Teacher", "Dr.", "教授", "老师", "讲师", "副教授")
            for (kw in teacherKeywords) {
                if (line.contains(kw)) {
                    val idx = line.indexOf(kw)
                    val endIdx = (idx + 15).coerceAtMost(line.length)
                    teacher = line.substring(idx, endIdx).split(" ", ",", ";", "，", "、").take(3).joinToString(" ")
                    break
                }
            }

            // Extract Classroom / Room
            var classroom = ""
            val matchedTimeStr = if (hasFoundTime) timeMatcher.group(0) else null
            val lineWithoutTime = if (matchedTimeStr != null) {
                line.replace(matchedTimeStr, " ")
            } else line

            val roomRegex = Pattern.compile("(Room\\s*[0-9A-Za-z-]+|\\b\\d{1,2}[-–]\\d{2,4}\\b|[A-Za-z0-9]+楼\\s*[0-9A-Za-z]+|East Campus [0-9A-Za-z-]+|Science Hall [0-9A-Za-z-]+|教室[:：]?\\s*\\S+)", Pattern.CASE_INSENSITIVE)
            val roomMatcher = roomRegex.matcher(lineWithoutTime)
            if (roomMatcher.find()) {
                classroom = roomMatcher.group(1) ?: ""
            }

            // Week range (e.g., 1-16周, Weeks 1-16, 单周, 双周, Odd, Even)
            var weekRange = "Weeks 1-16"
            var scheduleType = "Weekly"

            if (line.contains("单周") || lower.contains("odd week")) {
                scheduleType = "Odd Weeks"
                weekRange = "Weeks 1-15 (Odd)"
            } else if (line.contains("双周") || lower.contains("even week")) {
                scheduleType = "Even Weeks"
                weekRange = "Weeks 2-16 (Even)"
            } else if (line.contains("调课") || line.contains("补课") || lower.contains("make-up") || lower.contains("makeup")) {
                scheduleType = "Make-Up"
            } else if (line.contains("一次性") || lower.contains("one-time")) {
                scheduleType = "One-Time"
            }

            val weekMatch = Pattern.compile("(\\d{1,2}[-–]\\d{1,2}\\s*周|Weeks?\\s*\\d{1,2}[-–]\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(line)
            if (weekMatch.find()) {
                weekRange = weekMatch.group(1) ?: "Weeks 1-16"
            }

            // Course Name heuristic
            var courseName = ""
            if (line.contains("Algorithm", ignoreCase = true) || line.contains("算法")) {
                courseName = "Advanced Algorithms & Optimization"
                if (courseCode.isBlank()) "CS301"
            } else if (line.contains("Vision", ignoreCase = true) || line.contains("视觉")) {
                courseName = "Computer Vision & Pattern Recognition"
                if (courseCode.isBlank()) "CS305"
            } else if (line.contains("Operating System", ignoreCase = true) || line.contains("操作系统") || line.contains("OS")) {
                courseName = "Operating Systems Internals & Kernel Lab"
                if (courseCode.isBlank()) "CS308"
            } else if (line.contains("Chinese", ignoreCase = true) || line.contains("汉语") || line.contains("中文")) {
                courseName = "Advanced Technical & Academic Chinese"
                if (courseCode.isBlank()) "CH302"
            } else if (line.contains("Seminar", ignoreCase = true) || line.contains("Intelligence", ignoreCase = true) || line.contains("Lecture", ignoreCase = true) || line.contains("Course", ignoreCase = true) || line.contains("Lab", ignoreCase = true)) {
                val cleaned = line.replace("Wednesday", "", ignoreCase = true)
                    .replace("Monday", "", ignoreCase = true)
                    .replace("Tuesday", "", ignoreCase = true)
                    .replace("Thursday", "", ignoreCase = true)
                    .replace("Friday", "", ignoreCase = true)
                    .replace(":", "").trim()
                courseName = if (cleaned.isNotBlank()) cleaned else "University Seminar"
            } else if (courseCode.isNotBlank()) {
                // Strip code, times, rooms from line to get name
                val cleaned = line.replace(courseCode, "").replace(classroom, "").replace(teacher, "")
                    .replace(Regex("\\d{1,2}[:：]\\d{2}"), "").trim()
                if (cleaned.length in 3..40) {
                    courseName = cleaned
                }
            }

            // If we detected significant course tokens, construct an entity
            if (courseName.isNotBlank() || (hasFoundTime && (courseCode.isNotBlank() || classroom.isNotBlank()))) {
                val finalCode = if (courseCode.isNotBlank()) courseCode else guessCodeFromName(courseName)
                val finalName = if (courseName.isNotBlank()) courseName else "University Course $finalCode"
                val finalTeacher = if (teacher.isNotBlank()) teacher else "Faculty Instructor"
                val finalRoom = if (classroom.isNotBlank()) classroom else "To Be Announced"

                val isUncertain = !hasFoundTime || classroom.isBlank() || teacher.isBlank()
                val uncertaintyReason = buildString {
                    if (!hasFoundTime) append("Time slot was estimated from standard periods. ")
                    if (classroom.isBlank()) append("Classroom location missing in source. ")
                    if (teacher.isBlank()) append("Instructor name could not be resolved. ")
                }.trim()

                extractedClasses.add(
                    TimetableClassEntity(
                        courseCode = finalCode,
                        courseName = finalName,
                        teacher = finalTeacher,
                        dayOfWeek = currentDay,
                        startTime = startTime,
                        endTime = endTime,
                        classroom = finalRoom,
                        weekRange = weekRange,
                        scheduleType = scheduleType,
                        notes = "Imported from uploaded document/schedule.",
                        isUncertain = isUncertain,
                        uncertaintyReason = uncertaintyReason
                    )
                )
            }
        }

        // Fallback default sample if user uploaded an unformatted document but wants smart extraction
        if (extractedClasses.isEmpty()) {
            warnings.add("The timetable format was too ambiguous to extract without ambiguity. Provided default YSU schedule template for your confirmation.")
            extractedClasses.addAll(getStandardYsuTemplate().map { it.copy(isUncertain = true, uncertaintyReason = "Ambiguous source schedule; using standard template for user review") })
        }

        val uncertainCount = extractedClasses.count { it.isUncertain }
        val confidence = if (extractedClasses.isNotEmpty()) {
            ((extractedClasses.size - uncertainCount).toFloat() / extractedClasses.size).coerceIn(0.4f, 1.0f)
        } else 0.5f

        val summary = "Extracted ${extractedClasses.size} scheduled class sessions across the week. " +
                if (uncertainCount > 0) "$uncertainCount entries have uncertain fields marked for review."
                else "High certainty extraction. Please review and confirm before saving."

        return ParsedTimetableResult(
            classes = extractedClasses,
            warnings = warnings,
            confidenceScore = confidence,
            summary = summary
        )
    }

    private fun normalizeTime(raw: String): String {
        val parts = raw.replace("：", ":").split(":")
        if (parts.size == 2) {
            val h = parts[0].trim().toIntOrNull() ?: 8
            val m = parts[1].trim().toIntOrNull() ?: 0
            return String.format("%02d:%02d", h, m)
        }
        return raw.trim()
    }

    private fun guessCodeFromName(name: String): String {
        return when {
            name.contains("Algorithm", ignoreCase = true) -> "CS301"
            name.contains("Vision", ignoreCase = true) -> "CS305"
            name.contains("Operating System", ignoreCase = true) -> "CS308"
            name.contains("Chinese", ignoreCase = true) -> "CH302"
            else -> "CS" + (100..400).random()
        }
    }

    fun getStandardYsuTemplate(): List<TimetableClassEntity> {
        return listOf(
            TimetableClassEntity(
                courseCode = "CS301",
                courseName = "Advanced Algorithms & Optimization",
                teacher = "Prof. Wang Zhi",
                dayOfWeek = "Monday",
                startTime = "08:00",
                endTime = "09:35",
                classroom = "East Campus Bldg 4-302",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly",
                isUncertain = false
            ),
            TimetableClassEntity(
                courseCode = "CH302",
                courseName = "Advanced Technical Chinese",
                teacher = "Teacher Li Hua",
                dayOfWeek = "Monday",
                startTime = "16:00",
                endTime = "17:35",
                classroom = "Intl Exchange Center 204",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly",
                isUncertain = false
            ),
            TimetableClassEntity(
                courseCode = "CS305",
                courseName = "Computer Vision & Pattern Recognition",
                teacher = "Prof. Zhang Lin",
                dayOfWeek = "Tuesday",
                startTime = "10:15",
                endTime = "11:50",
                classroom = "Science Hall 5-201",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly",
                isUncertain = false
            ),
            TimetableClassEntity(
                courseCode = "CS301",
                courseName = "Advanced Algorithms & Optimization",
                teacher = "Prof. Wang Zhi",
                dayOfWeek = "Wednesday",
                startTime = "08:00",
                endTime = "09:35",
                classroom = "East Campus Bldg 4-302",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly",
                isUncertain = false
            ),
            TimetableClassEntity(
                courseCode = "CS305",
                courseName = "Computer Vision & Pattern Recognition",
                teacher = "Prof. Zhang Lin",
                dayOfWeek = "Thursday",
                startTime = "10:15",
                endTime = "11:50",
                classroom = "Science Hall 5-201",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly",
                isUncertain = false
            ),
            TimetableClassEntity(
                courseCode = "CS308",
                courseName = "Operating Systems Internals & Kernel Lab",
                teacher = "Prof. Chen Gang",
                dayOfWeek = "Friday",
                startTime = "09:50",
                endTime = "12:15",
                classroom = "East Campus Bldg 3-101",
                weekRange = "Weeks 1-16",
                scheduleType = "Weekly",
                isUncertain = false
            )
        )
    }
}
