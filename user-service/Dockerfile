FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app-user
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=builder /app-user/target/*.jar app-user.jar
ENTRYPOINT ["java","-jar","./app-user.jar"]