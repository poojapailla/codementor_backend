# syntax=docker/dockerfile:1
#
# CodeMentor AI backend - production Docker image
# Build context: repository root (this directory)
#   docker build -t codementor-backend .
#
# Render supplies the PORT env var at runtime; application.properties already
# binds server.port=${PORT:8080}, so no port wiring is needed in the image.

# --------------------------------------------------------------------------
# Stage 1: build the executable Spring Boot JAR with Java 17 + Maven
# --------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy only the build descriptor first so dependency downloads are cached
# across source-only changes.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline || true

# Copy the application sources (includes src/main/resources/certs/aiven-ca.pem)
COPY src ./src

# Tests need a live database, so they are skipped here; run them in CI instead.
RUN mvn -B -DskipTests clean package

# --------------------------------------------------------------------------
# Stage 2: minimal Java 17 runtime image
# --------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy AS runtime

# Non-root user. CaCertBootstrap.java writes the generated truststore to the
# relative path ./certs/, so the working directory must be writable by it.
WORKDIR /app
RUN groupadd --system --gid 1001 app \
    && useradd --system --uid 1001 --gid app --home-dir /app --shell /usr/sbin/nologin app

COPY --from=build /workspace/target/*.jar /app/app.jar
RUN chown -R app:app /app

USER app

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
