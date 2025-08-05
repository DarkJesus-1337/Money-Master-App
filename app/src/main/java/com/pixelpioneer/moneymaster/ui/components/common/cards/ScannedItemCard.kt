package com.pixelpioneer.moneymaster.ui.components.common.cards

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.ui.components.features.receipt.EditableItemContent
import com.pixelpioneer.moneymaster.ui.components.features.receipt.ReadOnlyItemContent

/**
 * A card component for displaying and editing scanned receipt items.
 *
 * This component manages the state for viewing and editing a transaction item
 * that was created from a scanned receipt. It allows users to edit the title
 * and amount of the transaction and supports removal.
 *
 * @param item The transaction representing a scanned receipt item
 * @param index The position index of this item in the list (for display purposes)
 * @param onItemUpdated Callback invoked when the item is updated with new values
 * @param onItemRemoved Callback invoked when the item is removed
 */
@Composable
fun ScannedItemCard(
    item: Transaction,
    index: Int,
    onItemUpdated: (Transaction) -> Unit,
    onItemRemoved: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(item.title) }
    var editedAmount by remember { mutableStateOf(item.amount.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        if (isEditing) {
            EditableItemContent(
                index = index,
                title = editedTitle,
                amount = editedAmount,
                onTitleChange = { editedTitle = it },
                onAmountChange = { editedAmount = it },
                onSave = {
                    val newAmount = editedAmount.toDoubleOrNull()
                    if (newAmount != null && editedTitle.isNotBlank()) {
                        val updatedItem = item.copy(
                            title = editedTitle.trim(),
                            amount = newAmount
                        )
                        onItemUpdated(updatedItem)
                        isEditing = false
                    }
                },
                onCancel = {
                    editedTitle = item.title
                    editedAmount = item.amount.toString()
                    isEditing = false
                },
                onRemove = {
                    onItemRemoved()
                    isEditing = false
                }
            )
        } else {
            ReadOnlyItemContent(
                item = item,
                index = index,
                onEdit = { isEditing = true }
            )
        }
    }
}
