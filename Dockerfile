 
# Usa una imagen de Java
FROM eclipse-temurin:17

# Crea un directorio de trabajo
WORKDIR /app

# Copia todo el código fuente
COPY . .

# Da permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Compila el proyecto
RUN ./mvnw clean package -DskipTests

# Expón el puerto en el que corre tu app
EXPOSE 8080

# Comando para iniciar Spring Boot
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
