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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.services.AppUpdateManager
import com.pixelpioneer.moneymaster.ui.components.UpdateDialog
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
    state: com.pixelpioneer.moneymaster.data.model.SettingsState,
    onPersonalClick: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    appUpdateManager: AppUpdateManager
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val updateState by appUpdateManager.updateState.collectAsState()
    var showUpdateDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.back)
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
            Button(
                onClick = onPersonalClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.personal_settings))
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        stringResource(R.string.app_settings),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.dark_mode), modifier = Modifier.weight(1f))
                        Switch(
                            checked = state.darkMode,
                            onCheckedChange = onDarkModeChange
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (activity != null) {
                                appUpdateManager.checkForUpdates(activity)
                                showUpdateDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.check_for_updates))
                    }
                }
            }
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
                title = { Text(stringResource(R.string.personal_settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.back)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        stringResource(R.string.personal_data),
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { settingsViewModel.updateName(it) },
                        label = { Text(stringResource(R.string.name)) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.income,
                        onValueChange = { settingsViewModel.updateIncome(it) },
                        label = { Text(stringResource(R.string.income_euro)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        stringResource(R.string.fixed_costs),
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = state.rent,
                        onValueChange = { settingsViewModel.updateRent(it) },
                        label = { Text(stringResource(R.string.rent_euro)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.electricity,
                        onValueChange = { settingsViewModel.updateElectricity(it) },
                        label = { Text(stringResource(R.string.electricity_euro)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.FlashOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.gas,
                        onValueChange = { settingsViewModel.updateGas(it) },
                        label = { Text(stringResource(R.string.gas_euro)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                Icons.Default.LocalGasStation,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state.internet,
                        onValueChange = { settingsViewModel.updateInternet(it) },
                        label = { Text(stringResource(R.string.internet_euro)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Wifi, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        stringResource(R.string.additional_fixed_costs),
                        style = MaterialTheme.typography.titleMedium
                    )
                    for ((idx, cost) in state.additionalCosts.withIndex()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = cost.label,
                                onValueChange = {
                                    settingsViewModel.updateAdditionalCost(
                                        idx,
                                        label = it
                                    )
                                },
                                label = { Text(stringResource(R.string.label)) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = cost.value,
                                onValueChange = {
                                    settingsViewModel.updateAdditionalCost(
                                        idx,
                                        value = it
                                    )
                                },
                                label = { Text(stringResource(R.string.amount_euro)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = { settingsViewModel.removeAdditionalCost(idx) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.remove)
                                    )
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                    OutlinedButton(
                        onClick = { settingsViewModel.addAdditionalCost() },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.add_fixed_cost))
                    }
                }
            }
        }
    }
}
