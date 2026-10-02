package com.latechhub.finance.ui.accounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.AccountRepository
import com.latechhub.finance.data.remote.CreateAccountRequest
import com.latechhub.finance.data.remote.UpdateAccountRequest
import com.latechhub.finance.data.remote.FinancialAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AccountState {

    data object Loading : AccountState

    data class Success(
        val accounts: List<FinancialAccount>
    ) : AccountState

    data class Error(
        val message: String
    ) : AccountState
}

class AccountViewModel(
    private val repository: AccountRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<AccountState>(AccountState.Loading)

    val state: StateFlow<AccountState> =
        _state.asStateFlow()

    init {
        loadAccounts()
    }

    fun updateAccount(
        id: String,
        request: UpdateAccountRequest,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.updateAccount(id, request)

                if (response.success && response.data != null) {
                    val accountsResponse = repository.getAccounts()

                    _state.value =
                        if (accountsResponse.success && accountsResponse.data != null) {
                            AccountState.Success(accountsResponse.data)
                        } else {
                            AccountState.Error(accountsResponse.message)
                        }

                    onResult(true, response.message)
                } else {
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    e.message ?: "Unable to update account"
                )
            }
        }
    }

    fun deleteAccount(
        id: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.deleteAccount(id)

                if (response.success) {
                    val accountsResponse = repository.getAccounts()

                    _state.value =
                        if (accountsResponse.success && accountsResponse.data != null) {
                            AccountState.Success(accountsResponse.data)
                        } else {
                            AccountState.Error(accountsResponse.message)
                        }

                    onResult(true, response.message)
                } else {
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    e.message ?: "Unable to delete account"
                )
            }
        }
    }

    fun createAccount(
        request: CreateAccountRequest,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.createAccount(request)

                if (response.success && response.data != null) {
                    val accountsResponse = repository.getAccounts()

                    _state.value =
                        if (accountsResponse.success && accountsResponse.data != null) {
                            AccountState.Success(accountsResponse.data)
                        } else {
                            AccountState.Error(accountsResponse.message)
                        }

                    onResult(true, response.message)
                } else {
                    onResult(false, response.message)
                }
            } catch (e: Exception) {
                onResult(
                    false,
                    e.message ?: "Unable to create account"
                )
            }
        }
    }

    fun loadAccounts() {
        viewModelScope.launch {
            _state.value = AccountState.Loading

            try {
                val response = repository.getAccounts()

                _state.value =
                    if (response.success && response.data != null) {
                        AccountState.Success(response.data)
                    } else {
                        AccountState.Error(response.message)
                    }
            } catch (e: Exception) {
                _state.value = AccountState.Error(
                    e.message ?: "Unable to load accounts"
                )
            }
        }
    }
}



