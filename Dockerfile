# ============================================
# Build Stage - Gradle 9.1.0 with Java 21
# ============================================
FROM gradle:8.11.1-jdk21-alpine AS build

WORKDIR /workspace

# Copy Gradle wrapper and config files first for better caching
COPY gradlew gradlew.bat ./
COPY gradle gradle
COPY settings.gradle.kts build.gradle.kts ./

# Download dependencies (cached layer if build files unchanged)
RUN ./gradlew --no-daemon dependencies

# Copy source code
COPY src src

# Build application (skip tests for faster builds)
RUN ./gradlew --no-daemon clean build -x test

# ============================================
# Runtime Stage - Minimal Alpine JRE 21
# ============================================
FROM eclipse-temurin:21-jre-alpine

# Set environment variables
ENV PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

WORKDIR /app

# Copy Quarkus fast-jar structure from build stage
COPY --from=build /workspace/build/quarkus-app/lib/ /app/lib/
COPY --from=build /workspace/build/quarkus-app/*.jar /app/
COPY --from=build /workspace/build/quarkus-app/app/ /app/app/
COPY --from=build /workspace/build/quarkus-app/quarkus/ /app/quarkus/

# Run as non-root user for security
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser && \
    chown -R appuser:appuser /app

USER appuser

# Start application with optimized JVM settings
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/quarkus-run.jar"]

