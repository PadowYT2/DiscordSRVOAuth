plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.cadixdev.licenser") version "0.6.1"
}

group = "ru.padow"
version = "1.0.4"

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
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://nexus.scarsz.me/content/groups/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("com.discordsrv:discordsrv:1.28.1")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    api("github.scarsz:configuralize:1.3.2") {
        exclude(module = "json-simple")
        exclude(module = "snakeyaml")
    }
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    shadowJar {
        archiveClassifier.set("")

        minimize {
            exclude(dependency("github.scarsz:configuralize"))
        }

        relocate("org.bstats", "ru.padow.discordsrvoauth.bstats")

        exclude("META-INF/maven/**")
    }
}
