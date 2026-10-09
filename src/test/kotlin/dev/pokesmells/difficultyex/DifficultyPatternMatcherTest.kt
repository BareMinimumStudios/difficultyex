package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DifficultyPatternMatcherTest {
    private val matcher = DifficultyPatternMatcher()

    @Test
    fun exactEntityIdsRemainCaseInsensitive() {
        assertTrue(matcher.matches("minecraft:zombie", "MINECRAFT:ZOMBIE"))
        assertFalse(matcher.matches("minecraft:zombie", "minecraft:skeleton"))
    }

    @Test
    fun regexBlacklistsWorkForMultipleEntityTypes() {
        assertTrue(matcher.matches("minecraft:(zombie|skeleton)", "minecraft:zombie"))
        assertTrue(matcher.matches("minecraft:(zombie|skeleton)", "minecraft:skeleton"))
        assertFalse(matcher.matches("minecraft:(zombie|skeleton)", "minecraft:cow"))
    }

    @Test
    fun invalidRegexNeverCrashesAFrameOrSpawn() {
        repeat(20) {
            assertFalse(matcher.matches("[unclosed", "minecraft:zombie"))
        }
    }

    @Test
    fun clearRetainsMatchingBehaviorAfterConfigurationChange() {
        assertTrue(matcher.matches("minecraft:.*", "minecraft:creeper"))
        matcher.clear()
        assertTrue(matcher.matches("minecraft:.*", "minecraft:creeper"))
        assertFalse(matcher.matches("minecraft:.*", "modded:creeper"))
    }
}
