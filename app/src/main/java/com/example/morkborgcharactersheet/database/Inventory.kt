package com.example.morkborgcharactersheet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * I could probably split inventory up into separate tables for each weapon type, but the point of
 * this project is to learn and demonstrate android, so just making a massive table antipattern for
 * now
 */

@Entity(tableName = "inventory")
data class Inventory (
    @PrimaryKey(autoGenerate = true)
    var inventoryId: Long = 0L,

    // TODO: Replace with join table
    //      This way multiple characters can use the same gear
    //      and we can have default gear
    //      I'll have to add a column denoting gear as default tho
    @ColumnInfo(name = "character_id")
    val characterId: Long,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "equipped")
    var equipped: Boolean = false,

    /**
     * Type: Can be one of
     *      1: Weapon/Attack
     *      2: Armor
     *      3: Shield
     *      4: Power
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
    var dice1Value: Int = 2,

    @ColumnInfo(name = "dice1_bonus")
    var dice1Bonus: Int = 0,

    @ColumnInfo(name = "dice1_ability")
    var dice1Ability: Int = 0,

    @ColumnInfo(name = "dice2_amount")
    var dice2Amount: Int = 1,

    @ColumnInfo(name = "dice2_value")
    var dice2Value: Int = 2,

    @ColumnInfo(name = "dice2_bonus")
    var dice2Bonus: Int = 0,

    @ColumnInfo(name = "dice2_ability")
    var dice2Ability: Int = 0,

    /**
     * Number of uses for limited-use items.
     * EX: A bow with limited arrows
     * A value of -1 indicates infinite uses
     */
    @ColumnInfo(name = "uses")
    var uses: Int = -1,

    /**
     * Dictates behavior upon all uses consumed.
     * Refillable = false: Item removed from inventory
     * Refillable = true: Item just can't be used
     */
    @ColumnInfo(name = "refillable")
    var refillable: Boolean = false,

    /**
     * Amount of uses an item can be refilled to
     * If an item has a static number of uses, only fill out refillDiceBonus
     */
    @ColumnInfo(name = "refill_dice_amount")
    var refillDiceAmount: Int = 0,

    @ColumnInfo(name = "refill_dice_value")
    var refillDiceValue: Int = 2,

    @ColumnInfo(name = "refill_dice_bonus")
    var refillDiceBonus: Int = 0,

    @ColumnInfo(name = "refill_dice_ability")
    var refillDiceAbility: Int = 0

    // TODO: Icon?
)
