package xyz.naomieow.difficultyex.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity

object EntityLevelingEvents {
    /** When the entity has been spawned in with their provided level. */
    @JvmField
    val SPAWNED: Event<Spawned<LivingEntity>> = EventFactory.createArrayBacked(Spawned::class.java) {
        Spawned { entity, level -> it.forEach { it.onEntitySpawned(entity, level) } }
    }

    /** When an entity has their level altered. */
    @JvmField
    val CHANGED: Event<Changed<LivingEntity>> = EventFactory.createArrayBacked(Changed::class.java) {
        Changed { entity, level -> it.forEach { it.onEntityLevelChanged(entity, level) } }
    }

    fun interface Changed<E: Entity> {
        fun onEntityLevelChanged(entity: E, level: Int)
    }

    fun interface Spawned<E: Entity> {
        fun onEntitySpawned(entity: E, level: Int)
    }
}