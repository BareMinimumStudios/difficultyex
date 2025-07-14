package xyz.naomieow.difficultyex.config

import blue.endless.jankson.Comment

data class EntityScalerConfig(
    @JvmField
    @Comment("The maximum distance this structure is able to affect around itself.")
    var maximumDistance: Int = 500,

    @JvmField
    @Comment("Based on (x) levels from a mob affected by the given distance, a formula can be provided to alter the value.")
    var levelFormula: String = "x",
)
