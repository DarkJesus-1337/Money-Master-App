package com.pixelpioneer.moneymaster.ui.navigation

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.pixelpioneer.moneymaster.R

/**
 * Represents a navigation destination (screen) in the MoneyMaster app.
 *
 * Each screen has a route, a string resource for its title, and optionally an icon.
 */
sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector? = null
) {
    data object Dashboard : Screen("dashboard", R.string.nav_dashboard, Icons.Filled.Home)
    data object Transactions :
        Screen("transactions", R.string.nav_transactions, Icons.AutoMirrored.Filled.List)

    data object Budgets : Screen("budgets", R.string.nav_budgets, Icons.Filled.DateRange)
    data object Statistics : Screen("statistics", R.string.nav_statistics, Icons.Filled.PieChart)
    data object ReceiptScan : Screen("receipt_scan", R.string.nav_receipts, Icons.Filled.Camera)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings)

    data object AddBudget : Screen("add_budget", R.string.nav_add_budget)
    data object AddTransaction :
        Screen("add_transaction", R.string.nav_add_transaction, Icons.Filled.Add)

    data object TransactionDetail :
        Screen("transaction_detail/{transactionId}", R.string.nav_transaction_details) {
        fun createRoute(transactionId: Long) = "transaction_detail/$transactionId"
    }

    data object BudgetDetail : Screen("budget_detail/{budgetId}", R.string.nav_budget_details) {
        fun createRoute(budgetId: Long) = "budget_detail/$budgetId"
    }

    data object EditBudget : Screen("edit_budget/{budgetId}", R.string.nav_edit_budget) {
        fun createRoute(budgetId: Long) = "edit_budget/$budgetId"
    }

    data object EditTransaction :
        Screen("edit_transaction/{transactionId}", R.string.nav_edit_transaction) {
        fun createRoute(transactionId: String) = "edit_transaction/$transactionId"
    }

    fun getTitle(context: Context): String = context.getString(titleRes)

}

/**
 * List of screens to be shown in the bottom navigation bar.
 */
val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Transactions,
    Screen.Budgets,
    Screen.Statistics,
    Screen.ReceiptScan
)