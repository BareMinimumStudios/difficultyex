package dev.pokesmells.difficultyex

/** Pure horizontal geometry shared by structure influence and its edge-case tests. */
object DifficultyStructureGeometry {
    const val MAX_RADIUS = 128

    @JvmStatic
    fun effectiveRadius(configured: Int): Int = configured.coerceIn(0, MAX_RADIUS)

    /** Inclusive chunk coordinates, using arithmetic shift for negative block positions. */
    @JvmStatic
    fun chunkRange(block: Int, radius: Int): IntRange {
        val r = effectiveRadius(radius).toLong()
        return ((block.toLong() - r) shr 4).toInt()..
            ((block.toLong() + r) shr 4).toInt()
    }

    /** The configured radius extends a structure's entire bounding box on the X/Z plane. */
    @JvmStatic
    fun contains(
        x: Int, z: Int,
        minX: Int, maxX: Int, minZ: Int, maxZ: Int,
        configuredRadius: Int
    ): Boolean {
        val r = effectiveRadius(configuredRadius).toLong()
        return x.toLong() >= minX.toLong() - r &&
            x.toLong() <= maxX.toLong() + r &&
            z.toLong() >= minZ.toLong() - r &&
            z.toLong() <= maxZ.toLong() + r
    }
}
