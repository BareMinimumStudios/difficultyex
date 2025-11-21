import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("fabric-loom") version "1.10-SNAPSHOT"
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm") version "2.1.10"
	id("com.google.devtools.ksp") version "2.1.10-1.0.31"
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
	maven("https://maven.kosmx.dev/") {
		name = "KosmX"
	}
	maven("https://api.modrinth.com/maven") {
		name = "Modrinth"
	}
	maven("https://maven.ladysnake.org/releases") {
		name = "Ladysnake"
	}
	maven("https://maven.nucleoid.xyz/") {
		name = "Nucleoid"
	}
	maven("https://redempt.dev") {
		name = "Redempt"
	}
	maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") {
		name = "Geckolib3"
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

	implementation("com.google.devtools.ksp:symbol-processing-api:${properties["ksp_version"]}")
	implementation("com.squareup:kotlinpoet-ksp:${properties["kotlinpoet_version"]}")
	ksp("dev.kosmx.kowoconfig:ksp-owo-config:${properties["ksp_owo_config_version"]}")

	modCompileOnly("maven.modrinth:data-attributes-directors-cut:${properties["data_attributes_version"]}")
	modCompileOnly("maven.modrinth:playerex-directors-cut:${properties["playerex_version"]}")

	modImplementation("io.wispforest:endec:${properties["endec_version"]}")?.let(::include)
	modImplementation("io.wispforest.endec:gson:${properties["endec_gson_version"]}")?.let(::include)
	modImplementation("io.wispforest.endec:netty:${properties["endec_netty_version"]}")?.let(::include)

	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${properties["cca_version"]}")?.let(::include)
	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${properties["cca_version"]}")?.let(::include)
	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-chunk:${properties["cca_version"]}")?.let(::include)

	modImplementation("software.bernie.geckolib:geckolib-fabric-${properties["geckolib_version"]}")
	modImplementation("eu.pb4:placeholder-api:${properties["placeholder_api_version"]}")?.let(::include)
	implementation("com.github.Redempt:Crunch:2.0.3")
	include("com.github.Redempt:Crunch:2.0.3")

	implementation("net.objecthunter:exp4j:${properties["exp4j_version"]}")?.let(::include)

	modCompileOnly("maven.modrinth:travelers-titles:1.19.4-Fabric-3.3.0") {
		exclude("net.fabricmc.fabric-api")
	}

	modImplementation("com.github.Fallen-Breath.conditional-mixin:conditional-mixin-fabric:0.6.4")
	// Suggested, to bundle it into your mod jar. Choose a method your build system provides
	include("com.github.Fallen-Breath.conditional-mixin:conditional-mixin-fabric:0.6.4")
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