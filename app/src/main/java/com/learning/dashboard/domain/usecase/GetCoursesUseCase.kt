package com.learning.dashboard.domain.usecase

import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.model.Course
import com.learning.dashboard.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

class GetCoursesUseCase(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(forceRefresh: Boolean = false): Flow<Resource<List<Course>>> {
        return courseRepository.getCourses(forceRefresh)
    }
}
