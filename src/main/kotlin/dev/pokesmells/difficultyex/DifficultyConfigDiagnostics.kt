package dev.pokesmells.difficultyex

import redempt.crunch.Crunch
import redempt.crunch.functional.ExpressionEnv

object DifficultyConfigDiagnostics {
    fun inspect(settings: DifficultySettings, dimensions: Set<String>, biomes: Set<String>, structures: Set<String>, entities: Set<String>): List<String> {
        val issues = mutableListOf<String>()
        fun registryRules(name: String, rules: Map<String, Int>, known: Set<String>) {
            for (id in rules.keys.sorted()) {
                if (id !in known) issues.add("$name: '$id' is not registered on this server.")
            }
        }
        fun patterns(name: String, values: Collection<String>) {
            for (pattern in values.distinct().sorted()) {
                val regex = runCatching { Regex(pattern, RegexOption.IGNORE_CASE) }.getOrNull()
                when {
                    regex == null -> issues.add("$name: '$pattern' is not a valid regular expression.")
                    entities.none { pattern.equals(it, ignoreCase = true) || regex.matches(it) } ->
                        issues.add("$name: '$pattern' matches no registered entity on this server.")
                }
            }
        }
        registryRules("dimensionStartingLevels", settings.dimensionStartingLevels, dimensions)
        registryRules("dimensionMaximumLevels", settings.dimensionMaximumLevels, dimensions)
        registryRules("biomeStartingLevels", settings.biomeStartingLevels, biomes)
        registryRules("biomeMaximumLevels", settings.biomeMaximumLevels, biomes)
        registryRules("structureStartingLevels", settings.structureStartingLevels, structures)
        registryRules("structureMaximumLevels", settings.structureMaximumLevels, structures)
        patterns("entityStartingLevels", settings.entityStartingLevels.keys)
        patterns("entityMaximumLevels", settings.entityMaximumLevels.keys)
        patterns("mobBlacklist", settings.mobBlacklist)
        patterns("nameplateBlacklist (server copy)", settings.nameplateBlacklist)
        runCatching { Crunch.compileExpression(settings.playerLevelFormula, ExpressionEnv().setVariableNames("x")) }
            .onFailure { issues.add("playerLevelFormula: cannot compile '${settings.playerLevelFormula}'; spawning uses the fallback formula x.") }
        if (settings.startingLevel > settings.maximumLevel) {
            issues.add("startingLevel exceeds maximumLevel; the maximum takes priority.")
        }
        for ((name, rate) in listOf("healthPerLevel" to settings.healthPerLevel, "armorPerLevel" to settings.armorPerLevel, "damagePerLevel" to settings.damagePerLevel, "experiencePerLevel" to settings.experiencePerLevel)) {
            if (!rate.isFinite()) issues.add("$name is not finite; its scaling is disabled.")
        }
        return issues
    }
}
