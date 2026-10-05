package com.latechhub.finance.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SavingsGoal(
    val id: String,
    val userId: String,
    val name: String,
    val targetAmount: Double,
    val targetDate: String,
    val currentAmount: Double,
    val remainingAmount: Double,
    val percentageComplete: Double,
    val status: String,
    val contributionCount: Int,
    val contributions: List<SavingsContribution> = emptyList(),
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class SavingsContribution(
    val id: String,
    val goalId: String,
    val amount: Double,
    val contributionDate: String,
    val note: String? = null,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class CreateSavingsGoalRequest(
    val name: String,
    val targetAmount: Double,
    val targetDate: String
)

@Serializable
data class UpdateSavingsGoalRequest(
    val name: String? = null,
    val targetAmount: Double? = null,
    val targetDate: String? = null
)

@Serializable
data class CreateSavingsContributionRequest(
    val amount: Double,
    val contributionDate: String? = null,
    val note: String? = null
)

@Serializable
data class UpdateSavingsContributionRequest(
    val amount: Double? = null,
    val contributionDate: String? = null,
    val note: String? = null
)
