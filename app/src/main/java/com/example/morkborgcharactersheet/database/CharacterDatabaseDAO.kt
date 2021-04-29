package com.example.morkborgcharactersheet.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.morkborgcharactersheet.models.AbilityType

@Dao
interface CharacterDatabaseDAO {
    @Insert
    fun insertCharacter(character: Character): Long

    @Insert
    fun insertInventory(inventory: Inventory)

    @Update
    fun updateCharacter(character: Character)

    @Update
    fun updateInventory(inventory: Inventory)

    @Query("DELETE FROM characters")
    fun clearAllCharacters()

    @Query("DELETE FROM inventory WHERE character_id = :characterId")
    fun clearInventoryByCharacterId(characterId: Long)

    @Query("DELETE FROM characters WHERE characterId = :characterId")
    fun clearCharacter(characterId: Long)

    @Query("DELETE FROM inventory WHERE inventoryId = :inventoryId")
    fun clearSingleInventoryObject(inventoryId: Long)

    @Query("SELECT * FROM characters WHERE characterId = :characterId")
    fun getCharacter(characterId: Long): Character?

    @Query("SELECT name FROM characters WHERE characterId = :characterId")
    fun getCharactersName(characterId: Long): String?

    @Query("UPDATE characters SET hp_current = :newHp WHERE characterId = :characterId")
    fun setCharactersHP(characterId: Long, newHp: Int)

    @Query("UPDATE characters SET powers = :newPowers, omens = :newOmens WHERE characterId = :characterId")
    fun refreshPowersAndOmens(characterId: Long, newPowers: Int, newOmens: Int)

    @Query("SELECT * FROM inventory WHERE inventoryId = :inventoryId")
    fun getInventory(inventoryId: Long): Inventory?

    @Query("SELECT * FROM inventory WHERE character_id = :characterId")
    fun getInventoryForCharacter(characterId: Long): LiveData<List<Inventory>>

    @Query("SELECT * FROM inventory WHERE character_id = :characterId AND type = 1 AND equipped = 1")
    fun getEquippedWeapons(characterId: Long): LiveData<List<Inventory>>

    @Query("SELECT * FROM inventory WHERE character_id = :characterId AND type = 4 AND equipped = 1")
    fun getEquippedPowers(characterId: Long): LiveData<List<Inventory>>

    @Query("SELECT * FROM inventory WHERE character_id = :characterId AND type = 3 AND equipped = 1 LIMIT 1")
    fun getEquippedShield(characterId: Long): LiveData<Inventory>

    // Can only equip one shield at a time
    @Query("UPDATE inventory SET equipped = CASE WHEN inventoryId = :inventoryId THEN 1 ELSE 0 END WHERE character_id = :characterId AND type = 3")
    fun equipShield(characterId: Long, inventoryId: Long)

    @Query("SELECT * FROM inventory WHERE character_id = :characterId AND type = 2 AND equipped = 1 LIMIT 1")
    fun getEquippedArmor(characterId: Long): LiveData<Inventory>

    // Can only equip one piece of armor at a time
    @Query("UPDATE inventory SET equipped = CASE WHEN inventoryId = :inventoryId THEN 1 ELSE 0 END WHERE character_id = :characterId AND type = 2")
    fun equipArmor(characterId: Long, inventoryId: Long)

    @Query("SELECT characterId, name FROM characters WHERE characterId != :characterId")
    fun getCharacterList(characterId: Long): LiveData<List<ListedCharacter>>

    @Query("SELECT CASE WHEN :abilityType = 1 THEN strength WHEN :abilityType = 2 THEN agility WHEN :abilityType = 3 THEN presence WHEN :abilityType = 4 THEN toughness ELSE 0 END FROM characters WHERE characterId = :characterId LIMIT 1")
    fun getAbilityScoreForCharacter(characterId: Long, abilityType: Int): Int

    @Query("UPDATE characters SET silver = :amount WHERE characterId = :characterId")
    fun setSilver(characterId: Long, amount: Int)

    @Query("SELECT characterId FROM characters ORDER BY last_used DESC LIMIT 1")
    fun getMostRecentCharacterId(): Long?

    @Query("UPDATE characters SET last_used = CURRENT_TIMESTAMP WHERE characterId = :characterId")
    fun timestampCharacter(characterId: Long)
}