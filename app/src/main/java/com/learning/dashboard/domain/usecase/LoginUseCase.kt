package com.learning.dashboard.domain.usecase

import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.model.User
import com.learning.dashboard.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.regex.Pattern

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    private val emailPattern: Pattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    operator fun invoke(email: String, password: String): Flow<Resource<User>> {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isEmpty()) {
            return flow { emit(Resource.Error("Email address cannot be empty")) }
        }

        if (!emailPattern.matcher(trimmedEmail).matches()) {
            return flow { emit(Resource.Error("Please enter a valid email address")) }
        }

        if (trimmedPassword.isEmpty()) {
            return flow { emit(Resource.Error("Password cannot be empty")) }
        }

        if (trimmedPassword.length < 6) {
            return flow { emit(Resource.Error("Password must be at least 6 characters long")) }
        }

        return authRepository.login(trimmedEmail, trimmedPassword)
    }

    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> "Email cannot be empty"
            !emailPattern.matcher(trimmed).matches() -> "Invalid email format (e.g. user@example.com)"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        val trimmed = password.trim()
        return when {
            trimmed.isEmpty() -> "Password cannot be empty"
            trimmed.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}
