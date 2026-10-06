package com.latechhub.finance.ui.recurringtransactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.latechhub.finance.data.remote.RecurringTransactionRepository

class RecurringTransactionViewModelFactory(
    private val repository: RecurringTransactionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(RecurringTransactionViewModel::class.java)) {
            return RecurringTransactionViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
