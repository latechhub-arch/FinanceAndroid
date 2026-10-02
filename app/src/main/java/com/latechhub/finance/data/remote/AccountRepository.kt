package com.latechhub.finance.data.remote

class AccountRepository {
    private val accountApi = RetrofitClient.accountApi

    suspend fun getAccounts(): ApiResponse<List<FinancialAccount>> {
        return accountApi.getAccounts()
    }

    suspend fun updateAccount(
        id: String,
        request: UpdateAccountRequest
    ): ApiResponse<FinancialAccount> {
        return accountApi.updateAccount(id, request)
    }

    suspend fun deleteAccount(
        id: String
    ): ApiResponse<Unit> {
        return accountApi.deleteAccount(id)
    }

    suspend fun createAccount(
        request: CreateAccountRequest
    ): ApiResponse<FinancialAccount> {
        return accountApi.createAccount(request)
    }
}

