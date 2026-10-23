package com.personal.freshkeep.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.freshkeep.data.AppDatabase
import com.personal.freshkeep.data.InventoryItem
import com.personal.freshkeep.data.ItemCategory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val itemDao = AppDatabase.getDatabase(application).itemDao()

    val items: StateFlow<List<InventoryItem>> = itemDao.getAllItemsSortedByExpiry()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Overloaded assignment strategy handler
    fun addItem(name: String, barcode: String?, category: String, expiryDateEpoch: Long, quantity: Int, rawJson: String? = null) {
        viewModelScope.launch {
            val newItem = InventoryItem(
                name = name,
                barcode = if (barcode.isNullOrBlank()) null else barcode,
                category = category,
                expiryDateEpoch = expiryDateEpoch,
                quantity = quantity,
                rawJsonDetails = rawJson
            )
            itemDao.insertOrUpdateItem(newItem)
        }
    }

    fun removeItem(item: InventoryItem) {
        viewModelScope.launch {
            itemDao.deleteItem(item)
        }
    }
}