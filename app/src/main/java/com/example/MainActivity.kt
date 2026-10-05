package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.DarkPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ScholarCyan
import com.example.ui.theme.ScholarGold
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.ScholarViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: ScholarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleWidgetNavigation(intent)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val isAuthenticated by viewModel.isAuthenticated.collectAsStateWithLifecycle()
                val toastMsg by viewModel.toastEvent.collectAsStateWithLifecycle()
                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(toastMsg) {
                    toastMsg?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearToast()
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (!isAuthenticated) {
                            AuthScreen(viewModel)
                        } else {
                            MainAppShell(viewModel)
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleWidgetNavigation(intent)
    }

    private fun handleWidgetNavigation(intent: Intent?) {
        if (intent == null) return
        when (intent.getStringExtra("NAV_TARGET")) {
            "command_center" -> viewModel.navigateTo(com.example.ui.viewmodel.AppScreen.COMMAND_CENTER)
            "university" -> viewModel.navigateTo(com.example.ui.viewmodel.AppScreen.UNIVERSITY)
            "research" -> viewModel.navigateTo(com.example.ui.viewmodel.AppScreen.RESEARCH_LAB)
            "chinese" -> viewModel.navigateTo(com.example.ui.viewmodel.AppScreen.CHINESE_LANGUAGE)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppShell(viewModel: ScholarViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    var showModuleSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Handle back button: return to DASHBOARD if on any other screen
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateTo(AppScreen.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CS Scholar OS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = currentScreen.title,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ScholarCyan,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    if (currentScreen != AppScreen.DASHBOARD) {
                        IconButton(onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Dashboard")
                        }
                    } else {
                        IconButton(onClick = { showModuleSheet = true }) {
                            Icon(Icons.Default.Menu, contentDescription = "Modules Menu", tint = DarkPrimary)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showModuleSheet = true }) {
                        Icon(Icons.Default.Apps, contentDescription = "All 12 Modules", tint = ScholarCyan)
                    }
                    IconButton(onClick = { viewModel.navigateTo(AppScreen.PROFILE) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = ScholarGold)
                    }
                    IconButton(onClick = { viewModel.logout() }) {
                        Icon(Icons.Default.Logout, contentDescription = "Sign Out", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val primaryScreens = listOf(
                    AppScreen.DASHBOARD to Icons.Default.Dashboard,
                    AppScreen.AI_MENTOR to Icons.Default.Psychology,
                    AppScreen.UNIVERSITY to Icons.Default.School,
                    AppScreen.RESEARCH_LAB to Icons.Default.Science,
                    AppScreen.CS_SKILLS to Icons.Default.Terminal
                )

                primaryScreens.forEach { (screen, icon) ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(screen) },
                        icon = { Icon(icon, contentDescription = screen.title) },
                        label = { Text(screen.title, fontSize = 10.5.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = DarkPrimary
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(viewModel)
                AppScreen.COMMAND_CENTER -> CommandCenterScreen(viewModel)
                AppScreen.AI_MENTOR -> MentorScreen(viewModel)
                AppScreen.UNIVERSITY -> UniversityScreen(viewModel)
                AppScreen.RESEARCH_LAB -> ResearchLabScreen(viewModel)
                AppScreen.CS_SKILLS -> SkillsScreen(viewModel)
                AppScreen.PROJECT_BUILDER -> ProjectBuilderScreen(viewModel)
                AppScreen.CAREER_CENTER -> CareerScreen(viewModel)
                AppScreen.CHINESE_LANGUAGE -> ChineseScreen(viewModel)
                AppScreen.DOCUMENTS -> DocumentScreen(viewModel)
                AppScreen.ROADMAP -> RoadmapScreen(viewModel)
                AppScreen.CHINA_WORK -> ChinaWorkScreen(viewModel)
                AppScreen.PROFILE -> ProfileScreen(viewModel)
                AppScreen.SETTINGS -> SettingsScreen(viewModel)
            }
        }
    }

    // Modal Bottom Sheet showing all 14 modules
    if (showModuleSheet) {
        ModalBottomSheet(
            onDismissRequest = { showModuleSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "CS Scholar OS • 14 Core Modules",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "Personal Operating System for International CS Students in China",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    items(AppScreen.entries) { screen ->
                        val isCurrent = currentScreen == screen
                        ModuleTile(
                            screen = screen,
                            isCurrent = isCurrent,
                            onClick = {
                                viewModel.navigateTo(screen)
                                scope.launch {
                                    sheetState.hide()
                                    showModuleSheet = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleTile(
    screen: AppScreen,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    val (icon, color) = when (screen) {
        AppScreen.DASHBOARD -> Icons.Default.Dashboard to DarkPrimary
        AppScreen.COMMAND_CENTER -> Icons.Default.AutoAwesome to ScholarGold
        AppScreen.AI_MENTOR -> Icons.Default.Psychology to ScholarCyan
        AppScreen.UNIVERSITY -> Icons.Default.School to ScholarGold
        AppScreen.RESEARCH_LAB -> Icons.Default.Science to DarkPrimary
        AppScreen.CS_SKILLS -> Icons.Default.Terminal to ScholarCyan
        AppScreen.PROJECT_BUILDER -> Icons.Default.Build to ScholarGold
        AppScreen.CAREER_CENTER -> Icons.Default.Work to DarkPrimary
        AppScreen.CHINESE_LANGUAGE -> Icons.Default.Translate to ScholarCyan
        AppScreen.DOCUMENTS -> Icons.Default.Description to ScholarGold
        AppScreen.ROADMAP -> Icons.Default.Timeline to DarkPrimary
        AppScreen.CHINA_WORK -> Icons.Default.Public to ScholarCyan
        AppScreen.PROFILE -> Icons.Default.Person to ScholarCyan
        AppScreen.SETTINGS -> Icons.Default.Settings to ScholarGold
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = screen.title,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = screen.title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                ),
                maxLines = 1
            )
        }
    }
}
