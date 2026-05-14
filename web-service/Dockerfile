
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

COPY common ./common
RUN mvn -f common/pom.xml clean install -DskipTests

COPY barcode-core ./barcode-core
RUN mvn -f barcode-core/pom.xml clean install -DskipTests

COPY web-service ./web-service
RUN mvn -f web-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /build/web-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]