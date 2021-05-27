package com.example.morkborgcharactersheet.models

enum class DiceValue(val value: Int, val id: Int) {
    D0 (0, 0),
    D2 (2, 1),
    D3 (3, 2),
    D4 (4, 3),
    D6 (6, 4),
    D8 (8, 5),
    D10 (10, 6),
    D12 (12, 7),
    D20 (20, 8);

    companion object {
        fun get(value: Int): DiceValue? = values().find { it.id == value }
        fun getByValue(value: Int): DiceValue? = values().find { it.value == value }
    }
}