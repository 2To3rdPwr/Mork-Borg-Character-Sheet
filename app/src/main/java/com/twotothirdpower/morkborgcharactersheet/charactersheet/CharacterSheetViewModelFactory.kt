package com.twotothirdpower.morkborgcharactersheet.charactersheet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabase
import java.lang.IllegalArgumentException

class CharacterSheetViewModelFactory(private val characterId: Long, private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataSource = CharacterDatabase.getInstance(application).characterDatabaseDAO

        if (modelClass.isAssignableFrom(CharacterSheetViewModel::class.java)){
            return CharacterSheetViewModel(characterId, dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}