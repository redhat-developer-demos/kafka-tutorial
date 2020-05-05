FROM quay.io/rhdevelopers/ubi8-java-11

COPY target/song-app.jar /deployments/song-app.jar

EXPOSE 8080
USER 1001

ENTRYPOINT [ "/deployments/run-java.sh" ]