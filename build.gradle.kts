plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-beta13"
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "ru.padow"
version = "1.0.5"

java {
    sourceCompatibility = JavaVersion.toVersion(11)
    targetCompatibility = JavaVersion.toVersion(11)

    disableAutoTargetJvm()
}

license {
    include("**/*.java")
    header(project.file("LICENSE.head"))
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
    maven("https://repo.tcoded.com/releases")
    maven("https://repo.okaeri.cloud/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.discordsrv:discordsrv:1.29.0")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:6.0.0-beta.27")
    implementation("eu.okaeri:okaeri-configs-toml-jackson:6.0.0-beta.27")
    implementation("com.cjcrafter:foliascheduler:0.7.2")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("")

        relocate("org.bstats", "ru.padow.discordsrvoauth.relocated.bstats")
        relocate("eu.okaeri", "ru.padow.discordsrvoauth.relocated.okaeri")
        relocate("com.fasterxml", "ru.padow.discordsrvoauth.relocated.fasterxml")
        relocate("com.cjcrafter.foliascheduler", "ru.padow.discordsrvoauth.relocated.foliascheduler")

        exclude("META-INF/**")

        minimize {
            exclude(dependency("com.cjcrafter:foliascheduler:.*"))
        }
    }
}
