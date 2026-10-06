package com.latechhub.finance.data.remote

class TransactionRepository {
    private val transactionApi = RetrofitClient.transactionApi

    suspend fun createTransaction(
        request: CreateTransactionRequest
    ): ApiResponse<TransactionItem> {
        return transactionApi.createTransaction(request)
    }

    suspend fun getTransactions(
        page: Int = 1,
        limit: Int = 20,
        category: String? = null,
        type: String? = null,
        accountId: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        minAmount: Double? = null,
        maxAmount: Double? = null
    ): ApiResponse<TransactionListData> {
        return transactionApi.getTransactions(
            page = page,
            limit = limit,
            category = category,
            type = type,
            accountId = accountId,
            startDate = startDate,
            endDate = endDate,
            minAmount = minAmount,
            maxAmount = maxAmount
        )
    }

    suspend fun updateTransaction(
        id: String,
        request: UpdateTransactionRequest
    ): ApiResponse<TransactionItem> {
        return transactionApi.updateTransaction(
            id = id,
            request = request
        )
    }

    suspend fun deleteTransaction(
        id: String
    ): ApiResponse<TransactionItem?> {
        return transactionApi.deleteTransaction(id)
    }

    suspend fun importMpesaTransaction(
        request: MpesaImportRequest
    ): ApiResponse<TransactionItem> {
        return transactionApi.importMpesaTransaction(request)
    }

    suspend fun importBankSmsTransaction(
        request: BankSmsImportRequest
    ): ApiResponse<TransactionItem> {
        return transactionApi.importBankSmsTransaction(request)
    }

    suspend fun importFulizaTransaction(
        request: FulizaImportRequest
    ): ApiResponse<TransactionItem> {
        return transactionApi.importFulizaTransaction(request)
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
