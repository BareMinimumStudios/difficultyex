package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyNameplateHealthTest {
    @Test
    fun barIsFullOnlyAtMaximumHealth() {
        assertEquals(10, DifficultyNameplateHealth.filledSegments(20f, 20f))
        assertEquals(9, DifficultyNameplateHealth.filledSegments(19.99f, 20f))
        assertEquals(10, DifficultyNameplateHealth.filledSegments(40f, 20f))
    }

    @Test
    fun lowPositiveHealthIsNotShownAsDead() {
        assertEquals(1, DifficultyNameplateHealth.filledSegments(0.1f, 20f))
        assertEquals(1, DifficultyNameplateHealth.displayedHealth(0.1f))
        assertEquals(0, DifficultyNameplateHealth.filledSegments(0f, 20f))
        assertEquals(0, DifficultyNameplateHealth.displayedHealth(0f))
    }

    @Test
    fun barTracksQuarterAndHalfHealth() {
        assertEquals(3, DifficultyNameplateHealth.filledSegments(5f, 20f))
        assertEquals(5, DifficultyNameplateHealth.filledSegments(10f, 20f))
        assertEquals(8, DifficultyNameplateHealth.filledSegments(15f, 20f))
    }

    @Test
    fun invalidHealthNeverProducesInvalidSegments() {
        assertEquals(0, DifficultyNameplateHealth.filledSegments(Float.NaN, 20f))
        assertEquals(0, DifficultyNameplateHealth.filledSegments(10f, Float.NaN))
        assertEquals(0, DifficultyNameplateHealth.filledSegments(Float.POSITIVE_INFINITY, 20f))
        assertEquals(0, DifficultyNameplateHealth.filledSegments(10f, 0f))
        assertEquals(0, DifficultyNameplateHealth.displayedHealth(Float.NaN))
    }
}
