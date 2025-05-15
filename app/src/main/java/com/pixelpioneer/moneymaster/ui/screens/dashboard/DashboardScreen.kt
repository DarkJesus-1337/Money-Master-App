package com.pixelpioneer.moneymaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.ui.components.ErrorMessage
import com.pixelpioneer.moneymaster.ui.components.RecentTransactionsList
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterBottomNavigation
import com.pixelpioneer.moneymaster.ui.navigation.Screen
import com.pixelpioneer.moneymaster.ui.viewmodel.BudgetViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel
import com.pixelpioneer.moneymaster.util.FormatUtils
import com.pixelpioneer.moneymaster.util.UiState
import java.text.NumberFormat
import java.util.Locale

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
        bottomBar = { MoneyMasterBottomNavigation(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTransaction.route) }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Financial Summary Section
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
                    // Show empty state or initial guidance
                    EmptyFinancialSummary()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Transactions Section
            Text(
                text = "Recent Transactions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            when (transactionsState) {
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
                    // Show only the 5 most recent transactions
                    val recentTransactions = transactionsState.data.take(5)
                    RecentTransactionsList(
                        transactions = recentTransactions,
                        onTransactionClick = { transaction ->
                            navController.navigate(Screen.TransactionDetail.createRoute(transaction.id))
                        }
                    )
                    
                    // View All Transactions button
                    if (transactionsState.data.size > 5) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ViewAllTransactionsButton(
                            onClick = { navController.navigate(Screen.Transactions.route) }
                        )
                    }
                }
                is UiState.Error -> {
                    ErrorMessage(
                        message = transactionsState.message,
                        onRetry = { /* Reload data */ }
                    )
                }
                is UiState.Empty -> {
                    EmptyTransactionsList(
                        onAddTransaction = { navController.navigate(Screen.AddTransaction.route) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Budget Overview Section
            Text(
                text = "Budget Overview",
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
                        onRetry = { /* Reload data */ }
                    )
                }
                is UiState.Empty -> {
                    EmptyBudgetsList(
                        onAddBudget = { navController.navigate(Screen.AddBudget.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialSummaryCards(summary: com.pixelpioneer.moneymaster.ui.viewmodel.FinancialSummary) {
    Column {
        // Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Balance,
                            contentDescription = "Balance",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Text(
                    text = FormatUtils.formatCurrency(summary.balance),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        // Income and Expenses Cards in Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Income Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Income",
                            tint = Color.Green
                        )
                        Text(
                            text = "Income",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    
                    Text(
                        text = FormatUtils.formatCurrency(summary.totalIncome),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            // Expenses Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Expenses",
                            tint = Color.Red
                        )
                        Text(
                            text = "Expenses",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    
                    Text(
                        text = FormatUtils.formatCurrency(summary.totalExpenses),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

// Empty State components will be implemented in separate files