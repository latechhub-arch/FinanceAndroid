package com.latechhub.finance.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class FinancialAccount(
    val id: String,
    val userId: String,
    val name: String,
    val type: String,
    val institution: String?,
    val accountNumber: String?,
    val currency: String,
    val balance: Double,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
