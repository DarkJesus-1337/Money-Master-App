package com.pixelpioneer.moneymaster

import android.app.Application
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Application class for MoneyMaster.
 *
 * Initializes the database and ensures default categories are present on startup.
 */
@HiltAndroidApp
class MoneyMasterApplication : Application() {

    @Inject
    lateinit var categoryRepository: CategoryRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        initializeDatabase()
    }

    private fun initializeDatabase() {
        applicationScope.launch {
            try {
                categoryRepository.initializeDefaultCategoriesAndRepairDatabase()
                Timber.d("Database initialized successfully")
            } catch (e: Exception) {
                Timber.e(e, "Error initializing the database")
            }
        }
    }
}