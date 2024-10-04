FROM openjdk:17.0.2-slim-bullseye

COPY build/libs/api-gateway-1.0.0-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]