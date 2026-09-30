package com.learning.dashboard.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.learning.dashboard.presentation.dashboard.CourseDashboardViewModel
import com.learning.dashboard.presentation.detail.CourseDetailViewModel
import com.learning.dashboard.presentation.login.LoginViewModel

class ViewModelFactory(
    private val appContainer: AppContainer,
    private val courseId: Int? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(loginUseCase = appContainer.loginUseCase) as T
            }
            modelClass.isAssignableFrom(CourseDashboardViewModel::class.java) -> {
                CourseDashboardViewModel(
                    getCoursesUseCase = appContainer.getCoursesUseCase,
                    courseRepository = appContainer.courseRepository
                ) as T
            }
            modelClass.isAssignableFrom(CourseDetailViewModel::class.java) -> {
                requireNotNull(courseId) { "courseId must be provided for CourseDetailViewModel" }
                CourseDetailViewModel(
                    courseId = courseId,
                    getCourseDetailUseCase = appContainer.getCourseDetailUseCase,
                    toggleLessonCompletionUseCase = appContainer.toggleLessonCompletionUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
