package xyz.naomieow.difficultyex

import com.bibireden.data_attributes.api.event.EntityAttributeModifiedEvents
import com.bibireden.playerex.ext.level
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.phys.AABB
import net.objecthunter.exp4j.ExpressionBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.naomieow.difficultyex.config.DifficultyEXConfig
import xyz.naomieow.difficultyex.entity.player.PlayerLevelHandler
import xyz.naomieow.difficultyex.event.EntityLevelingEvents
import xyz.naomieow.difficultyex.ext.difficultyExLevel

object DifficultyEX : ModInitializer {
	const val MOD_ID: String = "difficultyex"
	val logger: Logger = LoggerFactory.getLogger(MOD_ID)
	val CONFIG: DifficultyEXConfig = DifficultyEXConfig.createAndLoad()

	@JvmField
	val PLAYER_LEVEL_HANDLER = PlayerLevelHandler()

	const val SCALING_VARIABLE = "x"


	override fun onInitialize() {
		ServerPlayConnectionEvents.JOIN.register { listener, _, _ ->
			PLAYER_LEVEL_HANDLER.insert(listener.player)
		}

		ServerEntityEvents.ENTITY_LOAD.register { entity, world ->
			if (entity !is Monster) return@register

			var level = CONFIG.scalingLevelSettings.startingLevel

			val dimensionSettings = CONFIG.dimensionSettings
			val biomeSettings = CONFIG.biomeScalingSettings
			val structureSettings = CONFIG.structureScalingSettings
			val scalingLevelSettings = CONFIG.scalingLevelSettings

			val levelAverageAdjustment = (-scalingLevelSettings.levelAverageDecrement..scalingLevelSettings.levelAverageIncrement).random()


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
			val expression = ExpressionBuilder(CONFIG.scalingLevelSettings.levelScalingByPlayerFormula).variable(SCALING_VARIABLE).build()
			val playerComputedAverage = players.map { expression.setVariable(SCALING_VARIABLE, it.level).evaluate() }.average().toInt()

			level += kotlin.math.max(0, playerComputedAverage + levelAverageAdjustment)

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
			scalingLevelSettings.entityMaximumLevels[entityKey]?.let {
				level = kotlin.math.min(level, it)
			}
			scalingLevelSettings.entityStartingLevels[entityKey]?.let {
				level = kotlin.math.max(level, it)
			}

			// clamp down the level based on max level scaling
			level = kotlin.math.max(kotlin.math.min(level, scalingLevelSettings.maximumLevel), CONFIG.scalingLevelSettings.startingLevel)

			entity.difficultyExLevel = kotlin.math.max(0, level)

			EntityLevelingEvents.SPAWNED.invoker().onEntitySpawned(entity, level)
		}

		EntityAttributeModifiedEvents.MODIFIED.register { attribute, entity, modifier, value, _ ->
			if (entity !is ServerPlayer) return@register
			PLAYER_LEVEL_HANDLER.insert(entity)
		}
	}

	fun id(id: String): ResourceLocation = ResourceLocation.tryBuild(MOD_ID, id)!!
}