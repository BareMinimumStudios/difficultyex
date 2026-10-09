package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DifficultyConfigDiagnosticsTest {
    private val dimensions = setOf("minecraft:overworld")
    private val biomes = setOf("minecraft:plains")
    private val structures = setOf("minecraft:village_plains")
    private val entities = setOf("minecraft:zombie", "minecraft:cow")
    private fun inspect(settings: DifficultySettings) = DifficultyConfigDiagnostics.inspect(settings, dimensions, biomes, structures, entities)

    @Test
    fun acceptsRegisteredRulesAndMatchingCaseInsensitivePatterns() {
        assertTrue(inspect(DifficultySettings(dimensionStartingLevels=mapOf("minecraft:overworld" to 10), biomeMaximumLevels=mapOf("minecraft:plains" to 20), structureStartingLevels=mapOf("minecraft:village_plains" to 30), entityStartingLevels=mapOf("MINECRAFT:ZOMBIE" to 10), mobBlacklist=listOf("minecraft:(zombie|cow)"), playerLevelFormula="x*2")).isEmpty())
    }

    @Test
    fun reportsInactiveRegistryRulesWithoutChangingThem() {
        val settings = DifficultySettings(dimensionStartingLevels=mapOf("missing:dimension" to 25), biomeMaximumLevels=mapOf("minecraft:plainz" to 20), structureMaximumLevels=mapOf("missing:structure" to 30))
        val issues = inspect(settings)
        assertEquals(3, issues.size)
        assertTrue(issues.any { it.contains("dimensionStartingLevels") && it.contains("missing:dimension") })
        assertEquals(25, settings.dimensionStartingLevels["missing:dimension"])
    }

    @Test
    fun distinguishesBrokenRegexFromValidPatternsWithNoMatches() {
        val issues = inspect(DifficultySettings(entityStartingLevels=mapOf("[broken" to 25), mobBlacklist=listOf("missing:.*", "missing:.*")))
        assertEquals(2, issues.size)
        assertTrue(issues.any { it.contains("not a valid regular expression") })
        assertTrue(issues.any { it.contains("matches no registered entity") })
    }

    @Test
    fun reportsFormulaFallbackAndConflictingGlobalBounds() {
        val issues = inspect(DifficultySettings(playerLevelFormula="x + (", startingLevel=30, maximumLevel=20))
        assertEquals(2, issues.size)
        assertTrue(issues.any { it.contains("fallback formula x") })
        assertTrue(issues.any { it.contains("maximum takes priority") })
    }

    @Test
    fun reportsNonFiniteRatesButAcceptsDisabledFiniteRates() {
        assertTrue(inspect(DifficultySettings(healthPerLevel=0.0, armorPerLevel=-1.0)).isEmpty())
        assertEquals(4, inspect(DifficultySettings(healthPerLevel=Double.NaN, armorPerLevel=Double.POSITIVE_INFINITY, damagePerLevel=Double.NEGATIVE_INFINITY, experiencePerLevel=Double.NaN)).size)
    }
}
