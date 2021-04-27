package com.example.morkborgcharactersheet.editcharacter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.morkborgcharactersheet.charactersheet.CharacterSheetViewModel
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import java.lang.IllegalArgumentException

class EditCharacterViewModelFactory (private val characterId: Long, private val dataSource: CharacterDatabaseDAO) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditCharacterViewModel::class.java)){
            return EditCharacterViewModel(characterId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}