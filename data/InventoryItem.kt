package com.personal.freshkeep.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val barcode: String?,
    val name: String,
    val expiryDateEpoch: Long, // Storing timestamps as Long (Unix epoch) for lightning-fast date math
    val quantity: Int
)