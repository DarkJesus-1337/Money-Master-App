package com.pixelpioneer.moneymaster.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.network.OcrSpaceApiClient
import com.pixelpioneer.moneymaster.data.remote.dto.OcrSpaceResponse
import com.pixelpioneer.moneymaster.core.network.RemoteConfigManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Repository for scanning receipts and processing OCR results.
 *
 * Handles image compression, orientation correction, and communication with the OCR Space API.
 * Provides methods to scan receipts and retrieve debug information.
 *
 * @property remoteConfigManager Manager for remote configuration and API key retrieval.
 */
class ReceiptScanRepository(
    private val remoteConfigManager: RemoteConfigManager,
    private val context: Context
) {
    companion object {
        private const val TAG = "ReceiptScanRepository"

        /** Maximum allowed image file size in bytes (1MB). */
        private const val MAX_IMAGE_SIZE = 1024 * 1024

        /** Maximum image width in pixels for compression. */
        private const val MAX_WIDTH = 1024

        /** Maximum image height in pixels for compression. */
        private const val MAX_HEIGHT = 1024

        /** JPEG compression quality (0-100). */
        private const val JPEG_QUALITY = 85
    }

    private val apiService = OcrSpaceApiClient.apiService

    /**
     * Scans a receipt image using the OCR Space API.
     *
     * Automatically compresses the image if needed and corrects orientation
     * before sending it to the OCR service.
     *
     * @param imageFile The image file to scan.
     * @return An [OcrSpaceResponse] containing the parsed text and metadata.
     * @throws IllegalStateException If the API key is not available.
     * @throws IOException If there's a network error or OCR processing fails.
     */
    suspend fun scanReceipt(imageFile: File): OcrSpaceResponse {
        val compressedFile = compressImageIfNeeded(imageFile)

        val apiKey = remoteConfigManager.getOcrSpaceApiKey()

        Log.d(TAG, context.getString(R.string.log_ocr_scan_starting, apiKey.take(10)))
        Log.d(TAG, context.getString(R.string.log_original_file_size, imageFile.length()))
        Log.d(TAG, context.getString(R.string.log_compressed_file_size, compressedFile.length()))

        if (apiKey.isEmpty()) {
            throw IllegalStateException(context.getString(R.string.error_ocr_api_key_missing))
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

        Log.d(TAG, context.getString(R.string.log_making_ocr_api_request))

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

            Log.d(TAG, context.getString(R.string.log_response_code, response.code()))

            if (response.isSuccessful) {
                val ocrResponse = response.body()
                if (ocrResponse != null) {
                    if (ocrResponse.isErroredOnProcessing) {
                        Log.e(
                            TAG,
                            context.getString(
                                R.string.error_ocr_processing,
                                ocrResponse.errorMessage
                            )
                        )
                        throw IOException(
                            context.getString(
                                R.string.error_ocr_processing,
                                ocrResponse.errorMessage
                            )
                        )
                    }

                    if (ocrResponse.ocrExitCode != 1) {
                        Log.e(
                            TAG,
                            context.getString(R.string.error_ocr_exit_code, ocrResponse.ocrExitCode)
                        )
                        throw IOException(
                            context.getString(
                                R.string.error_ocr_exit_code,
                                ocrResponse.ocrExitCode
                            )
                        )
                    }

                    Log.d(TAG, context.getString(R.string.log_ocr_scan_successful))
                    ocrResponse
                } else {
                    throw IOException(context.getString(R.string.error_empty_response_body))
                }
            } else {
                Log.e(TAG, context.getString(R.string.error_http_error, response.code()))
                throw IOException(context.getString(R.string.error_http_error, response.code()))
            }
        } catch (e: Exception) {
            Log.e(TAG, context.getString(R.string.error_network), e)
            throw IOException(
                context.getString(
                    R.string.error_network,
                    e.message
                ), e
            )
        } finally {
            if (compressedFile != imageFile && compressedFile.exists()) {
                compressedFile.delete()
            }
        }
    }

    /**
     * Compresses an image file if it exceeds the maximum size limit.
     *
     * Includes orientation correction and dimension scaling to optimize
     * the image for OCR processing.
     *
     * @param originalFile The original image file.
     * @return A compressed image file, or the original file if compression wasn't needed.
     * @throws IOException If image processing fails.
     */
    private fun compressImageIfNeeded(originalFile: File): File {
        if (originalFile.length() <= MAX_IMAGE_SIZE) {
            Log.d(TAG, context.getString(R.string.log_image_size_ok))
            return originalFile
        }

        Log.d(TAG, context.getString(R.string.log_image_too_large))

        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
            ?: throw IOException("Could not decode image file")

        val rotatedBitmap = correctImageOrientation(bitmap, originalFile.absolutePath)

        val (newWidth, newHeight) = calculateNewDimensions(
            rotatedBitmap.width,
            rotatedBitmap.height
        )

        val scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, newWidth, newHeight, true)

        val compressedFile =
            File.createTempFile("compressed_receipt", ".jpg", originalFile.parentFile)

        FileOutputStream(compressedFile).use { fos ->
            val byteArrayOutputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, byteArrayOutputStream)
            fos.write(byteArrayOutputStream.toByteArray())
        }

        if (rotatedBitmap != bitmap) {
            rotatedBitmap.recycle()
        }
        bitmap.recycle()
        scaledBitmap.recycle()

        Log.d(TAG, context.getString(R.string.log_compression_complete, compressedFile.length()))
        return compressedFile
    }

    /**
     * Corrects the orientation of a bitmap based on EXIF data.
     *
     * @param bitmap The bitmap to correct.
     * @param imagePath The path to the original image file for EXIF data.
     * @return A bitmap with corrected orientation, or the original bitmap if no correction is needed.
     */
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
            Log.w(TAG, context.getString(R.string.log_could_not_correct_orientation), e)
            bitmap
        }
    }

    /**
     * Calculates new dimensions for image scaling while maintaining aspect ratio.
     *
     * @param originalWidth The original width of the image.
     * @param originalHeight The original height of the image.
     * @return A [Pair] containing the new width and height.
     */
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

    /**
     * Checks if the OCR Space API key is available.
     *
     * @return True if the API key is available and not empty, false otherwise.
     */
    fun isApiKeyAvailable(): Boolean {
        val available = remoteConfigManager.getOcrSpaceApiKey().isNotEmpty()
        Log.d(TAG, context.getString(R.string.log_api_key_available, available))
        return available
    }

    /**
     * Provides debug information about the OCR configuration.
     *
     * @return A map containing debug information such as API key length and availability.
     */
    fun getDebugInfo(): Map<String, String> {
        val apiKey = remoteConfigManager.getOcrSpaceApiKey()
        return mapOf(
            "apiKeyLength" to apiKey.length.toString(),
            "apiKeyPrefix" to apiKey.take(10),
            "hasRemoteConfig" to remoteConfigManager.hasKey(RemoteConfigManager.OCR_SPACE_API_KEY)
                .toString()
        )
    }
}