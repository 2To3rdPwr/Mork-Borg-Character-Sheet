package com.twotothirdpower.morkborgcharactersheet.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twotothirdpower.morkborgcharactersheet.database.Character
import com.twotothirdpower.morkborgcharactersheet.database.CharacterDatabaseDAO
import com.twotothirdpower.morkborgcharactersheet.database.CharacterInventoryJoin
import com.twotothirdpower.morkborgcharactersheet.models.Dice
import com.twotothirdpower.morkborgcharactersheet.models.DiceValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

// Not really intro. More intro to new character creation.
class IntroFragmentViewModel (dataSource: CharacterDatabaseDAO) : ViewModel() {
    val database = dataSource

    private val _newCharacterId = MutableLiveData<Long>()
    val newCharacterId: LiveData<Long>
        get() = _newCharacterId

    private var str: Int = 0
    private var agl: Int = 0
    private var pres: Int = 0
    private var tgh: Int = 0

    /**
     * Event LiveData
     */
    private val _autoGenerateEvent = MutableLiveData<Boolean>()
    val autoGenerateEvent: LiveData<Boolean>
        get() = _autoGenerateEvent
    fun onAutoGenerateComplete() {
        _autoGenerateEvent.value = false
    }

    private val _manualGenerateEvent = MutableLiveData<Boolean>()
    val manualGenerateEvent: LiveData<Boolean>
        get() = _manualGenerateEvent
    fun onManualGenerateComplete() {
        _manualGenerateEvent.value = false
    }

    /**
     * FE Functions
     */

    fun onManualGenerateClicked() {
        _manualGenerateEvent.value = true
    }

    fun generateNewCharacter(names: Array<String>, backstories: Array<String>, traits: Array<String>, quirks: Array<String>, appearances: Array<String>) {
        // Pass arrays of strings up from fragment
        // resources

        val charName = names[Random.nextInt(names.size)]
        var charDescription = backstories[Random.nextInt(backstories.size)] + "\n" + traits[Random.nextInt(traits.size)] + " " + quirks[Random.nextInt(quirks.size)] + "\n" + appearances[Random.nextInt(appearances.size)]
        charDescription = charDescription.replace("\$", charName)
        val str = rollAbilityScore()
        val agl = rollAbilityScore()
        val pres = rollAbilityScore()
        val tgh = rollAbilityScore()
        val hp = Math.max(Dice(diceValue = DiceValue.D8).roll(tgh), 1)
        val powers = Math.max(Dice(diceValue = DiceValue.D4).roll(pres), 0)

        viewModelScope.launch {
            val newCharacter = Character(characterName = charName, description = charDescription, silver = Dice(2, DiceValue.D6).roll() * 10,
                currentHP = hp, maxHP = hp, omens = Dice(diceValue = DiceValue.D2).roll(), powers = powers,
                strength = str, presence = pres, agility = agl, toughness = tgh)
            _newCharacterId.value = saveNewCharacter(newCharacter)

            // Default equipment.
            // Long values here correspond to InventoryIds set in DefaultInventoryList
            val characterId = newCharacterId.value ?: throw IllegalArgumentException("Illegal characterid")
            var startingScroll = false

            val gear1 = when(Random.nextInt(6)) {
                // Backpack
                2 -> 41L
                // Sack
                3 -> 80L
                // Small Wagon
                4 -> 76L
                //Mule
                5 -> 38L
                else -> null
            }

            var gear2b: Long? = null
            val gear2 = when(Random.nextInt(12)) {
                // Rope
                0 -> 75L
                // Torches
                1 -> 79L
                // Lantern (Includes oil 58L)
                2 -> {
                    gear2b = 58L
                    71L
                }
                // Magnesium Strip
                3 -> 62L
                // Unclean Scroll
                4 -> {
                    startingScroll = true
                    when(Random.nextInt(10)) {
                        0 -> 11L
                        1 -> 12L
                        2 -> 13L
                        3 -> 14L
                        4 -> 15L
                        5 -> 16L
                        6 -> 17L
                        7 -> 18L
                        8 -> 19L
                        9 -> 20L
                        else -> throw IllegalStateException("Not possible")
                    }
                }
                // Sharp Needle
                5 -> 83L
                // MedKit
                6 -> 66L
                // Metal File and Lockpicks (67L)
                7 -> {
                    gear2b = 67L
                    61L
                }
                // Bear Trap
                8 -> 42L
                // Bomb
                9 -> 85L
                // Used Red Poison
                10 -> 73L
                // Silver Crucifix
                11 -> 48L
                else -> null
            }

            val gear3 = when(Random.nextInt(12)) {
                // Life Elixir
                0 -> 86L
                // Sacred Scroll
                1 -> {
                    startingScroll = true
                    when(Random.nextInt(10)) {
                        0 -> 21L
                        1 -> 22L
                        2 -> 23L
                        3 -> 24L
                        4 -> 25L
                        5 -> 26L
                        6 -> 27L
                        7 -> 28L
                        8 -> 29L
                        9 -> 30L
                        else -> throw IllegalStateException("Not possible")
                    }
                }
                // Trained Dog
                2 -> 35L
                // 1D4 monkeys
                3 -> 40L
                // Exquisite Perfume
                4 -> 51L
                // Toolbox
                5 -> 78L
                // Heavy Chain
                6 -> 55L
                //Grappling Hook
                7 -> 53L
                //Shield
                8 -> 34L
                // Crowbar
                9 -> 47L
                // Lard
                10 -> 59L
                // Tent
                11 -> 77L
                else -> null
            }

            val weapon = when(Random.nextInt(if (startingScroll) 6 else 10)) {
                // Femur
                0 -> 1L
                // Staff
                1 -> 2L
                // Shortsword
                2 -> 3L
                // Knife
                3 -> 4L
                // Warhammer
                4 -> 5L
                // Sword
                5 -> 6L
                // Bow
                6 -> 7L
                // Flail
                7 -> 8L
                // Crossbow
                8 -> 9L
                // Zweihander
                9 -> 10L
                else -> null
            }

            val armor = when(Random.nextInt(if (startingScroll) 2 else 4)) {
                1 -> 31L
                2 -> 32L
                3 -> 33L
                else -> null
            }

            if (gear1 != null) {
                saveEquipment(createInvJoin(characterId, gear1))
            }
            if (gear2 != null) {
                saveEquipment(createInvJoin(characterId, gear2))
            }
            if (gear2b != null) {
                saveEquipment(createInvJoin(characterId, gear2b))
            }
            if (gear3 != null) {
                saveEquipment(createInvJoin(characterId, gear3))
            }
            if (weapon != null) {
                saveEquipment(createInvJoin(characterId, weapon))
            }
            if (armor != null) {
                saveEquipment(createInvJoin(characterId, armor))
            }

            _autoGenerateEvent.value = true
        }
    }

    private suspend fun saveNewCharacter(character: Character): Long {
        return withContext(Dispatchers.IO) {
            database.insertCharacter(character)
        }
    }

    private suspend fun saveEquipment(equipment: CharacterInventoryJoin) {
        withContext(Dispatchers.IO) {
            database.insertInventoryJoin(equipment)        }
    }

    private suspend fun createInvJoin(characterId: Long, inventoryId: Long): CharacterInventoryJoin {
        val gear = CharacterInventoryJoin(characterId = characterId, inventoryId = inventoryId)

        return withContext(Dispatchers.IO) {
            val inventory = database.getInventory(inventoryId) ?: throw IllegalArgumentException("Invalid Inventory Id")

            // Only equip weapons, armor, shield, power
            gear.equipped = inventory.type == 1 || inventory.type == 2 || inventory.type == 3 || inventory.type == 4

            if (inventory.limitedUses) {
                val abilityBonus = when (inventory.InitialUsesDiceAbility) {
                    1 -> str
                    2 -> agl
                    3 -> pres
                    4 -> tgh
                    else -> 0
                }
                val uses = Dice(amount = inventory.InitialUsesDiceAmount, diceValue = DiceValue.getByValue(inventory.InitialUsesDiceValue)?:DiceValue.D0, bonus = inventory.InitialUsesDiceBonus).roll(abilityBonus)
                gear.uses = uses
            }
            gear
        }
    }

    private fun rollAbilityScore(): Int {
        val abilityScore = Dice(3, DiceValue.D6).roll()
        return when (abilityScore) {
            in 1..4 -> -3
            in 5..6 -> -2
            in 7..8 -> -1
            in 9..12 -> 0
            in 13..14 -> 1
            in 15..16 -> 2
            else -> 3
        }
    }
}