FROM eclipse-temurin:21-jre

LABEL authors="Andrew Akash"

WORKDIR /app

COPY target/weddinghub-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]