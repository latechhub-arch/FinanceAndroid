package com.latechhub.finance.data.remote

class RecurringTransactionRepository {
    private val api = RetrofitClient.recurringTransactionApi

    suspend fun create(
        request: CreateRecurringTransactionRequest
    ): RecurringTransactionResponse<RecurringTransaction> {
        return api.create(request)
    }

    suspend fun getAll(
        status: String? = null,
        type: String? = null,
        frequency: String? = null
    ): RecurringTransactionResponse<List<RecurringTransaction>> {
        return api.getAll(
            status = status,
            type = type,
            frequency = frequency
        )
    }

    suspend fun getById(
        id: String
    ): RecurringTransactionResponse<RecurringTransaction> {
        return api.getById(id)
    }

    suspend fun update(
        id: String,
        request: UpdateRecurringTransactionRequest
    ): RecurringTransactionResponse<RecurringTransaction> {
        return api.update(id, request)
    }

    suspend fun delete(
        id: String
    ): RecurringDeleteResponse {
        return api.delete(id)
    }
}

