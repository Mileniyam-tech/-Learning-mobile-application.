package com.learning.dashboard.data.repository

import com.learning.dashboard.core.DispatcherProvider
import com.learning.dashboard.core.Resource
import com.learning.dashboard.data.remote.MockApiService
import com.learning.dashboard.domain.model.User
import com.learning.dashboard.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepositoryImpl(
    private val apiService: MockApiService,
    private val dispatchers: DispatcherProvider
) : AuthRepository {

    private var currentUser: User? = null

    override fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val user = apiService.login(email, password)
            currentUser = user
            emit(Resource.Success(user))
        } catch (e: Exception) {
            val message = e.message ?: "An unexpected authentication error occurred"
            emit(Resource.Error(message, e))
        }
    }.flowOn(dispatchers.io)

    override fun getCurrentUser(): User? = currentUser

    override fun logout() {
        currentUser = null
    }
}
