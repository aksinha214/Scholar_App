package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfileEntity::class,
        UniversityProfileEntity::class,
        CourseEntity::class,
        TimetableClassEntity::class,
        StudyPlanSessionEntity::class,
        ExamPlanEntity::class,
        PresentationPlanEntity::class,
        AcademicTaskEntity::class,
        ResearchProjectEntity::class,
        ResearchMilestoneEntity::class,
        ResearchPaperEntity::class,
        ResearchExperimentEntity::class,
        ManuscriptSectionEntity::class,
        ReproducibilityItemEntity::class,
        SkillItemEntity::class,
        GeneratedProjectEntity::class,
        ChinesePhraseEntity::class,
        KnowledgeItemEntity::class,
        RoadmapGoalEntity::class,
        ChatMessageEntity::class,
        PersonalDocumentEntity::class,
        CareerProfileEntity::class,
        CareerGoalEntity::class,
        SkillInventoryEntity::class,
        JobPostingEntity::class,
        SkillDevelopmentPlanEntity::class,
        JobApplicationEntity::class,
        InterviewPrepQuestionEntity::class,
        CareerRoadmapMilestoneEntity::class,
        ChineseLanguageProfileEntity::class,
        ChineseVocabularyEntity::class,
        ChineseStudySessionEntity::class,
        ChineseConversationMessageEntity::class,
        ChineseListeningExerciseEntity::class,
        ChineseGrammarPointEntity::class,
        ChineseWeeklyProgressEntity::class,
        ImmigrationProfileEntity::class,
        OfficialSourceEntity::class,
        CityImmigrationPolicyEntity::class,
        GraduationChecklistItemEntity::class,
        ImmigrationDocumentEntity::class,
        OfficialContactEntity::class,
        ImmigrationReminderEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun universityDao(): UniversityDao
    abstract fun researchDao(): ResearchDao
    abstract fun skillDao(): SkillDao
    abstract fun projectGeneratorDao(): ProjectGeneratorDao
    abstract fun chineseDao(): ChineseDao
    abstract fun knowledgeDao(): KnowledgeDao
    abstract fun roadmapDao(): RoadmapDao
    abstract fun chatDao(): ChatDao
    abstract fun personalDocumentDao(): PersonalDocumentDao
    abstract fun careerDao(): CareerDao
    abstract fun immigrationDao(): ImmigrationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cs_scholar_os.db"
                )
                .addCallback(DatabaseCallback(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                InitialDataPopulator.populate(database)
            }
        }
    }
}
