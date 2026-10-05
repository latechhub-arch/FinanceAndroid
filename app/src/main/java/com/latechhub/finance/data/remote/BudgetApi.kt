package com.latechhub.finance.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BudgetApi {

    @POST("api/v1/budgets")
    suspend fun createBudget(
        @Body request: CreateBudgetRequest
    ): ApiResponse<Budget>

    @GET("api/v1/budgets")
    suspend fun getBudgets(
        @Query("category") category: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): ApiResponse<List<Budget>>

    @GET("api/v1/budgets/{id}")
    suspend fun getBudget(
        @Path("id") id: String
    ): ApiResponse<Budget>

    @PATCH("api/v1/budgets/{id}")
    suspend fun updateBudget(
        @Path("id") id: String,
        @Body request: UpdateBudgetRequest
    ): ApiResponse<Budget>

    @DELETE("api/v1/budgets/{id}")
    suspend fun deleteBudget(
        @Path("id") id: String
    ): ApiResponse<Unit>
}
