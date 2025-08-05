package com.pixelpioneer.moneymaster.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents a scanned receipt with store information and purchased items.
 *
 * This Parcelable class contains all the information extracted from a
 * scanned receipt, including store name, date, and a list of items.
 *
 * @property storeName The name of the store or merchant
 * @property date The date when the purchase was made
 * @property items List of items purchased in this receipt
 * @property totalAmount Computed total amount of all items in the receipt
 */
@Parcelize
data class Receipt(
    val storeName: String?,
    val date: String?,
    val items: List<ReceiptItem>
) : Parcelable {
    val totalAmount: Double
        get() = items.sumOf { it.price }
}

/**
 * Represents a single item in a receipt.
 *
 * @property name The name or description of the purchased item
 * @property price The price of the item
 */
@Parcelize
data class ReceiptItem(
    val name: String,
    val price: Double
) : Parcelable