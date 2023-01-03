FROM openjdk:17-alpine3.14

MAINTAINER john@zarg.co.uk

EXPOSE 8080
VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=layertools -jar application.jar extract

FROM openjdk:17-alpine3.14


CMD ["java","-jar","/app.jar"]
