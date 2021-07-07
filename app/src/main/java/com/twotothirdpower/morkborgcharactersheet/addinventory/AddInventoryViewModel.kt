package com.twotothirdpower.morkborgcharactersheet.addinventory

import android.util.Log
import androidx.lifecycle.*
import com.twotothirdpower.morkborgcharactersheet.database.Character
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabaseDAO
import com.twotothirdpower.morkborgcharactersheet.database.CharacterInventoryJoin
import com.twotothirdpower.morkborgcharactersheet.database.Inventory
import com.twotothirdpower.morkborgcharactersheet.models.*
import com.twotothirdpower.morkborgcharactersheet.util.EquipmentRecyclerViewButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddInventoryViewModel (val characterId: Long, dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource
    private lateinit var character: Character

    /**
     * LiveData
     */
    private val _silver = MutableLiveData<Int>()
    val silver: LiveData<Int>
        get() = _silver

    val searchString = MutableLiveData<String>("")

    private val _expandableEquipmentList = MutableLiveData<List<ExpandableEquipment>>()
    val expandableEquipmentList: LiveData<List<ExpandableEquipment>>
        get() = _expandableEquipmentList

    val freeEquipment = MutableLiveData<Boolean>(false)

    // TODO: Use MediatorLiveData to do a switchmap of both searchString and freeEquipment (we want to set silver to 0 if free)
    // https://stackoverflow.com/questions/49493772/mediatorlivedata-or-switchmap-transformation-with-multiple-parameters
    val filteredEquipmentList: LiveData<List<ExpandableEquipment>> = Transformations.switchMap(searchString) {
        filterInventoryList(it)
    }

    /**
     * Events
     */
    private val _customInventoryEvent = MutableLiveData<Boolean>(false)
    val customInventoryEvent: LiveData<Boolean>
        get() = _customInventoryEvent
    fun onCustomInventoryEvent() {
        _customInventoryEvent.value = true
    }
    fun onCustomInventoryEventDone() {
        _customInventoryEvent.value = false
    }

    // TODO: Event for toast pop for user feedback on adding equipment

    fun onEquipmentClick(equipment: ExpandableEquipment, action: EquipmentRecyclerViewButton) {
        when(action) {
            EquipmentRecyclerViewButton.ADD -> addEquipment(equipment)
            EquipmentRecyclerViewButton.EXPAND -> onEquipmentExpand(equipment)
            else -> throw IllegalStateException("Illegal button type")
        }
    }

    private fun onEquipmentExpand(equipment: ExpandableEquipment) {
        Log.i("expanding", equipment.inventoryId.toString())
        _expandableEquipmentList.value!!.forEach { item ->
            item.expanded = if (item.inventoryId == equipment.inventoryId) !item.expanded else false
        }
        // Force expandableEquipmentList to refresh
        _expandableEquipmentList.value = expandableEquipmentList.value
        searchString.value = searchString.value
    }

    private fun filterInventoryList(query: String): LiveData<List<ExpandableEquipment>>? {
        val inventoryList = expandableEquipmentList.value
        if (inventoryList == null) {
            return null
        } else {
            // Create a MutableLiveData to set the value, then reference it in the LiveData that we return
            val filteredInventoryList = MutableLiveData<List<ExpandableEquipment>>()
            filteredInventoryList.value = inventoryList.filter { item ->
                item.name.contains(query)
            }
            return filteredInventoryList
        }
    }

    private fun addEquipment(equipment: Equipment) {
        /**
         * 1. subtract silver if necessary
         * 2. If limited uses and type = other, or item is arrows or bolts, check if the character already owns the item
         *      if so, add purchased uses to the item and update it. Return.
         *      else, add uses to inventoryJoin. Continue.
         * 3. Save equipment.
         */
        viewModelScope.launch {
            if (freeEquipment.value == false) {
                // TODO: Prevent purchase if too expensive
                val newSilver = Math.max(silver.value?.minus(equipment.silver) ?: 0, 0)
                _silver.value = newSilver
                setSilver(newSilver)
            }

            var newInvJoin = true
            var saveInvJoin = equipment.getInvJoin()

            if (equipment.limitedUses) {
                // hardcoded inventoryIds from DefaultInventoryList
                val searchInventoryId = when (equipment.inventoryId) {
                    // Buying arrows. Search for bow
                    87L -> 7L
                    // Buying bolts. Search for crossbow
                    88L -> 9L
                    else -> equipment.inventoryId
                }
                // Does the user already own this item?
                val ownedEquipment = getOwnedEquipment(searchInventoryId)
                if (ownedEquipment != null) {
                    saveInvJoin = ownedEquipment
                    newInvJoin = false
                }

                // Add uses
                val abilityBonus = when (equipment.initialUseDice.ability) {
                    AbilityType.STRENGTH -> character.strength
                    AbilityType.AGILITY -> character.agility
                    AbilityType.PRESENCE -> character.presence
                    AbilityType.TOUGHNESS -> character.toughness
                    else -> 0
                }
                saveInvJoin.uses += equipment.initialUseDice.roll(abilityBonus)
            }

            if (newInvJoin) {
                insertEquipment(saveInvJoin)
            } else {
                updateEquipment(saveInvJoin)
            }
            // TODO: Pop toast
        }
    }

    /**
     * Database Access
     */
    private suspend fun getCharacter(): Character {
        return withContext(Dispatchers.IO) {
            database.getCharacter(characterId) ?: throw IllegalArgumentException("Invalid CharacterId")
        }
    }

    private suspend fun getDefaultEquipment(): List<ExpandableEquipment> {
        return withContext(Dispatchers.IO) {
            val myData = database.getDefaultInventory()
            myData.map { equipmentData -> ExpandableEquipment(equipmentData, characterId) }
        }
    }

    private suspend fun getOwnedEquipment(inventoryId: Long): CharacterInventoryJoin? {
        return withContext(Dispatchers.IO) {
            database.getCharactersEquipmentByType(characterId, inventoryId)
        }
    }

    private suspend fun setSilver(silver: Int) {
        withContext(Dispatchers.IO) {
            database.setSilver(characterId, silver)
        }
    }

    private suspend fun updateEquipment(equipment: CharacterInventoryJoin) {
        withContext(Dispatchers.IO) {
            database.updateInventoryJoin(equipment)
        }
    }

    private suspend fun insertEquipment(equipment: CharacterInventoryJoin) {
        withContext(Dispatchers.IO) {
            database.insertInventoryJoin(equipment)        }
    }

    init {
        viewModelScope.launch {
            character = getCharacter()
            _silver.value = character.silver
            _expandableEquipmentList.value = getDefaultEquipment()
            searchString.value = ""
        }
    }
}