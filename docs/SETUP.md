# Setup Guide

## Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Docker and Docker Compose
- PostgreSQL (or use Docker)
- Redis (or use Docker)
- Kafka (or use Docker)

## Quick Start

### 1. Start Infrastructure Services

```bash
# Start all infrastructure services (PostgreSQL, Redis, Kafka, Elasticsearch, etc.)
docker-compose -f docker/docker-compose.yml up -d

# Verify services are running
docker-compose -f docker/docker-compose.yml ps
```

### 2. Build the Project

```bash
# Build all modules
mvn clean install

# Or build from root
mvn clean install -DskipTests
```

### 3. Run Services

#### Option 1: Run Individual Services

```bash
# Terminal 1 - API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 2 - User Service
cd user-service
mvn spring-boot:run

# Terminal 3 - Profile Service
cd profile-service
mvn spring-boot:run

# ... and so on for other services
```

#### Option 2: Use Your IDE

1. Import the project as Maven project
2. Run each service's main class:
   - `ApiGatewayApplication`
   - `UserServiceApplication`
   - `ProfileServiceApplication`
   - etc.

### 4. Verify Services

- API Gateway: http://localhost:8080
- User Service: http://localhost:8081/actuator/health
- Profile Service: http://localhost:8082/actuator/health
- Post Service: http://localhost:8083/actuator/health
- etc.

### 5. Access Monitoring Tools

- **Zipkin**: http://localhost:9411
- **Kibana**: http://localhost:5601
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090

## Database Setup

The database will be automatically created when services start (with `ddl-auto: update`).

To manually create database:

```sql
CREATE DATABASE linkedme_db;
CREATE USER linkedme_user WITH PASSWORD 'linkedme_password';
GRANT ALL PRIVILEGES ON DATABASE linkedme_db TO linkedme_user;
```

## Kafka Topics Setup

Kafka topics will be auto-created. To manually create:

```bash
# List topics
docker exec -it linkedme-kafka kafka-topics --list --bootstrap-server localhost:9092

# Create topic
docker exec -it linkedme-kafka kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic post-events \
  --partitions 3 \
  --replication-factor 1
```

## Environment Variables

Create `.env` file in root directory:

```env
POSTGRES_DB=linkedme_db
POSTGRES_USER=linkedme_user
POSTGRES_PASSWORD=linkedme_password
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
ELASTICSEARCH_HOST=localhost
ELASTICSEARCH_PORT=9200
JWT_SECRET=your-secret-key-change-this-in-production
```

## Troubleshooting

### Port Already in Use
If a port is already in use, either:
1. Stop the service using that port
2. Change the port in `application.yml`

### Database Connection Issues
- Ensure PostgreSQL is running: `docker ps | grep postgres`
- Check connection string in `application.yml`
- Verify database credentials

### Kafka Connection Issues
- Ensure Kafka and Zookeeper are running
- Check Kafka bootstrap servers configuration
- Verify network connectivity

## Next Steps

1. Review the [README.md](README.md) for project overview
2. Check [HLD.md](HLD.md) for architecture details
3. Start implementing features in each service
4. Add tests for your code
5. Deploy to Kubernetes (see kubernetes/ directory)
