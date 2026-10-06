package com.latechhub.finance.ui.savingsgoals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.ApiErrorHandler
import com.latechhub.finance.data.remote.CreateSavingsContributionRequest
import com.latechhub.finance.data.remote.CreateSavingsGoalRequest
import com.latechhub.finance.data.remote.SavingsContribution
import com.latechhub.finance.data.remote.SavingsGoal
import com.latechhub.finance.data.remote.SavingsGoalRepository
import com.latechhub.finance.data.remote.UpdateSavingsContributionRequest
import com.latechhub.finance.data.remote.UpdateSavingsGoalRequest
import kotlinx.coroutines.launch

sealed class SavingsGoalState {
    data object Loading : SavingsGoalState()
    data class Success(val goals: List<SavingsGoal>) : SavingsGoalState()
    data class Error(val message: String) : SavingsGoalState()
}

sealed class ContributionsState {
    data object Loading : ContributionsState()
    data class Success(val contributions: List<SavingsContribution>) : ContributionsState()
    data class Error(val message: String) : ContributionsState()
}

class SavingsGoalViewModel(
    private val repository: SavingsGoalRepository
) : ViewModel() {

    var state: SavingsGoalState by mutableStateOf(SavingsGoalState.Loading)
        private set

    var contributionsState: ContributionsState by mutableStateOf(ContributionsState.Loading)
        private set

    fun loadGoals(status: String? = null) {
        state = SavingsGoalState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getGoals(status)

                state = if (response.success && response.data != null) {
                    SavingsGoalState.Success(response.data)
                } else {
                    SavingsGoalState.Error(response.message)
                }
            } catch (e: Exception) {
                state = SavingsGoalState.Error(
                    ApiErrorHandler.getMessage(e, "Unable to load savings goals")
                )
            }
        }
    }

    fun createGoal(
        request: CreateSavingsGoalRequest,
        onResult: (Boolean, String, SavingsGoal?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createGoal(request)

                if (response.success && response.data != null) {
                    loadGoals()
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to create savings goal"),
                    null
                )
            }
        }
    }

    fun updateGoal(
        id: String,
        request: UpdateSavingsGoalRequest,
        onResult: (Boolean, String, SavingsGoal?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.updateGoal(id, request)

                if (response.success && response.data != null) {
                    loadGoals()
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to update savings goal"),
                    null
                )
            }
        }
    }

    fun deleteGoal(
        id: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.deleteGoal(id)

                if (response.success) {
                    loadGoals()
                    onResult(true, response.message)
                } else {
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to delete savings goal")
                )
            }
        }
    }

    fun loadGoal(
        goalId: String,
        onResult: (SavingsGoal?) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getGoal(goalId)

                if (response.success && response.data != null) {
                    onResult(response.data)
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
    fun loadContributions(goalId: String) {
        contributionsState = ContributionsState.Loading

        viewModelScope.launch {
            try {
                val response = repository.getContributions(goalId)

                contributionsState = if (response.success && response.data != null) {
                    ContributionsState.Success(response.data)
                } else {
                    ContributionsState.Error(response.message)
                }
            } catch (e: Exception) {
                contributionsState = ContributionsState.Error(
                    ApiErrorHandler.getMessage(e, "Unable to load contributions")
                )
            }
        }
    }

    fun createContribution(
        goalId: String,
        request: CreateSavingsContributionRequest,
        onResult: (Boolean, String, SavingsContribution?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createContribution(goalId, request)

                if (response.success && response.data != null) {
                    loadGoals()
                    loadContributions(goalId)
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to add contribution"),
                    null
                )
            }
        }
    }

    fun updateContribution(
        goalId: String,
        contributionId: String,
        request: UpdateSavingsContributionRequest,
        onResult: (Boolean, String, SavingsContribution?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.updateContribution(
                    goalId,
                    contributionId,
                    request
                )

                if (response.success && response.data != null) {
                    loadGoals()
                    loadContributions(goalId)
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to update contribution"),
                    null
                )
            }
        }
    }

    fun deleteContribution(
        goalId: String,
        contributionId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.deleteContribution(
                    goalId,
                    contributionId
                )

                if (response.success) {
                    loadGoals()
                    loadContributions(goalId)
                    onResult(true, response.message)
                } else {
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to delete contribution")
                )
            }
        }
    }

    init {
        loadGoals()
    }
}

