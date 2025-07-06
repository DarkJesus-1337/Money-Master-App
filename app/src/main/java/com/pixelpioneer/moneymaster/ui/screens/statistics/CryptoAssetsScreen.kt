package com.pixelpioneer.moneymaster.ui.screens.statistics

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.ui.viewmodel.StatisticsViewModel
import com.pixelpioneer.moneymaster.util.UiState

@Composable
fun CryptoAssetsScreen(viewModel: StatisticsViewModel) {
    // Assets beim ersten Anzeigen laden
    LaunchedEffect(Unit) {
        viewModel.loadCryptoAssets()
    }

    val cryptoAssetsState by viewModel.cryptoAssetsState.collectAsState()

    when (cryptoAssetsState) {
        is UiState.Loading -> Text("Lade Krypto-Daten...")
        is UiState.Success -> {
            val assets = (cryptoAssetsState as UiState.Success<List<Asset>>).data
            LazyColumn {
                items(assets) { asset ->
                    Text("${asset.name} (${asset.symbol}): ${asset.priceUsd} USD")
                }
            }
        }
        is UiState.Error -> Text("Fehler: ${(cryptoAssetsState as UiState.Error).message}")
        else -> Text("Keine Daten verfügbar.")
    }
}

