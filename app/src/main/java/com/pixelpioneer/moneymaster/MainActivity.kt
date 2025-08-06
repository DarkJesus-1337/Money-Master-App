package com.pixelpioneer.moneymaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.pixelpioneer.moneymaster.core.network.RemoteConfigManager
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterNavHost
import com.pixelpioneer.moneymaster.ui.theme.MoneyMasterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * The main entry point of the MoneyMaster application.
 *
 * Sets up the Compose UI, applies the app theme, and initializes the navigation host.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val success = remoteConfigManager.fetchAndActivate()
            Timber.tag("MainActivity").d("Remote Config loaded: $success")

            val debugInfo = remoteConfigManager.getDebugInfo()
            Timber.tag("MainActivity").d("Remote Config Debug: $debugInfo")
        }

        setContent {
        MoneyMasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    MoneyMasterNavHost(navController = navController)
                }
            }
        }
    }
}