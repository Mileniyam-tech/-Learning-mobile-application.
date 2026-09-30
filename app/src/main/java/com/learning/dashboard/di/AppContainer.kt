package com.learning.dashboard.di

import android.content.Context
import com.learning.dashboard.core.DefaultDispatcherProvider
import com.learning.dashboard.core.DispatcherProvider
import com.learning.dashboard.data.local.AppDatabase
import com.learning.dashboard.data.remote.MockApiService
import com.learning.dashboard.data.repository.AuthRepositoryImpl
import com.learning.dashboard.data.repository.CourseRepositoryImpl
import com.learning.dashboard.domain.repository.AuthRepository
import com.learning.dashboard.domain.repository.CourseRepository
import com.learning.dashboard.domain.usecase.CalculateCourseProgressUseCase
import com.learning.dashboard.domain.usecase.GetCourseDetailUseCase
import com.learning.dashboard.domain.usecase.GetCoursesUseCase
import com.learning.dashboard.domain.usecase.LoginUseCase
import com.learning.dashboard.domain.usecase.ToggleLessonCompletionUseCase

class AppContainer(context: Context) {

    val dispatchers: DispatcherProvider by lazy {
        DefaultDispatcherProvider()
    }

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val apiService: MockApiService by lazy {
        MockApiService()
    }

    val calculateCourseProgressUseCase: CalculateCourseProgressUseCase by lazy {
        CalculateCourseProgressUseCase()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            apiService = apiService,
            dispatchers = dispatchers
        )
    }

    val courseRepository: CourseRepository by lazy {
        CourseRepositoryImpl(
            courseDao = database.courseDao(),
            lessonDao = database.lessonDao(),
            apiService = apiService,
            calculateProgressUseCase = calculateCourseProgressUseCase,
            dispatchers = dispatchers
        )
    }

    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(authRepository = authRepository)
    }

    val getCoursesUseCase: GetCoursesUseCase by lazy {
        GetCoursesUseCase(courseRepository = courseRepository)
    }

    val getCourseDetailUseCase: GetCourseDetailUseCase by lazy {
        GetCourseDetailUseCase(courseRepository = courseRepository)
    }

    val toggleLessonCompletionUseCase: ToggleLessonCompletionUseCase by lazy {
        ToggleLessonCompletionUseCase(courseRepository = courseRepository)
    }
}
