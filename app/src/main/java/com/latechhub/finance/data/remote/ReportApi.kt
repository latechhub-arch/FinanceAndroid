package com.latechhub.finance.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApi {

    @GET("api/v1/reports/summary")
    suspend fun getSummary(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): ApiResponse<ReportSummary>

    @GET("api/v1/reports/expenses-by-category")
    suspend fun getExpensesByCategory(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): ApiResponse<List<ExpenseByCategory>>

    @GET("api/v1/reports/by-account")
    suspend fun getByAccount(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): ApiResponse<List<AccountReport>>
}
