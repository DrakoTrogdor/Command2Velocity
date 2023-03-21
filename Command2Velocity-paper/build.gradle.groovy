plugins {
    id 'maven-publish'
    id 'com.github.johnrengelman.shadow'
    id 'org.jetbrains.gradle.plugin.idea-ext'
    id 'com.github.ben-manes.versions'
    id 'java'
}

repositories {
    maven {
        name = "Spigot"
        url "https://hub.spigotmc.org/nexus/content/repositories/snapshots/"
    }
    maven {
        name = "papermc"
        url = "https://papermc.io/repo/repository/maven-public/"
    }
    maven {
        name = "sonatype"
        url = "https://oss.sonatype.org/content/groups/public/"
    }
    maven { url = "https://jitpack.io" }
    maven { url = "https://repo.codemc.org/repository/maven-public/" }
}

dependencies {
    implementation "io.papermc.paper:paper-api:${project.paperapi_version}"
    implementation "org.spigotmc:spigot-api:${project.minecraft_version}-R0.1-SNAPSHOT"
    shadow "dev.jorel:commandapi-shade:${project.commandapi_version}"
}

processResources {
    def props = [
            version: project.version,
            name: rootProject.name,
            main: "${project.maven_group}.${project.archives_base_name}.${rootProject.name}Paper",
            apiversion: project.spigotapi_version
    ]
    inputs.properties props
    expand props
    filteringCharset 'UTF-8'
    filesMatching('plugin.yml') {
        expand props
    }
}

shadowJar {
    dependsOn 'jar'
    manifest.inheritFrom jar.manifest
    configurations = [project.configurations.shadow]
    dependencies {
        include(dependency("dev.jorel:commandapi-shade:${project.commandapi_version}"))

    }
    relocate("dev.jorel.commandapi", "${project.maven_group}.commandapi")
}