FROM amazoncorretto:17

ARG JAR_FILE=build/libs/SwaggerMock-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]