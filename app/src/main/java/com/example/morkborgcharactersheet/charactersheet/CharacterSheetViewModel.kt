package com.example.morkborgcharactersheet.charactersheet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import com.example.morkborgcharactersheet.database.*
import com.example.morkborgcharactersheet.models.ItemType.*
import com.example.morkborgcharactersheet.models.AbilityType
import com.example.morkborgcharactersheet.models.AbilityType.*
import com.example.morkborgcharactersheet.util.DatabaseConverter
import kotlinx.coroutines.*
import java.util.Date
import kotlin.random.Random
import kotlin.math.min

/**
 * Public constants for inventory types and ability types.
 */

class CharacterSheetViewModel(private val characterId: Long, dataSource: CharacterDatabaseDAO) : ViewModel() {

    val database = dataSource

    /**
     * Live Data and MutableLiveData
     */
    private val _character = MutableLiveData<Character?>()

    val character: LiveData<Character?>
        get() = _character

    // TODO: Initialize these properly
    // TODO: Armor Tiers
    val attacks = database.getEquippedWeapons(characterId)
    val powers = database.getEquippedPowers(characterId)
    val armor = database.getEquippedArmor(characterId)
    val shield = database.getEquippedShield(characterId)


    // Dice Rolls
    private val _rolledValue = MutableLiveData<Int>()
    val rolledValue: LiveData<Int>
        get() = _rolledValue

    private val _rolledValue2 = MutableLiveData<Int>()
    val rolledValue2: LiveData<Int>
        get() = _rolledValue2

    private val _rolledValue3 = MutableLiveData<Int>()
    val rolledValue3: LiveData<Int>
        get() = _rolledValue3
    // End Dice Rolls

    // Lets FE know the relevant inventory item, and used in VM functions when dialogs complete
    // TODO: LiveData these
    private var _recentInventory: Inventory? = null
    val recentInventory: Inventory?
        get() = _recentInventory

    // Power descriptions may vary, and sometimes have dice results embedded directly in their text.
    private var _powerDescriptionText: String? = null
    val powerDescriptionText: String?
        get() = _powerDescriptionText

    /**
     * Transformed LiveData For display
     */
    val currentHPString = Transformations.map(character) {
        it?.currentHP.toString()
    }

    val maxHPString = Transformations.map(character) {
        it?.maxHP.toString()
    }

    // TODO: Move label to FE and use string resource
    val powersString = Transformations.map(character) {
        it?.powers.toString()
    }

    val strengthButtonText = Transformations.map(character) {
        "Strength " + it?.strength.toString()
    }

    val agilityButtonText = Transformations.map(character) {
        "Agility " + it?.agility.toString()
    }

    val presenceButtonText = Transformations.map(character) {
        "Presence " + it?.presence.toString()
    }

    val toughnessButtonText = Transformations.map(character) {
        "Toughness " + it?.toughness.toString()
    }

    /**
     * Events
     */
    private val _showRollResultEvent = MutableLiveData<Boolean>()
    val showRollResultEvent: LiveData<Boolean>
        get() = _showRollResultEvent
    fun onShowRollResultEventDone() {
        _showRollResultEvent.value = false
    }

    private val _showAttackEvent = MutableLiveData<Boolean>()
    val showAttackEvent: LiveData<Boolean>
        get() = _showAttackEvent
    fun onShowAttackEventDone() {
        _showAttackEvent.value = false
    }

    private val _showPowerEvent = MutableLiveData<Boolean>()
    val showPowerEvent: LiveData<Boolean>
        get() = _showPowerEvent
    fun onShowPowerEventDone() {
        _showPowerEvent.value = false
    }

    private val _showDefenceEvent = MutableLiveData<Boolean>()
    val showDefenceEvent: LiveData<Boolean>
        get() = _showDefenceEvent
    fun onShowDefenceEventDone() {
        _showDefenceEvent.value = false
    }

    private val _editCharacterEvent = MutableLiveData<Boolean>()
    val editCharacterEvent: LiveData<Boolean>
        get() = _editCharacterEvent
    fun oneditCharacterEventDone() {
        _editCharacterEvent.value = false
    }

    /**
     * FE functions
     */
    fun onAbilityButtonPressed(ability: Int) {
        // TODO: Inform user AGL DR +2/+4 if tier 2/3 armor equipped
        // TODO: Allow user to input all mods for rolls
        //          (Use custom roller view)
        val abilityType = AbilityType.get(ability)!!
        _rolledValue.value = abilityRoll(abilityType)
        _showRollResultEvent.value = true
    }

    fun onAttackClicked(attack: Inventory) {
        viewModelScope.launch {
            // Can't shoot a bow with no arrows
            if (attack.uses == 0) {
                // TODO: Pop toast
                return@launch
            }

            _rolledValue.value = abilityRoll(AbilityType.get(attack.ability)!!)
            _rolledValue2.value = abilityRoll(AbilityType.get(attack.dice1Ability)!!, attack.dice1Amount, attack.dice1Value, attack.dice1Bonus)

            _recentInventory = attack

            _showAttackEvent.value = true

            // Decrement attack's uses, if applicable
            if(attack.uses > 0) {
                attack.uses --
                updateInventory(attack)
            }
        }
    }

    fun onPowerClicked(power: Inventory) {
        // Ensure this character has enough uses of powers left for the day
        if (character.value!!.powers <= 0) {
            // TODO: Pop a toast for user feedback?
            return
        }
        // TODO: Powers can't be used in tier 2/3 armor or shields. Pop toast for user.

        // Presence test to see if power fires off or fails.
        _rolledValue.value = abilityRoll(PRESENCE)
        // Other rolled values are determined on a power-by-power basis
        _rolledValue2.value = if (power.dice1Value != 0) abilityRoll(AbilityType.get(power.dice1Ability)!!, power.dice1Amount, power.dice1Value, power.dice1Bonus) else 0
        _rolledValue3.value = if (power.dice2Value != 0) abilityRoll(AbilityType.get(power.dice2Ability)!!, power.dice2Amount, power.dice2Value, power.dice2Bonus) else 0

        // TODO: inject rolled values into powerDescriptionText
        _powerDescriptionText = power.description

        _recentInventory = power

        _showPowerEvent.value = true
    }

    fun onPowerComplete(feedback: Boolean) {
        if (feedback) {
            val feedbackDamage = roll(diceValue = 2)
            takeDamage(feedbackDamage)
        }
        _character.value!!.powers --
        // Notify LiveData of changes to character
        _character.value = _character.value
    }

    fun onDefenceClicked() {
        // TODO: Armor Tiers

        // rolledValue 1 is agility test to avoid damage
        _rolledValue.value = abilityRoll(AGILITY)
        // rolledValue2 is damage negated by armor/shield
        _rolledValue2.value = if (armor.value != null) roll(diceValue = armor.value!!.dice1Value) else 0
        if (shield.value != null) {
            _rolledValue2.value = if (rolledValue2.value != null) _rolledValue2.value!! + 1 else 1
        }
        _rolledValue2.value = -1 * _rolledValue2.value!!

        _showDefenceEvent.value = true
    }

    fun onDefenceComplete(damage: Int, shielded: Boolean) {
        if (!shielded) {
            takeDamage(damage)
        } else {
            // TODO: Destroy shield
        }
    }

    fun onCharacterEdit() {
        _editCharacterEvent.value = true
    }

    /**
     * suspend functions for async database access
     */
    private suspend fun getCharacter(characterId: Long) : Character? {
        return withContext(Dispatchers.IO) {
            var myCharacter = database.getCharacter(characterId)
            myCharacter
        }
    }

    private suspend fun saveCharacter() {
        withContext(Dispatchers.IO) {
            database.updateCharacter(character.value!!)
        }
    }

    private suspend fun updateInventory(newInventory: Inventory) {
        return withContext(Dispatchers.IO) {
            database.updateInventory(newInventory)
        }
    }

    fun loadCharacter() {
        viewModelScope.launch {
            var myCharacter = getCharacter(characterId)
            _character.value = myCharacter
        }
    }

    init {
        loadCharacter()
    }


    /**
     * Utility Functions
     */
    // Used for general dice rolls
    private fun roll(amount: Int = 1, diceValue: Int, bonus: Int = 0): Int {
        var total = 0
        for(i in 1 .. amount) {
            total += Random.nextInt(1, diceValue + 1)
        }
        total += bonus

        return total
    }

    // Used for dice rolls modified by ability scores
    private fun abilityRoll(ability: AbilityType, diceAmount: Int = 1, diceValue: Int = 20, diceBonus: Int = 0): Int {
        val abilityScore = when (ability) {
            STRENGTH -> character.value!!.strength
            AGILITY -> character.value!!.agility
            PRESENCE -> character.value!!.presence
            TOUGHNESS -> character.value!!.toughness
            else -> 0
        }
        return roll(diceAmount, diceValue, diceBonus + abilityScore)
    }

    private fun takeDamage(damage: Int) {
        _character.value!!.currentHP -= damage

        if (_character.value!!.currentHP == 0) {
            val brokenOutcome = Random.nextInt(4)
            var brokenText: String = ""
            var restoredHP: Int = 0

            // TODO: Extract hardcoded strings
            when (brokenOutcome) {
                0 -> {
                    // Fall unconscious
                    restoredHP = roll(diceValue = 4)
                    brokenText = "Fall unconscious for " + roll(diceValue = 4) + " rounds."
                }
                1 -> {
                    // Lost a limb or (1:6 chance) eye
                    val eyeLost = Random.nextInt(6) == 0
                    restoredHP = roll(diceValue = 4)
                    brokenText = (if (eyeLost) "Lost an eye. " else "Broken or severed limb. ") + "Can\'t act for " + roll(diceValue =  4) + " rounds."
                }
                2 -> {
                    // Bleeding out
                    brokenText = "Bleeding out. Death in " + roll(diceValue = 2) + " hours. All tests are DR 16 in the first hour and DR 18 in the final hour."
                }
                3 -> {
                    // TODO: Death
                }
            }
            _character.value!!.currentHP = min(restoredHP, character.value!!.maxHP)
            // TODO: Convey brokenText to player
        } else if (_character.value!!.currentHP < 0) {
            // TODO: Death
        }
        // Notify LiveData of changes to character
        _character.value = _character.value

        viewModelScope.launch {
            saveCharacter()
        }
    }
}