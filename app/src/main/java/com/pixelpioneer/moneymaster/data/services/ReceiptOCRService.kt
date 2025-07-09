// app/src/main/java/com/pixelpioneer/moneymaster/data/services/ReceiptOCRService.kt
package com.pixelpioneer.moneymaster.data.services

import android.graphics.Bitmap
import android.util.Log
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.util.TextRecognitionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReceiptOCRService {
    private val textRecognitionHelper = TextRecognitionHelper()

    fun recognizeReceipt(
        bitmap: Bitmap,
        onSuccess: (Receipt) -> Unit,
        onError: (Exception) -> Unit
    ) {
        Log.d("OCR_SERVICE", "Starting receipt recognition...")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val receipt = textRecognitionHelper.processReceiptImage(bitmap)
                Log.d("OCR_SERVICE", "Receipt processed successfully:")
                Log.d("OCR_SERVICE", "Store: ${receipt.storeName}")
                Log.d("OCR_SERVICE", "Items count: ${receipt.items.size}")
                receipt.items.forEach { item ->
                    Log.d("OCR_SERVICE", "Service item: '${item.name}' -> ${item.price}€")
                }
                onSuccess(receipt)
            } catch (e: Exception) {
                Log.e("OCR_SERVICE", "Error processing receipt", e)
                onError(e)
            }
        }
    }
}