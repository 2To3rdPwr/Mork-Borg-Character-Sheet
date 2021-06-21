package com.example.morkborgcharactersheet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "characters")
data class Character (
    @PrimaryKey(autoGenerate = true)
    var characterId: Long = 0L,

    @ColumnInfo(name = "name")
    var characterName: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "hp_current")
    var currentHP: Int = 0,

    @ColumnInfo(name = "hp_max")
    var maxHP: Int = 0,

    @ColumnInfo(name = "strength")
    var strength: Int = 0,

    @ColumnInfo(name = "agility")
    var agility: Int = 0,

    @ColumnInfo(name = "presence")
    var presence: Int = 0,

    @ColumnInfo(name = "toughness")
    var toughness: Int = 0,

    @ColumnInfo(name = "omens")
    var omens: Int = 0,

    @ColumnInfo(name = "powers")
    var powers: Int = 0,

    @ColumnInfo(name = "silver")
    var silver: Int = 0,

    @ColumnInfo(name = "last_used")
    var lastUsed: Date? = Date()
)

/**
 * Data classes for queries that don't need to return an entire object
 */
data class ListedCharacter(
        val characterId: Int,
        val name: String
)
