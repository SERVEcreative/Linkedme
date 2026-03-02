# Quick Start Guide

## Prerequisites
- Java 20+
- Maven 3.8+

## Database
By default, all services use **H2 in-memory database** (no installation required).

## Running Services

Start services in this order:

```bash
# Terminal 1: API Gateway (port 8080)
mvn spring-boot:run -pl api-gateway

# Terminal 2: User Service (port 8081)
mvn spring-boot:run -pl user-service

# Terminal 3: Profile Service (port 8082)
mvn spring-boot:run -pl profile-service
```

## Testing

### 1. Register User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","firstName":"Test","lastName":"User"}'
```

Response includes JWT token. Copy the `token` value.

### 2. Create Profile
```bash
curl -X PUT http://localhost:8080/api/profiles/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{"headline":"Software Engineer","about":"Full Stack Developer"}'
```

### 3. Get Profile
```bash
curl -X GET http://localhost:8080/api/profiles/me \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## H2 Database Console

Access the H2 web console to view data:

- **User Service**: http://localhost:8081/h2-console
- **Profile Service**: http://localhost:8082/h2-console

Login credentials:
- JDBC URL: `jdbc:h2:mem:linkedme_db`
- Username: `sa`
- Password: (leave empty)

## Using PostgreSQL (Optional)

To use PostgreSQL instead of H2:

1. Create `application-postgres.properties` in each service:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/linkedme_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

2. Run with postgres profile:
```bash
mvn spring-boot:run -pl user-service -Dspring-boot.run.arguments="--spring.profiles.active=postgres"
```

## Port Reference

| Service | Port |
|---------|------|
| API Gateway | 8080 |
| User Service | 8081 |
| Profile Service | 8082 |
| Post Service | 8083 |
| Connection Service | 8084 |
| Feed Service | 8085 |
| Job Service | 8087 |

## Important Notes

- **All requests** go through API Gateway (port 8080)
- **H2 data** is temporary (lost on restart)
- **Redis** is optional (only needed for logout token blacklist)
- **JWT tokens** expire after 24 hours
