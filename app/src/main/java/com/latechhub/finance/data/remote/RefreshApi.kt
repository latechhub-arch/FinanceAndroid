package com.latechhub.finance.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {

    @POST("api/v1/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequest
    ): ApiResponse<AuthData>
}
