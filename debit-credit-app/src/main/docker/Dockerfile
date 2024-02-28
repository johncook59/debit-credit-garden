FROM eclipse-temurin:17-alpine as builder

MAINTAINER john@zarg.co.uk

EXPOSE 8080
VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:17-alpine

ENV DB_HOST=postgres:5432
ENV DB_NAME=debit_credit
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=letmein

COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
