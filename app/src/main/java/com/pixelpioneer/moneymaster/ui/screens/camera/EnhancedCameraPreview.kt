package com.pixelpioneer.moneymaster.ui.screens.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.Receipt
import com.pixelpioneer.moneymaster.data.model.ReceiptItem
import com.pixelpioneer.moneymaster.data.services.EnhancedReceiptOCRService
import java.io.File
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedCameraPreview(
    onNavigateBack: () -> Unit,
    onReceiptProcessed: (Receipt) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var camera by remember { mutableStateOf<Camera?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var isProcessing by remember { mutableStateOf(false) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val ocrService = remember { EnhancedReceiptOCRService() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kassenzettel scannen") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = "Zurück")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            flashMode = when (flashMode) {
                                ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                                ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                                else -> ImageCapture.FLASH_MODE_OFF
                            }
                            imageCapture?.flashMode = flashMode
                        }
                    ) {
                        Icon(
                            when (flashMode) {
                                ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                                ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                                else -> Icons.Default.FlashOff
                            },
                            contentDescription = "Flash"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        startCamera(ctx, this, lifecycleOwner) { cam, imgCapture ->
                            camera = cam
                            imageCapture = imgCapture
                            imageCapture?.flashMode = flashMode
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(0.7f)
                        .align(Alignment.Center)
                        .background(Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (!isProcessing) {
                            captureImage(
                                imageCapture = imageCapture,
                                context = context,
                                ocrService = ocrService,
                                onProcessingStart = { isProcessing = true },
                                onReceiptProcessed = { receipt ->
                                    isProcessing = false
                                    onReceiptProcessed(receipt)
                                },
                                onError = {
                                    isProcessing = false
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Foto aufnehmen",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Richten Sie den Kassenzettel im Rahmen aus und tippen Sie auf den Auslöser",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun startCamera(
    context: Context,
    previewView: PreviewView,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onCameraReady: (Camera, ImageCapture) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        try {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )

            onCameraReady(camera, imageCapture)

        } catch (exc: Exception) {
            Log.e("CameraPreview", "Kamera-Start fehlgeschlagen", exc)
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun captureImage(
    imageCapture: ImageCapture?,
    context: Context,
    ocrService: EnhancedReceiptOCRService,
    onProcessingStart: () -> Unit,
    onReceiptProcessed: (Receipt) -> Unit,
    onError: () -> Unit
) {
    val capture = imageCapture ?: return

    onProcessingStart()

    val outputFile = File(context.filesDir, "receipt_${System.currentTimeMillis()}.jpg")
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

    capture.takePicture(
        outputFileOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                processReceiptMock(onReceiptProcessed)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraPreview", "Foto-Fehler", exception)
                onError()
            }
        }
    )
}

private fun processReceiptMock(onReceiptProcessed: (Receipt) -> Unit) {
    val mockReceipt = Receipt(
        storeName = "LIDL",
        date = "24.01.2025",
        items = listOf(
            ReceiptItem("Laugenbrezel 10er", 1.99),
            ReceiptItem("Hähn.Nugg.Cornf1.XXL", 4.29),
            ReceiptItem("Penne Gorgonzala", 2.99),
            ReceiptItem("Food Bottle Banane", 2.49),
            ReceiptItem("Kaffeegetr.Espr.Mac.", 1.98),
            ReceiptItem("Frischkäse Kräuter", 1.69)
        )
    )

    onReceiptProcessed(mockReceipt)
}