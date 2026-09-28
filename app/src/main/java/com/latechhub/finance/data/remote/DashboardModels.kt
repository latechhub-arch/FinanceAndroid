package com.latechhub.finance.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class DashboardData(
    val accounts: DashboardAccounts,
    val cashFlow: DashboardCashFlow,
    val budgets: DashboardBudgets,
    val savingsGoals: DashboardSavingsGoals,
    val recurringTransactions: DashboardRecurringTransactions
)

@Serializable
data class DashboardAccounts(
    val totalAccounts: Int,
    val activeAccounts: Int,
    val currencies: List<String>,
    val balancesByCurrency: Map<String, Double>,
    val accounts: List<DashboardAccount>
)

@Serializable
data class DashboardAccount(
    val id: String,
    val name: String,
    val type: String,
    val currency: String,
    val balance: Double,
    val isActive: Boolean
)

@Serializable
data class DashboardCashFlow(
    val totalIncome: Double,
    val totalExpenses: Double,
    val netCashFlow: Double,
    val transactionCount: Int
)

@Serializable
data class DashboardBudgets(
    val total: Int,
    val active: Int,
    val overBudget: Int
)

@Serializable
data class DashboardSavingsGoals(
    val total: Int,
    val completed: Int,
    val inProgress: Int,
    val overdue: Int
)

@Serializable
data class DashboardRecurringTransactions(
    val active: Int,
    val upcoming: List<DashboardRecurringTransaction>
)

@Serializable
data class DashboardRecurringTransaction(
    val id: String,
    val type: String,
    val category: String?,
    val amount: Double,
    val description: String?,
    val frequency: String,
    val nextRunDate: String,
    val account: DashboardRecurringAccount?
)

@Serializable
data class DashboardRecurringAccount(
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
    val currency: String? = null,
    val balance: Double? = null,
    val isActive: Boolean? = null
)
