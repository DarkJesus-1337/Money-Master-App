package com.pixelpioneer.moneymaster.data.services

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class AppUpdateManager {
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState

    private val updateJsonUrl = "https://darkjesus-1337.github.io/Money-Master-App/assets/update.json"

    fun checkForUpdates(activity: Activity) {
        _updateState.value = UpdateState.Checking

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(updateJsonUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val json = connection.inputStream.bufferedReader().readText()
                val obj = JSONObject(json)
                val latestVersion = obj.getString("version")
                val changelog = obj.getString("changelog")
                val apkUrl = obj.getString("apkUrl")

                val currentVersion = activity.packageManager
                    .getPackageInfo(activity.packageName, 0).versionName

                if (latestVersion != currentVersion) {
                    _updateState.value = UpdateState.Downloading(0, 1)
                    val apkFile = downloadApk(apkUrl, activity)
                    _updateState.value = UpdateState.Success
                    installApk(apkFile, activity)
                } else {
                    _updateState.value = UpdateState.NoUpdate
                }
            } catch (e: Exception) {
                Log.e("AppUpdate", "Update fehlgeschlagen: ${e.message}")
                _updateState.value = UpdateState.Error(e.message ?: "Unbekannter Fehler")
            }
        }
    }

    private fun downloadApk(apkUrl: String, activity: Activity): File {
        val url = URL(apkUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connect()
        val file = File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")
        FileOutputStream(file).use { output ->
            connection.inputStream.copyTo(output)
        }
        return file
    }

    private fun installApk(file: File, activity: Activity) {
        val uri = FileProvider.getUriForFile(
            activity,
            "${activity.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        activity.startActivity(intent)
    }

    sealed class UpdateState {
        object Idle : UpdateState()
        object Checking : UpdateState()
        data class Downloading(val downloaded: Long, val total: Long) : UpdateState()
        object Success : UpdateState()
        object NoUpdate : UpdateState()
        data class Error(val message: String) : UpdateState()
    }
}