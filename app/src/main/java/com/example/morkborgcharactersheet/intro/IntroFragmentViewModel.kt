package com.example.morkborgcharactersheet.intro

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.morkborgcharactersheet.database.Character
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import com.example.morkborgcharactersheet.models.Dice
import com.example.morkborgcharactersheet.models.DiceValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class IntroFragmentViewModel (dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    private val _newCharacterId = MutableLiveData<Long>()
    val newCharacterId: LiveData<Long>
        get() = _newCharacterId

    /**
     * Event LiveData
     */
    private val _autoGenerateEvent = MutableLiveData<Boolean>()
    val autoGenerateEvent: LiveData<Boolean>
        get() = _autoGenerateEvent
    fun onAutoGenerateComplete() {
        _autoGenerateEvent.value = false
    }

    private val _manualGenerateEvent = MutableLiveData<Boolean>()
    val manualGenerateEvent: LiveData<Boolean>
        get() = _manualGenerateEvent
    fun onManualGenerateComplete() {
        _manualGenerateEvent.value = false
    }

    /**
     * FE Functions
     */
    fun onAutoGenerateClicked() {
        generateNewCharacter()
    }

    fun onManualGenerateClicked() {
        _manualGenerateEvent.value = true
    }

    private fun generateNewCharacter() {
        // TODO: Figure out a better way to hold this random data
        //      Ideally in a way that lets us localize it
        val namesArray: Array<String> = arrayOf("Eavoth", "Wearda", "Ardwulf", "Hamond", "Rythey", "Ryany", "Skytsav", "Gery", "Beray", "Wine", "Ament", "Forde", "Cyne", "Leofre", "Elffric", "Giles", "Grichye", "Tomath", "Sone", "Phamas", "Rancent", "Arthur", "Bertio")
        val desc1Array: Array<String> = arrayOf("Endlessly Aggravated\n", "Inferiority Complex\n", "Problems with Authority\n", "Loud Mouth\n", "Cruel\n", "Egocentric\n", "Nihilistic\n", "Prone to Substance Abuse\n", "Conflicted\n", "Shrewd\n", "Vindictive\n", "Cowardly\n", "Lazy\n", "Suspicious\n", "Ruthless\n", "Worried\n", "Bitter\n", "Deceitful\n", "Wasteful\n", "Arrogant\n")
        val desc2Array: Array<String> = arrayOf("Staring, Manic Gaze\n", "Covered in Blasphemous Tattoos\n", "Rotting Face, Wears a Mask\n", "Lost Three Toes, Limps\n", "Starved: Gaunt and Pale\n", "One Hand Replaced with Rusting Hook\n", "Decaying Teeth\n", "Hauntingly Beautiful, Unnervingly Clean\n", "Hands Caked with Sores\n", "Cataract Slowly but Surely Spreading in Both Eyes\n", "Long Tangled Hair, at least one cockroach in residence.\n", "Broken, crushed ears\n", "Juddering and stuttering from nerve damage or stress\n", "Corpulent, ravenous, drooling\n", "One hand lacks thumb and index finger, grips like a lobster\n", "Red, swollen alcoholic's nose\n", "Resting maniac face, making friends is hard\n", "Chronic athelete's fook. Stinks\n", "Recently slashed and stinking eye covered with a patch\n", "Nails cracked and black, maybe about to drop off\n")

        val charName = namesArray.get(Random.nextInt(namesArray.size))
        val charDescription = desc1Array.get(Random.nextInt(desc1Array.size)) + desc2Array.get(Random.nextInt(desc2Array.size))
        val charToughness = rollAbility()
        val hp = Math.max(Dice(diceValue = DiceValue.D8).roll(charToughness), 1)
        val charPresence = rollAbility()
        val powers = Dice(diceValue = DiceValue.D4).roll(charPresence)

        viewModelScope.launch {
            val newCharacter = Character(characterName = charName, description = charDescription, silver = Dice(2, DiceValue.D6).roll() * 10,
                currentHP = hp, maxHP = hp, omens = Dice(diceValue = DiceValue.D2).roll(), powers = powers,
                strength = rollAbility(), presence = charPresence, agility = rollAbility(), toughness = charToughness)
            Log.i("New:", newCharacter.toString())
            _newCharacterId.value = saveNewCharacter(newCharacter)
            //TODO: Inventory for character
            _autoGenerateEvent.value = true
        }
    }

    private suspend fun saveNewCharacter(character: Character): Long{
        return withContext(Dispatchers.IO) {
            database.insertCharacter(character)
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