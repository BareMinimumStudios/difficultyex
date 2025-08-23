package xyz.naomieow.difficultyex.mixin.client;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.naomieow.difficultyex.DifficultyEX;
import xyz.naomieow.difficultyex.component.DifficultyEXComponents;
import xyz.naomieow.difficultyex.nameplate.access.MobAccess;

@Mixin(Mob.class)
public class MobMixin implements MobAccess {
    @Unique
    private boolean showMobLabel = true;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(EntityType<?> entityType, Level level, CallbackInfo ci) {
        var mob = (Mob) (Object) this;
        // todo: regex this i think
        if (DifficultyEX.CONFIG.getVisualSettings().getNameplateMobBlacklist().contains(mob.getType().toString().replace("entity.", "").replace(".", ":"))) {
            this.setShowMobLabel(false);
        }
    }

    @Override
    public boolean getShowMobLabel() {
        return this.showMobLabel;
    }

    @Override
    public void setShowMobLabel(boolean b) {
        this.showMobLabel = b;
    }
}
