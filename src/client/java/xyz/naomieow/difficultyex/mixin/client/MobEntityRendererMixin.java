package xyz.naomieow.difficultyex.mixin.client;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import xyz.naomieow.difficultyex.nameplate.util.NameplateRender;

@Environment(EnvType.CLIENT)
@Mixin(MobRenderer.class)
public abstract class MobEntityRendererMixin<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public MobEntityRendererMixin(EntityRendererProvider.Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "render*", at = @At("HEAD"))
    private void renderMixin(T mobEntity, float f, float g, PoseStack matrices, MultiBufferSource vertexConsumers, int i, CallbackInfo info) {
        NameplateRender.renderNameplate(this, mobEntity, matrices, vertexConsumers, entityRenderDispatcher, this.getFont(), this.isBodyVisible(mobEntity), i);
    }

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/Mob;)Z", at = @At("HEAD"), cancellable = true)
    protected void shouldShowName(T mobEntity, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);
    }
}