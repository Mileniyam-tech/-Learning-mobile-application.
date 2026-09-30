package com.learning.dashboard.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.learning.dashboard.domain.model.Lesson

data class LessonDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("isCompleted")
    val isCompleted: Boolean,
    @SerializedName("orderIndex")
    val orderIndex: Int
) {
    fun toDomain(): Lesson {
        return Lesson(
            id = id,
            courseId = courseId,
            title = title,
            isCompleted = isCompleted,
            orderIndex = orderIndex
        )
    }
}
