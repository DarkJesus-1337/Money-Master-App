package com.pixelpioneer.moneymaster.core.di

import android.content.Context
import com.pixelpioneer.moneymaster.core.network.CoinCapApiClient
import com.pixelpioneer.moneymaster.core.network.RemoteConfigManager
import com.pixelpioneer.moneymaster.data.remote.api.CoinCapApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for networking components.
 * Provides dependencies related to remote API services and configuration.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Provides a singleton instance of RemoteConfigManager.
     *
     * @param context The application context needed for Firebase configuration.
     * @return An instance of [RemoteConfigManager].
     */
    @Provides
    @Singleton
    fun provideRemoteConfigManager(
        @ApplicationContext context: Context
    ): RemoteConfigManager {
        return RemoteConfigManager()
    }

    /**
     * Provides a singleton instance of CoinCapApiClient.
     *
     * @param remoteConfigManager Used to retrieve API keys and configuration.
     * @return An instance of [CoinCapApiClient].
     */
    @Provides
    @Singleton
    fun provideCoinCapApiClient(
        remoteConfigManager: RemoteConfigManager
    ): CoinCapApiClient {
        return CoinCapApiClient(remoteConfigManager)
    }

    /**
     * Provides a singleton instance of CoinCapApiService.
     *
     * @param coinCapApiClient The client that configures and creates the API service.
     * @return An instance of [CoinCapApiService].
     */
    @Provides
    @Singleton
    fun provideCoinCapApiService(
        coinCapApiClient: CoinCapApiClient
    ): CoinCapApiService {
        return coinCapApiClient.api
    }
}