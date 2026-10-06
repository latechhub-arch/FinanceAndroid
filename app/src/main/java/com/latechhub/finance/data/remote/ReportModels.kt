package com.latechhub.finance.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ReportSummary(
    val totalIncome: Double,
    val totalExpenses: Double,
    val fulizaBorrowing: Double,
    val fulizaRepayment: Double,
    val fulizaCharges: Double,
    val netCashFlow: Double,
    val transactionCount: Int,
    val period: ReportPeriod
)

@Serializable
data class ReportPeriod(
    val startDate: String? = null,
    val endDate: String? = null
)

@Serializable
data class ExpenseByCategory(
    val category: String,
    val totalAmount: Double,
    val transactionCount: Int
)

@Serializable
data class AccountReport(
    val accountId: String,
    val accountName: String,
    val totalIncome: Double,
    val totalExpenses: Double,
    val netCashFlow: Double,
    val transactionCount: Int
)
