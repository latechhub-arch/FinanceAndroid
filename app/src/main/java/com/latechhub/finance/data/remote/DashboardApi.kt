package com.latechhub.finance.data.remote

import retrofit2.http.GET

interface DashboardApi {

    @GET("api/v1/dashboard")
    suspend fun getDashboard(): ApiResponse<DashboardData>
}
