# ============================
# Stage 1: Build
# ============================
FROM gradle:9.2.1-jdk21-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper + config first (cache dependencies)
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle.kts settings.gradle.kts ./

# Fix permissions for Linux
RUN chmod +x gradlew

# Pre-fetch dependencies (cached in Docker layer)
RUN ./gradlew dependencies --no-daemon || true

# Now copy source (changes invalidate this only)
COPY src ./src

# Build application
RUN gradle build -x test --no-daemon


# ============================
# Stage 2: Runtime
# ============================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl

RUN addgroup -S quarkus && adduser -S quarkus -G quarkus

# Copy built output from builder
COPY --from=builder /app/build/quarkus-app/lib/ ./lib/
COPY --from=builder /app/build/quarkus-app/*.jar ./
COPY --from=builder /app/build/quarkus-app/app/ ./app/
COPY --from=builder /app/build/quarkus-app/quarkus/ ./quarkus/

RUN chown -R quarkus:quarkus /app
USER quarkus

EXPOSE 8080 8443

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://0.0.0.0:8080/q/health/live || exit 1

# Strict configuration via Environment Variables
ENV QUARKUS_HTTP_HOST=0.0.0.0
ENV QUARKUS_HTTP_PORT=8080

ENTRYPOINT ["java", \
    "-Djava.util.logging.manager=org.jboss.logmanager.LogManager", \
    "-jar", "quarkus-run.jar"]
