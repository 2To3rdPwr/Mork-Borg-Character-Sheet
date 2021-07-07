package com.twotothirdpower.morkborgcharactersheet.addinventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabaseDAO
import java.lang.IllegalArgumentException

class AddInventoryViewModelFactory (private val characterId: Long, private val dataSource: CharacterDatabaseDAO) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddInventoryViewModel::class.java)) {
            return AddInventoryViewModel(characterId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}