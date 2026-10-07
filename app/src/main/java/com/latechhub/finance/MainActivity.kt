package com.latechhub.finance

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle

import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.latechhub.finance.data.local.TokenStorage
import com.latechhub.finance.data.local.SmsSettingsStore
import com.latechhub.finance.data.remote.AccountRepository
import com.latechhub.finance.data.remote.AuthRepository
import com.latechhub.finance.data.remote.BudgetRepository
import com.latechhub.finance.data.remote.DashboardRepository
import com.latechhub.finance.data.remote.TransactionRepository
import com.latechhub.finance.data.remote.SavingsGoalRepository
import com.latechhub.finance.data.remote.ReportRepository
import com.latechhub.finance.data.remote.RecurringTransactionRepository
import com.latechhub.finance.ui.accounts.AccountViewModel
import com.latechhub.finance.ui.accounts.AccountViewModelFactory
import com.latechhub.finance.ui.accounts.SmsSettingsViewModel
import com.latechhub.finance.ui.accounts.SmsSettingsViewModelFactory
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
import com.latechhub.finance.ui.recurringtransactions.RecurringTransactionViewModel
import com.latechhub.finance.ui.recurringtransactions.RecurringTransactionViewModelFactory
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

        val smsSettingsViewModel = ViewModelProvider(
            this,
            SmsSettingsViewModelFactory(SmsSettingsStore(applicationContext))
        )[SmsSettingsViewModel::class.java]

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

        val recurringTransactionViewModel = ViewModelProvider(
            this,
            RecurringTransactionViewModelFactory(RecurringTransactionRepository())
        )[RecurringTransactionViewModel::class.java]

        setContent {
            FinanceTheme {
                SessionRoot(
                    viewModel = viewModel,
                    dashboardViewModel = dashboardViewModel,
                    transactionViewModel = transactionViewModel,
                    accountViewModel = accountViewModel,
                    smsSettingsViewModel = smsSettingsViewModel,
                    budgetViewModel = budgetViewModel,
                    savingsGoalViewModel = savingsGoalViewModel,
                    reportViewModel = reportViewModel,
                    recurringTransactionViewModel = recurringTransactionViewModel
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
    smsSettingsViewModel: SmsSettingsViewModel,
    budgetViewModel: BudgetViewModel,
    savingsGoalViewModel: SavingsGoalViewModel,
    reportViewModel: ReportViewModel,
    recurringTransactionViewModel: RecurringTransactionViewModel
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { }
    )
    val isAuthenticated = state is SessionState.Authenticated

    LaunchedEffect(isAuthenticated) {
        if (
            isAuthenticated &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            smsPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
        }
    }

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
                    smsSettingsViewModel = smsSettingsViewModel,
                budgetViewModel = budgetViewModel,
                savingsGoalViewModel = savingsGoalViewModel,
                reportViewModel = reportViewModel,
                recurringTransactionViewModel = recurringTransactionViewModel,
                onLogout = {
                    viewModel.logout()
                }
            )
        }
    }
}










