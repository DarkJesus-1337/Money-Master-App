package com.pixelpioneer.moneymaster.ui.components.coincap

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.pixelpioneer.moneymaster.data.model.HistoryDataPoint
import com.pixelpioneer.moneymaster.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

