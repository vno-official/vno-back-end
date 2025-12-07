# Multi-stage Dockerfile for VNO Backend
# Stage 1: Build
FROM gradle:9.1.0-jdk21-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and build files
COPY build.gradle.kts settings.gradle.kts ./

# Copy source code
COPY src ./src

# Build application (Gradle will download dependencies automatically)
RUN gradle build -x test --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S quarkus && adduser -S quarkus -G quarkus

# Copy built artifacts from builder
COPY --from=builder /app/build/quarkus-app/lib/ ./lib/
COPY --from=builder /app/build/quarkus-app/*.jar ./
COPY --from=builder /app/build/quarkus-app/app/ ./app/
COPY --from=builder /app/build/quarkus-app/quarkus/ ./quarkus/

# Set ownership
RUN chown -R quarkus:quarkus /app

USER quarkus

# Expose ports
EXPOSE 8080 8443

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/q/health/live || exit 1

# Run application
ENTRYPOINT ["java", \
    "-Djava.util.logging.manager=org.jboss.logmanager.LogManager", \
    "-jar", "quarkus-run.jar"]