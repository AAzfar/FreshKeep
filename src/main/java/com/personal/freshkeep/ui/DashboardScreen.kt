package com.personal.freshkeep.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.freshkeep.data.InventoryItem

@Composable
fun DashboardScreen(
    viewModel: InventoryViewModel,
    onOpenScanner: () -> Unit,
    onOpenManualEntry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemList by viewModel.items.collectAsState()
    var selectedSection by remember { mutableIntStateOf(0) }
    var selectedFinishedIds by remember { mutableStateOf(emptySet<Int>()) }
    val currentTime = System.currentTimeMillis()
    val availableItems = itemList.filter {
        !it.isOnBuyList && it.isAvailable && it.expiryDateEpoch >= currentTime
    }
    val finishedItems = itemList.filter {
        !it.isOnBuyList && (!it.isAvailable || it.expiryDateEpoch < currentTime)
    }
    val buyListItems = itemList.filter { it.isOnBuyList }
    val finishedItemIds = finishedItems.map { it.id }.toSet()

    LaunchedEffect(finishedItemIds) {
        selectedFinishedIds = selectedFinishedIds.intersect(finishedItemIds)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F9FA))
            .padding(16.dp)
    ) {
        // App Master Banner Header
        Text(
            text = "FreshKeep Dashboard",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Control Panel Row Button
        Button(
            onClick = onOpenScanner,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061A4)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("📷 Scan Barcode to Add", fontSize = 16.sp, color = Color.White)
        }

        // Add this Button in the column right beneath the existing camera scan button:
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onOpenManualEntry,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("➕ Add Item Manually", fontSize = 16.sp, color = Color(0xFF0061A4))
        }

        Spacer(modifier = Modifier.height(16.dp))

        ScrollableTabRow(selectedTabIndex = selectedSection) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("Available (${availableItems.size})") }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Finished / Expired (${finishedItems.size})") }
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("Buy List (${buyListItems.size})") }
            )
        }

        if (selectedSection == 1) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${selectedFinishedIds.size} selected", color = Color.Gray)
                Button(
                    onClick = {
                        viewModel.addToBuyList(selectedFinishedIds)
                        selectedFinishedIds = emptySet()
                    },
                    enabled = selectedFinishedIds.isNotEmpty()
                ) {
                    Text("Add to Buy List")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sectionItems = when (selectedSection) {
                0 -> availableItems
                1 -> finishedItems
                else -> buyListItems
            }
            if (sectionItems.isEmpty()) {
                item {
                    Text(
                        text = when (selectedSection) {
                            0 -> "No available items"
                            1 -> "No finished or expired items"
                            else -> "Your buy list is empty"
                        },
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        color = Color.Gray
                    )
                }
            }
            items(sectionItems, key = { it.id }) { item ->
                var activeJsonDetails = remember { mutableStateOf<String?>(null) }
                
                InventoryRow(
                    item = item, 
                    onDelete = { viewModel.removeItem(item) },
                    onAvailabilityChange = { isAvailable -> viewModel.updateAvailability(item, isAvailable) },
                    showAvailabilityControl = selectedSection != 2,
                    selectedForBuyList = item.id in selectedFinishedIds,
                    onSelectionChange = if (selectedSection == 1) {
                        { isSelected ->
                            selectedFinishedIds = if (isSelected) {
                                selectedFinishedIds + item.id
                            } else {
                                selectedFinishedIds - item.id
                            }
                        }
                    } else null,
                    onMarkPurchased = if (selectedSection == 2) {
                        { viewModel.markPurchased(item) }
                    } else null,
                    onShowDetails = { json -> activeJsonDetails.value = json }
                )
                
                // Interactive JSON System Viewer Modal overlay box
                if (activeJsonDetails.value != null) {
                    AlertDialog(
                        onDismissRequest = { activeJsonDetails.value = null },
                        title = { Text("Barcode Metadata Payload JSON") },
                        text = {
                            Box(modifier = Modifier.heightIn(max = 300.dp)) {
                                LazyColumn {
                                    item {
                                        Text(
                                            text = activeJsonDetails.value!!, 
                                            fontSize = 11.sp, 
                                            color = Color.DarkGray,
                                            style = LocalTextStyle.current.copy(lineHeight = 16.sp)
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { activeJsonDetails.value = null }) { Text("Close") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryRow(
    item: InventoryItem, 
    onDelete: () -> Unit,
    onAvailabilityChange: (Boolean) -> Unit,
    showAvailabilityControl: Boolean,
    selectedForBuyList: Boolean,
    onSelectionChange: ((Boolean) -> Unit)?,
    onMarkPurchased: (() -> Unit)?,
    onShowDetails: (String) -> Unit
) {
    val currentTime = System.currentTimeMillis()
    val msLeft = item.expiryDateEpoch - currentTime
    val daysLeft = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(msLeft)

    val statusColor = when {
        daysLeft < 0 -> Color(0xFFBA1A1A) 
        daysLeft <= 7 -> Color(0xFFE2C000) 
        else -> Color(0xFF386B01) 
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (daysLeft < 0) "Expired!" else "$daysLeft days left",
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(text = "Category: ${item.category}", fontSize = 12.sp, color = Color.Gray)
                if (!item.barcode.isNullOrBlank()) {
                    Text(text = "Code: ${item.barcode}", fontSize = 11.sp, color = Color.Gray)
                }
                if (onSelectionChange != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Select for buy list", fontSize = 12.sp, color = Color.Gray)
                        Checkbox(
                            checked = selectedForBuyList,
                            onCheckedChange = onSelectionChange
                        )
                    }
                }
                if (showAvailabilityControl) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (item.isOnBuyList) "On buy list" else if (item.isAvailable) "Available" else "Finished",
                        fontSize = 12.sp,
                        color = if (item.isAvailable) Color(0xFF386B01) else Color(0xFF666666)
                    )
                    Switch(
                        checked = item.isAvailable,
                        onCheckedChange = onAvailabilityChange
                    )
                }
                }
            }
            
            // Action Panel Management Layout Hub
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (onMarkPurchased != null) {
                    Button(
                        onClick = onMarkPurchased,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Bought", fontSize = 12.sp)
                    }
                }
                if (!item.rawJsonDetails.isNullOrBlank()) {
                    Button(
                        onClick = { onShowDetails(item.rawJsonDetails) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1F5FE)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Details", color = Color(0xFF0288D1), fontSize = 12.sp)
                    }
                }
                
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAD7)),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Remove", color = Color(0xFF410002), fontSize = 12.sp)
                }
            }
        }
    }
}
