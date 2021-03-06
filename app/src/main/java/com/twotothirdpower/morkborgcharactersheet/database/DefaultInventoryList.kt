package com.twotothirdpower.morkborgcharactersheet.database

import android.content.Context
import com.twotothirdpower.morkborgcharactersheet.R

class DefaultInventoryList(val context: Context) {
    val defaultInventoryList = listOf<Inventory>(
        // Weapons
        Inventory(inventoryId = 1L, name = context.getString(R.string.femur_weapon_name), type = 1, ability = 1, dice1Value = 4, defaultItem = true),
        Inventory(inventoryId = 2L, name = context.getString(R.string.staff_weapon_name), type = 1, ability = 1, dice1Value = 4, silver = 5, defaultItem = true),
        Inventory(inventoryId = 3L, name = context.getString(R.string.shortsword_weapon_name), type = 1, ability = 1, dice1Value = 4, silver = 20, defaultItem = true),
        Inventory(inventoryId = 4L, name = context.getString(R.string.knife_weapon_name), type = 1, ability = 1, dice1Value = 4, silver = 10, defaultItem = true),
        Inventory(inventoryId = 5L, name = context.getString(R.string.warhammer_weapon_name), type = 1, ability = 1, dice1Value = 6, silver = 30, defaultItem = true),
        Inventory(inventoryId = 6L, name = context.getString(R.string.sword_weapon_name), type = 1, ability = 1, dice1Value = 6, silver = 30, defaultItem = true),
        Inventory(inventoryId = 7L, name = context.getString(R.string.bow_weapon_name), type = 1, ability = 3, dice1Value = 6, limitedUses = true, initialUsesDiceBonus = 10, initialUsesDiceAbility = 3, silver = 25, defaultItem = true),
        Inventory(inventoryId = 8L, name = context.getString(R.string.flail_weapon_name), type = 1, ability = 1, dice1Value = 8, silver = 35, defaultItem = true),
        Inventory(inventoryId = 9L, name = context.getString(R.string.crossbow_weapon_name), type = 1, ability = 3, dice1Value = 8, limitedUses = true, initialUsesDiceBonus = 10, initialUsesDiceAbility = 3, silver = 40, defaultItem = true),
        Inventory(inventoryId = 10L, name = context.getString(R.string.zweihander_weapon_name), description = context.getString(R.string.zweihander_weapon_description), type = 1, ability = 1, dice1Value = 10, silver = 60, defaultItem = true),
        // Scrolls
        Inventory(inventoryId = 11L, name = context.getString(R.string.fireball_scroll_name), description = context.getString(R.string.fireball_scroll_description), type = 4, dice1Value = 2, dice2Value = 8, silver = 50, defaultItem = true),
        Inventory(inventoryId = 12L, name = context.getString(R.string.confusion_scroll_name), description = context.getString(R.string.confusion_scroll_description), type = 4, silver = 50, defaultItem = true),
        Inventory(inventoryId = 13L, name = context.getString(R.string.telekinesis_scroll_name), description = context.getString(R.string.telekinesis_scroll_description), type = 4, dice1Value = 10, dice2Value = 6, silver = 50, defaultItem = true),
        Inventory(inventoryId = 14L, name = context.getString(R.string.levetation_scroll_name), description = context.getString(R.string.levetation_scroll_description), type = 4, dice1Value = 10, dice1Ability = 3, silver = 50, defaultItem = true),
        Inventory(inventoryId = 15L, name = context.getString(R.string.suffocation_scroll_name), description = context.getString(R.string.suffocation_scroll_description), type = 4, dice1Value = 6, silver = 50, defaultItem = true),
        Inventory(inventoryId = 16L, name = context.getString(R.string.lightning_scroll_name), description = context.getString(R.string.lightning_scroll_description), type = 4, dice1Value = 4, dice2Value = 6, silver = 50, defaultItem = true),
        Inventory(inventoryId = 17L, name = context.getString(R.string.summon_scroll_name), description = context.getString(R.string.summon_scroll_description), type = 4, dice1Value = 6, dice2Value = 4, silver = 50, defaultItem = true),
        Inventory(inventoryId = 18L, name = context.getString(R.string.invisibility_scroll_name), description = context.getString(R.string.invisibility_scroll_description), type = 4, dice1Value = 6, silver = 50, defaultItem = true),
        Inventory(inventoryId = 19L, name = context.getString(R.string.sleep_scroll_name), description = context.getString(R.string.sleep_scroll_description), type = 4, dice1Value = 4, silver = 50, defaultItem = true),
        Inventory(inventoryId = 20L, name = context.getString(R.string.death_scroll_name), description = context.getString(R.string.death_scroll_description), type = 4, dice1Amount = 4, dice1Value = 10, silver = 50, defaultItem = true),
        Inventory(inventoryId = 21L, name = context.getString(R.string.heal_scroll_name), description = context.getString(R.string.heal_scroll_description), type = 4, dice1Value = 2, dice2Value = 10, silver = 50, defaultItem = true),
        Inventory(inventoryId = 22L, name = context.getString(R.string.bonus_scroll_name), description = context.getString(R.string.bonus_scroll_description), type = 4, dice1Value = 6, silver = 50, defaultItem = true),
        Inventory(inventoryId = 23L, name = context.getString(R.string.deadspeak_scroll_name), description = context.getString(R.string.deadspeak_scroll_description), type = 4, silver = 50, defaultItem = true),
        Inventory(inventoryId = 24L, name = context.getString(R.string.temphealth_scroll_name), description = context.getString(R.string.temphealth_scroll_description), type = 4, dice1Amount = 2, dice1Value = 6, silver = 50, defaultItem = true),
        Inventory(inventoryId = 25L, name = context.getString(R.string.revive_scroll_name), description = context.getString(R.string.revive_scroll_description), type = 4, silver = 50, defaultItem = true),
        Inventory(inventoryId = 26L, name = context.getString(R.string.animalspeak_scroll_name), description = context.getString(R.string.animalspeak_scroll_description), type = 4, dice1Value = 20, silver = 50, defaultItem = true),
        Inventory(inventoryId = 27L, name = context.getString(R.string.light_scroll_name), description = context.getString(R.string.light_scroll_description), type = 4, dice1Amount = 3, dice1Value = 10, silver = 50, defaultItem = true),
        Inventory(inventoryId = 28L, name = context.getString(R.string.trapfind_scroll_name), description = context.getString(R.string.trapfind_scroll_description), type = 4, dice1Amount = 2, dice1Value = 10, silver = 50, defaultItem = true),
        Inventory(inventoryId = 29L, name = context.getString(R.string.glare_scroll_name), description = context.getString(R.string.glare_scroll_description), type = 4, dice1Value = 4, dice2Value = 8, silver = 50, defaultItem = true),
        Inventory(inventoryId = 30L, name = context.getString(R.string.command_scroll_name), description = context.getString(R.string.command_scroll_description), type = 4, silver = 50, defaultItem = true),
        // Armor
        Inventory(inventoryId = 31L, name = context.getString(R.string.light_armor_string), type = 2, armorTier = 1, silver = 20, defaultItem = true),
        Inventory(inventoryId = 32L, name = context.getString(R.string.medium_armor_string), type = 2, armorTier = 2, silver = 100, defaultItem = true),
        Inventory(inventoryId = 33L, name = context.getString(R.string.heavy_armor_string), type = 2, armorTier = 3, silver = 200, defaultItem = true),
        // Shield
        Inventory(inventoryId = 34L, name = context.getString(R.string.shield_string), type = 3, silver = 20, defaultItem = true),
        // Animals
        Inventory(inventoryId = 35L, name = context.getString(R.string.dog_trained_name), description = context.getString(R.string.dog_trained_description), dice1Value = 6, dice1Bonus = 2, dice2Value = 4, silver = 25, defaultItem = true),
        Inventory(inventoryId = 36L, name = context.getString(R.string.dog_wild_name), description = context.getString(R.string.dog_wild_description), dice1Value = 6, dice1Bonus = 2, dice2Value = 4, silver = 10, defaultItem = true),
        Inventory(inventoryId = 37L, name = context.getString(R.string.horse_name), silver = 80, defaultItem = true),
        Inventory(inventoryId = 38L, name = context.getString(R.string.mule_name), silver = 10, defaultItem = true),
        Inventory(inventoryId = 39L, name = context.getString(R.string.rat_name), silver = 8, defaultItem = true),
        Inventory(inventoryId = 40L, name = context.getString(R.string.monkey_name), description = context.getString(R.string.monkey_description), dice1Value = 4, dice1Bonus = 2, dice2Value = 4, limitedUses = true, initialUsesDiceValue = 4, defaultItem = true),
        // Miscelaneous
        Inventory(inventoryId = 41L, name = context.getString(R.string.backpack_name), description = context.getString(R.string.backpack_description), silver = 6, defaultItem = true),
        Inventory(inventoryId = 42L, name = context.getString(R.string.beartrap_name), description = context.getString(R.string.beartrap_description), dice1Value = 8, silver = 20, defaultItem = true),
        Inventory(inventoryId = 43L, name = context.getString(R.string.blanket_name), silver = 4, defaultItem = true),
        Inventory(inventoryId = 44L, name = context.getString(R.string.caltrops_name), description = context.getString(R.string.caltrops_description), dice1Value = 4, dice2Value = 6, silver = 7, defaultItem = true),
        Inventory(inventoryId = 45L, name = context.getString(R.string.chalk_name), silver = 1, defaultItem = true),
        Inventory(inventoryId = 46L, name = context.getString(R.string.tobacco_name), silver = 1, defaultItem = true),
        Inventory(inventoryId = 47L, name = context.getString(R.string.crowbar_name), silver = 8, defaultItem = true),
        Inventory(inventoryId = 48L, name = context.getString(R.string.silver_crucifix_name), silver = 60, defaultItem = true),
        Inventory(inventoryId = 49L, name = context.getString(R.string.wood_crucifix_name), silver = 8, defaultItem = true),
        Inventory(inventoryId = 50L, name = context.getString(R.string.food_ration_name), description = context.getString(R.string.food_ration_description), limitedUses = true, initialUsesDiceBonus = 1, silver = 1, defaultItem = true),
        Inventory(inventoryId = 51L, name = context.getString(R.string.perfume_name), silver = 25, defaultItem = true),
        Inventory(inventoryId = 52L, name = context.getString(R.string.firestarter_name), silver = 4, defaultItem = true),
        Inventory(inventoryId = 53L, name = context.getString(R.string.grappling_hook_name), silver = 12, defaultItem = true),
        Inventory(inventoryId = 54L, name = context.getString(R.string.hammer_name), silver = 8, defaultItem = true),
        Inventory(inventoryId = 55L, name = context.getString(R.string.chain_name), description = context.getString(R.string.unit_length_string), limitedUses = true, initialUsesDiceBonus = 15, silver = 10, defaultItem = true),
        Inventory(inventoryId = 56L, name = context.getString(R.string.nails_name), limitedUses = true, initialUsesDiceBonus = 10, silver = 5, defaultItem = true),
        Inventory(inventoryId = 57L, name = context.getString(R.string.ladder_name), silver = 7, defaultItem = true),
        Inventory(inventoryId = 58L, name = context.getString(R.string.oil_name), description = context.getString(R.string.oil_description), limitedUses = true, initialUsesDiceBonus = 6, initialUsesDiceAbility = 3, silver = 5, defaultItem = true),
        Inventory(inventoryId = 59L, name = context.getString(R.string.lard_name), description = context.getString(R.string.lard_description), limitedUses = true, initialUsesDiceBonus = 5, silver = 5, defaultItem = true),
        Inventory(inventoryId = 60L, name = context.getString(R.string.hook_name), silver = 9, defaultItem = true),
        Inventory(inventoryId = 61L, name = context.getString(R.string.lockpick_name), silver = 5, defaultItem = true),
        Inventory(inventoryId = 62L, name = context.getString(R.string.magnesium_name), silver = 4, defaultItem = true),
        Inventory(inventoryId = 63L, name = context.getString(R.string.manacles_name), silver = 10, defaultItem = true),
        Inventory(inventoryId = 64L, name = context.getString(R.string.mattress_name), silver = 3, defaultItem = true),
        Inventory(inventoryId = 65L, name = context.getString(R.string.cleaver_name), silver = 15, defaultItem = true),
        Inventory(inventoryId = 66L, name = context.getString(R.string.healthkit_name), description = context.getString(R.string.healthkit_description), dice1Value = 6, limitedUses = true, initialUsesDiceBonus = 4, initialUsesDiceAbility = 3, silver = 15, defaultItem = true),
        Inventory(inventoryId = 67L, name = context.getString(R.string.file_name), silver = 10, defaultItem = true),
        Inventory(inventoryId = 68L, name = context.getString(R.string.mirror_name), silver = 15, defaultItem = true),
        Inventory(inventoryId = 69L, name = context.getString(R.string.muzzle_name), silver = 6, defaultItem = true),
        Inventory(inventoryId = 70L, name = context.getString(R.string.noose_name), silver = 5, defaultItem = true),
        Inventory(inventoryId = 71L, name = context.getString(R.string.lamp_name), silver = 10, defaultItem = true),
        Inventory(inventoryId = 72L, name = context.getString(R.string.poison_black_name), description = context.getString(R.string.poison_black_description), dice1Value = 6, limitedUses = true, initialUsesDiceBonus = 3, silver = 20, defaultItem = true),
        Inventory(inventoryId = 73L, name = context.getString(R.string.poison_red_name), description = context.getString(R.string.poison_red_description), dice1Value = 10, limitedUses = true, initialUsesDiceBonus = 3, silver = 20, defaultItem = true),
        Inventory(inventoryId = 74L, name = context.getString(R.string.corpse_name), defaultItem = true),
        Inventory(inventoryId = 75L, name = context.getString(R.string.rope_name), description = context.getString(R.string.unit_length_string), limitedUses = true, initialUsesDiceBonus = 30, silver = 4, defaultItem = true),
        Inventory(inventoryId = 76L, name = context.getString(R.string.wagon_name), silver = 25, defaultItem = true),
        Inventory(inventoryId = 77L, name = context.getString(R.string.tent_name), silver = 12, defaultItem = true),
        Inventory(inventoryId = 78L, name = context.getString(R.string.toolbox_name), description = context.getString(R.string.toolbox_description), silver = 20, defaultItem = true),
        Inventory(inventoryId = 79L, name = context.getString(R.string.torch_name), limitedUses = true, initialUsesDiceBonus = 4, initialUsesDiceAbility = 3, silver = 2, defaultItem = true),
        Inventory(inventoryId = 80L, name = context.getString(R.string.sack_name), description = context.getString(R.string.sack_description), silver = 3, defaultItem = true),
        Inventory(inventoryId = 81L, name = context.getString(R.string.salt_name), silver = 4, defaultItem = true),
        Inventory(inventoryId = 82L, name = context.getString(R.string.scissors_name), silver = 9, defaultItem = true),
        Inventory(inventoryId = 83L, name = context.getString(R.string.needle_name), silver = 3, defaultItem = true),
        Inventory(inventoryId = 84L, name = context.getString(R.string.waterskin_name), limitedUses = true, initialUsesDiceBonus = 4, silver = 4, defaultItem = true),
        Inventory(inventoryId = 85L, name = context.getString(R.string.bomb_name), description = context.getString(R.string.bomb_description), dice1Value = 10, limitedUses = true, initialUsesDiceBonus = 1, defaultItem = true),
        Inventory(inventoryId = 86L, name = context.getString(R.string.elixir_name), description = context.getString(R.string.elixir_description), dice1Value = 6, limitedUses = true, initialUsesDiceValue = 4, defaultItem = true),
        Inventory(inventoryId = 87L, name = context.getString(R.string.arrows_name), limitedUses = true, initialUsesDiceBonus = 20, silver = 10, defaultItem = true),
        Inventory(inventoryId = 88L, name = context.getString(R.string.bolts_name), limitedUses = true, initialUsesDiceBonus = 10, silver = 10, defaultItem = true)
    )
}