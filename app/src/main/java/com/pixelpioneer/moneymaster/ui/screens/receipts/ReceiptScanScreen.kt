package com.pixelpioneer.moneymaster.ui.screens.receipts

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.ui.components.common.buttons.AddAllTransactionsButton
import com.pixelpioneer.moneymaster.ui.components.common.cards.CategorySelectionCard
import com.pixelpioneer.moneymaster.ui.components.common.cards.ErrorCard
import com.pixelpioneer.moneymaster.ui.components.common.cards.ImageSelectionCard
import com.pixelpioneer.moneymaster.ui.components.common.cards.InfoCard
import com.pixelpioneer.moneymaster.ui.components.common.buttons.ScanButton
import com.pixelpioneer.moneymaster.ui.components.common.cards.ScannedItemCard
import com.pixelpioneer.moneymaster.ui.navigation.MoneyMasterBottomNavigation
import com.pixelpioneer.moneymaster.ui.viewmodel.CategoryViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.ReceiptScanViewModel
import com.pixelpioneer.moneymaster.ui.viewmodel.TransactionViewModel
import com.pixelpioneer.moneymaster.core.util.uriToFile
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScanScreen(
    navController: NavController,
    receiptScanViewModel: ReceiptScanViewModel,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel
) {
    val context = LocalContext.current
    val scannedItems by receiptScanViewModel.scannedItems.collectAsState()
    val isLoading by receiptScanViewModel.isLoading.collectAsState()
    val error by receiptScanViewModel.error.collectAsState()
    val categories by categoryViewModel.categories.collectAsState(initial = emptyList())

    var editableItems by remember {
        mutableStateOf<List<Transaction>>(
            emptyList()
        )
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedCategory by remember { mutableStateOf<TransactionCategory?>(null) }
    var scanTriggered by remember { mutableStateOf(false) }

    val tempImageFile = remember {
        File(context.cacheDir, "temp_receipt_${System.currentTimeMillis()}.jpg")
    }

    val tempImageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempImageFile
        )
    }

    LaunchedEffect(scannedItems) {
        editableItems = scannedItems.toList()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        scanTriggered = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = tempImageUri
            scanTriggered = false
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tempImageUri)
        }
    }

    Scaffold(
        bottomBar = { MoneyMasterBottomNavigation(navController) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.receipts_title)) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ImageSelectionCard(
                    selectedImageUri = selectedImageUri,
                    onImageSelect = { imagePickerLauncher.launch("image/*") },
                    onCameraCapture = {
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                cameraLauncher.launch(tempImageUri)
                            }

                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }
                )
            }


            if (selectedImageUri != null) {
                item {
                    CategorySelectionCard(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }

                item {
                    ScanButton(
                        isEnabled = !isLoading && selectedCategory != null,
                        isLoading = isLoading,
                        onScan = {
                            selectedImageUri?.let { uri ->
                                selectedCategory?.let { category ->
                                    try {
                                        val file = uriToFile(uri, context)
                                        Log.d(
                                            "ReceiptScan",
                                            "Start scanning for file: ${file.absolutePath}"
                                        )
                                        receiptScanViewModel.scanReceipt(
                                            file, category,
                                            context = context
                                        )
                                        scanTriggered = true
                                    } catch (e: Exception) {
                                        Log.e(
                                            "ReceiptScan",
                                            "Error to load: ${e.message}"
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            error?.let {
                item {
                    ErrorCard(error = it)
                }
            }

            if (scanTriggered && !isLoading && scannedItems.isEmpty() && error == null) {
                item {
                    InfoCard(message = stringResource(R.string.receipts_no_items_found))
                }
            }

            if (editableItems.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.receipts_found_items_count) + " (${editableItems.size})",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                itemsIndexed(editableItems) { index, item ->
                    ScannedItemCard(
                        item = item,
                        index = index + 1,
                        onItemUpdated = { updatedItem ->
                            editableItems = editableItems.toMutableList().apply {
                                set(index, updatedItem)
                            }
                        },
                        onItemRemoved = {
                            editableItems = editableItems.toMutableList().apply {
                                removeAt(index)
                            }
                        }
                    )
                }

                item {
                    AddAllTransactionsButton(
                        totalAmount = editableItems.sumOf { it.amount },
                        onAddAll = {
                            editableItems.forEach { transactionViewModel.addTransactionDirect(it) }
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
