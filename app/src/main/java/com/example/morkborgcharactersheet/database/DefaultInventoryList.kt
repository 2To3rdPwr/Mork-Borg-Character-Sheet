package com.example.morkborgcharactersheet.database

import android.content.Context
import com.example.morkborgcharactersheet.R

class DefaultInventoryList(val context: Context) {
    val defaultInventoryList = listOf<Inventory>(
        Inventory(name = context.getString(R.string.femur_weapon_name), type = 1, ability = 1, dice1Amount = 1, dice1Value = 4, defaultItem = true),
        Inventory(name = context.getString(R.string.staff_weapon_name), type = 1, ability = 1, dice1Amount = 1, dice1Value = 4, defaultItem = true),
        Inventory(name = context.getString(R.string.shortsword_weapon_name), type = 1, ability = 1, dice1Amount = 1, dice1Value = 4, defaultItem = true),
        Inventory(name = context.getString(R.string.knife_weapon_name), type = 1, ability = 1, dice1Amount = 1, dice1Value = 4, defaultItem = true),
        Inventory(name = context.getString(R.string.warhammer_weapon_name), type = 1, ability = 1, dice1Amount = 1, dice1Value = 6, defaultItem = true),
        Inventory(name = context.getString(R.string.sword_weapon_name), type = 1, ability = 1, dice1Amount = 1, dice1Value = 6, defaultItem = true),
        Inventory(name = context.getString(R.string.bow_weapon_name), type = 1, ability = 3, dice1Amount = 1, dice1Value = 6, limitedUses = true, refillable = true, InitialUsesDiceBonus = 10, InitialUsesDiceAbility = 3, defaultItem = true),
        Inventory(name = context.getString(R.string.flail_weapon_name), type = 1, ability = 1, dice1Amount = 1, dice1Value = 8, defaultItem = true),
        Inventory(name = context.getString(R.string.crossbow_weapon_name), type = 1, ability = 3, dice1Amount = 1, dice1Value = 8, limitedUses = true, refillable = true, InitialUsesDiceBonus = 10, InitialUsesDiceAbility = 3, defaultItem = true),
        Inventory(name = context.getString(R.string.zweihander_weapon_name), description = context.getString(R.string.zweihander_weapon_description), type = 1, ability = 1, dice1Amount = 1, dice1Value = 10, defaultItem = true)
    )
}