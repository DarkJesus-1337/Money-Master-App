package com.pixelpioneer.moneymaster.ui.components.features.dashboard

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.ui.components.common.items.RecentTransactionItem

/**
 * A list component for displaying recent transactions on the dashboard.
 *
 * Renders each transaction using RecentTransactionItem and separates them with dividers.
 *
 * @param transactions The list of recent transactions to display.
 * @param onTransactionClick Callback when a transaction is clicked.
 */
@Composable
fun RecentTransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(transactions) { transaction ->
            RecentTransactionItem(
                transaction = transaction,
                onClick = { onTransactionClick(transaction) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
