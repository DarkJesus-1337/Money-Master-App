package com.pixelpioneer.moneymaster.ui.components.common.buttons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.pixelpioneer.moneymaster.R

/**
 * A button that navigates the user to the full transactions list.
 *
 * This component is typically displayed at the bottom of a limited
 * transactions list to provide access to the complete transaction history.
 *
 * @param onClick Callback invoked when the button is clicked
 */
@Composable
fun ViewAllTransactionsButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = stringResource(R.string.transactions_view_all))
    }
}
