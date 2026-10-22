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
import java.util.concurrent.TimeUnit

@Composable
fun DashboardScreen(
    viewModel: InventoryViewModel,
    onOpenScanner: () -> Unit,
    onOpenManualEntry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val itemList by viewModel.items.collectAsState()

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

        // Tabular Rows / Inventory Output
        LazyColumn(
            verticalArrangement = java.util.concurrent.atomic.AtomicInteger(8).get().let { Arrangement.spacedBy(8.dp) }
        ) {
            items(itemList, key = { it.id }) { item ->
                InventoryRow(item = item, onDelete = { viewModel.removeItem(item) })
            }
        }
    }
}

@Composable
fun InventoryRow(item: InventoryItem, onDelete: () -> Unit) {
    val currentTime = System.currentTimeMillis()
    val msLeft = item.expiryDateEpoch - currentTime
    val daysLeft = TimeUnit.MILLISECONDS.toDays(msLeft)

    // Mainframe-logic boundary color assessment
    val statusColor = when {
        daysLeft < 0 -> Color(0xFFBA1A1A)       // Expired: Solid Red
        daysLeft <= 7 -> Color(0xFFE2C000)      // Warning < 7 days: Amber Yellow
        else -> Color(0xFF386B01)               // Fresh: Forest Green
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = item.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    text = if (daysLeft < 0) "Expired!" else "$daysLeft days left",
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (!item.barcode.isNullOrBlank()) {
                    Text(text = "Code: ${item.barcode}", fontSize = 11.sp, color = Color.Gray)
                }
            }
            
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAD7)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Remove", color = Color(0xFF410002), fontSize = 12.sp)
            }
        }
    }
}
