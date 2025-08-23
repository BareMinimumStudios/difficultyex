package xyz.naomieow.difficultyex.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoReplacedEntityRenderer;
import xyz.naomieow.difficultyex.nameplate.util.NameplateRender;

@Environment(EnvType.CLIENT)
@SuppressWarnings("rawtypes")
@Mixin(GeoReplacedEntityRenderer.class)
public abstract class GeoReplacedEntityRendererMixin extends EntityRenderer {

    @Shadow
    protected Entity currentEntity;

    public GeoReplacedEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Inject(method = "actuallyRender", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Mob;getLeashHolder()Lnet/minecraft/world/entity/Entity;"))
    private void render(PoseStack poseStack, GeoAnimatable animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        NameplateRender.renderNameplate(this, (Mob) currentEntity, poseStack, bufferSource, entityRenderDispatcher, this.getFont(), currentEntity.shouldShowName(), packedLight);
    }

    @Inject(method = "shouldShowName", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    protected void shouldShowNameMixin(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof Mob) {
            info.setReturnValue(false);
        }
    }
}