package com.pixelpioneer.moneymaster.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
   data object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Home)
   data object Transactions : Screen("transactions", "Transactions", Icons.AutoMirrored.Filled.List)
   data object AddTransaction : Screen("add_transaction", "New Transaction", Icons.Filled.Add)
   data object TransactionDetail : Screen("transaction_detail/{transactionId}", "Details") {
        fun createRoute(transactionId: Long) = "transaction_detail/$transactionId"
    }
   data object Budgets : Screen("budgets", "Budgets", Icons.Filled.DateRange)
   data object AddBudget : Screen("add_budget", "Add Budget")
   data object Statistics : Screen("statistics", "Statistics", Icons.Filled.PieChart)
   data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Transactions,
    Screen.Budgets,
    Screen.Statistics,
    Screen.Settings
)