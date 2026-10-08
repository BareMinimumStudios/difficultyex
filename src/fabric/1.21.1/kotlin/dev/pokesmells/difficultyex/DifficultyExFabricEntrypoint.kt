package dev.pokesmells.difficultyex

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.world.entity.Mob

object DifficultyExFabricEntrypoint : ModInitializer {
    override fun onInitialize() {
        DifficultyExLegacyConfigMigration.migrateIfNeeded(FabricLoader.getInstance().configDir)
        val config = ConfigApi.registerAndLoadConfig(::DifficultyExConfig)
        DifficultyEx.configure(config.toSnapshot())
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ -> DifficultyExCommands.register(dispatcher) }
        ServerEntityEvents.ENTITY_LOAD.register { entity, _ ->
            if (entity is Mob) DifficultyEx.onEntityLoad(entity)
        }
    }
}
