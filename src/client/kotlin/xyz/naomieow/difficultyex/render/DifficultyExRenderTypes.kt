package xyz.naomieow.difficultyex.render

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import java.util.function.Function


class DifficultyExRenderTypes(name: String, format: VertexFormat, mode: VertexFormat.Mode, bufferSize: Int, affectsCrumbling: Boolean, sortOnUpload: Boolean, setupState: Runnable, clearState: Runnable) : RenderType(name, format,
    mode,
    bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState
){
    @JvmField
    val ICON: Function<ResourceLocation, RenderType> = Util.memoize { resourceLocation -> create("icon", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RENDERTYPE_SOLID_SHADER).setTextureState(RenderStateShard.TextureStateShard(resourceLocation, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(LIGHTMAP).setDepthTestState(NO_DEPTH_TEST).setWriteMaskState(COLOR_WRITE).createCompositeState(false)) }
}