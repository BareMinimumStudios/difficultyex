package dev.pokesmells.difficultyex

object DifficultyHealthMath {
    @JvmStatic
    fun rescale(health: Float, previousMaximum: Float, newMaximum: Float): Float {
        if (!health.isFinite() || !previousMaximum.isFinite() || !newMaximum.isFinite() ||
            health <= 0f || previousMaximum <= 0f || newMaximum <= 0f) return 0f
        val fraction = (health.toDouble() / previousMaximum.toDouble()).coerceIn(0.0, 1.0)
        return (fraction * newMaximum.toDouble()).toFloat().coerceIn(0f, newMaximum)
    }
}
