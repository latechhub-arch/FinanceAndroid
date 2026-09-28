package com.latechhub.finance.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val data: T? = null
)

@Serializable
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val isActive: Boolean,
    val emailVerified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class AuthData(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class HealthResponse(
    val status: String,
    val service: String
)
