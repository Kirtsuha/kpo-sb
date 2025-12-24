FROM gradle:8.10.2-jdk21 AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle :payments-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/payments-service/build/libs/payments-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
