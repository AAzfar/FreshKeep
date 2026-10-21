package com.personal.freshkeep

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.personal.freshkeep.scanner.CameraPreview
import com.personal.freshkeep.ui.DashboardScreen
import com.personal.freshkeep.ui.InventoryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: InventoryViewModel by viewModels()
    private var isScannerOpen = mutableStateOf(false)
    private var lastScannedBarcode = mutableStateOf("")

    // Runtime hardware authorization handler
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            isScannerOpen.value = true
        } else {
            Toast.makeText(this, "Camera permission is required to scan items.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                var showAddDialog by remember { mutableStateOf(false) }

                if (isScannerOpen.value) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CameraPreview(
                            onBarcodeScanned = { code ->
                                // Debounce framework to prevent duplicate scan processing loops
                                if (code != lastScannedBarcode.value) {
                                    lastScannedBarcode.value = code
                                    isScannerOpen.value = false
                                    showAddDialog = true
                                }
                            }
                        )
                        
                        // Floating Back Button to exit scanner view overlay safely
                        Button(
                            onClick = { isScannerOpen.value = false },
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) {
                            Text("Back to Dashboard")
                        }
                    }
                } else {
                    DashboardScreen(
                        viewModel = viewModel,
                        onOpenScanner = { checkAndRequestCameraPermission() }
                    )
                }

                // Simple Prompt Dialog overlay to insert a scanned item
                if (showAddDialog) {
                    var itemName by remember { mutableStateOf("") }
                    var itemExpiryDays by remember { mutableStateOf("14") } // Default to 2 weeks structural fallback
                    
                    AlertDialog(
                        onDismissRequest = { showAddDialog = false; lastScannedBarcode.value = "" },
                        title = { Text("New Scanned Item Found") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Barcode: ${lastScannedBarcode.value}", color = Color.Gray)
                                OutlinedTextField(
                                    value = itemName,
                                    onValueChange = { itemName = it },
                                    label = { Text("Item Name (e.g., Milk)") }
                                )
                                OutlinedTextField(
                                    value = itemExpiryDays,
                                    onValueChange = { itemExpiryDays = it },
                                    label = { Text("Days until expiry") }
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (itemName.isNotBlank()) {
                                    val days = itemExpiryDays.toIntOrNull() ?: 14
                                    viewModel.addItem(
                                        name = itemName,
                                        barcode = lastScannedBarcode.value,
                                        daysToExpiry = days,
                                        quantity = 1
                                    )
                                    showAddDialog = false
                                    lastScannedBarcode.value = ""
                                }
                            }) { Text("Save Item") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddDialog = false; lastScannedBarcode.value = "" }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }

    private fun checkAndRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                isScannerOpen.value = true
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
