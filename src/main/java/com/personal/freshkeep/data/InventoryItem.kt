package com.personal.freshkeep.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ItemCategory(val displayName: String, val useExplicitCalendar: Boolean) {
    FRESH_FOOD("Fresh Food (Days)", useExplicitCalendar = false),
    GROCERY("Pantry/Groceries (Days)", useExplicitCalendar = false),
    MEDICINE("Medicine (Exact Date)", useExplicitCalendar = true),
    BABY_CARE("Baby Care (Exact Date)", useExplicitCalendar = true),
    OTHERS("Others (Days)", useExplicitCalendar = false)
}

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val barcode: String?,
    val name: String,
    val category: String = "OTHERS", // SQLite text-based storage backup field
    val expiryDateEpoch: Long,
    val quantity: Int
)
