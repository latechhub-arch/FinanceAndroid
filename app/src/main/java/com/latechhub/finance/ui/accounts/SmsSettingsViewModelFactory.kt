package com.latechhub.finance.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.latechhub.finance.data.local.SmsSettingsStore

class SmsSettingsViewModelFactory(
    private val store: SmsSettingsStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(SmsSettingsViewModel::class.java)) {
            return SmsSettingsViewModel(store) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}
