package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ai.ImmigrationIntelligenceEngine
import com.example.data.model.*
import com.example.ui.components.FactBadge
import com.example.ui.components.ModuleTabBar
import com.example.ui.components.SectionHeader
import com.example.ui.screens.chinawork.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScholarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChinaWorkScreen(viewModel: ScholarViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Overview & Expiry",
        "Immigration Profile",
        "Source Registry",
        "City Policies",
        "Post-Grad Options",
        "Work Authorization",
        "Grad Checklist",
        "Documents",
        "Career & Research",
        "Official Q&A",
        "Contacts"
    )

    // Observable states from ViewModel
    val profile by viewModel.immigrationProfile.collectAsStateWithLifecycle()
    val sources by viewModel.officialSources.collectAsStateWithLifecycle()
    val cityPolicies by viewModel.cityImmigrationPolicies.collectAsStateWithLifecycle()
    val checklist by viewModel.graduationChecklist.collectAsStateWithLifecycle()
    val documents by viewModel.immigrationDocuments.collectAsStateWithLifecycle()
    val contacts by viewModel.officialContacts.collectAsStateWithLifecycle()
    val reminders by viewModel.immigrationReminders.collectAsStateWithLifecycle()
    val passportAudit by viewModel.passportExpirationAudit.collectAsStateWithLifecycle()
    val permitAudit by viewModel.residencePermitExpirationAudit.collectAsStateWithLifecycle()
    val qaAnswer by viewModel.structuredImmigrationAnswer.collectAsStateWithLifecycle()
    val jobAnalysis by viewModel.jobImmigrationAnalysis.collectAsStateWithLifecycle()
    val bridge by viewModel.researchCareerChinaBridge.collectAsStateWithLifecycle()
    val jobPostings by viewModel.jobPostings.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var selectedSourceForDetail by remember { mutableStateOf<OfficialSourceEntity?>(null) }
    var showAddPolicyDialog by remember { mutableStateOf(false) }
    var showAddChecklistDialog by remember { mutableStateOf(false) }
    var showAddDocDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header Banner with Official Disclaimer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ScholarNavySurface)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = ScholarCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "China Student → Work & Immigration",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                FactBadge(type = "OFFICIAL")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Notice: This assistant provides official government source guidelines and document organization. It does not provide legal counsel, guarantee work authorization, or submit automated filings.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    color = Color(0xFF94A3B8)
                )
            )
        }

        // Unified Navigation Tabs
        ModuleTabBar(
            tabs = tabs,
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        // Main Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> OverviewTab(
                    profile = profile,
                    passportAudit = passportAudit,
                    permitAudit = permitAudit,
                    reminders = reminders,
                    onDismissReminder = { viewModel.dismissImmigrationReminder(it) },
                    onNavigateTab = { selectedTab = it },
                    onEditProfile = { showEditProfileDialog = true }
                )
                1 -> ImmigrationProfileTab(
                    profile = profile,
                    onEdit = { showEditProfileDialog = true }
                )
                2 -> SourceRegistryTab(
                    sources = sources,
                    onSelectSource = { selectedSourceForDetail = it }
                )
                3 -> CityPoliciesTab(
                    policies = cityPolicies,
                    onAddPolicy = { showAddPolicyDialog = true }
                )
                4 -> PostGraduationOptionsTab()
                5 -> WorkAuthorizationTab()
                6 -> GraduationChecklistTab(
                    items = checklist,
                    onToggle = { id, done -> viewModel.toggleGraduationChecklistItem(id, done) },
                    onDelete = { viewModel.deleteGraduationChecklistItem(it) },
                    onAdd = { showAddChecklistDialog = true }
                )
                7 -> DocumentsOrganizerTab(
                    documents = documents,
                    onUpdateStatus = { id, status -> viewModel.updateImmigrationDocumentStatus(id, status) },
                    onDelete = { viewModel.deleteImmigrationDocument(it) },
                    onAdd = { showAddDocDialog = true }
                )
                8 -> CareerResearchBridgeTab(
                    bridge = bridge,
                    jobs = jobPostings,
                    analysis = jobAnalysis,
                    onAnalyzeJob = { viewModel.analyzeJobImmigration(it) },
                    onClearJobAnalysis = { viewModel.clearJobImmigrationAnalysis() }
                )
                9 -> OfficialQATab(
                    answer = qaAnswer,
                    onAsk = { viewModel.askImmigrationAssistant(it) },
                    onClear = { viewModel.clearImmigrationAnswer() }
                )
                10 -> OfficialContactsTab(contacts = contacts)
            }
        }
    }

    // Dialogs
    if (showEditProfileDialog) {
        EditImmigrationProfileDialog(
            currentProfile = profile,
            onDismiss = { showEditProfileDialog = false },
            onSave = {
                viewModel.updateImmigrationProfile(it)
                showEditProfileDialog = false
            }
        )
    }

    if (selectedSourceForDetail != null) {
        OfficialSourceViewerDialog(
            source = selectedSourceForDetail!!,
            onDismiss = { selectedSourceForDetail = null }
        )
    }

    if (showAddPolicyDialog) {
        AddCityPolicyDialog(
            onDismiss = { showAddPolicyDialog = false },
            onAdd = {
                viewModel.addCityPolicy(it)
                showAddPolicyDialog = false
            }
        )
    }

    if (showAddChecklistDialog) {
        AddGraduationChecklistDialog(
            onDismiss = { showAddChecklistDialog = false },
            onAdd = {
                viewModel.addGraduationChecklistItem(it)
                showAddChecklistDialog = false
            }
        )
    }

    if (showAddDocDialog) {
        AddImmigrationDocumentDialog(
            onDismiss = { showAddDocDialog = false },
            onAdd = {
                viewModel.addImmigrationDocument(it)
                showAddDocDialog = false
            }
        )
    }
}

// ========================================================
// 1. OVERVIEW & EXPIRY TRACKERS TAB
// ========================================================

@Composable
fun OverviewTab(
    profile: ImmigrationProfileEntity?,
    passportAudit: ImmigrationIntelligenceEngine.ExpirationAudit,
    permitAudit: ImmigrationIntelligenceEngine.ExpirationAudit,
    reminders: List<ImmigrationReminderEntity>,
    onDismissReminder: (Long) -> Unit,
    onNavigateTab: (Int) -> Unit,
    onEditProfile: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Transition Pipeline Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "International Student Career & Legal Transition Pipeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val steps = listOf(
                        "1. Student in China" to "Active study residence permit (学习类)",
                        "2. Graduation" to "Degree conferral & university clearance",
                        "3. Post-Grad Options" to "Direct work, departure, startup, or Ph.D.",
                        "4. Job & Employer" to "Qualified employer sponsorship & contract",
                        "5. Work Permit" to "SAFEA Notification Letter & Permit Card",
                        "6. Work Residence" to "PSB Work-Type Residence Permit (工作类)"
                    )
                    steps.forEach { (step, sub) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(DarkPrimary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = ScholarGoldLight
                                    )
                                )
                                Text(
                                    text = sub,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Expiration Trackers (Passport & Residence Permit)
        item {
            SectionHeader(title = "Document Validity & Personal Reminders")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Residence Permit Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (permitAudit.urgencyLevel == "Critical") Color(0x33EF4444) else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Residence Permit", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            val badgeColor = when (permitAudit.urgencyLevel) {
                                "Critical" -> Color.Red
                                "Warning" -> ScholarGold
                                else -> ScholarCyan
                            }
                            Text(permitAudit.urgencyLevel, color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = permitAudit.expirationDate,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                        )
                        if (permitAudit.daysRemaining != null) {
                            Text(
                                text = "${permitAudit.daysRemaining} days remaining",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (permitAudit.daysRemaining!! < 30) Color.Red else Color.White,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = permitAudit.recommendation,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFF94A3B8))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Personal reminder only", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = ScholarGold))
                    }
                }

                // Passport Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Passport Expiry", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            Text(passportAudit.urgencyLevel, color = ScholarCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = passportAudit.expirationDate,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                        )
                        if (passportAudit.daysRemaining != null) {
                            Text(
                                text = "${passportAudit.daysRemaining} days remaining",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.sp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = passportAudit.recommendation,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFF94A3B8))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Privacy rule: numbers optional", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = ScholarCyan))
                    }
                }
            }
        }

        // Active Personal Reminders
        if (reminders.isNotEmpty()) {
            item {
                SectionHeader(title = "Scheduled Deadlines & Actions")
            }
            items(reminders) { reminder ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(reminder.title, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
                            Text("Target Date: ${reminder.dueDate}", style = MaterialTheme.typography.bodySmall, color = ScholarCyan)
                            Text(reminder.label, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = Color(0xFFCBD5E1))
                        }
                        IconButton(onClick = { onDismissReminder(reminder.id) }) {
                            Icon(Icons.Default.Check, contentDescription = "Dismiss", tint = DarkPrimary)
                        }
                    }
                }
            }
        }

        // Quick Navigation Tiles
        item {
            SectionHeader(title = "Core Assistant Sections")
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onNavigateTab(3) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                ) {
                    Icon(Icons.Default.LocationCity, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("City Policies & Qinhuangdao / Hebei Support")
                }
                Button(
                    onClick = { onNavigateTab(6) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Checklist, contentDescription = null, tint = ScholarCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Graduation Checklist & Document Organizer", color = Color.White)
                }
                Button(
                    onClick = { onNavigateTab(8) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.AltRoute, contentDescription = null, tint = ScholarGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Research & Career → China Work Bridge", color = Color.White)
                }
            }
        }
    }
}

// ========================================================
// 2. IMMIGRATION PROFILE TAB
// ========================================================

@Composable
fun ImmigrationProfileTab(
    profile: ImmigrationProfileEntity?,
    onEdit: () -> Unit
) {
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
                Column {
                    Text(
                        text = "User Immigration Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Stores only explicitly provided user data. Missing fields display 'Not provided'.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileFieldRow("Nationality", profile?.nationality ?: "Not provided")
                    ProfileFieldRow("Current University", profile?.currentUniversity ?: "Not provided")
                    ProfileFieldRow("University Location", profile?.universityCityProvince ?: "Not provided")
                    ProfileFieldRow("Degree Level", profile?.degreeLevel ?: "Not provided")
                    ProfileFieldRow("Major", profile?.major ?: "Not provided")
                    ProfileFieldRow("Student Status", profile?.currentStudentStatus ?: "Not provided")
                    ProfileFieldRow("Expected Graduation", profile?.expectedGraduationDate ?: "Not provided")

                    HorizontalDivider(color = Color(0x33FFFFFF))

                    ProfileFieldRow("Current Visa Category", profile?.currentVisaCategory ?: "Not provided")
                    ProfileFieldRow("Residence Permit Type", profile?.currentResidencePermitType ?: "Not provided")
                    ProfileFieldRow("Residence Permit Expiry", profile?.residencePermitExpirationDate ?: "Not provided")
                    ProfileFieldRow("Passport Expiry", profile?.passportExpirationDate ?: "Not provided")
                    ProfileFieldRow("Passport Number (Optional)", profile?.passportNumberOptional ?: "Not provided")

                    HorizontalDivider(color = Color(0x33FFFFFF))

                    ProfileFieldRow("Target Employment City", profile?.targetEmploymentCity ?: "Not provided")
                    ProfileFieldRow("Target Job Field", profile?.targetJobField ?: "Not provided")
                    ProfileFieldRow("Target Employer Type", profile?.targetEmployerType ?: "Not provided")
                    ProfileFieldRow("Intends to Remain in China", profile?.intendsToRemainInChina ?: "Not provided")
                    ProfileFieldRow("Considering Entrepreneurship", profile?.isConsideringEntrepreneurship ?: "Not provided")
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x2238BDF8), shape = RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "Data Isolation & Privacy Guarantee",
                        fontWeight = FontWeight.Bold,
                        color = ScholarCyan,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "All immigration profile parameters are isolated strictly to your authenticated session. The system will never infer or assign a legal nationality or visa status without explicit input.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFFE2E8F0))
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileFieldRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (value == "Not provided") FontWeight.Normal else FontWeight.Bold,
                color = if (value == "Not provided") Color(0xFF64748B) else Color.White
            )
        )
    }
}

// ========================================================
// 3. OFFICIAL SOURCE REGISTRY TAB
// ========================================================

@Composable
fun SourceRegistryTab(
    sources: List<OfficialSourceEntity>,
    onSelectSource: (OfficialSourceEntity) -> Unit
) {
    var selectedJurisdiction by remember { mutableStateOf("ALL") }
    val jurisdictions = listOf("ALL", "National", "Shanghai", "Beijing", "Qinhuangdao / Hebei")

    val filtered = if (selectedJurisdiction == "ALL") {
        sources
    } else {
        sources.filter { it.jurisdiction.contains(selectedJurisdiction, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Structured Official-Source Registry",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Prioritizes National Immigration Administration, State Council, MOHRSS, and official provincial/university portals. Random blogs are strictly prohibited.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(jurisdictions) { jur ->
                    FilterChip(
                        selected = selectedJurisdiction == jur,
                        onClick = { selectedJurisdiction = jur },
                        label = { Text(jur, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filtered) { source ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectSource(source) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = source.jurisdiction,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ScholarCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        FactBadge(type = source.status)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = source.sourceTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = source.organization,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ScholarGoldLight,
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = source.summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        ),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Checked: ${source.lastCheckedDate} • Pub: ${source.publicationDate}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = "View Source →",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ScholarCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

// ========================================================
// 4. CITY POLICIES TAB & QINHUANGDAO / HEBEI
// ========================================================

@Composable
fun CityPoliciesTab(
    policies: List<CityImmigrationPolicyEntity>,
    onAddPolicy: () -> Unit
) {
    var selectedCity by remember { mutableStateOf("ALL") }
    val cities = listOf("ALL", "Shanghai", "Beijing", "Qinhuangdao", "Shenzhen", "National")

    val filtered = if (selectedCity == "ALL") {
        policies
    } else {
        policies.filter { it.city.equals(selectedCity, ignoreCase = true) }
    }

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
                Column {
                    Text(
                        text = "Foreign Graduate City Policy Database",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Strict rule: Never apply one city's policy to another. Shanghai policies are labeled Shanghai-specific.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                IconButton(onClick = onAddPolicy) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Policy", tint = ScholarCyan)
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(cities) { city ->
                    FilterChip(
                        selected = selectedCity == city,
                        onClick = { selectedCity = city },
                        label = { Text(city, fontSize = 11.sp) }
                    )
                }
            }
        }

        // Dedicated Qinhuangdao / Hebei Support Card
        if (selectedCity == "ALL" || selectedCity == "Qinhuangdao") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x3310B981))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Yanshan University (Qinhuangdao, Hebei) Local Guideline",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• Residence Permit Extensions: Submit materials to YSU College of International Exchange at least 30 days prior to expiration.\n• Local Authority: Qinhuangdao PSB Exit-Entry Administration (Haigang District).\n• Work Transition: If seeking post-grad employment in tier-1 hubs (Shanghai/Beijing), obtain departure clearance and transition to target city's SAFEA work permit sponsorship.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), fontSize = 11.sp)
                        )
                    }
                }
            }
        }

        items(filtered) { policy ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (policy.isLocalSpecific) "${policy.city} Specific" else "National Standard",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (policy.isLocalSpecific) ScholarGold else ScholarCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        FactBadge(type = policy.verificationStatus)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = policy.policyTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Category: ${policy.policyCategory} • Authority: ${policy.applicationAuthority}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Eligibility: ${policy.eligibility}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Requirements: ${policy.requirements}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.5.sp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Required Docs: ${policy.requiredDocuments}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x33000000), shape = RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Caveat: ${policy.importantUncertaintyWarning}\nSource: ${policy.sourceOrganization} (${policy.lastVerifiedDate})",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontSize = 10.sp)
                        )
                    }
                }
            }
        }
    }
}

// ========================================================
// 5. POST-GRADUATION OPTIONS TAB (UNRANKED INFORMATIONAL)
// ========================================================

@Composable
fun PostGraduationOptionsTab() {
    val options = remember { ImmigrationIntelligenceEngine.getPostGraduationOptions() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Post-Graduation Pathways (Informational Comparison)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Official comparison of options available to foreign graduates in China. These pathways are unranked; applicability depends on individual circumstances and destination policies.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        items(options) { opt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt.code,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ScholarGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        FactBadge(type = opt.responsibleAuthority)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = opt.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = opt.generalDescription,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Potential Requirements:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = ScholarCyan
                        )
                    )
                    opt.potentialRequirements.forEach { req ->
                        Text("• $req", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), fontSize = 11.sp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Common Documents: ${opt.documentsCommonlyMentioned.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 10.5.sp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Official Source: ${opt.officialSource} • Checked: ${opt.dateChecked}",
                        style = MaterialTheme.typography.labelSmall.copy(color = ScholarGoldLight, fontSize = 10.sp)
                    )
                    Text(
                        text = "Note: ${opt.notes}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFEF4444), fontSize = 10.sp)
                    )
                }
            }
        }
    }
}

// ========================================================
// 6. WORK AUTHORIZATION & STUDENT RESTRICTIONS TAB
// ========================================================

@Composable
fun WorkAuthorizationTab() {
    val workflowSteps = remember { ImmigrationIntelligenceEngine.getWorkPermitWorkflowSteps() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Warning Banner: Student Work Restrictions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x33EF4444))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Important Legal Warning: Student Work Restrictions",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A student residence permit does NOT authorize unrestricted employment. Off-campus internships are only permitted under Article 30 of Ministry of Education Decree No. 42 with prior written university approval and an official endorsement ('Internship') stamped on the residence permit by the Public Security Exit-Entry Administration. Working without this endorsement is classified as illegal employment under Article 43 of the Exit-Entry Law.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFFCA5A5), fontSize = 11.sp)
                    )
                }
            }
        }

        // Employer Work Permit Workflow
        item {
            SectionHeader(
                title = "Employer-Side Work Permit Workflow",
                subtitle = "Responsibilities across Applicant, Employer, University, and Government Authorities"
            )
        }

        items(workflowSteps) { step ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Step ${step.stepNumber}: ${step.stageName}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ScholarGold
                            )
                        )
                        FactBadge(type = step.estimatedTimeline)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Responsible: ${step.responsibleParty}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ScholarCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Required Documents: ${step.documentsRequired.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF94A3B8),
                            fontSize = 10.5.sp
                        )
                    )
                }
            }
        }
    }
}

// ========================================================
// 7. GRADUATION CHECKLIST TAB
// ========================================================

@Composable
fun GraduationChecklistTab(
    items: List<GraduationChecklistItemEntity>,
    onToggle: (Long, Boolean) -> Unit,
    onDelete: (GraduationChecklistItemEntity) -> Unit,
    onAdd: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    val categories = listOf("ALL", "University", "Academic", "Documents", "Residence status", "Employment")

    val filtered = if (selectedCategory == "ALL") {
        items
    } else {
        items.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

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
                Column {
                    Text(
                        text = "Graduation Transition Checklist",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Track university departure, academic defense, documents, and residence transitions.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                IconButton(onClick = onAdd) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Task", tint = DarkPrimary)
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }
        }

        items(filtered) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else ScholarNavySurface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = item.isCompleted,
                        onCheckedChange = { onToggle(item.id, it) },
                        colors = CheckboxDefaults.colors(checkedColor = DarkPrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ScholarCyan,
                                    fontSize = 10.sp
                                )
                            )
                            if (item.deadline.isNotBlank()) {
                                Text(
                                    text = "Due: ${item.deadline}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ScholarGold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (item.isCompleted) Color(0xFF94A3B8) else Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Responsible: ${item.responsibleParty}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        )
                    }
                    if (item.isCustom) {
                        IconButton(onClick = { onDelete(item) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 8. DOCUMENTS ORGANIZER TAB
// ========================================================

@Composable
fun DocumentsOrganizerTab(
    documents: List<ImmigrationDocumentEntity>,
    onUpdateStatus: (Long, String) -> Unit,
    onDelete: (ImmigrationDocumentEntity) -> Unit,
    onAdd: () -> Unit
) {
    val statuses = listOf("Obtained / Ready", "In Progress", "Not Obtained", "Expired")

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
                Column {
                    Text(
                        text = "Immigration Document Organizer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Notice: Requirement depends on application category and location.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                IconButton(onClick = onAdd) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Document", tint = DarkPrimary)
                }
            }
        }

        items(documents) { doc ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = doc.documentName,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        val badgeColor = when (doc.status) {
                            "Obtained / Ready" -> DarkPrimary
                            "In Progress" -> ScholarGold
                            "Expired" -> Color.Red
                            else -> Color(0xFF64748B)
                        }
                        FactBadge(type = doc.status)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Required for: ${doc.requiredFor}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ScholarCyan,
                            fontSize = 11.5.sp
                        )
                    )
                    if (doc.expirationDate.isNotBlank()) {
                        Text(
                            text = "Validity: ${doc.expirationDate}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        )
                    }
                    if (doc.notes.isNotBlank()) {
                        Text(
                            text = "Notes: ${doc.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Status change row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        statuses.forEach { st ->
                            OutlinedButton(
                                onClick = { onUpdateStatus(doc.id, st) },
                                modifier = Modifier.weight(1f).height(28.dp),
                                contentPadding = PaddingValues(2.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (doc.status == st) DarkPrimary.copy(alpha = 0.2f) else Color.Transparent
                                )
                            ) {
                                Text(st.split(" ").first(), fontSize = 9.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========================================================
// 9. CAREER & RESEARCH BRIDGE TAB
// ========================================================

@Composable
fun CareerResearchBridgeTab(
    bridge: ImmigrationIntelligenceEngine.ResearchCareerChinaBridge,
    jobs: List<JobPostingEntity>,
    analysis: ImmigrationIntelligenceEngine.JobImmigrationAnalysis?,
    onAnalyzeJob: (JobPostingEntity) -> Unit,
    onClearJobAnalysis: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Research -> Career -> China Bridge Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Research Lab → Career Target → China Work Bridge",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Research: ${bridge.researchProjectTitle}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ScholarCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "Target Role: ${bridge.targetRole} (${bridge.targetCity})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ScholarGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = bridge.industryRelevanceInChina,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "High-Tech Work Permit Policy Alignments:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    bridge.highTechPolicyAlignments.forEach { align ->
                        Text("• $align", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), fontSize = 11.sp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Permit Tier: ${bridge.visaWorkPermitCategory}",
                        style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontSize = 11.sp)
                    )
                    Text(
                        text = "Source Citation: ${bridge.officialSourceCitation}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8), fontSize = 10.sp)
                    )
                }
            }
        }

        // Job Immigration Analyzer
        item {
            SectionHeader(
                title = "Job Immigration Consideration Analyzer",
                subtitle = "Cross-reference Stage 4 Career jobs against municipal foreign graduate work pathways"
            )
        }

        if (analysis != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Analysis: ${analysis.jobTitle} @ ${analysis.company}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                            )
                            IconButton(onClick = onClearJobAnalysis) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(
                            text = "Pathway: ${analysis.applicablePathwayTitle} (${analysis.pathwayJurisdiction})",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Work Authorization Considerations:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        analysis.workAuthorizationConsiderations.forEach { c ->
                            Text("• $c", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Employer Responsibilities: ${analysis.employerResponsibilities.joinToString("; ")}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 10.5.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Source: ${analysis.verifiedOfficialSource}",
                            style = MaterialTheme.typography.labelSmall.copy(color = ScholarCyan, fontSize = 10.sp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = analysis.disclaimer,
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFEF4444), fontSize = 9.5.sp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Select a tracked Career posting to analyze:",
                style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF94A3B8))
            )
        }

        items(jobs) { job ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnalyzeJob(job) },
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(job.jobTitle, fontWeight = FontWeight.SemiBold, color = Color.White, fontSize = 13.sp)
                        Text("${job.company} • ${job.location}", style = MaterialTheme.typography.bodySmall, color = ScholarCyan)
                    }
                    Text(
                        text = "Analyze Pathway →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = DarkPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

// ========================================================
// 10. OFFICIAL Q&A ASSISTANT TAB (SOURCE-FIRST)
// ========================================================

@Composable
fun OfficialQATab(
    answer: ImmigrationIntelligenceEngine.StructuredImmigrationAnswer?,
    onAsk: (String) -> Unit,
    onClear: () -> Unit
) {
    var queryInput by remember { mutableStateOf("") }
    val sampleQueries = listOf(
        "Can international students work off-campus or take internships in China?",
        "Can a master's graduate directly work in Shanghai without two years work experience?",
        "What are the rules regarding 24-hour accommodation registration for foreigners?",
        "How does the Foreigner Work Permit Tier System (Category A, B, C) operate?",
        "What should an international student in Qinhuangdao do when graduating from YSU?"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Official Source-First Q&A Assistant",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Answers cite verified government sources, jurisdictions, and dates. If unverified, the assistant will explicitly decline to fabricate advice.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = queryInput,
                    onValueChange = { queryInput = it },
                    placeholder = { Text("Ask about visas, work permits, internships...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (queryInput.isNotBlank()) {
                            onAsk(queryInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                ) {
                    Text("Ask")
                }
            }
        }

        item {
            Text("Sample verified queries:", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                sampleQueries.forEach { sq ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                queryInput = sq
                                onAsk(sq)
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "• $sq",
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, color = Color(0xFFCBD5E1))
                        )
                    }
                }
            }
        }

        if (answer != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Official Response", fontWeight = FontWeight.Bold, color = ScholarCyan)
                            IconButton(onClick = onClear) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = answer.question,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = answer.shortAnswer,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Applies To: ${answer.appliesTo}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Conditions & Rules:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                        )
                        answer.conditions.forEach { cond ->
                            Text("• $cond", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0), fontSize = 11.sp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Common Documents: ${answer.requiredDocuments.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8), fontSize = 10.5.sp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Responsible Authority: ${answer.responsibleAuthority}",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarGoldLight, fontSize = 11.sp)
                        )
                        Text(
                            text = "Location / Jurisdiction: ${answer.locationOrJurisdiction}",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontSize = 11.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x33000000), shape = RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Source Citation:",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold)
                                )
                                Text(
                                    text = "${answer.officialSourceTitle} • ${answer.sourceOrganization}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color.White)
                                )
                                Text(
                                    text = "Checked: ${answer.dateChecked} • Freshness: ${answer.freshnessStatus} • Pub: ${answer.publicationDate}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFF94A3B8))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Important Uncertainty Warning: ${answer.importantUncertaintyWarning}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, color = Color(0xFFFCA5A5))
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
// 11. OFFICIAL CONTACTS TAB
// ========================================================

@Composable
fun OfficialContactsTab(contacts: List<OfficialContactEntity>) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Verified Official Contact Directory",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Official administrative bodies, 12367 hotline, and university international offices. Contact details are verified directly from government directories.",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        items(contacts) { contact ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = contact.location,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ScholarCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        FactBadge(type = contact.category)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = contact.organization,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Purpose: ${contact.purpose}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Phone / Hotline: ${contact.officialPhoneOrEmail}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ScholarGold,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "Website: ${contact.officialWebsite}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = DarkPrimary,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contact.officialWebsite))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            },
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Official Website", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
