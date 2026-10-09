package dev.pokesmells.difficultyex

import java.math.BigDecimal
import java.math.MathContext
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
        if (valid.isEmpty()) return fallbackLevel
        val average = valid.average()
        if (average.isFinite()) return average.roundToInt()
        val sum = valid.fold(BigDecimal.ZERO) { total, value -> total + BigDecimal.valueOf(value) }
        return sum.divide(BigDecimal.valueOf(valid.size.toLong()), MathContext.DECIMAL128)
            .toDouble().roundToInt()
    }
}
