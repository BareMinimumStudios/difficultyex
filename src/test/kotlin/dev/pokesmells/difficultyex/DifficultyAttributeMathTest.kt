package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyAttributeMathTest {
    @Test
    fun healthModifierUsesOriginalLevelTimesRate() {
        assertEquals(0.08, DifficultyAttributeMath.multiplier(1, 0.08), 1e-10)
        assertEquals(0.8, DifficultyAttributeMath.multiplier(10, 0.08), 1e-10)
        assertEquals(1.6, DifficultyAttributeMath.multiplier(20, 0.08), 1e-10)
    }

    @Test
    fun attackAndArmorRateMatchConfig() {
        assertEquals(2.0, DifficultyAttributeMath.multiplier(20, 0.10), 1e-10)
        assertEquals(1.6, DifficultyAttributeMath.multiplier(20, 0.08), 1e-10)
    }

    @Test
    fun negativeAndZeroRatesNeverReduceAttributes() {
        assertEquals(0.0, DifficultyAttributeMath.multiplier(20, -0.5))
        assertEquals(0.0, DifficultyAttributeMath.multiplier(20, 0.0))
        assertEquals(0.0, DifficultyAttributeMath.multiplier(0, 0.8))
    }

    @Test
    fun invalidRatesDoNotIntroduceNanModifiers() {
        assertEquals(0.0, DifficultyAttributeMath.multiplier(20, Double.NaN))
        assertEquals(0.0, DifficultyAttributeMath.multiplier(20, Double.POSITIVE_INFINITY))
        assertEquals(0.0, DifficultyAttributeMath.multiplier(20, Double.NEGATIVE_INFINITY))
    }

    @Test
    fun veryLargeModifiersSaturateSafely() {
        assertEquals(1000.0, DifficultyAttributeMath.multiplier(20, 500.0))
        assertEquals(1000.0, DifficultyAttributeMath.multiplier(Int.MAX_VALUE, 0.1))
    }
}
