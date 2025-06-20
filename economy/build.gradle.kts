import kotlin.io.path.Path

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "live.blackninja"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://libraries.minecraft.net/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")

    compileOnly("fr.minuskube.inv:smart-invs:1.2.7") {
        exclude(group = "org.spigotmc", module = "spigot-api")
    }

    compileOnly(project(":core"))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

java {
    withSourcesJar()
}