package com.learning.dashboard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.learning.dashboard.di.AppContainer
import com.learning.dashboard.di.ViewModelFactory
import com.learning.dashboard.presentation.dashboard.CourseDashboardScreen
import com.learning.dashboard.presentation.dashboard.CourseDashboardViewModel
import com.learning.dashboard.presentation.detail.CourseDetailScreen
import com.learning.dashboard.presentation.detail.CourseDetailViewModel
import com.learning.dashboard.presentation.login.LoginScreen
import com.learning.dashboard.presentation.login.LoginViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Screen 1: Login
        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = ViewModelFactory(appContainer)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.CourseDashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Screen 2: Course Dashboard
        composable(route = Screen.CourseDashboard.route) {
            val dashboardViewModel: CourseDashboardViewModel = viewModel(
                factory = ViewModelFactory(appContainer)
            )
            CourseDashboardScreen(
                viewModel = dashboardViewModel,
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                }
            )
        }

        // Screen 3: Course Details
        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(
                navArgument("courseId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt("courseId") ?: 1
            val detailViewModel: CourseDetailViewModel = viewModel(
                key = "course_detail_$courseId",
                factory = ViewModelFactory(appContainer, courseId = courseId)
            )
            CourseDetailScreen(
                viewModel = detailViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
