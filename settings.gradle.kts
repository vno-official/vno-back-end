pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id("io.quarkus") version "3.29.3" apply false
    }
}

rootProject.name = "vno-backend"

// Include all service modules
include("auth-service")
include("user-service")
include("note-service")
include("realtime-collab-service")
include("notification-service")
include("notification-service:producer")
include("notification-service:processor")
include("common-observability")
include("common-openapi")
