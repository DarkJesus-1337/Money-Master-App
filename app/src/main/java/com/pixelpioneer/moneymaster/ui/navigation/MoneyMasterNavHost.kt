package com.pixelpioneer.moneymaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pixelpioneer.moneymaster.data.services.AppUpdateManager
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

@Composable
fun MoneyMasterNavHost(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    budgetViewModel: BudgetViewModel,
    statisticsViewModel: StatisticsViewModel,
    cryptoViewModel: CryptoViewModel,
    receiptScanViewModel: ReceiptScanViewModel,
    settingsViewModel: SettingsViewModel,
    appUpdateManager: AppUpdateManager,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                budgetViewModel = budgetViewModel,
            )
        }

        composable(Screen.Transactions.route) {
            TransactionsScreen(
                navController = navController,
                transactionViewModel = transactionViewModel
            )
        }

        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
            )
        }

        composable(
            route = Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
            TransactionDetailScreen(
                navController = navController,
                transactionId = transactionId,
                transactionViewModel = transactionViewModel
            )
        }

        composable(Screen.Budgets.route) {
            BudgetsScreen(
                navController = navController,
                budgetViewModel = budgetViewModel
            )
        }

        composable(Screen.AddBudget.route) {
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
            EditBudgetScreen(
                navController = navController,
                budgetId = budgetId,
                budgetViewModel = budgetViewModel
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                navController = navController,
                statisticsViewModel = statisticsViewModel,
                cryptoViewModel = cryptoViewModel
            )
        }

        composable(Screen.ReceiptScan.route) {
            ReceiptScanScreen(
                navController = navController,
                receiptScanViewModel = receiptScanViewModel,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
            )
        }

        composable(
            route = "edit_transaction/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId") ?: "0"
            EditTransactionScreen(
                navController = navController,
                transactionId = transactionId,
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settingsViewModel = settingsViewModel,
                appUpdateManager = appUpdateManager
            )
        }
    }
}