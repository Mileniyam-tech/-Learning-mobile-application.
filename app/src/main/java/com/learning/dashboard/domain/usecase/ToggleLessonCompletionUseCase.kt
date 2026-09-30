package com.learning.dashboard.domain.usecase

import com.learning.dashboard.domain.repository.CourseRepository

class ToggleLessonCompletionUseCase(
    private val courseRepository: CourseRepository
) {
    suspend operator fun invoke(courseId: Int, lessonId: Int): Result<Unit> {
        return courseRepository.toggleLessonCompletion(courseId, lessonId)
    }
}
