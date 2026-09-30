package com.learning.dashboard.presentation.detail

import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.model.Course
import com.learning.dashboard.domain.model.Lesson
import com.learning.dashboard.domain.usecase.GetCourseDetailUseCase
import com.learning.dashboard.domain.usecase.ToggleLessonCompletionUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CourseDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var getCourseDetailUseCase: GetCourseDetailUseCase
    private lateinit var toggleLessonCompletionUseCase: ToggleLessonCompletionUseCase
    private lateinit var viewModel: CourseDetailViewModel

    private val sampleCourse = Course(
        id = 1,
        title = "Python Programming",
        instructor = "John Smith",
        progress = 50,
        lessonsCount = 2,
        lessons = listOf(
            Lesson(id = 101, courseId = 1, title = "Introduction", isCompleted = true, orderIndex = 1),
            Lesson(id = 102, courseId = 1, title = "Variables", isCompleted = false, orderIndex = 2)
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getCourseDetailUseCase = mockk()
        toggleLessonCompletionUseCase = mockk(relaxed = true)

        every { getCourseDetailUseCase(1) } returns flowOf(
            Resource.Loading,
            Resource.Success(sampleCourse)
        )

        viewModel = CourseDetailViewModel(
            courseId = 1,
            getCourseDetailUseCase = getCourseDetailUseCase,
            toggleLessonCompletionUseCase = toggleLessonCompletionUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadCourseDetail emits Success state with course and lessons`() {
        val state = viewModel.uiState.value
        assertTrue(state is CourseDetailUiState.Success)
        val success = state as CourseDetailUiState.Success
        assertEquals("Python Programming", success.course.title)
        assertEquals(2, success.course.lessons.size)
        assertEquals(50, success.course.progress)
    }

    @Test
    fun `toggleLessonStatus invokes ToggleLessonCompletionUseCase`() {
        coEvery { toggleLessonCompletionUseCase(courseId = 1, lessonId = 102) } returns Result.success(Unit)

        viewModel.toggleLessonStatus(102)

        coVerify { toggleLessonCompletionUseCase(courseId = 1, lessonId = 102) }
    }
}
