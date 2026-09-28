package com.latechhub.finance.data.remote

import com.latechhub.finance.data.local.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class AuthAuthenticator(
    private val tokenStorage: TokenStorage
) : Authenticator {

    override fun authenticate(
        route: Route?,
        response: Response
    ): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        return runBlocking {
            try {
                val refreshToken = tokenStorage.refreshToken.first()
                    ?: return@runBlocking null

                val refreshResponse = RetrofitClient.refreshApi.refresh(
                    RefreshTokenRequest(refreshToken)
                )

                if (!refreshResponse.success || refreshResponse.data == null) {
                    tokenStorage.clearTokens()
                    return@runBlocking null
                }

                val authData = refreshResponse.data

                tokenStorage.saveTokens(
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken
                )

                response.request
                    .newBuilder()
                    .header(
                        "Authorization",
                        "Bearer ${authData.accessToken}"
                    )
                    .build()
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse

        while (prior != null) {
            count++
            prior = prior.priorResponse
        }

        return count
    }
}
