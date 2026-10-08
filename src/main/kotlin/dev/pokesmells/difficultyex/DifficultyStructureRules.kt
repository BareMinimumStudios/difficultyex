package dev.pokesmells.difficultyex

import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.ChunkPos

/**
 * Finds configured structures in already loaded chunks around the mob.
 * Do not use structure locate or force-load APIs during mob spawning.
 */
object DifficultyStructureRules {
    @JvmStatic
    fun applicableIds(world: ServerLevel, mob: Mob, config: DifficultySettings): Set<String> {
        val configured = config.structureStartingLevels.keys + config.structureMaximumLevels.keys
        if (configured.isEmpty()) return emptySet()

        // Bound work per spawned mob, even with a malformed config.
        val radius = config.structureRadius.coerceIn(0, 128)
        val pos = mob.blockPosition()
        val types = configured.toHashSet()
        val registry = world.registryAccess().registryOrThrow(Registries.STRUCTURE)
        val manager = world.structureManager()
        val found = mutableSetOf<String>()
        val visited = mutableSetOf<String>()

        for (chunkX in (pos.x - radius shr 4)..(pos.x + radius shr 4)) {
            for (chunkZ in (pos.z - radius shr 4)..(pos.z + radius shr 4)) {
                if (!world.hasChunk(chunkX, chunkZ)) continue

                // Starts here include structure references that overlap the current chunk.
                for (start in manager.startsForStructure(ChunkPos(chunkX, chunkZ)) {
                    type -> registry.getKey(type).toString() in types
                }) {
                    if (!start.isValid) continue
                    val id = registry.getKey(start.structure).toString()
                    val key = "$id:${start.chunkPos.toLong()}"
                    if (!visited.add(key)) continue

                    val box = start.boundingBox
                    // Structure influence is horizontal: spawning above/below a structure
                    // can still be affected when inside its configured surroundings.
                    if (pos.x >= box.minX() - radius && pos.x <= box.maxX() + radius &&
                        pos.z >= box.minZ() - radius && pos.z <= box.maxZ() + radius) {
                        found.add(id)
                        if (found.size == types.size) return found
                    }
                }
            }
        }
        return found
    }
}
