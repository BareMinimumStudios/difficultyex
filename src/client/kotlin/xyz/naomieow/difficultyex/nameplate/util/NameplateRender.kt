package xyz.naomieow.difficultyex.nameplate.util

import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferVertexConsumer
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
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
import net.minecraft.world.entity.monster.Monster
import xyz.naomieow.difficultyex.DifficultyEX
import xyz.naomieow.difficultyex.ext.difficultyExLevel
import xyz.naomieow.difficultyex.mixin.client.DrawContextAccessor
import xyz.naomieow.difficultyex.nameplate.access.MobAccess

@Environment(EnvType.CLIENT)
object NameplateRender {
    val ICONS = ResourceLocation("difficultyex:textures/icons.png")

    @JvmStatic
    fun renderNameplate(renderer: EntityRenderer<*>, mob: Mob, matrices: PoseStack, vertexConsumers: MultiBufferSource, dispatcher: EntityRenderDispatcher, textRenderer: Font, isVisible: Boolean, i: Int) {
        if (isVisible && (mob as MobAccess).showMobLabel) {
            if (Minecraft.getInstance().player?.hasLineOfSight(mob) != true) {
                return
            }

            if ((Minecraft.getInstance().player?.distanceTo(mob) ?: 0f) > 20f) {
                return
            }

            val settings = DifficultyEX.CONFIG.visualSettings

            if (settings.nameplateShowHostileMobsOnly && mob !is Monster) {
                return
            }

            matrices.pushPose()
            matrices.translate(0.0, mob.bbHeight.toDouble() + DifficultyEX.CONFIG.visualSettings.nameplateOffsetY, 0.0)
            matrices.mulPose(dispatcher.cameraOrientation())

            val s = 0.025F * DifficultyEX.CONFIG.visualSettings.nameplateOffsetScale

            matrices.scale(-s, -s, s)

            // if health-bar

            if (settings.showNameplateHealthBar) {
                val client = Minecraft.getInstance()

                val immediate = MultiBufferSource.immediate(Tesselator.getInstance().builder)
                val context = DrawContextAccessor.getGuiGraphics(client, matrices, immediate)

                matrices.pushPose()
                matrices.scale(1.0F, 1.5F, 1.0F)

                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
                RenderSystem.enableBlend()
                RenderSystem.defaultBlendFunc()
                RenderSystem.enableDepthTest()

                RenderSystem.enablePolygonOffset()
                RenderSystem.polygonOffset(3.0F, 3.0F)


                val barW = 60
                val barH = 6
                val x0 = -barW / 2
                val y0 = 0

                context.blit(ICONS, x0, y0, 0f, 0f, barW, barH, 256, 256)

                val healthPct = (mob.health / mob.maxHealth).coerceIn(0f, 1f)
                val fillW = kotlin.math.round(barW * healthPct).toInt().coerceIn(0, barW)

                matrices.translate(0.0, 0.0, -0.01)
                context.blit(ICONS, x0, y0, 0f, 6f, fillW, barH, 256, 256)


                immediate.endBatch()

                RenderSystem.polygonOffset(0.0F, 0.0F)
                RenderSystem.disablePolygonOffset()

                matrices.popPose()
                matrices.translate(0.0, -9.0, 0.8)
                RenderSystem.disableBlend()
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
            }

            val matrix = matrices.last().pose()
            val o = dispatcher.options.textBackgroundOpacity().get()
            val j = (o * 255.0).toInt() shl 24
            var string = ""



            if (settings.showNameplateLevel) {
                string = Component.translatable("text.nameplate.level", mob.difficultyExLevel).string
            }

            string = "$string ${Component.translatable("text.nameplate.name", mob.customName?.string ?: mob.name.string).string}"

            if (settings.showNameplateHealthText) {
                string = "$string ${Component.translatable("text.nameplate.health", kotlin.math.round(mob.health), kotlin.math.round(mob.maxHealth)).string }"
            }

            val text = Component.literal(string)

            val h = (-textRenderer.width(text) / 2).toFloat()
            textRenderer.drawInBatch(text.string, h, 0.0F, DifficultyEX.CONFIG.visualSettings.nameplateColor.argb(), false, matrix, vertexConsumers, Font.DisplayMode.NORMAL, j, i)
            textRenderer.drawInBatch(text.string, h, 0.0F, DifficultyEX.CONFIG.visualSettings.nameplateBackgroundColor.argb(), false,  matrix, vertexConsumers, Font.DisplayMode.NORMAL, 0, i)
            matrices.popPose()
        }
    }
}