package com.latechhub.finance.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latechhub.finance.data.remote.DashboardData
import com.latechhub.finance.data.remote.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DashboardState {

    data object Loading : DashboardState

    data class Success(
        val dashboard: DashboardData
    ) : DashboardState

    data class Error(
        val message: String
    ) : DashboardState
}

class DashboardViewModel(
    private val repository: DashboardRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<DashboardState>(DashboardState.Loading)

    val state: StateFlow<DashboardState> =
        _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _state.value = DashboardState.Loading

            try {
                val response = repository.getDashboard()

                _state.value =
                    if (response.success && response.data != null) {
                        DashboardState.Success(response.data)
                    } else {
                        DashboardState.Error(response.message)
                    }
            } catch (e: Exception) {
                _state.value = DashboardState.Error(
                    e.message ?: "Unable to load dashboard"
                )
            }
        }
    }
}
