package com.pixelpioneer.moneymaster.ui.components.common.empty

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.R

/**
 * A component for displaying an empty state when no transactions exist.
 *
 * This component shows a message indicating that no transactions have been
 * created yet, along with a brief explanation and a button to add a new
 * transaction. It is typically used when the transactions list is empty.
 *
 * @param onAddTransaction Callback invoked when the "Add Transaction" button is clicked
 */
@Composable
fun EmptyTransactionsList(
    onAddTransaction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.empty_transactions_title),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.empty_transactions_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAddTransaction
        ) {
            Text(text = stringResource(R.string.transactions_add))
        }
    }
}