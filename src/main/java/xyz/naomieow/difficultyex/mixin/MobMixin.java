package xyz.naomieow.difficultyex.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.naomieow.difficultyex.component.DifficultyEXComponents;

@Mixin(Mob.class)
public class MobMixin {
    @WrapOperation(method = "doHurtTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getAttributeValue(Lnet/minecraft/world/entity/ai/attributes/Attribute;)D"))
    public double doHurtTarget(Mob instance, Attribute attribute, Operation<Double> original) {
        var level = instance.getComponent(DifficultyEXComponents.ENTITY_DATA).getLevel();

        Double mobDamage = original.call(instance, attribute) * (1 + level * 0.1);

        return original.call(instance, attribute) * mobDamage;
    }
}
