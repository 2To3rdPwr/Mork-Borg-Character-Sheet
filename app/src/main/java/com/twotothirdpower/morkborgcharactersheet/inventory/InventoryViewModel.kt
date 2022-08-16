package com.twotothirdpower.morkborgcharactersheet.inventory

import androidx.lifecycle.*
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabaseDAO
import kotlinx.coroutines.launch
import com.twotothirdpower.morkborgcharactersheet.models.*
import com.twotothirdpower.morkborgcharactersheet.util.EquipmentRecyclerViewButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryViewModel(val characterId: Long = 1, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource
    private lateinit var character: com.twotothirdpower.morkborgcharactersheet.database.Character

    /**
     * LiveData
     */
    private val _expandableEquipmentList = MutableLiveData<MutableList<ExpandableEquipment>>()
    val expandableEquipmentList: LiveData<MutableList<ExpandableEquipment>>
        get() = _expandableEquipmentList

    private val _usedEquipment = MutableLiveData<Equipment>()
    val usedEquipment: LiveData<Equipment>
        get() = _usedEquipment

    val silver = MutableLiveData(0)

    /**
     * Events
     */
    // Set editingItem to -1L to generate a new item
    private val _editingItem = MutableLiveData<Long?>()
    val editingItem: LiveData<Long?>
        get() = _editingItem
    fun onEditItem(joinId: Long) {
        _editingItem.value = joinId
    }
    fun onEditItemDone() {
        _editingItem.value = null
    }

    private val _restoredHP = MutableLiveData<Int?>(null)
    val restoredHP: LiveData<Int?>
        get() = _restoredHP
    fun onRestoredHpDone() {
        _restoredHP.value = null
    }

    private val _usedEquipmentDescription = MutableLiveData<String?>()
    val usedEquipmentDescription: LiveData<String?>
        get() = _usedEquipmentDescription
    fun onUseItemDone() {
        _usedEquipmentDescription.value = null
    }

    /**
     * Not sure how to properly handle clicklisteners to multiple parts of a recyclerview item, so
     * I'm passing an enum along with the Inventory item to allow the viewModel to decide the
     * appropriate course of action.
     */
    fun onEquipmentClick(equipment: ExpandableEquipment, action: EquipmentRecyclerViewButton) {
        when(action) {
            EquipmentRecyclerViewButton.USE -> onEquipmentUseClicked(equipment)
            EquipmentRecyclerViewButton.EQUIP -> onEquipmentEquipClicked(equipment)
            EquipmentRecyclerViewButton.EDIT -> onEquipmentEditClicked(equipment)
            EquipmentRecyclerViewButton.DELETE -> onEquipmentDeleteClicked(equipment)
            EquipmentRecyclerViewButton.EXPAND -> onEquipmentExpand(equipment)
            else -> throw IllegalStateException("Illegal button type")
        }
    }

    fun onShortRestClicked() {
        var newHP = Dice(diceValue = DiceValue.D4).roll()
        _restoredHP.value = newHP
        newHP = Math.min(character.currentHP + newHP, character.maxHP)
        viewModelScope.launch {
            setHP(newHP)
        }
    }

    fun onLongRestClicked() {
        var newHP = Dice(diceValue = DiceValue.D6).roll()
        _restoredHP.value = newHP
        newHP = Math.min(character.currentHP + newHP, character.maxHP)

        val newPowers = Math.max(Dice(diceValue = DiceValue.D4).roll(character.presence), 0)
        val newOmens = Dice(diceValue = DiceValue.D2).roll()

        viewModelScope.launch {
            setHP(newHP)
            restCharacter(newPowers, newOmens)
        }
    }

    fun onStop() {
        // Save any currently expanded equipment when stopping
        viewModelScope.launch {
            setSilver(silver.value?:0)
            _expandableEquipmentList.value!!.forEach { item ->
                if (item.expanded) {
                    updateEquipment(item)
                }
            }
        }
    }

    private fun onEquipmentExpand(equipment: ExpandableEquipment) {
        _expandableEquipmentList.value!!.forEach { item ->
            if (item.expanded) {
                viewModelScope.launch {
                    updateEquipment(item)
                }
            }
            item.expanded = if (item.joinId == equipment.joinId) !item.expanded else false
        }
        _expandableEquipmentList.value = expandableEquipmentList.value
    }

    private fun onEquipmentEquipClicked(equipment: ExpandableEquipment) {
        equipment.equipped = !equipment.equipped
        viewModelScope.launch {
            if (equipment.equipped && (equipment.type == ItemType.ARMOR || equipment.type == ItemType.SHIELD)) {
                equipSingle(characterId, equipment.joinId, equipment.type!!.id)

                _expandableEquipmentList.value!!.forEach { listEquipment ->
                    if (listEquipment.type == equipment.type && listEquipment.joinId != equipment.joinId) {
                        listEquipment.equipped = false
                    }
                }
                // Trigger update
                _expandableEquipmentList.value = expandableEquipmentList.value
            } else {
                updateEquipment(equipment)
            }
        }
        _expandableEquipmentList.value!![equipment.position] = equipment
        _expandableEquipmentList.value = expandableEquipmentList.value
    }

    private fun onEquipmentEditClicked(equipment: ExpandableEquipment) {
        _editingItem.value = equipment.joinId
    }

    private fun onEquipmentDeleteClicked(equipment: ExpandableEquipment) {
        viewModelScope.launch {
            deleteEquipment(equipment)
        }
        _expandableEquipmentList.value!!.remove(equipment)
        _expandableEquipmentList.value = expandableEquipmentList.value
    }

    private fun onEquipmentUseClicked(equipment: ExpandableEquipment) {
        if (equipment.type == ItemType.OTHER && (equipment.uses > 0 || !equipment.limitedUses)) {
            _usedEquipment.value = equipment
            _usedEquipmentDescription.value = equipment.rolledDescription(character)
        }

        if (equipment.limitedUses && equipment.uses > 0) {
            equipment.uses --
            viewModelScope.launch {
                updateEquipment(equipment)
            }
            _expandableEquipmentList.value!![equipment.position] = equipment
            _expandableEquipmentList.value = expandableEquipmentList.value
        }
    }

    /**
     * suspend functions for async database access
     */
    private suspend fun getCharacter(): com.twotothirdpower.morkborgcharactersheet.database.Character {
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
            database.clearEquipment(equipment.joinId)
            if (!equipment.defaultItem)
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
            myEquipment = myEquipment.sorted()
            _expandableEquipmentList.value = myEquipment.toMutableList()

            character = getCharacter()
            silver.value = character.silver
        }
    }

    init {
        loadInventory()
    }
}