package com.latechhub.finance.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.local.SmsSettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SmsSettingsState(
    val mpesaAccountId: String? = null,
    val bankAccountId: String? = null
)

class SmsSettingsViewModel(
    private val store: SmsSettingsStore
) : ViewModel() {

    private val _state = MutableStateFlow(SmsSettingsState())
    val state: StateFlow<SmsSettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val mpesaAccountId = store.mpesaAccountId.first()
            val bankAccountId = store.bankAccountId.first()

            _state.value = SmsSettingsState(
                mpesaAccountId = mpesaAccountId,
                bankAccountId = bankAccountId
            )
        }
    }

    fun saveMpesaAccount(accountId: String) {
        viewModelScope.launch {
            store.saveMpesaAccountId(accountId)

            _state.value = _state.value.copy(
                mpesaAccountId = accountId
            )
        }
    }

    fun saveBankAccount(accountId: String) {
        viewModelScope.launch {
            store.saveBankAccountId(accountId)

            _state.value = _state.value.copy(
                bankAccountId = accountId
            )
        }
    }

    fun clearMappings() {
        viewModelScope.launch {
            store.clearMappings()
            _state.value = SmsSettingsState()
        }
    }
}
