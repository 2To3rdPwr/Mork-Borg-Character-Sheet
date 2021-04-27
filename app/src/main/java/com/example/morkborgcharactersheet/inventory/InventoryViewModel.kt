package com.example.morkborgcharactersheet.inventory

import android.util.Log
import androidx.lifecycle.*
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import com.example.morkborgcharactersheet.database.Inventory
import com.example.morkborgcharactersheet.models.ItemType
import kotlinx.coroutines.launch
import com.example.morkborgcharactersheet.inventory.EquipmentAdapter.EquiptmentRecyclerViewButton
import com.example.morkborgcharactersheet.models.Dice
import com.example.morkborgcharactersheet.models.DiceValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Integer.min

class InventoryViewModel(val characterId: Long = 1, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    /**
     * LiveData
     */
    lateinit var inventory: LiveData<List<Inventory>>
    private lateinit var character: com.example.morkborgcharactersheet.database.Character

    val silver = MutableLiveData<Int>(0)

    private val _currentItem = MutableLiveData<Long>()
    val currentItem: LiveData<Long>
        get() = _currentItem

    /**
     * Events
     */
    private val _newInventoryEvent = MutableLiveData<Boolean>()
    val newInventoryEvent: LiveData<Boolean>
        get() = _newInventoryEvent
    fun onNewInventoryEventDone() {
        Log.i("Navigating", "NewInventoryEventCompleted")
        _newInventoryEvent.value = false
    }

    /**
     * FE Functions
     */

    fun onNewInventoryEvent() {
        Log.i("Navigating", "NewInventoryEvent")
        _newInventoryEvent.value = true
    }

    /**
     * Not sure how to properly handle clicklisteners to multiple parts of a recyclerview item, so
     * I'm passing an enum along with the Inventory item to allow the viewModel to decide the
     * appropriate course of action.
     */
    fun onEquipmentClick(equipment: Inventory, action: EquiptmentRecyclerViewButton) {
        when(action) {
            EquiptmentRecyclerViewButton.RELOAD -> onEquipmentReloadClicked(equipment)
            EquiptmentRecyclerViewButton.EQUIP -> onEquipmentEquipClicked(equipment)
            EquiptmentRecyclerViewButton.EDIT -> onEquipmentEditClicked(equipment)
            EquiptmentRecyclerViewButton.DELETE -> onEquipmentDeleteClicked(equipment)
        }
    }

    fun onShortRestClicked() {
        var newHP = Dice(diceValue = DiceValue.D4).roll()
        newHP = Math.min(character.currentHP + newHP, character.maxHP)
        viewModelScope.launch {
            setHP(newHP)
        }
    }

    fun onLongRestClicked() {
        var newHP = Dice(diceValue = DiceValue.D6).roll()
        newHP = Math.min(character.currentHP + newHP, character.maxHP)

        val newPowers = Math.max(Dice(diceValue = DiceValue.D4).roll(character.presence), 0)
        val newOmens = Dice(diceValue = DiceValue.D2).roll()

        viewModelScope.launch {
            setHP(newHP)
            restCharacter(newPowers, newOmens)
        }
    }

    fun setCharactersSilver() {
        viewModelScope.launch {
            setSilver(silver.value?:0)
        }
    }

    private fun onEquipmentEquipClicked(equipment: Inventory) {
        viewModelScope.launch {
            equipment.equipped = !equipment.equipped
            if (equipment.equipped && equipment.type == ItemType.ARMOR.id)
                equipArmor(characterId, equipment.inventoryId)
            else if (equipment.equipped && equipment.type == ItemType.SHIELD.id)
                equipShield(characterId, equipment.inventoryId)
            else
                updateEquipment(equipment)
        }
    }

    private fun onEquipmentEditClicked(equipment: Inventory) {
        _currentItem.value = equipment.inventoryId
        _newInventoryEvent.value = true
    }

    private fun onEquipmentDeleteClicked(equipment: Inventory) {
        viewModelScope.launch {
            deleteEquipment(equipment.inventoryId)
        }
    }

    private fun onEquipmentReloadClicked(equipment: Inventory) {
        viewModelScope.launch {
            val abilityBonus = getAbilityScore(equipment.characterId, equipment.refillDiceAbility)
            equipment.uses = Dice(equipment.refillDiceAmount, DiceValue.getByValue(equipment.refillDiceValue)?:DiceValue.D2, equipment.refillDiceBonus).roll(abilityBonus)
            updateEquipment(equipment)
        }
    }

    /**
     * suspend functions for async database access
     */
    private suspend fun getCharacter(): com.example.morkborgcharactersheet.database.Character {
        return withContext(Dispatchers.IO) {
            var character = database.getCharacter(characterId)!!
            character
        }
    }

    private suspend fun updateEquipment(equipment: Inventory) {
        withContext(Dispatchers.IO) {
            database.updateInventory(equipment)
        }
    }

    private suspend fun deleteEquipment(equipment: Long) {
        withContext(Dispatchers.IO) {
            database.clearSingleInventoryObject(equipment)
        }
    }

    private suspend fun equipArmor(characterId: Long, equipment: Long) {
        withContext(Dispatchers.IO) {
            database.equipArmor(characterId, equipment)
        }
    }

    private suspend fun equipShield(characterId: Long, equipment: Long) {
        withContext(Dispatchers.IO) {
            database.equipShield(characterId, equipment)
        }
    }

    private suspend fun getAbilityScore(characterId: Long, abilityType: Int): Int {
        return withContext(Dispatchers.IO) {
            var myScore = database.getAbilityScoreForCharacter(characterId, abilityType)
            myScore
        }
    }

    private suspend fun setSilver(silver: Int) {
        withContext(Dispatchers.IO) {
            database.setSilver(characterId, silver)
        }
    }

    private suspend fun setHP(newHP: Int) {
        withContext(Dispatchers.IO) {
            database.setCharactersHP(characterId, newHP)
        }
    }

    private suspend fun restCharacter(newPowers: Int, newOmens: Int) {
        withContext(Dispatchers.IO) {
            database.refreshPowersAndOmens(characterId, newPowers, newOmens)
        }
    }

    init {
        viewModelScope.launch {
            inventory = database.getInventoryForCharacter(characterId)
            character = getCharacter()
            silver.value = character.silver
        }
    }
}