package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyExperienceMathTest {
    @Test
    fun scalesRegularMobExperience() {
        assertEquals(10, DifficultyExperienceMath.scale(5, 10, 0.1))
        assertEquals(5, DifficultyExperienceMath.scale(5, 1, 0.1))
        assertEquals(0, DifficultyExperienceMath.scale(0, 100, 0.1))
    }

    @Test
    fun keepsUnleveledMobExperienceUnchanged() {
        assertEquals(7, DifficultyExperienceMath.scale(7, 0, 0.9))
        assertEquals(7, DifficultyExperienceMath.scale(7, -2, 0.9))
    }

    @Test
    fun invalidRatesDoNotProduceInvalidRewards() {
        assertEquals(7, DifficultyExperienceMath.scale(7, 10, -5.0))
        assertEquals(7, DifficultyExperienceMath.scale(7, 10, Double.NaN))
        assertEquals(7, DifficultyExperienceMath.scale(7, 10, Double.POSITIVE_INFINITY))
    }

    @Test
    fun largeRewardsSaturateAtIntMax() {
        assertEquals(Int.MAX_VALUE, DifficultyExperienceMath.scale(Int.MAX_VALUE, Int.MAX_VALUE, 100.0))
    }
}
