package com.twotothirdpower.morkborgcharactersheet.models

enum class ItemType(val id: Int) {
    WEAPON (1),
    ARMOR (2),
    SHIELD (3),
    POWER (4),
    OTHER (0);

    companion object {
        fun get(value: Int): ItemType? = values().find { it.id == value }
    }
}