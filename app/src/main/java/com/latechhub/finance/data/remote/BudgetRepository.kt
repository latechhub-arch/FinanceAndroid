package com.latechhub.finance.data.remote

class BudgetRepository {
    private val budgetApi = RetrofitClient.budgetApi

    suspend fun getBudgets(
        category: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<List<Budget>> {
        return budgetApi.getBudgets(
            category = category,
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getBudget(id: String): ApiResponse<Budget> {
        return budgetApi.getBudget(id)
    }

    suspend fun createBudget(
        request: CreateBudgetRequest
    ): ApiResponse<Budget> {
        return budgetApi.createBudget(request)
    }

    suspend fun updateBudget(
        id: String,
        request: UpdateBudgetRequest
    ): ApiResponse<Budget> {
        return budgetApi.updateBudget(id, request)
    }

    suspend fun deleteBudget(id: String): ApiResponse<Unit> {
        return budgetApi.deleteBudget(id)
    }
}
