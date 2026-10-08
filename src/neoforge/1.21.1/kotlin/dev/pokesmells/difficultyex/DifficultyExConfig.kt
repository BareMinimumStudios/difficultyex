package dev.pokesmells.difficultyex

import me.fzzyhmstrs.fzzy_config.annotations.NonSync
import me.fzzyhmstrs.fzzy_config.annotations.RootConfig
import me.fzzyhmstrs.fzzy_config.annotations.Version
import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigGroup
import me.fzzyhmstrs.fzzy_config.event.api.ServerUpdateContext
import me.fzzyhmstrs.fzzy_config.util.Translatable
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedStringMap
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedDouble
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt

/**
 * Server-authoritative progression and client-local nameplates.
 *
 * Validated fields are required for the Fzzy Config GUI. The serialized JSON5
 * remains flat and keeps its previous keys and primitive/list/map values.
 */
@RootConfig
@Version(1)
@Translatable.Name("DifficultyEx")
@Translatable.Desc("Mob progression and world-based difficulty scaling.")
class DifficultyExConfig : Config(DifficultyEx.id("config")) {
    @Translatable.Name("Progression")
    val progression = ConfigGroup("progression", false)
    var startingLevel = ValidatedInt(1, 1_000_000, 1)
    var maximumLevel = ValidatedInt(1_000_000, 1_000_000, 1)
    var playerRadius = ValidatedInt(100, 4096, 0)
    var playerLevelFormula = ValidatedString("x")
    var averageDecrement = ValidatedInt(3, 1_000_000, 0)
    @ConfigGroup.Pop var averageIncrement = ValidatedInt(3, 1_000_000, 0)

    @Translatable.Name("Mob rules")
    val mobs = ConfigGroup("mobs", true)
    var mobBlacklist = ValidatedString().toList()
    var entityStartingLevels = ValidatedStringMap(emptyMap<String, Int>(), ValidatedString(), ValidatedInt(1))
    var entityMaximumLevels = ValidatedStringMap(emptyMap<String, Int>(), ValidatedString(), ValidatedInt(1))
    var healthPerLevel = ValidatedDouble(0.08)
    var armorPerLevel = ValidatedDouble(0.08)
    var damagePerLevel = ValidatedDouble(0.1)
    @ConfigGroup.Pop var experiencePerLevel = ValidatedDouble(0.1)

    @Translatable.Name("World rules")
    val world = ConfigGroup("world", true)
    var dimensionStartingLevels = ValidatedStringMap(emptyMap<String, Int>(), ValidatedString(), ValidatedInt(1))
    var dimensionMaximumLevels = ValidatedStringMap(emptyMap<String, Int>(), ValidatedString(), ValidatedInt(1))
    var biomeStartingLevels = ValidatedStringMap(emptyMap<String, Int>(), ValidatedString(), ValidatedInt(1))
    @ConfigGroup.Pop var biomeMaximumLevels = ValidatedStringMap(emptyMap<String, Int>(), ValidatedString(), ValidatedInt(1))

    @Translatable.Name("Structure rules")
    val structures = ConfigGroup("structures", true)
    var structureRadius = ValidatedInt(50, 128, 0)
    var structureStartingLevels = ValidatedStringMap(emptyMap<String, Int>(), ValidatedString(), ValidatedInt(1))
    @ConfigGroup.Pop var structureMaximumLevels = ValidatedStringMap(emptyMap<String, Int>(), ValidatedString(), ValidatedInt(1))

    @Translatable.Name("Nameplates (client only)")
    val nameplates = ConfigGroup("nameplates", true)
    @field:NonSync var nameplatesEnabled = ValidatedBoolean(true)
    @field:NonSync var nameplateDistance = ValidatedInt(20, 64, 0)
    @field:NonSync var nameplateHostileOnly = ValidatedBoolean(false)
    @field:NonSync var nameplateBlacklist = ValidatedString().toList()
    @field:NonSync var nameplateShowLevel = ValidatedBoolean(true)
    @field:NonSync var nameplateShowHealth = ValidatedBoolean(true)
    @field:NonSync @ConfigGroup.Pop var nameplateShowHealthText = ValidatedBoolean(true)

    fun toSnapshot() = DifficultySettings(
        startingLevel.get(), maximumLevel.get(), playerRadius.get(), playerLevelFormula.get(),
        averageDecrement.get(), averageIncrement.get(),
        mobBlacklist.get(), entityStartingLevels.get(), entityMaximumLevels.get(),
        dimensionStartingLevels.get(), dimensionMaximumLevels.get(),
        biomeStartingLevels.get(), biomeMaximumLevels.get(),
        structureRadius.get(), structureStartingLevels.get(), structureMaximumLevels.get(),
        healthPerLevel.get(), armorPerLevel.get(), damagePerLevel.get(), experiencePerLevel.get(),
        nameplatesEnabled.get(), nameplateDistance.get(), nameplateHostileOnly.get(), nameplateBlacklist.get(),
        nameplateShowLevel.get(), nameplateShowHealth.get(), nameplateShowHealthText.get()
    )

    override fun onSyncClient() { DifficultyEx.configure(toSnapshot()) }
    override fun onUpdateClient() { DifficultyEx.configure(toSnapshot()) }
    override fun onUpdateServer(context: ServerUpdateContext) { DifficultyEx.configure(toSnapshot()) }
    override fun fileType(): FileType = FileType.JSON5
}
