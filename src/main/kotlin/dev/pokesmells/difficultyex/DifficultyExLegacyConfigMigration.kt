package dev.pokesmells.difficultyex

import blue.endless.jankson.Jankson
import blue.endless.jankson.JsonGrammar
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.slf4j.LoggerFactory
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path

/**
 * One-way, non-destructive migration of the original owo-lib difficultyex-config.json5.
 * Only runs if the new Fzzy Config file does not already exist.
 */
object DifficultyExLegacyConfigMigration {
    private val log = LoggerFactory.getLogger("difficultyex/legacy-config")
    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @JvmStatic
    fun migrateIfNeeded(configDir: Path): Boolean {
        val target = configDir.resolve("difficultyex").resolve("config.json5")
        if (Files.exists(target)) return false

        val legacy = listOf("difficultyex-config.json5", "difficultyex-config.json")
            .map(configDir::resolve)
            .firstOrNull(Files::isRegularFile) ?: return false
        var staging: Path? = null
        return try {
            val converted = convert(Files.readString(legacy))
            Files.createDirectories(target.parent)
            // Create beside the target so the final move is on the same filesystem.
            staging = Files.createTempFile(target.parent, "config-migration-", ".tmp")
            Files.writeString(staging, converted)
            // Never replace a user's existing Fzzy Config.
            Files.move(staging, target)
            staging = null
            log.info("Imported legacy DifficultyEx settings from {} into {}; original left untouched", legacy, target)
            true
        } catch (error: Exception) {
            log.warn("Could not import legacy DifficultyEx settings from {}; original left untouched", legacy, error)
            false
        } finally {
            staging?.let { runCatching { Files.deleteIfExists(it) } }
        }
    }

    /** Converts the former nested owo JSON5 structure into flat Fzzy Config JSON5 fields. */
    @JvmStatic
    fun convert(raw: String): String {
        // Gson's lenient mode does not handle all owo-lib JSON5 syntax (notably
        // trailing commas). Jankson supports the original format.
        val strictJson = StringWriter()
        Jankson.builder().build().load(raw).toJson(strictJson, JsonGrammar.STRICT, 0)
        val legacy = JsonParser.parseString(strictJson.toString()).asJsonObject
        // Start with full modern defaults to avoid missing-field warnings.
        val result = gson.toJsonTree(DifficultySettings()).asJsonObject
        result.addProperty("version", 1)

        fun copy(section: String, oldName: String, newName: String) {
            val group = legacy.get(section)
            if (group == null || !group.isJsonObject) return
            val value = group.asJsonObject.get(oldName) ?: return
            val expected = result.get(newName) ?: return
            val compatible = when {
                expected.isJsonObject -> value.isJsonObject
                expected.isJsonArray -> value.isJsonArray
                expected.isJsonPrimitive -> value.isJsonPrimitive &&
                    (expected.asJsonPrimitive.isBoolean == value.asJsonPrimitive.isBoolean) &&
                    (expected.asJsonPrimitive.isNumber == value.asJsonPrimitive.isNumber) &&
                    (expected.asJsonPrimitive.isString == value.asJsonPrimitive.isString)
                else -> false
            }
            if (compatible) result.add(newName, value.deepCopy())
        }

        val scaling = "scalingLevelSettings"
        copy(scaling, "startingLevel", "startingLevel")
        copy(scaling, "maximumLevel", "maximumLevel")
        copy(scaling, "levelScalingMaxRadiusByBlocks", "playerRadius")
        copy(scaling, "levelScalingByPlayerFormula", "playerLevelFormula")
        copy(scaling, "levelAverageDecrement", "averageDecrement")
        copy(scaling, "levelAverageIncrement", "averageIncrement")
        copy(scaling, "mobBlacklist", "mobBlacklist")
        copy(scaling, "entityStartingLevels", "entityStartingLevels")
        copy(scaling, "entityMaximumLevels", "entityMaximumLevels")
        copy(scaling, "entityBaseHealthPercentage", "healthPerLevel")
        copy(scaling, "entityBaseArmorPercentage", "armorPerLevel")
        copy(scaling, "entityBaseDamagePercentage", "damagePerLevel")
        copy(scaling, "entityExperiencePercentage", "experiencePerLevel")

        for ((section, name) in listOf(
            "dimensionSettings" to "dimension",
            "biomeScalingSettings" to "biome",
            "structureScalingSettings" to "structure"
        )) {
            copy(section, "startingLevels", "${name}StartingLevels")
            copy(section, "maximumLevels", "${name}MaximumLevels")
        }
        copy("structureScalingSettings", "radius", "structureRadius")

        val visual = "visualSettings"
        copy(visual, "nameplateEnabled", "nameplatesEnabled")
        copy(visual, "nameplateRenderDistance", "nameplateDistance")
        copy(visual, "nameplateShowHostileMobsOnly", "nameplateHostileOnly")
        copy(visual, "nameplateMobBlacklist", "nameplateBlacklist")
        copy(visual, "showNameplateLevel", "nameplateShowLevel")
        copy(visual, "showNameplateHealthBar", "nameplateShowHealth")
        copy(visual, "showNameplateHealthText", "nameplateShowHealthText")

        return gson.toJson(result)
    }
}
