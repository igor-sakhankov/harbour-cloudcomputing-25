FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the pre-built JAR file
COPY ./build/libs/shiftbooking-server-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
