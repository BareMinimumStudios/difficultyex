package dev.pokesmells.difficultyex;

/** Cross-loader accessor for persistent, vanilla-synchronized mob progression. */
public interface MobLevelAccess {
    int difficultyExGetLevel();
    void difficultyExSetLevel(int level);
}
