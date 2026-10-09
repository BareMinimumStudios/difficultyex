package dev.pokesmells.difficultyex

import kotlin.math.roundToInt

/** Client-label formatting calculations, independent of Minecraft rendering. */
object DifficultyNameplateHealth {
    private const val BAR_SEGMENTS = 10

    /**
     * Never draw an empty bar for a living mob or a completely full bar for
     * an injured mob. Preserve vanilla health precision before rounding.
     */
    @JvmStatic
    fun filledSegments(health: Float, maximum: Float): Int {
        if (!health.isFinite() || !maximum.isFinite() || maximum <= 0f || health <= 0f) return 0
        if (health >= maximum) return BAR_SEGMENTS
        val ratio = (health.toDouble() / maximum.toDouble()).coerceIn(0.0, 1.0)
        return (ratio * BAR_SEGMENTS).roundToInt().coerceIn(1, BAR_SEGMENTS - 1)
    }

    /** Don't display zero HP for an entity that is still alive. */
    @JvmStatic
    fun displayedHealth(health: Float): Int {
        if (!health.isFinite() || health <= 0f) return 0
        return kotlin.math.ceil(health.toDouble()).toInt().coerceAtLeast(1)
    }
}
