package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.data.model.UserProfileEntity
import com.example.ui.components.FactBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScholarViewModel

@Composable
fun ProfileScreen(viewModel: ScholarViewModel) {
    val currentProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }

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

    LaunchedEffect(currentProfile) {
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
                    title = "Scholar Identity Profile",
                    subtitle = "Grounds AI Mentor and career recommendations"
                )
                Button(
                    onClick = {
                        if (isEditing) {
                            // Save
                            val updated = UserProfileEntity(
                                id = 1,
                                name = name,
                                nationality = nationality,
                                university = university,
                                department = department,
                                degree = degree,
                                currentSemester = currentSemester,
                                expectedGraduation = expectedGraduation,
                                researchInterests = researchInterests,
                                technicalSkills = technicalSkills,
                                programmingLanguages = programmingLanguages,
                                aiMlSkills = aiMlSkills,
                                cvSkills = cvSkills,
                                researchExperience = researchExperience,
                                publications = publications,
                                projects = projects,
                                githubUrl = githubUrl,
                                certifications = certifications,
                                chineseProficiency = chineseProficiency,
                                englishProficiency = englishProficiency,
                                careerGoals = careerGoals,
                                targetIndustries = targetIndustries,
                                targetCountries = targetCountries,
                                targetCompanies = targetCompanies,
                                targetUniversities = targetUniversities,
                                targetVenues = targetVenues,
                                currentAcademicTasks = currentAcademicTasks,
                                currentResearchProjects = currentResearchProjects
                            )
                            viewModel.updateProfile(updated)
                            isEditing = false
                        } else {
                            isEditing = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) ScholarGreen else DarkPrimary,
                        contentColor = DarkOnPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isEditing) "Save Profile" else "Edit Profile")
                }
            }
        }

        // Profile Fields
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("1. BASIC & ACADEMIC STANDING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarCyan))
                    ProfileField("Full Name", name, isEditing) { name = it }
                    ProfileField("Nationality", nationality, isEditing) { nationality = it }
                    ProfileField("University", university, isEditing) { university = it }
                    ProfileField("Department", department, isEditing) { department = it }
                    ProfileField("Degree Program", degree, isEditing) { degree = it }
                    ProfileField("Current Semester / Year", currentSemester, isEditing) { currentSemester = it }
                    ProfileField("Expected Graduation Date", expectedGraduation, isEditing) { expectedGraduation = it }
                }
            }
        }

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

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("3. LANGUAGES & CAREER OBJECTIVES", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = ScholarPurple))
                    ProfileField("Chinese Proficiency (HSK / Oral)", chineseProficiency, isEditing) { chineseProficiency = it }
                    ProfileField("English Proficiency (IELTS / TOEFL)", englishProficiency, isEditing) { englishProficiency = it }
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
fun ProfileField(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    if (isEditing) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth()
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
