package com.personal.freshkeep.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.personal.freshkeep.data.AppDatabase
import com.personal.freshkeep.data.InventoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    
    private val itemDao = AppDatabase.getDatabase(application).itemDao()

    // Expose a read-only state flow that automatically streams data directly out of SQLite
    val items: StateFlow<List<InventoryItem>> = itemDao.getAllItemsSortedByExpiry()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addItem(name: String, barcode: String?, daysToExpiry: Int, quantity: Int) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, daysToExpiry)
            
            val newItem = InventoryItem(
                name = name,
                barcode = if (barcode.isNullOrBlank()) null else barcode,
                expiryDateEpoch = calendar.timeInMillis,
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
