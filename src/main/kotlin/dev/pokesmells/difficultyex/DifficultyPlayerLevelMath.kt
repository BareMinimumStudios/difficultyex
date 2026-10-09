package dev.pokesmells.difficultyex

import kotlin.math.roundToInt

/**
 * Deterministic part of PlayerEx-driven spawn scaling.
 * Kept separate from world/entity access so edge cases can be unit-tested.
 */
object DifficultyPlayerLevelMath {
    @JvmStatic
    fun withinRadius(distanceSquared: Double, configuredRadius: Int): Boolean {
        val radius = configuredRadius.coerceIn(0, 4096).toDouble()
        return distanceSquared.isFinite() && distanceSquared >= 0.0 &&
            distanceSquared <= radius * radius
    }

    @JvmStatic
    fun eligible(isSpectator: Boolean, distanceSquared: Double, configuredRadius: Int): Boolean =
        !isSpectator && withinRadius(distanceSquared, configuredRadius)

    @JvmStatic
    fun averageTransformedLevels(levels: Iterable<Double>, fallbackLevel: Int): Int {
        // A malformed user expression can produce NaN or infinity for individual
        // players; ignore only those values rather than invalidating everyone.
        val valid = levels.filter(Double::isFinite)
        return if (valid.isEmpty()) fallbackLevel else valid.average().roundToInt()
    }
}
