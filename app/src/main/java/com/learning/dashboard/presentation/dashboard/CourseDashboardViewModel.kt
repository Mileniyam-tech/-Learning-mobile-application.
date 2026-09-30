package com.learning.dashboard.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.repository.CourseRepository
import com.learning.dashboard.domain.usecase.GetCoursesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CourseDashboardViewModel(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseDashboardUiState>(CourseDashboardUiState.Loading)
    val uiState: StateFlow<CourseDashboardUiState> = _uiState.asStateFlow()

    private val _isOnline = MutableStateFlow(courseRepository.isSimulatedOnline())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        loadCourses(forceRefresh = false)
    }

    fun loadCourses(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            getCoursesUseCase(forceRefresh).collect { resource ->
                val currentOnline = courseRepository.isSimulatedOnline()
                when (resource) {
                    is Resource.Loading -> {
                        // Only show full-screen loading if not already in success state
                        if (_uiState.value !is CourseDashboardUiState.Success) {
                            _uiState.value = CourseDashboardUiState.Loading
                        }
                    }
                    is Resource.Success -> {
                        if (resource.data.isEmpty()) {
                            _uiState.value = CourseDashboardUiState.Empty
                        } else {
                            _uiState.value = CourseDashboardUiState.Success(
                                courses = resource.data,
                                isOffline = !currentOnline
                            )
                        }
                    }
                    is Resource.Error -> {
                        // If we already have success data (cached), keep displaying it with offline flag
                        val current = _uiState.value
                        if (current is CourseDashboardUiState.Success) {
                            _uiState.value = current.copy(isOffline = true)
                        } else {
                            _uiState.value = CourseDashboardUiState.Error(resource.message)
                        }
                    }
                }
            }
        }
    }

    fun toggleNetworkStatus(online: Boolean) {
        courseRepository.setSimulatedNetworkStatus(online)
        _isOnline.value = online
        val current = _uiState.value
        if (current is CourseDashboardUiState.Success) {
            _uiState.value = current.copy(isOffline = !online)
        }
        if (online) {
            loadCourses(forceRefresh = true)
        }
    }
}
