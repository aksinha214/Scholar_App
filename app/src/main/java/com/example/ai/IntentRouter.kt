package com.example.ai

import com.example.ui.viewmodel.AppScreen

enum class CommandIntent(val displayName: String, val primaryScreen: AppScreen) {
    STUDY_TODAY("Study Planner & Timetable", AppScreen.UNIVERSITY),
    RESEARCH_PROGRESS("Research Lab Progress", AppScreen.RESEARCH_LAB),
    DEADLINES_TASKS("Upcoming Deadlines & Tasks", AppScreen.UNIVERSITY),
    PRESENTATION_PREP("Presentation Preparation", AppScreen.DOCUMENTS),
    CAREER_SKILLS("Career & Skills Advisory", AppScreen.CAREER_CENTER),
    CHINESE_PRACTICE("Chinese Practice & Vocab", AppScreen.CHINESE_LANGUAGE),
    CHINA_WORK_VISA("China Work & Immigration", AppScreen.CHINA_WORK),
    KNOWLEDGE_SEARCH("Personal Knowledge Base", AppScreen.DOCUMENTS),
    ATTENDANCE_TIMETABLE("Attendance & Schedule", AppScreen.UNIVERSITY),
    PLAN_MY_DAY("Daily Briefing & Priorities", AppScreen.COMMAND_CENTER),
    MULTI_MODULE("Multi-Module Intelligence", AppScreen.COMMAND_CENTER),
    GENERAL_QUERY("General Academic Assistant", AppScreen.AI_MENTOR)
}

data class IntentRoutingResult(
    val primaryIntent: CommandIntent,
    val targetModules: List<AppScreen>,
    val explanation: String,
    val confidence: Float,
    val detectedKeywords: List<String>,
    val isMultiModule: Boolean
)

object IntentRouter {

    fun routeQuery(rawQuery: String): IntentRoutingResult {
        val q = rawQuery.trim().lowercase()
        val detectedKeywords = mutableListOf<String>()

        // 1. Multi-Module query check (e.g. research presentation prep)
        val isPresentation = q.contains("presentation") || q.contains("defense") || q.contains("slides") || q.contains("talk") || q.contains("开题")
        val isResearch = q.contains("research") || q.contains("paper") || q.contains("experiment") || q.contains("milestone") || q.contains("lab") || q.contains("progress")
        val isCareer = q.contains("job") || q.contains("career") || q.contains("interview") || q.contains("skill") || q.contains("resume") || q.contains("cv")
        val isStudy = q.contains("study") || q.contains("assignment") || q.contains("homework") || q.contains("exam") || q.contains("course") || q.contains("class")
        val isSchedule = q.contains("today") || q.contains("schedule") || q.contains("timetable") || q.contains("day") || q.contains("plan")
        val isChinese = q.contains("chinese") || q.contains("hsk") || q.contains("vocab") || q.contains("pinyin") || q.contains("mandarin") || q.contains("中文")
        val isImmigration = q.contains("visa") || q.contains("permit") || q.contains("immigration") || q.contains("residence") || q.contains("safea") || q.contains("transition") || q.contains("graduat") || q.contains("work in china")
        val isKnowledge = q.contains("notes") || q.contains("knowledge") || q.contains("document") || q.contains("pdf") || q.contains("summar") || q.contains("literature")

        // Specific Multi-Module cases
        if (isPresentation && isResearch) {
            detectedKeywords.addAll(listOf("presentation", "research", "defense"))
            return IntentRoutingResult(
                primaryIntent = CommandIntent.MULTI_MODULE,
                targetModules = listOf(AppScreen.RESEARCH_LAB, AppScreen.UNIVERSITY, AppScreen.DOCUMENTS, AppScreen.AI_MENTOR),
                explanation = "Routing to Research Lab, University Schedule, Knowledge Base, and Presentation Coach.",
                confidence = 0.95f,
                detectedKeywords = detectedKeywords,
                isMultiModule = true
            )
        }

        // Study query check (e.g. "What should I study today?")
        if (isStudy && !isCareer && !isResearch) {
            detectedKeywords.addAll(listOf("study", "timetable"))
            return IntentRoutingResult(
                primaryIntent = CommandIntent.STUDY_TODAY,
                targetModules = listOf(AppScreen.UNIVERSITY),
                explanation = "Query routed to Intelligent Study Planner and Timetable Intelligence.",
                confidence = 0.94f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        val isPlanDay = q.contains("most important") || q.contains("priorit") || q.contains("plan my day") || q.contains("briefing") || (isSchedule && !isChinese && !isImmigration)

        if (isPlanDay) {
            detectedKeywords.add("plan_day")
            return IntentRoutingResult(
                primaryIntent = CommandIntent.PLAN_MY_DAY,
                targetModules = listOf(AppScreen.COMMAND_CENTER, AppScreen.UNIVERSITY, AppScreen.RESEARCH_LAB),
                explanation = "Synthesizing cross-module daily priorities from Timetable, Tasks, and Milestones.",
                confidence = 0.92f,
                detectedKeywords = detectedKeywords,
                isMultiModule = true
            )
        }

        // Single / Direct Module Routing
        if (q.contains("attendance") || q.contains("absent") || q.contains("present") || q.contains("class record")) {
            detectedKeywords.add("attendance")
            return IntentRoutingResult(
                primaryIntent = CommandIntent.ATTENDANCE_TIMETABLE,
                targetModules = listOf(AppScreen.UNIVERSITY),
                explanation = "Query mapped to University Life attendance records and class statistics.",
                confidence = 0.94f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        if (isImmigration) {
            detectedKeywords.addAll(listOf("immigration", "visa", "work_permit"))
            return IntentRoutingResult(
                primaryIntent = CommandIntent.CHINA_WORK_VISA,
                targetModules = listOf(AppScreen.CHINA_WORK),
                explanation = "Query routed to Stage 6 China Student & Work Transition assistant with official sources.",
                confidence = 0.95f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        if (isChinese) {
            detectedKeywords.addAll(listOf("chinese", "language"))
            return IntentRoutingResult(
                primaryIntent = CommandIntent.CHINESE_PRACTICE,
                targetModules = listOf(AppScreen.CHINESE_LANGUAGE),
                explanation = "Query routed to Chinese Language Coach (SRS vocabulary and dialogue).",
                confidence = 0.96f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        if (isResearch && !isStudy) {
            detectedKeywords.add("research")
            return IntentRoutingResult(
                primaryIntent = CommandIntent.RESEARCH_PROGRESS,
                targetModules = listOf(AppScreen.RESEARCH_LAB),
                explanation = "Query routed to Research Lab (projects, experiments, and milestones).",
                confidence = 0.93f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        if (isCareer) {
            detectedKeywords.addAll(listOf("career", "jobs", "skills"))
            return IntentRoutingResult(
                primaryIntent = CommandIntent.CAREER_SKILLS,
                targetModules = listOf(AppScreen.CAREER_CENTER),
                explanation = "Query routed to Career Center & China Job Assistant.",
                confidence = 0.92f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        if (isPresentation) {
            detectedKeywords.add("presentation")
            return IntentRoutingResult(
                primaryIntent = CommandIntent.PRESENTATION_PREP,
                targetModules = listOf(AppScreen.DOCUMENTS, AppScreen.UNIVERSITY),
                explanation = "Query routed to Presentation Coach and Document Intelligence.",
                confidence = 0.91f,
                detectedKeywords = detectedKeywords,
                isMultiModule = true
            )
        }

        if (q.contains("deadline") || q.contains("task") || q.contains("pending") || q.contains("assignment")) {
            detectedKeywords.addAll(listOf("deadlines", "tasks"))
            return IntentRoutingResult(
                primaryIntent = CommandIntent.DEADLINES_TASKS,
                targetModules = listOf(AppScreen.UNIVERSITY),
                explanation = "Query routed to Academic Tasks and Study Planner deadlines.",
                confidence = 0.92f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        if (isKnowledge) {
            detectedKeywords.add("knowledge_base")
            return IntentRoutingResult(
                primaryIntent = CommandIntent.KNOWLEDGE_SEARCH,
                targetModules = listOf(AppScreen.DOCUMENTS),
                explanation = "Query routed to Personal Knowledge Base and Document Intelligence.",
                confidence = 0.90f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        if (isStudy) {
            detectedKeywords.addAll(listOf("study", "timetable"))
            return IntentRoutingResult(
                primaryIntent = CommandIntent.STUDY_TODAY,
                targetModules = listOf(AppScreen.UNIVERSITY),
                explanation = "Query routed to Intelligent Study Planner and Timetable Intelligence.",
                confidence = 0.89f,
                detectedKeywords = detectedKeywords,
                isMultiModule = false
            )
        }

        // Default fallback
        detectedKeywords.add("general")
        return IntentRoutingResult(
            primaryIntent = CommandIntent.GENERAL_QUERY,
            targetModules = listOf(AppScreen.COMMAND_CENTER, AppScreen.AI_MENTOR),
            explanation = "General academic query routed across CS Scholar OS knowledge repository.",
            confidence = 0.75f,
            detectedKeywords = detectedKeywords,
            isMultiModule = false
        )
    }
}
