plugins {
    id 'java'
    id 'maven-publish'
    id 'com.github.johnrengelman.shadow'
    id 'org.jetbrains.gradle.plugin.idea-ext'
    id 'fabric-loom' version '1.1.10' apply false
    id 'com.github.ben-manes.versions' version '0.46.0'
}

static getTimestamp() {
    return new Date().format('yyyyMMddHHmmss')
}
def getGitHash() {
    def stdout = new ByteArrayOutputStream()
    exec {
        commandLine 'git', 'rev-parse', '--short', 'HEAD'
        standardOutput = stdout
    }
    return stdout.toString().trim()
}

group = project.maven_group
version = project.mod_version + "+" + getTimestamp() + "+" + getGitHash()

defaultTasks 'shadowJar'

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    apply plugin: 'java'
    apply plugin: 'java-library'
    apply plugin: 'maven-publish'
    apply plugin: 'com.github.johnrengelman.shadow'
    apply plugin: 'org.jetbrains.gradle.plugin.idea-ext'
}

subprojects {
    group = parent.group
    version = parent.version

    def targetJavaVersion = 17
    java {
        def javaVersion = JavaVersion.toVersion(targetJavaVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        if (JavaVersion.current() < javaVersion) {
            toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
        }
    }

    tasks.withType(JavaCompile).configureEach {
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
            options.release.set(targetJavaVersion)
        }
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects.each { subproject ->
    evaluationDependsOn(subproject.path)
}

shadowJar {
    dependsOn 'jar'
    //classifier ''
    manifest.inheritFrom jar.manifest
    subprojects.each { subproject ->
        from subproject.collect {
            it.tasks.shadowJar
        }
    }
}
