# Usa una imagen base de OpenJDK 17
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo .jar del proyecto al contenedor
COPY target/healthplus-api-0.0.1-SNAPSHOT.jar /app/healthplus_api.jar

# Copia el archivo .env al contenedor
COPY .env /app/.env

EXPOSE 8105

CMD ["java", "-jar", "/app/healthplus_api.jar"]