package com.sahed.my_own_vocabulary.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sahed.my_own_vocabulary.ui.designsystem.components.AppBackground
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import com.sahed.my_own_vocabulary.data.preferences.AppPreferences
import com.sahed.my_own_vocabulary.data.repository.AuthRepository
import com.sahed.my_own_vocabulary.ui.components.VocabBottomBar
import com.sahed.my_own_vocabulary.ui.screens.calendar.CalendarScreen
import com.sahed.my_own_vocabulary.ui.screens.calendar.CalendarViewModel
import com.sahed.my_own_vocabulary.ui.screens.entry.AddVocabularySheet
import com.sahed.my_own_vocabulary.ui.screens.entry.AddVocabularyViewModel
import com.sahed.my_own_vocabulary.ui.screens.login.LoginScreen
import com.sahed.my_own_vocabulary.ui.screens.login.LoginViewModel
import com.sahed.my_own_vocabulary.ui.screens.quiz.QuizScreen
import com.sahed.my_own_vocabulary.ui.screens.quiz.QuizViewModel
import com.sahed.my_own_vocabulary.ui.screens.settings.ManageFoldersScreen
import com.sahed.my_own_vocabulary.ui.screens.settings.ManageFoldersViewModel
import com.sahed.my_own_vocabulary.ui.screens.settings.SettingsScreen
import com.sahed.my_own_vocabulary.ui.screens.settings.SettingsViewModel
import com.sahed.my_own_vocabulary.ui.screens.vocabulary.VocabularyScreen
import com.sahed.my_own_vocabulary.ui.screens.vocabulary.VocabularyViewModel

object AppRoutes {
    const val LOGIN = "login"
    const val VOCABULARY = "vocabulary"
    const val QUIZ = "quiz"
    const val CALENDAR = "calendar"
    const val SETTINGS = "settings"
    const val MANAGE_FOLDERS = "manage_folders"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val preferences = remember { AppPreferences(context) }
    val isUserLoggedIn = remember {
        authRepository.isUserSignedIn || preferences.isLoggedIn
    }
    val startDestination = remember {
        if (isUserLoggedIn) AppRoutes.VOCABULARY else AppRoutes.LOGIN
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: startDestination

    var showAddVocabularySheet by remember { mutableStateOf(false) }

    // Shared / Screen ViewModels
    val addVocabViewModel: AddVocabularyViewModel = viewModel()

    // Add Vocabulary Sheet Modal
    if (showAddVocabularySheet) {
        AddVocabularySheet(
            viewModel = addVocabViewModel,
            onDismiss = { showAddVocabularySheet = false }
        )
    }

    val showBottomBar = currentRoute in listOf(
        AppRoutes.VOCABULARY,
        AppRoutes.QUIZ,
        AppRoutes.CALENDAR,
        AppRoutes.SETTINGS
    )

    AppBackground(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
            if (showBottomBar) {
                VocabBottomBar(
                    currentRoute = currentRoute,
                    onNavigateToVocabulary = {
                        if (currentRoute != AppRoutes.VOCABULARY) {
                            navController.navigate(AppRoutes.VOCABULARY) {
                                popUpTo(AppRoutes.VOCABULARY) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onNavigateToQuiz = {
                        if (currentRoute != AppRoutes.QUIZ) {
                            navController.navigate(AppRoutes.QUIZ) {
                                popUpTo(AppRoutes.VOCABULARY) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onNavigateToCalendar = {
                        if (currentRoute != AppRoutes.CALENDAR) {
                            navController.navigate(AppRoutes.CALENDAR) {
                                popUpTo(AppRoutes.VOCABULARY) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onNavigateToSettings = {
                        if (currentRoute != AppRoutes.SETTINGS) {
                            navController.navigate(AppRoutes.SETTINGS) {
                                popUpTo(AppRoutes.VOCABULARY) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    onOpenAddVocabulary = {
                        addVocabViewModel.startNewEntry()
                        showAddVocabularySheet = true
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 0. Login Screen (First screen)
            composable(
                route = AppRoutes.LOGIN,
                enterTransition = { fadeIn(tween(250)) },
                exitTransition = { fadeOut(tween(200)) }
            ) {
                val loginViewModel: LoginViewModel = viewModel()
                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToHome = {
                        preferences.isLoggedIn = true
                        navController.navigate(AppRoutes.VOCABULARY) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 1. Vocabulary Explorer
            composable(
                route = AppRoutes.VOCABULARY,
                enterTransition = { fadeIn(tween(250)) },
                exitTransition = { fadeOut(tween(200)) }
            ) {
                val vocabViewModel: VocabularyViewModel = viewModel()
                VocabularyScreen(
                    viewModel = vocabViewModel,
                    onEditEntry = { entry ->
                        addVocabViewModel.startEditing(entry)
                        showAddVocabularySheet = true
                    }
                )
            }

            // 2. Quiz / Practice Games
            composable(
                route = AppRoutes.QUIZ,
                enterTransition = { fadeIn(tween(250)) },
                exitTransition = { fadeOut(tween(200)) }
            ) {
                val quizViewModel: QuizViewModel = viewModel()
                QuizScreen(
                    viewModel = quizViewModel
                )
            }

            // 3. Calendar Daily Word Activity
            composable(
                route = AppRoutes.CALENDAR,
                enterTransition = { fadeIn(tween(250)) },
                exitTransition = { fadeOut(tween(200)) }
            ) {
                val calendarViewModel: CalendarViewModel = viewModel()
                CalendarScreen(
                    viewModel = calendarViewModel,
                    onOpenAddVocabulary = {
                        addVocabViewModel.startNewEntry()
                        showAddVocabularySheet = true
                    }
                )
            }

            // 4. Settings
            composable(
                route = AppRoutes.SETTINGS,
                enterTransition = { fadeIn(tween(250)) },
                exitTransition = { fadeOut(tween(200)) }
            ) {
                val settingsViewModel: SettingsViewModel = viewModel()
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToManageFolders = {
                        navController.navigate(AppRoutes.MANAGE_FOLDERS)
                    },
                    onSignOut = {
                        preferences.isLoggedIn = false
                        navController.navigate(AppRoutes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }

            // 5. Manage Folders & Sub-Folders
            composable(
                route = AppRoutes.MANAGE_FOLDERS,
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(300)
                    ) + fadeIn(tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(300)
                    ) + fadeOut(tween(300))
                }
            ) {
                val manageFoldersViewModel: ManageFoldersViewModel = viewModel()
                ManageFoldersScreen(
                    viewModel = manageFoldersViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
}
