package xyz.naomieow.difficultyex.nameplate.util

import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferVertexConsumer
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import xyz.naomieow.difficultyex.DifficultyEX
import xyz.naomieow.difficultyex.mixin.client.DrawContextAccessor
import xyz.naomieow.difficultyex.nameplate.access.MobAccess

@Environment(EnvType.CLIENT)
object NameplateRender {
    val ICONS = ResourceLocation("nameplate:textures/icons.png")

    @JvmStatic
    fun renderNameplate(renderer: EntityRenderer<*>, mob: Mob, matrices: PoseStack, vertexConsumers: MultiBufferSource, dispatcher: EntityRenderDispatcher, textRenderer: Font, isVisible: Boolean, i: Int) {
        if (isVisible && (mob as MobAccess).showMobLabel) {
            if (Minecraft.getInstance().player?.hasLineOfSight(mob) != true) {
                return
            }

            matrices.pushPose()
            matrices.translate(0.0, mob.bbHeight.toDouble() + DifficultyEX.CONFIG.visualSettings.nameplateOffsetY, 0.0)
            matrices.mulPose(dispatcher.cameraOrientation())
            matrices.scale(DifficultyEX.CONFIG.visualSettings.nameplateOffsetScale, DifficultyEX.CONFIG.visualSettings.nameplateOffsetScale, 0.025F)

            // if health-bar

            matrices.pushPose()
            matrices.scale(1.0F, 1.5F, 1.0F)

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            RenderSystem.enableBlend()

            RenderSystem.defaultBlendFunc()
            RenderSystem.enableDepthTest()

            RenderSystem.enablePolygonOffset()
            RenderSystem.polygonOffset(3.0F, 3.0F)

            val client = Minecraft.getInstance()
            val context = DrawContextAccessor.getGuiGraphics(client, matrices, client.renderBuffers().bufferSource())
            context.blit(ICONS, -20, 0, 0F, 0F, 40, 6, 256, 256)
            val health = mob.health / mob.maxHealth
            matrices.translate(0.0, 0.0, -0.01)
            context.blit(ICONS, -20, 0, 0F, 6F, kotlin.math.round(40 * health).toInt(), 6, 256, 256)
            RenderSystem.polygonOffset(0.0F, 0.0F)
            RenderSystem.disablePolygonOffset()

            matrices.popPose()
            matrices.translate(0.0, -9.0, 0.8)
            RenderSystem.disableBlend()

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)

            // end-if health bar

            val matrix = matrices.last().pose()
            val o = dispatcher.options.textBackgroundOpacity().get()
            val j = (o * 255.0).toInt() shl 24
            var string = mob.customName?.string ?: mob.name.string

            // if show-health
            string = "$string ${Component.translatable("text.nameplate.health", kotlin.math.round(mob.health), kotlin.math.round(mob.maxHealth)).string }"
            // end if show-health

            val levelString = Component.translatable("text.nameplate.level", (mob as MobAccess).mobLevel).string
            string = "$string ${Component.translatable("text.nameplate.name", string).string}"
            val text = Component.literal(string)

            val h = (-textRenderer.width(text) / 2).toFloat()
            textRenderer.drawInBatch(text.string, h, 0.0F, DifficultyEX.CONFIG.visualSettings.nameplateColor.argb(), false, matrix, vertexConsumers, Font.DisplayMode.NORMAL, j, i);
            textRenderer.drawInBatch(text.string, h, 0.0F, DifficultyEX.CONFIG.visualSettings.nameplateBackgroundColor.argb(), false,  matrix, vertexConsumers, Font.DisplayMode.NORMAL, 0, i);
            matrices.popPose()
        }
    }
}