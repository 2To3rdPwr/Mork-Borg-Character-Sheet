package com.example.morkborgcharactersheet.editcharacter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.morkborgcharactersheet.database.Character
import com.example.morkborgcharactersheet.database.CharacterDatabase
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import com.example.morkborgcharactersheet.models.AbilityType
import com.example.morkborgcharactersheet.models.Dice
import com.example.morkborgcharactersheet.models.DiceValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class EditCharacterViewModel(private val characterId: Long, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    private lateinit var character: Character
    private var leveledUp = false

    private val _newCharacter = MutableLiveData<Boolean>()
    val newCharacter: LiveData<Boolean>
        get() = _newCharacter

    private val _newCharacterId = MutableLiveData<Long>()
    val newCharacterId: LiveData<Long>
        get() = _newCharacterId

    val characterName = MutableLiveData<String>()
    val characterDescription = MutableLiveData<String>()
    val strength = MutableLiveData<String>()
    val agility = MutableLiveData<String>()
    val presence = MutableLiveData<String>()
    val toughness = MutableLiveData<String>()
    val hp = MutableLiveData<Int>()

    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String>
        get() = _toastText

    /**
     * Events
     */
    private val _saveEvent = MutableLiveData<Boolean>()
    val saveEvent: LiveData<Boolean>
        get() = _saveEvent
    fun onSaveEventDone() {
        _saveEvent.value = false
    }

    private val _snackbar = MutableLiveData<String>()
    val snackbar: LiveData<String>
        get() = _snackbar
    fun onSnackbarComplete() {
        _snackbar.value = ""
    }

    /**
     * FE Functions
     */
    fun onAbilityRolled(ability: AbilityType) {
        val newScore = rollAbility()

        when (ability) {
            AbilityType.STRENGTH -> strength.value = newScore.toString()
            AbilityType.AGILITY -> agility.value = newScore.toString()
            AbilityType.PRESENCE -> presence.value = newScore.toString()
            AbilityType.TOUGHNESS -> toughness.value = newScore.toString()
            else -> return
        }
    }

    fun onHPRolled() {
        hp.value = Math.max(Dice(diceValue = DiceValue.D8).roll(toughness.value?.toIntOrNull()?:1), 1)
    }

    fun onLevelUp() {
        // Leveling HP: Roll 6D10. If >= current max HP, increase max HP by d6
        if (hp.value!! <= Dice(6, DiceValue.D10).roll()) {
            hp.value = hp.value?:0 + Dice(diceValue = DiceValue.D6).roll()
        }

        // Leveling abilities: Roll 1D6. If greater than the ability, increase it by 1. If less, decrease it by one
        //      Abilities from -3 to +1 always increase except when a 1 is rolled, in which case they are decreased
        //      Maximum value is +6. Minimum value is -3

        // Strength
        var str = strength.value?.toIntOrNull()?:0
        val strRoll = Dice(diceValue = DiceValue.D6).roll()
        if (strRoll == 1 && str > -3) {
            str -= 1
        } else if (strRoll > str || str <= 1) {
            str ++
        } else if (strRoll < str) {
            str --
        }
        strength.value = str.toString()

        // Agility
        var agl = agility.value?.toIntOrNull()?:0
        val aglRoll = Dice(diceValue = DiceValue.D6).roll()
        if (aglRoll == 1 && agl > -3) {
            agl -= 1
        } else if (aglRoll > agl || agl <= 1) {
            agl ++
        } else if (aglRoll < agl) {
            agl --
        }
        agility.value = agl.toString()

        // Presence
        var pres =  presence.value?.toIntOrNull()?:0
        val presRoll = Dice(diceValue = DiceValue.D6).roll()
        if (presRoll == 1 && pres > -3) {
            pres -= 1
        } else if (presRoll > pres || pres <= 1) {
            pres ++
        } else if (presRoll < pres) {
            pres --
        }
        presence.value = pres.toString()

        // Toughness
        var tgh = toughness.value?.toIntOrNull()?:0
        val tghRoll = Dice(diceValue = DiceValue.D6).roll()
        if (tghRoll == 1 && tgh > -3) {
            tgh -= 1
        } else if (tghRoll > tgh || tgh <= 1) {
            tgh ++
        } else if (tghRoll < tgh) {
            tgh --
        }
        toughness.value = tgh.toString()

        leveledUp = true
    }

    fun onSave() {
        // Default to -6 to trip input validation
        // (-6 is a bit of an arbitrary magic number :/)
        val str = strength.value?.toIntOrNull()?:-6
        val agl = agility.value?.toIntOrNull()?:-6
        val pres = presence.value?.toIntOrNull()?:-6
        val tgh = toughness.value?.toIntOrNull()?:-6

        if (characterName.value?:"" == "") {
            _snackbar.value = "Must input character's name"
        } else if (hp.value?:0 <= 0) {
            _snackbar.value = "HP must be a positive integer"
        } else if (str > 6 || str < -3 || agl > 6 || agl < -3 || pres > 6 || pres < -3 || tgh > 6 || tgh < -3) {
            _snackbar.value = "Ability scores must be between -3 and 6"
        } else {
            character.characterName = characterName.value!!
            character.description = characterDescription.value ?: ""
            character.maxHP = hp.value!!
            character.strength = str
            character.agility = agl
            character.presence = pres
            character.toughness = tgh

            if (newCharacter.value == true) {
                character.currentHP = hp.value!!
                character.silver = Dice(2, DiceValue.D6).roll() * 10
                character.powers = Math.max(
                    Dice(diceValue = DiceValue.D4).roll(pres), 0
                )
                character.omens = Dice(diceValue = DiceValue.D2).roll()
                viewModelScope.launch {
                    _newCharacterId.value = saveNewCharacter(character)
                    _saveEvent.value = true
                }
            } else {
                if (leveledUp) {
                    character.currentHP = hp.value!!
                    character.powers = Math.max(
                        Dice(diceValue = DiceValue.D4).roll(pres), 0
                    )
                    character.omens = Dice(diceValue = DiceValue.D2).roll()
                }
                character.currentHP = Math.min(hp.value!!, character.currentHP)

                viewModelScope.launch {
                    updateExistingCharacter(character)
                }
                _newCharacterId.value = characterId
                _saveEvent.value = true
            }
        }
    }

    init {
        if (characterId != -1L) {
            _newCharacter.value = false
            viewModelScope.launch {
                character = getCharacter() ?: throw IllegalArgumentException("Attempted to modify non-existent character $characterId")
                characterName.value = character.characterName
                characterDescription.value = character.description
                strength.value = character.strength.toString()
                agility.value = character.agility.toString()
                presence.value = character.presence.toString()
                toughness.value = character.toughness.toString()
                hp.value = character.maxHP
            }
        } else {
            _newCharacter.value = true
            character = Character()
            strength.value = "0"
            agility.value = "0"
            presence.value = "0"
            toughness.value = "0"
            hp.value = 4
        }
    }

    private suspend fun getCharacter(): Character? {
        return withContext(Dispatchers.IO) {
            database.getCharacter(characterId)
        }
    }

    private suspend fun saveNewCharacter(character: Character): Long {
        return withContext(Dispatchers.IO) {
            database.insertCharacter(character)
        }
    }

    private suspend fun updateExistingCharacter(character: Character) {
        withContext(Dispatchers.IO) {
            database.updateCharacter(character)
        }
    }

    private fun rollAbility(): Int {
        val abilityScore = Dice(3, DiceValue.D6).roll()
        return when (abilityScore) {
            in 1..4 -> -3
            in 5..6 -> -2
            in 7..8 -> -1
            in 9..12 -> 0
            in 13..14 -> 1
            in 15..16 -> 2
            else -> 3
        }
    }
}