# Use a image based on Java JDK 21
FROM openjdk:21-jdk-slim

# Set work directory
WORKDIR /app

# Copy the JAR to the container
COPY build/libs/tenpo-backend-challenge-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Execute the app
ENTRYPOINT ["java", "-jar", "app.jar"]