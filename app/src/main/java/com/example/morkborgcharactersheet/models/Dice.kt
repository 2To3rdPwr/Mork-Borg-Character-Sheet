package com.example.morkborgcharactersheet.models

import com.example.morkborgcharactersheet.database.Inventory
import com.example.morkborgcharactersheet.models.AbilityType
import kotlin.random.Random

// TODO: DiceValue = 0
/**
 * Simple dice roller. Lets us easily generate rolled values from DiceValues
 */
class Dice (var amount: Int = 1, var diceValue: DiceValue = DiceValue.D20, var bonus: Int = 0, var ability: AbilityType = AbilityType.UNTYPED) {
    /**
     * Generates a dice roll for the given dice
     * Note that ability score must be passed as a parameter
     */
    fun roll(miscBonus: Int = 0): Int {
        var total = 0
        for(i in 1 .. amount) {
            total += Random.nextInt(1, diceValue.value + 1)
        }
        total += bonus + miscBonus

        return total
    }

    override fun toString(): String {
        var diceString = ""

        if (amount > 0)
            diceString = "$amount"
        diceString += "$diceValue"
        if (bonus > 0)
            diceString += "+$bonus"
        else if (bonus < 0)
            diceString += "$bonus"
        if (ability != AbilityType.UNTYPED)
            diceString += "$ability"

        return diceString
    }

    fun toString(abilityBonus: Int): String {
        var diceString = ""

        if (amount > 0)
            diceString = "$amount"
        diceString += "$diceValue"
        if (bonus > 0)
            diceString += "+$bonus"
        else if (bonus < 0)
            diceString += "$bonus"
        if (abilityBonus > 0)
            diceString += "+$abilityBonus"
        else if (abilityBonus < 0)
            diceString += "$abilityBonus"

        return diceString
    }
}