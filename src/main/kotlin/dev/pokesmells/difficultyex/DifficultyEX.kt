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
import kotlin.math.roundToInt

object DifficultyEx {
    const val MOD_ID = "difficultyex"
    private val logger = LoggerFactory.getLogger(MOD_ID)
    @Volatile var settings = DifficultySettings()
        private set

    private val expressions = ConcurrentHashMap<String, CompiledExpression>()
    private val maxHealthModifier = id("level_health")
    private val armorModifier = id("level_armor")
    private val attackModifier = id("level_attack")

    @JvmStatic fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)

    @JvmStatic fun configure(snapshot: DifficultySettings) {
        settings = snapshot
        expressions.clear()
    }

    /** Spawn once, reload without re-rolling the mob's persisted level. */
    @JvmStatic fun onEntityLoad(mob: Mob) {
        val world = mob.level() as? ServerLevel ?: return
        val state = mob as MobLevelAccess
        if (state.difficultyExGetLevel() > 0) {
            applyAttributes(mob, state.difficultyExGetLevel(), false)
            return
        }
        val cfg = settings
        val mobId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.type).toString()
        if (cfg.mobBlacklist.any { match(it, mobId) }) return
        val radius = cfg.playerRadius.coerceIn(0, 4096).toDouble()
        val nearby = world.players().filter { it.distanceToSqr(mob) <= radius * radius }
        val expr = expressions.computeIfAbsent(cfg.playerLevelFormula) {
            runCatching { Crunch.compileExpression(it, ExpressionEnv().setVariableNames("x")) }
                .onFailure { error -> logger.warn("Invalid difficulty formula: {}", it, error) }
                .getOrElse { Crunch.compileExpression("x", ExpressionEnv().setVariableNames("x")) }
        }
        val players = nearby.map { expr.evaluate(PlayerStateService.get(it).level.toDouble()) }
            .filter { it.isFinite() }
        val mean = if (players.isEmpty()) cfg.startingLevel else players.average().roundToInt()
        val lower = cfg.averageDecrement.coerceIn(0, 1000000)
        val upper = cfg.averageIncrement.coerceIn(0, 1000000)
        var result = (mean.toLong() + (-lower..upper).random()).coerceIn(1, Int.MAX_VALUE.toLong()).toInt()
        val dimension = world.dimension().location().toString()
        val biome = world.getBiome(mob.blockPosition()).unwrapKey().orElse(null)?.location()?.toString()
        fun clamp(lowerBound: Int?, upperBound: Int?) {
            lowerBound?.let { result = result.coerceAtLeast(it) }
            upperBound?.let { result = result.coerceAtMost(it) }
        }
        clamp(cfg.dimensionStartingLevels[dimension], cfg.dimensionMaximumLevels[dimension])
        if (biome != null) clamp(cfg.biomeStartingLevels[biome], cfg.biomeMaximumLevels[biome])
        cfg.entityStartingLevels.forEach { (id, value) -> if (match(id, mobId)) result = result.coerceAtLeast(value) }
        cfg.entityMaximumLevels.forEach { (id, value) -> if (match(id, mobId)) result = result.coerceAtMost(value) }
        val minimum = cfg.startingLevel.coerceAtLeast(1)
        result = result.coerceIn(minimum, cfg.maximumLevel.coerceAtLeast(minimum))
        state.difficultyExSetLevel(result)
        applyAttributes(mob, result, true)
    }

    /** Operator override: update the synchronized level and keep the mob's health percentage. */
    @JvmStatic fun setMobLevel(mob: Mob, level: Int) {
        val currentMaximum = mob.maxHealth.coerceAtLeast(1f)
        val healthFraction = (mob.health / currentMaximum).coerceIn(0f, 1f)
        (mob as MobLevelAccess).difficultyExSetLevel(level.coerceAtLeast(1))
        applyAttributes(mob, level.coerceAtLeast(1), false)
        mob.health = (healthFraction * mob.maxHealth).coerceAtLeast(1f)
    }

    @JvmStatic fun scaledExperience(original: Int, mob: Mob): Int {
        val level = (mob as MobLevelAccess).difficultyExGetLevel()
        if (level < 1) return original
        val factor = 1.0 + level * settings.experiencePerLevel.coerceIn(0.0, 100.0)
        return (original * factor).coerceIn(0.0, Int.MAX_VALUE.toDouble()).toInt()
    }

    private fun applyAttributes(mob: Mob, level: Int, heal: Boolean) {
        val cfg = settings
        fun modify(attribute: Holder<Attribute>, key: ResourceLocation, rate: Double) {
            val instance = mob.attributes.getInstance(attribute) ?: return
            instance.removeModifier(key)
            val multiplier = (level * rate.coerceIn(0.0, 100.0)).coerceIn(0.0, 1000.0)
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
        pattern.equals(value, ignoreCase = true) ||
            runCatching { Regex(pattern, RegexOption.IGNORE_CASE).matches(value) }.getOrDefault(false)
}
