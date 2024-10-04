FROM openjdk:17.0.2-slim-bullseye

COPY build/libs/*-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "/app.jar" ]
