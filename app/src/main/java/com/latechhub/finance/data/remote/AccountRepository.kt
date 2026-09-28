package com.latechhub.finance.data.remote

class AccountRepository {
    private val accountApi = RetrofitClient.accountApi

    suspend fun getAccounts(): ApiResponse<List<FinancialAccount>> {
        return accountApi.getAccounts()
    }
}
