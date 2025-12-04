plugins {
    id("java")
    id("io.quarkus") version "3.29.3"
}

group = "com.vno"
version = "0.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("io.quarkus:quarkus-bom:3.29.3"))

    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    
    // Reactive Hibernate instead of ORM
    implementation("io.quarkus:quarkus-hibernate-reactive-panache")
    implementation("io.quarkus:quarkus-reactive-pg-client")
    implementation("io.quarkus:quarkus-jdbc-postgresql") // Still needed for Flyway migrations
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-vertx")
    
    implementation("io.quarkus:quarkus-redis-client")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-micrometer")

    // Security deps for Phase 1
    implementation("io.quarkus:quarkus-smallrye-jwt")
    implementation("io.quarkus:quarkus-smallrye-jwt-build")
    implementation("io.quarkus:quarkus-oidc")

    // YAML config support
    implementation("io.quarkus:quarkus-config-yaml")

    // REST client for Resend API
    implementation("io.quarkus:quarkus-rest-client")
    
    // Resend Java SDK
    implementation("com.resend:resend-java:3.0.0")

    // Validation
    implementation("io.quarkus:quarkus-hibernate-validator")
    
    // BCrypt for password hashing
    implementation("org.mindrot:jbcrypt:0.4")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

tasks.test {
    useJUnitPlatform()
}
