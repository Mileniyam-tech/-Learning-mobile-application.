package com.learning.dashboard.domain.usecase

import com.learning.dashboard.domain.model.Lesson
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CalculateCourseProgressUseCaseTest {

    private lateinit var useCase: CalculateCourseProgressUseCase

    @Before
    fun setUp() {
        useCase = CalculateCourseProgressUseCase()
    }

    @Test
    fun `invoke with empty lessons list returns zero percent`() {
        val lessons = emptyList<Lesson>()
        val result = useCase(lessons)
        assertEquals(0, result)
    }

    @Test
    fun `invoke with no completed lessons returns zero percent`() {
        val lessons = listOf(
            Lesson(id = 1, courseId = 1, title = "Intro", isCompleted = false, orderIndex = 1),
            Lesson(id = 2, courseId = 1, title = "Syntax", isCompleted = false, orderIndex = 2)
        )
        val result = useCase(lessons)
        assertEquals(0, result)
    }

    @Test
    fun `invoke with all completed lessons returns 100 percent`() {
        val lessons = listOf(
            Lesson(id = 1, courseId = 1, title = "Intro", isCompleted = true, orderIndex = 1),
            Lesson(id = 2, courseId = 1, title = "Syntax", isCompleted = true, orderIndex = 2),
            Lesson(id = 3, courseId = 1, title = "Functions", isCompleted = true, orderIndex = 3)
        )
        val result = useCase(lessons)
        assertEquals(100, result)
    }

    @Test
    fun `invoke with partial completed lessons calculates correctly rounded percentage`() {
        // 2 out of 4 completed = 50%
        val fourLessons = listOf(
            Lesson(id = 1, courseId = 1, title = "L1", isCompleted = true, orderIndex = 1),
            Lesson(id = 2, courseId = 1, title = "L2", isCompleted = true, orderIndex = 2),
            Lesson(id = 3, courseId = 1, title = "L3", isCompleted = false, orderIndex = 3),
            Lesson(id = 4, courseId = 1, title = "L4", isCompleted = false, orderIndex = 4)
        )
        assertEquals(50, useCase(fourLessons))

        // 1 out of 3 completed = 33%
        val threeLessons = listOf(
            Lesson(id = 1, courseId = 1, title = "L1", isCompleted = true, orderIndex = 1),
            Lesson(id = 2, courseId = 1, title = "L2", isCompleted = false, orderIndex = 2),
            Lesson(id = 3, courseId = 1, title = "L3", isCompleted = false, orderIndex = 3)
        )
        assertEquals(33, useCase(threeLessons))

        // 2 out of 3 completed = 67%
        val twoOfThree = listOf(
            Lesson(id = 1, courseId = 1, title = "L1", isCompleted = true, orderIndex = 1),
            Lesson(id = 2, courseId = 1, title = "L2", isCompleted = true, orderIndex = 2),
            Lesson(id = 3, courseId = 1, title = "L3", isCompleted = false, orderIndex = 3)
        )
        assertEquals(67, useCase(twoOfThree))
    }

    @Test
    fun `calculateFromCounts handles zero and negative totals safely`() {
        assertEquals(0, useCase.calculateFromCounts(completedLessons = 0, totalLessons = 0))
        assertEquals(0, useCase.calculateFromCounts(completedLessons = 5, totalLessons = -1))
        assertEquals(75, useCase.calculateFromCounts(completedLessons = 3, totalLessons = 4))
    }
}
