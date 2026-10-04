package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.*
import com.example.data.model.PersonalDocumentEntity
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun DocumentScreen(viewModel: ScholarViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Document Library", "Ask My Documents", "Knowledge Notes")

    val userDocs by viewModel.userDocuments.collectAsStateWithLifecycle()
    val activeCategory by viewModel.docCategoryFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.docSearchQuery.collectAsStateWithLifecycle()
    val activeSummary by viewModel.activeDocSummary.collectAsStateWithLifecycle()
    val askResult by viewModel.askDocumentsResult.collectAsStateWithLifecycle()
    val knowledgeItems by viewModel.knowledgeItems.collectAsStateWithLifecycle()

    var showUploadDialog by remember { mutableStateOf(false) }
    var viewingDocument by remember { mutableStateOf<PersonalDocumentEntity?>(null) }
    var editingDocument by remember { mutableStateOf<PersonalDocumentEntity?>(null) }
    var documentForActions by remember { mutableStateOf<PersonalDocumentEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = ScholarCyan
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> DocumentLibraryTab(
                documents = userDocs,
                activeCategory = activeCategory,
                searchQuery = searchQuery,
                onCategoryChange = { viewModel.setDocCategoryFilter(it) },
                onSearchChange = { viewModel.setDocSearchQuery(it) },
                onUploadClick = { showUploadDialog = true },
                onOpen = { viewingDocument = it },
                onAsk = {
                    selectedTab = 1
                    viewModel.askMyDocuments("Summarize and analyze '${it.fileName}'")
                },
                onSummarize = { viewModel.summarizeDocument(it) },
                onExtractActions = { documentForActions = it },
                onEdit = { editingDocument = it },
                onDelete = { viewModel.deleteDocument(it) }
            )
            1 -> AskMyDocumentsTab(
                askResult = askResult,
                onAsk = { viewModel.askMyDocuments(it) },
                onClear = { viewModel.clearAskDocumentsResult() }
            )
            2 -> KnowledgeNotesTab(viewModel)
        }
    }

    // Dialogs
    if (showUploadDialog) {
        UploadDocumentDialog(
            onDismiss = { showUploadDialog = false },
            onConfirm = { name, type, cat, content, tags, notes, course, isBinary, uriStr ->
                viewModel.uploadDocument(name, type, cat, content, tags, notes, course, isBinary, uriStr)
                showUploadDialog = false
            }
        )
    }

    if (viewingDocument != null) {
        ViewDocumentDialog(
            doc = viewingDocument!!,
            onDismiss = { viewingDocument = null },
            onAsk = {
                val doc = viewingDocument!!
                viewingDocument = null
                selectedTab = 1
                viewModel.askMyDocuments("What are the key requirements and details in '${doc.fileName}'?")
            },
            onSummarize = {
                val doc = viewingDocument!!
                viewModel.summarizeDocument(doc)
            }
        )
    }

    if (editingDocument != null) {
        EditDocumentDialog(
            doc = editingDocument!!,
            onDismiss = { editingDocument = null },
            onConfirm = { updated ->
                viewModel.updateDocument(updated)
                editingDocument = null
            }
        )
    }

    if (activeSummary != null) {
        SummaryResultDialog(
            summary = activeSummary!!,
            onDismiss = { viewModel.clearActiveDocSummary() }
        )
    }

    if (documentForActions != null) {
        val actions = remember(documentForActions) {
            DocumentIntelligenceEngine.extractActionableTasks(documentForActions!!)
        }
        ExtractActionsDialog(
            docName = documentForActions!!.fileName,
            actions = actions,
            onDismiss = { documentForActions = null },
            onCreateTask = { action ->
                viewModel.createTaskFromAction(action)
            }
        )
    }
}

@Composable
fun DocumentLibraryTab(
    documents: List<PersonalDocumentEntity>,
    activeCategory: String,
    searchQuery: String,
    onCategoryChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onUploadClick: () -> Unit,
    onOpen: (PersonalDocumentEntity) -> Unit,
    onAsk: (PersonalDocumentEntity) -> Unit,
    onSummarize: (PersonalDocumentEntity) -> Unit,
    onExtractActions: (PersonalDocumentEntity) -> Unit,
    onEdit: (PersonalDocumentEntity) -> Unit,
    onDelete: (PersonalDocumentEntity) -> Unit
) {
    val categories = listOf("ALL", "ACADEMIC", "RESEARCH", "PROJECTS", "CAREER", "CHINESE", "UNIVERSITY", "PERSONAL", "OTHER")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Personal Document Library",
                    subtitle = "Organize syllabi, manuscripts, notes, and certificates"
                )
                Button(
                    onClick = onUploadClick,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary, contentColor = DarkOnPrimary)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search documents (e.g., 'point cloud', 'deadline', 'CS305')...", fontSize = 12.5.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ScholarCyan) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    val isSel = cat.equals(activeCategory, ignoreCase = true)
                    FilterChip(
                        selected = isSel,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat, fontSize = 11.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkPrimary,
                            selectedLabelColor = DarkOnPrimary
                        )
                    )
                }
            }
        }

        if (documents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No documents found in '$activeCategory'", style = MaterialTheme.typography.titleSmall)
                        Text("Click '+ Upload' to import notes, syllabi, or papers.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(documents) { doc ->
            DocumentItemCard(
                doc = doc,
                onOpen = { onOpen(doc) },
                onAsk = { onAsk(doc) },
                onSummarize = { onSummarize(doc) },
                onExtractActions = { onExtractActions(doc) },
                onEdit = { onEdit(doc) },
                onDelete = { onDelete(doc) }
            )
        }
    }
}

@Composable
fun DocumentItemCard(
    doc: PersonalDocumentEntity,
    onOpen: () -> Unit,
    onAsk: () -> Unit,
    onSummarize: () -> Unit,
    onExtractActions: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val typeColor = when (doc.fileType.uppercase()) {
        "MD", "MARKDOWN" -> ScholarCyan
        "TXT" -> DarkPrimary
        "PDF" -> ScholarRed
        "DOCX" -> ScholarBlueAccent
        "PPTX" -> ScholarGold
        "IMAGE", "PNG", "JPG" -> ScholarPurple
        else -> ScholarGoldLight
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category + Type Tag + Edit/Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = typeColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = doc.fileType.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = typeColor,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = doc.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = ScholarCyan,
                            fontSize = 11.sp
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Metadata", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Document", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // File Name
            Text(
                text = doc.fileName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            // Course / Project association & Date
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (doc.courseOrProject.isNotBlank()) "Linked: ${doc.courseOrProject}" else "Personal Knowledge",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = ScholarGoldLight)
                )
                Text(
                    text = "Uploaded: ${doc.uploadDate}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF94A3B8))
                )
            }

            // Binary Notice if applicable
            if (doc.isBinaryUnsupported) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0x33EF4444),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = ScholarRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Binary file stored safely. Deep parsing scheduled for desktop indexer.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFCA5A5), fontSize = 10.5.sp)
                        )
                    }
                }
            } else if (doc.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = doc.content.take(160) + if (doc.content.length > 160) "..." else "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
            }

            // Tags
            if (doc.tags.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "🏷️ ${doc.tags}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp, color = ScholarCyan)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Open, Ask, Summarize, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f).height(34.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Open", fontSize = 11.sp)
                }

                Button(
                    onClick = onAsk,
                    modifier = Modifier.weight(1f).height(34.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan, contentColor = Color(0xFF0A1128))
                ) {
                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ask", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onSummarize,
                    modifier = Modifier.weight(1.1f).height(34.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Icon(Icons.Default.Summarize, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Summarize", fontSize = 11.sp)
                }

                IconButton(
                    onClick = onExtractActions,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(Icons.Default.AddTask, contentDescription = "Extract Tasks", tint = ScholarGold, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun AskMyDocumentsTab(
    askResult: AskDocumentsResponse?,
    onAsk: (String) -> Unit,
    onClear: () -> Unit
) {
    var queryInput by remember { mutableStateOf("What are the key deadlines and requirements in my uploaded documents?") }
    val sampleQuestions = listOf(
        "What is the deadline mentioned in my syllabus?",
        "Summarize this research paper.",
        "What methodology did I use in my research?",
        "Find everything I uploaded about computer vision.",
        "What are the requirements for this assignment?"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Ask My Documents",
                subtitle = "Grounded Q&A strictly backed by citations from your uploaded library"
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = queryInput,
                        onValueChange = { queryInput = it },
                        label = { Text("Ask your documents a specific question...") },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        trailingIcon = {
                            IconButton(onClick = { onAsk(queryInput) }) {
                                Icon(Icons.Default.Send, contentDescription = "Ask", tint = ScholarCyan)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onAsk(queryInput) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary, contentColor = DarkOnPrimary)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Search & Synthesize Documents")
                    }
                }
            }
        }

        item {
            Text(
                text = "SUGGESTED DOCUMENT INQUIRIES:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(sampleQuestions) { sample ->
                    SuggestionChip(
                        onClick = {
                            queryInput = sample
                            onAsk(sample)
                        },
                        label = { Text(sample, fontSize = 11.sp, maxLines = 1) }
                    )
                }
            }
        }

        if (askResult != null) {
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
                            FactBadge(if (askResult.foundInDocuments) "GROUNDED IN USER DOCUMENTS" else "GENERAL AI KNOWLEDGE")
                            IconButton(onClick = onClear) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Q: ${askResult.query}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGoldLight)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = askResult.documentFindings,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, lineHeight = 20.sp)
                        )

                        if (askResult.citations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "VERIFIED DOCUMENT CITATIONS:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                            )
                            askResult.citations.forEach { c ->
                                Surface(
                                    color = Color(0x3300D2D3),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "📑 ${c.documentTitle} (${c.category})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                                        )
                                        Text(
                                            text = "\"${c.relevantExcerpt}\"",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color(0x22FFFFFF),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "BACKGROUND CS & ACADEMIC CONTEXT:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = askResult.generalKnowledge,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), lineHeight = 18.sp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KnowledgeNotesTab(viewModel: ScholarViewModel) {
    val items by viewModel.knowledgeItems.collectAsStateWithLifecycle()
    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    var showAddNoteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Knowledge Base Notes",
                    subtitle = "Quick snippets, research quotes, and survival guides"
                )
                Button(onClick = { showAddNoteDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Note")
                }
            }
        }

        item {
            OutlinedTextField(
                value = search,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search curated knowledge notes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            )
        }

        items(items) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.category.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
                        IconButton(onClick = { viewModel.deleteKnowledge(item) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(item.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(item.content, style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Tags: ${item.tags} • Source: ${item.sourceRef}", style = MaterialTheme.typography.labelSmall, color = ScholarGold)
                }
            }
        }
    }

    if (showAddNoteDialog) {
        AddKnowledgeDialog(
            onDismiss = { showAddNoteDialog = false },
            onConfirm = { note ->
                viewModel.insertKnowledge(note)
                showAddNoteDialog = false
            }
        )
    }
}

// ----------------------------------------------------
// DIALOGS & ACTION HANDLERS
// ----------------------------------------------------

@Composable
fun UploadDocumentDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String, cat: String, content: String, tags: String, notes: String, course: String, isBinary: Boolean, uriStr: String) -> Unit
) {
    val context = LocalContext.current
    var fileName by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("TXT") }
    var category by remember { mutableStateOf("ACADEMIC") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var userNotes by remember { mutableStateOf("") }
    var courseOrProject by remember { mutableStateOf("") }
    var isBinaryUnsupported by remember { mutableStateOf(false) }
    var fileUriString by remember { mutableStateOf("") }

    val categories = listOf("ACADEMIC", "RESEARCH", "PROJECTS", "CAREER", "CHINESE", "UNIVERSITY", "PERSONAL", "OTHER")

    val filePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            fileUriString = uri.toString()
            val rawName = uri.lastPathSegment?.substringAfterLast('/') ?: "Uploaded_Document"
            fileName = rawName
            val ext = rawName.substringAfterLast('.', "").uppercase()
            fileType = if (ext.isNotBlank()) ext else "TXT"

            when (fileType) {
                "PDF", "DOCX", "PPTX" -> {
                    isBinaryUnsupported = true
                    content = "[Binary ${fileType} document stored in your personal library. On-device text parsing for binary ${fileType} files is scheduled for the desktop indexer. Metadata, course association, and notes are indexed for search.]"
                }
                "PNG", "JPG", "JPEG", "WEBP" -> {
                    isBinaryUnsupported = true
                    content = "[Image visual asset stored. On-device optical character recognition available via Document Scanner.]"
                }
                else -> {
                    isBinaryUnsupported = false
                    try {
                        val stream = context.contentResolver.openInputStream(uri)
                        content = stream?.bufferedReader()?.use { it.readText() } ?: ""
                    } catch (_: Exception) {
                        content = ""
                    }
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Document to Library") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Button(
                        onClick = { filePicker.launch("*/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FileOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick File from Device (TXT, MD, PDF, DOCX)")
                    }
                }
                item {
                    OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text("File Name") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = fileType, onValueChange = { fileType = it }, label = { Text("File Type (TXT, MD, PDF, DOCX, PPTX)") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Text("Select Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = cat == category,
                                onClick = { category = cat },
                                label = { Text(cat, fontSize = 10.sp) }
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(value = courseOrProject, onValueChange = { courseOrProject = it }, label = { Text("Course or Project Association") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (e.g. syllabus, deadlines, cvpr)") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = userNotes, onValueChange = { userNotes = it }, label = { Text("Personal Notes") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Document Content / Extracted Text") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fileName.isNotBlank()) {
                        onConfirm(fileName, fileType, category, content, tags, userNotes, courseOrProject, isBinaryUnsupported, fileUriString)
                    }
                }
            ) { Text("Save Document") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ViewDocumentDialog(
    doc: PersonalDocumentEntity,
    onDismiss: () -> Unit,
    onAsk: () -> Unit,
    onSummarize: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(doc.fileName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${doc.category} • ${doc.fileType} • Uploaded: ${doc.uploadDate}", fontSize = 11.sp, color = ScholarCyan)
            }
        },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (doc.courseOrProject.isNotBlank()) {
                    item {
                        Text("Course/Project: ${doc.courseOrProject}", style = MaterialTheme.typography.labelSmall.copy(color = ScholarGold))
                    }
                }
                if (doc.tags.isNotBlank()) {
                    item {
                        Text("Tags: ${doc.tags}", style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan))
                    }
                }
                if (doc.userNotes.isNotBlank()) {
                    item {
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("Notes: ${doc.userNotes}", modifier = Modifier.padding(8.dp), fontSize = 12.sp)
                        }
                    }
                }
                item {
                    Text("Document Content:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Surface(color = ScholarNavySurface, shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (doc.content.isNotBlank()) doc.content else "No text extract available.",
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), lineHeight = 18.sp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(onClick = onAsk) { Text("Ask", fontSize = 12.sp) }
                OutlinedButton(onClick = onSummarize) { Text("Summarize", fontSize = 12.sp) }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun EditDocumentDialog(
    doc: PersonalDocumentEntity,
    onDismiss: () -> Unit,
    onConfirm: (PersonalDocumentEntity) -> Unit
) {
    var fileName by remember { mutableStateOf(doc.fileName) }
    var category by remember { mutableStateOf(doc.category) }
    var tags by remember { mutableStateOf(doc.tags) }
    var userNotes by remember { mutableStateOf(doc.userNotes) }
    var courseOrProject by remember { mutableStateOf(doc.courseOrProject) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Document Metadata") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text("File Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = courseOrProject, onValueChange = { courseOrProject = it }, label = { Text("Course or Project") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = userNotes, onValueChange = { userNotes = it }, label = { Text("User Notes") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        doc.copy(
                            fileName = fileName,
                            category = category.uppercase(),
                            tags = tags,
                            userNotes = userNotes,
                            courseOrProject = courseOrProject
                        )
                    )
                }
            ) { Text("Save Changes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SummaryResultDialog(
    summary: StructuredDocumentSummary,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Document Summary", fontWeight = FontWeight.Bold)
                Text(summary.title, fontSize = 12.sp, color = ScholarCyan)
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("MAIN TOPIC:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
                    Text(summary.mainTopic, style = MaterialTheme.typography.bodySmall)
                }

                if (summary.isResearchPaper) {
                    item {
                        Text("RESEARCH PROBLEM:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold))
                        Text(summary.researchProblem ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                    item {
                        Text("METHODOLOGY & MODELS:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = DarkPrimary))
                        Text("${summary.methodology}\nModels: ${summary.models}", style = MaterialTheme.typography.bodySmall)
                    }
                    item {
                        Text("DATASET & EVALUATION METRICS:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7)))
                        Text("${summary.dataset}\nMetrics: ${summary.evaluationMetrics}", style = MaterialTheme.typography.bodySmall)
                    }
                    item {
                        Text("MAIN FINDINGS:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGreen))
                        Text(summary.mainFindings ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                    item {
                        Text("LIMITATIONS & RELEVANCE:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFFEF4444)))
                        Text("${summary.limitations}\nRelevance: ${summary.relevanceToScholar}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                if (summary.keyPoints.isNotEmpty()) {
                    item {
                        Text("KEY POINTS:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGoldLight))
                        summary.keyPoints.forEach { kp -> Text("• $kp", style = MaterialTheme.typography.bodySmall) }
                    }
                }

                if (summary.importantDates.isNotEmpty()) {
                    item {
                        Text("IMPORTANT DATES:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarRed))
                        summary.importantDates.forEach { d -> Text("⏰ $d", style = MaterialTheme.typography.bodySmall) }
                    }
                }

                if (summary.actionItems.isNotEmpty()) {
                    item {
                        Text("ACTION ITEMS:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
                        summary.actionItems.forEach { a -> Text("✅ $a", style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun ExtractActionsDialog(
    docName: String,
    actions: List<ExtractedAction>,
    onDismiss: () -> Unit,
    onCreateTask: (ExtractedAction) -> Unit
) {
    var createdSet by remember { mutableStateOf(setOf<String>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Action & Deadline Extraction", fontWeight = FontWeight.Bold)
                Text("From '$docName'", fontSize = 11.5.sp, color = ScholarCyan)
            }
        },
        text = {
            if (actions.isEmpty()) {
                Text("No actionable dates or deliverables found in this document.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Text("Detected ${actions.size} action items. Click to add to your University Planner:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    items(actions) { action ->
                        val isCreated = createdSet.contains(action.title)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(action.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
                                Text("Due: ${action.dueDate} • Association: ${action.courseOrProject}", style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight))
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        onCreateTask(action)
                                        createdSet = createdSet + action.title
                                    },
                                    enabled = !isCreated,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCreated) Color(0xFF065F46) else DarkPrimary
                                    ),
                                    modifier = Modifier.fillMaxWidth().height(34.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCreated) Icons.Default.Check else Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isCreated) "Added to Task Planner" else "Create Task: ${action.title} (Due: ${action.dueDate})", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Done") }
        }
    )
}

@Composable
fun AddKnowledgeDialog(
    onDismiss: () -> Unit,
    onConfirm: (com.example.data.model.KnowledgeItemEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Notes") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Knowledge Base") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Notes, Paper, Visa, Career)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags (comma-separated)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            com.example.data.model.KnowledgeItemEntity(
                                title = title,
                                category = category,
                                content = content,
                                tags = tags,
                                sourceRef = "Student Knowledge Base"
                            )
                        )
                    }
                }
            ) { Text("Save Note") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
