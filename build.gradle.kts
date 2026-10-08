import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    `maven-publish`
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.cloche)
    alias(libs.plugins.mixinmcp.decompile)
}

group = providers.gradleProperty("maven_group").get()
version = providers.gradleProperty("mod_version").get()

repositories {
    cloche.librariesMinecraft()
    mavenCentral()
    cloche {
        main()
        mavenFabric()
        mavenNeoforgedMeta()
        mavenNeoforged()
        mavenParchment()
    }
    maven("https://thedarkcolour.github.io/KotlinForForge/") {
        content { includeGroup("thedarkcolour") }
    }
    maven("https://maven.fzzyhmstrs.me/")
    maven("https://redempt.dev")
    maven("https://api.modrinth.com/maven") {
        content { includeGroup("maven.modrinth") }
    }
}

cloche {
    metadata {
        modId = "difficultyex"
        name = "DifficultyEx"
        description = "Scales mob levels and combat stats with PlayerEx progression and world rules."
        license = "BML-1.0"
        author("pokesmells")
        url = "https://github.com/BareMinimumStudios/difficultyex"
        sources = "https://github.com/BareMinimumStudios/difficultyex"
        issues = "https://github.com/BareMinimumStudios/difficultyex/issues"
        icon = "assets/difficultyex/icon.png"
    }

    common {
        client { }
        mixins.from("src/main/resources/difficultyex.mixins.json")
        sourceSet.resources.exclude("difficultyex.mixins.json")
        mappings {
            official()
            parchment(libs.versions.parchment)
        }
        dependencies {
            implementation(libs.crunch)
            compileOnly(libs.mixinextras)
        }
    }

    fabric("fabric:1.21.1") {
        minecraftVersion = "1.21.1"
        loaderVersion = libs.versions.fabric.loader
        includedClient()
        runs {
            server()
            client()
        }
        dependencies {
            fabricApi(libs.versions.fabric.api)
            modImplementation(libs.fabric.language.kotlin)
            modImplementation(libs.fzzy.config.fabric)
            modImplementation(libs.remnant.fabric)
            modImplementation(libs.data.attributes.fabric)
            modImplementation(libs.playerex.fabric)
            include(libs.crunch)
        }
        metadata {
            dependencies {
                dependency { modId = "fabric-api"; version(libs.versions.fabric.api.get()) }
                dependency { modId = "fabric-language-kotlin"; version(libs.versions.fabric.language.kotlin.get()) }
                dependency { modId = "fzzy_config"; version(libs.versions.fzzy.fabric.get()) }
                dependency { modId = "remnant"; version(libs.versions.remnant.get()) }
                dependency { modId = "data_attributes"; version(libs.versions.data.attributes.get()) }
                dependency { modId = "playerex"; version(libs.versions.playerex.get()) }
            }
            entrypoint("main") {
                adapter.set("kotlin")
                value.set("dev.pokesmells.difficultyex.DifficultyExFabricEntrypoint")
            }
        }
    }

    neoforge("neoforge:1.21.1") {
        minecraftVersion = "1.21.1"
        loaderVersion = libs.versions.neoforge.loader
        runs {
            server()
            client()
        }
        dependencies {
            modImplementation(libs.neoforge.language.kotlin)
            modImplementation(libs.fzzy.config.neoforge)
            modImplementation(libs.remnant.neoforge)
            modImplementation(libs.data.attributes.neoforge)
            modImplementation(libs.playerex.neoforge)
            include(libs.crunch)
        }
        metadata {
            modLoader = "kotlinforforge"
            loaderVersion { start = libs.versions.neoforge.language.kotlin.get() }
            blurLogo = false
            dependencies {
                dependency { modId = "kotlinforforge"; version(libs.versions.neoforge.language.kotlin.get()) }
                dependency { modId = "fzzy_config"; version(libs.versions.fzzy.neoforge.get()) }
                dependency { modId = "remnant"; version(libs.versions.remnant.get()) }
                dependency { modId = "data_attributes"; version(libs.versions.data.attributes.get()) }
                dependency { modId = "playerex"; version(libs.versions.playerex.get()) }
            }
        }
    }
}

// Cloche's Fabric mapping archive must be generated before access widening under Gradle 9.8.
tasks.matching { it.name.startsWith("accessWidenFabric1211") }.configureEach {
    dependsOn("generateFabric1211MappingsArtifact")
}

kotlin {
    jvmToolchain(21)
    sourceSets.named("fabric1211") { kotlin.srcDir("src/fabric/1.21.1/kotlin") }
    sourceSets.named("neoforge1211") { kotlin.srcDir("src/neoforge/1.21.1/kotlin") }
    @OptIn(ExperimentalBuildToolsApi::class, ExperimentalKotlinGradlePluginApi::class)
    compilerVersion.set("2.2.20")
    coreLibrariesVersion = "2.2.20"
}

tasks.withType<net.msrandom.stubs.GenerateStubApi>().configureEach {
    outputDirectory.set(layout.buildDirectory.dir("generated/$name"))
}

// Ship the studio license and inherited MIT notice in each loader's distributable JAR.
tasks.withType<Jar>().matching { it.name == "fabric1211Jar" || it.name == "neoforge1211Jar" }.configureEach {
    from(rootProject.file("LICENSE")) {
        into("META-INF/licenses")
        rename { "difficultyex-LICENSE" }
    }
    from(rootProject.file("docs/ORIGINAL_MIT_LICENSE.txt")) {
        into("META-INF/licenses")
        rename { "difficultyex-ORIGINAL-MIT-LICENSE" }
    }
}
