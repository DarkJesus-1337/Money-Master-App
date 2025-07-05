package com.pixelpioneer.moneymaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pixelpioneer.moneymaster.ui.screens.budgets.AddBudgetScreen
import com.pixelpioneer.moneymaster.ui.screens.budgets.BudgetDetailScreen
import com.pixelpioneer.moneymaster.ui.screens.budgets.BudgetsScreen
import com.pixelpioneer.moneymaster.ui.screens.dashboard.DashboardScreen
import com.pixelpioneer.moneymaster.ui.screens.settings.SettingsScreen
import com.pixelpioneer.moneymaster.ui.screens.statistics.StatisticsScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.AddTransactionScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.TransactionDetailScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.TransactionsScreen
import com.pixelpioneer.moneymaster.ui.viewmodel.BudgetViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.CategoryViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel

@Composable
fun MoneyMasterNavHost(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    budgetViewModel: BudgetViewModel,
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
                budgetViewModel = budgetViewModel
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
                transactionViewModel = transactionViewModel
            )
        }

        composable(
            Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0
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

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                navController = navController,
                transactionViewModel = transactionViewModel
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController
            )
        }

        composable(
            Screen.BudgetDetail.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.LongType })
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getLong("budgetId") ?: 0
            BudgetDetailScreen(
                navController = navController,
                budgetId = budgetId,
                budgetViewModel = budgetViewModel,
                transactionViewModel = transactionViewModel
            )
        }
    }
}