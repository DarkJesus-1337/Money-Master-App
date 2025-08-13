package com.pixelpioneer.moneymaster.ui.components.common.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.ui.components.common.items.CategoryListItem

@Composable
fun CategoryList(
    categories: List<TransactionCategory>,
    modifier: Modifier = Modifier,
    onEditCategory: (TransactionCategory) -> Unit,
    onDeleteCategory: (TransactionCategory) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.category_predefined),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(categories.filter { it.id <= 10 }) { category ->
            CategoryListItem(
                category = category,
                isPredefined = true,
                onEdit = { onEditCategory(category) },
                onDelete = {  }
            )
        }
        
        val customCategories = categories.filter { it.id > 10 }
        if (customCategories.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.category_custom),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            items(customCategories) { category ->
                CategoryListItem(
                    category = category,
                    isPredefined = false,
                    onEdit = { onEditCategory(category) },
                    onDelete = { onDeleteCategory(category) }
                )
            }
        }
    }
}
