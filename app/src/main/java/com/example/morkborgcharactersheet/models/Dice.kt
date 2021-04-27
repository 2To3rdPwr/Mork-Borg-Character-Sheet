package com.example.morkborgcharactersheet.models

import com.example.morkborgcharactersheet.models.AbilityType
import kotlin.random.Random

/**
 * Simple dice roller. Lets us easily generate rolled values from DiceValues
 */
class Dice (var amount: Int = 1, var diceValue: DiceValue = DiceValue.D20, var bonus: Int = 0, var ability: AbilityType = AbilityType.UNTYPED) {
    /**
     * Generates a dice roll for the given dice
     * Note that ability score must be passed as a parameter
     */
    fun roll(bonus: Int = 0): Int {
        var total = 0
        for(i in 1 .. amount) {
            total += Random.nextInt(1, diceValue.value + 1)
        }
        total += bonus + bonus

        return total
    }
}