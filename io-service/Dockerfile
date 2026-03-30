FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app-io
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=builder /app-io/target/*.jar app-io.jar
ENTRYPOINT ["java","-jar","./app-io.jar"]