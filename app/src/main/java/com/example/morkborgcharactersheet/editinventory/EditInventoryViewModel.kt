package com.example.morkborgcharactersheet.editinventory

import android.util.Log
import androidx.lifecycle.*
import com.example.morkborgcharactersheet.database.*
import com.example.morkborgcharactersheet.models.*
import com.example.morkborgcharactersheet.util.PropertyAwareMutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.IllegalArgumentException

// TODO: Parameter is CharacterInventoryJoinId
class EditInventoryViewModel (private var inventoryId: Long, private val characterId: Long, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    private var newItem: Boolean = inventoryId == -1L

    /**
     * LiveData
     */
    private val _equipment = PropertyAwareMutableLiveData<Equipment>()
    val equipment: LiveData<Equipment>
        get() = _equipment

    val rolledMaxUses = MutableLiveData<Boolean>(false)
    val staticUses = MutableLiveData<Int>(0)

    // Transformed liveData for observers
    val equipmentType: LiveData<ItemType> = Transformations.map(equipment) {
        it.type
    }
    val limitedUses: LiveData<Boolean> = Transformations.map(equipment) {
        it.limitedUses
    }
    val refillable: LiveData<Boolean> = Transformations.map(equipment) {
        it.refillable
    }

    // TODO: Might be able to replace all these with PropertyAwareMutableLiveData<Dice>
    val damageRollerAmount = MutableLiveData<Int>()
    val damageRollerValue = MutableLiveData<DiceValue>()
    val damageRollerBonus = MutableLiveData<String>()
    val damageRollerAbility = MutableLiveData<AbilityType>()

    val usesRollerAmount = MutableLiveData<Int>()
    val usesRollerValue = MutableLiveData<DiceValue>()
    val usesRollerBonus = MutableLiveData<String>()
    val usesRollerAbility = MutableLiveData<AbilityType>()

    val description1RollerAmount = MutableLiveData<Int>()
    val description1RollerValue = MutableLiveData<DiceValue>()
    val description1RollerBonus = MutableLiveData<String>()
    val description1RollerAbility = MutableLiveData<AbilityType>()

    val description2RollerAmount = MutableLiveData<Int>()
    val description2RollerValue = MutableLiveData<DiceValue>()
    val description2RollerBonus = MutableLiveData<String>()
    val description2RollerAbility = MutableLiveData<AbilityType>()

    val dice1InDescriptionVisibility: LiveData<Boolean> = Transformations.map(equipment) {
        it.description.contains("\$D1")
    }

    // TODO: MediatorLiveData to enforce ItemType != WEAPON for description2Dice
    val dice2InDescriptionVisibility: LiveData<Boolean> = Transformations.map(equipment) {
        it.description.contains("\$D2")
    }

    /**
     * Events
     */
    private val _saveItemEvent = MutableLiveData<Boolean>()
    val saveItemEvent: LiveData<Boolean>
        get() = _saveItemEvent
    fun onItemSaveComplete() {
        _saveItemEvent.value = false
    }

    private val _showToastEvent = MutableLiveData<Boolean>()
    val showToastEvent: LiveData<Boolean>
        get() = _showToastEvent
    fun onShowToastEventComplete() {
        _showToastEvent.value = false
    }

    /**
     * FE methods
     */
    // Can't get spinners two-way bound for some reason
    fun setDamageRollerDiceValue(diceValue: DiceValue) {
        damageRollerValue.value = diceValue
    }

    fun setDamageRollerAbility(abilityType: AbilityType) {
        damageRollerAbility.value = abilityType
    }

    fun setUsesRollerDiceValue(diceValue: DiceValue) {
        usesRollerValue.value = diceValue
    }

    fun setUsesRollerAbility(abilityType: AbilityType) {
        usesRollerAbility.value = abilityType
    }

    fun setDescription1RollerDiceValue(diceValue: DiceValue) {
        description1RollerValue.value = diceValue
    }

    fun setDescription1RollerAbility(abilityType: AbilityType) {
        description1RollerAbility.value = abilityType
    }

    fun setDescription2RollerDiceValue(diceValue: DiceValue) {
        description2RollerValue.value = diceValue
    }

    fun setDescription2RollerAbility(abilityType: AbilityType) {
        description2RollerAbility.value = abilityType
    }


    fun onItemSaved() {
        var myEquipment = equipment.value
        if (myEquipment == null) {
            throw IllegalArgumentException("Null equipment")
        }

        if (myEquipment.name == "") {
            _showToastEvent.value = true
        } else {
            //  Setting values by item type
            //  EX: Don't set ArmorTier for weapons

            if (myEquipment.type == ItemType.WEAPON) {
                myEquipment.dice1 = Dice(damageRollerAmount.value?:0, damageRollerValue.value?:DiceValue.D0, damageRollerBonus.value?.toIntOrNull()?:0, damageRollerAbility.value?:AbilityType.UNTYPED)
                myEquipment.dice2 = Dice(description1RollerAmount.value?:0, description1RollerValue.value?:DiceValue.D0, description1RollerBonus.value?.toIntOrNull()?:0, description1RollerAbility.value?:AbilityType.UNTYPED)
            } else {
                myEquipment.dice1 = Dice(description1RollerAmount.value?:0, description1RollerValue.value?:DiceValue.D0, description1RollerBonus.value?.toIntOrNull()?:0, description1RollerAbility.value?:AbilityType.UNTYPED)
                myEquipment.dice2 = Dice(description2RollerAmount.value?:0, description2RollerValue.value?:DiceValue.D0, description2RollerBonus.value?.toIntOrNull()?:0, description2RollerAbility.value?:AbilityType.UNTYPED)
            }

            if ((myEquipment.type == ItemType.WEAPON || myEquipment.type == ItemType.OTHER) && myEquipment.limitedUses) {
                if (rolledMaxUses.value == true) {
                    myEquipment.refillDice = Dice(usesRollerAmount.value?:1, usesRollerValue.value?:DiceValue.D0, usesRollerBonus.value?.toIntOrNull()?:0, usesRollerAbility.value?:AbilityType.UNTYPED)
                } else {
                    myEquipment.refillDice = Dice(0, DiceValue.D0, staticUses.value?:0, AbilityType.UNTYPED)
                }
            } else {
                myEquipment.refillDice = Dice(0, DiceValue.D0, 0, AbilityType.UNTYPED)
            }

            viewModelScope.launch {
                if (myEquipment.limitedUses) {
                    if (rolledMaxUses.value == true) {
                        val abilityScore = if (myEquipment.refillDice.ability != AbilityType.UNTYPED) getAbilityScoreForCharacter(characterId, myEquipment.refillDice.ability) else 0
                        myEquipment.uses = myEquipment.refillDice.roll(abilityScore)
                    } else {
                        myEquipment.uses = staticUses.value?:0
                    }
                }

                Log.i("Equipment", myEquipment.type.toString())
                if (newItem) {
                    inventoryId = newInventory(myEquipment.getInventory())
                    myEquipment.initialize(characterId, inventoryId)
                    newInventoryJoin(myEquipment.getInvJoin())
                } else {
                    updateInventory(myEquipment.getInventory())
                    updateInventoryJoin(myEquipment.getInvJoin())
                }

                _saveItemEvent.value = true
            }
        }
    }

    /**
     * Suspend functions for database access
     */
    private suspend fun newInventory(inventory: Inventory): Long {
        return withContext(Dispatchers.IO) {
            database.insertInventory(inventory)
        }
    }

    private suspend fun newInventoryJoin(inventoryJoin: CharacterInventoryJoin) {
        withContext(Dispatchers.IO) {
            database.insertInventoryJoin(inventoryJoin)
        }
    }

    private suspend fun updateInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            database.updateInventory(inventory)
        }
    }

    private suspend fun updateInventoryJoin(characterInventoryJoin: CharacterInventoryJoin) {
        withContext(Dispatchers.IO) {
            database.updateInventoryJoin(characterInventoryJoin)
        }
    }

    private suspend fun getAbilityScoreForCharacter(characterId: Long, abilityType: AbilityType): Int {
        return withContext(Dispatchers.IO){
            val abilityMod = database.getAbilityScoreForCharacter(characterId, abilityType.id)
            abilityMod
        }
    }

    private suspend fun getEquipment(inventoryId: Long): Equipment {
        return withContext(Dispatchers.IO){
            val myData = database.getEquipment(characterId, inventoryId) ?: throw IllegalArgumentException("Invalid equipment")
            Equipment(myData)
        }
    }

    init {
        // Setting initial values
        viewModelScope.launch {
            val myEquipment = if (newItem) {
                val newInventory = Inventory(name = "", description = "", type = 1, ability = 1, dice1Amount = 1, dice1Value = 6, dice2Amount = 1, dice2Value = 2, refillable = true)
                val newJoin = CharacterInventoryJoin(inventoryId = 0L, characterId = characterId)

                Equipment(newInventory, newJoin)
            } else {
                getEquipment(inventoryId)
            }

            rolledMaxUses.value = !(myEquipment.refillDice.amount == 0 || myEquipment.refillDice.diceValue == DiceValue.D0)

            staticUses.value = if (rolledMaxUses.value == false) myEquipment.refillDice.bonus else 0

            damageRollerAmount.value = myEquipment.dice1.amount
            damageRollerValue.value = myEquipment.dice1.diceValue
            damageRollerBonus.value = myEquipment.dice1.bonus.toString()
            damageRollerAbility.value = myEquipment.dice1.ability

            usesRollerAmount.value = myEquipment.refillDice.amount
            usesRollerValue.value = myEquipment.refillDice.diceValue
            usesRollerBonus.value = myEquipment.refillDice.bonus.toString()
            usesRollerAbility.value = myEquipment.refillDice.ability

            if (myEquipment.type == ItemType.WEAPON) {
                description1RollerAmount.value = myEquipment.dice2.amount
                description1RollerValue.value = myEquipment.dice2.diceValue
                description1RollerBonus.value = myEquipment.dice2.bonus.toString()
                description1RollerAbility.value = myEquipment.dice2.ability
            } else {
                description1RollerAmount.value = myEquipment.dice1.amount
                description1RollerValue.value = myEquipment.dice1.diceValue
                description1RollerBonus.value = myEquipment.dice1.bonus.toString()
                description1RollerAbility.value = myEquipment.dice1.ability
            }
            description2RollerAmount.value = myEquipment.dice2.amount
            description2RollerValue.value = myEquipment.dice2.diceValue
            description2RollerBonus.value = myEquipment.dice2.bonus.toString()
            description2RollerAbility.value = myEquipment.dice2.ability

            _equipment.value = myEquipment
        }
    }
}