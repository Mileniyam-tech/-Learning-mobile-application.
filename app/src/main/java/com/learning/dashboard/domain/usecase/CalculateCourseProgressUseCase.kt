package com.learning.dashboard.domain.usecase

import com.learning.dashboard.domain.model.Lesson
import kotlin.math.roundToInt

/**
 * Pure domain use case for calculating course progress percentage.
 * Handles edge cases like zero lessons, rounding, and limits (0-100%).
 */
class CalculateCourseProgressUseCase {

    operator fun invoke(lessons: List<Lesson>): Int {
        if (lessons.isEmpty()) return 0
        val completedCount = lessons.count { it.isCompleted }
        val percentage = (completedCount.toDouble() / lessons.size.toDouble()) * 100.0
        return percentage.roundToInt().coerceIn(0, 100)
    }

    fun calculateFromCounts(completedLessons: Int, totalLessons: Int): Int {
        if (totalLessons <= 0) return 0
        val percentage = (completedLessons.toDouble() / totalLessons.toDouble()) * 100.0
        return percentage.roundToInt().coerceIn(0, 100)
    }
}
