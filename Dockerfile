# Fase 1: construir el proyecto con Maven
FROM maven:3.9.4-eclipse-temurin-23 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Fase 2: ejecutar el .jar generado
FROM eclipse-temurin:23-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
