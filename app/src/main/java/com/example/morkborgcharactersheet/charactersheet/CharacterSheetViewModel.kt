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
    val armor: LiveData<Equipment>
        get() = _armor

    private val _shield = MutableLiveData<Equipment>()
    val shield: LiveData<Equipment>
        get() = _shield

    private val _brokenType = MutableLiveData<BrokenEventType>(BrokenEventType.NOT_BROKEN)
    val brokenType: LiveData<BrokenEventType>
        get() = _brokenType

    private val _brokenRoll = MutableLiveData<Int>()
    val brokenRoll: LiveData<Int>
        get() = _brokenRoll

    private val _crit = MutableLiveData<Boolean>(false)
    val crit: LiveData<Boolean>
        get() = _crit

    private val _fumble = MutableLiveData<Boolean>(false)
    val fumble: LiveData<Boolean>
        get() = _fumble

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

    private val _defaultEvasionDR = MutableLiveData<Int>()
    val defaultEvasionDR: LiveData<Int>
        get() = _defaultEvasionDR

    private val _defenceDialogStep = MutableLiveData<Int>(1)
    val defenceDialogStep: LiveData<Int>
        get() = _defenceDialogStep

    private val _armorRoll = MutableLiveData<Int>()
    val armorRoll: LiveData<Int>
        get() = _armorRoll

    private var armorToggle = true

    // Defence Dialog Damage Roller
    val damageRollerAmount = MutableLiveData<Int>(1)
    val damageRollerValue = MutableLiveData<DiceValue>(DiceValue.D4)
    val damageRollerBonus = MutableLiveData<String>("0")
    private val _defenceDamage = MutableLiveData<Int>()
    val defenceDamage: LiveData<Int>
        get() = _defenceDamage
    val minDefenceDamage: LiveData<Int> = Transformations.map(defenceDamage) {
        Math.max(it, 0)
    }

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
        _customRolledValue.value = abilityRoll(Dice(customRollerAmount.value ?: 0, customRollerValue.value!!, customRollerBonus.value?.toIntOrNull() ?: 0, customRollerAbility.value!!))
    }

    fun onAttackClicked(attack: Equipment) {
        // Can't shoot a bow with no arrows
        if (attack.uses == 0) {
            _snackbarText.value=CharacterSheetSnackbarType.NO_USES
            return
        }

        // Decrement attack's uses, if applicable
        if(attack.uses > 0) {
            attack.uses --
            viewModelScope.launch {
                updateInventory(attack.getInventory())
            }
        }

        _attackToHit.value = abilityRoll(attack.weaponAbility!!)
        var damage = abilityRoll(attack.dice1)

        if (crit.value == true) {
            damage *= 2
        } else if (fumble.value == true) {
            // Lose used weapon on fumble
            attack.equipped = false
            viewModelScope.launch {
                updateInventory(attack.getInventory())
            }
            // Force state change in recyclerview
            var myAttacks = attacks.value!!
            _attacks.value = myAttacks.filterNot {
                it.inventoryId == attack.inventoryId
            }
        }

        _attackDamage.value = damage

        _showAttackEvent.value = true
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

    fun onDefenceDamageRoll() {
        var myArmor = armor.value
        val myShield = shield.value
        armorToggle = true

        var damageRoll = Dice(damageRollerAmount.value ?: 0, damageRollerValue.value ?: DiceValue.D2, damageRollerBonus.value?.toIntOrNull() ?: 0).roll()
        if (fumble.value == true) {
            damageRoll *= 2
            if (myArmor != null) {
                myArmor.armorTier = when (myArmor.armorTier) {
                    ArmorTier.HEAVY -> ArmorTier.MEDIUM
                    ArmorTier.MEDIUM -> ArmorTier.LIGHT
                    ArmorTier.LIGHT -> ArmorTier.NONE
                    else -> ArmorTier.NONE
                }
            }
        }

        var defenceArmorRoll = 0
        if (myArmor != null) {
            val armorDice = when (myArmor.armorTier) {
                ArmorTier.LIGHT -> DiceValue.D2
                ArmorTier.MEDIUM -> DiceValue.D4
                ArmorTier.HEAVY -> DiceValue.D6
                else -> DiceValue.D2                    // Doesn't matter since ArmorTier(None) doesn't roll
            }

            if (myArmor.armorTier != ArmorTier.NONE) {
                defenceArmorRoll = Dice(diceValue = armorDice).roll()
            }
        }

        if (myShield != null && !myShield.broken) {
            defenceArmorRoll ++
        }

        _armorRoll.value = defenceArmorRoll
        _defenceDamage.value = damageRoll - defenceArmorRoll

        _defenceDialogStep.value = 3
    }

    fun toggleArmor() {
        armorToggle = !armorToggle

        if (armorToggle) {
            _defenceDamage.value = _defenceDamage.value?.minus(armorRoll.value ?: 0)
        } else {
            _defenceDamage.value = _defenceDamage.value?.plus(armorRoll.value ?: 0)
        }
    }

    fun onDefenceComplete(hit: Boolean, shielded: Boolean) {
        if(hit) {
            takeDamage(minDefenceDamage.value?:0)
        } else if (shielded) {
            viewModelScope.launch {
                shield.value?.let { breakShield(it.inventoryId) }
            }
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

    private suspend fun breakShield(inventoryId: Long) {
        withContext(Dispatchers.IO) {
            database.breakShield(inventoryId)
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
    // Roll a simple 1D20 + bonus that can crit
    private fun critRoll(bonus: Int = 0): Int {
        var roll = Random.nextInt(1, 21)

        _crit.value = roll == 20
        _fumble.value = roll == 1

        return roll + bonus
    }

    // Roll 1D20 + ability score. Can crit.
    private fun abilityRoll(ability: AbilityType): Int {
        val abilityScore = when (ability) {
            STRENGTH -> character.value!!.strength
            AGILITY -> character.value!!.agility
            PRESENCE -> character.value!!.presence
            TOUGHNESS -> character.value!!.toughness
            else -> 0
        }
        return critRoll(abilityScore)
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
        Log.i("CurrentHP", character.value?.currentHP.toString())
        var currentHp = character.value!!.currentHP - damage

        if (currentHp == 0) {
            var restoredHP: Int = 0

            // TODO: Extract hardcoded strings
            //  Add strings to string resource file and let FE know which version of dying to use strings for
            when (Random.nextInt(4)) {
                0 -> {
                    // Fall unconscious
                    _brokenType.value = BrokenEventType.UNCONSCIOUS
                    _brokenRoll.value = Dice(diceValue = DiceValue.D4).roll()
                    restoredHP = Dice(diceValue = DiceValue.D4).roll()
                }
                1 -> {
                    // Lost a limb or (1:6 chance) eye
                    val eyeLost = Random.nextInt(6) == 0
                    _brokenType.value = if (eyeLost) BrokenEventType.LOSE_EYE else BrokenEventType.LOSE_LIMB
                    _brokenRoll.value = Dice(diceValue = DiceValue.D4).roll()
                    restoredHP = Dice(diceValue = DiceValue.D4).roll()
                }
                2 -> {
                    // Bleeding out
                    _brokenType.value = BrokenEventType.BLEEDING
                    _brokenRoll.value = Dice(diceValue = DiceValue.D4).roll()
                }
                3 -> {
                    // Death
                    _brokenType.value = BrokenEventType.DEAD
                }
            }
            _character.value!!.currentHP = min(restoredHP, character.value!!.maxHP)

        } else if (currentHp < 0) {
            _brokenType.value = BrokenEventType.DEAD
        }

        _character.value!!.currentHP = currentHp
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

    enum class BrokenEventType {
        NOT_BROKEN,
        UNCONSCIOUS,
        LOSE_EYE,
        LOSE_LIMB,
        BLEEDING,
        DEAD;
    }
}