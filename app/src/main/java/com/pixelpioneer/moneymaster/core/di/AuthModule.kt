package com.pixelpioneer.moneymaster.core.di

import com.pixelpioneer.moneymaster.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for authentication dependencies.
 * Provides authentication-related dependencies for dependency injection.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    
    /**
     * Provides a singleton instance of AuthRepository.
     *
     * @return An instance of [AuthRepository].
     */
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }
}