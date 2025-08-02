package com.pixelpioneer.moneymaster.ui.screens.settings

import android.app.Activity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.SettingsState
import com.pixelpioneer.moneymaster.data.services.AppUpdateManager
import com.pixelpioneer.moneymaster.ui.components.UpdateDialog
import com.pixelpioneer.moneymaster.ui.components.getNewAppVersion
import com.pixelpioneer.moneymaster.ui.viewmodel.SettingsViewModel

enum class SettingsSubScreen {
    MAIN, PERSONAL
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(
    state: SettingsState,
    onPersonalClick: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    appUpdateManager: AppUpdateManager
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val updateState by appUpdateManager.updateState.collectAsState()
    var showUpdateDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Personal Settings Button
            Button(
                onClick = onPersonalClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.settings_personal))
            }

            // App Settings Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.settings_app),
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Dark Mode Toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.settings_dark_mode),
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = state.darkMode,
                            onCheckedChange = onDarkModeChange
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Update Check Button
                    Button(
                        onClick = {
                            if (activity != null) {
                                appUpdateManager.checkForUpdates(activity)
                                showUpdateDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.settings_check_updates))
                    }

                    // App Version Display
                    val appVersion = getNewAppVersion()
                    Text(
                        "Version: $appVersion",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Update Dialog
            if (showUpdateDialog && updateState != AppUpdateManager.UpdateState.Idle) {
                UpdateDialog(
                    updateState = updateState,
                    onDismiss = { showUpdateDialog = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalSettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val state by settingsViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_personal)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Personal Data Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.settings_personal_data),
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Name Field
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { settingsViewModel.updateName(it) },
                        label = { Text(stringResource(R.string.label_name)) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Income Field
                    OutlinedTextField(
                        value = state.income,
                        onValueChange = { settingsViewModel.updateIncome(it) },
                        label = { Text(stringResource(R.string.settings_income)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Default.AttachMoney, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Fixed Costs Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.settings_fixed_costs),
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Rent Field
                    OutlinedTextField(
                        value = state.rent,
                        onValueChange = { settingsViewModel.updateRent(it) },
                        label = { Text(stringResource(R.string.settings_rent)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Default.Home, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Electricity Field
                    OutlinedTextField(
                        value = state.electricity,
                        onValueChange = { settingsViewModel.updateElectricity(it) },
                        label = { Text(stringResource(R.string.settings_electricity)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Default.FlashOn, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Gas Field
                    OutlinedTextField(
                        value = state.gas,
                        onValueChange = { settingsViewModel.updateGas(it) },
                        label = { Text(stringResource(R.string.settings_gas)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Default.LocalGasStation, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Internet Field
                    OutlinedTextField(
                        value = state.internet,
                        onValueChange = { settingsViewModel.updateInternet(it) },
                        label = { Text(stringResource(R.string.settings_internet)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(Icons.Default.Wifi, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Additional Fixed Costs Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        stringResource(R.string.settings_additional_costs),
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Dynamic Additional Costs
                    for ((idx, cost) in state.additionalCosts.withIndex()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Cost Label Field
                            OutlinedTextField(
                                value = cost.label,
                                onValueChange = {
                                    settingsViewModel.updateAdditionalCost(idx, label = it)
                                },
                                label = { Text(stringResource(R.string.label_name)) },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Cost Amount Field
                            OutlinedTextField(
                                value = cost.value,
                                onValueChange = {
                                    settingsViewModel.updateAdditionalCost(idx, value = it)
                                },
                                label = { Text(stringResource(R.string.label_amount)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Remove Button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = { settingsViewModel.removeAdditionalCost(idx) }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.action_remove)
                                    )
                                }
                            }
                        }
                        HorizontalDivider()
                    }

                    // Add Fixed Cost Button
                    OutlinedButton(
                        onClick = { settingsViewModel.addAdditionalCost() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.settings_add_fixed_cost))
                    }
                }
            }
        }
    }
}