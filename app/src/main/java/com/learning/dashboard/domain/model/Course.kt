package com.learning.dashboard.domain.model

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val progress: Int,
    val lessonsCount: Int,
    val lessons: List<Lesson> = emptyList()
)
