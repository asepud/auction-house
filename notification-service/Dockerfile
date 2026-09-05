FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY auction-service/pom.xml auction-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml
COPY notification-service/src notification-service/src
RUN mvn -pl notification-service -am -DskipTests package
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/notification-service/target/notification-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]
