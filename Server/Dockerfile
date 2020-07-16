# 리눅스 환경에 대한 셋팅

# 자바 openjdk 8 환경
FROM openjdk:8-jdk-alpine

VOLUME /tmp

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]