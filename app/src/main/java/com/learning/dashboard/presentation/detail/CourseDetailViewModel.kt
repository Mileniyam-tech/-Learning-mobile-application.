package com.learning.dashboard.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.usecase.GetCourseDetailUseCase
import com.learning.dashboard.domain.usecase.ToggleLessonCompletionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    private val courseId: Int,
    private val getCourseDetailUseCase: GetCourseDetailUseCase,
    private val toggleLessonCompletionUseCase: ToggleLessonCompletionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CourseDetailUiState>(CourseDetailUiState.Loading)
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    init {
        loadCourseDetail()
    }

    fun loadCourseDetail() {
        viewModelScope.launch {
            getCourseDetailUseCase(courseId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (_uiState.value !is CourseDetailUiState.Success) {
                            _uiState.value = CourseDetailUiState.Loading
                        }
                    }
                    is Resource.Success -> {
                        _uiState.value = CourseDetailUiState.Success(course = resource.data)
                    }
                    is Resource.Error -> {
                        _uiState.value = CourseDetailUiState.Error(message = resource.message)
                    }
                }
            }
        }
    }

    fun toggleLessonStatus(lessonId: Int) {
        viewModelScope.launch {
            val current = _uiState.value
            if (current is CourseDetailUiState.Success) {
                _uiState.value = current.copy(isUpdating = true)
            }

            toggleLessonCompletionUseCase(courseId = courseId, lessonId = lessonId)
            // Note: Since CourseDetailUseCase observes Room DB Flow, the UI will automatically
            // receive the updated lesson list and recalculated course progress!
        }
    }
}
