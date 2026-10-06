package com.latechhub.finance.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.ApiErrorHandler
import com.latechhub.finance.data.remote.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SessionViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    init {
        checkSession()
    }

    fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _state.value = SessionState.LoggingIn

            try {
                val response = repository.login(
                    email = email,
                    password = password
                )

                _state.value = if (response.success && response.data != null) {
                    SessionState.Authenticated(response.data.user)
                } else {
                    SessionState.Error(response.message)
                }
            } catch (e: Exception) {
                _state.value = SessionState.Error(
                    ApiErrorHandler.getMessage(e, "Login failed")
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (e: Exception) {
                // Local tokens are cleared by the repository even if the API call fails.
            } finally {
                _state.value = SessionState.Unauthenticated
            }
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            try {
                val token = repository.getSavedAccessToken().first()

                if (token.isNullOrBlank()) {
                    _state.value = SessionState.Unauthenticated
                    return@launch
                }

                val response = repository.getCurrentUser()

                _state.value = if (response.success && response.data != null) {
                    SessionState.Authenticated(response.data)
                } else {
                    SessionState.Error(response.message)
                }
            } catch (e: Exception) {
                _state.value = SessionState.Error(
                    ApiErrorHandler.getMessage(e, "Unable to verify session")
                )
            }
        }
    }
}




