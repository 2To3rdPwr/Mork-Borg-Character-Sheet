package com.example.morkborgcharactersheet.models

// TODO: DiceValue for D0
enum class DiceValue(val value: Int, val id: Int) {
    D2 (2, 0),
    D3 (3, 1),
    D4 (4, 2),
    D6 (6, 3),
    D8 (8, 4),
    D10 (10, 5),
    D12 (12, 6),
    D20 (20, 7);

    companion object {
        fun get(value: Int): DiceValue? = values().find { it.id == value }
        fun getByValue(value: Int): DiceValue? = values().find { it.value == value }
    }
}