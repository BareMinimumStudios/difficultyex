package xyz.naomieow.difficultyex.config

import io.wispforest.owo.config.annotation.*
import xyz.naomieow.difficultyex.DifficultyEX

@Suppress("UNUSED")
@Modmenu(modId = DifficultyEX.MOD_ID)
@Config(name = "difficultyex-config", wrapperName = "DifficultyEXConfig")
class DifficultyEXConfigModel {
    @JvmField
    var enabled: Boolean = true
}