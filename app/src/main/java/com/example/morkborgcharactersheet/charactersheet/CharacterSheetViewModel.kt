package com.example.morkborgcharactersheet.charactersheet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import com.example.morkborgcharactersheet.database.*
import com.example.morkborgcharactersheet.models.*
import com.example.morkborgcharactersheet.models.ItemType.*
import com.example.morkborgcharactersheet.models.AbilityType.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
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

    private val _attacks = MutableLiveData<List<Equipment>>()
    val attacks: LiveData<List<Equipment>>
        get() = _attacks

    private val _powers = MutableLiveData<List<Equipment>>()
    val powers: LiveData<List<Equipment>>
        get() = _powers

    private val _armor = MutableLiveData<Equipment>()

    private val _shield = MutableLiveData<Equipment>()

    val armor = database.getEquippedArmor(characterId)
    val shield = database.getEquippedShield(characterId)

    // Custom ability rolls
    val customRollerAmount = MutableLiveData<Int>(1)
    val customRollerValue = MutableLiveData<DiceValue>(DiceValue.D20)
    val customRollerBonus = MutableLiveData<String>("0")
    val customRollerAbility = MutableLiveData<AbilityType>(UNTYPED)
    private val _customRolledValue = MutableLiveData<Int>()
    val customRolledValue: LiveData<Int>
        get() = _customRolledValue

    // Attack Dialog
    private val _attackToHit = MutableLiveData<Int>()
    val attackToHit: LiveData<Int>
        get() = _attackToHit

    private val _attackDamage = MutableLiveData<Int>()
    val attackDamage: LiveData<Int>
        get() = _attackDamage

    // Power Dialog
    private val _powerName = MutableLiveData<String>()
    val powerName: LiveData<String>
        get() = _powerName

    private val _powerUseRoll = MutableLiveData<Int>()
    val powerUseRoll: LiveData<Int>
        get() = _powerUseRoll

    private val _powerDescription = MutableLiveData<String>()
    val powerDescription: LiveData<String>
        get() = _powerDescription

    // Defense Dialog
    private val _evasionRoll = MutableLiveData<Int>()
    val evasionRoll: LiveData<Int>
        get() = _evasionRoll

    // Defence Dialog Damage Roller
    val damageRollerAmount = MutableLiveData<Int>(1)
    val damageRollerValue = MutableLiveData<DiceValue>(DiceValue.D4)
    val damageRollerBonus = MutableLiveData<String>("0")
    private val _damageRoll = MutableLiveData<Int>()
    val damageRoll: LiveData<Int>
        get() = _damageRoll

    private val _armorRoll = MutableLiveData<Int>()
    val armorRoll: LiveData<Int>
        get() = _armorRoll

    private val _defaultEvasionDR = MutableLiveData<Int>()
    val defaultEvasionDR: LiveData<Int>
        get() = _defaultEvasionDR

    private val _defenceDialogStep = MutableLiveData<Int>(1)
    val defenceDialogStep: LiveData<Int>
        get() = _defenceDialogStep

    /**
     * Events
     */

    // Pairs with onAttackClicked()
    private val _showAttackEvent = MutableLiveData<Boolean>()
    val showAttackEvent: LiveData<Boolean>
        get() = _showAttackEvent
    fun onShowAttackEventDone() {
        _showAttackEvent.value = false
    }

    // Pairs with onPowerClicked() and onPowerComplete()
    private val _showPowerEvent = MutableLiveData<Boolean>()
    val showPowerEvent: LiveData<Boolean>
        get() = _showPowerEvent

    private val _showDefenceEvent = MutableLiveData<Boolean>()
    val showDefenceEvent: LiveData<Boolean>
        get() = _showDefenceEvent
    fun onShowDefenceEventDone() {
        _showDefenceEvent.value = false
    }

    private val _editCharacterEvent = MutableLiveData<Boolean>()
    val editCharacterEvent: LiveData<Boolean>
        get() = _editCharacterEvent
    fun onCharacterEdit() {
        _editCharacterEvent.value = true
    }
    fun oneditCharacterEventDone() {
        _editCharacterEvent.value = false
    }

    // Should probably have done this as an enum
    private val _snackbarText = MutableLiveData<CharacterSheetSnackbarType?>()
    val snackbarText: LiveData<CharacterSheetSnackbarType?>
        get() = _snackbarText
    fun onSnackbarDone() {
        _snackbarText.value = null
    }

    /**
     * FE functions
     */
    // Can't get spinners two-way bound for some reason
    fun setCustomRollerDiceValue(diceValue: DiceValue) {
        customRollerValue.value = diceValue
    }

    fun setCustomRollerAbility(abilityType: AbilityType) {
        customRollerAbility.value = abilityType
    }

    fun onCustomRoll() {
        _customRolledValue.value = abilityRoll(Dice(customRollerAmount.value ?: 0, customRollerValue.value!!, customRollerBonus.value?.toInt() ?: 0, customRollerAbility.value!!))
    }

    fun onAttackClicked(attack: Equipment) {
        // Can't shoot a bow with no arrows
        if (attack.uses == 0) {
            _snackbarText.value=CharacterSheetSnackbarType.NO_USES
            return
        }

        _attackToHit.value = abilityRoll(attack.weaponAbility!!)
        _attackDamage.value = abilityRoll(attack.dice1)

        _showAttackEvent.value = true

        // Decrement attack's uses, if applicable
        if(attack.uses > 0) {
            attack.uses --
            viewModelScope.launch {
                updateInventory(attack.getInventory())
            }
        }
    }

    fun onPowerClicked(power: Equipment) {
        // Ensure this character has enough uses of powers left for the day
        if (character.value!!.powers <= 0) {
            _snackbarText.value = CharacterSheetSnackbarType.NO_POWERS
            return
        } else if (_armor.value?.armorTier?.ordinal ?: 0 > 1) {
            _snackbarText.value = CharacterSheetSnackbarType.WEARING_ARMOR
            return
        }

        // Presence test to see if power fires off or fails.
        _powerUseRoll.value = abilityRoll(PRESENCE)

        // Other rolled values are determined on a power-by-power basis
        val roll1 = abilityRoll(power.dice1)
        val roll2 = abilityRoll(power.dice2)

        _powerDescription.value = power.description.replace("\$D1", roll1.toString()).replace("\$D2", roll2.toString())

        _showPowerEvent.value = true
    }

    fun onPowerComplete() {
        if (powerUseRoll.value!! < 12) {
            val feedbackDamage = Dice(diceValue = DiceValue.D2).roll()
            takeDamage(feedbackDamage)
        }
        _character.value!!.powers --
        // Notify LiveData of changes to character
        _character.value = _character.value

        _showPowerEvent.value = false
    }

    fun onDefenceClicked() {
        _defenceDialogStep.value = 1
        _defaultEvasionDR.value = if (_armor.value?.armorTier?.ordinal ?: 0 >= 2) 14 else 12
        _evasionRoll.value = abilityRoll(AGILITY)

        _showDefenceEvent.value = true
    }

    fun onDefenceHit() {
        _defenceDialogStep.value = 2
    }

    fun onDefenceComplete(hit: Boolean) {
        if(hit) {
            // TODO
        }
        _showDefenceEvent.value = false
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

    private suspend fun getEquipment(): List<Inventory> {
        return withContext(Dispatchers.IO) {
            database.getEquipment(characterId)
        }
    }

    fun loadCharacter() {
        viewModelScope.launch {
            val myCharacter: Character = getCharacter(characterId)
                    ?: throw IllegalArgumentException("Invalid characterId")
            _character.value = myCharacter

            val myInventory = getEquipment()
            val myEquipment = myInventory.map { inventory ->
                Equipment(inventory, null)
            }

            _attacks.value = myEquipment.filter { equipment ->
                equipment.equipped && equipment.type == WEAPON
            }

            _powers.value = myEquipment.filter { equipment ->
                equipment.equipped && equipment.type == POWER
            }

            _armor.value = myEquipment.find { equipment ->
                equipment.equipped && equipment.type == ARMOR
            }

            _shield.value = myEquipment.find { equipment ->
                equipment.equipped && equipment.type == SHIELD
            }
        }
    }

    init {
        loadCharacter()
    }


    /**
     * Utility Functions
     */
    // Used for general dice rolls
    // TODO: Deprecated: Replace with Dice's roll() function
    private fun roll(amount: Int = 1, diceValue: Int, bonus: Int = 0): Int {
        var total = 0
        for(i in 1 .. amount) {
            total += Random.nextInt(1, diceValue + 1)
        }
        total += bonus

        return total
    }

    // Used for dice rolls modified by ability scores
    // TODO: Deprecated: Replace with Dice's roll() function
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

    private fun abilityRoll(dice: Dice): Int {
        val abilityScore = when (dice.ability) {
            STRENGTH -> character.value!!.strength
            AGILITY -> character.value!!.agility
            PRESENCE -> character.value!!.presence
            TOUGHNESS -> character.value!!.toughness
            else -> 0
        }
        return dice.roll(abilityScore)
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

    enum class CharacterSheetSnackbarType {
        NO_USES,
        NO_POWERS,
        WEARING_ARMOR;
    }
}