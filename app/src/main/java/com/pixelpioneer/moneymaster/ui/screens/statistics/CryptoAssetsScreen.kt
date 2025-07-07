package com.pixelpioneer.moneymaster.ui.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.ui.components.coincap.AssetChip
import com.pixelpioneer.moneymaster.ui.components.coincap.InfoRow
import com.pixelpioneer.moneymaster.ui.components.coincap.PriceChart
import com.pixelpioneer.moneymaster.ui.viewmodel.CryptoViewModel
import com.pixelpioneer.moneymaster.util.FormatUtils
import com.pixelpioneer.moneymaster.util.UiState

@Composable
fun CryptoAssetsScreen(viewModel: CryptoViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadCryptoAssets()
    }

    val cryptoAssetsState by viewModel.cryptoAssetsState.collectAsState()
    val cryptoHistoryState by viewModel.cryptoHistoryState.collectAsState()
    val selectedAsset by viewModel.selectedAsset.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        when (val assetsState = cryptoAssetsState) {
            is UiState.Success -> {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(assetsState.data) { asset ->
                        AssetChip(
                            asset = asset,
                            isSelected = selectedAsset?.id == asset.id,
                            onClick = { viewModel.selectAsset(asset) }
                        )
                    }
                }
            }

            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                Text(
                    text = "Fehler: ${assetsState.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            is UiState.Empty -> {
                Text("Keine Krypto-Daten verfügbar")
            }
        }

        selectedAsset?.let { asset ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = asset.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = asset.symbol,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = FormatUtils.formatCurrency(
                                    asset.priceUsd.toDoubleOrNull() ?: 0.0
                                ),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            val changePercent = asset.changePercent24Hr.toDoubleOrNull() ?: 0.0
                            Text(
                                text = "${if (changePercent >= 0) "+" else ""}${
                                    FormatUtils.formatPercentage(
                                        changePercent / 100,
                                        2
                                    )
                                }",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (changePercent >= 0) Color.Green else Color.Red,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (val historyState = cryptoHistoryState) {
                        is UiState.Success -> {
                            PriceChart(
                                historyData = historyState.data,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }

                        is UiState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        is UiState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Fehler beim Laden der Grafik: ${historyState.message}",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        is UiState.Empty -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Keine Daten verfügbar")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Marktdaten",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoRow(
                        "Marktkapitalisierung",
                        FormatUtils.formatCurrency(asset.marketCapUsd.toDoubleOrNull() ?: 0.0)
                    )
                    InfoRow(
                        "Volumen (24h)",
                        FormatUtils.formatCurrency(asset.volumeUsd24Hr.toDoubleOrNull() ?: 0.0)
                    )
                    InfoRow("Rang", "#${asset.rank}")
                    asset.maxSupply?.let { maxSupply ->
                        InfoRow(
                            "Max. Angebot",
                            FormatUtils.formatCurrency(maxSupply.toDoubleOrNull() ?: 0.0)
                        )
                    }
                }
            }
        }
    }
}

