package com.twotothirdpower.morkborgcharactersheet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * I could probably split inventory up into separate tables for each weapon type, but the point of
 * this project is to learn and demonstrate android not sql, so just making a massive table
 * antipattern for now
 */

@Entity(tableName = "inventory")
data class Inventory (
    @PrimaryKey(autoGenerate = true)
    var inventoryId: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String = "",

    /**
     * Type: Can be one of
     *      1: Weapon/Attack
     *      2: Armor
     *      3: Shield
     *      4: Power
     *      TODO: 5: Creature/ally
     *      0: Other
     */
    @ColumnInfo(name = "type")
    var type: Int = 0,

    /**
     * Armor Tier
     *      0: None
     *      1: Light armor: -d2 damage
     *      2: Medium Armor: -d4 damage, -2 on agility checks, Can't use powers
     *      3: Heavy Armor: -d6 damage, -4 on agility checks, Defence DR + 2, Can't use powers
     */
    @ColumnInfo(name = "armor_tier")
    var armorTier: Int = 0,

    /**
     * Weapons use abilities when calculating to-hit
     * Ability can be one of
     *      1: Strength
     *      2: Agility
     *      3: Presence
     *      4: Toughness
     *      0: None
     * Currently limited to just 1 (melee weapons) and 3 (ranged weapons) but leaving things open to
     *  potentially allow for weird homebrew stuff in the future.
     */
    @ColumnInfo(name = "ability")
    var ability: Int = 0,

    /**
     * The following variables are used for dice rolls
     * When rolling dice due to an inventory item, roll
     * amount D value + bonus + ability
     */
    @ColumnInfo(name = "dice1_amount")
    var dice1Amount: Int = 1,

    @ColumnInfo(name = "dice1_value")
    var dice1Value: Int = 0,

    @ColumnInfo(name = "dice1_bonus")
    var dice1Bonus: Int = 0,

    @ColumnInfo(name = "dice1_ability")
    var dice1Ability: Int = 0,

    @ColumnInfo(name = "dice2_amount")
    var dice2Amount: Int = 1,

    @ColumnInfo(name = "dice2_value")
    var dice2Value: Int = 0,

    @ColumnInfo(name = "dice2_bonus")
    var dice2Bonus: Int = 0,

    @ColumnInfo(name = "dice2_ability")
    var dice2Ability: Int = 0,

    /**
     * Does this item have a set number of uses available?
     * Applicable to weapons and other
     */
    @ColumnInfo(name = "limited_uses")
    var limitedUses: Boolean = false,

    /**
     * TODO: Remove and allow all items to be refilled.
     */
    @ColumnInfo(name = "refillable")
    var refillable: Boolean = false,

    /**
     * Some default items start with a rolled number of uses
     * (EX: rolled character can start with a bottle of Red Poison with d4 doses)
     * Other items come with a set number of uses modified by an ability
     * (Bows start with Presence + 10 arrows)
     *
     * Not applicable beyond creating new instances of default items.
     */
    @ColumnInfo(name = "uses_dice_amount")
    val InitialUsesDiceAmount: Int = 1,

    @ColumnInfo(name = "uses_dice_value")
    val InitialUsesDiceValue: Int = 0,

    @ColumnInfo(name = "uses_dice_bonus")
    val InitialUsesDiceBonus: Int = 0,

    @ColumnInfo(name = "uses_dice_ability")
    val InitialUsesDiceAbility: Int = 0,

    @ColumnInfo(name = "default_item")
    val defaultItem: Boolean = false
)
