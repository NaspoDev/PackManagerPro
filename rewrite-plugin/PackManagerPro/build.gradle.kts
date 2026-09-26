plugins {
    kotlin("jvm") version "2.5.0-Beta1"
    id("com.gradleup.shadow") version "9.6.1"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.3.build.+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

kotlin {
    jvmToolchain(25)
}

tasks {
    jar {
        // Disabling the jar task as we only want the fat jar output from the shadowJar task.
        enabled = false
    }

    shadowJar {
        // Removing the "-all" suffix from the end of the build output.
        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
