# Multi-stage Dockerfile for K12 Backend (Quarkus Java 21)
# Stage 1: Builder
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install build dependencies
RUN apk add --no-cache curl

# Set working directory
WORKDIR /build

# Copy build artifacts
COPY build/quarkus-app/ /build/

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Install runtime dependencies (for curl-based health checks)
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy Quarkus application from builder
COPY --from=builder /build /app

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose application port (Quarkus default: 8080)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/q/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]

# Default JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
