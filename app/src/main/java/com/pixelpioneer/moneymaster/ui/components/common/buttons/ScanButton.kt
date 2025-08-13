package com.pixelpioneer.moneymaster.ui.components.common.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.R

/**
 * A button component for initiating the receipt scanning process.
 *
 * This component displays either a scan button or a loading indicator
 * depending on the current scanning state. When pressed, it triggers
 * the receipt scanning workflow.
 *
 * @param isEnabled Whether the scan button is enabled
 * @param isLoading Whether the scanning process is currently running
 * @param onScan Callback invoked when the scan button is pressed
 */
@Composable
fun ScanButton(
    isEnabled: Boolean,
    isLoading: Boolean,
    onScan: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.receipts_scanning_progress))
                }
            } else {
                Button(
                    onClick = onScan,
                    enabled = isEnabled,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.receipts_scan_button))
                }
            }
        }
    }
}
