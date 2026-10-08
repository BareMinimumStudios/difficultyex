package dev.pokesmells.difficultyex.mixin;

import dev.pokesmells.difficultyex.DifficultyEx;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Scale XP from every Mob subclass, even if that subclass overrides
 * getBaseExperienceReward without calling Mob's implementation.
 *
 * Adjust the vanilla base reward before enchantment modifiers are applied,
 * preserving the intended reward order and vanilla death/loot conditions.
 */
@Mixin(LivingEntity.class)
public abstract class MobExperienceMixin {
    @ModifyArg(
        method = "getExperienceReward",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;processMobExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;I)I"
        ),
        index = 3
    )
    private int difficultyex$scaleBaseXp(int original) {
        return (Object) this instanceof Mob mob
            ? DifficultyEx.scaledExperience(original, mob)
            : original;
    }
}
