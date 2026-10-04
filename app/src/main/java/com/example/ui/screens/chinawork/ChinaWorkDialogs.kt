package com.example.ui.screens.chinawork

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun EditImmigrationProfileDialog(
    currentProfile: ImmigrationProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (ImmigrationProfileEntity) -> Unit
) {
    var nationality by remember { mutableStateOf(currentProfile?.nationality ?: "Not provided") }
    var university by remember { mutableStateOf(currentProfile?.currentUniversity ?: "Not provided") }
    var cityProvince by remember { mutableStateOf(currentProfile?.universityCityProvince ?: "Not provided") }
    var degreeLevel by remember { mutableStateOf(currentProfile?.degreeLevel ?: "Not provided") }
    var major by remember { mutableStateOf(currentProfile?.major ?: "Not provided") }
    var studentStatus by remember { mutableStateOf(currentProfile?.currentStudentStatus ?: "Not provided") }
    var graduationDate by remember { mutableStateOf(currentProfile?.expectedGraduationDate ?: "Not provided") }
    var visaCategory by remember { mutableStateOf(currentProfile?.currentVisaCategory ?: "Not provided") }
    var permitType by remember { mutableStateOf(currentProfile?.currentResidencePermitType ?: "Not provided") }
    var permitExpiry by remember { mutableStateOf(currentProfile?.residencePermitExpirationDate ?: "Not provided") }
    var passportExpiry by remember { mutableStateOf(currentProfile?.passportExpirationDate ?: "Not provided") }
    var passportNumber by remember { mutableStateOf(currentProfile?.passportNumberOptional ?: "Not provided") }
    var targetCity by remember { mutableStateOf(currentProfile?.targetEmploymentCity ?: "Not provided") }
    var targetJobField by remember { mutableStateOf(currentProfile?.targetJobField ?: "Not provided") }
    var targetEmployerType by remember { mutableStateOf(currentProfile?.targetEmployerType ?: "Not provided") }
    var intendsToRemain by remember { mutableStateOf(currentProfile?.intendsToRemainInChina ?: "Not provided") }
    var entrepreneurship by remember { mutableStateOf(currentProfile?.isConsideringEntrepreneurship ?: "Not provided") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Edit Immigration Profile", fontWeight = FontWeight.Bold, color = ScholarCyan)
                Text(
                    "Store only explicitly provided information. Missing items display 'Not provided'.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = nationality,
                    onValueChange = { nationality = it },
                    label = { Text("Nationality") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = university,
                    onValueChange = { university = it },
                    label = { Text("Current University") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cityProvince,
                    onValueChange = { cityProvince = it },
                    label = { Text("University City / Province") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = degreeLevel,
                    onValueChange = { degreeLevel = it },
                    label = { Text("Degree Level (e.g. Master's, Ph.D.)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = major,
                    onValueChange = { major = it },
                    label = { Text("Major / Academic Field") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = studentStatus,
                    onValueChange = { studentStatus = it },
                    label = { Text("Current Student Status") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = graduationDate,
                    onValueChange = { graduationDate = it },
                    label = { Text("Expected Graduation Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = visaCategory,
                    onValueChange = { visaCategory = it },
                    label = { Text("Current Visa Category (e.g. X1, X2)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = permitType,
                    onValueChange = { permitType = it },
                    label = { Text("Residence Permit Type (e.g. Study 学习)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = permitExpiry,
                    onValueChange = { permitExpiry = it },
                    label = { Text("Residence Permit Expiration Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = passportExpiry,
                    onValueChange = { passportExpiry = it },
                    label = { Text("Passport Expiration Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = passportNumber,
                    onValueChange = { passportNumber = it },
                    label = { Text("Passport Number (Optional - Privacy Protected)") },
                    supportingText = { Text("Optional only. Leave as 'Not provided' if not needed.", fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetCity,
                    onValueChange = { targetCity = it },
                    label = { Text("Target Employment City (e.g. Shanghai, Beijing)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetJobField,
                    onValueChange = { targetJobField = it },
                    label = { Text("Target Job Field") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetEmployerType,
                    onValueChange = { targetEmployerType = it },
                    label = { Text("Target Employer Type (e.g. High-tech Firm)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = intendsToRemain,
                    onValueChange = { intendsToRemain = it },
                    label = { Text("Intends to Remain in China (Yes / No / Undecided)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = entrepreneurship,
                    onValueChange = { entrepreneurship = it },
                    label = { Text("Considering Entrepreneurship (Yes / No / Undecided)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updated = (currentProfile ?: ImmigrationProfileEntity()).copy(
                        nationality = nationality.ifBlank { "Not provided" },
                        currentUniversity = university.ifBlank { "Not provided" },
                        universityCityProvince = cityProvince.ifBlank { "Not provided" },
                        degreeLevel = degreeLevel.ifBlank { "Not provided" },
                        major = major.ifBlank { "Not provided" },
                        currentStudentStatus = studentStatus.ifBlank { "Not provided" },
                        expectedGraduationDate = graduationDate.ifBlank { "Not provided" },
                        currentVisaCategory = visaCategory.ifBlank { "Not provided" },
                        currentResidencePermitType = permitType.ifBlank { "Not provided" },
                        residencePermitExpirationDate = permitExpiry.ifBlank { "Not provided" },
                        passportExpirationDate = passportExpiry.ifBlank { "Not provided" },
                        passportNumberOptional = passportNumber.ifBlank { "Not provided" },
                        targetEmploymentCity = targetCity.ifBlank { "Not provided" },
                        targetJobField = targetJobField.ifBlank { "Not provided" },
                        targetEmployerType = targetEmployerType.ifBlank { "Not provided" },
                        intendsToRemainInChina = intendsToRemain.ifBlank { "Not provided" },
                        isConsideringEntrepreneurship = entrepreneurship.ifBlank { "Not provided" },
                        lastUpdated = System.currentTimeMillis()
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
            ) {
                Text("Save Profile")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OfficialSourceViewerDialog(
    source: OfficialSourceEntity,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = ScholarCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Official Source Record",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color.White
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = source.sourceTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = ScholarGold
                )

                Text(
                    text = "Organization: ${source.organization}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Jurisdiction: ${source.jurisdiction}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ScholarCyan
                    )
                    Text(
                        text = "Topic: ${source.topic}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = Color(0x33FFFFFF))

                Text(
                    text = "Publication / Update Date: ${source.publicationDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCBD5E1)
                )
                Text(
                    text = "Last Checked: ${source.lastCheckedDate} • Status: ${source.status}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ScholarCyan
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33000000), shape = RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "Summary:",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ScholarGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = source.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE2E8F0)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Key Statutory Excerpt:\n${source.contentSnippet}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Text(
                    text = "Official URL: ${source.url}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = DarkPrimary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
                colors = ButtonDefaults.buttonColors(containerColor = ScholarCyan)
            ) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Official Source", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AddCityPolicyDialog(
    onDismiss: () -> Unit,
    onAdd: (CityImmigrationPolicyEntity) -> Unit
) {
    var city by remember { mutableStateOf("") }
    var province by remember { mutableStateOf("") }
    var policyTitle by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Foreign Graduate Direct Employment") }
    var eligibility by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var documents by remember { mutableStateOf("") }
    var authority by remember { mutableStateOf("") }
    var sourceUrl by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var pubDate by remember { mutableStateOf("Publication/update date not available.") }
    var warning by remember { mutableStateOf("Policies can vary by district. Confirm with local immigration and HR bureau.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Verified City Policy", fontWeight = FontWeight.Bold, color = ScholarCyan)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 450.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City (e.g. Hangzhou, Tianjin)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = province,
                    onValueChange = { province = it },
                    label = { Text("Province / Municipality") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = policyTitle,
                    onValueChange = { policyTitle = it },
                    label = { Text("Policy Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = eligibility,
                    onValueChange = { eligibility = it },
                    label = { Text("Eligibility Criteria") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = requirements,
                    onValueChange = { requirements = it },
                    label = { Text("Requirements & Conditions") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = documents,
                    onValueChange = { documents = it },
                    label = { Text("Required Documents") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = authority,
                    onValueChange = { authority = it },
                    label = { Text("Application Authority (e.g. Municipal S&T Bureau)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = organization,
                    onValueChange = { organization = it },
                    label = { Text("Source Organization") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = sourceUrl,
                    onValueChange = { sourceUrl = it },
                    label = { Text("Official Source URL (Must be official government site)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pubDate,
                    onValueChange = { pubDate = it },
                    label = { Text("Publication / Update Date") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = warning,
                    onValueChange = { warning = it },
                    label = { Text("Important Local Caveats / Warnings") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (city.isNotBlank() && policyTitle.isNotBlank()) {
                        val policy = CityImmigrationPolicyEntity(
                            city = city.trim(),
                            provinceOrMunicipality = province.ifBlank { city },
                            policyTitle = policyTitle.trim(),
                            policyCategory = category,
                            eligibility = eligibility.ifBlank { "Foreign graduates with bachelor's or master's degrees." },
                            requirements = requirements.ifBlank { "Full-time employment offer in designated district." },
                            requiredDocuments = documents.ifBlank { "Degree, Contract, Non-criminal record, Physical exam." },
                            applicationAuthority = authority.ifBlank { "Local Exit-Entry & HR Bureau" },
                            officialSourceUrl = sourceUrl.ifBlank { "http://gov.cn" },
                            sourceOrganization = organization.ifBlank { "Municipal Government" },
                            publicationDate = pubDate,
                            lastVerifiedDate = "Oct 2026",
                            verificationStatus = "Verified recently",
                            isLocalSpecific = true,
                            importantUncertaintyWarning = warning
                        )
                        onAdd(policy)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
            ) {
                Text("Add Policy")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddGraduationChecklistDialog(
    onDismiss: () -> Unit,
    onAdd: (GraduationChecklistItemEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("University") }
    var responsibleParty by remember { mutableStateOf("Student") }
    var deadline by remember { mutableStateOf("") }

    val categories = listOf("University", "Academic", "Documents", "Residence status", "Career", "Employment", "China departure", "Post-graduation options")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Graduation Transition Task", fontWeight = FontWeight.Bold, color = ScholarCyan) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Category:", style = MaterialTheme.typography.labelMedium)
                Column {
                    categories.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            row.forEach { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = responsibleParty,
                    onValueChange = { responsibleParty = it },
                    label = { Text("Responsible Party (e.g. Student, Employer, University)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Deadline (e.g. June 2027)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val item = GraduationChecklistItemEntity(
                            category = category,
                            title = title.trim(),
                            description = description.trim(),
                            responsibleParty = responsibleParty.ifBlank { "Student" },
                            deadline = deadline.ifBlank { "Before graduation" },
                            isCompleted = false,
                            isCustom = true
                        )
                        onAdd(item)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
            ) { Text("Add Task") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddImmigrationDocumentDialog(
    onDismiss: () -> Unit,
    onAdd: (ImmigrationDocumentEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var requiredFor by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("In Progress") }
    var expirationDate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Track Immigration Document", fontWeight = FontWeight.Bold, color = ScholarCyan) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Document Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = requiredFor,
                    onValueChange = { requiredFor = it },
                    label = { Text("Required For (e.g. Work Permit, Study Extension)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = expirationDate,
                    onValueChange = { expirationDate = it },
                    label = { Text("Expiration Date (YYYY-MM-DD or 'Valid 6 mos')") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Issuing Authority / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val doc = ImmigrationDocumentEntity(
                            documentName = name.trim(),
                            requiredFor = requiredFor.ifBlank { "Immigration / Work Authorization" },
                            status = status,
                            expirationDate = expirationDate,
                            notes = notes
                        )
                        onAdd(doc)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
            ) { Text("Save Document") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
