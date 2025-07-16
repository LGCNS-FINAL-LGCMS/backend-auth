FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
EXPOSE 8080
COPY build/libs/*.jar /app/app.jar
ENTRYPOINT java -jar "-Dspring.profiles.active=local" /app/app.jar
