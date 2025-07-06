package com.pixelpioneer.moneymaster.ui.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.data.model.HistoryDataPoint
import com.pixelpioneer.moneymaster.ui.viewmodel.CryptoViewModel
import com.pixelpioneer.moneymaster.util.FormatUtils
import com.pixelpioneer.moneymaster.util.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
        // Asset-Auswahlleiste
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

        // Asset-Details und Grafik
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
                    // Asset-Info
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

                    // Preis-Chart
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

            // Zusätzliche Asset-Informationen
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

@Composable
fun AssetChip(
    asset: Asset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = asset.symbol,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PriceChart(
    historyData: List<HistoryDataPoint>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                legend.isEnabled = false

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                        override fun getFormattedValue(value: Float): String {
                            return dateFormat.format(Date(value.toLong()))
                        }
                    }
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return FormatUtils.formatCurrency(value.toDouble())
                        }
                    }
                }

                axisRight.isEnabled = false
            }
        },
        modifier = modifier,
        update = { chart ->
            if (historyData.isNotEmpty()) {
                val entries = historyData.mapIndexed { index, dataPoint ->
                    Entry(
                        dataPoint.time.toFloat(),
                        dataPoint.priceUsd.toFloatOrNull() ?: 0f
                    )
                }

                val dataSet = LineDataSet(entries, "Preis").apply {
                    color = Color.Blue.toArgb()
                    setCircleColor(Color.Blue.toArgb())
                    lineWidth = 2f
                    circleRadius = 3f
                    setDrawCircleHole(false)
                    valueTextSize = 0f
                    setDrawValues(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(true)
                    fillColor = Color.Blue.copy(alpha = 0.3f).toArgb()
                }

                chart.data = LineData(dataSet)
                chart.invalidate()
            }
        }
    )
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}