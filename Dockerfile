FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml /app
COPY src /app
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
# Configura encoding, localtime, idioma y país
ENV LANG='es_MX.UTF-8'
ENV LC_ALL='es_MX.UTF-8'
ENV LANGUAGE='es_MX:es'
# Configura la zona horaria
ENV TZ=America/Mexico_City
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app
COPY --from=build /app/target/webpage-0.0.1-SNAPSHOT.jar /app/webpage.jar
EXPOSE 20000
ENTRYPOINT [ "java", "-jar", "/app/webpage.jar" ]