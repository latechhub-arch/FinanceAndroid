package com.latechhub.finance.data.remote

class TransactionRepository {
    private val transactionApi = RetrofitClient.transactionApi

    suspend fun getTransactions(
        page: Int = 1,
        limit: Int = 20
    ): ApiResponse<TransactionListData> {
        return transactionApi.getTransactions(
            page = page,
            limit = limit
        )
    }

    suspend fun getRecentTransactions(
        limit: Int = 5
    ): ApiResponse<TransactionListData> {
        return getTransactions(
            page = 1,
            limit = limit
        )
    }
}
