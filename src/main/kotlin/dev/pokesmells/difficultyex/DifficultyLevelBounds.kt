package dev.pokesmells.difficultyex

/** Pure deterministic bounds calculation shared across dimension, biome, structure, and entity rules. */
object DifficultyLevelBounds {
    @JvmStatic
    fun apply(
        rolledLevel: Int,
        globalMinimum: Int,
        globalMaximum: Int,
        localMinimums: Collection<Int>,
        localMaximums: Collection<Int>
    ): Int {
        val floor = maxOf(globalMinimum.coerceAtLeast(1), localMinimums.maxOrNull() ?: 1)
        val ceiling = minOf(globalMaximum.coerceAtLeast(1), localMaximums.minOrNull() ?: Int.MAX_VALUE)
        // When user rules conflict, explicit maximum restrictions win.
        return rolledLevel.coerceAtLeast(floor).coerceAtMost(ceiling).coerceAtLeast(1)
    }
}
