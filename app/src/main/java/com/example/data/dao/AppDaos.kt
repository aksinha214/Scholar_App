package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}

@Dao
interface UniversityDao {
    // University Profile
    @Query("SELECT * FROM university_profile WHERE id = 1 LIMIT 1")
    fun getUniversityProfile(): Flow<UniversityProfileEntity?>

    @Query("SELECT * FROM university_profile WHERE id = 1 LIMIT 1")
    suspend fun getUniversityProfileOnce(): UniversityProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUniversityProfile(profile: UniversityProfileEntity)

    // Courses & Attendance
    @Query("SELECT * FROM courses ORDER BY code ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: Long): CourseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity): Long

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("UPDATE courses SET attendedClasses = :attended, absentClasses = :absent, excusedClasses = :excused WHERE id = :id")
    suspend fun updateCourseAttendance(id: Long, attended: Int, absent: Int, excused: Int)

    // Academic Tasks & Assignments
    @Query("SELECT * FROM academic_tasks ORDER BY isCompleted ASC, deadline ASC")
    fun getAllTasks(): Flow<List<AcademicTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: AcademicTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<AcademicTaskEntity>)

    @Update
    suspend fun updateTask(task: AcademicTaskEntity)

    @Query("UPDATE academic_tasks SET isCompleted = :completed, status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: String, completed: Boolean)

    @Query("UPDATE academic_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean)

    @Delete
    suspend fun deleteTask(task: AcademicTaskEntity)

    // Timetable Classes
    @Query("SELECT * FROM timetable_classes ORDER BY CASE dayOfWeek WHEN 'Monday' THEN 1 WHEN 'Tuesday' THEN 2 WHEN 'Wednesday' THEN 3 WHEN 'Thursday' THEN 4 WHEN 'Friday' THEN 5 WHEN 'Saturday' THEN 6 WHEN 'Sunday' THEN 7 ELSE 8 END, startTime ASC")
    fun getAllTimetableClasses(): Flow<List<TimetableClassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableClass(item: TimetableClassEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetableClasses(items: List<TimetableClassEntity>)

    @Update
    suspend fun updateTimetableClass(item: TimetableClassEntity)

    @Delete
    suspend fun deleteTimetableClass(item: TimetableClassEntity)

    @Query("DELETE FROM timetable_classes")
    suspend fun clearTimetable()

    // Study Plan Sessions
    @Query("SELECT * FROM study_plan_sessions ORDER BY CASE dayOfWeek WHEN 'Monday' THEN 1 WHEN 'Tuesday' THEN 2 WHEN 'Wednesday' THEN 3 WHEN 'Thursday' THEN 4 WHEN 'Friday' THEN 5 WHEN 'Saturday' THEN 6 WHEN 'Sunday' THEN 7 ELSE 8 END, startTime ASC")
    fun getAllStudySessions(): Flow<List<StudyPlanSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudyPlanSessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySessions(sessions: List<StudyPlanSessionEntity>)

    @Update
    suspend fun updateStudySession(session: StudyPlanSessionEntity)

    @Query("UPDATE study_plan_sessions SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleStudySessionCompleted(id: Long, completed: Boolean)

    @Delete
    suspend fun deleteStudySession(session: StudyPlanSessionEntity)

    @Query("DELETE FROM study_plan_sessions")
    suspend fun clearStudySessions()

    // Exam Plans
    @Query("SELECT * FROM exam_plans ORDER BY examDate ASC")
    fun getAllExamPlans(): Flow<List<ExamPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamPlan(plan: ExamPlanEntity): Long

    @Update
    suspend fun updateExamPlan(plan: ExamPlanEntity)

    @Delete
    suspend fun deleteExamPlan(plan: ExamPlanEntity)

    // Presentation Plans
    @Query("SELECT * FROM presentation_plans ORDER BY targetDate ASC")
    fun getAllPresentationPlans(): Flow<List<PresentationPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresentationPlan(plan: PresentationPlanEntity): Long

    @Update
    suspend fun updatePresentationPlan(plan: PresentationPlanEntity)

    @Delete
    suspend fun deletePresentationPlan(plan: PresentationPlanEntity)
}

@Dao
interface ResearchDao {
    // Projects
    @Query("SELECT * FROM research_projects ORDER BY lastUpdated DESC")
    fun getAllProjects(): Flow<List<ResearchProjectEntity>>

    @Query("SELECT * FROM research_projects WHERE isActive = 1 ORDER BY lastUpdated DESC")
    fun getActiveProjects(): Flow<List<ResearchProjectEntity>>

    @Query("SELECT * FROM research_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Long): ResearchProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ResearchProjectEntity): Long

    @Update
    suspend fun updateProject(project: ResearchProjectEntity)

    @Query("UPDATE research_projects SET isActive = :isActive WHERE id = :id")
    suspend fun setProjectActive(id: Long, isActive: Boolean)

    @Delete
    suspend fun deleteProject(project: ResearchProjectEntity)

    // Milestones
    @Query("SELECT * FROM research_milestones WHERE projectId = :projectId ORDER BY orderIndex ASC, id ASC")
    fun getMilestonesForProject(projectId: Long): Flow<List<ResearchMilestoneEntity>>

    @Query("SELECT * FROM research_milestones ORDER BY deadline ASC")
    fun getAllMilestones(): Flow<List<ResearchMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: ResearchMilestoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<ResearchMilestoneEntity>)

    @Update
    suspend fun updateMilestone(milestone: ResearchMilestoneEntity)

    @Query("UPDATE research_milestones SET isCompleted = :completed, status = :status WHERE id = :id")
    suspend fun toggleMilestone(id: Long, completed: Boolean, status: String)

    @Delete
    suspend fun deleteMilestone(milestone: ResearchMilestoneEntity)

    // Papers & Literature Matrix
    @Query("SELECT * FROM research_papers WHERE projectId = :projectId ORDER BY year DESC, id DESC")
    fun getPapersForProject(projectId: Long): Flow<List<ResearchPaperEntity>>

    @Query("SELECT * FROM research_papers ORDER BY year DESC, id DESC")
    fun getAllPapers(): Flow<List<ResearchPaperEntity>>

    @Query("SELECT * FROM research_papers WHERE id = :id LIMIT 1")
    suspend fun getPaperById(id: Long): ResearchPaperEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaper(paper: ResearchPaperEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPapers(papers: List<ResearchPaperEntity>)

    @Update
    suspend fun updatePaper(paper: ResearchPaperEntity)

    @Delete
    suspend fun deletePaper(paper: ResearchPaperEntity)

    // Experiments
    @Query("SELECT * FROM research_experiments WHERE projectId = :projectId ORDER BY date DESC, id DESC")
    fun getExperimentsForProject(projectId: Long): Flow<List<ResearchExperimentEntity>>

    @Query("SELECT * FROM research_experiments ORDER BY date DESC, id DESC")
    fun getAllExperiments(): Flow<List<ResearchExperimentEntity>>

    @Query("SELECT * FROM research_experiments WHERE id = :id LIMIT 1")
    suspend fun getExperimentById(id: Long): ResearchExperimentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiment(experiment: ResearchExperimentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExperiments(experiments: List<ResearchExperimentEntity>)

    @Update
    suspend fun updateExperiment(experiment: ResearchExperimentEntity)

    @Delete
    suspend fun deleteExperiment(experiment: ResearchExperimentEntity)

    // Manuscript Sections
    @Query("SELECT * FROM manuscript_sections WHERE projectId = :projectId ORDER BY id ASC")
    fun getManuscriptSections(projectId: Long): Flow<List<ManuscriptSectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManuscriptSection(section: ManuscriptSectionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManuscriptSections(sections: List<ManuscriptSectionEntity>)

    @Update
    suspend fun updateManuscriptSection(section: ManuscriptSectionEntity)

    // Reproducibility Checklist
    @Query("SELECT * FROM reproducibility_items WHERE projectId = :projectId ORDER BY id ASC")
    fun getReproducibilityItems(projectId: Long): Flow<List<ReproducibilityItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReproducibilityItem(item: ReproducibilityItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReproducibilityItems(items: List<ReproducibilityItemEntity>)

    @Update
    suspend fun updateReproducibilityItem(item: ReproducibilityItemEntity)

    @Query("UPDATE reproducibility_items SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleReproducibilityItem(id: Long, completed: Boolean)
}

@Dao
interface SkillDao {
    @Query("SELECT * FROM cs_skills ORDER BY category ASC, id ASC")
    fun getAllSkills(): Flow<List<SkillItemEntity>>

    @Query("SELECT * FROM cs_skills WHERE category = :category ORDER BY id ASC")
    fun getSkillsByCategory(category: String): Flow<List<SkillItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: SkillItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(skills: List<SkillItemEntity>)

    @Update
    suspend fun updateSkill(skill: SkillItemEntity)

    @Delete
    suspend fun deleteSkill(skill: SkillItemEntity)

    @Query("UPDATE cs_skills SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleSkill(id: Long, completed: Boolean)
}

@Dao
interface ProjectGeneratorDao {
    @Query("SELECT * FROM generated_projects ORDER BY createdAt DESC")
    fun getAllGeneratedProjects(): Flow<List<GeneratedProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedProject(project: GeneratedProjectEntity): Long

    @Delete
    suspend fun deleteGeneratedProject(project: GeneratedProjectEntity)
}

@Dao
interface ChineseDao {
    // Legacy / Seed Phrases
    @Query("SELECT * FROM chinese_phrases ORDER BY id ASC")
    fun getAllPhrases(): Flow<List<ChinesePhraseEntity>>

    @Query("SELECT * FROM chinese_phrases WHERE category = :category")
    fun getPhrasesByCategory(category: String): Flow<List<ChinesePhraseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phrases: List<ChinesePhraseEntity>)

    @Query("UPDATE chinese_phrases SET isMastered = :mastered WHERE id = :id")
    suspend fun toggleMastered(id: Long, mastered: Boolean)

    // Chinese Language Profile
    @Query("SELECT * FROM chinese_language_profile WHERE userEmail = :userEmail LIMIT 1")
    fun getProfile(userEmail: String): Flow<ChineseLanguageProfileEntity?>

    @Query("SELECT * FROM chinese_language_profile WHERE userEmail = :userEmail LIMIT 1")
    suspend fun getProfileSync(userEmail: String): ChineseLanguageProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ChineseLanguageProfileEntity)

    @Query("UPDATE chinese_language_profile SET notificationsEnabled = :enabled WHERE userEmail = :userEmail")
    suspend fun updateNotificationsEnabled(userEmail: String, enabled: Boolean)

    // Chinese Vocabulary & Spaced Repetition
    @Query("SELECT * FROM chinese_vocabulary WHERE userEmail = :userEmail ORDER BY id DESC")
    fun getAllVocabulary(userEmail: String): Flow<List<ChineseVocabularyEntity>>

    @Query("SELECT * FROM chinese_vocabulary WHERE userEmail = :userEmail AND (reviewStatus = 'Due' OR reviewStatus = 'New') ORDER BY id ASC")
    fun getVocabularyDue(userEmail: String): Flow<List<ChineseVocabularyEntity>>

    @Query("SELECT * FROM chinese_vocabulary WHERE userEmail = :userEmail AND category = :category ORDER BY id DESC")
    fun getVocabularyByCategory(userEmail: String, category: String): Flow<List<ChineseVocabularyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(item: ChineseVocabularyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllVocabulary(items: List<ChineseVocabularyEntity>)

    @Update
    suspend fun updateVocabulary(item: ChineseVocabularyEntity)

    @Delete
    suspend fun deleteVocabulary(item: ChineseVocabularyEntity)

    @Query("UPDATE chinese_vocabulary SET isFavorite = :favorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long, favorite: Boolean)

    @Query("UPDATE chinese_vocabulary SET isKnown = :known WHERE id = :id")
    suspend fun toggleKnown(id: Long, known: Boolean)

    @Query("UPDATE chinese_vocabulary SET isDifficult = :difficult WHERE id = :id")
    suspend fun toggleDifficult(id: Long, difficult: Boolean)

    @Query("UPDATE chinese_vocabulary SET reviewStatus = :reviewStatus, lastReviewedTimestamp = :lastReviewed, nextReviewDate = :nextReviewDate, reviewCount = :count, correctAnswers = :correct, incorrectAnswers = :incorrect, intervalDays = :interval, easeFactor = :ease WHERE id = :id")
    suspend fun updateSpacedRepetition(
        id: Long,
        reviewStatus: String,
        lastReviewed: Long,
        nextReviewDate: String,
        count: Int,
        correct: Int,
        incorrect: Int,
        interval: Int,
        ease: Float
    )

    // Chinese Study Sessions
    @Query("SELECT * FROM chinese_study_sessions WHERE userEmail = :userEmail ORDER BY id ASC")
    fun getStudySessions(userEmail: String): Flow<List<ChineseStudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: ChineseStudySessionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudySessions(sessions: List<ChineseStudySessionEntity>)

    @Delete
    suspend fun deleteStudySession(session: ChineseStudySessionEntity)

    @Query("DELETE FROM chinese_study_sessions WHERE userEmail = :userEmail")
    suspend fun clearStudySessions(userEmail: String)

    @Query("UPDATE chinese_study_sessions SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun toggleStudySessionCompleted(id: Long, isCompleted: Boolean)

    // Chinese Conversation Messages
    @Query("SELECT * FROM chinese_conversation_messages WHERE userEmail = :userEmail AND scenarioId = :scenarioId ORDER BY timestamp ASC")
    fun getConversationMessages(userEmail: String, scenarioId: String): Flow<List<ChineseConversationMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversationMessage(msg: ChineseConversationMessageEntity): Long

    @Query("DELETE FROM chinese_conversation_messages WHERE userEmail = :userEmail AND scenarioId = :scenarioId")
    suspend fun clearConversationMessages(userEmail: String, scenarioId: String)

    // Listening Exercises
    @Query("SELECT * FROM chinese_listening_exercises ORDER BY id ASC")
    fun getAllListeningExercises(): Flow<List<ChineseListeningExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllListeningExercises(exercises: List<ChineseListeningExerciseEntity>)

    @Update
    suspend fun updateListeningExercise(exercise: ChineseListeningExerciseEntity)

    @Query("UPDATE chinese_listening_exercises SET userSelectedOptionIndex = :selectedIndex, isAnswered = 1, isCorrect = :isCorrect WHERE id = :id")
    suspend fun answerListeningExercise(id: Long, selectedIndex: Int, isCorrect: Boolean)

    // Grammar Points
    @Query("SELECT * FROM chinese_grammar_points ORDER BY id ASC")
    fun getAllGrammarPoints(): Flow<List<ChineseGrammarPointEntity>>

    @Query("SELECT * FROM chinese_grammar_points WHERE hskLevel = :hskLevel ORDER BY id ASC")
    fun getGrammarPointsByLevel(hskLevel: String): Flow<List<ChineseGrammarPointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGrammarPoints(points: List<ChineseGrammarPointEntity>)

    // Weekly Progress
    @Query("SELECT * FROM chinese_weekly_progress WHERE userEmail = :userEmail LIMIT 1")
    fun getWeeklyProgress(userEmail: String): Flow<ChineseWeeklyProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateWeeklyProgress(progress: ChineseWeeklyProgressEntity)
}

@Dao
interface KnowledgeDao {
    @Query("SELECT * FROM knowledge_items ORDER BY dateAdded DESC")
    fun getAllKnowledge(): Flow<List<KnowledgeItemEntity>>

    @Query("SELECT * FROM knowledge_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchKnowledge(query: String): Flow<List<KnowledgeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: KnowledgeItemEntity): Long

    @Delete
    suspend fun deleteItem(item: KnowledgeItemEntity)
}

@Dao
interface RoadmapDao {
    @Query("SELECT * FROM roadmap_goals ORDER BY isCompleted ASC, id ASC")
    fun getAllGoals(): Flow<List<RoadmapGoalEntity>>

    @Query("SELECT * FROM roadmap_goals WHERE timeframe = :timeframe ORDER BY isCompleted ASC, id ASC")
    fun getGoalsByTimeframe(timeframe: String): Flow<List<RoadmapGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: RoadmapGoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGoals(goals: List<RoadmapGoalEntity>)

    @Query("UPDATE roadmap_goals SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleGoal(id: Long, completed: Boolean)

    @Delete
    suspend fun deleteGoal(goal: RoadmapGoalEntity)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE modeId = :modeId ORDER BY timestamp ASC")
    fun getMessagesForMode(modeId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE modeId = :modeId")
    suspend fun clearHistory(modeId: String)
}

@Dao
interface PersonalDocumentDao {
    @Query("SELECT * FROM personal_documents WHERE userEmail = :userEmail ORDER BY timestamp DESC")
    fun getDocumentsForUser(userEmail: String): Flow<List<PersonalDocumentEntity>>

    @Query("SELECT * FROM personal_documents WHERE userEmail = :userEmail AND (fileName LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR userNotes LIKE '%' || :query || '%' OR courseOrProject LIKE '%' || :query || '%') ORDER BY timestamp DESC")
    fun searchDocuments(userEmail: String, query: String): Flow<List<PersonalDocumentEntity>>

    @Query("SELECT * FROM personal_documents WHERE userEmail = :userEmail AND category = :category ORDER BY timestamp DESC")
    fun getDocumentsByCategory(userEmail: String, category: String): Flow<List<PersonalDocumentEntity>>

    @Query("SELECT * FROM personal_documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: Long): PersonalDocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: PersonalDocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(docs: List<PersonalDocumentEntity>)

    @Update
    suspend fun updateDocument(doc: PersonalDocumentEntity)

    @Delete
    suspend fun deleteDocument(doc: PersonalDocumentEntity)
}

@Dao
interface CareerDao {
    // Career Profile
    @Query("SELECT * FROM career_profiles WHERE id = 1 LIMIT 1")
    fun getCareerProfile(): Flow<CareerProfileEntity?>

    @Query("SELECT * FROM career_profiles WHERE id = 1 LIMIT 1")
    suspend fun getCareerProfileOnce(): CareerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerProfile(profile: CareerProfileEntity): Long

    @Update
    suspend fun updateCareerProfile(profile: CareerProfileEntity)

    // Career Goals
    @Query("SELECT * FROM career_goals ORDER BY isPrimary DESC, id ASC")
    fun getAllCareerGoals(): Flow<List<CareerGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerGoal(goal: CareerGoalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareerGoals(goals: List<CareerGoalEntity>)

    @Update
    suspend fun updateCareerGoal(goal: CareerGoalEntity)

    @Delete
    suspend fun deleteCareerGoal(goal: CareerGoalEntity)

    // Skill Inventory
    @Query("SELECT * FROM skill_inventory ORDER BY category ASC, skillName ASC")
    fun getAllSkills(): Flow<List<SkillInventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: SkillInventoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkills(skills: List<SkillInventoryEntity>)

    @Update
    suspend fun updateSkill(skill: SkillInventoryEntity)

    @Delete
    suspend fun deleteSkill(skill: SkillInventoryEntity)

    // Job Postings
    @Query("SELECT * FROM job_postings ORDER BY id DESC")
    fun getAllJobPostings(): Flow<List<JobPostingEntity>>

    @Query("SELECT * FROM job_postings WHERE isSaved = 1 ORDER BY id DESC")
    fun getSavedJobPostings(): Flow<List<JobPostingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobPosting(job: JobPostingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobPostings(jobs: List<JobPostingEntity>)

    @Update
    suspend fun updateJobPosting(job: JobPostingEntity)

    @Delete
    suspend fun deleteJobPosting(job: JobPostingEntity)

    @Query("UPDATE job_postings SET isSaved = :isSaved WHERE id = :id")
    suspend fun toggleSaveJob(id: Long, isSaved: Boolean)

    // Skill Development Plans
    @Query("SELECT * FROM skill_development_plans ORDER BY id ASC")
    fun getAllSkillPlans(): Flow<List<SkillDevelopmentPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkillPlan(plan: SkillDevelopmentPlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkillPlans(plans: List<SkillDevelopmentPlanEntity>)

    @Update
    suspend fun updateSkillPlan(plan: SkillDevelopmentPlanEntity)

    @Query("UPDATE skill_development_plans SET status = :status WHERE id = :id")
    suspend fun updateSkillPlanStatus(id: Long, status: String)

    @Delete
    suspend fun deleteSkillPlan(plan: SkillDevelopmentPlanEntity)

    // Job Applications
    @Query("SELECT * FROM job_applications ORDER BY id DESC")
    fun getAllApplications(): Flow<List<JobApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplication(app: JobApplicationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplications(apps: List<JobApplicationEntity>)

    @Update
    suspend fun updateApplication(app: JobApplicationEntity)

    @Delete
    suspend fun deleteApplication(app: JobApplicationEntity)

    // Interview Prep Questions
    @Query("SELECT * FROM interview_questions ORDER BY id ASC")
    fun getAllInterviewQuestions(): Flow<List<InterviewPrepQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterviewQuestion(q: InterviewPrepQuestionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterviewQuestions(qs: List<InterviewPrepQuestionEntity>)

    @Update
    suspend fun updateInterviewQuestion(q: InterviewPrepQuestionEntity)

    @Delete
    suspend fun deleteInterviewQuestion(q: InterviewPrepQuestionEntity)

    // Career Roadmap Milestones
    @Query("SELECT * FROM career_roadmap_milestones ORDER BY orderIndex ASC, id ASC")
    fun getAllRoadmapMilestones(): Flow<List<CareerRoadmapMilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoadmapMilestone(m: CareerRoadmapMilestoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoadmapMilestones(ms: List<CareerRoadmapMilestoneEntity>)

    @Update
    suspend fun updateRoadmapMilestone(m: CareerRoadmapMilestoneEntity)

    @Query("UPDATE career_roadmap_milestones SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleRoadmapMilestone(id: Long, completed: Boolean)

    @Delete
    suspend fun deleteRoadmapMilestone(m: CareerRoadmapMilestoneEntity)
}

// ========================================================
// STAGE 6: IMMIGRATION, WORK & VISA ASSISTANT DAO
// ========================================================

@Dao
interface ImmigrationDao {
    // Profile
    @Query("SELECT * FROM immigration_profiles WHERE userEmail = :userEmail LIMIT 1")
    fun getImmigrationProfile(userEmail: String): Flow<ImmigrationProfileEntity?>

    @Query("SELECT * FROM immigration_profiles WHERE userEmail = :userEmail LIMIT 1")
    suspend fun getImmigrationProfileSync(userEmail: String): ImmigrationProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ImmigrationProfileEntity)

    // Official Sources
    @Query("SELECT * FROM official_sources ORDER BY id ASC")
    fun getAllOfficialSources(): Flow<List<OfficialSourceEntity>>

    @Query("SELECT * FROM official_sources WHERE jurisdiction = :jurisdiction ORDER BY id ASC")
    fun getSourcesByJurisdiction(jurisdiction: String): Flow<List<OfficialSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfficialSources(sources: List<OfficialSourceEntity>)

    // City Policies
    @Query("SELECT * FROM city_immigration_policies ORDER BY city ASC, id ASC")
    fun getAllCityPolicies(): Flow<List<CityImmigrationPolicyEntity>>

    @Query("SELECT * FROM city_immigration_policies WHERE city = :city ORDER BY id ASC")
    fun getPoliciesForCity(city: String): Flow<List<CityImmigrationPolicyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityPolicy(policy: CityImmigrationPolicyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCityPolicies(policies: List<CityImmigrationPolicyEntity>)

    // Graduation Checklist
    @Query("SELECT * FROM graduation_checklist_items WHERE userEmail = :userEmail ORDER BY id ASC")
    fun getGraduationChecklist(userEmail: String): Flow<List<GraduationChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: GraduationChecklistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<GraduationChecklistItemEntity>)

    @Query("UPDATE graduation_checklist_items SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleChecklistItem(id: Long, completed: Boolean)

    @Delete
    suspend fun deleteChecklistItem(item: GraduationChecklistItemEntity)

    // Immigration Documents
    @Query("SELECT * FROM immigration_documents WHERE userEmail = :userEmail ORDER BY id ASC")
    fun getImmigrationDocuments(userEmail: String): Flow<List<ImmigrationDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: ImmigrationDocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<ImmigrationDocumentEntity>)

    @Query("UPDATE immigration_documents SET status = :status WHERE id = :id")
    suspend fun updateDocumentStatus(id: Long, status: String)

    @Delete
    suspend fun deleteDocument(doc: ImmigrationDocumentEntity)

    // Official Contacts
    @Query("SELECT * FROM official_contacts ORDER BY location ASC, id ASC")
    fun getAllOfficialContacts(): Flow<List<OfficialContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfficialContacts(contacts: List<OfficialContactEntity>)

    // Reminders
    @Query("SELECT * FROM immigration_reminders WHERE userEmail = :userEmail AND isDismissed = 0 ORDER BY id ASC")
    fun getActiveReminders(userEmail: String): Flow<List<ImmigrationReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ImmigrationReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ImmigrationReminderEntity>)

    @Query("UPDATE immigration_reminders SET isDismissed = 1 WHERE id = :id")
    suspend fun dismissReminder(id: Long)

    @Delete
    suspend fun deleteReminder(reminder: ImmigrationReminderEntity)
}

