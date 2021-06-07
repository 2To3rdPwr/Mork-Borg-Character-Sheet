package com.example.morkborgcharactersheet.database

import androidx.room.*

@Entity(tableName = "character_inventory_join")
data class CharacterInventoryJoin (
    @PrimaryKey(autoGenerate = true)
    val characterInventoryJoinId: Long = 0L,

    @ColumnInfo(name = "character_id")
    var characterId: Long,

    @ColumnInfo(name = "inventory_id")
    var inventoryId: Long,

    @ColumnInfo(name = "equipped")
    var equipped: Boolean = false,

    @ColumnInfo(name="uses")
    var uses: Int = 0,

    @ColumnInfo(name = "broken")
    var broken: Boolean = false
)

data class EquipmentData (
    @Embedded
    val inventoryJoin: CharacterInventoryJoin,
    @Relation (
        parentColumn = "inventory_id",
        entityColumn = "inventoryId"
    )
    val inventory: Inventory
)