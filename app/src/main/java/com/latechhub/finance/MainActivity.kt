package com.latechhub.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.latechhub.finance.data.local.TokenStorage
import com.latechhub.finance.data.remote.AuthRepository
import com.latechhub.finance.data.remote.DashboardRepository
import com.latechhub.finance.data.remote.TransactionRepository
import com.latechhub.finance.ui.auth.LoginScreen
import com.latechhub.finance.ui.dashboard.DashboardViewModel
import com.latechhub.finance.ui.dashboard.DashboardViewModelFactory
import com.latechhub.finance.ui.dashboard.TransactionViewModel
import com.latechhub.finance.ui.dashboard.TransactionViewModelFactory
import com.latechhub.finance.ui.navigation.FinanceNavHost
import com.latechhub.finance.ui.session.SessionState
import com.latechhub.finance.ui.session.SessionViewModel
import com.latechhub.finance.ui.session.SessionViewModelFactory
import com.latechhub.finance.ui.theme.FinanceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AuthRepository(
            TokenStorage(applicationContext)
        )

        val viewModel = ViewModelProvider(
            this,
            SessionViewModelFactory(repository)
        )[SessionViewModel::class.java]

        val dashboardViewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(DashboardRepository())
        )[DashboardViewModel::class.java]

        val transactionViewModel = ViewModelProvider(
            this,
            TransactionViewModelFactory(TransactionRepository())
        )[TransactionViewModel::class.java]

        setContent {
            FinanceTheme {
                SessionRoot(
                    viewModel = viewModel,
                    dashboardViewModel = dashboardViewModel,
                    transactionViewModel = transactionViewModel
                )
            }
        }
    }
}

@Composable
fun SessionRoot(
    viewModel: SessionViewModel,
    dashboardViewModel: DashboardViewModel,
    transactionViewModel: TransactionViewModel
) {
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        SessionState.Loading -> {
            Text("Checking session...")
        }

        SessionState.Unauthenticated,
        SessionState.LoggingIn,
        is SessionState.Error -> {
            LoginScreen(
                viewModel = viewModel,
                state = currentState
            )
        }

        is SessionState.Authenticated -> {
            FinanceNavHost(
                dashboardViewModel = dashboardViewModel,
                transactionViewModel = transactionViewModel
            )
        }


    }
}

