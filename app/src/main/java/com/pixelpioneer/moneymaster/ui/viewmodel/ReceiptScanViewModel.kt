package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.repository.ReceiptScanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ViewModel for scanning receipts and extracting transaction items.
 *
 * Handles receipt image scanning, parsing OCR results into transactions,
 * and manages loading and error states for the scanning process.
 *
 * @property receiptScanRepository Repository for receipt scanning operations.
 */
class ReceiptScanViewModel(
    private val receiptScanRepository: ReceiptScanRepository,
) : ViewModel() {

    private val _scannedItems = MutableStateFlow<List<Transaction>>(emptyList())
    val scannedItems: StateFlow<List<Transaction>> = _scannedItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun scanReceipt(
        imageFile: File,
        defaultCategory: TransactionCategory,
        context: Context
    ) {
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val response = receiptScanRepository.scanReceipt(imageFile)
                Log.d("ReceiptScanViewModel", "OCR Response: $response")

                val parsedText = response.parsedResults?.firstOrNull()?.parsedText.orEmpty()

                Log.d("ReceiptScanViewModel", "OCR ParsedText: $parsedText")
                if (parsedText.isBlank()) {
                    _error.value = buildString {
                        append(context.getString(R.string.error_no_text_recognized))
                        append("\nOCRExitCode: ${response.ocrExitCode}\n")
                        response.errorMessage?.let {
                            if (it.toString().isNotBlank()) append("ErrorMessage: $it\n")
                        }
                        response.errorDetails?.let {
                            if (it.toString().isNotBlank()) append("ErrorDetails: $it\n")
                        }
                    }
                    _scannedItems.value = emptyList()
                    return@launch
                }

                val items = parseItemsFromText(parsedText, defaultCategory)
                if (items.isEmpty()) {
                    _error.value = context.getString(R.string.error_no_item_recognized, parsedText)
                }
                _scannedItems.value = items
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("OCR_ERROR", "Error scanning receipt", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parseItemsFromText(
        text: String,
        defaultCategory: TransactionCategory
    ): List<Transaction> {
        val regex = Regex("""(.+?)\s+(\d{1,3}[\.,]\d{2})""")
        val now = System.currentTimeMillis()

        val excludeTerms = setOf(
            "zu zahlen",
            "summe",
            "total",
            "gesamt",
            "kreditkarte",
            "bargeld",
            "cash",
            "kartenzahlung",
            "rückgeld",
            "change",
            "mwst",
            "steuer",
            "tax",
            "rabatt",
            "discount",
            "gutschein",
            "coupon",
            "pfand",
            "deposit",
            "trinkgeld",
            "tip",
            "service",
            "beleg",
            "receipt",
            "quittung",
            "datum",
            "date",
            "zeit",
            "time",
            "uhrzeit",
            "kassierer",
            "cashier",
            "kasse",
            "register",
            "filiale",
            "branch",
            "store",
            "laden",
            "geschäft",
            "adresse",
            "address",
            "telefon",
            "phone",
            "tel",
            "fax",
            "email",
            "website",
            "www",
            "http",
            "danke",
            "thank",
            "vielen dank",
            "thank you",
            "auf wiedersehen",
            "goodbye",
            "tschüss",
            "bis bald",
            "preisvorteile",
            "savings",
            "ersparnis",
            "mehrwertsteuer",
            "ust-id",
            "tax-id",
            "steuernummer",
            "hnr",
            "str",
            "plz",
            "ort",
            "bar"
        )

        return text.lines().mapNotNull { line ->
            val match = regex.find(line)
            match?.let {
                val (title, amountStr) = it.destructured
                val cleanTitle = title.trim().lowercase()

                // Skip if the title contains any excluded terms
                if (excludeTerms.any { excludeTerm -> cleanTitle.contains(excludeTerm) }) {
                    return@let null
                }

                // Skip if the title is too short (likely not a product name)
                if (cleanTitle.length < 3) {
                    return@let null
                }

                // Skip if the title contains mostly numbers (likely a product code or similar)
                if (cleanTitle.replace(
                        Regex("[^a-zA-ZäöüßÄÖÜ]"),
                        ""
                    ).length < cleanTitle.length * 0.3
                ) {
                    return@let null
                }

                val amount = amountStr.replace(",", ".").toDoubleOrNull() ?: return@let null

                // Skip very small amounts (might be taxes or fees)
                if (amount < 0.10) {
                    return@let null
                }

                Transaction(
                    amount = amount,
                    title = title.trim(), // Use original title with proper casing
                    category = defaultCategory,
                    date = now,
                    isExpense = true
                )
            }
        }
    }
}