package com.example.ai

import com.example.ui.viewmodel.AppScreen

data class GlobalSearchResult(
    val id: String,
    val title: String,
    val module: AppScreen,
    val moduleName: String,
    val shortPreview: String,
    val dateStr: String? = null,
    val category: String,
    val matchRelevance: Int = 100
)

object GlobalSearchEngine {

    /**
     * Performs an in-memory cross-module search over the unified scholar context.
     * Searches across tasks, timetable, research projects, milestones, papers, career records, Chinese vocab, and knowledge base.
     */
    fun search(context: UnifiedScholarContext, query: String): List<GlobalSearchResult> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()

        val results = mutableListOf<GlobalSearchResult>()

        // 1. Academic Tasks & Assignments
        context.tasks.forEach { task ->
            if (task.title.lowercase().contains(q) ||
                task.courseName.lowercase().contains(q) ||
                task.description.lowercase().contains(q) ||
                task.type.lowercase().contains(q)
            ) {
                results.add(
                    GlobalSearchResult(
                        id = "task_${task.id}",
                        title = task.title,
                        module = AppScreen.UNIVERSITY,
                        moduleName = "Study Planner",
                        shortPreview = "${task.courseName} • ${task.type} • Status: ${task.status} • Weight: ${task.gradingWeight}",
                        dateStr = task.deadline,
                        category = "Academic Task"
                    )
                )
            }
        }

        // 2. Timetable Classes
        context.timetableClasses.forEach { cls ->
            if (cls.courseName.lowercase().contains(q) ||
                cls.courseCode.lowercase().contains(q) ||
                cls.teacher.lowercase().contains(q) ||
                cls.classroom.lowercase().contains(q)
            ) {
                results.add(
                    GlobalSearchResult(
                        id = "timetable_${cls.id}",
                        title = "${cls.courseCode} ${cls.courseName}",
                        module = AppScreen.UNIVERSITY,
                        moduleName = "University Timetable",
                        shortPreview = "${cls.dayOfWeek} ${cls.startTime}-${cls.endTime} @ ${cls.classroom} (Instructor: ${cls.teacher})",
                        dateStr = cls.dayOfWeek,
                        category = "Timetable"
                    )
                )
            }
        }

        // 3. Research Projects
        context.researchProjects.forEach { proj ->
            if (proj.title.lowercase().contains(q) ||
                proj.researchArea.lowercase().contains(q) ||
                proj.description.lowercase().contains(q) ||
                proj.targetVenue.lowercase().contains(q)
            ) {
                results.add(
                    GlobalSearchResult(
                        id = "research_proj_${proj.id}",
                        title = proj.title,
                        module = AppScreen.RESEARCH_LAB,
                        moduleName = "Research Lab",
                        shortPreview = "Area: ${proj.researchArea} • Target: ${proj.targetVenue} • Progress: ${proj.progressPercent}%",
                        dateStr = "Updated " + java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(proj.lastUpdated)),
                        category = "Research Project"
                    )
                )
            }
        }

        // 4. Research Milestones
        context.researchMilestones.forEach { m ->
            if (m.title.lowercase().contains(q) || m.notes.lowercase().contains(q) || m.status.lowercase().contains(q)) {
                results.add(
                    GlobalSearchResult(
                        id = "research_milestone_${m.id}",
                        title = m.title,
                        module = AppScreen.RESEARCH_LAB,
                        moduleName = "Research Lab",
                        shortPreview = "Status: ${m.status} • Completed: ${m.isCompleted} • Notes: ${m.notes.take(60)}",
                        dateStr = m.deadline,
                        category = "Research Milestone"
                    )
                )
            }
        }

        // 5. Research Papers & Literature Matrix
        context.researchPapers.forEach { paper ->
            if (paper.title.lowercase().contains(q) ||
                paper.authors.lowercase().contains(q) ||
                paper.venue.lowercase().contains(q) ||
                paper.abstractText.lowercase().contains(q) ||
                paper.researchProblem.lowercase().contains(q)
            ) {
                results.add(
                    GlobalSearchResult(
                        id = "research_paper_${paper.id}",
                        title = paper.title,
                        module = AppScreen.RESEARCH_LAB,
                        moduleName = "Research Lab",
                        shortPreview = "${paper.authors} (${paper.year}) @ ${paper.venue} • Problem: ${paper.researchProblem.take(80)}",
                        dateStr = paper.year,
                        category = "Literature Matrix"
                    )
                )
            }
        }

        // 6. Career Records (Job Postings, Applications, Skills)
        context.jobPostings.forEach { job ->
            if (job.jobTitle.lowercase().contains(q) ||
                job.company.lowercase().contains(q) ||
                job.location.lowercase().contains(q) ||
                job.responsibilities.lowercase().contains(q) ||
                job.industry.lowercase().contains(q)
            ) {
                results.add(
                    GlobalSearchResult(
                        id = "career_job_${job.id}",
                        title = "${job.jobTitle} @ ${job.company}",
                        module = AppScreen.CAREER_CENTER,
                        moduleName = "Career Center",
                        shortPreview = "${job.location} • Salary: ${job.salary} • Industry: ${job.industry}",
                        dateStr = job.dateChecked,
                        category = "Job Opportunity"
                    )
                )
            }
        }

        context.skills.forEach { skill ->
            if (skill.skillName.lowercase().contains(q) || skill.category.lowercase().contains(q) || skill.evidence.lowercase().contains(q)) {
                results.add(
                    GlobalSearchResult(
                        id = "skill_${skill.id}",
                        title = "Skill: ${skill.skillName}",
                        module = AppScreen.CAREER_CENTER,
                        moduleName = "Career Center",
                        shortPreview = "Category: ${skill.category} • Level: ${skill.currentLevel} (Evidence: ${skill.evidence.take(40)})",
                        dateStr = skill.lastPracticed,
                        category = "Skill Inventory"
                    )
                )
            }
        }

        // 7. Chinese Vocabulary
        context.chineseVocab.forEach { vocab ->
            if (vocab.hanzi.contains(q) ||
                vocab.pinyin.lowercase().contains(q) ||
                vocab.english.lowercase().contains(q) ||
                vocab.category.lowercase().contains(q)
            ) {
                results.add(
                    GlobalSearchResult(
                        id = "chinese_vocab_${vocab.id}",
                        title = "${vocab.hanzi} (${vocab.pinyin})",
                        module = AppScreen.CHINESE_LANGUAGE,
                        moduleName = "Chinese Coach",
                        shortPreview = "Meaning: ${vocab.english} • HSK ${vocab.hskLevel} • Category: ${vocab.category}",
                        dateStr = if (vocab.nextReviewDate.isNotBlank()) "Review: ${vocab.nextReviewDate}" else null,
                        category = "Chinese Vocabulary"
                    )
                )
            }
        }

        // 8. Personal Knowledge Base & Documents
        context.knowledgeItems.forEach { item ->
            if (item.title.lowercase().contains(q) ||
                item.content.lowercase().contains(q) ||
                item.tags.lowercase().contains(q) ||
                item.category.lowercase().contains(q)
            ) {
                results.add(
                    GlobalSearchResult(
                        id = "kb_${item.id}",
                        title = item.title,
                        module = AppScreen.DOCUMENTS,
                        moduleName = "Knowledge Base",
                        shortPreview = item.content.take(100),
                        dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(item.dateAdded)),
                        category = "Knowledge Item"
                    )
                )
            }
        }

        // 9. China Work Policies & Graduation Checklist
        context.cityPolicies.forEach { pol ->
            if (pol.city.lowercase().contains(q) || pol.policyTitle.lowercase().contains(q) || pol.eligibility.lowercase().contains(q)) {
                results.add(
                    GlobalSearchResult(
                        id = "policy_${pol.id}",
                        title = "${pol.city}: ${pol.policyTitle}",
                        module = AppScreen.CHINA_WORK,
                        moduleName = "China Work & Visa",
                        shortPreview = "${pol.eligibility.take(90)} • Authority: ${pol.applicationAuthority}",
                        dateStr = pol.lastVerifiedDate,
                        category = "Immigration Policy"
                    )
                )
            }
        }

        return results
    }
}
