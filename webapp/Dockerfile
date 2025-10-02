FROM gradle:8.14.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :webapp:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/webapp/build/libs/NextSkill-webapp.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

