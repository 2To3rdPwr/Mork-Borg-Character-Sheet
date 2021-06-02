package com.example.morkborgcharactersheet.inventory

import androidx.lifecycle.*
import com.example.morkborgcharactersheet.database.CharacterDatabaseDAO
import com.example.morkborgcharactersheet.database.Inventory
import kotlinx.coroutines.launch
import com.example.morkborgcharactersheet.inventory.EquipmentAdapter.EquipmentRecyclerViewButton
import com.example.morkborgcharactersheet.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// TODO: reference equipment by JoinId instead of InventoryId
class InventoryViewModel(val characterId: Long = 1, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    /**
     * LiveData
     */
    private val _expandableEquipmentList = MutableLiveData<MutableList<ExpandableEquipment>>()
    val expandableEquipmentList: LiveData<MutableList<ExpandableEquipment>>
        get() = _expandableEquipmentList

    private lateinit var character: com.example.morkborgcharactersheet.database.Character

    val silver = MutableLiveData(0)

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
        _newInventoryEvent.value = false
    }
    fun onNewInventoryEvent() {
        _newInventoryEvent.value = true
    }

    /**
     * Not sure how to properly handle clicklisteners to multiple parts of a recyclerview item, so
     * I'm passing an enum along with the Inventory item to allow the viewModel to decide the
     * appropriate course of action.
     */
    fun onEquipmentClick(equipment: ExpandableEquipment, action: EquipmentRecyclerViewButton) {
        when(action) {
            EquipmentRecyclerViewButton.USE -> onEquipmentUseClicked(equipment)
            EquipmentRecyclerViewButton.RELOAD -> onEquipmentReloadClicked(equipment)
            EquipmentRecyclerViewButton.EQUIP -> onEquipmentEquipClicked(equipment)
            EquipmentRecyclerViewButton.EDIT -> onEquipmentEditClicked(equipment)
            EquipmentRecyclerViewButton.DELETE -> onEquipmentDeleteClicked(equipment)
            EquipmentRecyclerViewButton.EXPAND -> onEquipmentExpand(equipment)
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

    private fun onEquipmentExpand(equipment: ExpandableEquipment) {
        _expandableEquipmentList.value!!.forEach { item -> item.expanded = if (item.inventoryId == equipment.inventoryId) !item.expanded else false }
        _expandableEquipmentList.value = expandableEquipmentList.value
    }

    private fun onEquipmentEquipClicked(equipment: ExpandableEquipment) {
        equipment.equipped = !equipment.equipped
        viewModelScope.launch {
            if (equipment.equipped && (equipment.type == ItemType.ARMOR || equipment.type == ItemType.SHIELD))
                equipSingle(characterId, equipment.getInvJoin().characterInventoryJoinId, equipment.type!!.id)
            else
                updateEquipment(equipment)
        }
        _expandableEquipmentList.value!![equipment.position] = equipment
        _expandableEquipmentList.value = expandableEquipmentList.value
    }

    private fun onEquipmentEditClicked(equipment: ExpandableEquipment) {
        _currentItem.value = equipment.inventoryId
        _newInventoryEvent.value = true
    }

    private fun onEquipmentDeleteClicked(equipment: ExpandableEquipment) {
        viewModelScope.launch {
            deleteEquipment(equipment)
        }
        _expandableEquipmentList.value!!.remove(equipment)
        _expandableEquipmentList.value = expandableEquipmentList.value
    }

    private fun onEquipmentUseClicked(equipment: ExpandableEquipment) {
        if (equipment.limitedUses && equipment.uses > 0) {
            equipment.uses --
            viewModelScope.launch {
                updateEquipment(equipment)
            }
            _expandableEquipmentList.value!![equipment.position] = equipment
            _expandableEquipmentList.value = expandableEquipmentList.value
        }
    }

    private fun onEquipmentReloadClicked(equipment: ExpandableEquipment) {
        if (equipment.refillable) {
            viewModelScope.launch {
                var abilityBonus = 0
                if (equipment.refillDice.ability != AbilityType.UNTYPED)
                    abilityBonus = getAbilityScore(equipment.characterId, equipment.refillDice.ability.id)
                equipment.reload(abilityBonus)
                updateEquipment(equipment)
            }
            _expandableEquipmentList.value!![equipment.position] = equipment
            _expandableEquipmentList.value = expandableEquipmentList.value
        } else {
            throw IllegalArgumentException("This Equipment not capable of reloading")
        }
    }

    /**
     * suspend functions for async database access
     */
    private suspend fun getCharacter(): com.example.morkborgcharactersheet.database.Character {
        return withContext(Dispatchers.IO) {
            database.getCharacter(characterId) ?: throw IllegalArgumentException("Invalid CharacterId")
        }
    }

    private suspend fun getEquipment(): List<ExpandableEquipment> {
        return withContext(Dispatchers.IO) {
            val myData = database.getCharactersEquipment(characterId)
            myData.map { equipmentData -> ExpandableEquipment(equipmentData) }
        }
    }

    private suspend fun updateEquipment(equipment: Equipment) {
        withContext(Dispatchers.IO) {
            database.updateInventoryJoin(equipment.getInvJoin())
        }
    }

    private suspend fun deleteEquipment(equipment: Equipment) {
        withContext(Dispatchers.IO) {
            database.clearEquipment(characterId, equipment.inventoryId)
            if (!equipment.default)
                database.clearInventory(equipment.inventoryId)
        }
    }

    private suspend fun equipSingle(characterId: Long, equipment: Long, type: Int) {
        withContext(Dispatchers.IO) {
            val unequipList = database.getEquippedByType(characterId, type)
            database.unequipMultipleItems(unequipList)
            database.equipItem(equipment)
        }
    }

    private suspend fun getAbilityScore(characterId: Long, abilityType: Int): Int {
        return withContext(Dispatchers.IO) {
            database.getAbilityScoreForCharacter(characterId, abilityType)
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

    fun loadInventory() {
        viewModelScope.launch {
            var myEquipment = getEquipment()
            myEquipment = myEquipment.sortedBy { it.type?.ordinal ?: 6}
            _expandableEquipmentList.value = myEquipment.toMutableList()

            character = getCharacter()
            silver.value = character.silver
        }
    }

    init {
        loadInventory()
    }
}