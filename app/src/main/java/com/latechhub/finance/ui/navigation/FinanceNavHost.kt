package com.latechhub.finance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text

@Composable
fun FinanceNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.DASHBOARD
    ) {
        composable(NavRoutes.LOGIN) {
            Text("Login")
        }

        composable(NavRoutes.DASHBOARD) {
            Text("Dashboard")
        }

        composable(NavRoutes.TRANSACTIONS) {
            Text("Transactions")
        }

        composable(NavRoutes.MORE) {
            Text("More")
        }
    }
}

