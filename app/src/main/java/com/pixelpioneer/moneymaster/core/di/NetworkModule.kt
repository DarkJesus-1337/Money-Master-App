package com.pixelpioneer.moneymaster.core.di

import android.content.Context
import com.pixelpioneer.moneymaster.core.network.CoinCapApiClient
import com.pixelpioneer.moneymaster.data.remote.api.CoinCapApiService
import com.pixelpioneer.moneymaster.core.network.RemoteConfigManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRemoteConfigManager(
        @ApplicationContext context: Context
    ): RemoteConfigManager {
        return RemoteConfigManager(context)
    }

    @Provides
    @Singleton
    fun provideCoinCapApiClient(
        remoteConfigManager: RemoteConfigManager
    ): CoinCapApiClient {
        return CoinCapApiClient(remoteConfigManager)
    }

    @Provides
    @Singleton
    fun provideCoinCapApiService(
        coinCapApiClient: CoinCapApiClient
    ): CoinCapApiService {
        return coinCapApiClient.api
    }
}