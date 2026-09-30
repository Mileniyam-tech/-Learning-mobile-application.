package com.learning.dashboard.domain.usecase

import app.cash.turbine.test
import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.model.User
import com.learning.dashboard.domain.repository.AuthRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        authRepository = mockk()
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `validateEmail returns error for empty or invalid email`() {
        assertNotNull(loginUseCase.validateEmail(""))
        assertNotNull(loginUseCase.validateEmail("   "))
        assertNotNull(loginUseCase.validateEmail("invalid-email"))
        assertNotNull(loginUseCase.validateEmail("user@"))
        assertNotNull(loginUseCase.validateEmail("@example.com"))

        assertNull(loginUseCase.validateEmail("test@example.com"))
        assertNull(loginUseCase.validateEmail("developer.lead@learning.org"))
    }

    @Test
    fun `validatePassword returns error for empty or short password`() {
        assertNotNull(loginUseCase.validatePassword(""))
        assertNotNull(loginUseCase.validatePassword("12345"))

        assertNull(loginUseCase.validatePassword("123456"))
        assertNull(loginUseCase.validatePassword("strongPassword123!"))
    }

    @Test
    fun `invoke with invalid email emits Error without calling repository`() = runTest {
        loginUseCase("bademail", "password123").test {
            val item = awaitItem()
            assertTrue(item is Resource.Error)
            assertEquals("Please enter a valid email address", (item as Resource.Error).message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke with valid credentials forwards call to auth repository`() = runTest {
        val mockUser = User(id = "1", email = "test@example.com", name = "Test User", token = "token123")
        every { authRepository.login("test@example.com", "password123") } returns flowOf(
            Resource.Loading,
            Resource.Success(mockUser)
        )

        loginUseCase("test@example.com", "password123").test {
            assertEquals(Resource.Loading, awaitItem())
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(mockUser, (success as Resource.Success).data)
            awaitComplete()
        }
    }
}
