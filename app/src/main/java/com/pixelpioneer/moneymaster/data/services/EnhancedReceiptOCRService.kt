package com.pixelpioneer.moneymaster.data.services

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.model.ReceiptItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

class EnhancedReceiptOCRService {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val pricePattern = Pattern.compile("""(\d{1,3}(?:[.,]\d{2,3})*[.,]\d{2})\s*[€A]""")
    private val itemWithPricePattern =
        Pattern.compile("""^(.+?)\s+(\d{1,3}(?:[.,]\d{2,3})*[.,]\d{2})\s*[€A]?$""")
    private val quantityPattern =
        Pattern.compile("""(\d+(?:[.,]\d+)?)\s*x\s*(\d+(?:[.,]\d+)?)\s*=?\s*(\d+(?:[.,]\d+)?)\s*[€A]?""")
    private val discountPattern =
        Pattern.compile("""(?:Preisvorteil|Rabatt|Discount)\s*[-]?(\d+(?:[.,]\d+)?)\s*[€A]?""")
    private val totalPattern =
        Pattern.compile("""(?:zu zahlen|Gesamt|Total|Summe).*?(\d{1,3}(?:[.,]\d{2,3})*[.,]\d{2})""")

    private val storeNames = listOf("LIDL", "ALDI", "REWE", "EDEKA", "PENNY", "NETTO", "KAUFLAND")

    fun recognizeReceipt(
        bitmap: Bitmap,
        onSuccess: (Receipt) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)

        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                try {
                    val receipt = parseReceiptText(visionText.text)
                    onSuccess(receipt)
                } catch (e: Exception) {
                    onError(e)
                }
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    private fun parseReceiptText(text: String): Receipt {
        val lines = text.split('\n').map { it.trim() }.filter { it.isNotEmpty() }

        val storeName = extractStoreName(lines)
        val date = extractDate(lines)
        val items = extractItems(lines)
        val cleanedItems = postProcessItems(items)

        val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            .format(java.util.Date())

        return Receipt(
            storeName = storeName,
            items = items,
            date = currentDate
        )
    }

    private fun extractStoreName(lines: List<String>): String? {
        return lines.take(5).firstOrNull { line ->
            storeNames.any { store ->
                line.uppercase().contains(store)
            }
        }?.let { line ->
            storeNames.first { store ->
                line.uppercase().contains(store)
            }
        }
    }

    private fun extractDate(lines: List<String>): String? {
        val datePatterns = listOf(
            Pattern.compile("""(\d{1,2})[./](\d{1,2})[./](\d{2,4})"""),
            Pattern.compile("""(\d{1,2})[.](\d{1,2})[.](\d{2,4})""")
        )

        for (line in lines) {
            for (pattern in datePatterns) {
                val matcher = pattern.matcher(line)
                if (matcher.find()) {
                    return matcher.group()
                }
            }
        }

        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }

    private fun extractItems(lines: List<String>): List<ReceiptItem> {
        val items = mutableListOf<ReceiptItem>()
        var i = 0

        while (i < lines.size) {
            val line = lines[i]

            if (isHeaderLine(line)) {
                i++
                continue
            }

            if (isFooterLine(line)) {
                break
            }

            val item = parseItemLine(line) ?: parseMultiLineItem(lines, i)?.also { i++ }
            ?: parseQuantityLine(line)

            item?.let {
                if (it.price > 0 && it.name.isNotBlank()) {
                    items.add(it)
                }
            }

            i++
        }

        return items
    }

    private fun parseItemLine(line: String): ReceiptItem? {
        val matcher = itemWithPricePattern.matcher(line)
        if (matcher.find()) {
            val name = matcher.group(1)?.trim() ?: return null
            val priceStr = matcher.group(2) ?: return null
            val price = parsePrice(priceStr)

            if (price > 0 && name.isNotBlank()) {
                return ReceiptItem(cleanItemName(name), price)
            }
        }

        return null
    }

    private fun parseMultiLineItem(lines: List<String>, index: Int): ReceiptItem? {
        if (index + 1 >= lines.size) return null

        val currentLine = lines[index]
        val nextLine = lines[index + 1]

        val priceMatch = pricePattern.matcher(nextLine)
        if (priceMatch.find() && nextLine.trim().matches(Regex("""^\d+[.,]\d{2}\s*[€A]?$"""))) {
            val priceStr = priceMatch.group(1) ?: return null
            val price = parsePrice(priceStr)
            if (price > 0) {
                return ReceiptItem(cleanItemName(currentLine), price)
            }
        }

        return null
    }

    private fun parseQuantityLine(line: String): ReceiptItem? {
        val matcher = quantityPattern.matcher(line)
        if (matcher.find()) {
            val totalPriceStr = matcher.group(3) ?: return null
            val price = parsePrice(totalPriceStr)

            if (price > 0) {
                val cleanLine = line.replace(matcher.group(), "").trim()
                if (cleanLine.isNotBlank()) {
                    return ReceiptItem(cleanItemName(cleanLine), price)
                }
            }
        }

        return null
    }

    private fun parsePrice(priceStr: String): Double {
        return try {
            val normalized = priceStr.replace(',', '.').replace(Regex("""[^\d.]"""), "")
            normalized.toDoubleOrNull() ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    private fun cleanItemName(name: String): String {
        return name
            .replace(Regex("""[€A]\s*$"""), "")
            .replace(Regex("""\d+[.,]\d{2}\s*[€A]?\s*$"""), "")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun isHeaderLine(line: String): Boolean {
        val headerKeywords = listOf(
            "LIDL", "ALDI", "REWE", "EDEKA", "PENNY", "NETTO", "KAUFLAND",
            "Str.", "Straße", "Plz", "Tel", "Ust", "MwSt", "EUR"
        )

        return headerKeywords.any { keyword ->
            line.uppercase().contains(keyword.uppercase())
        } || line.matches(Regex("""\d{5}\s+\w+"""))
    }

    private fun isFooterLine(line: String): Boolean {
        val footerKeywords = listOf(
            "zu zahlen", "Gesamt", "Total", "Summe", "Kreditkarte", "Bargeld",
            "MwSt", "Steuer", "Bon", "Vielen Dank", "Wiedersehen"
        )

        return footerKeywords.any { keyword ->
            line.uppercase().contains(keyword.uppercase())
        }
    }

    private fun postProcessItems(items: List<ReceiptItem>): List<ReceiptItem> {
        return items
            .distinctBy { "${it.name}_${it.price}" }
            .filter { item ->
                item.price in 0.01..999.99 &&
                        item.name.length >= 3 &&
                        !item.name.uppercase().contains("PREISVORTEIL") &&
                        !item.name.uppercase().contains("RABATT")
            }
            .sortedBy { it.name }
    }
}