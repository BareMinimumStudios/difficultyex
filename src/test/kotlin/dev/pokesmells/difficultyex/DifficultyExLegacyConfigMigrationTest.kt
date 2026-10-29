package dev.pokesmells.difficultyex

import com.google.gson.JsonParser
import java.nio.file.Files
import java.nio.file.Path
import java.util.Comparator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DifficultyExLegacyConfigMigrationTest {
    private val legacy = """
        {
          // owo-lib generated nested config
          scalingLevelSettings: {
            startingLevel: 7,
            maximumLevel: 250,
            levelScalingMaxRadiusByBlocks: 96,
            levelScalingByPlayerFormula: "x * 0.8",
            entityBaseHealthPercentage: 0.25,
            entityExperiencePercentage: 0.3,
            entityStartingLevels: {"minecraft:zombie": 15},
            mobBlacklist: ["minecraft:creeper"],
          },
          dimensionSettings: { startingLevels: {"minecraft:the_nether": 30} },
          biomeScalingSettings: { maximumLevels: {"minecraft:desert": 80} },
          structureScalingSettings: {
            radius: 45,
            startingLevels: {"minecraft:desert_pyramid": 40}
          },
          visualSettings: {
            nameplateEnabled: false,
            showNameplateHealthText: false,
            nameplateRenderDistance: 38,
            nameplateMobBlacklist: ["minecraft:bat"],
          }
        }
    """.trimIndent()

    @Test
    fun migratesFormerNestedFieldsAndRetainsDefaults() {
        val parsed = JsonParser.parseString(DifficultyExLegacyConfigMigration.convert(legacy)).asJsonObject
        assertEquals(1, parsed.get("version").asInt)
        assertEquals(7, parsed.get("startingLevel").asInt)
        assertEquals(250, parsed.get("maximumLevel").asInt)
        assertEquals(96, parsed.get("playerRadius").asInt)
        assertEquals("x * 0.8", parsed.get("playerLevelFormula").asString)
        assertEquals(0.25, parsed.get("healthPerLevel").asDouble)
        assertEquals(0.3, parsed.get("experiencePerLevel").asDouble)
        assertEquals(15, parsed.getAsJsonObject("entityStartingLevels").get("minecraft:zombie").asInt)
        assertEquals("minecraft:creeper", parsed.getAsJsonArray("mobBlacklist").get(0).asString)
        assertEquals(30, parsed.getAsJsonObject("dimensionStartingLevels").get("minecraft:the_nether").asInt)
        assertEquals(80, parsed.getAsJsonObject("biomeMaximumLevels").get("minecraft:desert").asInt)
        assertEquals(40, parsed.getAsJsonObject("structureStartingLevels").get("minecraft:desert_pyramid").asInt)
        assertEquals(45, parsed.get("structureRadius").asInt)
        assertFalse(parsed.get("nameplatesEnabled").asBoolean)
        assertFalse(parsed.get("nameplateShowHealthText").asBoolean)
        assertEquals(38, parsed.get("nameplateDistance").asInt)
        assertEquals("minecraft:bat", parsed.getAsJsonArray("nameplateBlacklist").get(0).asString)
        assertTrue(parsed.get("nameplateShowLevel").asBoolean)
    }

    @Test
    fun createsNewFzzyFileWithoutTouchingOriginal() = inTempDirectory { root ->
        val legacyPath = root.resolve("difficultyex-config.json5")
        Files.writeString(legacyPath, legacy)
        assertTrue(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        val migrated = root.resolve("difficultyex/config.json5")
        assertTrue(Files.exists(migrated))
        assertEquals(7, JsonParser.parseString(Files.readString(migrated)).asJsonObject.get("startingLevel").asInt)
        assertEquals(legacy, Files.readString(legacyPath))
    }

    @Test
    fun existingModernConfigIsNeverOverwritten() = inTempDirectory { root ->
        Files.writeString(root.resolve("difficultyex-config.json5"), legacy)
        val modern = root.resolve("difficultyex/config.json5")
        Files.createDirectories(modern.parent)
        Files.writeString(modern, "{\"startingLevel\": 99}")
        assertFalse(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        assertEquals("{\"startingLevel\": 99}", Files.readString(modern))
    }

    @Test
    fun invalidOldFileIsNotMigratedOrDestroyed() = inTempDirectory { root ->
        val legacyPath = root.resolve("difficultyex-config.json5")
        Files.writeString(legacyPath, "{ invalid: [ }")
        assertFalse(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        assertFalse(Files.exists(root.resolve("difficultyex/config.json5")))
        assertTrue(Files.isRegularFile(legacyPath))
    }

    @Test
    fun malformedMapEntriesDoNotDiscardValidAreaRules() {
        val raw = """{
          structureScalingSettings: { startingLevels: {
            "minecraft:village_plains": 30,
            "minecraft:desert_pyramid": 40.0,
            "string": "20", "fraction": 2.5, "overflow": 2147483648,
            "negative": -5, "zero": 0, "boolean": true, "null": null,
            "object": {value: 10}
          } }
        }"""
        val parsed = JsonParser.parseString(DifficultyExLegacyConfigMigration.convert(raw)).asJsonObject
        val rules = parsed.getAsJsonObject("structureStartingLevels")
        assertEquals(setOf("minecraft:village_plains", "minecraft:desert_pyramid"), rules.keySet())
        assertEquals(30, rules.get("minecraft:village_plains").asInt)
        assertEquals(40, rules.get("minecraft:desert_pyramid").asInt)
    }

    @Test
    fun malformedBlacklistEntriesAreSkippedWithoutCoercingThemToStrings() {
        val raw = """{
          scalingLevelSettings: { mobBlacklist: ["minecraft:creeper", 15, true, null, {}, []] },
          visualSettings: { nameplateMobBlacklist: [null, "minecraft:bat", false] }
        }"""
        val parsed = JsonParser.parseString(DifficultyExLegacyConfigMigration.convert(raw)).asJsonObject
        assertEquals(listOf("minecraft:creeper"), parsed.getAsJsonArray("mobBlacklist").map { it.asString })
        assertEquals(listOf("minecraft:bat"), parsed.getAsJsonArray("nameplateBlacklist").map { it.asString })
    }

    @Test
    fun invalidNumericFieldsKeepModernDefaults() {
        val raw = """{
          scalingLevelSettings: {
            startingLevel: 1.5, maximumLevel: 2147483648,
            levelScalingMaxRadiusByBlocks: "50", levelAverageIncrement: 4.0,
            entityBaseHealthPercentage: 1.0e400
          },
          visualSettings: { nameplateEnabled: "false" }
        }"""
        val parsed = JsonParser.parseString(DifficultyExLegacyConfigMigration.convert(raw)).asJsonObject
        assertEquals(1.0, parsed.get("startingLevel").asDouble)
        assertEquals(1_000_000, parsed.get("maximumLevel").asInt)
        assertEquals(100, parsed.get("playerRadius").asInt)
        assertEquals(4, parsed.get("averageIncrement").asInt)
        assertEquals(0.08, parsed.get("healthPerLevel").asDouble)
        assertTrue(parsed.get("nameplatesEnabled").asBoolean)
    }

    @Test
    fun importsJsonAlternativeWhenJson5IsAbsent() = inTempDirectory { root ->
        val original = legacy.toByteArray()
        val source = root.resolve("difficultyex-config.json")
        Files.write(source, original)
        assertTrue(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        assertEquals(7, JsonParser.parseString(Files.readString(root.resolve("difficultyex/config.json5")))
            .asJsonObject.get("startingLevel").asInt)
        assertTrue(original.contentEquals(Files.readAllBytes(source)))
    }

    @Test
    fun json5TakesPriorityOverJsonAndMigrationRunsOnlyOnce() = inTempDirectory { root ->
        val json5 = root.resolve("difficultyex-config.json5")
        val json = root.resolve("difficultyex-config.json")
        Files.writeString(json5, legacy)
        Files.writeString(json, legacy.replace("startingLevel: 7", "startingLevel: 88"))
        assertTrue(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        val target = root.resolve("difficultyex/config.json5")
        val migrated = Files.readAllBytes(target)
        assertEquals(7, JsonParser.parseString(String(migrated)).asJsonObject.get("startingLevel").asInt)
        Files.writeString(json5, legacy.replace("startingLevel: 7", "startingLevel: 42"))
        assertFalse(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        assertTrue(migrated.contentEquals(Files.readAllBytes(target)))
        assertEquals(legacy.replace("startingLevel: 7", "startingLevel: 88"), Files.readString(json))
    }

    @Test
    fun malformedPreferredFileIsPreservedUntilUserRepairsIt() = inTempDirectory { root ->
        val preferred = root.resolve("difficultyex-config.json5")
        val alternative = root.resolve("difficultyex-config.json")
        val invalid = "{ broken: [ }"
        Files.writeString(preferred, invalid)
        Files.writeString(alternative, legacy)
        assertFalse(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        assertFalse(Files.exists(root.resolve("difficultyex/config.json5")))
        assertEquals(invalid, Files.readString(preferred))
        assertEquals(legacy, Files.readString(alternative))
        Files.writeString(preferred, legacy)
        assertTrue(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        Files.list(root.resolve("difficultyex")).use { files ->
            assertEquals(listOf("config.json5"), files.map { it.fileName.toString() }.toList())
        }
    }

    @Test
    fun blockedDestinationPreservesOriginalAndCanBeRetried() = inTempDirectory { root ->
        val source = root.resolve("difficultyex-config.json5")
        val destination = root.resolve("difficultyex")
        Files.writeString(source, legacy)
        Files.writeString(destination, "unrelated existing file")
        assertFalse(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        assertEquals(legacy, Files.readString(source))
        assertEquals("unrelated existing file", Files.readString(destination))
        Files.delete(destination)
        assertTrue(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        Files.list(destination).use { files ->
            assertEquals(listOf("config.json5"), files.map { it.fileName.toString() }.toList())
        }
    }

    @Test
    fun evenMalformedModernConfigPreventsLegacyOverwrite() = inTempDirectory { root ->
        val target = root.resolve("difficultyex/config.json5")
        Files.createDirectories(target.parent)
        val existing = "{ incomplete: [".toByteArray()
        Files.write(target, existing)
        Files.writeString(root.resolve("difficultyex-config.json5"), legacy)
        assertFalse(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        assertTrue(existing.contentEquals(Files.readAllBytes(target)))
        assertEquals(legacy, Files.readString(root.resolve("difficultyex-config.json5")))
    }

    @Test
    fun absentLegacyFilesCreateNothing() = inTempDirectory { root ->
        assertFalse(DifficultyExLegacyConfigMigration.migrateIfNeeded(root))
        Files.list(root).use { assertEquals(0, it.count()) }
    }

    private inline fun inTempDirectory(block: (Path) -> Unit) {
        val root = Files.createTempDirectory("difficultyex-legacy-test-")
        try {
            block(root)
        } finally {
            Files.walk(root).use { stream ->
                stream.sorted(Comparator.reverseOrder()).forEach { Files.deleteIfExists(it) }
            }
        }
    }
}
