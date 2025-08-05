package com.pixelpioneer.moneymaster.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.ui.components.common.buttons.ViewAllTransactionsButton
import com.pixelpioneer.moneymaster.ui.components.common.cards.FinancialSummaryCards
import com.pixelpioneer.moneymaster.ui.components.common.empty.EmptyBudgetsList
import com.pixelpioneer.moneymaster.ui.components.common.empty.EmptyFinancialSummary
import com.pixelpioneer.moneymaster.ui.components.common.empty.EmptyTransactionsList
import com.pixelpioneer.moneymaster.ui.components.common.indicators.ErrorMessage
import com.pixelpioneer.moneymaster.ui.components.common.items.RecentTransactionItem
import com.pixelpioneer.moneymaster.ui.components.features.dashboard.BudgetOverview
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterBottomNavigation
import com.pixelpioneer.moneymaster.ui.navigation.Screen
import com.pixelpioneer.moneymaster.ui.viewmodel.BudgetViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    budgetViewModel: BudgetViewModel
) {
    val financialSummaryState = transactionViewModel.financialSummary.collectAsState().value
    val transactionsState = transactionViewModel.transactionsState.collectAsState().value
    val budgetsState = budgetViewModel.budgetsState.collectAsState().value

    Scaffold(
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = stringResource(R.string.dashboard_add_transaction)
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_title)
                        )
                    }
                }
            )
        },
        bottomBar = { MoneyMasterBottomNavigation(navController) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                when (financialSummaryState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Success -> {
                        FinancialSummaryCards(financialSummaryState.data)
                    }

                    is UiState.Error -> {
                        ErrorMessage(
                            message = financialSummaryState.message,
                            onRetry = { /* Reload data */ }
                        )
                    }

                    is UiState.Empty -> {
                        EmptyFinancialSummary()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.dashboard_budget_overview),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (budgetsState) {
                    is UiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is UiState.Success -> {
                        BudgetOverview(
                            budgets = budgetsState.data,
                            onBudgetsClick = { navController.navigate(Screen.Budgets.route) }
                        )
                    }

                    is UiState.Error -> {
                        ErrorMessage(
                            message = budgetsState.message,
                            onRetry = { budgetViewModel.refreshBudgets() }
                        )
                    }

                    is UiState.Empty -> {
                        EmptyBudgetsList(
                            onAddBudget = { navController.navigate(Screen.AddBudget.route) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.dashboard_recent_transactions),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            when (transactionsState) {
                is UiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is UiState.Success -> {
                    val recentTransactions = transactionsState.data.take(5)

                    items(recentTransactions) { transaction ->
                        RecentTransactionItem(
                            transaction = transaction,
                            onClick = {
                                navController.navigate(
                                    Screen.TransactionDetail.createRoute(
                                        transaction.id
                                    )
                                )
                            },
                            onEdit = {
                                navController.navigate(
                                    Screen.EditTransaction.createRoute(transaction.id.toString())
                                )
                            }
                        )
                    }

                    if (transactionsState.data.size > 5) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            ViewAllTransactionsButton(
                                onClick = { navController.navigate(Screen.Transactions.route) }
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    item {
                        ErrorMessage(
                            message = transactionsState.message,
                            onRetry = {
                                transactionViewModel.refreshFinancialSummary()
                                budgetViewModel.refreshBudgets()
                            }
                        )
                    }
                }

                is UiState.Empty -> {
                    item {
                        EmptyTransactionsList(
                            onAddTransaction = { navController.navigate(Screen.AddTransaction.route) }
                        )
                    }
                }
            }
        }
    }
}