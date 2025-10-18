import jdk.nashorn.internal.objects.NativeRegExp.source

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



plugins {
    java
    id("net.minecraftforge.gradle") version "6.+"
//    id("net.minecraftforge.gradle")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
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
}

minecraft {
    mappings(mapOf("channel" to "stable", "version" to "39-1.12"))
    runs {

        create("client") {
            workingDirectory(file("./run"))
            mods {
//                sources(sourceSets.main)
//                create(project.name) {
//                    source(sourceSets.main)
//                }
            }
        }

        create("server") {
            workingDirectory(file("./run/server"))
            mods {
//                sources(sourceSets.main)
//                create(project.name) {
//                    source(sourceSets.main)
//                }
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
}