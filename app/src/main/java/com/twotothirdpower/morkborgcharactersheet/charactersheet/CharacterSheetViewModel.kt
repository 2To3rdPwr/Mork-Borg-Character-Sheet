package com.twotothirdpower.morkborgcharactersheet.charactersheet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import com.twotothirdpower.morkborgcharactersheet.database.*
import com.twotothirdpower.morkborgcharactersheet.models.*
import com.twotothirdpower.morkborgcharactersheet.models.ItemType.*
import com.twotothirdpower.morkborgcharactersheet.models.AbilityType.*
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.util.*
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

    private val _attackDescription = MutableLiveData<String>("")
    val attackDescription: LiveData<String>
        get() = _attackDescription

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

    private val _defenceArmorDescription = MutableLiveData<String>("")
    val defenceArmorDescription: LiveData<String>
        get() = _defenceArmorDescription

    private val _defenceShieldDescription = MutableLiveData<String>("")
    val defenceShieldDescription: LiveData<String>
        get() = _defenceShieldDescription

    private var armorToggle = true

    // Defence Dialog Damage Roller
    val damageRollerAmount = MutableLiveData<Int>(1)
    private val _damageRollerValue = MutableLiveData<DiceValue>(DiceValue.D4)
    val damageRollerValue: LiveData<DiceValue>
        get() = _damageRollerValue
    val damageRollerBonus = MutableLiveData<String>("0")
    private val _defenceDamage = MutableLiveData<Int>()
    val defenceDamage: LiveData<Int>
        get() = _defenceDamage
    val minDefenceDamage: LiveData<Int> = Transformations.map(defenceDamage) {
        Math.max(it, 0)
    }

    // Broken dialog
    private val _brokenType = MutableLiveData<BrokenEventType>(BrokenEventType.NOT_BROKEN)
    val brokenType: LiveData<BrokenEventType>
        get() = _brokenType
    fun onBrokenDialogDone() {
        _brokenType.value = BrokenEventType.NOT_BROKEN
    }

    private val _brokenRoll = MutableLiveData<Int>()
    val brokenRoll: LiveData<Int>
        get() = _brokenRoll

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

    private val _editCharacterEvent = MutableLiveData<Boolean>()
    val editCharacterEvent: LiveData<Boolean>
        get() = _editCharacterEvent
    fun onCharacterEdit() {
        _editCharacterEvent.value = true
    }
    fun onEditCharacterEventDone() {
        _editCharacterEvent.value = false
    }

    private val _snackbarText = MutableLiveData<CharacterSheetSnackbarType?>()
    val snackbarText: LiveData<CharacterSheetSnackbarType?>
        get() = _snackbarText
    fun onSnackbarDone() {
        _snackbarText.value = null
    }

    private val _showDescriptionEvent = MutableLiveData<Boolean>(false)
    val showDescriptionEvent: LiveData<Boolean>
        get() = _showDescriptionEvent
    fun onShowDescription() {
        _showDescriptionEvent.value = true
    }
    fun onShowDescriptionDone() {
        _showDescriptionEvent.value = false
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
        if (attack.uses == 0 && attack.limitedUses) {
            _snackbarText.value = CharacterSheetSnackbarType.NO_USES
            return
        }

        // Decrement attack's uses, if applicable
        if(attack.uses > 0 && attack.limitedUses) {
            attack.uses --
            viewModelScope.launch {
                updateEquipment(attack)
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
                updateEquipment(attack)
            }
            // Force state change in recyclerview
            val myAttacks = attacks.value!!
            _attacks.value = myAttacks.filterNot {
                it.joinId == attack.joinId
            }
        }

        _attackDescription.value = attack.rolledDescription(character.value ?: throw IllegalStateException("No character"))
        _attackDamage.value = damage
        _showAttackEvent.value = true
    }

    fun onPowerClicked(power: Equipment, fumbleString: String, fumbles: Array<String>, cubeEscape: Array<String>) {
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

        if (fumble.value == true) {
            val fumbleIndex = Random.nextInt(20)

            var fumbleDescription = fumbles[fumbleIndex]
            if (fumbleIndex == 18) {
                // Cube-Violet
                fumbleDescription += " " + cubeEscape[Random.nextInt(4)]
            } else if (fumbleIndex == 19) {
                // HE arrives
                _character.value!!.currentHP = -1
            }

            _powerName.value = fumbleString
            _powerDescription.value = fumbleDescription
        } else {
            _powerName.value = power.name
            _powerDescription.value = power.rolledDescription(
                character.value ?: throw IllegalStateException("No character")
            )
        }
        _showPowerEvent.value = true
    }

    fun onPowerComplete() {
        if (powerUseRoll.value!! < 12) {
            val feedbackDamage = Dice(diceValue = DiceValue.D2).roll()
            takeDamage(feedbackDamage)
        }
        _character.value!!.powers --

        _character.value = _character.value
        _showPowerEvent.value = false
    }

    fun onDefenceClicked() {
        _defenceDialogStep.value = 1
        _defaultEvasionDR.value = if (_armor.value?.armorTier?.ordinal ?: 0 >= 2) 14 else 12
        _evasionRoll.value = abilityRoll(AGILITY)

        val myArmor = armor.value
        val myShield = shield.value
        val myCharacter = character.value ?: throw IllegalStateException("No character")

        if (myArmor != null) {
            _defenceArmorDescription.value = myArmor.rolledDescription(myCharacter)
        } else {
            _defenceArmorDescription.value = ""
        }
        if (myShield != null) {
            _defenceShieldDescription.value = myShield.rolledDescription(myCharacter)
        } else {
            _defenceShieldDescription.value = ""
        }

        _showDefenceEvent.value = true
    }

    fun onDefenceHit() {
        _defenceDialogStep.value = 2
    }

    fun setDefenceDamageDiceValue(diceValue: DiceValue) {
        _damageRollerValue.value = diceValue
    }

    fun onDefenceDamageRoll() {
        val myArmor = armor.value
        val myShield = shield.value
        armorToggle = true

        var damageRoll = Dice(damageRollerAmount.value ?: 0, damageRollerValue.value ?: DiceValue.D0, damageRollerBonus.value?.toIntOrNull() ?: 0).roll()
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
                else -> DiceValue.D0                   // Doesn't matter since ArmorTier(None) doesn't roll
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

    fun onPause() {
        viewModelScope.launch {
            saveCharacter()
        }
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
        _character.value?.lastUsed = Date()
        withContext(Dispatchers.IO) {
            database.updateCharacter(character.value!!)
        }
    }

    private suspend fun updateEquipment(equipment: Equipment) {
        return withContext(Dispatchers.IO) {
            database.updateInventoryJoin(equipment.getInvJoin())
        }
    }

    private suspend fun breakShield(inventoryId: Long) {
        withContext(Dispatchers.IO) {
            database.breakEquipment(characterId, inventoryId)
        }
    }

    private suspend fun getEquipment(): List<Equipment> {
        return withContext(Dispatchers.IO) {
            val myData = database.getCharactersEquipment(characterId)
            myData.map { equipmentData -> Equipment(equipmentData) }
        }
    }

    /**
     * initialization
     */
    fun loadCharacter() {
        viewModelScope.launch {
            val myCharacter: Character = getCharacter(characterId)
                    ?: throw IllegalArgumentException("Invalid characterId")
            _character.value = myCharacter
            // Immediately save loaded character to timestamp it
            saveCharacter()

            val myEquipment = getEquipment()

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

            // If this character is brand new (lastUsed set to null) or hasn't been used in over a week, pop the character description
            if (myCharacter.lastUsed == null || (Date().time - myCharacter.lastUsed!!.time) / (1000 * 60 * 60 * 24) > 7) {
                _showDescriptionEvent.value = true
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
        var currentHP = character.value!!.currentHP - damage

        if (currentHP == 0) {
            var restoredHP = 0

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
            currentHP = min(restoredHP, character.value!!.maxHP)

        } else if (currentHP < 0) {
            _brokenType.value = BrokenEventType.DEAD
        }

        _character.value!!.currentHP = currentHP
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