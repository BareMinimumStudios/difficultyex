package dev.pokesmells.difficultyex.client;

import dev.pokesmells.difficultyex.DifficultyEx;
import dev.pokesmells.difficultyex.DifficultySettings;
import dev.pokesmells.difficultyex.MobLevelAccess;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;

/** Client-only nameplate policy, common to Fabric and NeoForge. */
public final class DifficultyExNameplates {
    private DifficultyExNameplates() {}

    public static boolean shouldRender(Mob mob) {
        DifficultySettings settings = DifficultyEx.INSTANCE.getSettings();
        if (!settings.getNameplatesEnabled() ||
            ((MobLevelAccess) mob).difficultyExGetLevel() < 1 ||
            mob.isInvisible() ||
            (settings.getNameplateHostileOnly() && !(mob instanceof Monster))) {
            return false;
        }

        Player viewer = Minecraft.getInstance().player;
        if (viewer == null || viewer.isSpectator()) return false;
        int distance = Math.max(0, settings.getNameplateDistance());
        if (viewer.distanceToSqr(mob) > (double) distance * distance) return false;
        if (!viewer.hasLineOfSight(mob)) return false;

        String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).toString();
        for (String pattern : settings.getNameplateBlacklist()) {
            if (DifficultyEx.match(pattern, entityId)) return false;
        }
        return true;
    }

    public static Component decorate(Component vanilla, Entity entity) {
        if (!(entity instanceof Mob mob) || !shouldRender(mob)) return vanilla;

        DifficultySettings settings = DifficultyEx.INSTANCE.getSettings();
        int level = ((MobLevelAccess) mob).difficultyExGetLevel();
        MutableComponent label = Component.empty();

        if (settings.getNameplateShowLevel()) {
            label.append(Component.translatable("text.nameplate.level", level));
            label.append(Component.literal(" "));
        }
        label.append(vanilla.copy());

        if (settings.getNameplateShowHealth()) {
            float max = Math.max(1f, mob.getMaxHealth());
            float ratio = Math.max(0f, Math.min(1f, mob.getHealth() / max));
            int filled = Math.max(0, Math.min(10, Math.round(ratio * 10f)));
            ChatFormatting color = ratio > 0.5f ? ChatFormatting.GREEN :
                ratio > 0.25f ? ChatFormatting.YELLOW : ChatFormatting.RED;
            label.append(Component.literal("  [").withStyle(ChatFormatting.DARK_GRAY));
            label.append(Component.literal("|".repeat(filled)).withStyle(color));
            label.append(Component.literal("|".repeat(10 - filled)).withStyle(ChatFormatting.DARK_GRAY));
            label.append(Component.literal("]").withStyle(ChatFormatting.DARK_GRAY));
        }
        if (settings.getNameplateShowHealthText()) {
            label.append(Component.literal("  "));
            label.append(Component.translatable("text.nameplate.health",
                Math.round(mob.getHealth()), Math.round(mob.getMaxHealth())));
        }
        return label;
    }
}
