import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow")
    id("org.jetbrains.gradle.plugin.idea-ext")
    id("fabric-loom") version "1.1.10" apply false
    id("com.github.ben-manes.versions") version "0.46.0"
}

fun getTimestamp(): String {
    return SimpleDateFormat("yyyyMMddHHmmss").format(Date())
}

fun getGitHash(): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

group = project.extra["maven_group"]
version = "${project.extra["mod_version"]}+${getTimestamp()}+${getGitHash()}"

defaultTasks("shadowJar")

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")
}

subprojects {
    group = parent!!.group
    version = parent!!.version

    val targetJavaVersion = 17
    java {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects.forEach { subproject ->
    evaluationDependsOn(subproject.path)
}

tasks.shadowJar {
    dependsOn("jar")
    manifest.inheritFrom(tasks.jar.get().manifest)
    subprojects.forEach { subproject ->
        from(subproject.tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>())
    }
}
