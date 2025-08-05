package com.pixelpioneer.moneymaster.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelpioneer.moneymaster.core.network.AppUpdateManager
import com.pixelpioneer.moneymaster.data.enums.SettingsSubScreen
import com.pixelpioneer.moneymaster.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    var currentScreen by remember { mutableStateOf(SettingsSubScreen.MAIN) }
    val state by settingsViewModel.state.collectAsState()
    val appUpdateManager = AppUpdateManager()

    when (currentScreen) {
        SettingsSubScreen.MAIN -> SettingsMainScreen(
            state = state,
            onPersonalClick = { currentScreen = SettingsSubScreen.PERSONAL },
            onDarkModeChange = { settingsViewModel.updateDarkMode(it) },
            appUpdateManager = appUpdateManager
        )

        SettingsSubScreen.PERSONAL -> PersonalSettingsScreen(
            settingsViewModel = settingsViewModel,
            onBack = { currentScreen = SettingsSubScreen.MAIN }
        )
    }
}
