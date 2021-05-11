package com.example.morkborgcharactersheet.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_inventory_join", primaryKeys = ["character_id", "inventory_id"])
data class CharacterInventoryJoin(
//    @PrimaryKey(autoGenerate = true)
//    val characterInventoryJoinId: Long = 0L,

    @ColumnInfo(name = "character_id")
    val characterId: Long,

    @ColumnInfo(name = "inventory_id")
    val inventoryId: Long,

    @ColumnInfo(name = "equipped")
    var equipped: Boolean = false,

    @ColumnInfo(name="uses")
    var uses: Int = 0,

    @ColumnInfo(name = "broken")
    var broken: Boolean = false
)