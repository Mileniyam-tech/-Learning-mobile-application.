package com.learning.dashboard.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.learning.dashboard.domain.model.Course

data class CourseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("instructor")
    val instructor: String,
    @SerializedName("progress")
    val progress: Int,
    @SerializedName("lessons")
    val lessons: Int,
    @SerializedName("lessonsList")
    val lessonsList: List<LessonDto>? = null
) {
    fun toDomain(): Course {
        return Course(
            id = id,
            title = title,
            instructor = instructor,
            progress = progress,
            lessonsCount = lessons,
            lessons = lessonsList?.map { it.toDomain() } ?: emptyList()
        )
    }
}
