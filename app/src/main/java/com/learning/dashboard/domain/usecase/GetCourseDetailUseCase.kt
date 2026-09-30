package com.learning.dashboard.domain.usecase

import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.model.Course
import com.learning.dashboard.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

class GetCourseDetailUseCase(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(courseId: Int): Flow<Resource<Course>> {
        return courseRepository.getCourseById(courseId)
    }
}
