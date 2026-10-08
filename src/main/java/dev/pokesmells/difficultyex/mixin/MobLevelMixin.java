package dev.pokesmells.difficultyex.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import dev.pokesmells.difficultyex.MobLevelAccess;

@Mixin(Mob.class)
public abstract class MobLevelMixin implements MobLevelAccess {
    @Unique private float difficultyex$pendingLoadedHealth = Float.NaN;
    @Unique private static final EntityDataAccessor<Integer> DIFFICULTYEX_LEVEL =
        SynchedEntityData.defineId(Mob.class, EntityDataSerializers.INT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void difficultyex$define(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(DIFFICULTYEX_LEVEL, 0);
    }

    @Override
    public int difficultyExGetLevel() {
        return ((Mob)(Object)this).getEntityData().get(DIFFICULTYEX_LEVEL);
    }

    @Override
    public void difficultyExSetLevel(int level) {
        ((Mob)(Object)this).getEntityData().set(DIFFICULTYEX_LEVEL, Math.max(0, level));
    }

    @Override
    public float difficultyExConsumeSavedHealth() {
        float value = difficultyex$pendingLoadedHealth;
        difficultyex$pendingLoadedHealth = Float.NaN;
        return value;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void difficultyex$save(CompoundTag tag, CallbackInfo ci) {
        if (difficultyExGetLevel() > 0) tag.putInt("difficultyex_level", difficultyExGetLevel());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void difficultyex$load(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("difficultyex_level", 3)) difficultyExSetLevel(tag.getInt("difficultyex_level"));
        // Vanilla clamps LivingEntity Health to its unscaled maximum before our
        // transient scaling attributes can be restored during EntityJoinLevelEvent.
        // Hold the original saved value until the attributes have been reapplied.
        difficultyex$pendingLoadedHealth = tag.contains("Health", 99) ? tag.getFloat("Health") : Float.NaN;
    }
}
