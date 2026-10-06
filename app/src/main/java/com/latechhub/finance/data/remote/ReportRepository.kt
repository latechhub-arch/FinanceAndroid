package com.latechhub.finance.data.remote

class ReportRepository {
    private val reportApi = RetrofitClient.reportApi

    suspend fun getSummary(
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<ReportSummary> {
        return reportApi.getSummary(
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getExpensesByCategory(
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<List<ExpenseByCategory>> {
        return reportApi.getExpensesByCategory(
            startDate = startDate,
            endDate = endDate
        )
    }

    suspend fun getByAccount(
        startDate: String? = null,
        endDate: String? = null
    ): ApiResponse<List<AccountReport>> {
        return reportApi.getByAccount(
            startDate = startDate,
            endDate = endDate
        )
    }
}
