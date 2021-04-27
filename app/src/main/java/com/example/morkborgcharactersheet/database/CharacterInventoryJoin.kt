package com.example.morkborgcharactersheet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_inventory_join")
data class CharacterInventoryJoin(
    @ColumnInfo(name = "character_id")
    val characterId: Long,

    @ColumnInfo(name = "inventory_id")
    val inventoryId: Long
)