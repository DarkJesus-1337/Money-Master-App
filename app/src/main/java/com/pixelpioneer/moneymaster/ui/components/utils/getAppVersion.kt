package com.pixelpioneer.moneymaster.ui.components.utils

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

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