package com.pixelpioneer.moneymaster.ui.components.utils

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Retrieves the current version name of the application.
 *
 * @return The version name as defined in the app's build configuration, or "?" if not found.
 */
@Composable
fun getNewAppVersion(): String {
    val context = LocalContext.current
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName ?: "?"
    } catch (e: PackageManager.NameNotFoundException) {
        "?"
    }
}