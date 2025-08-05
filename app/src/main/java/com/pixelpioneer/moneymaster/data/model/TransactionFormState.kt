package com.pixelpioneer.moneymaster.data.model

import java.util.Calendar

/**
 * State holder for the transaction form.
 *
 * @property amount The transaction amount entered by the user.
 * @property title The title of the transaction.
 * @property description The description of the transaction.
 * @property selectedCategory The selected category for the transaction.
 * @property date The date of the transaction.
 * @property isExpense Whether the transaction is an expense.
 * @property amountError Error message for the amount field, if any.
 * @property titleError Error message for the title field, if any.
 * @property categoryError Error message for the category field, if any.
 */
data class TransactionFormState(
    val amount: Double = 0.0,
    val title: String = "",
    val description: String = "",
    val selectedCategory: TransactionCategory? = null,
    val date: Long = Calendar.getInstance().timeInMillis,
    val isExpense: Boolean = true,
    val amountError: String? = null,
    val titleError: String? = null,
    val categoryError: String? = null
)