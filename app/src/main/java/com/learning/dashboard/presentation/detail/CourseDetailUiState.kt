package com.learning.dashboard.presentation.detail

import com.learning.dashboard.domain.model.Course

sealed interface CourseDetailUiState {
    data object Loading : CourseDetailUiState
    data class Success(val course: Course, val isUpdating: Boolean = false) : CourseDetailUiState
    data class Error(val message: String) : CourseDetailUiState
}
