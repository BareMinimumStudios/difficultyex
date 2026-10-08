package xyz.naomieow.difficultyex

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.minecraft.world.entity.Mob

object DifficultyExFabricEntrypoint : ModInitializer {
    override fun onInitialize() {
        val config = ConfigApi.registerAndLoadConfig(::DifficultyExConfig)
        DifficultyEx.configure(config.toSnapshot())
        ServerEntityEvents.ENTITY_LOAD.register { entity, _ ->
            if (entity is Mob) DifficultyEx.onEntityLoad(entity)
        }
    }
}
