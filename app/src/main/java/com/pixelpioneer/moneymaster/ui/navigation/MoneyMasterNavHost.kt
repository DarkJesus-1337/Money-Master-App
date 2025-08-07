package com.pixelpioneer.moneymaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pixelpioneer.moneymaster.ui.screens.budgets.AddBudgetScreen
import com.pixelpioneer.moneymaster.ui.screens.budgets.BudgetDetailScreen
import com.pixelpioneer.moneymaster.ui.screens.budgets.BudgetsScreen
import com.pixelpioneer.moneymaster.ui.screens.budgets.EditBudgetScreen
import com.pixelpioneer.moneymaster.ui.screens.dashboard.DashboardScreen
import com.pixelpioneer.moneymaster.ui.screens.receipts.ReceiptScanScreen
import com.pixelpioneer.moneymaster.ui.screens.settings.SettingsScreen
import com.pixelpioneer.moneymaster.ui.screens.statistics.StatisticsScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.AddTransactionScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.EditTransactionScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.TransactionDetailScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.TransactionsScreen
import com.pixelpioneer.moneymaster.ui.viewmodel.BudgetViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.CategoryViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.CryptoViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.ReceiptScanViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.SettingsViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.StatisticsViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel

/**
 * Sets up the navigation host for the MoneyMaster app.
 *
 * Defines all navigation routes and their associated composable screens.
 *
 * @param navController The NavHostController for navigation.
 * @param modifier Optional modifier for the NavHost.
 */
@Composable
fun MoneyMasterNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            val transactionViewModel: TransactionViewModel = hiltViewModel()
            val budgetViewModel: BudgetViewModel = hiltViewModel()

            DashboardScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                budgetViewModel = budgetViewModel
            )
        }

        composable(Screen.Transactions.route) {
            val transactionViewModel: TransactionViewModel = hiltViewModel()

            TransactionsScreen(
                navController = navController,
                transactionViewModel = transactionViewModel
            )
        }

        composable(Screen.AddTransaction.route) {
            val transactionViewModel: TransactionViewModel = hiltViewModel()

            AddTransactionScreen(
                navController = navController,
                transactionViewModel = transactionViewModel
            )
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
            val transactionViewModel: TransactionViewModel = hiltViewModel()

            TransactionDetailScreen(
                navController = navController,
                transactionId = transactionId,
                transactionViewModel = transactionViewModel
            )
        }

        composable(Screen.Budgets.route) {
            val budgetViewModel: BudgetViewModel = hiltViewModel()

            BudgetsScreen(
                navController = navController,
                budgetViewModel = budgetViewModel
            )
        }

        composable(Screen.AddBudget.route) {
            val budgetViewModel: BudgetViewModel = hiltViewModel()

            AddBudgetScreen(
                navController = navController,
                budgetViewModel = budgetViewModel
            )
        }

        composable(
            route = Screen.BudgetDetail.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.LongType })
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getLong("budgetId") ?: 0L
            val budgetViewModel: BudgetViewModel = hiltViewModel()
            val transactionViewModel: TransactionViewModel = hiltViewModel()

            BudgetDetailScreen(
                navController = navController,
                budgetId = budgetId,
                budgetViewModel = budgetViewModel,
                transactionViewModel = transactionViewModel
            )
        }

        composable(
            route = Screen.EditBudget.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.LongType })
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getLong("budgetId") ?: 0L
            val budgetViewModel: BudgetViewModel = hiltViewModel()

            EditBudgetScreen(
                navController = navController,
                budgetId = budgetId,
                budgetViewModel = budgetViewModel
            )
        }

        composable(Screen.Statistics.route) {
            val statisticsViewModel: StatisticsViewModel = hiltViewModel()
            val cryptoViewModel: CryptoViewModel = hiltViewModel()

            StatisticsScreen(
                navController = navController,
                statisticsViewModel = statisticsViewModel,
                cryptoViewModel = cryptoViewModel
            )
        }

        composable(Screen.ReceiptScan.route) {
            val receiptScanViewModel: ReceiptScanViewModel = hiltViewModel()
            val transactionViewModel: TransactionViewModel = hiltViewModel()
            val categoryViewModel: CategoryViewModel = hiltViewModel()

            ReceiptScanScreen(
                navController = navController,
                receiptScanViewModel = receiptScanViewModel,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel
            )
        }

        composable(
            route = "edit_transaction/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: "0"
            val transactionViewModel: TransactionViewModel = hiltViewModel()
            val categoryViewModel: CategoryViewModel = hiltViewModel()

            EditTransactionScreen(
                navController = navController,
                transactionId = transactionId,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel
            )
        }
    }
}