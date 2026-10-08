package dev.pokesmells.difficultyex.mixin.client;

import dev.pokesmells.difficultyex.client.DifficultyExNameplates;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Enables a vanilla-style nametag for leveled mobs that pass visibility checks. */
@Mixin(MobRenderer.class)
public abstract class MobNameplateMixin {
    @Inject(
        method = "shouldShowName(Lnet/minecraft/world/entity/Mob;)Z",
        at = @At("RETURN"),
        cancellable = true
    )
    private void difficultyex$showLevelName(Mob mob, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && DifficultyExNameplates.shouldRender(mob)) {
            cir.setReturnValue(true);
        }
    }
}
