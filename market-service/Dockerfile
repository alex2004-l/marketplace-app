FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app-market
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=builder /app-market/target/*.jar app-market.jar
ENTRYPOINT ["java","-jar","./app-market.jar"]