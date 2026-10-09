package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
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
import com.example.ui.components.ModuleTabBar
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
    var docToDelete by remember { mutableStateOf<PersonalDocumentEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ModuleTabBar(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

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
                onDelete = { docToDelete = it }
            )
            1 -> AskMyDocumentsTab(
                askResult = askResult,
                onAsk = { viewModel.askMyDocuments(it) },
                onClear = { viewModel.clearAskDocumentsResult() }
            )
            2 -> KnowledgeNotesTab(viewModel)
        }
    }

    if (docToDelete != null) {
        com.example.ui.components.DeleteConfirmationDialog(
            title = "Delete Document",
            message = "Are you sure you want to delete '${docToDelete!!.fileName}' from your personal library?",
            onConfirm = {
                viewModel.deleteDocument(docToDelete!!)
                docToDelete = null
            },
            onDismiss = { docToDelete = null }
        )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Personal Document Library",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Organize syllabi, manuscripts, notes, and certificates",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Button(
                    onClick = onUploadClick,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary, contentColor = DarkOnPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
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
                com.example.ui.components.EmptyStateView(
                    icon = Icons.Default.FolderOpen,
                    title = "No documents found in '$activeCategory'",
                    subtitle = if (searchQuery.isNotBlank()) "No files match query '$searchQuery'." else "Import syllabi, research papers, course notes, or assignment prompts.",
                    actionText = "+ Upload Document",
                    onAction = onUploadClick
                )
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
    var noteToEdit by remember { mutableStateOf<com.example.data.model.KnowledgeItemEntity?>(null) }
    var noteToDelete by remember { mutableStateOf<com.example.data.model.KnowledgeItemEntity?>(null) }

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

        if (items.isEmpty()) {
            item {
                com.example.ui.components.EmptyStateView(
                    icon = Icons.Default.Description,
                    title = "No Knowledge Notes Found",
                    subtitle = if (search.isNotBlank()) "No notes matching '$search'." else "Create your first knowledge snippet or campus survival note.",
                    actionText = "+ Add Note",
                    onAction = { showAddNoteDialog = true }
                )
            }
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
                        Row {
                            IconButton(onClick = { noteToEdit = item }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Note", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { noteToDelete = item }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Note", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                            }
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

    if (noteToEdit != null) {
        EditKnowledgeDialog(
            note = noteToEdit!!,
            onDismiss = { noteToEdit = null },
            onConfirm = { updated ->
                viewModel.updateKnowledge(updated)
                noteToEdit = null
            }
        )
    }

    if (noteToDelete != null) {
        com.example.ui.components.DeleteConfirmationDialog(
            title = "Delete Knowledge Note",
            message = "Are you sure you want to delete note '${noteToDelete!!.title}'?",
            onConfirm = {
                viewModel.deleteKnowledge(noteToDelete!!)
                noteToDelete = null
            },
            onDismiss = { noteToDelete = null }
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
    var uploadMode by remember { mutableStateOf(0) } // 0 = File Import, 1 = Plain-Text Note
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

    val academicMimes = remember {
        arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain",
            "text/markdown",
            "image/jpeg",
            "image/png",
            "*/*"
        )
    }

    val filePicker = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            fileUriString = uri.toString()
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}

            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val resolvedName = cursor?.use { c ->
                if (c.moveToFirst()) {
                    val nameIdx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIdx != -1) c.getString(nameIdx) else null
                } else null
            } ?: uri.lastPathSegment?.substringAfterLast('/') ?: "academic_document"

            fileName = resolvedName
            val ext = resolvedName.substringAfterLast('.', "").uppercase()
            fileType = if (ext.isNotBlank()) ext else "FILE"

            when (fileType) {
                "PDF", "DOC", "DOCX", "PPT", "PPTX", "XLS", "XLSX" -> {
                    isBinaryUnsupported = true
                    content = "[Binary ${fileType} document stored in your library. Full text parsing for binary ${fileType} files is not run locally on device. File can be opened via system apps and searched via title, notes, and tags.]"
                }
                "PNG", "JPG", "JPEG", "WEBP" -> {
                    isBinaryUnsupported = true
                    content = "[Visual image asset stored. Image can be previewed or opened via system gallery.]"
                }
                "TXT", "MD", "MARKDOWN", "CSV", "JSON", "PY", "CPP", "JAVA" -> {
                    isBinaryUnsupported = false
                    try {
                        val stream = context.contentResolver.openInputStream(uri)
                        content = stream?.bufferedReader()?.use { it.readText() } ?: ""
                    } catch (_: Exception) {
                        content = ""
                    }
                }
                else -> {
                    isBinaryUnsupported = true
                    content = "[File ${fileType} document stored. Content available via external viewer.]"
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (uploadMode == 0) "Import Document File" else "Create Plain-Text Note", fontWeight = FontWeight.Bold)
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uploadMode == 0,
                            onClick = { uploadMode = 0 },
                            label = { Text("File Import", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = uploadMode == 1,
                            onClick = {
                                uploadMode = 1
                                fileType = "TXT"
                                isBinaryUnsupported = false
                            },
                            label = { Text("Text Note", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (uploadMode == 0) {
                    item {
                        Button(
                            onClick = { filePicker.launch(academicMimes) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary, contentColor = DarkOnPrimary)
                        ) {
                            Icon(Icons.Default.FileOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select File (PDF, DOCX, PPTX, TXT, MD, Images)")
                        }
                    }
                    if (fileUriString.isNotBlank()) {
                        item {
                            Surface(color = ScholarCyan.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth()) {
                                Text("Selected: $fileName ($fileType)", modifier = Modifier.padding(8.dp), fontSize = 11.5.sp, color = ScholarCyan)
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text(if (uploadMode == 0) "Document Filename *" else "Note Title *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text("Select Category:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
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
                    OutlinedTextField(
                        value = courseOrProject,
                        onValueChange = { courseOrProject = it },
                        label = { Text("Associated Course or Project") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags (comma-separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = userNotes,
                        onValueChange = { userNotes = it },
                        label = { Text("Personal Description / Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text(if (uploadMode == 0) "Extracted / Indexed Text" else "Note Content *") },
                        modifier = Modifier.fillMaxWidth().height(110.dp)
                    )
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
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(doc.fileName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${doc.category} • ${doc.fileType} • Added: ${doc.uploadDate}", fontSize = 11.sp, color = ScholarCyan)
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
                if (doc.fileUriString.isNotBlank()) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    val mime = when (doc.fileType.uppercase()) {
                                        "PDF" -> "application/pdf"
                                        "DOC", "DOCX" -> "application/msword"
                                        "PPT", "PPTX" -> "application/vnd.ms-powerpoint"
                                        "XLS", "XLSX" -> "application/vnd.ms-excel"
                                        "PNG", "JPG", "JPEG" -> "image/*"
                                        "TXT", "MD" -> "text/plain"
                                        else -> "*/*"
                                    }
                                    setDataAndType(Uri.parse(doc.fileUriString), mime)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "No app available to open this file format", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open File", fontSize = 11.5.sp)
                    }
                }
                OutlinedButton(onClick = onAsk) { Text("Ask AI", fontSize = 11.5.sp) }
                OutlinedButton(onClick = onSummarize) { Text("Summarize", fontSize = 11.5.sp) }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun EditKnowledgeDialog(
    note: com.example.data.model.KnowledgeItemEntity,
    onDismiss: () -> Unit,
    onConfirm: (com.example.data.model.KnowledgeItemEntity) -> Unit
) {
    var title by remember { mutableStateOf(note.title) }
    var category by remember { mutableStateOf(note.category) }
    var content by remember { mutableStateOf(note.content) }
    var tags by remember { mutableStateOf(note.tags) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Knowledge Note", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth().height(100.dp))
                OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Tags") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(note.copy(title = title.trim(), category = category.trim(), content = content.trim(), tags = tags.trim()))
                    }
                }
            ) { Text("Save Changes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
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
