package com.twotothirdpower.morkborgcharactersheet.models

// TODO: Add DiceValue as a part of the enum instead of checking it externally
enum class ArmorTier(val id: Int, val diceValue: Int) {
    NONE (0, 0),
    LIGHT (1, 2),
    MEDIUM (2, 4),
    HEAVY (3, 6);

    companion object {
        fun get(value: Int): ArmorTier? = ArmorTier.values().find { it.id == value }
    }
}