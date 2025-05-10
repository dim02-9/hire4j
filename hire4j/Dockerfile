FROM ubuntu:latest

ARG SPRING_DATASOURCE_URL

WORKDIR /home/hire4j

RUN apt-get update && apt-get install -y openjdk-21-jdk

COPY target/hire4j-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "hire4j-0.0.1-SNAPSHOT.jar", "--spring.datasource.url=${SPRING_DATASOURCE_URL}"]
