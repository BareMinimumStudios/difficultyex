package dev.pokesmells.difficultyex;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public final class DifficultySavedMobLevel {
    private DifficultySavedMobLevel() {}

    public static int read(CompoundTag entity) {
        if (entity.contains("difficultyex_level")) {
            return entity.contains("difficultyex_level", Tag.TAG_INT)
                ? Math.max(0, entity.getInt("difficultyex_level")) : 0;
        }
        CompoundTag component = entity.getCompound("cardinal_components")
            .getCompound("difficultyex:entity_data").getCompound("DATA");
        return component.contains("level", Tag.TAG_INT) ? Math.max(0, component.getInt("level")) : 0;
    }
}
