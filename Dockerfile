FROM amazoncorretto:21-alpine
COPY target/*.jar app.jar
EXPOSE 667
ENTRYPOINT ["java", "-jar", "/app.jar"]
