package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DifficultyPlayerLevelMathTest {
    @Test
    fun radiusIsInclusiveAndUsesSquaredThreeDimensionalDistance() {
        assertTrue(DifficultyPlayerLevelMath.withinRadius(100.0, 10))
        assertFalse(DifficultyPlayerLevelMath.withinRadius(100.0001, 10))
        assertTrue(DifficultyPlayerLevelMath.withinRadius(0.0, 0))
        assertFalse(DifficultyPlayerLevelMath.withinRadius(1.0, 0))
    }

    @Test
    fun spectatorDoesNotInfluenceScalingEvenWhenClose() {
        assertFalse(DifficultyPlayerLevelMath.eligible(true, 0.0, 10))
        assertTrue(DifficultyPlayerLevelMath.eligible(false, 0.0, 10))
        assertFalse(DifficultyPlayerLevelMath.eligible(false, 121.0, 10))
    }

    @Test
    fun radiusLimitsMatchValidatedConfigAndRejectInvalidDistances() {
        assertTrue(DifficultyPlayerLevelMath.withinRadius(4096.0 * 4096.0, 999999))
        assertFalse(DifficultyPlayerLevelMath.withinRadius(4096.0 * 4096.0 + 1.0, 999999))
        assertFalse(DifficultyPlayerLevelMath.withinRadius(Double.NaN, 5))
        assertFalse(DifficultyPlayerLevelMath.withinRadius(Double.POSITIVE_INFINITY, 5))
        assertFalse(DifficultyPlayerLevelMath.withinRadius(-1.0, 5))
    }

    @Test
    fun levelAverageUsesPlayerExLevelsNotVanillaXp() {
        assertEquals(20, DifficultyPlayerLevelMath.averageTransformedLevels(listOf(10.0, 30.0), 7))
        assertEquals(21, DifficultyPlayerLevelMath.averageTransformedLevels(listOf(10.0, 31.0), 7))
        assertEquals(40, DifficultyPlayerLevelMath.averageTransformedLevels(listOf(20.0, 60.0), 7))
    }

    @Test
    fun noValidPlayersFallsBackToServerStartingLevel() {
        assertEquals(7, DifficultyPlayerLevelMath.averageTransformedLevels(emptyList(), 7))
        assertEquals(7, DifficultyPlayerLevelMath.averageTransformedLevels(
            listOf(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), 7))
    }

    @Test
    fun anInvalidPlayerFormulaResultDoesNotSuppressOtherPlayers() {
        assertEquals(30, DifficultyPlayerLevelMath.averageTransformedLevels(
            listOf(Double.NaN, 20.0, 40.0, Double.POSITIVE_INFINITY), 7))
    }
}
