package xyz.naomieow.difficultyex.ext

import net.minecraft.world.entity.LivingEntity
import xyz.naomieow.difficultyex.component.DifficultyEXComponents

var LivingEntity.dexLevel: Int
    get() {
        return DifficultyEXComponents.ENTITY_DATA.get(this).level
    }
    set(value) {
        DifficultyEXComponents.ENTITY_DATA.get(this).level = value
    }