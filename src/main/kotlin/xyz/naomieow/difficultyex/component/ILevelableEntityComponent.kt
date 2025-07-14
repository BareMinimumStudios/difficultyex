package xyz.naomieow.difficultyex.component

import dev.onyxstudios.cca.api.v3.component.Component

interface ILevelableEntityComponent: Component {
    val level: Int
}