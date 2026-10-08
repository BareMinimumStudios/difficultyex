package dev.pokesmells.difficultyex

/** Immutable settings shared by both loader implementations. */
data class DifficultySettings(
    val startingLevel: Int = 1,
    val maximumLevel: Int = 1_000_000,
    val playerRadius: Int = 100,
    val playerLevelFormula: String = "x",
    val averageDecrement: Int = 3,
    val averageIncrement: Int = 3,
    val mobBlacklist: List<String> = emptyList(),
    val entityStartingLevels: Map<String, Int> = emptyMap(),
    val entityMaximumLevels: Map<String, Int> = emptyMap(),
    val dimensionStartingLevels: Map<String, Int> = emptyMap(),
    val dimensionMaximumLevels: Map<String, Int> = emptyMap(),
    val biomeStartingLevels: Map<String, Int> = emptyMap(),
    val biomeMaximumLevels: Map<String, Int> = emptyMap(),
    val structureRadius: Int = 50,
    val structureStartingLevels: Map<String, Int> = emptyMap(),
    val structureMaximumLevels: Map<String, Int> = emptyMap(),
    val healthPerLevel: Double = 0.08,
    val armorPerLevel: Double = 0.08,
    val damagePerLevel: Double = 0.1,
    val experiencePerLevel: Double = 0.1,
    val nameplatesEnabled: Boolean = true,
    val nameplateDistance: Int = 20,
    val nameplateHostileOnly: Boolean = false,
    val nameplateBlacklist: List<String> = emptyList(),
    val nameplateShowLevel: Boolean = true,
    val nameplateShowHealth: Boolean = true,
    val nameplateShowHealthText: Boolean = true
)
