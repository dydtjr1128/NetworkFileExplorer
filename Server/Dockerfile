# 리눅스 환경에 대한 셋팅

# 자바 openjdk 8 환경
FROM openjdk:8-jdk-alpine

# 리눅스의 /app을 저장공간으로 사용하겠다는 의미. cd backend 동일
WORKDIR /backend

VOLUME /tmp

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]