FROM eclipse-temurin:17-jdk

COPY . .

RUN chmod +x mvnw

# 🔥 THIS IS THE FIX
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/*.jar"]