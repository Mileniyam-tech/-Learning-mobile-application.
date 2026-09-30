package com.learning.dashboard.presentation.dashboard

import com.learning.dashboard.domain.model.Course

sealed interface CourseDashboardUiState {
    data object Loading : CourseDashboardUiState
    data class Success(val courses: List<Course>, val isOffline: Boolean = false) : CourseDashboardUiState
    data object Empty : CourseDashboardUiState
    data class Error(val message: String) : CourseDashboardUiState
}
