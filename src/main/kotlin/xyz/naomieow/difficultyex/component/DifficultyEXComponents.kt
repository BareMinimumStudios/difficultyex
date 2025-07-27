package xyz.naomieow.difficultyex.component

import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import xyz.naomieow.difficultyex.DifficultyEX

class DifficultyEXComponents: EntityComponentInitializer {
    companion object {
        @JvmField
        val ENTITY_DATA: ComponentKey<ILevelableEntityComponent> =
            ComponentRegistry.getOrCreate(DifficultyEX.id("entity_data"), ILevelableEntityComponent::class.java)
    }

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.registerFor(Mob::class.java, ENTITY_DATA) { LevelableEntityComponent(it) }
    }

}