plugins {
    java
    id("io.quarkus") apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.quarkus")

    repositories {
        mavenCentral()
        mavenLocal()
    }

    val quarkusPlatformGroupId = "io.quarkus.platform"
    val quarkusPlatformArtifactId = "quarkus-bom"
    val quarkusPlatformVersion = "3.29.3"

    dependencies {
        implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
        implementation("io.quarkus:quarkus-config-yaml")
    }
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<Test> {
        systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    }
    
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }
}
