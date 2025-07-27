package xyz.naomieow.difficultyex.ext

import net.minecraft.world.entity.Mob
import xyz.naomieow.difficultyex.component.DifficultyEXComponents

var Mob.difficultyExLevel: Int
    get() {
        return DifficultyEXComponents.ENTITY_DATA.get(this).level
    }
    set(value) {
        DifficultyEXComponents.ENTITY_DATA.get(this).set(value)
    }