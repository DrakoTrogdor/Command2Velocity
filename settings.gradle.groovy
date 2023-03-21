pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven {
            name = 'Fabric'
            url = 'https://maven.fabricmc.net/'
        }
    }
    plugins {
        id 'com.github.johnrengelman.shadow' version '8.1.1'
        id 'org.jetbrains.gradle.plugin.idea-ext' version '1.1.7'
        id 'java'
    }
}

rootProject.name = 'Command2Velocity'

include ':command2velocity-paper'
// include ':command2velocity-fabric'

project(':command2velocity-paper').projectDir = "$rootDir/Command2Velocity-paper" as File
// project(':command2velocity-fabric').projectDir = "$rootDir/Command2Velocity-fabric" as File
