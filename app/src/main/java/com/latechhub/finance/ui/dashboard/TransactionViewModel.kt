package com.latechhub.finance.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.TransactionItem
import com.latechhub.finance.data.remote.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TransactionState {
    data object Loading : TransactionState

    data class Success(
        val transactions: List<TransactionItem>,
        val page: Int,
        val totalPages: Int,
        val total: Int
    ) : TransactionState

    data class Error(val message: String) : TransactionState
}

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<TransactionState>(TransactionState.Loading)

    val state: StateFlow<TransactionState> =
        _state.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        loadPage(1)
    }

    fun loadPage(page: Int) {
        viewModelScope.launch {
            _state.value = TransactionState.Loading

            try {
                val response = repository.getTransactions(
                    page = page,
                    limit = 20
                )

                _state.value =
                    if (response.success && response.data != null) {
                        TransactionState.Success(
                            transactions = response.data.transactions,
                            page = response.data.pagination.page,
                            totalPages = response.data.pagination.totalPages,
                            total = response.data.pagination.total
                        )
                    } else {
                        TransactionState.Error(response.message)
                    }
            } catch (e: Exception) {
                _state.value = TransactionState.Error(
                    e.message ?: "Unable to load transactions"
                )
            }
        }
    }
}
