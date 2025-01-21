FROM maven:3.8.4-openjdk-17-slim

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean install package && \
     cp -r target/*.jar /app/app.jar


EXPOSE 8777

CMD ["java", "-jar", "/app/app.jar"]
