# ===== Этап 1: сборка =====
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Копируем всё и сразу собираем (без кэширования go-offline)
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests -B

# ===== Этап 2: лёгкий runtime =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV APP_NAME="Podcast Hosting & Streaming API" \
    APP_ENV=production \
    APP_VERSION=1.0.0 \
    HTTP_PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]