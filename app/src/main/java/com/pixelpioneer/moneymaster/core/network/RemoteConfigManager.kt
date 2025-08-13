package com.pixelpioneer.moneymaster.core.network

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.pixelpioneer.moneymaster.BuildConfig
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
 * Manager for handling Firebase Remote Config operations.
 *
 * Initializes remote config settings, fetches and activates config values,
 * and provides access to API keys and debug information.
 */
class RemoteConfigManager() {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    companion object {
        private const val TAG = "RemoteConfigManager"
        private const val CACHE_EXPIRATION = 3600L

        const val OCR_SPACE_API_KEY = "ocr_space_api_key"

        const val COINCAP_API_KEY = "coincap_api_key"
    }

    init {
        initializeRemoteConfig()
    }

    /**
     * Initializes Firebase Remote Config with settings and default values.
     */
    private fun initializeRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else CACHE_EXPIRATION
        }

        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(
            mapOf(
                OCR_SPACE_API_KEY to BuildConfig.OCR_SPACE_API_KEY,
                COINCAP_API_KEY to BuildConfig.COINCAP_API_KEY
            )
        )
    }

    /**
     * Fetches and activates remote config values from Firebase.
     *
     * @return True if fetch and activation were successful, false otherwise.
     */
    suspend fun fetchAndActivate(): Boolean {
        return try {
            val fetchResult = remoteConfig.fetchAndActivate().await()

            Timber.tag(TAG).d("Available Remote Config keys: ${remoteConfig.all.keys}")
            Timber.tag(TAG).d("CoinCap key value: '${remoteConfig.getString(COINCAP_API_KEY)}'")

            Timber.tag(TAG).d("Remote config fetch successful: $fetchResult")
            fetchResult
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error fetching remote config")
            false
        }
    }

    /**
     * Retrieves the OCR Space API key from remote config.
     *
     * @return The OCR Space API key as a [String].
     */
    fun getOcrSpaceApiKey(): String {
        return remoteConfig.getString(OCR_SPACE_API_KEY).takeIf { it.isNotEmpty() }
            ?: BuildConfig.OCR_SPACE_API_KEY
    }

    /**
     * Retrieves the CoinCap API key from remote config.
     *
     * @return The CoinCap API key as a [String].
     */
    fun getCoinCapApiKey(): String {
        val remoteKey = remoteConfig.getString(COINCAP_API_KEY)
        Timber.tag(TAG).d("Retrieved remote key: '$remoteKey' (length: ${remoteKey.length})")

        if (remoteKey.isNotEmpty() && remoteKey != "null") {
            return remoteKey
        }

        Timber.tag(TAG).w("No valid CoinCap API key found in Remote Config")
        return ""
    }

    /**
     * Retrieves all available keys from remote config.
     *
     * @return A set of all keys as [String].
     */
    fun getAllKeys(): Set<String> {
        return remoteConfig.all.keys
    }

    /**
     * Provides debug information about the remote config state.
     *
     * @return A map containing debug info such as last fetch time, status, config settings, and available keys.
     */
    fun getDebugInfo(): Map<String, Any> {
        return mapOf(
            "lastFetchTime" to remoteConfig.info.fetchTimeMillis,
            "lastFetchStatus" to remoteConfig.info.lastFetchStatus,
            "configSettings" to remoteConfig.info.configSettings.minimumFetchIntervalInSeconds,
            "availableKeys" to getAllKeys()
        )
    }
}