plugins {
    java
    id("com.gradleup.shadow") version "9.0.0-beta13"
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "dev.padow"
version = "1.2.0"

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
    compileOnly("com.discordsrv:discordsrv:1.30.5")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:6.1.0-beta.4")
    implementation("eu.okaeri:okaeri-configs-toml-jackson:6.1.0-beta.4")
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

        relocate("org.bstats", "dev.padow.discordsrvoauth.relocated.bstats")
        relocate("eu.okaeri", "dev.padow.discordsrvoauth.relocated.okaeri")
        relocate("com.fasterxml", "dev.padow.discordsrvoauth.relocated.fasterxml")
        relocate("com.cjcrafter.foliascheduler", "dev.padow.discordsrvoauth.relocated.foliascheduler")

        exclude("META-INF/**")

        minimize {
            exclude(dependency("com.cjcrafter:foliascheduler:.*"))
        }
    }
}
