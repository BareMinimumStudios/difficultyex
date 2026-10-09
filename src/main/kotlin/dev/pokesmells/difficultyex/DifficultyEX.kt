package dev.pokesmells.difficultyex

import com.bibireden.playerex.state.PlayerStateService
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import org.slf4j.LoggerFactory
import redempt.crunch.Crunch
import redempt.crunch.CompiledExpression
import redempt.crunch.functional.ExpressionEnv
import java.util.concurrent.ConcurrentHashMap

object DifficultyEx {
    const val MOD_ID = "difficultyex"
    private val logger = LoggerFactory.getLogger(MOD_ID)
    @Volatile var settings = DifficultySettings()
        private set

    private val expressions = ConcurrentHashMap<String, CompiledExpression>()
    private val blacklistMatcher = DifficultyPatternMatcher()
    private val maxHealthModifier = id("level_health")
    private val armorModifier = id("level_armor")
    private val attackModifier = id("level_attack")

    @JvmStatic fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)

    @JvmStatic fun configure(snapshot: DifficultySettings) {
        settings = snapshot
        expressions.clear()
        blacklistMatcher.clear()
    }

    /** Spawn once, reload without re-rolling the mob's persisted level. */
    @JvmStatic fun onEntityLoad(mob: Mob) {
        val world = mob.level() as? ServerLevel ?: return
        val state = mob as MobLevelAccess
        if (state.difficultyExGetLevel() > 0) {
            applyAttributes(mob, state.difficultyExGetLevel(), false)
            val savedHealth = state.difficultyExConsumeSavedHealth()
            if (savedHealth.isFinite()) {
                mob.health = savedHealth.coerceIn(0f, mob.maxHealth)
            }
            return
        }
        val cfg = settings
        val mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.type).toString()
        if (cfg.mobBlacklist.any { match(it, mobId) }) return
        val nearby = world.players().filter {
            DifficultyPlayerLevelMath.eligible(it.isSpectator, it.distanceToSqr(mob), cfg.playerRadius)
        }
        val expr = expressions.computeIfAbsent(cfg.playerLevelFormula) {
            runCatching { Crunch.compileExpression(it, ExpressionEnv().setVariableNames("x")) }
                .onFailure { error -> logger.warn("Invalid difficulty formula: {}", it, error) }
                .getOrElse { Crunch.compileExpression("x", ExpressionEnv().setVariableNames("x")) }
        }
        val transformedLevels = nearby.map { expr.evaluate(PlayerStateService.get(it).level.toDouble()) }
        val mean = DifficultyPlayerLevelMath.averageTransformedLevels(transformedLevels, cfg.startingLevel)
        val lower = cfg.averageDecrement.coerceIn(0, 1000000)
        val upper = cfg.averageIncrement.coerceIn(0, 1000000)
        var result = (mean.toLong() + (-lower..upper).random()).coerceIn(1, Int.MAX_VALUE.toLong()).toInt()
        val dimension = world.dimension().location().toString()
        val biome = world.getBiome(mob.blockPosition()).unwrapKey().orElse(null)?.location()?.toString()
        val matchingStructures = DifficultyStructureRules.applicableIds(world, mob, cfg)
        val minimums = mutableListOf<Int>()
        val maximums = mutableListOf<Int>()
        cfg.dimensionStartingLevels[dimension]?.let(minimums::add)
        cfg.dimensionMaximumLevels[dimension]?.let(maximums::add)
        if (biome != null) {
            cfg.biomeStartingLevels[biome]?.let(minimums::add)
            cfg.biomeMaximumLevels[biome]?.let(maximums::add)
        }
        matchingStructures.forEach { id ->
            cfg.structureStartingLevels[id]?.let(minimums::add)
            cfg.structureMaximumLevels[id]?.let(maximums::add)
        }
        cfg.entityStartingLevels.forEach { (id, value) -> if (match(id, mobId)) minimums.add(value) }
        cfg.entityMaximumLevels.forEach { (id, value) -> if (match(id, mobId)) maximums.add(value) }
        result = DifficultyLevelBounds.apply(result, cfg.startingLevel, cfg.maximumLevel, minimums, maximums)
        state.difficultyExSetLevel(result)
        applyAttributes(mob, result, true)
    }

    /** Operator override: update the synchronized level and keep the mob's health percentage. */
    @JvmStatic fun setMobLevel(mob: Mob, level: Int) {
        val currentMaximum = mob.maxHealth
        val currentHealth = mob.health
        (mob as MobLevelAccess).difficultyExSetLevel(level.coerceAtLeast(1))
        applyAttributes(mob, level.coerceAtLeast(1), false)
        mob.health = DifficultyHealthMath.rescale(currentHealth, currentMaximum, mob.maxHealth)
    }

    @JvmStatic fun scaledExperience(original: Int, mob: Mob): Int =
        DifficultyExperienceMath.scale(
            original,
            (mob as MobLevelAccess).difficultyExGetLevel(),
            settings.experiencePerLevel
        )

    private fun applyAttributes(mob: Mob, level: Int, heal: Boolean) {
        val cfg = settings
        fun modify(attribute: Holder<Attribute>, key: ResourceLocation, rate: Double) {
            val instance = mob.attributes.getInstance(attribute) ?: return
            instance.removeModifier(key)
            val multiplier = DifficultyAttributeMath.multiplier(level, rate)
            if (multiplier > 0) instance.addTransientModifier(
                AttributeModifier(key, multiplier, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
            )
        }
        modify(Attributes.MAX_HEALTH, maxHealthModifier, cfg.healthPerLevel)
        modify(Attributes.ARMOR, armorModifier, cfg.armorPerLevel)
        modify(Attributes.ATTACK_DAMAGE, attackModifier, cfg.damagePerLevel)
        if (heal) mob.health = mob.maxHealth
    }

    @JvmStatic fun match(pattern: String, value: String): Boolean =
        blacklistMatcher.matches(pattern, value)
}
