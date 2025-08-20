package com.pixelpioneer.moneymaster.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.ui.components.common.dialogs.AddEditCategoryDialog
import com.pixelpioneer.moneymaster.ui.components.common.dialogs.GenericDeleteDialog
import com.pixelpioneer.moneymaster.ui.components.common.empty.EmptyState
import com.pixelpioneer.moneymaster.ui.components.common.error.ErrorState
import com.pixelpioneer.moneymaster.ui.components.common.lists.CategoryList
import com.pixelpioneer.moneymaster.ui.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val categoriesState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<TransactionCategory?>(null) }
    var categoryToDelete by remember { mutableStateOf<TransactionCategory?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.category_management_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = stringResource(R.string.action_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.category_add))
            }
        }
    ) { paddingValues ->
        when (val state = categoriesState) {
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
                val categories = state.data
                if (categories.isEmpty()) {
                    EmptyState(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                } else {
                    CategoryList(
                        categories = categories,
                        modifier = Modifier.padding(paddingValues),
                        onEditCategory = { category ->
                            categoryToEdit = category
                            showAddDialog = true
                        },
                        onDeleteCategory = { category ->
                            if (category.id > 10) {
                                categoryToDelete = category
                            }
                        }
                    )
                }
            }
            is UiState.Error -> {
                ErrorState(
                    message = state.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onRetry = { viewModel.refreshCategories() }
                )
            }
            is UiState.Empty -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }

    if (showAddDialog) {
        AddEditCategoryDialog(
            category = categoryToEdit,
            onDismiss = {
                showAddDialog = false
                categoryToEdit = null
            },
            onSave = { name, color ->
                scope.launch {
                    if (categoryToEdit != null) {
                        viewModel.updateCategory(
                            categoryToEdit!!.copy(name = name, color = color)
                        )
                    } else {
                        viewModel.addCategory(name, color)
                    }
                    showAddDialog = false
                    categoryToEdit = null
                }
            }
        )
    }

    GenericDeleteDialog(
        showDialog = categoryToDelete != null,
        title = stringResource(R.string.category_delete),
        message = stringResource(R.string.category_delete_confirmation_message),
        itemName = categoryToDelete?.name,
        onConfirm = {
            scope.launch {
                categoryToDelete?.let { category ->
                    viewModel.deleteCategory(category)
                }
                categoryToDelete = null
            }
        },
        onDismiss = {
            categoryToDelete = null
        }
    )
}