package xyz.naomieow.difficultyex

import net.fabricmc.api.ModInitializer
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object DifficultyEX : ModInitializer {
	const val MOD_ID: String = "difficultyex"
	val logger: Logger = LoggerFactory.getLogger(MOD_ID)
	const val CONFIG: DifficultyEXConfig = DifficultyEXConfig.createAndLoad()

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
	}

	fun id(id: String): ResourceLocation? {
		return ResourceLocation.tryBuild(MOD_ID, id);
	}
}