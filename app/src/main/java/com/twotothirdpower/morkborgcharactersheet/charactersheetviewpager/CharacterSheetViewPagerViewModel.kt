package com.twotothirdpower.morkborgcharactersheet.charactersheetviewpager

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabaseDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterSheetViewPagerViewModel(val characterId: Long, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource
    private val _characterName = MutableLiveData<String>("")
    val characterName: LiveData<String>
        get() = _characterName

    init {
        viewModelScope.launch {
            _characterName.value = getCharacterName()
            Log.i("Id", characterId.toString())
            Log.i("name", characterName.value?:"None")
        }
    }

    private suspend fun getCharacterName(): String {
        return withContext(Dispatchers.IO) {
            database.getCharactersName(characterId) ?: throw IllegalArgumentException("Illegal characterId")
        }
    }
}