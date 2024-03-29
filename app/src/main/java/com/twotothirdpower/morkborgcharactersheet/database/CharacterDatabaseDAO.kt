package com.twotothirdpower.morkborgcharactersheet.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CharacterDatabaseDAO {
    @Insert
    fun insertCharacter(character: Character): Long

    @Insert
    fun insertInventory(inventory: Inventory): Long

    @Insert
    fun insertInventoryJoin(characterInventoryJoin: CharacterInventoryJoin)

    @Update
    fun updateCharacter(character: Character)

    @Update
    fun updateInventory(inventory: Inventory)

    @Update
    fun updateInventoryJoin(characterInventoryJoin: CharacterInventoryJoin)

    @Query("DELETE FROM characters WHERE characterId = :characterId")
    fun clearCharacter(characterId: Long)

    @Query("DELETE FROM inventory WHERE inventoryId = :inventoryId")
    fun clearInventory(inventoryId: Long)

    @Query("DELETE FROM character_inventory_join WHERE characterInventoryJoinId = :joinId")
    fun clearEquipment(joinId: Long)

    @Transaction
    @Query("SELECT * FROM characters WHERE characterId = :characterId")
    fun getCharacter(characterId: Long): Character?

    @Transaction
    @Query("SELECT name FROM characters WHERE characterId = :characterId")
    fun getCharactersName(characterId: Long): String?

    @Query("UPDATE characters SET hp_current = :newHp WHERE characterId = :characterId")
    fun setCharactersHP(characterId: Long, newHp: Int)

    @Query("UPDATE characters SET powers = :newPowers, omens = :newOmens WHERE characterId = :characterId")
    fun refreshPowersAndOmens(characterId: Long, newPowers: Int, newOmens: Int)

    @Transaction
    @Query("SELECT * FROM inventory WHERE inventoryId = :inventoryId")
    fun getInventory(inventoryId: Long): Inventory?

    @Transaction
    @Query("SELECT * FROM inventory WHERE default_item = 1")
    fun getDefaultInventory(): List<Inventory>

    @Transaction
    @Query("SELECT * FROM character_inventory_join WHERE characterInventoryJoinId = :invJoinId")
    fun getEquipment(invJoinId: Long): EquipmentData?

    @Transaction
    @Query("SELECT * FROM character_inventory_join WHERE character_id = :characterId")
    fun getCharactersEquipment(characterId: Long): List<EquipmentData>

    @Transaction
    @Query("SELECT * FROM character_inventory_join WHERE character_id = :characterId AND inventory_id = :inventoryId")
    fun getCharactersEquipmentByType(characterId: Long, inventoryId: Long): CharacterInventoryJoin?

    // Can't seem to get cross-table joins working for update statements, so I'll have to handle the
    //  more complex queries manually
    @Transaction
    @Query("SELECT characterInventoryJoinId FROM character_inventory_join JOIN inventory ON inventory_id = inventoryId WHERE character_id = :characterId AND type = :type AND equipped = 1")
    fun getEquippedByType(characterId: Long, type: Int): List<Long>

    @Query("UPDATE character_inventory_join SET equipped = 1 WHERE characterInventoryJoinId = :joinId")
    fun equipItem(joinId: Long)

    @Query("UPDATE character_inventory_join SET equipped = 0 WHERE characterInventoryJoinId IN (:itemsToUnequip)")
    fun unequipMultipleItems(itemsToUnequip: List<Long>)

    @Query("UPDATE character_inventory_join SET equipped = 0 WHERE inventory_id = :inventoryId AND character_id = :characterId")
    fun breakEquipment(characterId: Long, inventoryId: Long)

    @Transaction
    @Query("SELECT characterId, name FROM characters WHERE characterId != :characterId")
    fun getCharacterList(characterId: Long): LiveData<List<ListedCharacter>>

    @Transaction
    @Query("SELECT CASE WHEN :abilityType = 1 THEN strength WHEN :abilityType = 2 THEN agility WHEN :abilityType = 3 THEN presence WHEN :abilityType = 4 THEN toughness ELSE 0 END FROM characters WHERE characterId = :characterId LIMIT 1")
    fun getAbilityScoreForCharacter(characterId: Long, abilityType: Int): Int

    @Query("UPDATE characters SET silver = :amount WHERE characterId = :characterId")
    fun setSilver(characterId: Long, amount: Int)

    @Transaction
    @Query("SELECT silver FROM characters WHERE characterId = :characterId")
    fun getSilver(characterId: Long): Int?

    @Transaction
    @Query("SELECT characterId FROM characters ORDER BY last_used DESC LIMIT 1")
    fun getMostRecentCharacterId(): Long?
}