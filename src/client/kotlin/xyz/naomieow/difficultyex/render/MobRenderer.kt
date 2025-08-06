package xyz.naomieow.difficultyex.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.entity.Mob
import xyz.naomieow.difficultyex.DifficultyEX

object MobRenderer {
    private fun modifyAlpha(color: Int, alpha: Int): Int {
        if (alpha == 0) return color
        val newAlpha = (color.shr(24)) * alpha / 255
        return (color.and(0x00ffffff)).or(newAlpha.shl(24))
    }

    @JvmStatic
    fun render(target: Mob, stack: PoseStack, buffer: MultiBufferSource) {
        val visualSettings = DifficultyEX.CONFIG.visualSettings

        val player = Minecraft.getInstance().player ?: return

        val distance = target.distanceTo(player)
        val layeredDistance = kotlin.math.max(0.005, distance * 0.005)

        stack.pushPose()
        stack.translate(0.0, (target.bbHeight + visualSettings.nameplateOffsetY).toDouble(), 0.0)
        stack.mulPose(Minecraft.getInstance().entityRenderDispatcher.cameraOrientation())

        // --

        stack.pushPose()

        val scale = 0.015F
        val halfWidth = 50F
        val halfHeight = 3F

        stack.scale(-scale, -scale, scale)
        val bufferBuilder = buffer.getBuffer(Graphics.RENDER_TYPE)

        val healthRate = kotlin.math.min(target.health / target.maxHealth, 1.0F)
        val healthWidth = halfWidth * 2 * healthRate

        val healthBarAlpha = 180
        val color = modifyAlpha(0xF52727, healthBarAlpha)
        val colorEmpty = modifyAlpha(0xBFBFBF, healthBarAlpha)

        if (healthWidth > 0) {
            Graphics.renderSolidGradient(bufferBuilder, stack, -halfWidth.toInt(), -halfHeight.toInt(), (-halfWidth + healthWidth).toInt(), halfHeight.toInt(), color, layeredDistance.toFloat())
        }
        if (healthWidth < 2 * halfWidth) {
            Graphics.renderSolidGradientUpDown(bufferBuilder, stack, (-halfWidth + healthWidth).toInt(), -halfHeight.toInt(), halfWidth.toInt(), halfHeight.toInt(), colorEmpty, layeredDistance.toFloat())
        }

        stack.popPose()

        stack.popPose()
    }
}