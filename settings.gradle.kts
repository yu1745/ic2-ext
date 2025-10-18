pluginManagement {
    repositories {
        // Gradle 默认的插件门户
        gradlePluginPortal()
        // Maven Central 仓库
        mavenCentral()
        // 你的自定义仓库
        maven {
            url = uri("https://maven.minecraftforge.net/")
        }
    }
}

// 这个文件通常还会包含 include 声明
//include(":app")
