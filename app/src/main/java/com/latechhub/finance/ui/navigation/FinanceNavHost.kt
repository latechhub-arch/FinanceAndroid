package com.latechhub.finance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.latechhub.finance.ui.accounts.AccountState
import com.latechhub.finance.ui.accounts.AccountViewModel
import com.latechhub.finance.ui.accounts.AccountsScreen
import com.latechhub.finance.ui.dashboard.DashboardScreen
import com.latechhub.finance.ui.dashboard.DashboardViewModel
import com.latechhub.finance.ui.dashboard.TransactionViewModel
import com.latechhub.finance.ui.transactions.TransactionDetailsScreen
import com.latechhub.finance.ui.transactions.TransactionsScreen

@Composable
fun FinanceNavHost(
    dashboardViewModel: DashboardViewModel,
    transactionViewModel: TransactionViewModel,
    accountViewModel: AccountViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val selectedTransaction = remember { mutableStateOf<com.latechhub.finance.data.remote.TransactionItem?>(null) }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.DASHBOARD
    ) {
        composable(NavRoutes.LOGIN) {
            Text("Login")
        }

        composable(NavRoutes.DASHBOARD) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                transactionViewModel = transactionViewModel,
                onTransactionsClick = {
                    navController.navigate(NavRoutes.TRANSACTIONS)
                },
                onAccountsClick = {
                    navController.navigate(NavRoutes.ACCOUNTS)
                },
                onLogout = onLogout
            )
        }

        composable(NavRoutes.ACCOUNTS) {
            val accountState = accountViewModel.state.value

            when (accountState) {
                is AccountState.Success -> {
                    AccountsScreen(
                        accounts = accountState.accounts
                    )
                }

                AccountState.Loading -> {
                    Text("Loading accounts...")
                }

                is AccountState.Error -> {
                    Text(accountState.message)
                }
            }
        }

        composable(NavRoutes.TRANSACTIONS) {
            TransactionsScreen(
                transactionViewModel = transactionViewModel,
                onTransactionClick = { transaction ->
                    selectedTransaction.value = transaction
                    navController.navigate(
                        NavRoutes.TRANSACTION_DETAILS.replace(
                            "{transactionId}",
                            transaction.id
                        )
                    )
                }
            )
        }

        composable(NavRoutes.TRANSACTION_DETAILS) {
            val transaction = selectedTransaction.value

            if (transaction != null) {
                TransactionDetailsScreen(
                    transaction = transaction
                )
            } else {
                Text("Transaction not available")
            }
        }

        composable(NavRoutes.MORE) {
            Text("More")
        }
    }
}
