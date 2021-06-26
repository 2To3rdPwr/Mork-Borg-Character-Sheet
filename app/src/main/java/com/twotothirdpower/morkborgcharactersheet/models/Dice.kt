package com.twotothirdpower.morkborgcharactersheet.models

import kotlin.random.Random

/**
 * Simple dice roller. Lets us easily generate rolled values from DiceValues
 */
class Dice (var amount: Int = 1, var diceValue: DiceValue = DiceValue.D20, var bonus: Int = 0, var ability: AbilityType = AbilityType.UNTYPED) {
    /**
     * Generates a dice roll for the given dice
     * Note that ability score must be passed as a parameter
     */
    fun roll(miscBonus: Int = 0): Int {
        if (diceValue == DiceValue.D0 || amount == 0) {
            return bonus + miscBonus
        }

        var total = 0
        for(i in 1 .. amount) {
            total += Random.nextInt(1, diceValue.value + 1)
        }
        total += bonus + miscBonus

        return total
    }

    override fun toString(): String {
        var diceString = ""

        if (amount > 0 && diceValue != DiceValue.D0) {
            diceString = "$amount"
            diceString += "$diceValue"
        }

        if (diceString != "" && bonus > 0)
            diceString += "+"
        if (bonus != 0)
            diceString += "$bonus"

        if (ability != AbilityType.UNTYPED) {
            if (diceString != "")
                diceString += "+"
            diceString += "$ability"
        }

        return diceString
    }

    fun toString(abilityBonus: Int): String {
        var diceString = ""

        if (amount > 0 && diceValue != DiceValue.D0) {
            diceString = "$amount"
            diceString += "$diceValue"
        }

        if (diceString != "" && bonus > 0)
            diceString += "+"
        if (bonus != 0)
            diceString += "$bonus"

        if (diceString != "" && abilityBonus > 0)
            diceString += "+"
        if (abilityBonus != 0)
            diceString += "$abilityBonus"

        return diceString
    }
}