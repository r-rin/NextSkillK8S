# syntax=docker/dockerfile:1
FROM gradle:8.14.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :email-service:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/email-service/build/libs/NextSkill-email-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

