package dev.pokesmells.difficultyex

import me.fzzyhmstrs.fzzy_config.annotations.RootConfig
import me.fzzyhmstrs.fzzy_config.annotations.Version
import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.event.api.ServerUpdateContext
import me.fzzyhmstrs.fzzy_config.util.Translatable

@RootConfig
@Version(1)
@Translatable.Name("DifficultyEx")
@Translatable.Desc("Mob progression and world-based difficulty scaling.")
class DifficultyExConfig : Config(DifficultyEx.id("config")) {
    @Translatable.Name("Progression")
    val progression = ConfigGroup("progression", false)
    var startingLevel = 1
    var maximumLevel = 1_000_000
    var playerRadius = 100
    var playerLevelFormula = "x"
    var averageDecrement = 3
    @ConfigGroup.Pop var averageIncrement = 3

    @Translatable.Name("Mob rules")
    val mobs = ConfigGroup("mobs", true)
    var mobBlacklist: List<String> = emptyList()
    var entityStartingLevels: Map<String, Int> = emptyMap()
    var entityMaximumLevels: Map<String, Int> = emptyMap()
    var healthPerLevel = 0.08
    var armorPerLevel = 0.08
    var damagePerLevel = 0.1
    @ConfigGroup.Pop var experiencePerLevel = 0.1

    @Translatable.Name("World rules")
    val world = ConfigGroup("world", true)
    var dimensionStartingLevels: Map<String, Int> = emptyMap()
    var dimensionMaximumLevels: Map<String, Int> = emptyMap()
    var biomeStartingLevels: Map<String, Int> = emptyMap()
    @ConfigGroup.Pop var biomeMaximumLevels: Map<String, Int> = emptyMap()

    @Translatable.Name("Structure rules")
    val structures = ConfigGroup("structures", true)
    var structureRadius = 50
    var structureStartingLevels: Map<String, Int> = emptyMap()
    @ConfigGroup.Pop var structureMaximumLevels: Map<String, Int> = emptyMap()

    @Translatable.Name("Nameplates")
    val nameplates = ConfigGroup("nameplates", true)
    var nameplatesEnabled = true
    var nameplateDistance = 20
    var nameplateHostileOnly = false
    var nameplateBlacklist: List<String> = emptyList()
    var nameplateShowLevel = true
    var nameplateShowHealth = true
    @ConfigGroup.Pop var nameplateShowHealthText = true

    fun toSnapshot() = DifficultySettings(
        startingLevel, maximumLevel, playerRadius, playerLevelFormula, averageDecrement, averageIncrement,
        mobBlacklist, entityStartingLevels, entityMaximumLevels,
        dimensionStartingLevels, dimensionMaximumLevels, biomeStartingLevels, biomeMaximumLevels,
        structureRadius, structureStartingLevels, structureMaximumLevels,
        healthPerLevel, armorPerLevel, damagePerLevel, experiencePerLevel,
        nameplatesEnabled, nameplateDistance, nameplateHostileOnly, nameplateBlacklist,
        nameplateShowLevel, nameplateShowHealth, nameplateShowHealthText
    )

    override fun onSyncClient() { DifficultyEx.configure(toSnapshot()) }
    override fun onUpdateClient() { DifficultyEx.configure(toSnapshot()) }
    override fun onUpdateServer(context: ServerUpdateContext) { DifficultyEx.configure(toSnapshot()) }
    override fun fileType(): FileType = FileType.JSON5
}
