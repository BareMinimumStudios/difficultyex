package dev.pokesmells.difficultyex

import java.util.concurrent.ConcurrentHashMap

/**
 * Reuse case-insensitive blacklist patterns across spawn checks and rendered
 * nameplates. Invalid patterns are cached as failures rather than retried on
 * every frame. Configuration reloads clear this instance's cache.
 */
class DifficultyPatternMatcher {
    private sealed interface Entry {
        data class Compiled(val regex: Regex) : Entry
        data object Invalid : Entry
    }

    private val entries = ConcurrentHashMap<String, Entry>()

    fun matches(pattern: String, value: String): Boolean {
        if (pattern.equals(value, ignoreCase = true)) return true
        val entry = entries[pattern] ?: if (entries.size < 256) {
            entries.computeIfAbsent(pattern, ::compile)
        } else {
            compile(pattern)
        }
        return entry is Entry.Compiled && entry.regex.matches(value)
    }

    fun clear() {
        entries.clear()
    }

    private fun compile(pattern: String): Entry =
        runCatching { Entry.Compiled(Regex(pattern, RegexOption.IGNORE_CASE)) }
            .getOrElse { Entry.Invalid }
}
