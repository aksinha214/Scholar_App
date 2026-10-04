package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.ChineseCoachEngine
import com.example.data.model.*
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.screens.chinese.CharacterPracticeCanvas
import com.example.ui.screens.chinese.SpeakingEvaluationCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScholarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChineseScreen(viewModel: ScholarViewModel) {
    var selectedMainTab by remember { mutableStateOf(0) }
    val mainTabs = listOf(
        "Dashboard",
        "Vocabulary",
        "HSK",
        "University & Lab",
        "Real-Life China",
        "Speaking & Listening",
        "Conversations",
        "Doc Intel & Translate",
        "Progress & Habits"
    )

    // State collections
    val profile by viewModel.chineseProfile.collectAsStateWithLifecycle()
    val vocabulary by viewModel.chineseVocabulary.collectAsStateWithLifecycle()
    val vocabDue by viewModel.chineseVocabularyDue.collectAsStateWithLifecycle()
    val studySessions by viewModel.chineseStudySessions.collectAsStateWithLifecycle()
    val listeningExercises by viewModel.chineseListeningExercises.collectAsStateWithLifecycle()
    val grammarPoints by viewModel.chineseGrammarPoints.collectAsStateWithLifecycle()
    val weeklyProgress by viewModel.chineseWeeklyProgress.collectAsStateWithLifecycle()
    val userDocuments by viewModel.userDocuments.collectAsStateWithLifecycle()
    val activeProject by viewModel.activeResearchProject.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Scrollable Tab Row for Chinese Coach sub-modules
        ScrollableTabRow(
            selectedTabIndex = selectedMainTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = ScholarCyan,
            edgePadding = 12.dp
        ) {
            mainTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedMainTab == index,
                    onClick = { selectedMainTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedMainTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedMainTab) {
            0 -> ChineseDashboardTab(
                viewModel = viewModel,
                profile = profile,
                vocabDueCount = vocabDue.size,
                totalVocabCount = vocabulary.size,
                studySessions = studySessions,
                weeklyProgress = weeklyProgress,
                onNavigateTab = { selectedMainTab = it }
            )
            1 -> ChineseVocabularyTab(
                viewModel = viewModel,
                vocabulary = vocabulary,
                vocabDue = vocabDue
            )
            2 -> ChineseHskTab(
                viewModel = viewModel,
                grammarPoints = grammarPoints
            )
            3 -> ChineseUniversityAndResearchTab(
                viewModel = viewModel,
                activeProject = activeProject
            )
            4 -> ChineseRealLifeLivingTab(viewModel = viewModel)
            5 -> ChineseSpeakingAndListeningTab(
                viewModel = viewModel,
                listeningExercises = listeningExercises
            )
            6 -> ChineseConversationsAndTutorTab(
                viewModel = viewModel,
                profile = profile
            )
            7 -> ChineseDocIntelAndTranslateTab(
                viewModel = viewModel,
                userDocuments = userDocuments
            )
            8 -> ChineseProgressAndHabitsTab(
                viewModel = viewModel,
                profile = profile,
                weeklyProgress = weeklyProgress,
                vocabulary = vocabulary,
                listeningExercises = listeningExercises
            )
        }
    }
}

// ========================================================
// 1. DASHBOARD TAB
// ========================================================

@Composable
fun ChineseDashboardTab(
    viewModel: ScholarViewModel,
    profile: ChineseLanguageProfileEntity?,
    vocabDueCount: Int,
    totalVocabCount: Int,
    studySessions: List<ChineseStudySessionEntity>,
    weeklyProgress: ChineseWeeklyProgressEntity?,
    onNavigateTab: (Int) -> Unit
) {
    var showStudyPlanDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ScholarNavyDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CHINESE LANGUAGE COACH • STAGE 5",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarCyan,
                                letterSpacing = 1.sp
                            )
                        )
                        FactBadge("HSK & ACADEMIC")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Current: ${profile?.currentLevel ?: "Not assessed"} → Target: ${profile?.targetHskLevel ?: "HSK 5"}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Goal: ${profile?.learningGoal ?: "Academic lab meetings & campus fluency"}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickMetricBadge(
                            label = "Streak",
                            value = "${profile?.currentStreak ?: 0} Days",
                            color = ScholarGold,
                            modifier = Modifier.weight(1f)
                        )
                        QuickMetricBadge(
                            label = "Due Today",
                            value = "$vocabDueCount Words",
                            color = if (vocabDueCount > 0) Color(0xFFEF4444) else ScholarGreen,
                            modifier = Modifier.weight(1f)
                        )
                        QuickMetricBadge(
                            label = "Total Saved",
                            value = "$totalVocabCount Words",
                            color = ScholarCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Quick Navigation Buttons
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    AssistChip(
                        onClick = { onNavigateTab(1) },
                        label = { Text("Spaced Repetition ($vocabDueCount Due)") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = ScholarGold) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { onNavigateTab(6) },
                        label = { Text("AI Conversation") },
                        leadingIcon = { Icon(Icons.Default.Forum, contentDescription = null, tint = ScholarCyan) }
                    )
                }
                item {
                    AssistChip(
                        onClick = { onNavigateTab(3) },
                        label = { Text("Email Supervisor") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DarkPrimary) }
                    )
                }
            }
        }

        // Today's Study Plan
        item {
            SectionHeader(
                title = "Today's Chinese Study Plan",
                subtitle = "${profile?.dailyStudyMinutes ?: 30} min/day target • ${profile?.preferredStudyTime ?: "Morning"}",
                actionText = "Re-generate",
                onActionClick = { showStudyPlanDialog = true }
            )
        }

        if (studySessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No study sessions generated yet.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showStudyPlanDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                        ) {
                            Text("Generate Balanced Study Plan")
                        }
                    }
                }
            }
        } else {
            items(studySessions) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = session.dayOfWeek.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ScholarCyan
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${session.moduleType}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = session.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = session.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        IconButton(onClick = { viewModel.toggleChineseStudySession(session.id, !session.isCompleted) }) {
                            Icon(
                                imageVector = if (session.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Complete",
                                tint = if (session.isCompleted) ScholarGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { viewModel.syncChineseSessionsToMainPlanner() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sync with Academic Timetable & Study Planner")
                }
            }
        }

        // Weekly Progress Overview
        item {
            SectionHeader(
                title = "Weekly Learning Progress",
                subtitle = "${weeklyProgress?.weekLabel ?: "Current Week"}"
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Recorded Weekly Metrics (Actual Data):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Study Time: ${weeklyProgress?.studyMinutes ?: 0} min", style = MaterialTheme.typography.bodySmall)
                        Text("Words Reviewed: ${weeklyProgress?.wordsReviewed ?: 0}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Exercises Done: ${weeklyProgress?.exercisesCompleted ?: 0}", style = MaterialTheme.typography.bodySmall)
                        Text("Listening Drills: ${weeklyProgress?.listeningPracticeCount ?: 0}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Conversations: ${weeklyProgress?.conversationSessionsCount ?: 0}", style = MaterialTheme.typography.bodySmall)
                        Text("Speaking Drills: ${weeklyProgress?.speakingPracticeCount ?: 0}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    if (showStudyPlanDialog) {
        StudyPlanGeneratorDialog(
            profile = profile,
            onDismiss = { showStudyPlanDialog = false },
            onGenerate = { mins, days, time, curr, target, date ->
                viewModel.generateChineseStudyPlan(mins, days, time, curr, target, date)
                showStudyPlanDialog = false
            }
        )
    }
}

@Composable
fun QuickMetricBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0x26000000), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8), fontSize = 10.5.sp))
            Text(text = value, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = color))
        }
    }
}

// ========================================================
// 2. VOCABULARY TAB & SPACED REPETITION
// ========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChineseVocabularyTab(
    viewModel: ScholarViewModel,
    vocabulary: List<ChineseVocabularyEntity>,
    vocabDue: List<ChineseVocabularyEntity>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Due", "Upcoming", "Mastered", "Favorites"
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredList = remember(vocabulary, searchQuery, selectedFilter) {
        vocabulary.filter { v ->
            val matchesQuery = searchQuery.isBlank() ||
                    v.hanzi.contains(searchQuery) ||
                    v.pinyin.contains(searchQuery, ignoreCase = true) ||
                    v.english.contains(searchQuery, ignoreCase = true) ||
                    v.category.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Due" -> v.reviewStatus == "Due" || v.reviewStatus == "New"
                "Upcoming" -> v.reviewStatus == "Upcoming"
                "Mastered" -> v.reviewStatus == "Mastered"
                "Favorites" -> v.isFavorite
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Vocabulary & Spaced Repetition",
                    subtitle = "Interval SM-2 algorithm • ${vocabDue.size} words due for review"
                )
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Word", fontSize = 12.sp)
                }
            }
        }

        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Chinese, pinyin, or English...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ScholarCyan,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp),
                textStyle = MaterialTheme.typography.bodySmall
            )
        }

        // Filter chips
        item {
            val filters = listOf("All", "Due (${vocabDue.size})", "Upcoming", "Mastered", "Favorites")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filters) { f ->
                    val filterKey = f.substringBefore(" ")
                    val isSelected = selectedFilter == filterKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filterKey },
                        label = { Text(f, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkPrimary,
                            selectedLabelColor = DarkOnPrimary
                        )
                    )
                }
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No vocabulary items found.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        } else {
            items(filteredList) { vocab ->
                VocabularyCardItem(
                    vocab = vocab,
                    onSpeak = { viewModel.speakChinese(vocab.hanzi) },
                    onToggleFavorite = { viewModel.toggleChineseVocabFavorite(vocab.id, !vocab.isFavorite) },
                    onToggleKnown = { viewModel.toggleChineseVocabKnown(vocab.id, !vocab.isKnown) },
                    onToggleDifficult = { viewModel.toggleChineseVocabDifficult(vocab.id, !vocab.isDifficult) },
                    onReview = { isCorrect, diff -> viewModel.reviewVocabularyItem(vocab, isCorrect, diff) },
                    onDelete = { viewModel.deleteChineseVocabulary(vocab) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddVocabularyDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { item ->
                viewModel.addChineseVocabulary(item)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun VocabularyCardItem(
    vocab: ChineseVocabularyEntity,
    onSpeak: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleKnown: () -> Unit,
    onToggleDifficult: () -> Unit,
    onReview: (Boolean, String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = vocab.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ScholarCyan
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${vocab.hskLevel}",
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${vocab.reviewStatus}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (vocab.reviewStatus == "Due") Color(0xFFEF4444) else ScholarGreen
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSpeak) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak",
                            tint = ScholarCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (vocab.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (vocab.isFavorite) ScholarGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = vocab.hanzi,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "${vocab.pinyin}  •  ${vocab.partOfSpeech}",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = ScholarGoldLight,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = vocab.english,
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFE2E8F0))
            )

            if (vocab.exampleSentenceZh.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33000000), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "例句: ${vocab.exampleSentenceZh}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 12.5.sp)
                        )
                        if (vocab.exampleSentenceEn.isNotBlank()) {
                            Text(
                                text = vocab.exampleSentenceEn,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                            )
                        }
                    }
                }
            }

            if (vocab.source.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Source: ${vocab.source}  •  Reviews: ${vocab.reviewCount}",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 10.5.sp)
                )
            }

            // Quick Spaced Repetition Buttons
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onReview(false, "Hard") },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                ) {
                    Text("Forgot (Due Tomorrow)", fontSize = 11.sp)
                }
                Button(
                    onClick = { onReview(true, "Medium") },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScholarGreen)
                ) {
                    Text("Remembered (Pass)", fontSize = 11.sp)
                }
            }
        }
    }
}

// ========================================================
// 3. HSK TAB
// ========================================================

@Composable
fun ChineseHskTab(
    viewModel: ScholarViewModel,
    grammarPoints: List<ChineseGrammarPointEntity>
) {
    var selectedHskIndex by remember { mutableStateOf(3) } // default HSK 4
    val hskLevels = remember { ChineseCoachEngine.getOfficialHskLevels() }
    val currentLevelData = hskLevels.getOrNull(selectedHskIndex) ?: hskLevels[0]

    val filteredGrammar = remember(grammarPoints, currentLevelData) {
        grammarPoints.filter { it.hskLevel.equals(currentLevelData.level, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "HSK Curated Syllabus & Practice",
                subtitle = "Standard Levels 1 through 6 • Verified learning targets"
            )
        }

        // HSK Level selector tabs
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(hskLevels.indices.toList()) { idx ->
                    val isSelected = selectedHskIndex == idx
                    val lvl = hskLevels[idx]
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedHskIndex = idx },
                        label = { Text(lvl.level, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkPrimary,
                            selectedLabelColor = DarkOnPrimary
                        )
                    )
                }
            }
        }

        // Level Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentLevelData.officialTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarGoldLight
                            )
                        )
                        FactBadge("CURATED SYLLABUS")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Verified Target Vocabulary: ${currentLevelData.verifiedVocabularyCount} words",
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = currentLevelData.description,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Capability: ${currentLevelData.targetCapability}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Core Grammar Focus:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                    )
                    currentLevelData.grammarFocus.forEach { gf ->
                        Text(
                            text = "• $gf",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Writing Focus: ${currentLevelData.writingFocus}",
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontSize = 12.sp)
                    )
                }
            }
        }

        // Sample Reading Passage
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "阅读理解 (Sample Reading)",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                        )
                        IconButton(onClick = { viewModel.speakChinese(currentLevelData.sampleReading) }) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = ScholarCyan)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentLevelData.sampleReading,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentLevelData.sampleReadingPinyin,
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontSize = 11.5.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentLevelData.sampleReadingEnglish,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        // Grammar Points List
        if (filteredGrammar.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Grammar Points for ${currentLevelData.level}",
                    subtitle = "Structures and common pitfalls"
                )
            }
            items(filteredGrammar) { gp ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = gp.pattern,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = gp.explanation, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0)))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "例句: ${gp.exampleZh}", style = MaterialTheme.typography.bodySmall.copy(color = Color.White))
                        Text(text = gp.examplePinyin, style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontSize = 11.5.sp))
                        Text(text = gp.exampleEn, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.5.sp))
                        if (gp.commonMistakes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "⚠️ 注意: ${gp.commonMistakes}", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFF87171), fontSize = 11.5.sp))
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 4. UNIVERSITY & RESEARCH CHINESE TAB
// ========================================================

@Composable
fun ChineseUniversityAndResearchTab(
    viewModel: ScholarViewModel,
    activeProject: ResearchProjectEntity?
) {
    var subSection by remember { mutableStateOf(0) } // 0: University Scenarios, 1: Research & CS Terms
    val scenarios = remember { ChineseCoachEngine.getUniversityScenarios() }
    val researchTerms = remember { ChineseCoachEngine.getResearchChineseTerms() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = subSection == 0,
                    onClick = { subSection = 0 },
                    label = { Text("University Scenarios (10)") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
                FilterChip(
                    selected = subSection == 1,
                    onClick = { subSection = 1 },
                    label = { Text("Research & CS Terms") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
            }
        }

        if (subSection == 0) {
            item {
                SectionHeader(
                    title = "University Chinese & Supervisor Etiquette",
                    subtitle = "Office hours, extensions, thesis proposals, and lab administration"
                )
            }

            items(scenarios) { sc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sc.titleZh,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ScholarGoldLight)
                            )
                            FactBadge("FORMAL TEMPLATE")
                        }
                        Text(
                            text = "${sc.titleEn}  •  Target: ${sc.formalTarget}",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Key Vocabulary: ${sc.keyVocabulary.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x33000000), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "口语对话 (Spoken Dialogue):",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                                )
                                Text(
                                    text = sc.dialogueSnippet,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 12.sp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x2638BDF8), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "正式书面邮件模版 (Formal Email Template):",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sc.emailTemplate,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFF1F5F9), fontSize = 12.sp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            item {
                SectionHeader(
                    title = "Research Lab & Technical CS Chinese",
                    subtitle = "Point clouds, loss functions, CUDA, ablation, and thesis defense"
                )
            }

            if (activeProject != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ScholarNavyDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Active Research Project Linked:",
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = activeProject.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Text(
                                text = "Area: ${activeProject.researchArea}  •  Problem: ${activeProject.researchProblem.take(80)}...",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                            )
                        }
                    }
                }
            }

            items(researchTerms) { term ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = term.hanzi,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            IconButton(onClick = { viewModel.speakChinese(term.hanzi) }) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Pronounce", tint = ScholarCyan)
                            }
                        }
                        Text(
                            text = "${term.pinyin}  •  ${term.english}",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Technical Meaning: ${term.technicalMeaning}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 12.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "例句: ${term.exampleUsage}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 12.sp)
                        )
                    }
                }
            }
        }
    }
}

// ========================================================
// 5. REAL-LIFE CHINA LIVING TAB (16 CATEGORIES)
// ========================================================

@Composable
fun ChineseRealLifeLivingTab(viewModel: ScholarViewModel) {
    val phrases = remember { ChineseCoachEngine.getRealLifePhrases() }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = remember {
        listOf("All") + phrases.map { it.category }.distinct()
    }

    val filtered = remember(phrases, selectedCategory) {
        if (selectedCategory == "All") phrases
        else phrases.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Chinese from Real Life in China",
                subtitle = "16 practical everyday categories • Colloquial & standard usage"
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkPrimary,
                            selectedLabelColor = DarkOnPrimary
                        )
                    )
                }
            }
        }

        items(filtered) { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = p.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                        )
                        IconButton(onClick = { viewModel.speakChinese(p.hanzi) }) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Speak", tint = ScholarCyan)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = p.hanzi,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        text = p.pinyin,
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = p.english,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFE2E8F0))
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x33000000), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "💡 ${p.usageExplanation}",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontSize = 11.5.sp)
                        )
                    }
                }
            }
        }
    }
}

// ========================================================
// 6. SPEAKING & LISTENING TAB
// ========================================================

@Composable
fun ChineseSpeakingAndListeningTab(
    viewModel: ScholarViewModel,
    listeningExercises: List<ChineseListeningExerciseEntity>
) {
    var subTab by remember { mutableStateOf(0) } // 0: Listening, 1: Speaking Practice, 2: Tones & Characters
    val speakingEval by viewModel.speakingEvaluation.collectAsStateWithLifecycle()
    val toneGuide = remember { ChineseCoachEngine.getTonesGuide() }
    val characterItems = remember { ChineseCoachEngine.getCharacterPracticeItems() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    label = { Text("Listening Drills") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
                FilterChip(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    label = { Text("Speaking Practice") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
                FilterChip(
                    selected = subTab == 2,
                    onClick = { subTab = 2 },
                    label = { Text("Tones & Characters") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
            }
        }

        when (subTab) {
            0 -> {
                item {
                    SectionHeader(
                        title = "Listening Comprehension Drills",
                        subtitle = "Audio playback via Text-to-Speech • Toggle pinyin and translation"
                    )
                }
                items(listeningExercises) { ex ->
                    ListeningExerciseCard(
                        exercise = ex,
                        onPlayAudio = { viewModel.speakChinese(ex.chineseText) },
                        onSelectOption = { optIndex -> viewModel.submitListeningAnswer(ex.id, optIndex) }
                    )
                }
            }
            1 -> {
                item {
                    SpeakingEvaluationCard(
                        expectedPhrase = "尊敬的张老师您好，关于上周布置的点云实验，我已经完成了基准对比。",
                        onEvaluateTranscription = { transcribed ->
                            viewModel.evaluateSpeaking("尊敬的张老师您好，关于上周布置的点云实验，我已经完成了基准对比。", transcribed)
                        },
                        evaluation = speakingEval,
                        onClearEvaluation = { viewModel.clearSpeakingEvaluation() }
                    )
                }
            }
            2 -> {
                item {
                    SectionHeader(
                        title = "Mandarin Tones & Character Stroke Practice",
                        subtitle = "5 tone pitch contours & handwriting tracing pad"
                    )
                }

                item {
                    Text(
                        text = "1. Mandarin Tone Pitch Contours (声调认知):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                    )
                }

                items(toneGuide) { t ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${t.nameZh}  •  ${t.nameEn}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGoldLight)
                                )
                                Text(
                                    text = "Pitch: ${t.pitchContour}  •  Example: ${t.exampleHanzi} (${t.exampleSyllable})",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontSize = 12.sp)
                                )
                                Text(
                                    text = t.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                                )
                                Text(
                                    text = "💡 Tip: ${t.tip}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontSize = 11.sp)
                                )
                            }
                            IconButton(onClick = { viewModel.speakChinese(t.exampleHanzi) }) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Pronounce", tint = ScholarCyan)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "2. Chinese Character Stroke & Handwriting Pad (汉字书写):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                    )
                }

                items(characterItems) { ch ->
                    CharacterPracticeCanvas(
                        character = ch.character,
                        pinyin = ch.pinyin,
                        meaning = ch.meaning,
                        strokeDescription = ch.strokeOrderDescription,
                        onSpeak = { viewModel.speakChinese(ch.character) }
                    )
                }
            }
        }
    }
}

@Composable
fun ListeningExerciseCard(
    exercise: ChineseListeningExerciseEntity,
    onPlayAudio: () -> Unit,
    onSelectOption: (Int) -> Unit
) {
    var showPinyin by remember { mutableStateOf(false) }
    var showTranslation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                )
                Button(
                    onClick = onPlayAudio,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play Audio (TTS)", fontSize = 11.5.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = exercise.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )

            // Audio text box
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33000000), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = exercise.chineseText,
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Medium)
                    )
                    if (showPinyin) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = exercise.pinyinText,
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontSize = 11.5.sp)
                        )
                    }
                    if (showTranslation) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = exercise.englishTranslation,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                        )
                    }
                }
            }

            // Toggle hide/show
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { showPinyin = !showPinyin }) {
                    Text(if (showPinyin) "Hide Pinyin" else "Show Pinyin", fontSize = 11.sp, color = ScholarCyan)
                }
                TextButton(onClick = { showTranslation = !showTranslation }) {
                    Text(if (showTranslation) "Hide English" else "Show English", fontSize = 11.sp, color = ScholarCyan)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "问题: ${exercise.question}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = ScholarGoldLight)
            )

            // Options
            val options = exercise.optionsJson.split("|")
            Spacer(modifier = Modifier.height(8.dp))
            options.forEachIndexed { idx, opt ->
                val isSelected = exercise.userSelectedOptionIndex == idx
                val isCorrect = exercise.correctOptionIndex == idx
                val optionColor = when {
                    exercise.isAnswered && isCorrect -> ScholarGreen
                    exercise.isAnswered && isSelected && !isCorrect -> Color(0xFFEF4444)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { onSelectOption(idx) },
                    colors = CardDefaults.cardColors(containerColor = optionColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = opt,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (exercise.isAnswered && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            if (exercise.isAnswered) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "解析: ${exercise.explanation}",
                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarGreen, fontSize = 11.5.sp)
                )
            }
        }
    }
}

// ========================================================
// 7. CONVERSATIONS & AI TUTOR TAB
// ========================================================

@Composable
fun ChineseConversationsAndTutorTab(
    viewModel: ScholarViewModel,
    profile: ChineseLanguageProfileEntity?
) {
    var subTab by remember { mutableStateOf(0) } // 0: Scenario Partner, 1: AI Chinese Tutor
    val selectedScenarioId by viewModel.selectedScenarioId.collectAsStateWithLifecycle()
    val messages by viewModel.chineseConversationMessages.collectAsStateWithLifecycle()
    val tutorResponse by viewModel.tutorResponse.collectAsStateWithLifecycle()

    var userMessageInput by remember { mutableStateOf("") }
    var tutorCommandInput by remember { mutableStateOf("") }

    val scenarios = remember {
        listOf(
            "advisor_meeting" to "Advisor Meeting",
            "restaurant" to "East Campus Canteen",
            "hospital" to "Hospital Fever Clinic",
            "police" to "Police Registration",
            "taxi" to "Taxi Ride",
            "dorm" to "Dorm Manager"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    label = { Text("Scenario Dialogue Partner") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
                FilterChip(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    label = { Text("AI Chinese Tutor") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
            }
        }

        if (subTab == 0) {
            item {
                SectionHeader(
                    title = "Interactive Scenario Dialogue",
                    subtitle = "AI roleplay partner • Reply in Chinese, pinyin, or English"
                )
            }

            // Scenario chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(scenarios) { (id, name) ->
                        FilterChip(
                            selected = selectedScenarioId == id,
                            onClick = { viewModel.selectConversationScenario(id) },
                            label = { Text(name, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DarkPrimary,
                                selectedLabelColor = DarkOnPrimary
                            )
                        )
                    }
                }
            }

            // Conversation Messages stream
            if (messages.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Start the dialogue! Say hello or make a statement.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Example prompt: '张老师您好，我想向您汇报一下实验进展。'",
                                style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan)
                            )
                        }
                    }
                }
            } else {
                items(messages) { msg ->
                    ConversationBubble(
                        message = msg,
                        onSpeak = { viewModel.speakChinese(msg.hanzi) }
                    )
                }
            }

            // Input Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = userMessageInput,
                        onValueChange = { userMessageInput = it },
                        placeholder = { Text("Reply in Chinese, pinyin, or English...") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ScholarCyan),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    IconButton(
                        onClick = {
                            if (userMessageInput.isNotBlank()) {
                                viewModel.sendChineseConversationMessage(selectedScenarioId, userMessageInput)
                                userMessageInput = ""
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = DarkPrimary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { viewModel.clearConversation(selectedScenarioId) }) {
                        Text("Clear Dialogue History", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        } else {
            // AI Chinese Tutor
            item {
                SectionHeader(
                    title = "AI Chinese Language Tutor",
                    subtitle = "Personalized coaching calibrated to your HSK level (${profile?.currentLevel ?: "Not assessed"})"
                )
            }

            // Quick Tutor Commands
            item {
                val quickCommands = listOf(
                    "Teach me 10 Chinese words for university life",
                    "Practice a conversation with my professor",
                    "Teach me Chinese words used in computer science",
                    "Help me prepare for HSK"
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickCommands) { cmd ->
                        AssistChip(
                            onClick = {
                                tutorCommandInput = cmd
                                viewModel.queryChineseTutor(cmd)
                            },
                            label = { Text(cmd, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Input field
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = tutorCommandInput,
                        onValueChange = { tutorCommandInput = it },
                        placeholder = { Text("Ask your Chinese tutor anything...") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ScholarCyan),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    IconButton(
                        onClick = {
                            if (tutorCommandInput.isNotBlank()) {
                                viewModel.queryChineseTutor(tutorCommandInput)
                            }
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = DarkPrimary)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Ask Tutor")
                    }
                }
            }

            // Tutor Response Card
            if (tutorResponse != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AI TUTOR RESPONSE",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                                )
                                IconButton(onClick = { viewModel.clearTutorResponse() }, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = tutorResponse?.replyContent ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, lineHeight = 20.sp)
                            )

                            if (tutorResponse?.vocabularyToSave?.isNotEmpty() == true) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.saveTutorVocabulary(tutorResponse!!.vocabularyToSave) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ScholarGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Save ${tutorResponse!!.vocabularyToSave.size} Words to My Deck", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationBubble(
    message: ChineseConversationMessageEntity,
    onSpeak: () -> Unit
) {
    val isUser = message.sender.uppercase() == "USER"
    val align = if (isUser) Alignment.End else Alignment.Start
    val bg = if (isUser) DarkPrimary else ScholarNavySurface

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = bg),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUser) "YOU" else "AI PARTNER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isUser) DarkOnPrimary else ScholarCyan
                        )
                    )
                    IconButton(onClick = onSpeak, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak",
                            tint = if (isUser) DarkOnPrimary else ScholarCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.hanzi,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )

                if (message.pinyin.isNotBlank()) {
                    Text(
                        text = message.pinyin,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ScholarGoldLight,
                            fontSize = 11.5.sp
                        )
                    )
                }

                if (message.english.isNotBlank()) {
                    Text(
                        text = message.english,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.5.sp
                        )
                    )
                }

                if (message.correctionZh.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x33000000), RoundedCornerShape(6.dp))
                            .padding(6.dp)
                    ) {
                        Column {
                            Text(
                                text = "💡 ${message.correctionZh}",
                                style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontSize = 11.sp)
                            )
                            if (message.correctionNotes.isNotBlank()) {
                                Text(
                                    text = message.correctionNotes,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 10.5.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 8. DOC INTEL & TRANSLATE TAB
// ========================================================

@Composable
fun ChineseDocIntelAndTranslateTab(
    viewModel: ScholarViewModel,
    userDocuments: List<PersonalDocumentEntity>
) {
    var subTab by remember { mutableStateOf(0) } // 0: Document-based Extraction, 1: Translation Tool
    val extractedItems by viewModel.extractedDocumentChinese.collectAsStateWithLifecycle()
    val translationResult by viewModel.translationResult.collectAsStateWithLifecycle()

    var translateInputText by remember { mutableStateOf("") }
    var translateDirection by remember { mutableStateOf("EN_TO_ZH") }
    var translateFormality by remember { mutableStateOf("FORMAL") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = subTab == 0,
                    onClick = { subTab = 0 },
                    label = { Text("Extract from Documents") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
                FilterChip(
                    selected = subTab == 1,
                    onClick = { subTab = 1 },
                    label = { Text("Politeness Translator") },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = DarkPrimary, selectedLabelColor = DarkOnPrimary)
                )
            }
        }

        if (subTab == 0) {
            item {
                SectionHeader(
                    title = "Document-Based Chinese Extraction",
                    subtitle = "Extract authentic vocabulary from campus notices, syllabi & research papers"
                )
            }

            if (userDocuments.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No personal documents uploaded in Knowledge Base.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.navigateTo(AppScreen.DOCUMENTS) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                            ) {
                                Text("Upload Document in Knowledge Base")
                            }
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Select an uploaded document to extract vocabulary:",
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                    )
                }

                items(userDocuments) { doc ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.extractChineseFromDocument(doc) },
                        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doc.fileName,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                                Text(
                                    text = "Category: ${doc.category}  •  ${doc.uploadDate}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                )
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Extract", tint = ScholarCyan)
                        }
                    }
                }
            }

            // Extracted items list
            if (extractedItems.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Extracted Vocabulary (${extractedItems.size})",
                        subtitle = "Grounded strictly in source document"
                    )
                }

                items(extractedItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.hanzi,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "${item.pinyin}  •  ${item.english}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = DarkPrimary, fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Context: \"${item.contextSentence}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.5.sp)
                                )
                            }
                            IconButton(onClick = { viewModel.saveExtractedChineseWord(item) }) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = "Save to Deck", tint = ScholarGreen)
                            }
                        }
                    }
                }
            }
        } else {
            // Politeness Translator
            item {
                SectionHeader(
                    title = "Chinese ↔ English Politeness Assistant",
                    subtitle = "Literal vs Natural translation with academic/formal etiquette"
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = translateDirection == "EN_TO_ZH",
                        onClick = { translateDirection = "EN_TO_ZH" },
                        label = { Text("English → Chinese") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = translateDirection == "ZH_TO_EN",
                        onClick = { translateDirection = "ZH_TO_EN" },
                        label = { Text("Chinese → English") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = translateFormality == "FORMAL",
                        onClick = { translateFormality = "FORMAL" },
                        label = { Text("Formal (Professor/Dean)") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = translateFormality == "CASUAL",
                        onClick = { translateFormality = "CASUAL" },
                        label = { Text("Casual / Peer") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = translateInputText,
                    onValueChange = { translateInputText = it },
                    placeholder = { Text("Enter message (e.g. 'I am sick and need 2 days leave for hospital appointment')") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ScholarCyan),
                    shape = RoundedCornerShape(12.dp),
                    textStyle = MaterialTheme.typography.bodySmall
                )
            }

            item {
                Button(
                    onClick = {
                        if (translateInputText.isNotBlank()) {
                            viewModel.translateChineseText(translateInputText, translateDirection, translateFormality)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Translate & Generate Etiquette Guidance")
                }
            }

            if (translationResult != null) {
                item {
                    val res = translationResult!!
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "TRANSLATION & PRAGMATICS",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                                )
                                FactBadge(res.politenessLevel)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "自然表达 (Natural / Politeness Adjusted):",
                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarGoldLight, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = res.naturalTranslation,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            if (res.pinyin.isNotBlank()) {
                                Text(
                                    text = res.pinyin,
                                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontSize = 12.sp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "字面直译 (Literal Translation):",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8))
                            )
                            Text(
                                text = res.literalTranslation,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0x33000000), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "💡 Usage Note: ${res.usageNotes}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontSize = 11.5.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 9. PROGRESS, HABITS & WEAKNESS TAB
// ========================================================

@Composable
fun ChineseProgressAndHabitsTab(
    viewModel: ScholarViewModel,
    profile: ChineseLanguageProfileEntity?,
    weeklyProgress: ChineseWeeklyProgressEntity?,
    vocabulary: List<ChineseVocabularyEntity>,
    listeningExercises: List<ChineseListeningExerciseEntity>
) {
    val weaknessReport by viewModel.weaknessReport.collectAsStateWithLifecycle()
    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Language Profile & Learning Habits",
                subtitle = "Confidence assessment • Streak tracking • Factual weakness analytics",
                actionText = "Edit Profile",
                onActionClick = { showEditProfileDialog = true }
            )
        }

        // Profile Overview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Current Level: ${profile?.currentLevel ?: "Not assessed"}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        text = "Target Level: ${profile?.targetHskLevel ?: "HSK 5"} (Target Date: ${profile?.targetDate ?: "2027-06-30"})",
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Daily Habit: ${profile?.dailyStudyMinutes ?: 30} min/day • ${profile?.daysPerWeek ?: 5} days/week",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                    )
                    Text(
                        text = "Streak: ${profile?.currentStreak ?: 0} Days (Longest: ${profile?.longestStreak ?: 0} Days)",
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Study & SRS Reminders",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                        Switch(
                            checked = profile?.notificationsEnabled ?: true,
                            onCheckedChange = { viewModel.toggleChineseNotifications(it) }
                        )
                    }
                }
            }
        }

        // Skill Confidences (1-5)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Self-Assessed Skill Confidences (1 - 5):",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ConfidenceBar("Speaking Confidence", profile?.speakingConfidence ?: 2)
                    ConfidenceBar("Listening Confidence", profile?.listeningConfidence ?: 3)
                    ConfidenceBar("Reading Confidence", profile?.readingConfidence ?: 4)
                    ConfidenceBar("Writing Confidence", profile?.writingConfidence ?: 2)
                    ConfidenceBar("Vocabulary Confidence", profile?.vocabularyConfidence ?: 3)
                    ConfidenceBar("Grammar Confidence", profile?.grammarConfidence ?: 3)
                }
            }
        }

        // Factual Weakness Analyzer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Factual Weakness Analyzer",
                    subtitle = "Derived exclusively from stored task performance"
                )
                TextButton(onClick = { viewModel.refreshWeaknessAnalysis() }) {
                    Text("Analyze Now", fontSize = 12.sp, color = DarkPrimary)
                }
            }
        }

        item {
            val report = weaknessReport
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (report == null) {
                        Text(
                            text = "Click 'Analyze Now' to run factual analysis over your vocabulary recalls and exercises.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
                        )
                    } else if (!report.hasSufficientData) {
                        Text(
                            text = report.summaryStatement,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = report.recommendations.firstOrNull() ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                        )
                    } else {
                        Text(
                            text = report.summaryStatement,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                        )
                        if (report.weakAreas.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Focus Areas (Weaknesses):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                            )
                            report.weakAreas.forEach { w ->
                                Text("• $w", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFCA5A5), fontSize = 12.sp))
                            }
                        }
                        if (report.strongAreas.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Retained Strengths:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGreen)
                            )
                            report.strongAreas.forEach { s ->
                                Text("• $s", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF86EFAC), fontSize = 12.sp))
                            }
                        }
                        if (report.recommendations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Recommended Action:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                            )
                            report.recommendations.forEach { r ->
                                Text("• $r", style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontSize = 12.sp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        EditChineseProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditProfileDialog = false },
            onSave = { updated ->
                viewModel.updateChineseProfile(updated)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
fun ConfidenceBar(label: String, score: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall)
            Text("$score / 5", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = DarkPrimary))
        }
        LinearProgressIndicator(
            progress = { score / 5f },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = if (score >= 4) ScholarGreen else if (score == 3) ScholarCyan else ScholarGold,
            trackColor = Color(0x33000000)
        )
    }
}

// ========================================================
// DIALOGS: ADD VOCAB, EDIT PROFILE, STUDY PLANNER
// ========================================================

@Composable
fun AddVocabularyDialog(
    onDismiss: () -> Unit,
    onAdd: (ChineseVocabularyEntity) -> Unit
) {
    var hanzi by remember { mutableStateOf("") }
    var pinyin by remember { mutableStateOf("") }
    var english by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("University") }
    var hskLevel by remember { mutableStateOf("HSK 4") }
    var exampleZh by remember { mutableStateOf("") }
    var exampleEn by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Vocabulary Item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = hanzi, onValueChange = { hanzi = it }, label = { Text("Chinese Characters (汉字)*") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = pinyin, onValueChange = { pinyin = it }, label = { Text("Pinyin*") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = english, onValueChange = { english = it }, label = { Text("English Meaning*") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (e.g. University, CS, Hospital)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = hskLevel, onValueChange = { hskLevel = it }, label = { Text("HSK Level (e.g. HSK 3, HSK 4, HSK 5)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = exampleZh, onValueChange = { exampleZh = it }, label = { Text("Example Sentence (Chinese)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = exampleEn, onValueChange = { exampleEn = it }, label = { Text("Example Sentence (English)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (hanzi.isNotBlank() && english.isNotBlank()) {
                        onAdd(
                            ChineseVocabularyEntity(
                                hanzi = hanzi.trim(),
                                pinyin = pinyin.trim(),
                                english = english.trim(),
                                category = category.trim(),
                                hskLevel = hskLevel.trim(),
                                exampleSentenceZh = exampleZh.trim(),
                                exampleSentenceEn = exampleEn.trim(),
                                source = "Manual Entry",
                                reviewStatus = "Due",
                                nextReviewDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
                            )
                        )
                    }
                },
                enabled = hanzi.isNotBlank() && english.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditChineseProfileDialog(
    currentProfile: ChineseLanguageProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (ChineseLanguageProfileEntity) -> Unit
) {
    var currentLevel by remember { mutableStateOf(currentProfile?.currentLevel ?: "Not assessed") }
    var targetHsk by remember { mutableStateOf(currentProfile?.targetHskLevel ?: "HSK 5") }
    var learningGoal by remember { mutableStateOf(currentProfile?.learningGoal ?: "") }
    var dailyMins by remember { mutableStateOf((currentProfile?.dailyStudyMinutes ?: 30).toString()) }
    var daysPerWeek by remember { mutableStateOf((currentProfile?.daysPerWeek ?: 5).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Chinese Language Profile") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = currentLevel, onValueChange = { currentLevel = it }, label = { Text("Current Level (or 'Not assessed')") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = targetHsk, onValueChange = { targetHsk = it }, label = { Text("Target HSK Level (e.g. HSK 5)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = learningGoal, onValueChange = { learningGoal = it }, label = { Text("Learning Goal") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dailyMins, onValueChange = { dailyMins = it }, label = { Text("Daily Study Minutes (15 - 120)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = daysPerWeek, onValueChange = { daysPerWeek = it }, label = { Text("Days Per Week (2 - 7)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = (currentProfile ?: ChineseLanguageProfileEntity()).copy(
                        currentLevel = currentLevel.trim(),
                        targetHskLevel = targetHsk.trim(),
                        learningGoal = learningGoal.trim(),
                        dailyStudyMinutes = dailyMins.toIntOrNull() ?: 30,
                        daysPerWeek = daysPerWeek.toIntOrNull() ?: 5
                    )
                    onSave(p)
                }
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun StudyPlanGeneratorDialog(
    profile: ChineseLanguageProfileEntity?,
    onDismiss: () -> Unit,
    onGenerate: (Int, Int, String, String, String, String) -> Unit
) {
    var minutes by remember { mutableStateOf((profile?.dailyStudyMinutes ?: 30).toString()) }
    var days by remember { mutableStateOf((profile?.daysPerWeek ?: 5).toString()) }
    var timeSlot by remember { mutableStateOf(profile?.preferredStudyTime ?: "Morning (08:00 - 08:30)") }
    var targetDate by remember { mutableStateOf(profile?.targetDate ?: "2027-06-30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Daily Chinese Plan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Generates realistic sessions for Vocabulary, Grammar, Listening, Speaking, Reading, Writing, and Review.",
                    style = MaterialTheme.typography.bodySmall
                )
                OutlinedTextField(value = minutes, onValueChange = { minutes = it }, label = { Text("Available Minutes Per Day (15 - 120)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = days, onValueChange = { days = it }, label = { Text("Days Per Week (2 - 7)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = timeSlot, onValueChange = { timeSlot = it }, label = { Text("Preferred Time Slot") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = targetDate, onValueChange = { targetDate = it }, label = { Text("Target Completion Date") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onGenerate(
                        minutes.toIntOrNull() ?: 30,
                        days.toIntOrNull() ?: 5,
                        timeSlot.trim(),
                        profile?.currentLevel ?: "Not assessed",
                        profile?.targetHskLevel ?: "HSK 5",
                        targetDate.trim()
                    )
                }
            ) {
                Text("Generate Plan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
