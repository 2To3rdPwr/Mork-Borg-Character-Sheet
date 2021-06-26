package com.twotothirdpower.morkborgcharactersheet.models

enum class AbilityType(val id: Int) {
    UNTYPED (0),
    STRENGTH (1),
    AGILITY (2),
    PRESENCE (3),
    TOUGHNESS (4);

    companion object {
        fun get(value: Int): AbilityType? = values().find { it.id == value }
    }
}