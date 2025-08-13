package com.pixelpioneer.moneymaster.ui.screens.budgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.ui.components.common.cards.BudgetDetailCard
import com.pixelpioneer.moneymaster.ui.components.common.indicators.ErrorMessage
import com.pixelpioneer.moneymaster.ui.components.common.items.RecentTransactionItem
import com.pixelpioneer.moneymaster.ui.navigation.Screen
import com.pixelpioneer.moneymaster.ui.viewmodel.BudgetViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailScreen(
    navController: NavController,
    budgetId: Long,
    budgetViewModel: BudgetViewModel,
    transactionViewModel: TransactionViewModel
) {
    val budgetsState = budgetViewModel.budgetsState.collectAsState().value
    val transactionsState = transactionViewModel.transactionsState.collectAsState().value

    val budget = when (budgetsState) {
        is UiState.Success -> budgetsState.data.find { it.id == budgetId }
        else -> null
    }

    val categoryTransactions = when (transactionsState) {
        is UiState.Success -> {
            budget?.let { b ->
                transactionsState.data.filter { it.category.id == b.category.id && it.isExpense }
            } ?: emptyList()
        }

        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        budget?.category?.name ?: stringResource(R.string.nav_budget_details)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    budget?.let { b ->
                        IconButton(onClick = {
                            navController.navigate(Screen.EditBudget.createRoute(b.id))
                        }) {
                            Icon(
                                painterResource(R.drawable.ic_edit),
                                contentDescription = stringResource(R.string.budgets_edit)
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (budgetsState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Success -> {
                budget?.let { b ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            BudgetDetailCard(budget = b)
                        }

                        item {
                            Text(
                                text = stringResource(R.string.transactions_in_category),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
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
                                if (categoryTransactions.isEmpty()) {
                                    item {
                                        Text(
                                            text = stringResource(R.string.empty_transaction_category),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    items(categoryTransactions) { transaction ->
                                        RecentTransactionItem(
                                            transaction = transaction,
                                            onClick = {
                                                navController.navigate(
                                                    Screen.TransactionDetail.createRoute(transaction.id)
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            is UiState.Error -> {
                                item {
                                    ErrorMessage(
                                        message = transactionsState.message,
                                        onRetry = { transactionViewModel.refreshTransactions() }
                                    )
                                }
                            }

                            is UiState.Empty -> {
                                item {
                                    Text(
                                        text = stringResource(R.string.no_transactions_found),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } ?: run {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_budgets_found),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorMessage(
                        message = budgetsState.message,
                        onRetry = { budgetViewModel.refreshBudgets() }
                    )
                }
            }

            is UiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_budgets_found),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}
