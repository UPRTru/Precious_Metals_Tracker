FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY build/libs/user-service-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]