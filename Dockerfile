# ==========================================
# Stage 1: Build the Application
# ==========================================
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy Maven descriptor and required source trees
COPY pom.xml ./
COPY backend ./backend
COPY frontend ./frontend

# Compile Java classes and package templates into the executable JAR
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Lightweight Runtime Environment
# ==========================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Ensure writable data directory for embedded database
RUN mkdir -p /app/data

# Copy the generated Spring Boot JAR from builder stage
COPY --from=builder /build/target/car-modification-management-1.0.0.jar app.jar

# Fallback default port
ENV PORT=8080
EXPOSE 8080

# Launch Spring Boot with dynamic Render port binding
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
