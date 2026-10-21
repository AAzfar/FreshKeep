package com.personal.freshkeep.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    // Stream all items, sorted chronologically so the closest expiry dates sit at the absolute top
    @Query("SELECT * FROM inventory_items ORDER BY expiryDateEpoch ASC")
    fun getAllItemsSortedByExpiry(): Flow<List<InventoryItem>>

    // Simple lookup to see if a barcode already exists in your kitchen cabinets
    @Query("SELECT * FROM inventory_items WHERE barcode = :barcode LIMIT 1")
    suspend fun getItemByBarcode(barcode: String): InventoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)
}
