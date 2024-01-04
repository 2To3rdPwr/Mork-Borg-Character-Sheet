package com.twotothirdpower.morkborgcharactersheet.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabaseDAO
import java.lang.IllegalArgumentException

class InventoryViewModelFactory(private val characterId: Long, private val dataSource: CharacterDatabaseDAO) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)){
            return InventoryViewModel(characterId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}