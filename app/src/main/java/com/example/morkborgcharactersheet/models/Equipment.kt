package com.example.morkborgcharactersheet.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.morkborgcharactersheet.BR
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.database.CharacterInventoryJoin
import com.example.morkborgcharactersheet.database.EquipmentData
import com.example.morkborgcharactersheet.database.Inventory

/**
 * Equipment objects contain data on individual instances of an inventory item
 * TBH, Should probably have subclassed this into the different inventory types
 */
open class Equipment(private var inventory: Inventory, private var invJoin: CharacterInventoryJoin) : BaseObservable() {
    val joinId = invJoin.characterInventoryJoinId
    val inventoryId = inventory.inventoryId
    val characterId = invJoin.characterId
    var name = inventory.name
    @Bindable
    var description = inventory.description
    set(value) {
        field = value
        notifyPropertyChanged(BR.description)
    }
    @Bindable
    var type = ItemType.get(inventory.type)
    set(value) {
        field = value
        notifyPropertyChanged(BR.type)
    }
    @Bindable
    var equipped = invJoin.equipped
    set(value) {
        field = value
        notifyPropertyChanged(BR.equipped)
    }
    @Bindable
    var armorTier = ArmorTier.get(inventory.armorTier)
    set(value) {
        field = value
        notifyPropertyChanged(BR.armorTier)
    }
    @Bindable
    var weaponAbility = AbilityType.get(inventory.ability)
    set(value) {
        field = value
        notifyPropertyChanged(BR.weaponAbility)
    }
    var dice1 = Dice(inventory.dice1Amount, DiceValue.getByValue(inventory.dice1Value)?:DiceValue.D0, inventory.dice1Bonus, AbilityType.get(inventory.dice1Ability)!!)
    var dice2 = Dice(inventory.dice2Amount, DiceValue.getByValue(inventory.dice2Value)?:DiceValue.D0, inventory.dice2Bonus, AbilityType.get(inventory.dice2Ability)!!)
    @Bindable
    var limitedUses = inventory.limitedUses
    set(value) {
        field = value
        notifyPropertyChanged(BR.limitedUses)
    }
    @Bindable
    var refillable = inventory.refillable
    set(value) {
        field = value
        notifyPropertyChanged(BR.refillable)
    }
    @Bindable
    var uses = invJoin.uses
    set(value) {
        field = value
        notifyPropertyChanged(BR.uses)
    }
    val initialUseDice = Dice(inventory.InitialUsesDiceAmount, DiceValue.getByValue(inventory.InitialUsesDiceValue)?:DiceValue.D0, inventory.InitialUsesDiceBonus, AbilityType.get(inventory.InitialUsesDiceAbility)!!)
    var broken = invJoin.broken
    @Bindable
    val defaultItem: Boolean = inventory.defaultItem

    var formattedDescription = formatDescription()

    constructor(equipmentData: EquipmentData) : this(equipmentData.inventory, equipmentData.inventoryJoin)

    // TODO: Later allow user to choose
    val equipmentImage = when (type) {
        ItemType.WEAPON -> if (weaponAbility == AbilityType.STRENGTH) R.drawable.sword else R.drawable.bow
        ItemType.POWER -> R.drawable.ancient_scroll
        ItemType.ARMOR -> R.drawable.armor
        ItemType.SHIELD -> R.drawable.shield
        ItemType.OTHER -> R.drawable.potion
        else -> R.drawable.broken
    }

    fun initialize(characterId: Long, inventoryId: Long) {
        inventory.inventoryId = inventoryId
        invJoin.inventoryId = inventoryId
        invJoin.characterId = characterId
    }

    fun getInventory(): Inventory {
        inventory.name = name
        inventory.description = description
        inventory.type = type!!.id
        inventory.ability = weaponAbility!!.id
        inventory.armorTier = armorTier!!.id
        inventory.limitedUses = limitedUses
        inventory.refillable = refillable
        inventory.dice1Amount = dice1.amount
        inventory.dice1Value = dice1.diceValue.value
        inventory.dice1Bonus = dice1.bonus
        inventory.dice1Ability = dice1.ability.id
        inventory.dice2Amount = dice2.amount
        inventory.dice2Value = dice2.diceValue.value
        inventory.dice2Bonus = dice2.bonus
        inventory.dice2Ability = dice2.ability.id
        return inventory
    }

    fun getInvJoin(): CharacterInventoryJoin {
        invJoin.equipped = equipped
        invJoin.broken = broken
        invJoin.uses = uses
        return invJoin
    }

    /**
     * Inserts the string values of our dice into the item's description
     */
    private fun formatDescription(): String {
        val d1 = if (type == ItemType.WEAPON) dice2.toString() else dice1.toString()
        val d2 = dice2.toString()
        return description.replace("\$D1", d1).replace("\$D2", d2)
    }

    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = inventory.hashCode()
        result = 31 * result + invJoin.hashCode()
        result = 31 * result + inventoryId.hashCode()
        result = 31 * result + characterId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + equipped.hashCode()
        result = 31 * result + (armorTier?.hashCode() ?: 0)
        result = 31 * result + (weaponAbility?.hashCode() ?: 0)
        result = 31 * result + dice1.hashCode()
        result = 31 * result + dice2.hashCode()
        result = 31 * result + limitedUses.hashCode()
        result = 31 * result + refillable.hashCode()
        result = 31 * result + uses
        result = 31 * result + initialUseDice.hashCode()
        result = 31 * result + broken.hashCode()
        return result
    }
}