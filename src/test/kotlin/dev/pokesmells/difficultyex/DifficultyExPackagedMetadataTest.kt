package dev.pokesmells.difficultyex

import org.apache.maven.artifact.versioning.DefaultArtifactVersion
import org.apache.maven.artifact.versioning.VersionRange
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DifficultyExPackagedMetadataTest {
    @Test
    fun neoForgeMinecraftRangeParsesAndExcludesOtherReleases() {
        val lines = Files.readAllLines(Path.of("build", "generated", "metadata", "neoforge1211",
            "main", "META-INF", "neoforge.mods.toml"))
        val dependency = lines.indexOfFirst { it.trim() == "modId = \"minecraft\"" }
        assertTrue(dependency >= 0, "Missing Minecraft dependency")
        val spec = lines.drop(dependency + 1).first { it.startsWith("versionRange = ") }
            .substringAfter('"').substringBefore('"')
        val range = VersionRange.createFromVersionSpec(spec)
        assertTrue(range.containsVersion(DefaultArtifactVersion("1.21.1")))
        for (unsupported in listOf("1.21", "1.21.2", "1.22")) {
            assertFalse(range.containsVersion(DefaultArtifactVersion(unsupported)), "Accepted $unsupported")
        }
    }
}
