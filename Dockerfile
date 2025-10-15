# ====== Etapa 1: Construcción ======
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar pom.xml y resolver dependencias primero
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# ====== Etapa 2: Imagen final ======
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

ENV LANG='es_MX.UTF-8'
ENV LC_ALL='es_MX.UTF-8'
ENV LANGUAGE='es_MX:es'
ENV TZ=America/Mexico_City
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
COPY --from=build /app/target/*.jar app.jar

ENV SSL_KEYSTORE_PASSWORD=BcbW3bp4g3_2025-+*$
EXPOSE 8443

ENTRYPOINT ["java", "-jar", "app.jar"]
