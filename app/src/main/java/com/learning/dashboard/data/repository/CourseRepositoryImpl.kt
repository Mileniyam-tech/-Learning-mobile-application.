package com.learning.dashboard.data.repository

import com.learning.dashboard.core.DispatcherProvider
import com.learning.dashboard.core.Resource
import com.learning.dashboard.data.local.dao.CourseDao
import com.learning.dashboard.data.local.dao.LessonDao
import com.learning.dashboard.data.local.entity.CourseEntity
import com.learning.dashboard.data.local.entity.LessonEntity
import com.learning.dashboard.data.remote.MockApiService
import com.learning.dashboard.domain.model.Course
import com.learning.dashboard.domain.model.Lesson
import com.learning.dashboard.domain.repository.CourseRepository
import com.learning.dashboard.domain.usecase.CalculateCourseProgressUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseRepositoryImpl(
    private val courseDao: CourseDao,
    private val lessonDao: LessonDao,
    private val apiService: MockApiService,
    private val calculateProgressUseCase: CalculateCourseProgressUseCase,
    private val dispatchers: DispatcherProvider
) : CourseRepository {

    override fun getCourses(forceRefresh: Boolean): Flow<Resource<List<Course>>> = channelFlow {
        send(Resource.Loading)

        // Single Source of Truth: Collect local DB courses flow
        val dbFlowJob = launch {
            courseDao.getAllCourses().collect { entities ->
                if (entities.isNotEmpty()) {
                    val domainCourses = entities.map { it.toDomain() }
                    send(Resource.Success(domainCourses))
                }
            }
        }

        // Check if we need to sync from network
        try {
            val cached = courseDao.getAllCourses().firstOrNull()
            val shouldFetchFromNetwork = forceRefresh || cached.isNullOrEmpty()

            if (shouldFetchFromNetwork) {
                fetchAndCacheCourses()
            }
        } catch (e: Exception) {
            val cached = courseDao.getAllCourses().firstOrNull()
            if (cached.isNullOrEmpty()) {
                send(Resource.Error(e.message ?: "Failed to load courses", e))
            }
            // If cached data exists, local DB flow already emitted/will emit it
        }

        dbFlowJob.join()
    }.flowOn(dispatchers.io)

    override fun getCourseById(courseId: Int): Flow<Resource<Course>> = channelFlow {
        send(Resource.Loading)

        val courseFlow = courseDao.getCourseById(courseId)
        val lessonsFlow = lessonDao.getLessonsForCourse(courseId)

        combine(courseFlow, lessonsFlow) { courseEntity, lessonEntities ->
            if (courseEntity == null) {
                null
            } else {
                val lessons = lessonEntities.map { it.toDomain() }
                val calculatedProgress = calculateProgressUseCase(lessons)
                Course(
                    id = courseEntity.id,
                    title = courseEntity.title,
                    instructor = courseEntity.instructor,
                    progress = if (lessons.isNotEmpty()) calculatedProgress else courseEntity.progress,
                    lessonsCount = if (lessons.isNotEmpty()) lessons.size else courseEntity.lessonsCount,
                    lessons = lessons
                )
            }
        }.collect { course ->
            if (course != null) {
                send(Resource.Success(course))
            } else {
                // If not in DB, attempt fetch
                try {
                    fetchAndCacheCourses()
                } catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Course not found", e))
                }
            }
        }
    }.flowOn(dispatchers.io)

    override suspend fun toggleLessonCompletion(courseId: Int, lessonId: Int): Result<Unit> = withContext(dispatchers.io) {
        try {
            val lesson = lessonDao.getLessonById(lessonId)
                ?: return@withContext Result.failure(IllegalArgumentException("Lesson not found"))

            val updatedStatus = !lesson.isCompleted
            lessonDao.updateLessonCompletion(lessonId, updatedStatus)

            // Recalculate progress and update course entity
            val lessons = lessonDao.getLessonsForCourseSync(courseId)
            val domainLessons = lessons.map { it.toDomain() }
            val newProgress = calculateProgressUseCase(domainLessons)

            courseDao.updateCourseProgress(courseId, newProgress)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun setSimulatedNetworkStatus(isOnline: Boolean) {
        apiService.isNetworkAvailable = isOnline
    }

    override fun isSimulatedOnline(): Boolean = apiService.isNetworkAvailable

    private suspend fun fetchAndCacheCourses() {
        val courseDtos = apiService.fetchCourses()
        val courseEntities = mutableListOf<CourseEntity>()
        val lessonEntities = mutableListOf<LessonEntity>()

        for (dto in courseDtos) {
            val lessons = dto.lessonsList ?: emptyList()
            var completedCount = 0

            lessons.forEach { lessonDto ->
                // Check if we already have local progress saved so we preserve user state
                val existingLesson = lessonDao.getLessonById(lessonDto.id)
                val isCompleted = existingLesson?.isCompleted ?: lessonDto.isCompleted
                if (isCompleted) completedCount++

                lessonEntities.add(
                    LessonEntity(
                        id = lessonDto.id,
                        courseId = dto.id,
                        title = lessonDto.title,
                        isCompleted = isCompleted,
                        orderIndex = lessonDto.orderIndex
                    )
                )
            }

            val finalProgress = if (lessons.isNotEmpty()) {
                calculateProgressUseCase.calculateFromCounts(completedCount, lessons.size)
            } else {
                dto.progress
            }

            courseEntities.add(
                CourseEntity(
                    id = dto.id,
                    title = dto.title,
                    instructor = dto.instructor,
                    progress = finalProgress,
                    lessonsCount = if (lessons.isNotEmpty()) lessons.size else dto.lessons
                )
            )
        }

        courseDao.insertCourses(courseEntities)
        if (lessonEntities.isNotEmpty()) {
            lessonDao.insertOrUpdateLessons(lessonEntities)
        }
    }
}
