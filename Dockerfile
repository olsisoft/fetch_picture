# Use an official OpenJDK image
FROM openjdk:17-jdk-alpine

# Set environment variable
ENV APP_HOME=/usr/app/

# Create application directory
RUN mkdir -p $APP_HOME

# Set working directory
WORKDIR $APP_HOME

# Copy built jar to container
COPY target/fetch_picture-0.0.1-SNAPSHOT.jar $APP_HOME

# Expose the port your application is running on
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "fetch_picture-0.0.1-SNAPSHOT.jar"]
