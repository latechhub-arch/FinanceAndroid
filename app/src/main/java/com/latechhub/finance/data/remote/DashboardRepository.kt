package com.latechhub.finance.data.remote

class DashboardRepository {

    private val dashboardApi = RetrofitClient.dashboardApi

    suspend fun getDashboard(): ApiResponse<DashboardData> {
        return dashboardApi.getDashboard()
    }
}
