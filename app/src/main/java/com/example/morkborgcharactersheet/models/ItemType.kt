package com.example.morkborgcharactersheet.models

enum class ItemType(val id: Int) {
    OTHER (0),
    WEAPON (1),
    ARMOR (2),
    SHIELD (3),
    POWER (4);

    companion object {
        fun get(value: Int): ItemType? = ItemType.values().find { it.id == value }
    }
}