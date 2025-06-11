# Imagen de JetBrains Runtime 17
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Archivos necesarios para aprovechar caché de Docker
COPY backend/pom.xml .
COPY backend/src ./src

# Build de la aplicación (ignora tests para producción)
RUN ./mvnw clean package -DskipTests

# Puerto expuesto (server.port)
EXPOSE 8080

# Comando para ejecutar (ajustar el nombre del JAR según el pom.xml)
CMD ["java", "-jar", "target/backend-0.7.0.jar"]