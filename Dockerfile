# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# El JAR final según tu pom.xml (<finalName>aerotickets-backend</finalName>)
COPY --from=build /app/target/aerotickets-backend.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""
CMD ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod} -jar app.jar"]