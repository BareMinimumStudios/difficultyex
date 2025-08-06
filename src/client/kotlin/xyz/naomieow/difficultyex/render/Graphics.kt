package xyz.naomieow.difficultyex.render

import com.mojang.blaze3d.vertex.*
import net.minecraft.client.renderer.RenderType


object Graphics {
    @JvmField
    val RENDER_TYPE: RenderType = RenderType.solid()

    fun renderSolidGradient(
        vertexConsumer: VertexConsumer,
        poseStack: PoseStack,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        color: Int,
        z: Float
    ) {
        vertexConsumer.vertex(poseStack.last().pose(), left.toFloat(), top.toFloat(), z).color(color).uv(0f, 0.625f)
            .uv2(0xff00ff).endVertex()
        vertexConsumer.vertex(poseStack.last().pose(), left.toFloat(), bottom.toFloat(), z).color(color).uv(0f, 1f)
            .uv2(0xff00ff).endVertex()
        vertexConsumer.vertex(poseStack.last().pose(), right.toFloat(), bottom.toFloat(), z).color(color).uv(1f, 1f)
            .uv2(0xff00ff).endVertex()
        vertexConsumer.vertex(poseStack.last().pose(), right.toFloat(), top.toFloat(), z).color(color).uv(1f, 0.625f)
            .uv2(0xff00ff).endVertex()
    }

    fun renderSolidGradientUpDown(
        vertexConsumer: VertexConsumer,
        poseStack: PoseStack,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        color: Int,
        z: Float
    ) {
        vertexConsumer.vertex(poseStack.last().pose(), left.toFloat(), top.toFloat(), z).color(color).uv(0f, 0f)
            .uv2(0xff00ff).endVertex()
        vertexConsumer.vertex(poseStack.last().pose(), left.toFloat(), bottom.toFloat(), z).color(color).uv(0f, 0.375f)
            .uv2(0xff00ff).endVertex()
        vertexConsumer.vertex(poseStack.last().pose(), right.toFloat(), bottom.toFloat(), z).color(color).uv(1f, 0.375f)
            .uv2(0xff00ff).endVertex()
        vertexConsumer.vertex(poseStack.last().pose(), right.toFloat(), top.toFloat(), z).color(color).uv(1f, 0f)
            .uv2(0xff00ff).endVertex()
    }
}