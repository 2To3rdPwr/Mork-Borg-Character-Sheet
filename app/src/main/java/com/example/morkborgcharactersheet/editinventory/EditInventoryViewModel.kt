package com.example.morkborgcharactersheet.editinventory

import android.util.Log
import androidx.lifecycle.*
import com.example.morkborgcharactersheet.database.*
import com.example.morkborgcharactersheet.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException

class EditInventoryViewModel (private val inventoryId: Long, private val characterId: Long, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    private var newInventory: Boolean = inventoryId == -1L

    /**
     * LiveData
     */

    // Can't really encapsulate 2-way databinded LiveData.
    val itemName = MutableLiveData<String>()
    val itemDescription = MutableLiveData<String>()
    val itemType = MutableLiveData<ItemType>()

    val weaponAbilityUsed = MutableLiveData<AbilityType>()
    val armorTier = MutableLiveData<ArmorTier>()

    val limitedUse = MutableLiveData<Boolean>(false)
    val rolledMaxUses = MutableLiveData<Boolean>()
    val refillableUses = MutableLiveData<Boolean>()
    val staticUses = MutableLiveData<Int>()

    // I'd love to figure out how to create a MutableLiveData<Dice> and two-way bind them to my includes
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

    val dice1InDescriptionVisibility: LiveData<Boolean> = Transformations.map(itemDescription) {
        it.contains("\${D1}")
    }

    // TODO: MediatorLiveData to enforce ItemType != WEAPON for description2Dice
    val dice2InDescriptionVisibility: LiveData<Boolean> = Transformations.map(itemDescription) {
        it.contains("\${D2}")
    }

    private val _toastText = MutableLiveData<String>()
    val toastText: LiveData<String>
        get() = _toastText
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
        // Confirm all applicable values are properly set
        // TODO: Use string resources for toasts
        // Not being SUPER thorough with input validation here but that's fine for now
        if (itemName.value == null || itemName.value == "") {
            _toastText.value = "Must set item name"
            _showToastEvent.value = true
        } else if (itemType.value == null) {
            _toastText.value = "Must set item type"
            _showToastEvent.value = true
        } else if (itemType.value == ItemType.WEAPON && (weaponAbilityUsed.value == null || weaponAbilityUsed.value == AbilityType.UNTYPED)) {
            _toastText.value = "Must set ability used"
            _showToastEvent.value = true
        } else if (itemType.value == ItemType.ARMOR && (armorTier.value == null || armorTier.value == ArmorTier.NONE)) {
            _toastText.value = "Must set armor tier"
            _showToastEvent.value = true
        } else {
            //  Setting values by item type
            //  EX: Don't set ArmorTier for weapons

            var newItemDice1Amount: Int; var newItemDice1Value: Int; var newItemDice1Ability: Int; var newItemDice1Bonus: Int
            var newItemDice2Amount: Int; var newItemDice2Value: Int; var newItemDice2Ability: Int; var newItemDice2Bonus: Int
            var newUsesDiceAmount: Int; var newUsesDiceValue: Int; var newUsesDiceAbility: Int; var newUsesDiceBonus: Int
            var currentUses: Int = -1

            if (itemType.value == ItemType.WEAPON) {
                newItemDice1Amount = damageRollerAmount.value?:0
                newItemDice1Value = damageRollerValue.value?.value?:DiceValue.D2.value
                newItemDice1Bonus = damageRollerBonus.value?.toIntOrNull()?:0
                newItemDice1Ability = damageRollerAbility.value?.id?:AbilityType.UNTYPED.id

                newItemDice2Amount = description1RollerAmount.value?:0
                newItemDice2Value = description1RollerValue.value?.value?:DiceValue.D2.value
                newItemDice2Bonus = description1RollerBonus.value?.toIntOrNull()?:0
                newItemDice2Ability = description1RollerAbility.value?.id?:AbilityType.UNTYPED.id
            } else {
                newItemDice1Amount = description1RollerAmount.value?:0
                newItemDice1Value = description1RollerValue.value?.value?:DiceValue.D2.value
                newItemDice1Bonus = description1RollerBonus.value?.toIntOrNull()?:0
                newItemDice1Ability = description1RollerAbility.value?.id?:AbilityType.UNTYPED.id

                newItemDice2Amount = description2RollerAmount.value?:0
                newItemDice2Value = description2RollerValue.value?.value?:DiceValue.D2.value
                newItemDice2Bonus = description2RollerBonus.value?.toIntOrNull()?:0
                Log.i("Ability Set", description2RollerAbility.value.toString())
                newItemDice2Ability = description2RollerAbility.value?.id?:AbilityType.UNTYPED.id
            }

            if ((itemType.value == ItemType.WEAPON || itemType.value == ItemType.OTHER) && limitedUse.value == true) {
                if (rolledMaxUses.value == true) {
                    newUsesDiceAmount = usesRollerAmount.value?:1
                    newUsesDiceValue = usesRollerValue.value!!.id
                    newUsesDiceBonus = usesRollerBonus.value?.toIntOrNull()?:0
                    newUsesDiceAbility = usesRollerAbility.value?.id?:0
                } else {
                    newUsesDiceAmount = 0
                    newUsesDiceValue = 0
                    newUsesDiceBonus = staticUses.value?:1
                    newUsesDiceAbility = 0
                }
            } else {
                newUsesDiceAmount = 0
                newUsesDiceValue = 0
                newUsesDiceBonus = 0
                newUsesDiceAbility = 0
            }

            val newArmorTier = if (itemType.value == ItemType.ARMOR) armorTier.value!!.id else 0
            val newAbilityUsed = if (itemType.value == ItemType.WEAPON) weaponAbilityUsed.value!!.id else 0
            val newRefillable = limitedUse.value == true && refillableUses.value == true

            viewModelScope.launch {
                if (limitedUse.value == true) {
                    if (rolledMaxUses.value == true) {
                        val abilityScore = if (usesRollerAbility.value != null && usesRollerAbility.value != AbilityType.UNTYPED) getAbilityScoreForCharacter(characterId, usesRollerAbility.value!!) else 0
                        currentUses = Dice(usesRollerAmount.value?:0, usesRollerValue.value?:DiceValue.D2, usesRollerBonus.value?.toIntOrNull()?:0).roll(abilityScore)
                    } else {
                        currentUses = staticUses.value?:0
                    }
                }

                val newItem = Inventory(
                    characterId = characterId, name = itemName.value!!, description = itemDescription.value?:"",
                    type = itemType.value!!.id, armorTier = newArmorTier, ability = newAbilityUsed,
                    dice1Amount = newItemDice1Amount, dice1Value = newItemDice1Value, dice1Bonus = newItemDice1Bonus, dice1Ability = newItemDice1Ability,
                    dice2Amount = newItemDice2Amount, dice2Value = newItemDice2Value, dice2Bonus = newItemDice2Bonus, dice2Ability = newItemDice2Ability,
                    refillDiceAmount = newUsesDiceAmount, refillDiceValue = newUsesDiceValue, refillDiceBonus = newUsesDiceBonus, refillDiceAbility = newUsesDiceAbility,
                    uses = currentUses, refillable = newRefillable
                )

                if (newInventory) {
                    newInventory(newItem)
                } else {
                    newItem.inventoryId = inventoryId
                    updateInventory(newItem)
                }

                Log.i("Saved Item", newItem.toString())

                _saveItemEvent.value = true
            }
        }
    }

    /**
     * Suspend functions for database access
     */
    private suspend fun newInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            database.insertInventory(inventory)
        }
    }

    private suspend fun updateInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            database.updateInventory(inventory)
        }
    }

    private suspend fun getAbilityScoreForCharacter(characterId: Long, abilityType: AbilityType): Int {
        return withContext(Dispatchers.IO){
            val abilityMod = database.getAbilityScoreForCharacter(characterId, abilityType.id)
            abilityMod
        }
    }

    // TODO: Throw error if no inventory pulled?
    private suspend fun getExistingInventory(inventoryId: Long): Inventory? {
        return withContext(Dispatchers.IO){
            val thisItem = database.getInventory(inventoryId)
            Log.i("editing", thisItem.toString())
            thisItem
        }
    }


    init {
        // Setting initial values
        itemType.value = ItemType.WEAPON
        staticUses.value = 0

        damageRollerAmount.value = 1
        damageRollerValue.value = DiceValue.D6
        damageRollerBonus.value = "0"
        damageRollerAbility.value = AbilityType.UNTYPED

        usesRollerAmount.value = 1
        usesRollerValue.value = DiceValue.D2
        usesRollerBonus.value = "0"
        usesRollerAbility.value = AbilityType.UNTYPED

        description1RollerAmount.value = 1
        description1RollerValue.value = DiceValue.D2
        description1RollerBonus.value = "0"
        description1RollerAbility.value = AbilityType.UNTYPED

        description2RollerAmount.value = 1
        description2RollerValue.value = DiceValue.D2
        description2RollerBonus.value = "0"
        description2RollerAbility.value = AbilityType.UNTYPED

        if (inventoryId > 0) {
            // Updating existing item
            viewModelScope.launch {
                val item = getExistingInventory(inventoryId)

                if (item != null) {
                    itemName.value = item.name
                    itemDescription.value = item.description
                    itemType.value = ItemType.get(item.type)
                    armorTier.value = ArmorTier.get(item.armorTier)
                    weaponAbilityUsed.value = AbilityType.get(item.ability)
                    limitedUse.value = item.uses != -1
                    refillableUses.value = item.refillable

                    if (item.uses != -1 && item.refillDiceAmount == 0 && item.refillDiceAbility == AbilityType.UNTYPED.id) {
                        // Static Uses
                        rolledMaxUses.value = false
                        staticUses.value = item.refillDiceBonus
                    } else {
                        // Rolled Uses
                        rolledMaxUses.value = true
                        usesRollerAmount.value = item.refillDiceAmount
                        usesRollerValue.value = DiceValue.get(item.refillDiceValue)
                        usesRollerBonus.value = item.refillDiceBonus.toString()
                        usesRollerAbility.value = AbilityType.get(item.refillDiceAbility)
                    }

                    if (itemType.value == ItemType.WEAPON) {
                        damageRollerAmount.value = item.dice1Amount
                        damageRollerValue.value = DiceValue.getByValue(item.dice1Value)
                        damageRollerBonus.value = item.dice1Bonus.toString()
                        damageRollerAbility.value = AbilityType.get(item.dice1Ability)

                        description1RollerAmount.value = item.dice2Amount
                        description1RollerValue.value = DiceValue.getByValue(item.dice2Value)
                        description1RollerBonus.value = item.dice2Bonus.toString()
                        description1RollerAbility.value = AbilityType.get(item.dice2Ability)
                    } else {
                        description1RollerAmount.value = item.dice1Amount
                        description1RollerValue.value = DiceValue.getByValue(item.dice1Value)
                        description1RollerBonus.value = item.dice1Bonus.toString()
                        Log.i("AbilityGiven", item.dice1Ability.toString())
                        description1RollerAbility.value = AbilityType.get(item.dice1Ability)

                        description2RollerAmount.value = item.dice2Amount
                        description2RollerValue.value = DiceValue.getByValue(item.dice2Value)
                        description2RollerBonus.value = item.dice2Bonus.toString()
                        description2RollerAbility.value = AbilityType.get(item.dice2Ability)
                    }
                } else {
                    throw IllegalArgumentException("Invalid inventoryId")
                }
            }
        }
    }
}