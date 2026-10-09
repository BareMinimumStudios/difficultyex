package dev.pokesmells.difficultyex

import com.google.gson.JsonParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DifficultyNameplateLanguageTest {
    private fun translation(key: String): String {
        val stream = assertNotNull(
            javaClass.getResourceAsStream("/assets/difficultyex/lang/en_us.json"),
            "Language resource must be bundled in both loader JARs"
        )
        val translations = stream.bufferedReader(Charsets.UTF_8).use {
            JsonParser.parseReader(it).asJsonObject
        }
        val entry = assertNotNull(translations.get(key), "Missing translation $key")
        return entry.asString
    }

    @Test
    fun healthLabelUsesMinecraftSupportedStringArguments() {
        assertEquals("HP: %s/%s", translation("text.nameplate.health"))
    }

    @Test
    fun levelAndJadeKeysPreserveSingleStringSubstitution() {
        assertEquals("Lv.%s", translation("text.nameplate.level"))
        assertEquals(" Lv.%s", translation("text.nameplate.jade.level"))
        assertEquals("%s", translation("text.nameplate.name"))
    }

    @Test
    fun nameplateLabelsContainNoBrokenLegacyColorEncoding() {
        for (key in listOf(
            "text.nameplate.health",
            "text.nameplate.level",
            "text.nameplate.name",
            "text.nameplate.jade.level"
        )) {
            val text = translation(key)
            assertFalse(text.contains('§'), "$key must use Component styles instead of legacy formatting codes")
            assertFalse(text.contains('Â'), "$key contains mojibake")
            assertTrue(text.isNotEmpty())
        }
    }
}
