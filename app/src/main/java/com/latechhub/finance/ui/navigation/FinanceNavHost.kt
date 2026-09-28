package com.latechhub.finance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.latechhub.finance.ui.dashboard.DashboardScreen
import com.latechhub.finance.ui.dashboard.DashboardViewModel
import com.latechhub.finance.ui.dashboard.TransactionViewModel

@Composable
fun FinanceNavHost(
    dashboardViewModel: DashboardViewModel,
    transactionViewModel: TransactionViewModel
) {
    val navController = rememberNavController()

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
                transactionViewModel = transactionViewModel
            )
        }

        composable(NavRoutes.TRANSACTIONS) {
            Text("Transactions")
        }

        composable(NavRoutes.MORE) {
            Text("More")
        }
    }
}

