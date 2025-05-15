package com.pixelpioneer.moneymaster.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Home)
    object Transactions : Screen("transactions", "Transaktionen", Icons.AutoMirrored.Filled.List)
    object AddTransaction : Screen("add_transaction", "Neue Transaktion", Icons.Filled.Add)
    object TransactionDetail : Screen("transaction_detail/{transactionId}", "Details") {
        fun createRoute(transactionId: Long) = "transaction_detail/$transactionId"
    }
    object Budgets : Screen("budgets", "Budgets", Icons.Filled.DateRange)
    object AddBudget : Screen("add_budget", "Budget hinzufügen")
    object Statistics : Screen("statistics", "Statistiken", Icons.Filled.PieChart)
    object Settings : Screen("settings", "Einstellungen", Icons.Filled.Settings)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Transactions,
    Screen.Budgets,
    Screen.Statistics,
    Screen.Settings
)