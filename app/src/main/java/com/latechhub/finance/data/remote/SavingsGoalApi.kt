package com.latechhub.finance.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SavingsGoalApi {

    @POST("api/v1/savings-goals")
    suspend fun createGoal(
        @Body request: CreateSavingsGoalRequest
    ): ApiResponse<SavingsGoal>

    @GET("api/v1/savings-goals")
    suspend fun getGoals(
        @Query("status") status: String? = null
    ): ApiResponse<List<SavingsGoal>>

    @GET("api/v1/savings-goals/{id}")
    suspend fun getGoal(
        @Path("id") id: String
    ): ApiResponse<SavingsGoal>

    @PATCH("api/v1/savings-goals/{id}")
    suspend fun updateGoal(
        @Path("id") id: String,
        @Body request: UpdateSavingsGoalRequest
    ): ApiResponse<SavingsGoal>

    @DELETE("api/v1/savings-goals/{id}")
    suspend fun deleteGoal(
        @Path("id") id: String
    ): ApiResponse<Unit>

    @POST("api/v1/savings-goals/{id}/contributions")
    suspend fun createContribution(
        @Path("id") goalId: String,
        @Body request: CreateSavingsContributionRequest
    ): ApiResponse<SavingsContribution>

    @GET("api/v1/savings-goals/{id}/contributions")
    suspend fun getContributions(
        @Path("id") goalId: String
    ): ApiResponse<List<SavingsContribution>>

    @PATCH("api/v1/savings-goals/{id}/contributions/{contributionId}")
    suspend fun updateContribution(
        @Path("id") goalId: String,
        @Path("contributionId") contributionId: String,
        @Body request: UpdateSavingsContributionRequest
    ): ApiResponse<SavingsContribution>

    @DELETE("api/v1/savings-goals/{id}/contributions/{contributionId}")
    suspend fun deleteContribution(
        @Path("id") goalId: String,
        @Path("contributionId") contributionId: String
    ): ApiResponse<Unit>
}
