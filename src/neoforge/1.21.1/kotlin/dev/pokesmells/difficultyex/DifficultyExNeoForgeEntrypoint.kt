package dev.pokesmells.difficultyex

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import net.minecraft.world.entity.Mob
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent

@Mod(DifficultyEx.MOD_ID)
class DifficultyExNeoForgeEntrypoint(modBus: IEventBus) {
    init {
        modBus.addListener { event: FMLCommonSetupEvent ->
            event.enqueueWork {
                DifficultyExLegacyConfigMigration.migrateIfNeeded(FMLPaths.CONFIGDIR.get())
                val config = ConfigApi.registerAndLoadConfig(::DifficultyExConfig)
                DifficultyEx.configure(config.toSnapshot())
            }
        }
        NeoForge.EVENT_BUS.addListener(::onEntityJoin)
        NeoForge.EVENT_BUS.addListener { event: RegisterCommandsEvent ->
            DifficultyExCommands.register(event.dispatcher)
        }
    }

    private fun onEntityJoin(event: EntityJoinLevelEvent) {
        val mob = event.entity as? Mob ?: return
        DifficultyEx.onEntityLoad(mob)
    }
}
