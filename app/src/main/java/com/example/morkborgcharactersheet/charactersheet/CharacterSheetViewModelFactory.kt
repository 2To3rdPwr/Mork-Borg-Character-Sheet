package com.example.morkborgcharactersheet.charactersheet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import java.lang.IllegalArgumentException

class CharacterSheetViewModelFactory(private val characterId: Long, private val dataSource: CharacterDatabaseDAO) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterSheetViewModel::class.java)){
            return CharacterSheetViewModel(characterId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}