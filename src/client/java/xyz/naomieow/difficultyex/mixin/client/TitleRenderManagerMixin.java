package xyz.naomieow.difficultyex.mixin.client;

import com.yungnickyoung.minecraft.travelerstitles.render.TitleRenderManager;
import com.yungnickyoung.minecraft.travelerstitles.render.TitleRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import xyz.naomieow.difficultyex.network.NameplateClientPacket;

@Environment(EnvType.CLIENT)
@Mixin(TitleRenderManager.class)
public class TitleRenderManagerMixin {

    @Shadow
    @Mutable
    @Final
    public TitleRenderer<Biome> biomeTitleRenderer;

    @Inject(method = "updateBiomeTitle", at = @At(value = "INVOKE", target = "Lcom/yungnickyoung/minecraft/travelerstitles/render/TitleRenderer;addRecentEntry(Ljava/lang/Object;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void updateBiomeTitleMixin(Level world, BlockPos playerPos, Player player, boolean isPlayerUnderground, CallbackInfo ci) {
//        if (NameplateMain.CONFIG.levelTitle) {

//        }
        NameplateClientPacket.writeC2STravelerCompatPacket();
    }
}