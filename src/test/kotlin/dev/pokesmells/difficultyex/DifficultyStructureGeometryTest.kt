package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DifficultyStructureGeometryTest {
    @Test
    fun inclusiveBoundaryAndOneBlockOutside() {
        assertTrue(DifficultyStructureGeometry.contains(84, 200, 100, 120, 200, 220, 16))
        assertTrue(DifficultyStructureGeometry.contains(136, 220, 100, 120, 200, 220, 16))
        assertFalse(DifficultyStructureGeometry.contains(83, 200, 100, 120, 200, 220, 16))
        assertFalse(DifficultyStructureGeometry.contains(137, 220, 100, 120, 200, 220, 16))
        assertFalse(DifficultyStructureGeometry.contains(100, 237, 100, 120, 200, 220, 16))
    }

    @Test
    fun zeroRadiusOnlyIncludesStructureBounds() {
        assertTrue(DifficultyStructureGeometry.contains(100, 220, 100, 120, 200, 220, 0))
        assertFalse(DifficultyStructureGeometry.contains(99, 220, 100, 120, 200, 220, 0))
        assertFalse(DifficultyStructureGeometry.contains(100, 221, 100, 120, 200, 220, 0))
    }

    @Test
    fun radiusIsCappedAndNegativeConfigDoesNotExpandInfluence() {
        assertEquals(128, DifficultyStructureGeometry.effectiveRadius(1000000))
        assertEquals(0, DifficultyStructureGeometry.effectiveRadius(-5))
        assertTrue(DifficultyStructureGeometry.contains(228, 200, 100, 100, 200, 200, 1000000))
        assertFalse(DifficultyStructureGeometry.contains(229, 200, 100, 100, 200, 200, 1000000))
        assertFalse(DifficultyStructureGeometry.contains(101, 200, 100, 100, 200, 200, -5))
    }

    @Test
    fun negativeChunkCoordinatesFloorCorrectlyAcrossZero() {
        assertEquals(-2..0, DifficultyStructureGeometry.chunkRange(-16, 16))
        assertEquals(-1..0, DifficultyStructureGeometry.chunkRange(-1, 1))
        assertEquals(0..1, DifficultyStructureGeometry.chunkRange(15, 1))
        assertEquals(0..0, DifficultyStructureGeometry.chunkRange(0, 0))
    }

    @Test
    fun coordinateArithmeticIsSafeAtIntegerExtremes() {
        assertTrue(DifficultyStructureGeometry.contains(Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MIN_VALUE, 16))
        assertFalse(DifficultyStructureGeometry.contains(Int.MAX_VALUE - 1, 0, Int.MAX_VALUE, Int.MAX_VALUE, 1, 1, 0))
        assertEquals((Int.MIN_VALUE.toLong() - 128 shr 4).toInt()..((Int.MIN_VALUE.toLong() + 128 shr 4).toInt()), DifficultyStructureGeometry.chunkRange(Int.MIN_VALUE, 128))
        assertEquals(((Int.MAX_VALUE.toLong() - 128 shr 4).toInt())..((Int.MAX_VALUE.toLong() + 128 shr 4).toInt()), DifficultyStructureGeometry.chunkRange(Int.MAX_VALUE, 128))
    }
}
