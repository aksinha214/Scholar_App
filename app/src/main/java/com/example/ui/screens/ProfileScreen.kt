package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserProfileEntity
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun ProfileScreen(viewModel: ScholarViewModel) {
    val currentProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val userEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()
    val studentId by viewModel.currentStudentId.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State holders initialized from profile
    var name by remember { mutableStateOf("") }
    var nationality by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var currentSemester by remember { mutableStateOf("") }
    var expectedGraduation by remember { mutableStateOf("") }
    var researchInterests by remember { mutableStateOf("") }
    var technicalSkills by remember { mutableStateOf("") }
    var programmingLanguages by remember { mutableStateOf("") }
    var aiMlSkills by remember { mutableStateOf("") }
    var cvSkills by remember { mutableStateOf("") }
    var researchExperience by remember { mutableStateOf("") }
    var publications by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf("") }
    var githubUrl by remember { mutableStateOf("") }
    var certifications by remember { mutableStateOf("") }
    var chineseProficiency by remember { mutableStateOf("") }
    var englishProficiency by remember { mutableStateOf("") }
    var careerGoals by remember { mutableStateOf("") }
    var targetIndustries by remember { mutableStateOf("") }
    var targetCountries by remember { mutableStateOf("") }
    var targetCompanies by remember { mutableStateOf("") }
    var targetUniversities by remember { mutableStateOf("") }
    var targetVenues by remember { mutableStateOf("") }
    var currentAcademicTasks by remember { mutableStateOf("") }
    var currentResearchProjects by remember { mutableStateOf("") }

    // Only update local editing state if not actively editing
    LaunchedEffect(currentProfile) {
        if (!isEditing) {
            currentProfile?.let { p ->
                name = p.name
                nationality = p.nationality
                university = p.university
                department = p.department
                degree = p.degree
                currentSemester = p.currentSemester
                expectedGraduation = p.expectedGraduation
                researchInterests = p.researchInterests
                technicalSkills = p.technicalSkills
                programmingLanguages = p.programmingLanguages
                aiMlSkills = p.aiMlSkills
                cvSkills = p.cvSkills
                researchExperience = p.researchExperience
                publications = p.publications
                projects = p.projects
                githubUrl = p.githubUrl
                certifications = p.certifications
                chineseProficiency = p.chineseProficiency
                englishProficiency = p.englishProficiency
                careerGoals = p.careerGoals
                targetIndustries = p.targetIndustries
                targetCountries = p.targetCountries
                targetCompanies = p.targetCompanies
                targetUniversities = p.targetUniversities
                targetVenues = p.targetVenues
                currentAcademicTasks = p.currentAcademicTasks
                currentResearchProjects = p.currentResearchProjects
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Top Header with Save/Cancel or Edit buttons
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
                        text = "Scholar Identity Profile",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Grounds AI Mentor, Research Lab, and Career Center",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isEditing) {
                        OutlinedButton(
                            onClick = {
                                // Cancel edits and restore original values
                                currentProfile?.let { p ->
                                    name = p.name
                                    nationality = p.nationality
                                    university = p.university
                                    department = p.department
                                    degree = p.degree
                                    currentSemester = p.currentSemester
                                    expectedGraduation = p.expectedGraduation
                                    researchInterests = p.researchInterests
                                    technicalSkills = p.technicalSkills
                                    programmingLanguages = p.programmingLanguages
                                    aiMlSkills = p.aiMlSkills
                                    cvSkills = p.cvSkills
                                    researchExperience = p.researchExperience
                                    publications = p.publications
                                    projects = p.projects
                                    githubUrl = p.githubUrl
                                    certifications = p.certifications
                                    chineseProficiency = p.chineseProficiency
                                    englishProficiency = p.englishProficiency
                                    careerGoals = p.careerGoals
                                    targetIndustries = p.targetIndustries
                                    targetCountries = p.targetCountries
                                    targetCompanies = p.targetCompanies
                                    targetUniversities = p.targetUniversities
                                    targetVenues = p.targetVenues
                                    currentAcademicTasks = p.currentAcademicTasks
                                    currentResearchProjects = p.currentResearchProjects
                                }
                                errorMessage = null
                                isEditing = false
                            },
                            modifier = Modifier.testTag("cancel_edit_btn")
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = "Full Name cannot be empty."
                                    return@Button
                                }
                                val updated = UserProfileEntity(
                                    id = 1,
                                    name = name.trim(),
                                    nationality = nationality.trim(),
                                    university = university.trim(),
                                    department = department.trim(),
                                    degree = degree.trim(),
                                    currentSemester = currentSemester.trim(),
                                    expectedGraduation = expectedGraduation.trim(),
                                    researchInterests = researchInterests.trim(),
                                    technicalSkills = technicalSkills.trim(),
                                    programmingLanguages = programmingLanguages.trim(),
                                    aiMlSkills = aiMlSkills.trim(),
                                    cvSkills = cvSkills.trim(),
                                    researchExperience = researchExperience.trim(),
                                    publications = publications.trim(),
                                    projects = projects.trim(),
                                    githubUrl = githubUrl.trim(),
                                    certifications = certifications.trim(),
                                    chineseProficiency = chineseProficiency.trim(),
                                    englishProficiency = englishProficiency.trim(),
                                    careerGoals = careerGoals.trim(),
                                    targetIndustries = targetIndustries.trim(),
                                    targetCountries = targetCountries.trim(),
                                    targetCompanies = targetCompanies.trim(),
                                    targetUniversities = targetUniversities.trim(),
                                    targetVenues = targetVenues.trim(),
                                    currentAcademicTasks = currentAcademicTasks.trim(),
                                    currentResearchProjects = currentResearchProjects.trim()
                                )
                                viewModel.updateProfile(updated)
                                errorMessage = null
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ScholarGreen,
                                contentColor = DarkOnPrimary
                            ),
                            modifier = Modifier.testTag("save_profile_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Profile")
                        }
                    } else {
                        Button(
                            onClick = { isEditing = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkPrimary,
                                contentColor = DarkOnPrimary
                            ),
                            modifier = Modifier.testTag("edit_profile_btn")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile")
                        }
                    }
                }
            }
        }

        // Error message banner if validation fails
        if (errorMessage != null) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onErrorContainer)
                        )
                    }
                }
            }
        }

        // Active Account & Credentials Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ScholarNavySurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ScholarCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (name.take(1).ifBlank { "S" }).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = ScholarCyan)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name.ifBlank { "Scholar Profile" },
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = if (userEmail.isNotBlank()) "Account: $userEmail" else "University Account Active",
                            style = MaterialTheme.typography.bodySmall.copy(color = ScholarCyan, fontSize = 11.sp)
                        )
                        if (studentId.isNotBlank()) {
                            Text(
                                text = "Student ID: $studentId",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1), fontSize = 10.5.sp)
                            )
                        }
                    }
                    FactBadge(if (isEditing) "EDITING" else "LOCAL")
                }
            }
        }

        // 1. BASIC & ACADEMIC STANDING
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("1. BASIC & ACADEMIC STANDING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
                    ProfileField("Full Name *", name, isEditing, testTag = "profile_name_input") { name = it }

                    // Nationality with quick suggestions
                    ProfileField("Nationality", nationality, isEditing) { nationality = it }
                    if (isEditing) {
                        QuickPresetsRow(
                            presets = listOf("Kazakhstan", "Russia", "Pakistan", "India", "Nigeria", "Bangladesh", "Uzbekistan", "Germany", "Other"),
                            selected = nationality,
                            onSelect = { nationality = it }
                        )
                    }

                    ProfileField("University", university, isEditing) { university = it }
                    ProfileField("Department", department, isEditing) { department = it }

                    // Degree program with quick presets
                    ProfileField("Degree Program", degree, isEditing) { degree = it }
                    if (isEditing) {
                        QuickPresetsRow(
                            presets = listOf("Bachelor of Engineering in CS & Technology", "Master of Science in Computer Science", "PhD in Computer Science & Technology", "Software Engineering"),
                            selected = degree,
                            onSelect = { degree = it }
                        )
                    }

                    // Semester with quick presets
                    ProfileField("Current Semester / Year", currentSemester, isEditing) { currentSemester = it }
                    if (isEditing) {
                        QuickPresetsRow(
                            presets = listOf("Year 1, Semester 1", "Year 2, Semester 1", "Year 3, Semester 1 (Fall 2026)", "Year 4, Semester 1", "Graduate Year 1", "Graduate Year 2"),
                            selected = currentSemester,
                            onSelect = { currentSemester = it }
                        )
                    }

                    ProfileField("Expected Graduation Date", expectedGraduation, isEditing) { expectedGraduation = it }
                    if (isEditing) {
                        QuickPresetsRow(
                            presets = listOf("June 2027", "June 2028", "January 2027", "June 2029"),
                            selected = expectedGraduation,
                            onSelect = { expectedGraduation = it }
                        )
                    }
                }
            }
        }

        // 2. RESEARCH & TECHNICAL MASTERY
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("2. RESEARCH & TECHNICAL MASTERY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarGold))
                    ProfileField("Research Interests", researchInterests, isEditing) { researchInterests = it }
                    ProfileField("Programming Languages", programmingLanguages, isEditing) { programmingLanguages = it }
                    ProfileField("Technical Frameworks & Tools", technicalSkills, isEditing) { technicalSkills = it }
                    ProfileField("AI / Machine Learning Skills", aiMlSkills, isEditing) { aiMlSkills = it }
                    ProfileField("Computer Vision Skills", cvSkills, isEditing) { cvSkills = it }
                    ProfileField("Research Experience & Lab", researchExperience, isEditing) { researchExperience = it }
                    ProfileField("Publications & Manuscripts", publications, isEditing) { publications = it }
                    ProfileField("Key Projects", projects, isEditing) { projects = it }
                    ProfileField("GitHub URL", githubUrl, isEditing) { githubUrl = it }
                    ProfileField("Certifications", certifications, isEditing) { certifications = it }
                }
            }
        }

        // 3. LANGUAGES & CAREER OBJECTIVES
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("3. LANGUAGES & CAREER OBJECTIVES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarPurple))

                    // Chinese proficiency with presets
                    ProfileField("Chinese Proficiency (HSK / Oral)", chineseProficiency, isEditing) { chineseProficiency = it }
                    if (isEditing) {
                        QuickPresetsRow(
                            presets = listOf("Beginner", "HSK 3", "HSK 4 (Certified)", "HSK 5 (Target)", "HSK 6", "Fluent / Native"),
                            selected = chineseProficiency,
                            onSelect = { chineseProficiency = it }
                        )
                    }

                    // English proficiency with presets
                    ProfileField("English Proficiency (IELTS / TOEFL)", englishProficiency, isEditing) { englishProficiency = it }
                    if (isEditing) {
                        QuickPresetsRow(
                            presets = listOf("Fluent / IELTS 7.5", "IELTS 7.0", "IELTS 6.5", "TOEFL 100+", "Working Proficiency", "Native Speaker"),
                            selected = englishProficiency,
                            onSelect = { englishProficiency = it }
                        )
                    }

                    ProfileField("Career Goals", careerGoals, isEditing) { careerGoals = it }
                    ProfileField("Target Industries", targetIndustries, isEditing) { targetIndustries = it }
                    ProfileField("Target Countries", targetCountries, isEditing) { targetCountries = it }
                    ProfileField("Target Companies / Labs", targetCompanies, isEditing) { targetCompanies = it }
                    ProfileField("Target Universities for Postgrad", targetUniversities, isEditing) { targetUniversities = it }
                    ProfileField("Target Journals / Conferences", targetVenues, isEditing) { targetVenues = it }
                    ProfileField("Active Academic Course Tasks", currentAcademicTasks, isEditing) { currentAcademicTasks = it }
                    ProfileField("Active Research Project", currentResearchProjects, isEditing) { currentResearchProjects = it }
                }
            }
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    isEditing: Boolean,
    testTag: String = "",
    onValueChange: (String) -> Unit
) {
    if (isEditing) {
        val modifier = if (testTag.isNotBlank()) Modifier.fillMaxWidth().testTag(testTag) else Modifier.fillMaxWidth()
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 11.sp) },
            modifier = modifier
        )
    } else {
        Column(modifier = Modifier.padding(vertical = 2.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp))
            Text(
                text = if (value.isNotBlank()) value else "Not specified",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}

@Composable
fun QuickPresetsRow(
    presets: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
    ) {
        items(presets) { preset ->
            val isSelected = selected.contains(preset, ignoreCase = true)
            SuggestionChip(
                onClick = { onSelect(preset) },
                label = { Text(preset, fontSize = 11.sp) },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (isSelected) ScholarCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                ),
                border = SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = if (isSelected) ScholarCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                )
            )
        }
    }
}
