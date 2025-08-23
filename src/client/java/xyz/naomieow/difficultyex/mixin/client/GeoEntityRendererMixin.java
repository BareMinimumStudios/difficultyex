package xyz.naomieow.difficultyex.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import xyz.naomieow.difficultyex.nameplate.util.NameplateRender;

@Environment(EnvType.CLIENT)
@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends LivingEntity> extends EntityRenderer<T> {

    public GeoEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render", at = @At("HEAD"), remap = false)
    private void render(Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (entity instanceof Mob) {
            NameplateRender.renderNameplate(this, (Mob) entity, poseStack, bufferSource, entityRenderDispatcher, this.getFont(), entity.shouldShowName(), packedLight);
        }
    }

    @Override
    protected boolean shouldShowName(T entity) {
        if (entity instanceof Mob) {
            return false;
        }
        return super.shouldShowName(entity);
    }

    @Inject(method = "shouldShowName", at = @At(value = "RETURN", ordinal = 1), cancellable = true, remap = false)
    private void shouldShowNameMixin(Entity animatable, CallbackInfoReturnable<Boolean> cir) {
        if (animatable instanceof Mob) {
            cir.setReturnValue(false);
        }
    }
}