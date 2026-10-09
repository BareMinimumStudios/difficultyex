package dev.pokesmells.difficultyex

import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Mob
import net.minecraft.world.level.ChunkPos

/**
 * Finds configured structure references within already loaded chunks.
 *
 * Vanilla StructureManager#startsForStructure can load the structure's origin
 * chunk even when the reference chunk is loaded. Only use getChunkNow, both
 * for reference chunks and origin chunks, during entity initialization.
 */
object DifficultyStructureRules {
    @JvmStatic
    fun applicableIds(world: ServerLevel, mob: Mob, config: DifficultySettings): Set<String> {
        val configured = config.structureStartingLevels.keys + config.structureMaximumLevels.keys
        if (configured.isEmpty()) return emptySet()

        val radius = DifficultyStructureGeometry.effectiveRadius(config.structureRadius)
        val pos = mob.blockPosition()
        val types = configured.toHashSet()
        val registry = world.registryAccess().registryOrThrow(Registries.STRUCTURE)
        val chunks = world.chunkSource
        val found = mutableSetOf<String>()
        val visited = mutableSetOf<String>()

        for (chunkX in DifficultyStructureGeometry.chunkRange(pos.x, radius)) {
            for (chunkZ in DifficultyStructureGeometry.chunkRange(pos.z, radius)) {
                // Never synchronously request a neighboring chunk while a mob spawns.
                val nearbyChunk = chunks.getChunkNow(chunkX, chunkZ) ?: continue
                for ((structure, references) in nearbyChunk.allReferences) {
                    val id = registry.getKey(structure).toString()
                    if (id !in types || id in found) continue
                    val referenceIterator = references.longIterator()
                    while (referenceIterator.hasNext()) {
                        val reference = referenceIterator.nextLong()
                        val key = "$id:$reference"
                        if (!visited.add(key)) continue

                        // Structure references may point to an origin several chunks away.
                        // Do not let a reference cause that chunk to generate or load.
                        val origin = ChunkPos(reference)
                        val originChunk = chunks.getChunkNow(origin.x, origin.z) ?: continue
                        val start = originChunk.getStartForStructure(structure) ?: continue
                        if (!start.isValid) continue

                        val box = start.boundingBox
                        // Horizontal influence is inclusive, even at bounding-box corners.
                        if (DifficultyStructureGeometry.contains(
                                pos.x, pos.z, box.minX(), box.maxX(), box.minZ(), box.maxZ(), radius
                            )) {
                            found.add(id)
                            if (found.size == types.size) return found
                            break
                        }
                    }
                }
            }
        }
        return found
    }
}
