package com.learning.dashboard.domain.repository

import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<User>>
    fun getCurrentUser(): User?
    fun logout()
}
