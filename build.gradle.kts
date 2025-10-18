//buildscript {
//    repositories {
//        mavenCentral()
//        maven { url = uri("https://maven.minecraftforge.net") }
//    }
//
//    dependencies {
////        classpath(url = uri("net.minecraftforge.gradle:ForgeGradle:6.+"))
//
//    }
//}


val kotlinVersion = "2.2.20"

plugins {
    java
    kotlin("jvm") version "2.2.20"
    id("net.minecraftforge.gradle") version "6.+"
//    id("net.minecraftforge.gradle")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

kotlin {
    jvmToolchain(8)
}


//tasks.compileJava {
//    sourceCompatibility = "8"
//    targetCompatibility = "8"
//}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.minecraftforge.net/") }
}

dependencies {
    minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2860")
    implementation(files("libs/industrialcraft-2-2.8.222-ex112-api.jar"))
    compileOnly(files("libs/industrialcraft-2-2.8.222-ex112-dev.jar"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
}

minecraft {
    mappings(mapOf("channel" to "stable", "version" to "39-1.12"))
    runs {

        create("client") {
            workingDirectory(file("./run"))
            mods {
                create(project.name) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(file("./run/server"))
            mods {
                create(project.name) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("mcmod.info") {
        expand(mapOf("version" to project.version))
    }
}

// When Forge 1.12 loads mods from a directory that's been put on the classpath, it expects to find resources in the same directory.
// Default Gradle behavior puts resources in ./build/resources/main instead of ./build/classes/main/java. Let's change that.
sourceSets.all {
//    it.output.resourcesDir = it.output.classesDirs.files.iterator().next()
//    output.resourcesDir =
    output.setResourcesDir(output.classesDirs.files.iterator().next())
}

//tasks.named("runClient").get().dependsOn(tasks.jar)

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    destinationDirectory.set(file("${layout.buildDirectory}/classes/java/main"))
//}
