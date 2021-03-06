package com.twotothirdpower.morkborgcharactersheet.util

import android.text.TextUtils
import androidx.databinding.InverseMethod
import com.twotothirdpower.morkborgcharactersheet.R
import com.twotothirdpower.morkborgcharactersheet.models.AbilityType
import com.twotothirdpower.morkborgcharactersheet.models.ArmorTier
import com.twotothirdpower.morkborgcharactersheet.models.DiceValue
import com.twotothirdpower.morkborgcharactersheet.models.ItemType

class DataBindingConverter {
    companion object {
        /**
         * One-Way bindings
         */
        @JvmStatic
        fun convertItemTypeToImage(type: ItemType?): Int {
            return when(type) {
                ItemType.WEAPON -> R.drawable.sword
                ItemType.ARMOR -> R.drawable.armor
                ItemType.SHIELD -> R.drawable.shield
                ItemType.POWER -> R.drawable.ancient_scroll_2
                ItemType.OTHER -> R.drawable.potion
                else -> R.drawable.broken
            }
        }

        /**
         * Two-way bindings
         */
        /**
         * Parse strings to ints and vice-versa for data binding purposes, allowing for negative values.
         *
         * It's typically better to bind an edittext that can potentially be negative to a string
         *  LiveData instead of using this due to quirks with input reading - as null and thus
         *  converting it to emptystring unless it's already a valid int
         */
        @InverseMethod("convertStringToInt")
        @JvmStatic
        fun convertIntToString(value: Int?): String {
            return value?.toString() ?: ""
        }

        @JvmStatic
        fun convertStringToInt(value: String): Int? {
            if (TextUtils.isEmpty(value) || !value.matches(Regex("-?\\d+"))) {
                return null
            }
            return value.toIntOrNull()
        }

        /**
         * Parse strings to ints and vice-versa for data binding purposes, allowing for negative values.
         * Default to 0 if passed an invalid value
         *
         * It's typically better to bind an edittext that can potentially be negative to a string
         *  LiveData instead of using this due to quirks with input reading - as null and thus
         *  converting it to 0 unless it's already a valid int
         */
        @InverseMethod("convertStringToIntNotNull")
        @JvmStatic
        fun convertIntToStringNotNull(value: Int): String {
            return value?.toString() ?: ""
        }

        @JvmStatic
        fun convertStringToIntNotNull(value: String): Int {
            if (TextUtils.isEmpty(value) || !value.matches(Regex("-?\\d+"))) {
                return 0
            }
            return value.toIntOrNull() ?: 0
        }

        @InverseMethod("convertSpinnerPositionToDiceValue")
        @JvmStatic
        fun convertDiceValueToSpinnerPosition(diceValue: DiceValue?): Int {
            return diceValue?.ordinal ?: 0
        }

        @JvmStatic
        fun convertSpinnerPositionToDiceValue(position: Int): DiceValue? {
            return DiceValue.get(position)
        }

        @InverseMethod("convertSpinnerPositionToAbilityType")
        @JvmStatic
        fun convertAbilityTypeToSpinnerPosition(abilityType: AbilityType?): Int {
            return abilityType?.ordinal ?: 0
        }

        @JvmStatic
        fun convertSpinnerPositionToAbilityType(position: Int): AbilityType? {
            return AbilityType.get(position)
        }

        /**
         * Begin editInventory-Specific conversions.
         * Could honestly probably extract them into their own conversion class
         */
        @InverseMethod("convertRadioButtonToItemType")
        @JvmStatic
        fun convertItemTypeToRadioButton(type: ItemType?): Int {
            return when (type) {
                ItemType.WEAPON -> R.id.fragment_item_new_weapon_radio_button
                ItemType.POWER -> R.id.fragment_item_new_power_radio_button
                ItemType.ARMOR -> R.id.fragment_item_new_armor_radio_button
                ItemType.SHIELD -> R.id.fragment_item_new_shield_radio_button
                ItemType.OTHER -> R.id.fragment_item_new_other_radio_button
                else -> -1
            }
        }

        @JvmStatic
        fun convertRadioButtonToItemType(button: Int): ItemType {
            return when (button) {
                R.id.fragment_item_new_weapon_radio_button -> ItemType.WEAPON
                R.id.fragment_item_new_power_radio_button -> ItemType.POWER
                R.id.fragment_item_new_armor_radio_button -> ItemType.ARMOR
                R.id.fragment_item_new_shield_radio_button -> ItemType.SHIELD
                R.id.fragment_item_new_other_radio_button -> ItemType.OTHER
                else -> ItemType.OTHER
            }
        }

        @InverseMethod("convertRadioButtonToAbilityType")
        @JvmStatic
        fun convertAbilityTypeToRadioButton(type: AbilityType?): Int {
            return when (type) {
                AbilityType.STRENGTH -> R.id.fragment_item_new_strength_radio_button
                AbilityType.PRESENCE -> R.id.fragment_item_new_presence_radio_button
                AbilityType.UNTYPED -> -1
                else -> -1
            }
        }

        @JvmStatic
        fun convertRadioButtonToAbilityType(button: Int): AbilityType {
            return when (button) {
                R.id.fragment_item_new_strength_radio_button -> AbilityType.STRENGTH
                R.id.fragment_item_new_presence_radio_button -> AbilityType.PRESENCE
                else -> AbilityType.UNTYPED
            }
        }

        @InverseMethod("convertRadioButtonToArmorTier")
        @JvmStatic
        fun convertArmorTierToRadioButton(type: ArmorTier?): Int {
            return when (type) {
                ArmorTier.LIGHT -> R.id.fragment_item_new_light_armor_radio
                ArmorTier.MEDIUM -> R.id.fragment_tiem_new_medium_armor_radio
                ArmorTier.HEAVY -> R.id.fragment_item_new_heavy_armor_radio
                else -> -1
            }
        }

        @JvmStatic
        fun convertRadioButtonToArmorTier(button: Int): ArmorTier? {
            return when (button) {
                R.id.fragment_item_new_light_armor_radio -> ArmorTier.LIGHT
                R.id.fragment_tiem_new_medium_armor_radio -> ArmorTier.MEDIUM
                R.id.fragment_item_new_heavy_armor_radio -> ArmorTier.HEAVY
                else -> null
            }
        }
        /**
         * End editInventory-specific bindings
         */
    }
}