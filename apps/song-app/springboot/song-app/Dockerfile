FROM registry.access.redhat.com/ubi8/openjdk-17:1.14

ENV LANGUAGE='en_US:en'

COPY --chown=185 target/song-app-0.0.1-SNAPSHOT.jar /deployments/song-app.jar

EXPOSE 8080
USER 185
ENV JAVA_APP_JAR="/deployments/song-app.jar"
