package com.latechhub.finance.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RecurringTransaction(
    val id: String,
    val userId: String,
    val accountId: String,
    val type: String,
    val category: String? = null,
    val amount: Double,
    val description: String? = null,
    val frequency: String,
    val startDate: String,
    val endDate: String? = null,
    val nextRunDate: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val account: RecurringAccountSummary
)

@Serializable
data class RecurringAccountSummary(
    val id: String,
    val name: String,
    val type: String,
    val currency: String
)

@Serializable
data class CreateRecurringTransactionRequest(
    val accountId: String,
    val type: String,
    val category: String? = null,
    val amount: Double,
    val description: String? = null,
    val frequency: String,
    val startDate: String,
    val endDate: String? = null,
    val nextRunDate: String
)

@Serializable
data class UpdateRecurringTransactionRequest(
    val accountId: String? = null,
    val type: String? = null,
    val category: String? = null,
    val amount: Double? = null,
    val description: String? = null,
    val frequency: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val nextRunDate: String? = null,
    val status: String? = null
)
