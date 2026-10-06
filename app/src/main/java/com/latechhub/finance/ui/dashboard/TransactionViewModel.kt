package com.latechhub.finance.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.ApiErrorHandler
import com.latechhub.finance.data.remote.CreateTransactionRequest
import com.latechhub.finance.data.remote.MpesaImportRequest
import com.latechhub.finance.data.remote.BankSmsImportRequest
import com.latechhub.finance.data.remote.FulizaImportRequest
import com.latechhub.finance.data.remote.TransactionItem
import com.latechhub.finance.data.remote.TransactionRepository
import com.latechhub.finance.data.remote.UpdateTransactionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TransactionFilters(
    val category: String? = null,
    val type: String? = null,
    val accountId: String? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null
)

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

    private val _filters =
        MutableStateFlow(TransactionFilters())

    val filters: StateFlow<TransactionFilters> =
        _filters.asStateFlow()

    init {
        loadTransactions()
    }

    fun createTransaction(
        request: CreateTransactionRequest,
        onResult: (Boolean, String, TransactionItem?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createTransaction(request)

                if (response.success && response.data != null) {
                    val currentPage =
                        (state.value as? TransactionState.Success)?.page ?: 1

                    loadPage(currentPage)

                    onResult(
                        true,
                        response.message,
                        response.data
                    )
                } else {
                    onResult(
                        false,
                        response.message,
                        null
                    )
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to create transaction"),
                    null
                )
            }
        }
    }

    fun importMpesaTransaction(
        request: MpesaImportRequest,
        onResult: (Boolean, String, TransactionItem?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.importMpesaTransaction(request)

                if (response.success && response.data != null) {
                    val currentPage =
                        (state.value as? TransactionState.Success)?.page ?: 1

                    loadPage(currentPage)

                    onResult(
                        true,
                        response.message,
                        response.data
                    )
                } else {
                    onResult(
                        false,
                        response.message,
                        null
                    )
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to import M-PESA transaction"),
                    null
                )
            }
        }
    }

    fun importBankSmsTransaction(
        request: BankSmsImportRequest,
        onResult: (Boolean, String, TransactionItem?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.importBankSmsTransaction(request)

                if (response.success && response.data != null) {
                    val currentPage =
                        (state.value as? TransactionState.Success)?.page ?: 1

                    loadPage(currentPage)

                    onResult(
                        true,
                        response.message,
                        response.data
                    )
                } else {
                    onResult(
                        false,
                        response.message,
                        null
                    )
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to import bank SMS transaction"),
                    null
                )
            }
        }
    }

    fun importFulizaTransaction(
        request: FulizaImportRequest,
        onResult: (Boolean, String, TransactionItem?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.importFulizaTransaction(request)

                if (response.success && response.data != null) {
                    val currentPage =
                        (state.value as? TransactionState.Success)?.page ?: 1

                    loadPage(currentPage)

                    onResult(
                        true,
                        response.message,
                        response.data
                    )
                } else {
                    onResult(
                        false,
                        response.message,
                        null
                    )
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to import Fuliza transaction"),
                    null
                )
            }
        }
    }

    fun updateTransaction(
        id: String,
        request: UpdateTransactionRequest,
        onResult: (Boolean, String, TransactionItem?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.updateTransaction(id, request)

                if (response.success && response.data != null) {
                    val updatedTransaction = response.data
                    val currentPage =
                        (state.value as? TransactionState.Success)?.page ?: 1

                    loadPage(currentPage)

                    onResult(
                        true,
                        response.message,
                        updatedTransaction
                    )
                } else {
                    onResult(
                        false,
                        response.message,
                        null
                    )
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to update transaction"),
                    null
                )
            }
        }
    }

    fun deleteTransaction(
        id: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.deleteTransaction(id)

                if (response.success) {
                    val currentState =
                        state.value as? TransactionState.Success

                    val currentPage = currentState?.page ?: 1
                    val currentTotal = currentState?.total ?: 0

                    val targetPage =
                        if (
                            currentPage > 1 &&
                            currentTotal > 0 &&
                            currentTotal - 1 <= (currentPage - 1) * 20
                        ) {
                            currentPage - 1
                        } else {
                            currentPage
                        }

                    loadPage(targetPage)

                    onResult(
                        true,
                        response.message
                    )
                } else {
                    onResult(
                        false,
                        response.message
                    )
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    ApiErrorHandler.getMessage(e, "Unable to delete transaction")
                )
            }
        }
    }

    fun setFilters(
        category: String? = null,
        type: String? = null,
        accountId: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        minAmount: Double? = null,
        maxAmount: Double? = null
    ) {
        _filters.value = TransactionFilters(
            category = category,
            type = type,
            accountId = accountId,
            startDate = startDate,
            endDate = endDate,
            minAmount = minAmount,
            maxAmount = maxAmount
        )

        loadPage(1)
    }

    fun clearFilters() {
        _filters.value = TransactionFilters()
        loadPage(1)
    }

    fun loadTransactions() {
        loadPage(1)
    }

    fun loadPage(page: Int) {
        viewModelScope.launch {
            _state.value = TransactionState.Loading

            try {
                val currentFilters = _filters.value

                val response = repository.getTransactions(
                    page = page,
                    limit = 20,
                    category = currentFilters.category,
                    type = currentFilters.type,
                    accountId = currentFilters.accountId,
                    startDate = currentFilters.startDate,
                    endDate = currentFilters.endDate,
                    minAmount = currentFilters.minAmount,
                    maxAmount = currentFilters.maxAmount
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
                    ApiErrorHandler.getMessage(e, "Unable to load transactions")
                )
            }
        }
    }
}

