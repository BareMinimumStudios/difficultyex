package dev.pokesmells.difficultyex

/**
 * Shared attribute scaling safety limits.
 *
 * The returned modifier is added as ADD_MULTIPLIED_BASE (for example, 0.08
 * means +8% of an entity's base attribute). Non-finite rates are ignored.
 */
object DifficultyAttributeMath {
    private const val MAX_RATE = 100.0
    private const val MAX_MULTIPLIER = 1000.0

    @JvmStatic
    fun multiplier(level: Int, rate: Double): Double {
        if (level < 1 || !rate.isFinite() || rate <= 0.0) return 0.0
        return (level.toDouble() * rate.coerceAtMost(MAX_RATE)).coerceAtMost(MAX_MULTIPLIER)
    }
}
