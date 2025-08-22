package xyz.naomieow.difficultyex.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(GuiGraphics.class)
public interface DrawContextAccessor {

    @Invoker("<init>")
    static GuiGraphics getGuiGraphics(Minecraft client, PoseStack matrices, MultiBufferSource vertexConsumers) {
        throw new AssertionError();
    }

}