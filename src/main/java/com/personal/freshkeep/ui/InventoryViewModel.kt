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
    fun addItem(name: String, barcode: String?, category: ItemCategory, timeInput: Long, quantity: Int, isExplicitDate: Boolean) {
        viewModelScope.launch {
            val targetEpoch = if (isExplicitDate) {
                timeInput // Direct epoch timestamp passed from picker
            } else {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, timeInput.toInt())
                calendar.timeInMillis
            }
            
            val newItem = InventoryItem(
                name = name,
                barcode = if (barcode.isNullOrBlank()) null else barcode,
                category = category.name,
                expiryDateEpoch = targetEpoch,
                quantity = quantity
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