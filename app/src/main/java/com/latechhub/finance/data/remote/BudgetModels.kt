package com.latechhub.finance.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: String,
    val userId: String,
    val category: String,
    val budgetAmount: Double,
    val actualSpending: Double,
    val remainingAmount: Double,
    val percentageUsed: Double,
    val status: String,
    val startDate: String,
    val endDate: String,
    val transactionCount: Int,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateBudgetRequest(
    val category: String,
    val amount: Double,
    val startDate: String,
    val endDate: String
)

@Serializable
data class UpdateBudgetRequest(
    val category: String? = null,
    val amount: Double? = null,
    val startDate: String? = null,
    val endDate: String? = null
)
