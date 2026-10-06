package com.latechhub.finance.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecurringTransactionApi {

    @POST("api/v1/recurring-transactions")
    suspend fun create(
        @Body request: CreateRecurringTransactionRequest
    ): RecurringTransactionResponse<RecurringTransaction>

    @GET("api/v1/recurring-transactions")
    suspend fun getAll(
        @Query("status") status: String? = null,
        @Query("type") type: String? = null,
        @Query("frequency") frequency: String? = null
    ): RecurringTransactionResponse<List<RecurringTransaction>>

    @GET("api/v1/recurring-transactions/{id}")
    suspend fun getById(
        @Path("id") id: String
    ): RecurringTransactionResponse<RecurringTransaction>

    @PATCH("api/v1/recurring-transactions/{id}")
    suspend fun update(
        @Path("id") id: String,
        @Body request: UpdateRecurringTransactionRequest
    ): RecurringTransactionResponse<RecurringTransaction>

    @DELETE("api/v1/recurring-transactions/{id}")
    suspend fun delete(
        @Path("id") id: String
    ): RecurringDeleteResponse
}

@kotlinx.serialization.Serializable
data class RecurringDeleteResponse(
    val success: Boolean,
    val message: String
)

@kotlinx.serialization.Serializable
data class RecurringTransactionResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String = "",
    val statusCode: Int? = null
)

