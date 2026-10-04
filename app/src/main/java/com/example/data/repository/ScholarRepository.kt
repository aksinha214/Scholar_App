package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class ScholarRepository(private val db: AppDatabase) {

    // User Profile
    val userProfile: Flow<UserProfileEntity?> = db.userProfileDao().getUserProfile()
    suspend fun getUserProfileOnce(): UserProfileEntity? = db.userProfileDao().getUserProfileOnce()
    suspend fun updateProfile(profile: UserProfileEntity) = db.userProfileDao().insertOrUpdateProfile(profile)

    // University Profile
    val universityProfile: Flow<UniversityProfileEntity?> = db.universityDao().getUniversityProfile()
    suspend fun getUniversityProfileOnce(): UniversityProfileEntity? = db.universityDao().getUniversityProfileOnce()
    suspend fun updateUniversityProfile(profile: UniversityProfileEntity) = db.universityDao().insertOrUpdateUniversityProfile(profile)

    // University Courses & Tasks
    val courses: Flow<List<CourseEntity>> = db.universityDao().getAllCourses()
    suspend fun getCourseById(id: Long): CourseEntity? = db.universityDao().getCourseById(id)
    val tasks: Flow<List<AcademicTaskEntity>> = db.universityDao().getAllTasks()
    suspend fun insertCourse(course: CourseEntity) = db.universityDao().insertCourse(course)
    suspend fun updateCourse(course: CourseEntity) = db.universityDao().updateCourse(course)
    suspend fun deleteCourse(course: CourseEntity) = db.universityDao().deleteCourse(course)
    suspend fun updateCourseAttendance(id: Long, attended: Int, absent: Int, excused: Int) =
        db.universityDao().updateCourseAttendance(id, attended, absent, excused)

    suspend fun insertTask(task: AcademicTaskEntity) = db.universityDao().insertTask(task)
    suspend fun insertAllTasks(tasks: List<AcademicTaskEntity>) = db.universityDao().insertAllTasks(tasks)
    suspend fun updateTask(task: AcademicTaskEntity) = db.universityDao().updateTask(task)
    suspend fun updateTaskStatus(id: Long, status: String, completed: Boolean) =
        db.universityDao().updateTaskStatus(id, status, completed)
    suspend fun toggleTaskCompleted(id: Long, isCompleted: Boolean) = db.universityDao().setTaskCompleted(id, isCompleted)
    suspend fun deleteTask(task: AcademicTaskEntity) = db.universityDao().deleteTask(task)

    // Timetable Classes
    val timetableClasses: Flow<List<TimetableClassEntity>> = db.universityDao().getAllTimetableClasses()
    suspend fun insertTimetableClass(item: TimetableClassEntity) = db.universityDao().insertTimetableClass(item)
    suspend fun insertTimetableClasses(items: List<TimetableClassEntity>) = db.universityDao().insertTimetableClasses(items)
    suspend fun updateTimetableClass(item: TimetableClassEntity) = db.universityDao().updateTimetableClass(item)
    suspend fun deleteTimetableClass(item: TimetableClassEntity) = db.universityDao().deleteTimetableClass(item)
    suspend fun clearTimetable() = db.universityDao().clearTimetable()

    // Study Plan Sessions
    val studySessions: Flow<List<StudyPlanSessionEntity>> = db.universityDao().getAllStudySessions()
    suspend fun insertStudySession(session: StudyPlanSessionEntity) = db.universityDao().insertStudySession(session)
    suspend fun insertStudySessions(sessions: List<StudyPlanSessionEntity>) = db.universityDao().insertStudySessions(sessions)
    suspend fun updateStudySession(session: StudyPlanSessionEntity) = db.universityDao().updateStudySession(session)
    suspend fun toggleStudySessionCompleted(id: Long, completed: Boolean) = db.universityDao().toggleStudySessionCompleted(id, completed)
    suspend fun deleteStudySession(session: StudyPlanSessionEntity) = db.universityDao().deleteStudySession(session)
    suspend fun clearStudySessions() = db.universityDao().clearStudySessions()

    // Exam Plans
    val examPlans: Flow<List<ExamPlanEntity>> = db.universityDao().getAllExamPlans()
    suspend fun insertExamPlan(plan: ExamPlanEntity) = db.universityDao().insertExamPlan(plan)
    suspend fun updateExamPlan(plan: ExamPlanEntity) = db.universityDao().updateExamPlan(plan)
    suspend fun deleteExamPlan(plan: ExamPlanEntity) = db.universityDao().deleteExamPlan(plan)

    // Presentation Plans
    val presentationPlans: Flow<List<PresentationPlanEntity>> = db.universityDao().getAllPresentationPlans()
    suspend fun insertPresentationPlan(plan: PresentationPlanEntity) = db.universityDao().insertPresentationPlan(plan)
    suspend fun updatePresentationPlan(plan: PresentationPlanEntity) = db.universityDao().updatePresentationPlan(plan)
    suspend fun deletePresentationPlan(plan: PresentationPlanEntity) = db.universityDao().deletePresentationPlan(plan)

    // Research
    val researchProjects: Flow<List<ResearchProjectEntity>> = db.researchDao().getAllProjects()
    val activeProjects: Flow<List<ResearchProjectEntity>> = db.researchDao().getActiveProjects()
    suspend fun getProjectById(id: Long): ResearchProjectEntity? = db.researchDao().getProjectById(id)
    suspend fun insertResearchProject(project: ResearchProjectEntity) = db.researchDao().insertProject(project)
    suspend fun updateResearchProject(project: ResearchProjectEntity) = db.researchDao().updateProject(project)
    suspend fun setProjectActive(id: Long, isActive: Boolean) = db.researchDao().setProjectActive(id, isActive)
    suspend fun deleteResearchProject(project: ResearchProjectEntity) = db.researchDao().deleteProject(project)

    // Research Milestones
    fun getMilestonesForProject(projectId: Long): Flow<List<ResearchMilestoneEntity>> = db.researchDao().getMilestonesForProject(projectId)
    val allMilestones: Flow<List<ResearchMilestoneEntity>> = db.researchDao().getAllMilestones()
    suspend fun insertMilestone(milestone: ResearchMilestoneEntity) = db.researchDao().insertMilestone(milestone)
    suspend fun insertMilestones(milestones: List<ResearchMilestoneEntity>) = db.researchDao().insertMilestones(milestones)
    suspend fun updateMilestone(milestone: ResearchMilestoneEntity) = db.researchDao().updateMilestone(milestone)
    suspend fun toggleMilestone(id: Long, completed: Boolean, status: String) = db.researchDao().toggleMilestone(id, completed, status)
    suspend fun deleteMilestone(milestone: ResearchMilestoneEntity) = db.researchDao().deleteMilestone(milestone)

    // Research Papers & Literature Matrix
    fun getPapersForProject(projectId: Long): Flow<List<ResearchPaperEntity>> = db.researchDao().getPapersForProject(projectId)
    val allPapers: Flow<List<ResearchPaperEntity>> = db.researchDao().getAllPapers()
    suspend fun getPaperById(id: Long): ResearchPaperEntity? = db.researchDao().getPaperById(id)
    suspend fun insertPaper(paper: ResearchPaperEntity) = db.researchDao().insertPaper(paper)
    suspend fun insertPapers(papers: List<ResearchPaperEntity>) = db.researchDao().insertPapers(papers)
    suspend fun updatePaper(paper: ResearchPaperEntity) = db.researchDao().updatePaper(paper)
    suspend fun deletePaper(paper: ResearchPaperEntity) = db.researchDao().deletePaper(paper)

    // Research Experiments
    fun getExperimentsForProject(projectId: Long): Flow<List<ResearchExperimentEntity>> = db.researchDao().getExperimentsForProject(projectId)
    val allExperiments: Flow<List<ResearchExperimentEntity>> = db.researchDao().getAllExperiments()
    suspend fun getExperimentById(id: Long): ResearchExperimentEntity? = db.researchDao().getExperimentById(id)
    suspend fun insertExperiment(experiment: ResearchExperimentEntity) = db.researchDao().insertExperiment(experiment)
    suspend fun insertExperiments(experiments: List<ResearchExperimentEntity>) = db.researchDao().insertExperiments(experiments)
    suspend fun updateExperiment(experiment: ResearchExperimentEntity) = db.researchDao().updateExperiment(experiment)
    suspend fun deleteExperiment(experiment: ResearchExperimentEntity) = db.researchDao().deleteExperiment(experiment)

    // Manuscript Sections
    fun getManuscriptSections(projectId: Long): Flow<List<ManuscriptSectionEntity>> = db.researchDao().getManuscriptSections(projectId)
    suspend fun insertManuscriptSection(section: ManuscriptSectionEntity) = db.researchDao().insertManuscriptSection(section)
    suspend fun insertManuscriptSections(sections: List<ManuscriptSectionEntity>) = db.researchDao().insertManuscriptSections(sections)
    suspend fun updateManuscriptSection(section: ManuscriptSectionEntity) = db.researchDao().updateManuscriptSection(section)

    // Reproducibility Checklist
    fun getReproducibilityItems(projectId: Long): Flow<List<ReproducibilityItemEntity>> = db.researchDao().getReproducibilityItems(projectId)
    suspend fun insertReproducibilityItem(item: ReproducibilityItemEntity) = db.researchDao().insertReproducibilityItem(item)
    suspend fun insertReproducibilityItems(items: List<ReproducibilityItemEntity>) = db.researchDao().insertReproducibilityItems(items)
    suspend fun updateReproducibilityItem(item: ReproducibilityItemEntity) = db.researchDao().updateReproducibilityItem(item)
    suspend fun toggleReproducibilityItem(id: Long, completed: Boolean) = db.researchDao().toggleReproducibilityItem(id, completed)

    // Skills
    val skills: Flow<List<SkillItemEntity>> = db.skillDao().getAllSkills()
    fun getSkillsByCategory(category: String): Flow<List<SkillItemEntity>> = db.skillDao().getSkillsByCategory(category)
    suspend fun toggleSkill(id: Long, completed: Boolean) = db.skillDao().toggleSkill(id, completed)
    suspend fun insertSkill(skill: SkillItemEntity) = db.skillDao().insertSkill(skill)
    suspend fun updateSkill(skill: SkillItemEntity) = db.skillDao().updateSkill(skill)

    // Generated Projects
    val generatedProjects: Flow<List<GeneratedProjectEntity>> = db.projectGeneratorDao().getAllGeneratedProjects()
    suspend fun saveGeneratedProject(project: GeneratedProjectEntity) = db.projectGeneratorDao().insertGeneratedProject(project)
    suspend fun deleteGeneratedProject(project: GeneratedProjectEntity) = db.projectGeneratorDao().deleteGeneratedProject(project)

    // Chinese Coach & Language System
    val chinesePhrases: Flow<List<ChinesePhraseEntity>> = db.chineseDao().getAllPhrases()
    suspend fun toggleChineseMastered(id: Long, mastered: Boolean) = db.chineseDao().toggleMastered(id, mastered)

    fun getChineseProfile(userEmail: String): Flow<ChineseLanguageProfileEntity?> = db.chineseDao().getProfile(userEmail)
    suspend fun getChineseProfileSync(userEmail: String): ChineseLanguageProfileEntity? = db.chineseDao().getProfileSync(userEmail)
    suspend fun insertOrUpdateChineseProfile(profile: ChineseLanguageProfileEntity) = db.chineseDao().insertOrUpdateProfile(profile)
    suspend fun updateChineseNotifications(userEmail: String, enabled: Boolean) = db.chineseDao().updateNotificationsEnabled(userEmail, enabled)

    fun getAllChineseVocabulary(userEmail: String): Flow<List<ChineseVocabularyEntity>> = db.chineseDao().getAllVocabulary(userEmail)
    fun getChineseVocabularyDue(userEmail: String): Flow<List<ChineseVocabularyEntity>> = db.chineseDao().getVocabularyDue(userEmail)
    fun getChineseVocabularyByCategory(userEmail: String, category: String): Flow<List<ChineseVocabularyEntity>> = db.chineseDao().getVocabularyByCategory(userEmail, category)
    suspend fun insertChineseVocabulary(item: ChineseVocabularyEntity) = db.chineseDao().insertVocabulary(item)
    suspend fun insertAllChineseVocabulary(items: List<ChineseVocabularyEntity>) = db.chineseDao().insertAllVocabulary(items)
    suspend fun updateChineseVocabulary(item: ChineseVocabularyEntity) = db.chineseDao().updateVocabulary(item)
    suspend fun deleteChineseVocabulary(item: ChineseVocabularyEntity) = db.chineseDao().deleteVocabulary(item)
    suspend fun toggleChineseVocabFavorite(id: Long, favorite: Boolean) = db.chineseDao().toggleFavorite(id, favorite)
    suspend fun toggleChineseVocabKnown(id: Long, known: Boolean) = db.chineseDao().toggleKnown(id, known)
    suspend fun toggleChineseVocabDifficult(id: Long, difficult: Boolean) = db.chineseDao().toggleDifficult(id, difficult)
    suspend fun updateSpacedRepetition(id: Long, reviewStatus: String, lastReviewed: Long, nextReviewDate: String, count: Int, correct: Int, incorrect: Int, interval: Int, ease: Float) =
        db.chineseDao().updateSpacedRepetition(id, reviewStatus, lastReviewed, nextReviewDate, count, correct, incorrect, interval, ease)

    fun getChineseStudySessions(userEmail: String): Flow<List<ChineseStudySessionEntity>> = db.chineseDao().getStudySessions(userEmail)
    suspend fun insertChineseStudySession(session: ChineseStudySessionEntity) = db.chineseDao().insertStudySession(session)
    suspend fun insertAllChineseStudySessions(sessions: List<ChineseStudySessionEntity>) = db.chineseDao().insertAllStudySessions(sessions)
    suspend fun deleteChineseStudySession(session: ChineseStudySessionEntity) = db.chineseDao().deleteStudySession(session)
    suspend fun clearChineseStudySessions(userEmail: String) = db.chineseDao().clearStudySessions(userEmail)
    suspend fun toggleChineseStudySessionCompleted(id: Long, completed: Boolean) = db.chineseDao().toggleStudySessionCompleted(id, completed)

    fun getChineseConversationMessages(userEmail: String, scenarioId: String): Flow<List<ChineseConversationMessageEntity>> = db.chineseDao().getConversationMessages(userEmail, scenarioId)
    suspend fun insertChineseConversationMessage(msg: ChineseConversationMessageEntity) = db.chineseDao().insertConversationMessage(msg)
    suspend fun clearChineseConversationMessages(userEmail: String, scenarioId: String) = db.chineseDao().clearConversationMessages(userEmail, scenarioId)

    val chineseListeningExercises: Flow<List<ChineseListeningExerciseEntity>> = db.chineseDao().getAllListeningExercises()
    suspend fun insertAllChineseListeningExercises(exercises: List<ChineseListeningExerciseEntity>) = db.chineseDao().insertAllListeningExercises(exercises)
    suspend fun answerChineseListeningExercise(id: Long, selectedIndex: Int, isCorrect: Boolean) = db.chineseDao().answerListeningExercise(id, selectedIndex, isCorrect)

    val chineseGrammarPoints: Flow<List<ChineseGrammarPointEntity>> = db.chineseDao().getAllGrammarPoints()
    fun getChineseGrammarPointsByLevel(hskLevel: String): Flow<List<ChineseGrammarPointEntity>> = db.chineseDao().getGrammarPointsByLevel(hskLevel)
    suspend fun insertAllChineseGrammarPoints(points: List<ChineseGrammarPointEntity>) = db.chineseDao().insertAllGrammarPoints(points)

    fun getChineseWeeklyProgress(userEmail: String): Flow<ChineseWeeklyProgressEntity?> = db.chineseDao().getWeeklyProgress(userEmail)
    suspend fun insertOrUpdateChineseWeeklyProgress(progress: ChineseWeeklyProgressEntity) = db.chineseDao().insertOrUpdateWeeklyProgress(progress)

    // Knowledge Base
    val knowledgeItems: Flow<List<KnowledgeItemEntity>> = db.knowledgeDao().getAllKnowledge()
    fun searchKnowledge(query: String): Flow<List<KnowledgeItemEntity>> = db.knowledgeDao().searchKnowledge(query)
    suspend fun insertKnowledge(item: KnowledgeItemEntity) = db.knowledgeDao().insertItem(item)
    suspend fun deleteKnowledge(item: KnowledgeItemEntity) = db.knowledgeDao().deleteItem(item)

    // Roadmap Goals
    val roadmapGoals: Flow<List<RoadmapGoalEntity>> = db.roadmapDao().getAllGoals()
    suspend fun insertGoal(goal: RoadmapGoalEntity) = db.roadmapDao().insertGoal(goal)
    suspend fun toggleGoal(id: Long, completed: Boolean) = db.roadmapDao().toggleGoal(id, completed)
    suspend fun deleteGoal(goal: RoadmapGoalEntity) = db.roadmapDao().deleteGoal(goal)

    // Chat
    fun getMessagesForMode(modeId: String): Flow<List<ChatMessageEntity>> = db.chatDao().getMessagesForMode(modeId)
    suspend fun insertChatMessage(message: ChatMessageEntity) = db.chatDao().insertMessage(message)
    suspend fun clearChat(modeId: String) = db.chatDao().clearHistory(modeId)

    // Personal Document Library
    fun getDocumentsForUser(email: String): Flow<List<PersonalDocumentEntity>> = db.personalDocumentDao().getDocumentsForUser(email)
    fun searchDocuments(email: String, query: String): Flow<List<PersonalDocumentEntity>> = db.personalDocumentDao().searchDocuments(email, query)
    fun getDocumentsByCategory(email: String, category: String): Flow<List<PersonalDocumentEntity>> = db.personalDocumentDao().getDocumentsByCategory(email, category)
    suspend fun insertDocument(doc: PersonalDocumentEntity) = db.personalDocumentDao().insertDocument(doc)
    suspend fun updateDocument(doc: PersonalDocumentEntity) = db.personalDocumentDao().updateDocument(doc)
    suspend fun deleteDocument(doc: PersonalDocumentEntity) = db.personalDocumentDao().deleteDocument(doc)

    // Career Profile
    val careerProfile: Flow<CareerProfileEntity?> = db.careerDao().getCareerProfile()
    suspend fun getCareerProfileOnce(): CareerProfileEntity? = db.careerDao().getCareerProfileOnce()
    suspend fun insertCareerProfile(profile: CareerProfileEntity) = db.careerDao().insertCareerProfile(profile)
    suspend fun updateCareerProfile(profile: CareerProfileEntity) = db.careerDao().updateCareerProfile(profile)

    // Career Goals
    val careerGoals: Flow<List<CareerGoalEntity>> = db.careerDao().getAllCareerGoals()
    suspend fun insertCareerGoal(goal: CareerGoalEntity) = db.careerDao().insertCareerGoal(goal)
    suspend fun insertCareerGoals(goals: List<CareerGoalEntity>) = db.careerDao().insertCareerGoals(goals)
    suspend fun updateCareerGoal(goal: CareerGoalEntity) = db.careerDao().updateCareerGoal(goal)
    suspend fun deleteCareerGoal(goal: CareerGoalEntity) = db.careerDao().deleteCareerGoal(goal)

    // Skill Inventory
    val skillInventory: Flow<List<SkillInventoryEntity>> = db.careerDao().getAllSkills()
    suspend fun insertSkillInventory(skill: SkillInventoryEntity) = db.careerDao().insertSkill(skill)
    suspend fun insertSkillInventories(skills: List<SkillInventoryEntity>) = db.careerDao().insertSkills(skills)
    suspend fun updateSkillInventory(skill: SkillInventoryEntity) = db.careerDao().updateSkill(skill)
    suspend fun deleteSkillInventory(skill: SkillInventoryEntity) = db.careerDao().deleteSkill(skill)

    // Job Postings
    val jobPostings: Flow<List<JobPostingEntity>> = db.careerDao().getAllJobPostings()
    val savedJobPostings: Flow<List<JobPostingEntity>> = db.careerDao().getSavedJobPostings()
    suspend fun insertJobPosting(job: JobPostingEntity) = db.careerDao().insertJobPosting(job)
    suspend fun insertJobPostings(jobs: List<JobPostingEntity>) = db.careerDao().insertJobPostings(jobs)
    suspend fun updateJobPosting(job: JobPostingEntity) = db.careerDao().updateJobPosting(job)
    suspend fun deleteJobPosting(job: JobPostingEntity) = db.careerDao().deleteJobPosting(job)
    suspend fun toggleSaveJob(id: Long, isSaved: Boolean) = db.careerDao().toggleSaveJob(id, isSaved)

    // Skill Development Plans
    val skillDevelopmentPlans: Flow<List<SkillDevelopmentPlanEntity>> = db.careerDao().getAllSkillPlans()
    suspend fun insertSkillPlan(plan: SkillDevelopmentPlanEntity) = db.careerDao().insertSkillPlan(plan)
    suspend fun insertSkillPlans(plans: List<SkillDevelopmentPlanEntity>) = db.careerDao().insertSkillPlans(plans)
    suspend fun updateSkillPlan(plan: SkillDevelopmentPlanEntity) = db.careerDao().updateSkillPlan(plan)
    suspend fun updateSkillPlanStatus(id: Long, status: String) = db.careerDao().updateSkillPlanStatus(id, status)
    suspend fun deleteSkillPlan(plan: SkillDevelopmentPlanEntity) = db.careerDao().deleteSkillPlan(plan)

    // Job Applications
    val jobApplications: Flow<List<JobApplicationEntity>> = db.careerDao().getAllApplications()
    suspend fun insertJobApplication(app: JobApplicationEntity) = db.careerDao().insertApplication(app)
    suspend fun insertJobApplications(apps: List<JobApplicationEntity>) = db.careerDao().insertApplications(apps)
    suspend fun updateJobApplication(app: JobApplicationEntity) = db.careerDao().updateApplication(app)
    suspend fun deleteJobApplication(app: JobApplicationEntity) = db.careerDao().deleteApplication(app)

    // Interview Prep Questions
    val interviewQuestions: Flow<List<InterviewPrepQuestionEntity>> = db.careerDao().getAllInterviewQuestions()
    suspend fun insertInterviewQuestion(q: InterviewPrepQuestionEntity) = db.careerDao().insertInterviewQuestion(q)
    suspend fun insertInterviewQuestions(qs: List<InterviewPrepQuestionEntity>) = db.careerDao().insertInterviewQuestions(qs)
    suspend fun updateInterviewQuestion(q: InterviewPrepQuestionEntity) = db.careerDao().updateInterviewQuestion(q)
    suspend fun deleteInterviewQuestion(q: InterviewPrepQuestionEntity) = db.careerDao().deleteInterviewQuestion(q)

    // Career Roadmap Milestones
    val careerRoadmapMilestones: Flow<List<CareerRoadmapMilestoneEntity>> = db.careerDao().getAllRoadmapMilestones()
    suspend fun insertRoadmapMilestone(m: CareerRoadmapMilestoneEntity) = db.careerDao().insertRoadmapMilestone(m)
    suspend fun insertRoadmapMilestones(ms: List<CareerRoadmapMilestoneEntity>) = db.careerDao().insertRoadmapMilestones(ms)
    suspend fun updateRoadmapMilestone(m: CareerRoadmapMilestoneEntity) = db.careerDao().updateRoadmapMilestone(m)
    suspend fun toggleRoadmapMilestone(id: Long, completed: Boolean) = db.careerDao().toggleRoadmapMilestone(id, completed)
    suspend fun deleteRoadmapMilestone(m: CareerRoadmapMilestoneEntity) = db.careerDao().deleteRoadmapMilestone(m)

    // ========================================================
    // STAGE 6: IMMIGRATION, WORK & VISA REPOSITORY
    // ========================================================

    fun getImmigrationProfile(userEmail: String): Flow<ImmigrationProfileEntity?> = db.immigrationDao().getImmigrationProfile(userEmail)
    suspend fun getImmigrationProfileSync(userEmail: String): ImmigrationProfileEntity? = db.immigrationDao().getImmigrationProfileSync(userEmail)
    suspend fun insertOrUpdateImmigrationProfile(profile: ImmigrationProfileEntity) = db.immigrationDao().insertOrUpdateProfile(profile)

    val officialSources: Flow<List<OfficialSourceEntity>> = db.immigrationDao().getAllOfficialSources()
    fun getSourcesByJurisdiction(jurisdiction: String): Flow<List<OfficialSourceEntity>> = db.immigrationDao().getSourcesByJurisdiction(jurisdiction)
    suspend fun insertOfficialSources(sources: List<OfficialSourceEntity>) = db.immigrationDao().insertOfficialSources(sources)

    val cityImmigrationPolicies: Flow<List<CityImmigrationPolicyEntity>> = db.immigrationDao().getAllCityPolicies()
    fun getPoliciesForCity(city: String): Flow<List<CityImmigrationPolicyEntity>> = db.immigrationDao().getPoliciesForCity(city)
    suspend fun insertCityPolicy(policy: CityImmigrationPolicyEntity) = db.immigrationDao().insertCityPolicy(policy)
    suspend fun insertCityPolicies(policies: List<CityImmigrationPolicyEntity>) = db.immigrationDao().insertCityPolicies(policies)

    fun getGraduationChecklist(userEmail: String): Flow<List<GraduationChecklistItemEntity>> = db.immigrationDao().getGraduationChecklist(userEmail)
    suspend fun insertGraduationChecklistItem(item: GraduationChecklistItemEntity) = db.immigrationDao().insertChecklistItem(item)
    suspend fun insertGraduationChecklistItems(items: List<GraduationChecklistItemEntity>) = db.immigrationDao().insertChecklistItems(items)
    suspend fun toggleGraduationChecklistItem(id: Long, completed: Boolean) = db.immigrationDao().toggleChecklistItem(id, completed)
    suspend fun deleteGraduationChecklistItem(item: GraduationChecklistItemEntity) = db.immigrationDao().deleteChecklistItem(item)

    fun getImmigrationDocuments(userEmail: String): Flow<List<ImmigrationDocumentEntity>> = db.immigrationDao().getImmigrationDocuments(userEmail)
    suspend fun insertImmigrationDocument(doc: ImmigrationDocumentEntity) = db.immigrationDao().insertDocument(doc)
    suspend fun insertImmigrationDocuments(docs: List<ImmigrationDocumentEntity>) = db.immigrationDao().insertDocuments(docs)
    suspend fun updateImmigrationDocumentStatus(id: Long, status: String) = db.immigrationDao().updateDocumentStatus(id, status)
    suspend fun deleteImmigrationDocument(doc: ImmigrationDocumentEntity) = db.immigrationDao().deleteDocument(doc)

    val officialContacts: Flow<List<OfficialContactEntity>> = db.immigrationDao().getAllOfficialContacts()
    suspend fun insertOfficialContacts(contacts: List<OfficialContactEntity>) = db.immigrationDao().insertOfficialContacts(contacts)

    fun getActiveImmigrationReminders(userEmail: String): Flow<List<ImmigrationReminderEntity>> = db.immigrationDao().getActiveReminders(userEmail)
    suspend fun insertImmigrationReminder(reminder: ImmigrationReminderEntity) = db.immigrationDao().insertReminder(reminder)
    suspend fun insertImmigrationReminders(reminders: List<ImmigrationReminderEntity>) = db.immigrationDao().insertReminders(reminders)
    suspend fun dismissImmigrationReminder(id: Long) = db.immigrationDao().dismissReminder(id)
    suspend fun deleteImmigrationReminder(reminder: ImmigrationReminderEntity) = db.immigrationDao().deleteReminder(reminder)
}
