package com.latechhub.finance.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TransactionApi {

    @POST("api/v1/transactions")
    suspend fun createTransaction(
        @Body request: CreateTransactionRequest
    ): ApiResponse<TransactionItem>

    @GET("api/v1/transactions")
    suspend fun getTransactions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null,
        @Query("type") type: String? = null,
        @Query("accountId") accountId: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("minAmount") minAmount: Double? = null,
        @Query("maxAmount") maxAmount: Double? = null
    ): ApiResponse<TransactionListData>

    @PATCH("api/v1/transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: String,
        @Body request: UpdateTransactionRequest
    ): ApiResponse<TransactionItem>

    @DELETE("api/v1/transactions/{id}")
    suspend fun deleteTransaction(
        @Path("id") id: String
    ): ApiResponse<TransactionItem?>
 
    @POST("api/v1/transactions/import/mpesa")
    suspend fun importMpesaTransaction(
        @Body request: MpesaImportRequest
    ): ApiResponse<TransactionItem>

    @POST("api/v1/transactions/import/mpesa-sms")
    suspend fun importMpesaSmsTransaction(
        @Body request: MpesaSmsImportRequest
    ): ApiResponse<TransactionItem>
    @POST("api/v1/transactions/import/bank-sms")
    suspend fun importBankSmsTransaction(
        @Body request: BankSmsImportRequest
    ): ApiResponse<TransactionItem>

    @POST("api/v1/transactions/import/fuliza")
    suspend fun importFulizaTransaction(
        @Body request: FulizaImportRequest
    ): ApiResponse<TransactionItem>

    @POST("api/v1/transactions/import/fuliza-repayment-sms")
    suspend fun importFulizaRepaymentSms(
        @Body request: FulizaRepaymentSmsImportRequest
    ): ApiResponse<TransactionItem>
}




