package com.latechhub.finance.data.remote

import com.latechhub.finance.data.local.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            tokenStorage.accessToken.first()
        }

        val request = chain.request()

        if (accessToken.isNullOrBlank()) {
            return chain.proceed(request)
        }

        return chain.proceed(
            request.newBuilder()
                .header(
                    "Authorization",
                    "Bearer $accessToken"
                )
                .build()
        )
    }
}
