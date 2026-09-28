package com.latechhub.finance.ui.session

import com.latechhub.finance.data.remote.User

sealed interface SessionState {

    data object Loading : SessionState

    data object Unauthenticated : SessionState

    data class Authenticated(
        val user: User
    ) : SessionState

    data class Error(
        val message: String
    ) : SessionState
}
