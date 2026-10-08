package dev.pokesmells.difficultyex

/**
 * Visibility policy equivalent to LivingEntityRenderer's name-tag team rules
 * for an entity which has already passed the base visibility checks.
 */
object DifficultyExNameplateVisibility {
    @JvmStatic
    fun permits(
        visibility: String?,
        viewerHasTeam: Boolean,
        alliedWithMobTeam: Boolean,
        invisibleToViewer: Boolean
    ): Boolean {
        if (invisibleToViewer) return false
        return when (visibility) {
            "NEVER" -> false
            "HIDE_FOR_OTHER_TEAMS" -> !viewerHasTeam || alliedWithMobTeam
            "HIDE_FOR_OWN_TEAM" -> !viewerHasTeam || !alliedWithMobTeam
            else -> true
        }
    }
}
