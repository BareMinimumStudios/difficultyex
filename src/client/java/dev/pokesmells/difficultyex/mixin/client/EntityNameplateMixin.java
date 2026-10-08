package dev.pokesmells.difficultyex.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import dev.pokesmells.difficultyex.client.DifficultyExNameplates;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/** Decorates existing vanilla nameplate rendering without replacing the pipeline. */
@Mixin(EntityRenderer.class)
public abstract class EntityNameplateMixin {
    @ModifyArg(
        method = "render",
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"),
        index = 1
    )
    private Component difficultyex$decorateMobName(Component vanilla, @Local(argsOnly = true) Entity entity) {
        return DifficultyExNameplates.decorate(vanilla, entity);
    }
}
