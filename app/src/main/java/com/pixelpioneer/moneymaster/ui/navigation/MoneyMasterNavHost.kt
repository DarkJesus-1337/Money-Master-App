package com.pixelpioneer.moneymaster.ui.navigation



import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pixelpioneer.moneymaster.ui.screens.budgets.BudgetsScreen
import com.pixelpioneer.moneymaster.ui.screens.dashboard.DashboardScreen
import com.pixelpioneer.moneymaster.ui.screens.settings.SettingsScreen
import com.pixelpioneer.moneymaster.ui.screens.statistics.StatisticsScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.AddTransactionScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.TransactionDetailScreen
import com.pixelpioneer.moneymaster.ui.screens.transactions.TransactionsScreen

@Composable
fun MoneyMasterNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }
        composable(Screen.Transactions.route) {
            TransactionsScreen(navController)
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(navController)
        }
        composable(
            Screen.TransactionDetail.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0
            TransactionDetailScreen(navController, transactionId)
        }
        composable(Screen.Budgets.route) {
            BudgetsScreen(navController)
        }
        composable(Screen.AddBudget.route) {
            // AddBudgetScreen(navController)
            // Placeholder bis zur Implementierung
            Text("Add Budget Screen")
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}