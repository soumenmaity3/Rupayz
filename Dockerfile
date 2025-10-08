FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/RupayZ.jar app.jar
EXPOSE 8080
CMD ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
