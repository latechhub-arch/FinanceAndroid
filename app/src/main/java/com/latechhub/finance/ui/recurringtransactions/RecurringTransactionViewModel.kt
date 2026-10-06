package com.latechhub.finance.ui.recurringtransactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.ApiErrorHandler
import com.latechhub.finance.data.remote.CreateRecurringTransactionRequest
import com.latechhub.finance.data.remote.RecurringTransaction
import com.latechhub.finance.data.remote.RecurringTransactionRepository
import com.latechhub.finance.data.remote.UpdateRecurringTransactionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecurringTransactionFilters(
    val status: String? = null,
    val type: String? = null,
    val frequency: String? = null
)

sealed class RecurringTransactionState {
    data object Loading : RecurringTransactionState()
    data class Success(
        val recurringTransactions: List<RecurringTransaction>
    ) : RecurringTransactionState()
    data class Error(val message: String) : RecurringTransactionState()
}

class RecurringTransactionViewModel(
    private val repository: RecurringTransactionRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<RecurringTransactionState>(
            RecurringTransactionState.Loading
        )

    val state: StateFlow<RecurringTransactionState> =
        _state.asStateFlow()

    private val _filters =
        MutableStateFlow(RecurringTransactionFilters())

    val filters: StateFlow<RecurringTransactionFilters> =
        _filters.asStateFlow()

    init {
        loadRecurringTransactions()
    }

    fun loadRecurringTransactions(
        status: String? = _filters.value.status,
        type: String? = _filters.value.type,
        frequency: String? = _filters.value.frequency
    ) {
        _filters.value = RecurringTransactionFilters(
            status = status,
            type = type,
            frequency = frequency
        )

        viewModelScope.launch {
            _state.value = RecurringTransactionState.Loading

            try {
                val response = repository.getAll(
                    status = status,
                    type = type,
                    frequency = frequency
                )

                if (response.success && response.data != null) {
                    _state.value =
                        RecurringTransactionState.Success(response.data)
                } else {
                    _state.value =
                        RecurringTransactionState.Error(response.message)
                }
            } catch (e: Exception) {
                _state.value =
                    RecurringTransactionState.Error(
                        ApiErrorHandler.getMessage(e, "Unable to load recurring transactions")
                    )
            }
        }
    }

    fun createRecurringTransaction(
        request: CreateRecurringTransactionRequest,
        onResult: (Boolean, String, RecurringTransaction?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.create(request)

                if (response.success && response.data != null) {
                    loadRecurringTransactions()
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to create recurring transaction"),
                    null
                )
            }
        }
    }

    fun updateRecurringTransaction(
        id: String,
        request: UpdateRecurringTransactionRequest,
        onResult: (Boolean, String, RecurringTransaction?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.update(id, request)

                if (response.success && response.data != null) {
                    loadRecurringTransactions()
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to update recurring transaction"),
                    null
                )
            }
        }
    }

    fun deleteRecurringTransaction(
        id: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.delete(id)

                if (response.success) {
                    loadRecurringTransactions()
                    onResult(true, response.message)
                } else {
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to delete recurring transaction")
                )
            }
        }
    }

    fun getRecurringTransaction(
        id: String,
        onResult: (Boolean, String, RecurringTransaction?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getById(id)

                if (response.success && response.data != null) {
                    onResult(true, response.message, response.data)
                } else {
                    onResult(false, response.message, null)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to load recurring transaction"),
                    null
                )
            }
        }
    }
}



