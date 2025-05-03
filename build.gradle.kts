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
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.discordsrv:discordsrv:1.29.0")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("dev.dejvokep:boosted-yaml:1.3.5")
    implementation("dev.dejvokep:boosted-yaml-spigot:1.5")
    implementation("com.github.technicallycoded:FoliaLib:main-SNAPSHOT")
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
        relocate("dev.dejvokep.boostedyaml", "ru.padow.discordsrvoauth.relocated.boostedyaml")
        relocate("com.tcoded.folialib", "ru.padow.discordsrvoauth.relocated.folialib")

        exclude("LICENSE")
        exclude("META-INF/versions/**")
        exclude("META-INF/maven/**")
    }
}
