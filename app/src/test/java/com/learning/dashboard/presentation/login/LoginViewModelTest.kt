package com.learning.dashboard.presentation.login

import com.learning.dashboard.core.Resource
import com.learning.dashboard.domain.model.User
import com.learning.dashboard.domain.usecase.LoginUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mockk()
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty and login button is disabled`() {
        val state = viewModel.uiState.value
        assertEquals("", state.email)
        assertEquals("", state.password)
        assertFalse(state.isLoginButtonEnabled)
        assertFalse(state.isLoading)
        assertNull(state.loggedInUser)
    }

    @Test
    fun `fillDemoCredentials populates valid email and password`() {
        viewModel.fillDemoCredentials()
        val state = viewModel.uiState.value
        assertEquals("developer@learning.com", state.email)
        assertEquals("password123", state.password)
        assertTrue(state.isLoginButtonEnabled)
    }

    @Test
    fun `login with validation errors updates uiState with error strings`() {
        every { loginUseCase.validateEmail("bad") } returns "Invalid email format"
        every { loginUseCase.validatePassword("123") } returns "Password must be at least 6 characters"

        viewModel.onEmailChanged("bad")
        viewModel.onPasswordChanged("123")
        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Invalid email format", state.emailError)
        assertEquals("Password must be at least 6 characters", state.passwordError)
        assertNull(state.loggedInUser)
    }

    @Test
    fun `login success updates loggedInUser in uiState`() {
        val mockUser = User(id = "1", email = "test@example.com", name = "Test User", token = "token123")
        every { loginUseCase.validateEmail("test@example.com") } returns null
        every { loginUseCase.validatePassword("password123") } returns null
        every { loginUseCase("test@example.com", "password123") } returns flowOf(
            Resource.Loading,
            Resource.Success(mockUser)
        )

        viewModel.onEmailChanged("test@example.com")
        viewModel.onPasswordChanged("password123")
        viewModel.login()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(mockUser, state.loggedInUser)
        assertNull(state.errorMessage)
    }
}
