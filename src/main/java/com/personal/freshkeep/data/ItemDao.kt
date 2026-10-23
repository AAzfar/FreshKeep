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

    @Query("UPDATE inventory_items SET isAvailable = :isAvailable WHERE id = :itemId")
    suspend fun updateAvailability(itemId: Int, isAvailable: Boolean)

    @Query("UPDATE inventory_items SET isOnBuyList = 1 WHERE id IN (:itemIds)")
    suspend fun addItemsToBuyList(itemIds: List<Int>)

    @Query("UPDATE inventory_items SET name = :name, isOnBuyList = 0, isAvailable = 1, expiryDateEpoch = :expiryDateEpoch WHERE id = :itemId")
    suspend fun markItemPurchased(itemId: Int, name: String, expiryDateEpoch: Long)

    @Delete
    suspend fun deleteItem(item: InventoryItem)
}
