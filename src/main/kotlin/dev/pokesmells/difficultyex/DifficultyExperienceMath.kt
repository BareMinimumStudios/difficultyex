package dev.pokesmells.difficultyex

/**
 * Calculates the amount returned by Mob#getBaseExperienceReward.
 *
 * Pure arithmetic kept separate from the Mixin so boundary cases can be
 * exercised without starting a Minecraft server.
 */
object DifficultyExperienceMath {
    @JvmStatic
    fun scale(original: Int, level: Int, percentagePerLevel: Double): Int {
        if (level < 1) return original
        val safeRate = if (percentagePerLevel.isFinite()) {
            percentagePerLevel.coerceIn(0.0, 100.0)
        } else {
            0.0
        }
        val factor = 1.0 + level.toDouble() * safeRate
        return (original.toDouble() * factor)
            .coerceIn(0.0, Int.MAX_VALUE.toDouble())
            .toInt()
    }
}
