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
        val radius = DifficultyStructureGeometry.effectiveRadius(config.structureRadius)
        val pos = mob.blockPosition()
        val types = configured.toHashSet()
        val registry = world.registryAccess().registryOrThrow(Registries.STRUCTURE)
        val manager = world.structureManager()
        val found = mutableSetOf<String>()
        val visited = mutableSetOf<String>()

        for (chunkX in DifficultyStructureGeometry.chunkRange(pos.x, radius)) {
            for (chunkZ in DifficultyStructureGeometry.chunkRange(pos.z, radius)) {
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
                    if (DifficultyStructureGeometry.contains(
                            pos.x, pos.z, box.minX(), box.maxX(), box.minZ(), box.maxZ(), radius
                        )) {
                        found.add(id)
                        if (found.size == types.size) return found
                    }
                }
            }
        }
        return found
    }
}
