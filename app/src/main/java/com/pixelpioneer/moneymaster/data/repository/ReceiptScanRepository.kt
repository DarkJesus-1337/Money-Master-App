package com.pixelpioneer.moneymaster.data.repository

import android.util.Log
import com.pixelpioneer.moneymaster.data.services.OcrSpaceApiClient
import com.pixelpioneer.moneymaster.data.services.OcrSpaceResponse
import com.pixelpioneer.moneymaster.data.services.RemoteConfigManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

class ReceiptScanRepository(
    private val remoteConfigManager: RemoteConfigManager
) {
    companion object {
        private const val TAG = "ReceiptScanRepository"
        private const val MAX_IMAGE_SIZE = 1024 * 1024 // 1MB
        private const val MAX_WIDTH = 1024
        private const val MAX_HEIGHT = 1024
        private const val JPEG_QUALITY = 85
    }

    private val apiService = OcrSpaceApiClient.apiService

    suspend fun scanReceipt(imageFile: File): OcrSpaceResponse {
        val compressedFile = compressImageIfNeeded(imageFile)

        val apiKey = remoteConfigManager.getOcrSpaceApiKey()

        Log.d(TAG, "Starting OCR scan with API key: ${apiKey.take(10)}...")
        Log.d(TAG, "Original file size: ${imageFile.length()} bytes")
        Log.d(TAG, "Compressed file size: ${compressedFile.length()} bytes")

        if (apiKey.isEmpty()) {
            throw IllegalStateException("OCR Space API Key not available")
        }

        val apiKeyBody = apiKey.toRequestBody("text/plain".toMediaType())
        val languageBody = "ger".toRequestBody("text/plain".toMediaType())
        val overlayBody = "false".toRequestBody("text/plain".toMediaType())
        val orientationBody = "true".toRequestBody("text/plain".toMediaType())
        val isTableBody = "true".toRequestBody("text/plain".toMediaType())
        val scaleBody = "true".toRequestBody("text/plain".toMediaType())

        val imagePart = MultipartBody.Part.createFormData(
            "file",
            compressedFile.name,
            compressedFile.asRequestBody("image/*".toMediaType())
        )

        Log.d(TAG, "Making OCR API request")

        return try {
            val response = apiService.parseImage(
                apiKey = apiKeyBody,
                language = languageBody,
                overlay = overlayBody,
                orientation = orientationBody,
                isTable = isTableBody,
                scale = scaleBody,
                image = imagePart
            )

            Log.d(TAG, "Response code: ${response.code()}")

            if (response.isSuccessful) {
                val ocrResponse = response.body()
                if (ocrResponse != null) {
                    if (ocrResponse.isErroredOnProcessing) {
                        Log.e(TAG, "OCR processing error: ${ocrResponse.errorMessage}")
                        throw IOException("OCR processing failed: ${ocrResponse.errorMessage}")
                    }

                    if (ocrResponse.ocrExitCode != 1) {
                        Log.e(TAG, "OCR exit code: ${ocrResponse.ocrExitCode}")
                        throw IOException("OCR failed with exit code: ${ocrResponse.ocrExitCode}")
                    }

                    Log.d(TAG, "OCR scan successful")
                    ocrResponse
                } else {
                    throw IOException("Empty response body")
                }
            } else {
                Log.e(TAG, "HTTP error: ${response.code()}")
                throw IOException("HTTP error: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error", e)
            throw IOException("Network error: ${e.message}", e)
        } finally {
            // Temporary compressed file löschen falls erstellt
            if (compressedFile != imageFile && compressedFile.exists()) {
                compressedFile.delete()
            }
        }
    }

    private fun compressImageIfNeeded(originalFile: File): File {
        // Prüfe ob Komprimierung nötig ist
        if (originalFile.length() <= MAX_IMAGE_SIZE) {
            Log.d(TAG, "Image size OK, no compression needed")
            return originalFile
        }

        Log.d(TAG, "Image too large, compressing...")

        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
            ?: throw IOException("Could not decode image file")

        // Korrigiere Bildrotation basierend auf EXIF-Daten
        val rotatedBitmap = correctImageOrientation(bitmap, originalFile.absolutePath)

        // Berechne neue Dimensionen
        val (newWidth, newHeight) = calculateNewDimensions(
            rotatedBitmap.width,
            rotatedBitmap.height
        )

        // Skaliere Bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, newWidth, newHeight, true)

        // Erstelle temporäre Datei
        val compressedFile = File.createTempFile("compressed_receipt", ".jpg", originalFile.parentFile)

        // Komprimiere und speichere
        FileOutputStream(compressedFile).use { fos ->
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, byteArrayOutputStream)
            fos.write(byteArrayOutputStream.toByteArray())
        }

        // Cleanup
        if (rotatedBitmap != bitmap) {
            rotatedBitmap.recycle()
        }
        bitmap.recycle()
        scaledBitmap.recycle()

        Log.d(TAG, "Compression complete. New size: ${compressedFile.length()} bytes")
        return compressedFile
    }

    private fun correctImageOrientation(bitmap: Bitmap, imagePath: String): Bitmap {
        return try {
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> return bitmap
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            Log.w(TAG, "Could not correct image orientation", e)
            bitmap
        }
    }

    private fun calculateNewDimensions(originalWidth: Int, originalHeight: Int): Pair<Int, Int> {
        if (originalWidth <= MAX_WIDTH && originalHeight <= MAX_HEIGHT) {
            return Pair(originalWidth, originalHeight)
        }

        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()

        return if (originalWidth > originalHeight) {
            val newWidth = MAX_WIDTH
            val newHeight = (newWidth / aspectRatio).toInt()
            Pair(newWidth, newHeight)
        } else {
            val newHeight = MAX_HEIGHT
            val newWidth = (newHeight * aspectRatio).toInt()
            Pair(newWidth, newHeight)
        }
    }

    // Rest der Klasse bleibt unverändert...
    fun isApiKeyAvailable(): Boolean {
        val available = remoteConfigManager.getOcrSpaceApiKey().isNotEmpty()
        Log.d(TAG, "API key available: $available")
        return available
    }

    fun getDebugInfo(): Map<String, String> {
        val apiKey = remoteConfigManager.getOcrSpaceApiKey()
        return mapOf(
            "apiKeyLength" to apiKey.length.toString(),
            "apiKeyPrefix" to apiKey.take(10),
            "hasRemoteConfig" to remoteConfigManager.hasKey(RemoteConfigManager.OCR_SPACE_API_KEY).toString()
        )
    }
}