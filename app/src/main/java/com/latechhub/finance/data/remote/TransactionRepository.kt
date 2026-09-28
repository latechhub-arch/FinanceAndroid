package com.latechhub.finance.data.remote

class TransactionRepository {
    private val transactionApi = RetrofitClient.transactionApi

    suspend fun getRecentTransactions(
        limit: Int = 5
    ): ApiResponse<TransactionListData> {
        return transactionApi.getTransactions(
            page = 1,
            limit = limit
        )
    }
}
