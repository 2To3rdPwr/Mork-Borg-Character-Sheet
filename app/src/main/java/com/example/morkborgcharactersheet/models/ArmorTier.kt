package com.example.morkborgcharactersheet.models

enum class ArmorTier(val id: Int, val diceValue: Int) {
    NONE (0, 0),
    LIGHT (1, 2),
    MEDIUM (2, 4),
    HEAVY (3, 6);

    companion object {
        fun get(value: Int): ArmorTier? = ArmorTier.values().find { it.id == value }
    }
}