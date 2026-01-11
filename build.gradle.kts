plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("com.gradleup.shadow") version "9.3.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "dev.officialmex.dee"
version = "2.0.0-beta"

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.1.0")
}

java {
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
}

tasks {
    jar {
        enabled = false
    }

    shadowJar {
        archiveClassifier.set("")
        configurations = listOf(project.configurations.runtimeClasspath.get())
        dependencies { exclude { it.moduleGroup != "org.bstats" } }
        relocate("org.bstats", project.group.toString())
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version)
        }
    }

    runServer {
        runDirectory.set(file(".run"))
    }
}
