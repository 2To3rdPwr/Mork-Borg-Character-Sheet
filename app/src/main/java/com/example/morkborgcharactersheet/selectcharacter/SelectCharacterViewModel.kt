package com.example.morkborgcharactersheet.selectcharacter

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectCharacterViewModel(characterId: Long, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    /**
     * LiveData
     */
    val characters = database.getCharacterList(characterId)


    /**
     * Events
     */
    private val _selectedCharacter = MutableLiveData<Long>(-1L)
    val selectedCharacter: LiveData<Long>
        get() = _selectedCharacter
    fun onSelectedCharacterDone() {
        _selectedCharacter.value = -1L
    }

    private val _newCharacterEvent = MutableLiveData<Boolean>()
    val newCharacterEvent: LiveData<Boolean>
        get() = _newCharacterEvent
    fun onNewCharacterEventDone() {
        _newCharacterEvent.value = false
    }

    fun onNewCharacterClicked() {
        _newCharacterEvent.value = true
    }

    fun onCharacterListClick(characterId: Long, type: CharacterSelectListener.CharacterSelectButton) {
        when (type) {
            CharacterSelectListener.CharacterSelectButton.SELECT -> onSelectCharacterClicked(characterId)
            CharacterSelectListener.CharacterSelectButton.DELETE -> onDeleteCharacterClicked(characterId)
        }
    }

    private fun onDeleteCharacterClicked(characterId: Long) {
        viewModelScope.launch {
            deleteCharacter(characterId)
        }
    }

    private fun onSelectCharacterClicked(characterId: Long) {
        _selectedCharacter.value = characterId
    }

    private suspend fun deleteCharacter(characterId: Long) {
        withContext(Dispatchers.IO) {
            database.clearCharacter(characterId)
        }
    }
}