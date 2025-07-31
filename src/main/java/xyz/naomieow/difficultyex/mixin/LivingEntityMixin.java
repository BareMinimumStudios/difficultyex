package xyz.naomieow.difficultyex.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import xyz.naomieow.difficultyex.component.DifficultyEXComponents;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@WrapMethod(method = "getMaxHealth")
	public float getMaxHealth(Operation<Float> original) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity instanceof Mob) {
			return original.call() * (float) Math.pow(1.05, entity.getComponent(DifficultyEXComponents.ENTITY_DATA).getLevel());
		}
		return original.call();
	}

	@WrapMethod(method = "getArmorValue")
	public int getArmorValue(Operation<Integer> original) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (entity instanceof Mob) {
			return original.call() * (int) (original.call() * (1 + entity.getComponent(DifficultyEXComponents.ENTITY_DATA).getLevel() * 0.08));
		}
		return original.call();
	}
}