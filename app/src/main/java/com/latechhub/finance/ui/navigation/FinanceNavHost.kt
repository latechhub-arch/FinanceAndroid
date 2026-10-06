package com.latechhub.finance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.latechhub.finance.ui.accounts.AccountState
import com.latechhub.finance.ui.accounts.AccountViewModel
import com.latechhub.finance.ui.accounts.AccountsScreen
import com.latechhub.finance.ui.budgets.BudgetScreen
import com.latechhub.finance.ui.budgets.BudgetState
import com.latechhub.finance.ui.budgets.BudgetViewModel
import com.latechhub.finance.ui.dashboard.DashboardScreen
import com.latechhub.finance.ui.dashboard.DashboardViewModel
import com.latechhub.finance.ui.dashboard.TransactionViewModel
import com.latechhub.finance.ui.savingsgoals.SavingsGoalDetailsScreen
import com.latechhub.finance.ui.savingsgoals.SavingsGoalScreen
import com.latechhub.finance.ui.savingsgoals.SavingsGoalState
import com.latechhub.finance.ui.savingsgoals.SavingsGoalViewModel
import com.latechhub.finance.ui.reports.ReportViewModel
import com.latechhub.finance.ui.reports.ReportsScreen
import com.latechhub.finance.ui.transactions.TransactionDetailsScreen
import com.latechhub.finance.ui.transactions.TransactionsScreen

@Composable
fun FinanceNavHost(
    dashboardViewModel: DashboardViewModel,
    transactionViewModel: TransactionViewModel,
    accountViewModel: AccountViewModel,
    budgetViewModel: BudgetViewModel,
    savingsGoalViewModel: SavingsGoalViewModel,
    reportViewModel: ReportViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val selectedTransaction =
        remember {
            mutableStateOf<com.latechhub.finance.data.remote.TransactionItem?>(null)
        }

    val selectedSavingsGoal =
        remember {
            mutableStateOf<com.latechhub.finance.data.remote.SavingsGoal?>(null)
        }

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
                onBudgetsClick = {
                    navController.navigate(NavRoutes.BUDGETS)
                },
                onSavingsGoalsClick = {
                    navController.navigate(NavRoutes.SAVINGS_GOALS)
                },
                onReportsClick = {
                    navController.navigate(NavRoutes.REPORTS)
                },
                onLogout = onLogout
            )
        }

        composable(NavRoutes.ACCOUNTS) {
            val accountState = accountViewModel.state.collectAsState().value

            when (accountState) {
                is AccountState.Success -> {
                    AccountsScreen(
                        accounts = accountState.accounts,
                        viewModel = accountViewModel
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

        composable(NavRoutes.BUDGETS) {
            val budgetState = budgetViewModel.state.collectAsState().value

            when (budgetState) {
                is BudgetState.Success -> {
                    BudgetScreen(
                        budgets = budgetState.budgets,
                        viewModel = budgetViewModel
                    )
                }

                BudgetState.Loading -> {
                    Text("Loading budgets...")
                }

                is BudgetState.Error -> {
                    Text(budgetState.message)
                }
            }
        }

        composable(NavRoutes.REPORTS) {
            ReportsScreen(
                viewModel = reportViewModel
            )
        }

        composable(NavRoutes.SAVINGS_GOALS) {
            val savingsGoalState = savingsGoalViewModel.state

            when (savingsGoalState) {
                is SavingsGoalState.Success -> {
                    SavingsGoalScreen(
                        goals = savingsGoalState.goals,
                        viewModel = savingsGoalViewModel,
                        onGoalClick = { goal ->
                            selectedSavingsGoal.value = goal
                            navController.navigate(
                                NavRoutes.SAVINGS_GOAL_DETAILS.replace(
                                    "{goalId}",
                                    goal.id
                                )
                            )
                        }
                    )
                }

                SavingsGoalState.Loading -> {
                    Text("Loading savings goals...")
                }

                is SavingsGoalState.Error -> {
                    Text(savingsGoalState.message)
                }
            }
        }

        composable(NavRoutes.SAVINGS_GOAL_DETAILS) {
            val goal = selectedSavingsGoal.value

            if (goal != null) {
                SavingsGoalDetailsScreen(
                    goal = goal,
                    viewModel = savingsGoalViewModel,
                    onBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                Text("Savings goal not available")
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
            val accountState = accountViewModel.state.collectAsState().value

            if (
                transaction != null &&
                accountState is AccountState.Success
            ) {
                TransactionDetailsScreen(
                    transaction = transaction,
                    viewModel = transactionViewModel,
                    accounts = accountState.accounts,
                    onTransactionUpdated = { updated ->
                        selectedTransaction.value = updated
                    }
                )
            } else if (transaction != null) {
                Text(
                    if (accountState is AccountState.Error) {
                        accountState.message
                    } else {
                        "Loading accounts..."
                    }
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




