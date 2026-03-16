# -----------------------------
# 1. Build Stage
# -----------------------------
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

COPY src src
RUN ./gradlew bootJar --no-daemon -x test


# -----------------------------
# 2. Extract Layer
# -----------------------------
FROM eclipse-temurin:21-jre-jammy AS extractor

WORKDIR /workspace

COPY --from=builder /workspace/build/libs/*.jar app.jar

RUN java -Djarmode=layertools -jar app.jar extract


# -----------------------------
# 3. Runtime Stage
# -----------------------------
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# curl 설치 (healthcheck용)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# non-root user
RUN useradd -ms /bin/bash spring
USER spring

COPY --from=extractor /workspace/dependencies/ ./
COPY --from=extractor /workspace/spring-boot-loader/ ./
COPY --from=extractor /workspace/snapshot-dependencies/ ./
COPY --from=extractor /workspace/application/ ./

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

# -----------------------------
# Healthcheck
# -----------------------------
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1