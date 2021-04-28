package com.example.morkborgcharactersheet.util

import android.telephony.ims.ImsMmTelManager
import android.text.TextUtils
import android.util.Log
import androidx.databinding.InverseMethod
import com.example.morkborgcharactersheet.R
import com.example.morkborgcharactersheet.models.AbilityType
import com.example.morkborgcharactersheet.models.ArmorTier
import com.example.morkborgcharactersheet.models.DiceValue
import com.example.morkborgcharactersheet.models.ItemType
import kotlinx.android.synthetic.main.fragment_inventory_edit.view.*

class DataBindingConverter {
    companion object {
        /**
         * The converter I built for ints only works for positive ints (negative sign isn't an int)
         * When dealing with int inputs that can possibly be negative, just ensure their editText has
         * an inputType of signedNumber and just two-way bind it as a string. Then parse to an int later.
         */
        @InverseMethod("convertStringToInt")
        @JvmStatic
        fun convertIntToString(value: Int?): String {
            return value?.toString() ?: ""
        }

        @JvmStatic
        fun convertStringToInt(value: String): Int? {
            if (TextUtils.isEmpty(value) || !TextUtils.isDigitsOnly(value)) {
                return null
            }
            return value.toIntOrNull()
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
                AbilityType.AGILITY -> R.id.fragment_item_new_agility_radio_button
                AbilityType.PRESENCE -> R.id.fragment_item_new_presence_radio_button
                AbilityType.TOUGHNESS -> R.id.fragment_item_new_toughness_radio_button
                AbilityType.UNTYPED -> -1
                else -> -1
            }
        }

        @JvmStatic
        fun convertRadioButtonToAbilityType(button: Int): AbilityType {
            return when (button) {
                R.id.fragment_item_new_strength_radio_button -> AbilityType.STRENGTH
                R.id.fragment_item_new_agility_radio_button -> AbilityType.AGILITY
                R.id.fragment_item_new_presence_radio_button -> AbilityType.PRESENCE
                R.id.fragment_item_new_toughness_radio_button -> AbilityType.TOUGHNESS
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
         *
         * Begin editCharacter-specific bindings
         */
    }
}