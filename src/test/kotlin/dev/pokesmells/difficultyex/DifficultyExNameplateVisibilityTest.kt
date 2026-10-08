package dev.pokesmells.difficultyex

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DifficultyExNameplateVisibilityTest {
    @Test
    fun neverVisibleForNeverTeam() {
        assertFalse(DifficultyExNameplateVisibility.permits("NEVER", false, false, false))
        assertFalse(DifficultyExNameplateVisibility.permits("NEVER", true, true, false))
    }

    @Test
    fun hideForOtherTeamsDoesNotExposeOpponents() {
        assertFalse(DifficultyExNameplateVisibility.permits("HIDE_FOR_OTHER_TEAMS", true, false, false))
        assertTrue(DifficultyExNameplateVisibility.permits("HIDE_FOR_OTHER_TEAMS", true, true, false))
        assertTrue(DifficultyExNameplateVisibility.permits("HIDE_FOR_OTHER_TEAMS", false, false, false))
    }

    @Test
    fun hideForOwnTeamHidesAllies() {
        assertFalse(DifficultyExNameplateVisibility.permits("HIDE_FOR_OWN_TEAM", true, true, false))
        assertTrue(DifficultyExNameplateVisibility.permits("HIDE_FOR_OWN_TEAM", true, false, false))
        assertTrue(DifficultyExNameplateVisibility.permits("HIDE_FOR_OWN_TEAM", false, false, false))
    }

    @Test
    fun invisibleEntitiesStayHiddenEvenIfTeamAllowsTags() {
        assertFalse(DifficultyExNameplateVisibility.permits("ALWAYS", true, true, true))
        assertFalse(DifficultyExNameplateVisibility.permits(null, false, false, true))
        assertTrue(DifficultyExNameplateVisibility.permits(null, false, false, false))
        assertTrue(DifficultyExNameplateVisibility.permits("ALWAYS", true, false, false))
    }
}
