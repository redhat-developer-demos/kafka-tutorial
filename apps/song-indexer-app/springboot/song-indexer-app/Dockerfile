FROM quay.io/rhdevelopers/ubi8-java-11

COPY target/song-indexer-app.jar /deployments/song-indexer-app.jar

EXPOSE 8080
USER 1001

ENTRYPOINT [ "/deployments/run-java.sh" ]