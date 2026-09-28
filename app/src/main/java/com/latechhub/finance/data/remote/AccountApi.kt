package com.latechhub.finance.data.remote

import retrofit2.http.GET

interface AccountApi {
    @GET("api/v1/accounts")
    suspend fun getAccounts(): ApiResponse<List<FinancialAccount>>
}
