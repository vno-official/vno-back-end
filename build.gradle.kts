plugins {
    java
}

// Common configuration for all subprojects
subprojects {
    group = "com.vno"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

// Task to build all services
tasks.register("buildAll") {
    group = "build"
    description = "Build all services"
    
    dependsOn(
        ":auth-service:build",
        ":user-service:build",
        ":note-service:build",
        ":realtime-collab-service:build",
        ":notification-service:producer:build",
        ":notification-service:processor:build"
    )
}

// Task to clean all services
tasks.register("cleanAll") {
    group = "build"
    description = "Clean all services"
    
    dependsOn(
        ":auth-service:clean",
        ":user-service:clean",
        ":note-service:clean",
        ":realtime-collab-service:clean",
        ":notification-service:producer:clean",
        ":notification-service:processor:clean"
    )
}

// Task to build all Docker images
tasks.register<Exec>("buildAllDockerImages") {
    group = "docker"
    description = "Build Docker images for all services"
    
    dependsOn("buildAll")
    
    doFirst {
        println("Building Docker images for all services...")
    }
    
    // Use PowerShell to run the build script
    commandLine("powershell", "-File", "${projectDir}/build-all-images.ps1")
}

// Task to test all services
tasks.register("testAll") {
    group = "verification"
    description = "Run tests for all services"
    
    dependsOn(
        ":auth-service:test",
        ":user-service:test",
        ":note-service:test",
        ":realtime-collab-service:test",
        ":notification-service:producer:test",
        ":notification-service:processor:test"
    )
}
