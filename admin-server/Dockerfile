# syntax=docker/dockerfile:1
FROM gradle:8.14.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :admin-server:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/admin-server/build/libs/NextSkill-springboot-admin-service.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

