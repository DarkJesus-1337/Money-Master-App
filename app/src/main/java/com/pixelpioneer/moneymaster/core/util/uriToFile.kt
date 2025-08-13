package com.pixelpioneer.moneymaster.core.util

import android.net.Uri
import java.io.File

/**
 * Converts a content URI to a temporary file.
 *
 * This utility function creates a temporary file in the cache directory
 * and copies the content from the given URI to this file.
 *
 * @param uri The content URI to convert
 * @param context The context needed to access the ContentResolver
 * @return A temporary File containing the content from the URI
 */
fun uriToFile(uri: Uri, context: android.content.Context): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("receipt", ".jpg", context.cacheDir)
    inputStream?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}