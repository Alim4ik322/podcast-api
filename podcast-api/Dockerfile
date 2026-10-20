# ===== Этап 1: сборка =====
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Сначала только pom — кэш зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Потом исходники
COPY src ./src
RUN mvn package -DskipTests -B

# ===== Этап 2: лёгкий runtime =====
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем только готовый JAR
COPY --from=builder /app/target/*.jar app.jar

# Порт приложения
EXPOSE 8080

# Переменные по умолчанию (можно переопределить при run)
ENV APP_NAME="Podcast Hosting & Streaming API" \
    APP_ENV=production \
    APP_VERSION=1.0.0 \
    HTTP_PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]