package com.latechhub.finance.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<AuthData>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshTokenRequest
    ): ApiResponse<AuthData>

    @GET("api/v1/auth/me")
    suspend fun me(
        @Header("Authorization") authorization: String
    ): ApiResponse<User>

    @POST("api/v1/auth/logout")
    suspend fun logout(
        @Body request: RefreshTokenRequest
    ): ApiResponse<Unit>

    @GET("health")
    suspend fun health(): HealthResponse
}
