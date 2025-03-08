import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("fabric-loom") version "1.10-SNAPSHOT"
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm") version "2.1.10"
}

version = "${properties["mod_version"]}"
group = "${properties["maven_group"]}"

base {
	archivesName = "${properties["archives_base_name"]}"
}

repositories {
	maven("https://maven.parchmentmc.org") {
		name = "ParchmentMC"
	}
	maven("https://maven.quiltmc.org/repository/release/") {
		name = "Quilt"
	}
	maven("https://maven.wispforest.io/releases/") {
		name = "WispForest"
	}
}

loom {
	splitEnvironmentSourceSets()

	mods {
		register("difficultyex") {
			sourceSet("main")
			sourceSet("client")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")

	mappings(loom.layered() {
		mappings("org.quiltmc:quilt-mappings:${properties["minecraft_version"]}+build.${properties["quilt_mappings_version"]}:intermediary-v2")
		officialMojangMappings()
		parchment("org.parchmentmc.data:parchment-${properties["minecraft_version"]}:${properties["parchment_version"]}@zip")
	})

	modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${properties["fabric_kotlin_version"]}")

	annotationProcessor("io.wispforest:owo-lib:${properties["owo_version"]}")?.let(::modImplementation)
	include("io.wispforest:owo-sentinel:${properties["owo_version"]}")
}
tasks {
	processResources {
		inputs.property("version", project.version)

		filesMatching("fabric.mod.json") {
			expand("version" to project.version)
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}"}
		}
	}

	java {
		// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
		// if it is present.
		// If you remove this line, sources will not be generated.
		withSourcesJar()

		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	// configure the maven publication
	publishing {
		publications {
			create<MavenPublication>("mavenJava") {
				artifact(remapJar) {
					builtBy(remapJar)
				}
				artifact(kotlinSourcesJar) {
					builtBy(remapSourcesJar)
				}
			}
		}

		repositories {}
	}

	withType<KotlinCompile>().configureEach {
		compilerOptions {
			jvmTarget.set(JvmTarget.JVM_17)
		}
	}
}