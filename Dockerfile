# ===============================
# Build stage
# ===============================
FROM maven:3.9-amazoncorretto-21 AS build

WORKDIR /app

# Sadece pom.xml kopyalanıyor
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B


# Copy source and build
COPY backend/src ./src
RUN mvn clean package -DskipTests && ls -lh /app/target

# ===============================
# Runtime stage
# ===============================
FROM amazoncorretto:21-alpine

WORKDIR /app

# Optional: uploads directory
RUN mkdir -p /app/uploads

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
