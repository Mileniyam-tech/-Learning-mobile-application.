package com.learning.dashboard.domain.repository

import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    /**
     * Observes course list from the local single source of truth (Room DB),
     * and triggers a network refresh from the API if requested.
     */
    fun getCourses(forceRefresh: Boolean = false): Flow<Resource<List<Course>>>

    /**
     * Observes a specific course and its lessons by ID from local DB.
     */
    fun getCourseById(courseId: Int): Flow<Resource<Course>>

    /**
     * Toggles a lesson completion status in the database and recalculates course progress.
     */
    suspend fun toggleLessonCompletion(courseId: Int, lessonId: Int): Result<Unit>

    /**
     * Toggle network availability simulation for testing offline mode and API failure states.
     */
    fun setSimulatedNetworkStatus(isOnline: Boolean)

    /**
     * Returns whether network is currently simulated as online.
     */
    fun isSimulatedOnline(): Boolean
}
