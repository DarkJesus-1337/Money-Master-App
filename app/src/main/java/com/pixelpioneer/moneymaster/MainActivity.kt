package com.pixelpioneer.moneymaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterNavHost
import com.pixelpioneer.moneymaster.ui.theme.MoneyMasterTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main entry point of the MoneyMaster application.
 *
 * Sets up the Compose UI, applies the app theme, and initializes the navigation host.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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