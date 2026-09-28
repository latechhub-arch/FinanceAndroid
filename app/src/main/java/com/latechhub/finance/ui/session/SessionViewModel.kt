package com.latechhub.finance.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                    e.message ?: "Unable to verify session"
                )
            }
        }
    }
}

