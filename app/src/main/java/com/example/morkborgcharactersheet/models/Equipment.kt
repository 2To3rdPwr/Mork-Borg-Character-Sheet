package com.example.morkborgcharactersheet.models

import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.database.CharacterInventoryJoin
import com.example.morkborgcharactersheet.database.Inventory
import kotlin.random.Random

/**
 * Equipment objects bridge the gap between Inventory and CharacterInventoryJoin
 * Could probably subclass this into the different inventory types TBH
 */
open class Equipment(private var inventory: Inventory, private var invJoin: CharacterInventoryJoin?) {
    val joinId = Random.nextLong()
    val inventoryId = inventory.inventoryId
    val characterId = inventory.characterId
    var name = inventory.name
    var description = inventory.description
    val type = ItemType.get(inventory.type)
    var equipped = inventory.equipped
    val armorTier = ArmorTier.get(inventory.armorTier)
    val weaponAbility = AbilityType.get(inventory.ability)
    val dice1 = Dice(inventory.dice1Amount, DiceValue.getByValue(inventory.dice1Value)?:DiceValue.D2, inventory.dice1Bonus, AbilityType.get(inventory.dice1Ability)!!)
    val dice2 = Dice(inventory.dice2Amount, DiceValue.getByValue(inventory.dice2Value)?:DiceValue.D2, inventory.dice2Bonus, AbilityType.get(inventory.dice2Ability)!!)
    val limitedUses = inventory.uses != -1
    val refillable = inventory.refillable
    var uses = inventory.uses
    val refillDice = Dice(inventory.refillDiceAmount, DiceValue.getByValue(inventory.refillDiceValue)?:DiceValue.D2, inventory.refillDiceBonus, AbilityType.get(inventory.refillDiceAbility)!!)
    var broken = false
    val formattedDescription = formatDescription()
    // TODO: Set by itemtype
    // TODO: Later allow user to choose
    val equipmentImage = R.drawable.shield

    fun getInventory(): Inventory {
        inventory.name = name
        inventory.description = description
        inventory.equipped = equipped
        inventory.uses = uses
        return inventory
    }

    fun reload(abilityScore: Int) {
        if (!refillable)
            throw IllegalArgumentException("This item incapable of reloading")

        uses = Math.max(refillDice.roll(abilityScore), 1)
    }

    /**
     * Inserts the string values of our dice into the item's description
     */
    private fun formatDescription(): String {
        val d1 = if (type == ItemType.WEAPON) dice2.toString() else dice1.toString()
        val d2 = dice2.toString()
        return description.replace("\${D1}", d1).replace("\${D2}", d2)
    }

    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = inventory.hashCode()
        result = 31 * result + (invJoin?.hashCode() ?: 0)
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
        result = 31 * result + refillDice.hashCode()
        result = 31 * result + broken.hashCode()
        return result
    }
}