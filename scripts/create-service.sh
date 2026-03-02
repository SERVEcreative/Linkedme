#!/bin/bash

# Script to create a new microservice structure

SERVICE_NAME=$1
SERVICE_PORT=$2

if [ -z "$SERVICE_NAME" ] || [ -z "$SERVICE_PORT" ]; then
    echo "Usage: ./create-service.sh <service-name> <port>"
    echo "Example: ./create-service.sh profile-service 8082"
    exit 1
fi

SERVICE_DIR="$SERVICE_NAME"
PACKAGE_NAME="com.linkedme.${SERVICE_NAME//-/.}"
MAIN_CLASS=$(echo "$SERVICE_NAME" | sed 's/-\([a-z]\)/\U\1/g' | sed 's/^\([a-z]\)/\U\1/')"Application"

# Create directory structure
mkdir -p "$SERVICE_DIR/src/main/java/${PACKAGE_NAME//./\/}"
mkdir -p "$SERVICE_DIR/src/main/resources"
mkdir -p "$SERVICE_DIR/src/test/java/${PACKAGE_NAME//./\/}"

# Create pom.xml
cat > "$SERVICE_DIR/pom.xml" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.linkedme</groupId>
        <artifactId>linkedme</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>$SERVICE_NAME</artifactId>
    <name>${SERVICE_NAME^} Service</name>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- PostgreSQL -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Actuator -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Micrometer Prometheus -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
EOF

# Create main application class
cat > "$SERVICE_DIR/src/main/java/${PACKAGE_NAME//./\/}/${MAIN_CLASS}.java" <<EOF
package $PACKAGE_NAME;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class $MAIN_CLASS {

    public static void main(String[] args) {
        SpringApplication.run($MAIN_CLASS.class, args);
    }
}
EOF

# Create application.yml
cat > "$SERVICE_DIR/src/main/resources/application.yml" <<EOF
server:
  port: $SERVICE_PORT

spring:
  application:
    name: $SERVICE_NAME
  
  datasource:
    url: jdbc:postgresql://localhost:5432/linkedme_db
    username: linkedme_user
    password: linkedme_password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    com.linkedme: DEBUG
EOF

echo "Service $SERVICE_NAME created successfully!"
echo "Main class: $PACKAGE_NAME.$MAIN_CLASS"
echo "Port: $SERVICE_PORT"
