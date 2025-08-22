package xyz.naomieow.difficultyex.component

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import xyz.naomieow.difficultyex.config.DifficultyEXConfigModel

interface IPlayerDataComponent : AutoSyncedComponent {
    fun difficulty(): DifficultyEXConfigModel.Difficulty
}