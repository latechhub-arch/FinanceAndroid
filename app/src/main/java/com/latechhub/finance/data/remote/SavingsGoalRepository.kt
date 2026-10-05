package com.latechhub.finance.data.remote

class SavingsGoalRepository {

    private val api = RetrofitClient.savingsGoalApi

    suspend fun getGoals(
        status: String? = null
    ): ApiResponse<List<SavingsGoal>> {
        return api.getGoals(status)
    }

    suspend fun getGoal(
        id: String
    ): ApiResponse<SavingsGoal> {
        return api.getGoal(id)
    }

    suspend fun createGoal(
        request: CreateSavingsGoalRequest
    ): ApiResponse<SavingsGoal> {
        return api.createGoal(request)
    }

    suspend fun updateGoal(
        id: String,
        request: UpdateSavingsGoalRequest
    ): ApiResponse<SavingsGoal> {
        return api.updateGoal(id, request)
    }

    suspend fun deleteGoal(
        id: String
    ): ApiResponse<Unit> {
        return api.deleteGoal(id)
    }

    suspend fun getContributions(
        goalId: String
    ): ApiResponse<List<SavingsContribution>> {
        return api.getContributions(goalId)
    }

    suspend fun createContribution(
        goalId: String,
        request: CreateSavingsContributionRequest
    ): ApiResponse<SavingsContribution> {
        return api.createContribution(goalId, request)
    }

    suspend fun updateContribution(
        goalId: String,
        contributionId: String,
        request: UpdateSavingsContributionRequest
    ): ApiResponse<SavingsContribution> {
        return api.updateContribution(goalId, contributionId, request)
    }

    suspend fun deleteContribution(
        goalId: String,
        contributionId: String
    ): ApiResponse<Unit> {
        return api.deleteContribution(goalId, contributionId)
    }
}
