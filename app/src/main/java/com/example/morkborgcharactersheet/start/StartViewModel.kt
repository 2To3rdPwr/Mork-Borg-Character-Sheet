package com.example.morkborgcharactersheet.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StartViewModel(dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    /**
     * Event
     */
    private val _characterId = MutableLiveData<Long?>(-1)
    val characterId: LiveData<Long?>
        get() = _characterId
    fun onCharacterIdComplete() {
        _characterId.value = -1
    }

    init {
        viewModelScope.launch {
            _characterId.value = getMostRecentCharacter()
        }
    }

    private suspend fun getMostRecentCharacter(): Long? {
        return withContext(Dispatchers.IO) {
            // Pause on start screen for a second to allow user to read license
            Thread.sleep(2_000)
            database.getMostRecentCharacterId()
        }
    }
}