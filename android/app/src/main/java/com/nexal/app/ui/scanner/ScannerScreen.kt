package com.nexal.app.ui.scanner

import android.Manifest
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.nexal.app.domain.model.FoodAlternative
import com.nexal.app.domain.model.FoodAssessment
import com.nexal.app.domain.model.ScannedProduct
import com.nexal.app.ui.components.*
import com.nexal.app.ui.theme.Cyan500
import com.nexal.app.ui.theme.Emerald500
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onBack: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Barcode Scanner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingScreen(message = "Looking up product...")
                }
                uiState.product != null -> {
                    ProductResultCard(
                        product = uiState.product!!,
                        ratio = uiState.proteinRatio,
                        ratingLabel = uiState.ratingLabel,
                        ratingColor = uiState.ratingColor,
                        onAddToLog = { viewModel.addToLog() },
                        onScanAgain = { viewModel.resetAndScan() }
                    )

                    uiState.aiAssessment?.let { assessment ->
                        AiAssessmentCard(
                            assessment = assessment.assessment,
                            alternatives = assessment.alternatives
                        )
                    }
                }
                uiState.error != null -> {
                    FitCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Text(uiState.error!!, style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(Modifier.height(12.dp))
                            FitButton(text = "Try Again", onClick = { viewModel.resetAndScan() })
                        }
                    }
                }
                !cameraPermission.status.isGranted -> {
                    EmptyState(
                        icon = Icons.Default.CameraAlt,
                        title = "Camera Permission Required",
                        description = "Grant camera access to scan barcodes.",
                        actionLabel = "Grant Permission",
                        onAction = { cameraPermission.launchPermissionRequest() }
                    )
                }
                uiState.scanning -> {
                    var torchOn by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        BarcodeCameraPreview(
                            onBarcodeDetected = { barcode ->
                                viewModel.onBarcodeScanned(barcode)
                            },
                            torchEnabled = torchOn
                        )

                        // Top overlay bar with torch + cancel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .background(Color.Black.copy(alpha = 0.45f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cancel button
                            TextButton(
                                onClick = { viewModel.stopScanning() },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Cancel", fontWeight = FontWeight.Medium)
                            }

                            // Flashlight toggle
                            IconButton(
                                onClick = { torchOn = !torchOn },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if (torchOn) Color(0xFFFBBF24).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Icon(
                                    if (torchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                                    contentDescription = if (torchOn) "Turn off flashlight" else "Turn on flashlight",
                                    tint = if (torchOn) Color(0xFFFBBF24) else Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Scan hint at the bottom
                        Text(
                            "Point at a barcode",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 20.dp, vertical = 8.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                else -> {
                    FitCard {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = Emerald500.copy(alpha = 0.1f),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Emerald500,
                                    modifier = Modifier.padding(20.dp).size(36.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text("Point your camera at a product barcode", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(16.dp))
                            GradientButton(text = "Start Scanning", onClick = { viewModel.startScanning() }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            }
        }
    }

    uiState.toast?.let {
        LaunchedEffect(it) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearToast()
        }
    }
}

@Composable
fun BarcodeCameraPreview(
    onBarcodeDetected: (String) -> Unit,
    torchEnabled: Boolean = false,
    onCameraReady: ((Camera) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var detected by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }

    // Toggle torch when the parameter changes
    LaunchedEffect(torchEnabled) {
        camera?.cameraControl?.enableTorch(torchEnabled)
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            @androidx.camera.core.ExperimentalGetImage
                            val mediaImage = imageProxy.image
                            if (mediaImage != null && !detected) {
                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                val scanner = BarcodeScanning.getClient()
                                scanner.process(image)
                                    .addOnSuccessListener { barcodes ->
                                        for (barcode in barcodes) {
                                            barcode.rawValue?.let { value ->
                                                if (!detected && (barcode.format == Barcode.FORMAT_EAN_13 || barcode.format == Barcode.FORMAT_EAN_8 || barcode.format == Barcode.FORMAT_UPC_A || barcode.format == Barcode.FORMAT_UPC_E)) {
                                                    detected = true
                                                    onBarcodeDetected(value)
                                                }
                                            }
                                        }
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    val cam = cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
                    camera = cam
                    onCameraReady?.invoke(cam)
                    // Apply initial torch state
                    cam.cameraControl.enableTorch(torchEnabled)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxWidth().height(300.dp)
    )
}

@Composable
private fun ProductResultCard(
    product: ScannedProduct,
    ratio: Double,
    ratingLabel: String,
    ratingColor: Color,
    onAddToLog: () -> Unit,
    onScanAgain: () -> Unit
) {
    FitCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(product.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            product.brand?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Text("Per ${product.servingSize}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(16.dp))

            // Macro grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MacroChip("${product.macros.calories}", "Calories", MaterialTheme.colorScheme.onSurface, Modifier.weight(1f))
                MacroChip("${product.macros.protein}g", "Protein", Emerald500, Modifier.weight(1f))
                MacroChip("${product.macros.carbs}g", "Carbs", Cyan500, Modifier.weight(1f))
                MacroChip("${product.macros.fats}g", "Fats", Color(0xFFF59E0B), Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            // Protein-to-Calorie Ratio
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Protein-to-Calorie Ratio", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("${"%.1f".format(ratio)}g/100cal", fontWeight = FontWeight.Bold, color = ratingColor)
                        Surface(color = ratingColor.copy(alpha = 0.15f), shape = MaterialTheme.shapes.extraSmall) {
                            Text(ratingLabel, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = ratingColor)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientButton(text = "Add to Log", onClick = onAddToLog, modifier = Modifier.weight(1f))
                FitButton(text = "Scan Again", onClick = onScanAgain, variant = ButtonVariant.SECONDARY, icon = Icons.Default.Refresh)
            }
        }
    }
}

@Composable
private fun MacroChip(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small, modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AiAssessmentCard(
    assessment: String,
    alternatives: List<FoodAlternative>
) {
    FitCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("AI Assessment", style = MaterialTheme.typography.titleSmall, color = Cyan500, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(assessment, style = MaterialTheme.typography.bodyMedium)

            if (alternatives.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("Better Alternatives:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                alternatives.forEach { alt ->
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), shape = MaterialTheme.shapes.small) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(alt.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(alt.reason, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            alt.macros?.let { macros ->
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${macros.protein}g protein", style = MaterialTheme.typography.bodySmall, color = Emerald500)
                                    Text("${macros.calories} cal", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}
