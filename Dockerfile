FROM amazoncorretto:24-alpine

LABEL maintainer="Rizzerve Application"

WORKDIR /app

COPY target/rizzerve-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
