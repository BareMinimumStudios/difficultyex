package xyz.naomieow.difficultyex

import com.bibireden.playerex.ext.level
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.phys.AABB
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redempt.crunch.Crunch
import redempt.crunch.functional.EvaluationEnvironment
import xyz.naomieow.difficultyex.component.DifficultyEXComponents
import xyz.naomieow.difficultyex.config.DifficultyEXConfig
import xyz.naomieow.difficultyex.event.EntityLevelingEvents
import xyz.naomieow.difficultyex.ext.difficultyExLevel
import xyz.naomieow.difficultyex.network.NameplateServerPacket
import kotlin.math.pow

object DifficultyEX : ModInitializer {
	const val MOD_ID: String = "difficultyex"
	val logger: Logger = LoggerFactory.getLogger(MOD_ID)

	@JvmField
	val CONFIG: DifficultyEXConfig = DifficultyEXConfig.createAndLoad()

	private const val SCALING_VARIABLE = "x"


	override fun onInitialize() {
		NameplateServerPacket.init()

		ServerEntityEvents.ENTITY_LOAD.register { entity, world ->
			if (entity !is Mob) return@register

			var level: Int

			val dimensionSettings = CONFIG.dimensionSettings
			val biomeSettings = CONFIG.biomeScalingSettings
			val structureSettings = CONFIG.structureScalingSettings
			val scalingLevelSettings = CONFIG.scalingLevelSettings

			val levelAverageAdjustment = (-kotlin.math.abs(scalingLevelSettings.levelAverageDecrement)..kotlin.math.abs(scalingLevelSettings.levelAverageIncrement)).random()


			// interesting way to get the structure result...

			val structureRegistry: Registry<Structure> = world.level.registryAccess().registryOrThrow(Registries.STRUCTURE)
			val structureHolders = HolderSet.direct(structureRegistry.holders().filter { structureSettings.maximumLevels.contains(it.key().location()) }.toList())

			val structureSearchResult = world.level.chunkSource.generator.findNearestMapStructure(
				world.level, structureHolders, entity.blockPosition(), structureSettings.radius, false
			)

			// first, let us start the calculation average.
			val players = PlayerLookup.world(world).filter {
				val distance = AABB(entity.blockPosition()).inflate(CONFIG.scalingLevelSettings.levelScalingMaxRadiusByBlocks.toDouble())
				return@filter distance.contains(it.position())
			}

			// implement expression and stuff
			val expression = EvaluationEnvironment()
			expression.setVariableNames(SCALING_VARIABLE)
			val expr = Crunch.compileExpression(CONFIG.scalingLevelSettings.levelScalingByPlayerFormula, expression)
			val playerComputedAverage = players.map { expr.evaluate(it.level) }.average().toInt()

			level = kotlin.math.max(0, playerComputedAverage + levelAverageAdjustment)

			// clamp down the level based on dimension
			val dimensionKey = world.dimension().location()
			dimensionSettings.maximumLevels[dimensionKey]?.let {
				level = kotlin.math.min(level, it)
			}
			dimensionSettings.startingLevels[dimensionKey]?.let {
				level = kotlin.math.max(level, it)
			}

			// clamp down the level based on biome // entities
			val biomeKey = world.getBiome(entity.blockPosition()).unwrapKey().get().location()
			biomeSettings.maximumLevels[biomeKey]?.let {
				level = kotlin.math.min(level, it)
			}
			biomeSettings.startingLevels[biomeKey]?.let {
				level = kotlin.math.max(level, it)
			}

			if (structureSearchResult != null) {
				// clamp down maximum level based on located structure
				val key = structureSearchResult.second.unwrapKey().get().location()

				structureSettings.maximumLevels[key]?.let {
					level = kotlin.math.min(level, it)
				}

				structureSettings.startingLevels[key]?.let {
					level = kotlin.math.max(level, it)
				}
			}

			// finally, entities

			val entityKey = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type)

			for ((key, entry) in scalingLevelSettings.entityMaximumLevels) {
				if (key.toRegex(RegexOption.IGNORE_CASE).matches(entityKey.toString())) {
					level = kotlin.math.min(level, entry)
				}
			}

			for ((key, entry) in scalingLevelSettings.entityStartingLevels) {
				if (key.toRegex(RegexOption.IGNORE_CASE).matches(entityKey.toString())) {
					level = kotlin.math.max(level, entry)
				}
			}

			// clamp down the level based on max level scaling
			level = kotlin.math.max(kotlin.math.min(level, scalingLevelSettings.maximumLevel), CONFIG.scalingLevelSettings.startingLevel)

			entity.difficultyExLevel = kotlin.math.max(1, level)

			val maxHealthAttribute = entity.attributes.getInstance(Attributes.MAX_HEALTH)
			if (maxHealthAttribute != null) {
				entity.health = entity.maxHealth
			}

			EntityLevelingEvents.SPAWNED.invoker().onEntitySpawned(entity, level)
		}
	}

	fun id(id: String): ResourceLocation = ResourceLocation.tryBuild(MOD_ID, id)!!
}