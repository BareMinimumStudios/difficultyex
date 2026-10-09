package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertEquals

class DifficultyHealthMathTest {
    @Test
    fun levelChangesPreserveFractionalHealthWithoutHealingToOne() {
        assertEquals(0.25f, DifficultyHealthMath.rescale(0.5f, 40f, 20f))
        assertEquals(1f, DifficultyHealthMath.rescale(0.5f, 20f, 40f))
        assertEquals(26f, DifficultyHealthMath.rescale(18f, 36f, 52f))
    }

    @Test
    fun deadAndInvalidHealthCannotReviveAMob() {
        assertEquals(0f, DifficultyHealthMath.rescale(0f, 20f, 40f))
        assertEquals(0f, DifficultyHealthMath.rescale(-1f, 20f, 40f))
        assertEquals(0f, DifficultyHealthMath.rescale(Float.NaN, 20f, 40f))
        assertEquals(0f, DifficultyHealthMath.rescale(10f, 0f, 40f))
        assertEquals(0f, DifficultyHealthMath.rescale(10f, 20f, Float.POSITIVE_INFINITY))
    }

    @Test
    fun overhealedAndExtremeFiniteHealthStayWithinTheNewMaximum() {
        assertEquals(30f, DifficultyHealthMath.rescale(40f, 20f, 30f))
        assertEquals(Float.MAX_VALUE, DifficultyHealthMath.rescale(Float.MAX_VALUE, 1f, Float.MAX_VALUE))
    }
}
