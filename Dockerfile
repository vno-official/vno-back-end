# Build stage
FROM gradle:8.10.2-jdk21-alpine AS build
WORKDIR /workspace
COPY . .
RUN gradle clean build -x test

# Runtime stage
FROM eclipse-temurin:21-jre
ENV PORT=8080
EXPOSE 8080
WORKDIR /app
COPY --from=build /workspace/build/quarkus-app/lib/ /app/lib/
COPY --from=build /workspace/build/quarkus-app/*.jar /app/
COPY --from=build /workspace/build/quarkus-app/app/ /app/app/
COPY --from=build /workspace/build/quarkus-app/quarkus/ /app/quarkus/
ENTRYPOINT ["java","-jar","/app/quarkus-run.jar"]
