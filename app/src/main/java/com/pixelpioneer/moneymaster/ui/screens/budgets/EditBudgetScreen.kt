package com.pixelpioneer.moneymaster.ui.screens.budgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.ui.components.common.dialogs.DeleteBudgetDialog
import com.pixelpioneer.moneymaster.ui.components.utils.getBudgetPeriodText
import com.pixelpioneer.moneymaster.ui.viewmodel.BudgetViewModel
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.ui.components.common.dialogs.CategorySelectorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(
    navController: NavController,
    budgetId: Long,
    budgetViewModel: BudgetViewModel
) {
    val formState by budgetViewModel.budgetFormState.collectAsState()
    val categoriesState by budgetViewModel.categoriesState.collectAsState()
    val budgetsState by budgetViewModel.budgetsState.collectAsState()

    var showCategorySelector by remember { mutableStateOf(false) }
    var periodDropdownExpanded by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val currentBudgetState = budgetsState
    val budget = when (currentBudgetState) {
        is UiState.Success -> currentBudgetState.data.find { it.id == budgetId }
        else -> null
    }

    LaunchedEffect(budget) {
        budget?.let { b ->
            budgetViewModel.initFormWithBudget(b)
            amountText = b.amount.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.budgets_edit)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.budgets_delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = amountText,
                onValueChange = { newValue ->
                    amountText = newValue
                    val amount = newValue.toDoubleOrNull() ?: 0.0
                    budgetViewModel.updateAmount(amount)
                },
                label = { Text(stringResource(R.string.budgets_amount)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = formState.amountError != null,
                supportingText = {
                    formState.amountError?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCategorySelector = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (formState.selectedCategory != null) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(formState.selectedCategory!!.color))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = formState.selectedCategory!!.name,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.select_category),
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.select_category)
                    )
                }
            }

            formState.categoryError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Box {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { periodDropdownExpanded = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getBudgetPeriodText(formState.period),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.select_period)
                        )
                    }
                }

                DropdownMenu(
                    expanded = periodDropdownExpanded,
                    onDismissRequest = { periodDropdownExpanded = false }
                ) {
                    BudgetPeriod.entries.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(getBudgetPeriodText(period)) },
                            onClick = {
                                budgetViewModel.updatePeriod(period)
                                periodDropdownExpanded = false
                            },
                            trailingIcon = {
                                if (formState.period == period) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = stringResource(R.string.label_selected)
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.action_delete))
                }

                Button(
                    onClick = {
                        budgetViewModel.updateBudget(budgetId)
                        navController.popBackStack()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = formState.amount > 0 && formState.selectedCategory != null
                ) {
                    Text(stringResource(R.string.budgets_update))
                }
            }
        }
    }

    if (showCategorySelector) {
        CategorySelectorDialog(
            categoriesState = categoriesState,
            onCategorySelected = { category ->
                budgetViewModel.updateSelectedCategory(category)
                showCategorySelector = false
            },
            onDismiss = { showCategorySelector = false }
        )
    }

    if (showDeleteDialog && budget != null) {
        DeleteBudgetDialog(
            budget = budget,
            onConfirm = {
                budgetViewModel.deleteBudget(budget)
                showDeleteDialog = false
                navController.popBackStack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}