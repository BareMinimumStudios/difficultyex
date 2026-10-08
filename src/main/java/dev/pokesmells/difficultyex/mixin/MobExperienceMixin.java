package dev.pokesmells.difficultyex.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import dev.pokesmells.difficultyex.DifficultyEx;

@Mixin(Mob.class)
public abstract class MobExperienceMixin {
    @ModifyReturnValue(method = "getBaseExperienceReward", at = @At("RETURN"))
    private int difficultyex$scaleXp(int original) {
        return DifficultyEx.scaledExperience(original, (Mob)(Object)this);
    }
}
