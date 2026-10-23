package com.personal.freshkeep.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ItemCategory(val displayName: String, val defaultDays: Int) {
    FRESH_FOOD("Fresh Food", 5),
    GROCERY("Pantry/Groceries", 90),
    MEDICINE("Medicine", 365),
    BABY_CARE("Baby Care", 180),
    OTHERS("Others", 14)
}

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val barcode: String?,
    val name: String,
    val category: String = "OTHERS",
    val expiryDateEpoch: Long,
    val quantity: Int,
    val rawJsonDetails: String? = null // Storing the full raw server payload response payload string
)