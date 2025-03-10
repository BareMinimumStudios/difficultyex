package xyz.naomieow.difficultyex.component

import dev.onyxstudios.cca.api.v3.component.Component

interface ILevellableEntityComponent: Component {
    var level: Int
}