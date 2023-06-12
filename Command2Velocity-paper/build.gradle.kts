plugins {
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
    id("org.jetbrains.gradle.plugin.idea-ext")
    id("com.github.ben-manes.versions")
    id("java")
}

repositories {
    maven {
        name = "Spigot"
        setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "papermc"
        setUrl("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        setUrl("https://oss.sonatype.org/content/groups/public/")
    }
    maven { setUrl("https://jitpack.io") }
    maven { setUrl("https://repo.codemc.org/repository/maven-public/") }
}

dependencies {
    implementation("io.papermc.paper:paper-api:${project.extra["paperapi_version"]}")
    implementation("org.spigotmc:spigot-api:${project.extra["minecraft_version"]}-R0.1-SNAPSHOT")
    shadow("dev.jorel:commandapi-bukkit-shade:${project.extra["commandapi_version"]}")
}

tasks.withType<Copy>().named("processResources") {
    val props = mapOf(
        "version" to project.version,
        "name" to rootProject.name,
        "main" to "${project.extra["maven_group"]}.${project.extra["archives_base_name"]}.${rootProject.name}Paper",
        "apiversion" to project.extra["spigotapi_version"]
    )
    inputs.properties(props)
    expand(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.shadowJar {
    dependsOn("jar")
    manifest.inheritFrom(tasks.jar.get().manifest)
    configurations = listOf(project.configurations.getByName("shadow"))

    dependencies {
        include(dependency("dev.jorel:commandapi-bukkit-shade:${project.extra["commandapi_version"]}"))
    }
    relocate("dev.jorel.commandapi", "${project.extra["maven_group"]}.commandapi")
}
