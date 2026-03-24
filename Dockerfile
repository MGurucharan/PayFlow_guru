FROM openjdk:17-jdk-slim

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean package

EXPOSE 8080

CMD ["java", "-jar", "target/*.jar"]
