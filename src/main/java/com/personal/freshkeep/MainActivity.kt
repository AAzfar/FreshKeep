package com.personal.freshkeep

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.personal.freshkeep.data.ExpiryNotificationWorker
import com.personal.freshkeep.data.ItemCategory
import com.personal.freshkeep.scanner.BarcodeNestClient
import com.personal.freshkeep.scanner.CameraPreview
import com.personal.freshkeep.ui.DashboardScreen
import com.personal.freshkeep.ui.InventoryViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val viewModel: InventoryViewModel by viewModels()
    private var isScannerOpen = mutableStateOf(false)
    private var showAddDialog = mutableStateOf(false)
    private var targetBarcode = mutableStateOf<String?>(null)
    private var detectedNetworkName = mutableStateOf("")
    private var detectedNetworkJson = mutableStateOf<String?>(null)
    private var lookupInProgress = mutableStateOf(false)
    private var lookupStatus = mutableStateOf("")

    // Camera access must be granted at runtime; without it CameraX only shows a black preview
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            isScannerOpen.value = true
        } else {
            Toast.makeText(this, "Camera permission is required to scan barcodes", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBackgroundAlarmTask()

        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                if (isScannerOpen.value) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CameraPreview(onBarcodeScanned = { code ->
                            if (code != targetBarcode.value) {
                                targetBarcode.value = code
                                isScannerOpen.value = false
                                detectedNetworkName.value = ""
                                detectedNetworkJson.value = null
                                lookupInProgress.value = true
                                lookupStatus.value = "Looking up barcode..."
                                showAddDialog.value = true

                                lifecycleScope.launch {
                                    val result = BarcodeNestClient.fetchProductData(code)
                                    detectedNetworkName.value = result.productName.orEmpty()
                                    detectedNetworkJson.value = result.rawJson
                                    lookupStatus.value = result.message
                                    lookupInProgress.value = false
                                }
                            }
                        })
                    }
                } else {
                    DashboardScreen(
                        viewModel = viewModel,
                        onOpenScanner = { openScanner() },
                        onOpenManualEntry = { 
                            targetBarcode.value = null
                            detectedNetworkName.value = ""
                            detectedNetworkJson.value = null
                            lookupInProgress.value = false
                            lookupStatus.value = ""
                            showAddDialog.value = true 
                        }
                    )
                }

                if (showAddDialog.value) {
                    ItemEntryFormDialog()
                }
            }
        }
    }

    @Composable
    fun ItemEntryFormDialog() {
        var name by remember(targetBarcode.value) { mutableStateOf(detectedNetworkName.value) }
        var category by remember { mutableStateOf(ItemCategory.OTHERS) }
        var inputModeByDays by remember { mutableStateOf(true) } // Dual entry switch toggle
        var showRawResponse by remember { mutableStateOf(false) }

        LaunchedEffect(detectedNetworkName.value) {
            if (name.isBlank() && detectedNetworkName.value.isNotBlank()) {
                name = detectedNetworkName.value
            }
        }
        
        // Input string assignments
        var daysInput by remember { mutableStateOf(category.defaultDays.toString()) }
        var dateYear by remember { mutableStateOf("2026") }
        var dateMonth by remember { mutableStateOf("12") }
        var dateDay by remember { mutableStateOf("31") }

        // Sync local default values instantly when user changes structural category
        LaunchedEffect(category) {
            if (inputModeByDays) {
                daysInput = category.defaultDays.toString()
            }
        }

        AlertDialog(
            onDismissRequest = { showAddDialog.value = false },
            title = { Text(if (targetBarcode.value == null) "Manual Addition" else "Barcode Found") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Item Name") })

                    if (targetBarcode.value != null) {
                        if (lookupInProgress.value) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                        Text(lookupStatus.value, style = MaterialTheme.typography.bodySmall)
                        detectedNetworkJson.value?.let { rawJson ->
                            TextButton(onClick = { showRawResponse = !showRawResponse }) {
                                Text(if (showRawResponse) "Hide API response" else "Show API response")
                            }
                            if (showRawResponse) {
                                SelectionContainer {
                                    Text(
                                        text = rawJson,
                                        modifier = Modifier.heightIn(max = 140.dp).verticalScroll(rememberScrollState()),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                    
                    Text("Category Selector:", fontWeight = FontWeight.Bold)
                    Row {
                        ItemCategory.values().take(3).forEach {
                            FilterChip(selected = category == it, onClick = { category = it }, label = { Text(it.displayName, fontSize = 10.sp) })
                        }
                    }
                    Row {
                        ItemCategory.values().drop(3).forEach {
                            FilterChip(selected = category == it, onClick = { category = it }, label = { Text(it.displayName, fontSize = 10.sp) })
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Use Day Count Offset")
                        Switch(checked = inputModeByDays, onCheckedChange = { inputModeByDays = it })
                        Text("Use Target Date")
                    }

                    if (inputModeByDays) {
                        OutlinedTextField(value = daysInput, onValueChange = { daysInput = it }, label = { Text("Days Until Expiry") })
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(value = dateYear, onValueChange = { dateYear = it }, label = { Text("YYYY") }, modifier = Modifier.weight(1.5f))
                            OutlinedTextField(value = dateMonth, onValueChange = { dateMonth = it }, label = { Text("MM") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = dateDay, onValueChange = { dateDay = it }, label = { Text("DD") }, modifier = Modifier.weight(1f))
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        val isExplicitDate = !inputModeByDays
                        val timeInput = if (inputModeByDays) {
                            daysInput.toLongOrNull() ?: category.defaultDays.toLong()
                        } else {
                            val cal = Calendar.getInstance()
                            cal.set(dateYear.toIntOrNull() ?: 2026, (dateMonth.toIntOrNull() ?: 12) - 1, dateDay.toIntOrNull() ?: 31)
                            cal.timeInMillis
                        }
                        val finalEpoch = if (isExplicitDate) {
                            timeInput
                        } else {
                            Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, timeInput.toInt())
                            }.timeInMillis
                        }

                        viewModel.addItem(
                            name = name,
                            barcode = targetBarcode.value,
                            category = category.name,
                            expiryDateEpoch = finalEpoch,
                            quantity = 1,
                            rawJson = detectedNetworkJson.value // Binding full tracking details to database row entry field
                        )
                        showAddDialog.value = false
                    }
                }) { Text("Save to Storage") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog.value = false }) { Text("Cancel") } }
        )
    }

    private fun openScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            isScannerOpen.value = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun initBackgroundAlarmTask() {
        val alertRequest = PeriodicWorkRequestBuilder<ExpiryNotificationWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "freshkeep_notification_job",
            ExistingPeriodicWorkPolicy.KEEP,
            alertRequest
        )
    }
}