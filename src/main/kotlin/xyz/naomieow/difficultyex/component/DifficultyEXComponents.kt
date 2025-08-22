package xyz.naomieow.difficultyex.component

import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import net.minecraft.world.entity.Mob
import xyz.naomieow.difficultyex.DifficultyEX
import xyz.naomieow.difficultyex.config.DifficultyEXConfigModel

class DifficultyEXComponents: EntityComponentInitializer {
    companion object {
        @JvmField
        val ENTITY_DATA: ComponentKey<ILevelableEntityComponent> =
            ComponentRegistry.getOrCreate(DifficultyEX.id("entity_data"), ILevelableEntityComponent::class.java)

        val PLAYER_DATA: ComponentKey<IPlayerDataComponent> = ComponentRegistry.getOrCreate(DifficultyEX.id("player-data"),IPlayerDataComponent::class.java)
    }

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(Mob::class.java, ENTITY_DATA) { LevelableEntityComponent(it) }
        registry.registerForPlayers(PLAYER_DATA, { PlayerDataComponent(DifficultyEXConfigModel.Difficulty.Medium) }, RespawnCopyStrategy.ALWAYS_COPY)
    }

}