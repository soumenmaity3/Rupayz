# Use an official OpenJDK image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY target/RupayZ.jar /app/app.jar

# Expose the port that Render assigns
EXPOSE 8080

# Run the app (Render injects $PORT automatically)
CMD ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
