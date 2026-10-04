package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.*
import com.example.export.BriefingExporter
import com.example.ui.theme.DarkPrimary
import com.example.ui.theme.ScholarCyan
import com.example.ui.theme.ScholarGold
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScholarViewModel
import com.example.voice.VoiceInputState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandCenterScreen(viewModel: ScholarViewModel) {
    val context = LocalContext.current
    val query by viewModel.commandCenterQuery.collectAsStateWithLifecycle()
    val response by viewModel.commandCenterResponse.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isCommandCenterProcessing.collectAsStateWithLifecycle()
    val todayBriefing by viewModel.todayBriefing.collectAsStateWithLifecycle()
    val proactiveSuggestions by viewModel.proactiveSuggestions.collectAsStateWithLifecycle()
    val recentActivities by viewModel.recentActivities.collectAsStateWithLifecycle()
    val searchQuery by viewModel.globalSearchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.globalSearchResults.collectAsStateWithLifecycle()
    val voiceState by viewModel.voiceInputState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    // Voice dictation microphone permission launcher
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceDictation()
        } else {
            viewModel.resetVoiceState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "CS SCHOLAR OS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ScholarCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = ScholarGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "STAGE 8A VOICE & EXPORT",
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ScholarGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Text(
                            text = "AI Command Center",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                        modifier = Modifier.testTag("command_center_home_btn")
                    ) {
                        Icon(Icons.Default.Dashboard, contentDescription = "Dashboard", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Natural Language Query & Voice Dictation Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_query_card"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ScholarCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ask CS Scholar OS",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            // Voice Status Badge
                            when (voiceState) {
                                is VoiceInputState.Listening -> {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = ScholarCyan.copy(alpha = 0.2f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ScholarCyan))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "LISTENING",
                                                style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                            )
                                        }
                                    }
                                }
                                is VoiceInputState.Processing -> {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = ScholarGold.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "PROCESSING SPEECH",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Text Field for query
                        OutlinedTextField(
                            value = query,
                            onValueChange = { viewModel.setCommandCenterQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("command_center_input"),
                            placeholder = {
                                Text(
                                    "Ask about study, research, career, deadlines, or tap mic to speak...",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                )
                            },
                            singleLine = false,
                            maxLines = 3,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    focusManager.clearFocus()
                                    viewModel.executeCommandCenterQuery(query)
                                }
                            ),
                            trailingIcon = {
                                if (query.isNotBlank()) {
                                    IconButton(onClick = { viewModel.clearCommandCenterResponse() }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear input")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Voice State Feedback Banner
                        AnimatedVisibility(
                            visible = voiceState !is VoiceInputState.Idle,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                when (val state = voiceState) {
                                    is VoiceInputState.Listening -> {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = ScholarCyan.copy(alpha = 0.15f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Mic, contentDescription = null, tint = ScholarCyan, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "Listening... Speak your question now",
                                                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontWeight = FontWeight.SemiBold)
                                                    )
                                                }
                                                Row {
                                                    TextButton(onClick = { viewModel.stopVoiceDictation() }) {
                                                        Text("Done", color = ScholarCyan, fontWeight = FontWeight.Bold)
                                                    }
                                                    TextButton(onClick = { viewModel.cancelVoiceDictation() }) {
                                                        Text("Cancel", color = MaterialTheme.colorScheme.error)
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    is VoiceInputState.Processing -> {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = ScholarGold.copy(alpha = 0.15f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ScholarGold, strokeWidth = 2.dp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = "Transcribing speech to text...",
                                                    style = MaterialTheme.typography.bodySmall.copy(color = ScholarGold, fontWeight = FontWeight.SemiBold)
                                                )
                                            }
                                        }
                                    }

                                    is VoiceInputState.Error -> {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = state.message,
                                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onErrorContainer)
                                                    )
                                                }
                                                IconButton(onClick = { viewModel.resetVoiceState() }, modifier = Modifier.size(24.dp)) {
                                                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = MaterialTheme.colorScheme.error)
                                                }
                                            }
                                        }
                                    }

                                    VoiceInputState.Idle -> {}
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bottom Actions: Voice Button + Ask AI Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Voice Dictation Button (Microphone)
                            FilledTonalIconButton(
                                onClick = {
                                    when (voiceState) {
                                        is VoiceInputState.Listening -> viewModel.stopVoiceDictation()
                                        is VoiceInputState.Processing -> viewModel.cancelVoiceDictation()
                                        else -> {
                                            val hasPermission = ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.RECORD_AUDIO
                                            ) == PackageManager.PERMISSION_GRANTED

                                            if (hasPermission) {
                                                viewModel.startVoiceDictation()
                                            } else {
                                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(44.dp)
                                    .testTag("voice_dictation_btn"),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = if (voiceState is VoiceInputState.Listening) ScholarCyan else MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = if (voiceState is VoiceInputState.Listening) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Voice Dictation",
                                    tint = if (voiceState is VoiceInputState.Listening) Color.Black else ScholarCyan
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Voice or Text Input",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            // Submit Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.executeCommandCenterQuery(query)
                                },
                                enabled = query.isNotBlank() && !isProcessing,
                                modifier = Modifier.testTag("ask_ai_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.Black,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                } else {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text("Ask AI", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Quick Action Buttons
            item {
                Column {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            QuickActionChip(
                                label = "Plan My Day",
                                icon = Icons.Default.Today,
                                color = ScholarGold,
                                onClick = { viewModel.executeQuickAction("What is the most important thing I should do today?") }
                            )
                        }
                        item {
                            QuickActionChip(
                                label = "Research Status",
                                icon = Icons.Default.Science,
                                color = DarkPrimary,
                                onClick = { viewModel.executeQuickAction("Show my research progress and active milestones") }
                            )
                        }
                        item {
                            QuickActionChip(
                                label = "Upcoming Deadlines",
                                icon = Icons.Default.Alarm,
                                color = Color(0xFFFF5252),
                                onClick = { viewModel.executeQuickAction("What are my upcoming deadlines?") }
                            )
                        }
                        item {
                            QuickActionChip(
                                label = "Study Now",
                                icon = Icons.Default.School,
                                color = ScholarCyan,
                                onClick = { viewModel.executeQuickAction("What should I study today?", AppScreen.UNIVERSITY) }
                            )
                        }
                        item {
                            QuickActionChip(
                                label = "Career Check",
                                icon = Icons.Default.Work,
                                color = DarkPrimary,
                                onClick = { viewModel.executeQuickAction("What skills should I improve for my target job?", AppScreen.CAREER_CENTER) }
                            )
                        }
                        item {
                            QuickActionChip(
                                label = "Chinese Practice",
                                icon = Icons.Default.Translate,
                                color = ScholarGold,
                                onClick = { viewModel.executeQuickAction("What Chinese should I practice today?", AppScreen.CHINESE_LANGUAGE) }
                            )
                        }
                        item {
                            QuickActionChip(
                                label = "Documents",
                                icon = Icons.Default.Description,
                                color = ScholarCyan,
                                onClick = { viewModel.navigateTo(AppScreen.DOCUMENTS) }
                            )
                        }
                        item {
                            QuickActionChip(
                                label = "Immigration / Work",
                                icon = Icons.Default.Public,
                                color = ScholarGold,
                                onClick = { viewModel.executeQuickAction("What documents are important for my current China work transition?", AppScreen.CHINA_WORK) }
                            )
                        }
                        item {
                            QuickActionChip(
                                label = "Search My Knowledge",
                                icon = Icons.Default.Search,
                                color = DarkPrimary,
                                onClick = { isSearchExpanded = true }
                            )
                        }
                    }
                }
            }

            // 3. AI Structured Response (if available)
            response?.let { resp ->
                item {
                    AIResponseCard(
                        response = resp,
                        onDismiss = { viewModel.clearCommandCenterResponse() },
                        onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )
                }
            }

            // 4. Global Search Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isSearchExpanded = !isSearchExpanded },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = ScholarCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Cross-Module Global Search",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Icon(
                                imageVector = if (isSearchExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSearchExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setGlobalSearchQuery(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("global_search_input"),
                                placeholder = { Text("Search tasks, notes, papers, jobs, Chinese vocab, timetable...") },
                                singleLine = true,
                                trailingIcon = {
                                    if (searchQuery.isNotBlank()) {
                                        IconButton(onClick = { viewModel.setGlobalSearchQuery("") }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear")
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(10.dp)
                            )

                            if (searchQuery.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Results found: ${searchResults.size}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                if (searchResults.isEmpty()) {
                                    Text(
                                        text = "No matching records found across your CS Scholar OS database.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                                    )
                                } else {
                                    searchResults.take(6).forEach { res ->
                                        SearchResultRow(
                                            result = res,
                                            onNavigate = { viewModel.navigateTo(res.module) }
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Today's Briefing Feature with Export Integration
            item {
                TodayBriefingCard(
                    briefing = todayBriefing,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    onExportClick = { showExportDialog = true }
                )
            }

            // 6. Proactive Suggestions Section
            if (proactiveSuggestions.isNotEmpty()) {
                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = ScholarGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Personalized Recommendations",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        proactiveSuggestions.take(3).forEach { sug ->
                            ProactiveSuggestionCard(
                                suggestion = sug,
                                onAction = { viewModel.navigateTo(sug.targetScreen) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // 7. Recent Activity Timeline
            if (recentActivities.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, contentDescription = null, tint = DarkPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Recent Activity (Verified Audit)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            recentActivities.take(5).forEach { act ->
                                ActivityRow(activity = act, onNavigate = { viewModel.navigateTo(act.module) })
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Export Daily Briefing Dialog
    if (showExportDialog) {
        ExportBriefingDialog(
            briefing = todayBriefing,
            onDismiss = { showExportDialog = false },
            onShareMarkdown = {
                showExportDialog = false
                viewModel.shareBriefingMarkdown(context)
            },
            onSharePlainText = {
                showExportDialog = false
                viewModel.shareBriefingPlainText(context)
            },
            onSharePdf = {
                showExportDialog = false
                viewModel.exportAndShareBriefingPdf(context)
            }
        )
    }
}

@Composable
fun ExportBriefingDialog(
    briefing: TodayBriefing,
    onDismiss: () -> Unit,
    onShareMarkdown: () -> Unit,
    onSharePlainText: () -> Unit,
    onSharePdf: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("export_briefing_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = ScholarCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Export Daily Briefing",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Share today's schedule, deadlines, and priorities using Android's standard share sheet.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Share as Markdown
                ExportOptionCard(
                    title = "Share as Markdown (.md)",
                    description = "Structured headings, bullet points, formatted for Obsidian, Notion, or GitHub",
                    icon = Icons.Default.Description,
                    accentColor = ScholarCyan,
                    onClick = onShareMarkdown,
                    tag = "share_markdown_btn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Option 2: Share as Plain Text
                ExportOptionCard(
                    title = "Share as Plain Text",
                    description = "Clean readable text formatted for WeChat, Telegram, WhatsApp, or Email",
                    icon = Icons.Default.TextFields,
                    accentColor = ScholarGold,
                    onClick = onSharePlainText,
                    tag = "share_plaintext_btn"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Option 3: Export PDF Document
                ExportOptionCard(
                    title = "Export as PDF Document",
                    description = "Native Android PDF document with priority color-coding and calendar styling",
                    icon = Icons.Default.PictureAsPdf,
                    accentColor = Color(0xFFFF5252),
                    onClick = onSharePdf,
                    tag = "share_pdf_btn"
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun ExportOptionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    tag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(accentColor.copy(alpha = 0.4f))),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp))
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun QuickActionChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        modifier = Modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
fun AIResponseCard(
    response: CommandCenterResponse,
    onDismiss: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_response_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(ScholarCyan.copy(alpha = 0.6f)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with intent tag and dismiss
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ScholarCyan.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "Intent: ${response.intentResult.primaryIntent.displayName}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan, fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close Response", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\"${response.query}\"",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION A: CS SCHOLAR OS DATA
            SectionPartitionCard(
                title = "A. INFORMATION FROM MY CS SCHOLAR OS DATA",
                badgeColor = ScholarCyan,
                badgeBg = ScholarCyan.copy(alpha = 0.15f),
                items = response.dataSection
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION B: AI RECOMMENDATION
            SectionPartitionCard(
                title = "B. AI SUGGESTION / RECOMMENDATION",
                badgeColor = DarkPrimary,
                badgeBg = DarkPrimary.copy(alpha = 0.15f),
                items = response.suggestionSection
            )

            Spacer(modifier = Modifier.height(10.dp))

            // SECTION C: OFFICIAL EXTERNAL INFORMATION
            if (response.officialSection.isNotEmpty()) {
                SectionPartitionCard(
                    title = "C. OFFICIAL EXTERNAL INFORMATION",
                    badgeColor = ScholarGold,
                    badgeBg = ScholarGold.copy(alpha = 0.15f),
                    items = response.officialSection
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // SECTION D: INFORMATION NOT AVAILABLE
            if (response.missingDataSection.isNotEmpty()) {
                SectionPartitionCard(
                    title = "D. INFORMATION NOT AVAILABLE IN MY DATA",
                    badgeColor = Color(0xFFFFB74D),
                    badgeBg = Color(0xFFFFB74D).copy(alpha = 0.15f),
                    items = response.missingDataSection
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Source Attribution Chips
            if (response.sourceAttributions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Attribution:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    response.sourceAttributions.forEach { source ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = source,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }

            // Deep Navigation link for primary target screen
            if (response.intentResult.targetModules.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                val target = response.intentResult.targetModules.first()
                if (target != AppScreen.COMMAND_CENTER) {
                    OutlinedButton(
                        onClick = { onNavigate(target) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open ${target.title} Module")
                    }
                }
            }
        }
    }
}

@Composable
fun SectionPartitionCard(
    title: String,
    badgeColor: Color,
    badgeBg: Color,
    items: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = badgeBg),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = badgeColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.height(6.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text("•", color = badgeColor, modifier = Modifier.padding(end = 6.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp)
                    )
                }
            }
        }
    }
}

@Composable
fun TodayBriefingCard(
    briefing: TodayBriefing,
    onNavigate: (AppScreen) -> Unit,
    onExportClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_briefing_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = briefing.greeting,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = briefing.dateSummary,
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "TODAY'S BRIEFING",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (briefing.isEmptyState) {
                Text(
                    text = briefing.emptyStateNotice,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            } else {
                Text(
                    text = "Key Priorities Summary:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                briefing.prioritiesOverview.forEach { line ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(ScholarCyan))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = line, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons: Export Briefing & View Details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onExportClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_briefing_btn"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = ScholarCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Briefing", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (expanded) "Hide Tiers" else "View All Tiers", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(12.dp))

                    if (briefing.highPriorityItems.isNotEmpty()) {
                        PriorityTierSection(
                            title = "High Priority (Immediate Action)",
                            badgeColor = Color(0xFFFF5252),
                            items = briefing.highPriorityItems,
                            onNavigate = onNavigate
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (briefing.recommendedItems.isNotEmpty()) {
                        PriorityTierSection(
                            title = "Recommended (Target for Today)",
                            badgeColor = ScholarGold,
                            items = briefing.recommendedItems,
                            onNavigate = onNavigate
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (briefing.optionalItems.isNotEmpty()) {
                        PriorityTierSection(
                            title = "Optional / Upcoming",
                            badgeColor = DarkPrimary,
                            items = briefing.optionalItems.take(3),
                            onNavigate = onNavigate
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityTierSection(
    title: String,
    badgeColor: Color,
    items: List<PrioritizedItem>,
    onNavigate: (AppScreen) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(color = badgeColor, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))
        items.forEach { item ->
            Surface(
                onClick = { onNavigate(item.module) },
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "${item.subtitle} • ${item.urgencyReason}",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = item.priorityLevel.label,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProactiveSuggestionCard(
    suggestion: ProactiveSuggestion,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ScholarGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = ScholarGold, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = suggestion.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = suggestion.message,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(
                    onClick = onAction,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(suggestion.actionText, style = MaterialTheme.typography.labelMedium.copy(color = ScholarCyan, fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ScholarCyan, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun SearchResultRow(
    result: GlobalSearchResult,
    onNavigate: () -> Unit
) {
    Surface(
        onClick = onNavigate,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = DarkPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = result.moduleName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(color = DarkPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = result.title,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = result.shortPreview,
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ActivityRow(
    activity: ScholarActivity,
    onNavigate: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(DarkPrimary))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = "${activity.description} • ${activity.formattedTime}",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f))
            )
        }
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = activity.moduleLabel,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
            )
        }
    }
}
