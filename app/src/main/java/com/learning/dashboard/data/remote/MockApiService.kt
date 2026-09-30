package com.learning.dashboard.data.remote

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.learning.dashboard.data.remote.dto.CourseDto
import com.learning.dashboard.data.remote.dto.LessonDto
import com.learning.dashboard.domain.model.User
import kotlinx.coroutines.delay
import java.io.IOException

class MockApiService {

    var isNetworkAvailable: Boolean = true
    var shouldSimulateApiError: Boolean = false

    private val mockCoursesJson = """
    [
      {
        "id": 1,
        "title": "Python Programming",
        "instructor": "John Smith",
        "progress": 65,
        "lessons": 20,
        "lessonsList": [
          { "id": 101, "courseId": 1, "title": "Introduction", "isCompleted": true, "orderIndex": 1 },
          { "id": 102, "courseId": 1, "title": "Variables & Data Types", "isCompleted": true, "orderIndex": 2 },
          { "id": 103, "courseId": 1, "title": "Functions", "isCompleted": false, "orderIndex": 3 },
          { "id": 104, "courseId": 1, "title": "OOP", "isCompleted": false, "orderIndex": 4 },
          { "id": 105, "courseId": 1, "title": "Modules & Packages", "isCompleted": false, "orderIndex": 5 },
          { "id": 106, "courseId": 1, "title": "File I/O & Serialization", "isCompleted": false, "orderIndex": 6 },
          { "id": 107, "courseId": 1, "title": "Exception Handling", "isCompleted": false, "orderIndex": 7 },
          { "id": 108, "courseId": 1, "title": "Decorators & Generators", "isCompleted": false, "orderIndex": 8 }
        ]
      },
      {
        "id": 2,
        "title": "Generative AI",
        "instructor": "Sarah Williams",
        "progress": 40,
        "lessons": 16,
        "lessonsList": [
          { "id": 201, "courseId": 2, "title": "Introduction to GenAI", "isCompleted": true, "orderIndex": 1 },
          { "id": 202, "courseId": 2, "title": "Prompt Engineering Essentials", "isCompleted": true, "orderIndex": 2 },
          { "id": 203, "courseId": 2, "title": "LLM Architecture & Transformers", "isCompleted": false, "orderIndex": 3 },
          { "id": 204, "courseId": 2, "title": "Embeddings & Vector Databases", "isCompleted": false, "orderIndex": 4 },
          { "id": 205, "courseId": 2, "title": "Retrieval-Augmented Generation (RAG)", "isCompleted": false, "orderIndex": 5 },
          { "id": 206, "courseId": 2, "title": "Fine-Tuning & Evaluation", "isCompleted": false, "orderIndex": 6 }
        ]
      },
      {
        "id": 3,
        "title": "Full Stack Development",
        "instructor": "David Brown",
        "progress": 25,
        "lessons": 28,
        "lessonsList": [
          { "id": 301, "courseId": 3, "title": "HTML5 & Modern CSS Systems", "isCompleted": true, "orderIndex": 1 },
          { "id": 302, "courseId": 3, "title": "Modern JavaScript (ES6+)", "isCompleted": false, "orderIndex": 2 },
          { "id": 303, "courseId": 3, "title": "React & State Management", "isCompleted": false, "orderIndex": 3 },
          { "id": 304, "courseId": 3, "title": "Backend with Node.js & Express", "isCompleted": false, "orderIndex": 4 },
          { "id": 305, "courseId": 3, "title": "PostgreSQL & Database Design", "isCompleted": false, "orderIndex": 5 },
          { "id": 306, "courseId": 3, "title": "API Security & Deployment", "isCompleted": false, "orderIndex": 6 }
        ]
      }
    ]
    """.trimIndent()

    private val gson = Gson()

    suspend fun login(email: String, password: String): User {
        delay(600) // Simulate network latency

        if (!isNetworkAvailable) {
            throw IOException("No internet connection. Please check your network settings.")
        }
        if (shouldSimulateApiError) {
            throw IOException("Server is temporarily unavailable. Please try again later (503).")
        }

        // Validate credentials
        if (password == "wrongpassword" || password == "error") {
            throw IllegalArgumentException("Invalid credentials. Please check your password.")
        }

        val userName = email.substringBefore("@").replace(".", " ").capitalizeWords()
        return User(
            id = "usr_99812",
            email = email,
            name = if (userName.isBlank()) "Mobile Developer" else userName,
            token = "jwt_mock_token_${System.currentTimeMillis()}"
        )
    }

    suspend fun fetchCourses(): List<CourseDto> {
        delay(500) // Simulate network latency

        if (!isNetworkAvailable) {
            throw IOException("No internet connection. Offline mode active.")
        }
        if (shouldSimulateApiError) {
            throw IOException("Failed to fetch courses: 500 Internal Server Error")
        }

        val type = object : TypeToken<List<CourseDto>>() {}.type
        return gson.fromJson(mockCoursesJson, type)
    }

    private fun String.capitalizeWords(): String {
        return split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
