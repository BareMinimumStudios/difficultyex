package xyz.naomieow.difficultyex.config

import blue.endless.jankson.Comment
import io.wispforest.owo.config.Option.SyncMode
import io.wispforest.owo.config.annotation.*
import io.wispforest.owo.ui.core.Color
import net.minecraft.resources.ResourceLocation
import xyz.naomieow.difficultyex.DifficultyEX

@Suppress("UNUSED")
@Modmenu(modId = DifficultyEX.MOD_ID)
@Config(name = "difficultyex-config", wrapperName = "DifficultyEXConfig")
class DifficultyEXConfigModel {
    @SectionHeader("global_settings")

    // todo: in order to implement the maximum levels we can clamp down the value multiple times through
    // dimension -> biome -> structure -> entity
    // for starting levels we just choose the highest out of the 4 to be honest.

    @JvmField
    @Comment("Settings for all dimensions and their scaling.")
    @Nest @Expanded var dimensionSettings: DimensionScalingSettings = DimensionScalingSettings()

    data class DimensionScalingSettings(
        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The starting mob level for any given dimension in this map.")
        var startingLevels: Map<ResourceLocation, Int> = emptyMap(),

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The maximum mob level for any given dimension in this map.")
        var maximumLevels: Map<ResourceLocation, Int> = emptyMap(),
    )

    @JvmField
    @Comment("Settings for all biomes and their scaling.")
    @Nest @Expanded var biomeScalingSettings: BiomeScalingSettings = BiomeScalingSettings()

    data class BiomeScalingSettings(
        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The starting mob level for any given biome in this map.")
        var startingLevels: Map<ResourceLocation, Int> = emptyMap(),

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The maximum mob level for any given biome in this map.")
        var maximumLevels: Map<ResourceLocation, Int> = emptyMap(),

        // // STRETCH GOAL :: STRUCTURES
        //
        // @JvmField
        // @Sync(SyncMode.OVERRIDE_CLIENT)
        // @Comment("Based on a given resource identifier of a structure, you can apply a configuration.")
        // var structures: Map<ResourceLocation, EntityScalerConfig> = emptyMap(),
    )

    @JvmField
    @Comment("Settings for all structures and their scaling.")
    @Nest @Expanded var structureScalingSettings: StructureScalingSettings = StructureScalingSettings()

    data class StructureScalingSettings(
        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The global setting for all structures to have a given radius which affects the spawned mobs within that dimension area.")
        var radius: Int = 50,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The starting mob level for any given structure in this map.")
        var startingLevels: Map<ResourceLocation, Int> = emptyMap(),

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The maximum mob level for any given structure in this map.")
        var maximumLevels: Map<ResourceLocation, Int> = emptyMap(),

        // // STRETCH GOAL :: BIOMES
        //
        // @JvmField
        // @Sync(SyncMode.OVERRIDE_CLIENT)
        // @Comment("Based on a given resource identifier of a biome, you can apply a configuration.")
        // var biomes: Map<ResourceLocation, EntityScalerConfig> = emptyMap(),
    )

    @JvmField
    @Comment("Settings for all entities and their scaling.")
    @Nest @Expanded var scalingLevelSettings: EntityScalingSettings = EntityScalingSettings()

    data class EntityScalingSettings(
        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The global maximum level for all entities.")
        var maximumLevel: Int = 1_000_000,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The global starting level for all entities.")
        var startingLevel: Int = 0,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("Based on a given block radius from the given mob, the players within it alongside their level will be considered in the leveling average.")
        var levelScalingMaxRadiusByBlocks: Int = 100,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("Based on (x) player level, the formula will be factored into the total averaging function.")
        var levelScalingByPlayerFormula: String = "x",

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("How many levels to decrement in the average total.")
        var levelAverageDecrement: Int = 3,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("How many levels to increment in the average total.")
        var levelAverageIncrement: Int = 3,

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The starting levels for any given entities.")
        var entityStartingLevels: Map<ResourceLocation, Int> = emptyMap(),

        @JvmField
        @Sync(SyncMode.OVERRIDE_CLIENT)
        @Comment("The maximum levels for any given entities.")
        var entityMaximumLevels: Map<ResourceLocation, Int> = emptyMap(),
    )


    @SectionHeader("client_settings")


    @JvmField
    @Comment("Visual settings for the client.")
    @Nest @Expanded var visualSettings: VisualSettings = VisualSettings()

    data class VisualSettings(
        @JvmField
        @Comment("Whether to enable mob nameplates.")
        var nameplateEnabled: Boolean = true,

        @JvmField
        @Comment("The color of the visible nameplate on mobs.")
        var nameplateColor: Color = Color.RED,

        @JvmField
        @Comment("What mob entities to blacklist from having a nameplate.")
        var nameplateMobBlacklist: List<ResourceLocation> = emptyList(),
    )
}

// STRETCH GOAL :: CLIENT SYNCHRONIZATION
// Allow the client to use a GUI to select local difficulty, which can affect scaling or damage dealt/taken
// Integrate with PlayerEX leveled gear in drops, as an incentive to select higher difficulties and level up in general.
// A way to level up tamed mobs using some kind of item like an orb.