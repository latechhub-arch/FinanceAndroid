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
import com.latechhub.finance.data.remote.AccountRepository
import com.latechhub.finance.data.remote.AuthRepository
import com.latechhub.finance.data.remote.BudgetRepository
import com.latechhub.finance.data.remote.DashboardRepository
import com.latechhub.finance.data.remote.TransactionRepository
import com.latechhub.finance.data.remote.SavingsGoalRepository
import com.latechhub.finance.data.remote.ReportRepository
import com.latechhub.finance.ui.accounts.AccountViewModel
import com.latechhub.finance.ui.accounts.AccountViewModelFactory
import com.latechhub.finance.ui.budgets.BudgetViewModel
import com.latechhub.finance.ui.budgets.BudgetViewModelFactory
import com.latechhub.finance.ui.auth.LoginScreen
import com.latechhub.finance.ui.dashboard.DashboardViewModel
import com.latechhub.finance.ui.dashboard.DashboardViewModelFactory
import com.latechhub.finance.ui.dashboard.TransactionViewModel
import com.latechhub.finance.ui.dashboard.TransactionViewModelFactory
import com.latechhub.finance.ui.navigation.FinanceNavHost
import com.latechhub.finance.ui.savingsgoals.SavingsGoalViewModel
import com.latechhub.finance.ui.savingsgoals.SavingsGoalViewModelFactory
import com.latechhub.finance.ui.reports.ReportViewModel
import com.latechhub.finance.ui.reports.ReportViewModelFactory
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

        val accountViewModel = ViewModelProvider(
            this,
            AccountViewModelFactory(AccountRepository())
        )[AccountViewModel::class.java]

        val budgetViewModel = ViewModelProvider(
            this,
            BudgetViewModelFactory(BudgetRepository())
        )[BudgetViewModel::class.java]

        val savingsGoalViewModel = ViewModelProvider(
            this,
            SavingsGoalViewModelFactory(SavingsGoalRepository())
        )[SavingsGoalViewModel::class.java]

        val reportViewModel = ViewModelProvider(
            this,
            ReportViewModelFactory(ReportRepository())
        )[ReportViewModel::class.java]

        setContent {
            FinanceTheme {
                SessionRoot(
                    viewModel = viewModel,
                    dashboardViewModel = dashboardViewModel,
                    transactionViewModel = transactionViewModel,
                    accountViewModel = accountViewModel,
                    budgetViewModel = budgetViewModel,
                    savingsGoalViewModel = savingsGoalViewModel,
                    reportViewModel = reportViewModel
                )
            }
        }
    }
}

@Composable
fun SessionRoot(
    viewModel: SessionViewModel,
    dashboardViewModel: DashboardViewModel,
    transactionViewModel: TransactionViewModel,
    accountViewModel: AccountViewModel,
    budgetViewModel: BudgetViewModel,
    savingsGoalViewModel: SavingsGoalViewModel,
    reportViewModel: ReportViewModel
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
                transactionViewModel = transactionViewModel,
                accountViewModel = accountViewModel,
                budgetViewModel = budgetViewModel,
                savingsGoalViewModel = savingsGoalViewModel,
                reportViewModel = reportViewModel,
                onLogout = {
                    viewModel.logout()
                }
            )
        }
    }
}
