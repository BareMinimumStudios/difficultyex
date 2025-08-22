package xyz.naomieow.difficultyex.component

import net.minecraft.nbt.CompoundTag
import xyz.naomieow.difficultyex.config.DifficultyEXConfigModel

class PlayerDataComponent(private var mDifficulty: DifficultyEXConfigModel.Difficulty) : IPlayerDataComponent {
    override fun difficulty(): DifficultyEXConfigModel.Difficulty {
        return this.mDifficulty
    }

    override fun readFromNbt(tag: CompoundTag) {
        this.mDifficulty = DifficultyEXConfigModel.Difficulty.valueOf(tag.getString("difficulty"))
    }

    override fun writeToNbt(tag: CompoundTag) {
        tag.putString("difficulty", this.mDifficulty.name)
    }
}