package com.learning.dashboard.data.repository

import app.cash.turbine.test
import com.learning.dashboard.TestDispatcherProvider
import com.learning.dashboard.core.Resource
import com.learning.dashboard.data.local.dao.CourseDao
import com.learning.dashboard.data.local.dao.LessonDao
import com.learning.dashboard.data.local.entity.CourseEntity
import com.learning.dashboard.data.local.entity.LessonEntity
import com.learning.dashboard.data.remote.MockApiService
import com.learning.dashboard.domain.usecase.CalculateCourseProgressUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class CourseRepositoryTest {

    private lateinit var courseDao: CourseDao
    private lateinit var lessonDao: LessonDao
    private lateinit var apiService: MockApiService
    private lateinit var calculateProgressUseCase: CalculateCourseProgressUseCase
    private lateinit var repository: CourseRepositoryImpl
    private val testDispatchers = TestDispatcherProvider()

    @Before
    fun setUp() {
        courseDao = mockk(relaxed = true)
        lessonDao = mockk(relaxed = true)
        apiService = MockApiService()
        calculateProgressUseCase = CalculateCourseProgressUseCase()

        repository = CourseRepositoryImpl(
            courseDao = courseDao,
            lessonDao = lessonDao,
            apiService = apiService,
            calculateProgressUseCase = calculateProgressUseCase,
            dispatchers = testDispatchers
        )
    }

    @Test
    fun `getCourses returns cached courses from Room database`() = runTest {
        val cachedEntities = listOf(
            CourseEntity(id = 1, title = "Python Programming", instructor = "John Smith", progress = 65, lessonsCount = 20),
            CourseEntity(id = 2, title = "Generative AI", instructor = "Sarah Williams", progress = 40, lessonsCount = 16)
        )

        every { courseDao.getAllCourses() } returns flowOf(cachedEntities)

        repository.getCourses(forceRefresh = false).test {
            assertEquals(Resource.Loading, awaitItem())
            val successItem = awaitItem()
            assertTrue(successItem is Resource.Success)
            val courses = (successItem as Resource.Success).data
            assertEquals(2, courses.size)
            assertEquals("Python Programming", courses[0].title)
            assertEquals("Generative AI", courses[1].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCourses when offline with cached data emits cached courses without throwing`() = runTest {
        apiService.isNetworkAvailable = false // Turn off network

        val cachedEntities = listOf(
            CourseEntity(id = 1, title = "Python Programming", instructor = "John Smith", progress = 65, lessonsCount = 20)
        )
        every { courseDao.getAllCourses() } returns flowOf(cachedEntities)

        repository.getCourses(forceRefresh = true).test {
            assertEquals(Resource.Loading, awaitItem())
            val item = awaitItem()
            assertTrue(item is Resource.Success)
            assertEquals("Python Programming", (item as Resource.Success).data.first().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `toggleLessonCompletion updates lesson status and recalculates course progress`() = runTest {
        val lesson = LessonEntity(id = 101, courseId = 1, title = "Intro", isCompleted = false, orderIndex = 1)
        val allLessons = listOf(
            LessonEntity(id = 101, courseId = 1, title = "Intro", isCompleted = true, orderIndex = 1),
            LessonEntity(id = 102, courseId = 1, title = "Syntax", isCompleted = false, orderIndex = 2)
        )

        coEvery { lessonDao.getLessonById(101) } returns lesson
        coEvery { lessonDao.getLessonsForCourseSync(1) } returns allLessons

        val result = repository.toggleLessonCompletion(courseId = 1, lessonId = 101)

        assertTrue(result.isSuccess)
        coVerify { lessonDao.updateLessonCompletion(101, true) }
        coVerify { courseDao.updateCourseProgress(1, 50) } // 1 of 2 = 50%
    }
}
