rootProject.name = "rostering-reproducer"


pluginManagement {
    val kotlinVersion = "1.6.21"
    plugins {
        id("org.springframework.boot") version "2.7.0"
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
        kotlin("plugin.spring") version kotlinVersion
        kotlin("kapt") version kotlinVersion
        kotlin("plugin.noarg") version kotlinVersion
    }
}
