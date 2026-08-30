# Build stage
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy pom first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B -q || true

# Copy source and build
COPY src ./src

# Force frontend rebuild by busting the build layer cache
ARG CACHEBUST=1
RUN rm -rf /app/node_modules /app/target || true

RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=build /app/target/clarituz-gerador-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
