package dev.pokesmells.difficultyex

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.MethodVisitor
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DifficultyExConfigSyncMetadataTest {
    private val nonSyncDescriptor = "Lme/fzzyhmstrs/fzzy_config/annotations/NonSync;"
    private val clientFields = setOf(
        "nameplatesEnabled",
        "nameplateDistance",
        "nameplateHostileOnly",
        "nameplateBlacklist",
        "nameplateShowLevel",
        "nameplateShowHealth",
        "nameplateShowHealthText"
    )
    private val serverFields = setOf(
        "startingLevel", "maximumLevel", "playerRadius", "playerLevelFormula",
        "averageDecrement", "averageIncrement", "mobBlacklist",
        "entityStartingLevels", "entityMaximumLevels",
        "dimensionStartingLevels", "dimensionMaximumLevels",
        "biomeStartingLevels", "biomeMaximumLevels",
        "structureRadius", "structureStartingLevels", "structureMaximumLevels",
        "healthPerLevel", "armorPerLevel", "damagePerLevel", "experiencePerLevel"
    )

    private data class FieldMetadata(val descriptor: String, val annotations: Set<String>)
    private fun metadata(loader: String): Map<String, FieldMetadata> {
        val file = Path.of(
            "build", "classes", "kotlin", loader,
            "dev", "pokesmells", "difficultyex", "DifficultyExConfig.class"
        )
        assertTrue(Files.isRegularFile(file), "Missing $loader config bytecode; compile the loader before tests")
        val descriptors = mutableMapOf<String, String>()
        val annotations = mutableMapOf<String, MutableSet<String>>()
        ClassReader(Files.readAllBytes(file)).accept(object : ClassVisitor(Opcodes.ASM9) {
            override fun visitField(
                access: Int, name: String, descriptor: String,
                signature: String?, value: Any?
            ): FieldVisitor? {
                if (access and Opcodes.ACC_SYNTHETIC != 0) return null
                descriptors[name] = descriptor
                return object : FieldVisitor(Opcodes.ASM9) {
                    override fun visitAnnotation(
                        descriptor: String, visible: Boolean
                    ): org.objectweb.asm.AnnotationVisitor? {
                        if (visible) annotations.getOrPut(name) { mutableSetOf() }.add(descriptor)
                        return null
                    }
                }
            }
        }, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
        return descriptors.mapValues { (name, desc) ->
            FieldMetadata(desc, annotations[name].orEmpty())
        }
    }

    @Test
    fun nameplatePreferencesHaveKotlinPropertyAnnotationsForFzzyReflection() {
        for (loader in listOf("fabric1211", "neoforge1211")) {
            val file = Path.of("build", "classes", "kotlin", loader,
                "dev", "pokesmells", "difficultyex", "DifficultyExConfig.class")
            val properties = mutableSetOf<String>()
            ClassReader(Files.readAllBytes(file)).accept(object : ClassVisitor(Opcodes.ASM9) {
                override fun visitMethod(access: Int, name: String, descriptor: String,
                    signature: String?, exceptions: Array<out String>?): MethodVisitor? {
                    if (!name.startsWith("get") || !name.endsWith("\$annotations")) return null
                    val property = name.removePrefix("get").removeSuffix("\$annotations")
                        .replaceFirstChar { it.lowercase() }
                    return object : MethodVisitor(Opcodes.ASM9) {
                        override fun visitAnnotation(descriptor: String, visible: Boolean): org.objectweb.asm.AnnotationVisitor? {
                            if (visible && descriptor == nonSyncDescriptor) properties.add(property)
                            return null
                        }
                    }
                }
            }, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
            assertEquals(clientFields, properties, "$loader must expose @NonSync through Kotlin property reflection")
        }
    }

    @Test
    fun fabricAndNeoForgeFieldsHaveIdenticalTypesAndNames() {
        val fabric = metadata("fabric1211")
        val neoforge = metadata("neoforge1211")
        assertEquals(fabric.keys, neoforge.keys, "Loader config field names have diverged")
        assertEquals(fabric.mapValues { it.value.descriptor },
            neoforge.mapValues { it.value.descriptor }, "Loader config field types have diverged")
        assertTrue(fabric.keys.containsAll(clientFields + serverFields))
    }

    @Test
    fun nameplatePreferencesAreNonSyncOnBothLoaderBytecodes() {
        for (loader in listOf("fabric1211", "neoforge1211")) {
            val fields = metadata(loader)
            val actual = fields.filterValues { nonSyncDescriptor in it.annotations }.keys
            assertEquals(clientFields, actual, "Incorrect client-local @NonSync fields in $loader")
        }
    }

    @Test
    fun everyProgressionAndRuleFieldRemainsServerSyncedOnBothLoaders() {
        for (loader in listOf("fabric1211", "neoforge1211")) {
            val fields = metadata(loader)
            for (field in serverFields) {
                val info = fields[field]
                assertTrue(info != null, "$loader missing server-authoritative $field")
                assertTrue(nonSyncDescriptor !in info.annotations,
                    "$loader $field was incorrectly made client-only")
            }
        }
    }
}
