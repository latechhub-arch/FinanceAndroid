package com.latechhub.finance.data.remote

import com.latechhub.finance.data.local.TokenStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthRepository(
    private val tokenStorage: TokenStorage
) {

    private val authApi = RetrofitClient.authApi

    suspend fun login(
        email: String,
        password: String
    ): ApiResponse<AuthData> {
        val response = authApi.login(
            LoginRequest(
                email = email,
                password = password
            )
        )

        if (response.success && response.data != null) {
            tokenStorage.saveTokens(
                accessToken = response.data.accessToken,
                refreshToken = response.data.refreshToken
            )
        }

        return response
    }

    suspend fun getCurrentUser(): ApiResponse<User> {
        val accessToken = tokenStorage.accessToken.first()
            ?: throw IllegalStateException("No saved access token")

        return authApi.me("Bearer $accessToken")
    }


    fun getSavedAccessToken(): Flow<String?> {
        return tokenStorage.accessToken
    }

    suspend fun logout(): ApiResponse<Unit>? {
        val refreshToken = tokenStorage.refreshToken.first()

        return try {
            if (!refreshToken.isNullOrBlank()) {
                authApi.logout(
                    RefreshTokenRequest(refreshToken)
                )
            } else {
                null
            }
        } finally {
            tokenStorage.clearTokens()
        }
    }
}




