package com.latechhub.finance.ui.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.ApiErrorHandler
import com.latechhub.finance.data.remote.Budget
import com.latechhub.finance.data.remote.BudgetRepository
import com.latechhub.finance.data.remote.CreateBudgetRequest
import com.latechhub.finance.data.remote.UpdateBudgetRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BudgetState {
    data object Loading : BudgetState()
    data class Success(val budgets: List<Budget>) : BudgetState()
    data class Error(val message: String) : BudgetState()
}

class BudgetViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _state = MutableStateFlow<BudgetState>(BudgetState.Loading)
    val state: StateFlow<BudgetState> = _state.asStateFlow()

    init {
        loadBudgets()
    }

    fun loadBudgets(
        category: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            _state.value = BudgetState.Loading

            try {
                val response = repository.getBudgets(
                    category = category,
                    startDate = startDate,
                    endDate = endDate
                )

                if (response.success && response.data != null) {
                    _state.value = BudgetState.Success(response.data)
                } else {
                    _state.value = BudgetState.Error(response.message)
                }
            } catch (e: Exception) {
                _state.value = BudgetState.Error(
                    ApiErrorHandler.getMessage(e, "Unable to load budgets")
                )
            }
        }
    }

    fun createBudget(
        request: CreateBudgetRequest,
        onResult: (Boolean, String, Budget?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createBudget(request)

                if (response.success && response.data != null) {
                    loadBudgets()
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to create budget"),
                    null
                )
            }
        }
    }

    fun updateBudget(
        id: String,
        request: UpdateBudgetRequest,
        onResult: (Boolean, String, Budget?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.updateBudget(id, request)

                if (response.success && response.data != null) {
                    loadBudgets()
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to update budget"),
                    null
                )
            }
        }
    }

    fun deleteBudget(
        id: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.deleteBudget(id)

                if (response.success) {
                    loadBudgets()
                    onResult(true, response.message)
                } else {
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to delete budget")
                )
            }
        }
    }
}


