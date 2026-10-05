package com.latechhub.finance.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TransactionListData(
    val transactions: List<TransactionItem>,
    val pagination: TransactionPagination
)

@Serializable
data class TransactionPagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

@Serializable
data class TransactionItem(
    val id: String,
    val accountId: String,
    val type: String,
    val category: String?,
    val amount: Double,
    val description: String?,
    val transactionDate: String,
    val reference: String?,
    val source: String,
    val externalReference: String?,
    val importedAt: String?,
    val transferAccountId: String?,
    val createdAt: String,
    val updatedAt: String,
    val account: TransactionAccount
)

@Serializable
data class UpdateTransactionRequest(
    val accountId: String? = null,
    val type: String? = null,
    val category: String? = null,
    val amount: Double? = null,
    val description: String? = null,
    val transactionDate: String? = null,
    val reference: String? = null,
    val transferAccountId: String? = null
)

@Serializable
data class TransactionAccount(
    val id: String,
    val name: String,
    val type: String,
    val institution: String?,
    val accountNumber: String?,
    val currency: String,
    val balance: Double,
    val isActive: Boolean
)


