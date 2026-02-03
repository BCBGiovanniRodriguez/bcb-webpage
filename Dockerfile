# ====== Etapa 1: Construcción ======
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar pom.xml y resolver dependencias primero
COPY pom.xml .
RUN mvn dependency:go-offline -B --quiet

# Copiar código fuente y compilar
COPY src ./src
COPY report-templates ./report-templates
RUN mvn clean package -DskipTests -B --quiet

# ====== Etapa 2: Imagen final ======
FROM eclipse-temurin:21-jre-alpine

# Metadata
LABEL maintainer="BCB Casa de Bolsa" \
description="Página Web desarrollada con Spring Boot & SSL habilitado" \
version="1.0.0"

WORKDIR /app

# Crear usuario no-root para seguridad
RUN addgroup -S spring && adduser -S spring -G spring \
    && apk add --no-cache curl wget openssl tzdata \
    && mkdir -p /app/uploads  \
    && chown -R spring:spring /app

# Configurar timezone
ENV TZ=America/Mexico_City \
    LANG='es_MX.UTF-8' \
    LC_ALL='es_MX.UTF-8' \
    LANGUAGE='es_MX:es' \
    JAVA_OPTS="-Xms512m -Xmx1024m"
    
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

COPY --chown=spring:spring --from=build /app/target/*.jar app.jar
COPY --chown=spring:spring ./certificates ./certificates
COPY --chown=spring:spring ./report-templates ./report-templates

USER spring

EXPOSE 8443

COPY --chown=spring:spring entrypoint.sh /app/
ENTRYPOINT ["/app/entrypoint.sh"]