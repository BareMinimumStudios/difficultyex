package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyLevelBoundsTest {
    @Test
    fun appliesTheHighestMinimumAndLowestMaximum() {
        assertEquals(30, DifficultyLevelBounds.apply(5, 1, 100, listOf(12, 30), listOf(90, 45)))
        assertEquals(45, DifficultyLevelBounds.apply(200, 1, 100, listOf(12, 30), listOf(90, 45)))
    }

    @Test
    fun explicitCeilingWinsOverConflictingMinimums() {
        assertEquals(10, DifficultyLevelBounds.apply(75, 1, 100, listOf(50), listOf(10)))
    }

    @Test
    fun minimumIsAlwaysAtLeastOne() {
        assertEquals(1, DifficultyLevelBounds.apply(-100, -5, 0, listOf(-40), listOf(-20)))
        assertEquals(1, DifficultyLevelBounds.apply(0, 1, 100, emptyList(), emptyList()))
    }

    @Test
    fun globalBoundsApplyWithoutLocalRules() {
        assertEquals(10, DifficultyLevelBounds.apply(4, 10, 50, emptyList(), emptyList()))
        assertEquals(50, DifficultyLevelBounds.apply(100, 10, 50, emptyList(), emptyList()))
    }
}
