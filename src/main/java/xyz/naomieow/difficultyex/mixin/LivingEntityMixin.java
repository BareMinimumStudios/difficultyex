package xyz.naomieow.difficultyex.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.naomieow.difficultyex.DifficultyEX;
import xyz.naomieow.difficultyex.component.DifficultyEXComponents;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@WrapMethod(method = "getMaxHealth")
	public float GetMaxHealth(Operation<Float> original) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity instanceof Mob) {
			return (float) (original.call() * (1 + entity.getComponent(DifficultyEXComponents.ENTITY_DATA).getLevel() * DifficultyEX.CONFIG.getScalingLevelSettings().getEntityBaseHealthPercentage()));
		}
		return original.call();
	}

	@WrapMethod(method = "getArmorValue")
	public int GetArmorValue(Operation<Integer> original) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity instanceof Mob) {
			return (int) (original.call() * (1 + entity.getComponent(DifficultyEXComponents.ENTITY_DATA).getLevel() * DifficultyEX.CONFIG.getScalingLevelSettings().getEntityBaseArmorPercentage()));
		}
		return original.call();
	}
}