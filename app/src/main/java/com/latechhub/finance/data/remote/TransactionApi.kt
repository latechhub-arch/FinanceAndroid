package com.latechhub.finance.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TransactionApi {
    @GET("api/v1/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 5
    ): ApiResponse<TransactionListData>
}
