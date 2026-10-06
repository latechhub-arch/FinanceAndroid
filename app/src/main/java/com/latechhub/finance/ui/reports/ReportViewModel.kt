package com.latechhub.finance.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.AccountReport
import com.latechhub.finance.data.remote.ExpenseByCategory
import com.latechhub.finance.data.remote.ReportRepository
import com.latechhub.finance.data.remote.ReportSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ReportState {
    data object Loading : ReportState()

    data class Success(
        val summary: ReportSummary,
        val expensesByCategory: List<ExpenseByCategory>,
        val accounts: List<AccountReport>
    ) : ReportState()

    data class Error(val message: String) : ReportState()
}

class ReportViewModel(
    private val repository: ReportRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ReportState>(ReportState.Loading)
    val state: StateFlow<ReportState> = _state.asStateFlow()

    init {
        loadReports()
    }

    fun loadReports(
        startDate: String? = null,
        endDate: String? = null
    ) {
        viewModelScope.launch {
            _state.value = ReportState.Loading

            try {
                val summaryResponse = repository.getSummary(
                    startDate = startDate,
                    endDate = endDate
                )

                val categoryResponse = repository.getExpensesByCategory(
                    startDate = startDate,
                    endDate = endDate
                )

                val accountResponse = repository.getByAccount(
                    startDate = startDate,
                    endDate = endDate
                )

                if (
                    summaryResponse.success &&
                    summaryResponse.data != null &&
                    categoryResponse.success &&
                    categoryResponse.data != null &&
                    accountResponse.success &&
                    accountResponse.data != null
                ) {
                    _state.value = ReportState.Success(
                        summary = summaryResponse.data,
                        expensesByCategory = categoryResponse.data,
                        accounts = accountResponse.data
                    )
                } else {
                    val message = when {
                        !summaryResponse.success -> summaryResponse.message
                        !categoryResponse.success -> categoryResponse.message
                        else -> accountResponse.message
                    }

                    _state.value = ReportState.Error(message)
                }
            } catch (e: Exception) {
                _state.value = ReportState.Error(
                    e.message ?: "Unable to load reports"
                )
            }
        }
    }
}
