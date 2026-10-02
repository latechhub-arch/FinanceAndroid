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

@Serializable
data class CreateAccountRequest(
    val name: String,
    val type: String,
    val institution: String? = null,
    val accountNumber: String? = null,
    val currency: String = "KES",
    val balance: Double = 0.0
)

@Serializable
data class UpdateAccountRequest(
    val name: String? = null,
    val type: String? = null,
    val institution: String? = null,
    val accountNumber: String? = null,
    val currency: String? = null,
    val balance: Double? = null,
    val isActive: Boolean? = null
)
