package com.pixelpioneer.moneymaster.ui.components.features.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.data.model.CategoryStats
import com.pixelpioneer.moneymaster.ui.components.common.indicators.ErrorMessage
import com.pixelpioneer.moneymaster.ui.components.common.items.CategoryStatsItem

/**
 * Tab for displaying statistics grouped by transaction category.
 *
 * Shows a list of categories with their statistics, or loading/error/empty states.
 *
 * @param categoryStatsState The UI state containing the list of category statistics.
 */
@Composable
fun CategoriesTab(categoryStatsState: UiState<List<CategoryStats>>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        when (categoryStatsState) {
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
                val totalAmount = categoryStatsState.data.sumOf { it.amount }

                items(categoryStatsState.data) { categoryStats ->
                    CategoryStatsItem(
                        categoryStats = categoryStats,
                        totalAmount = totalAmount
                    )
                }
            }

            is UiState.Error -> {
                item {
                    ErrorMessage(
                        message = categoryStatsState.message,
                        onRetry = { /* Reload data */ }
                    )
                }
            }

            is UiState.Empty -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.empty_category_statistics),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
