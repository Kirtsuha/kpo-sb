FROM gradle:8.10.2-jdk21 AS build
WORKDIR /home/gradle/src
COPY . .
RUN gradle :orders-service:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /home/gradle/src/orders-service/build/libs/orders-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
