package com.twotothirdpower.morkborgcharactersheet.models

import com.twotothirdpower.morkborgcharactersheet.database.CharacterInventoryJoin
import com.twotothirdpower.morkborgcharactersheet.database.EquipmentData
import com.twotothirdpower.morkborgcharactersheet.database.Inventory

class ExpandableEquipment(inventory: Inventory, inventoryJoin: CharacterInventoryJoin) : Equipment(inventory, inventoryJoin){
    var expanded = false
    var position = -1

    constructor(equipmentData: EquipmentData) : this(equipmentData.inventory, equipmentData.inventoryJoin)
    constructor(inventory: Inventory, characterId: Long) : this(inventory, CharacterInventoryJoin(inventoryId = inventory.inventoryId, characterId = characterId))

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + expanded.hashCode()
        result = 31 * result + position.hashCode()
        return result
    }
}