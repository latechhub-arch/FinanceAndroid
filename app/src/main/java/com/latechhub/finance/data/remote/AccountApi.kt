package com.latechhub.finance.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AccountApi {
    @GET("api/v1/accounts")
    suspend fun getAccounts(): ApiResponse<List<FinancialAccount>>

    @PATCH("api/v1/accounts/{id}")
    suspend fun updateAccount(
        @Path("id") id: String,
        @Body request: UpdateAccountRequest
    ): ApiResponse<FinancialAccount>

    @DELETE("api/v1/accounts/{id}")
    suspend fun deleteAccount(
        @Path("id") id: String
    ): ApiResponse<Unit>

    @POST("api/v1/accounts")
    suspend fun createAccount(
        @Body request: CreateAccountRequest
    ): ApiResponse<FinancialAccount>
}

