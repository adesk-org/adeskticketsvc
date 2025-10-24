# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -q -DskipTests package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/ticketsvc-*.jar app.jar
# App Runner expects the service to listen on PORT env var
ENV PORT=8080 JAVA_TOOL_OPTIONS="-XX:+UseZGC -XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
