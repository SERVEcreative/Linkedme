# LinkedMe - Professional Networking Platform

## 🎯 Project Overview

A scalable professional networking platform built with Spring Boot microservices architecture, designed to handle billions of interactions. This platform includes profiles, posts, connections, job postings, and real-time feed generation using Kafka streams.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│              Load Balancer / API Gateway                 │
│         (Spring Cloud Gateway + Security)                 │
└──────────────────────┬──────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  User          │          │  Profile         │
│  Service       │          │  Service         │
│  - Auth (JWT)  │          │  - Profiles      │
│  - RBAC        │          │  - Experience   │
│  - Sessions    │          │  - Education    │
└───────┬────────┘          └─────────┬───────┘
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  Post          │          │  Connection      │
│  Service       │          │  Service         │
│  - Posts       │          │  - Connections   │
│  - Comments    │          │  - Followers     │
│  - Reactions   │          │  - Network      │
└───────┬────────┘          └─────────┬───────┘
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  Feed          │          │  Job            │
│  Service       │          │  Service        │
│  - Kafka Stream│          │  - Job Postings │
│  - Feed Gen    │          │  - Applications │
└───────┬────────┘          └─────────┬───────┘
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  Recommendation│          │  Notification    │
│  Service       │          │  Service         │
│  - ML Models   │          │  - Real-time     │
│  - Feign Client│          │  - Email         │
└───────┬────────┘          └─────────┬───────┘
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  Search        │          │  Analytics       │
│  Service       │          │  Service         │
│  - Elasticsearch│         │  - User Actions │
│  - Full-text   │          │  - ELK Stack    │
└─────────────────┘          └──────────────────┘
```

## 🚀 Microservices

### 1. API Gateway Service
- Request routing to all services
- JWT authentication and validation
- Rate limiting per user/IP
- Request/Response logging
- Load balancing
- Circuit breaker pattern

### 2. User Service
- User registration and authentication
- JWT token generation and validation
- Password encryption (BCrypt)
- Session management
- User roles and permissions
- OAuth2 integration (Google, GitHub)

### 3. Profile Service
- User profile creation and management
- Professional experience
- Education history
- Skills and endorsements
- Profile visibility settings
- Profile recommendations

### 4. Post Service
- Post creation (text, images, videos)
- Post editing and deletion
- Post comments
- Post reactions (like, celebrate, support)
- Post sharing
- Post visibility (public, connections, private)

### 5. Connection Service
- Send connection requests
- Accept/reject connections
- Follow/unfollow users
- Connection network graph
- Mutual connections
- Connection recommendations

### 6. Feed Service (Kafka Streams)
- Real-time feed generation using Kafka Streams
- Personalized feed algorithm
- Feed ranking and relevance
- Feed caching (Redis)
- Feed pagination
- Real-time feed updates

### 7. Recommendation Service
- Job recommendations
- Connection recommendations
- Content recommendations
- Skill recommendations
- Company recommendations
- Uses Spring Cloud Feign for inter-service calls

### 8. Job Service
- Job posting creation
- Job search and filtering
- Job applications
- Job recommendations
- Company job listings
- Application tracking

### 9. Notification Service
- Real-time notifications (WebSocket)
- Email notifications
- Push notifications
- Notification preferences
- Notification history
- In-app notification center

### 10. Search Service
- Full-text search (Elasticsearch)
- User search
- Post search
- Job search
- Company search
- Advanced filters

### 11. Analytics Service
- User action logging
- Content moderation logs
- Engagement metrics
- User behavior analytics
- ELK Stack integration
- Audit trail

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.x**
- **Spring Cloud Gateway** (API Gateway)
- **Spring Security** (JWT, OAuth2)
- **Spring Data JPA** (PostgreSQL)
- **Spring WebSocket** (Real-time notifications)
- **Spring Kafka** (Event streaming)
- **Spring Cloud OpenFeign** (Inter-service communication)
- **Spring Cloud Sleuth** (Distributed tracing)

### Databases
- **PostgreSQL** (Primary database - Users, Posts, Connections)
- **Redis** (Caching, sessions, feed cache)
- **Elasticsearch** (Search functionality)
- **MongoDB** (Optional: for analytics data)

### Messaging & Streaming
- **Apache Kafka** (Event streaming, feed generation)
- **Kafka Streams** (Real-time feed processing)
- **RabbitMQ** (Optional: for async tasks)

### Infrastructure & Orchestration
- **Docker** (Containerization)
- **Docker Compose** (Local development)
- **Kubernetes** (Production orchestration)
- **Helm Charts** (K8s deployment)

### Observability
- **Zipkin** (Distributed tracing)
- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Prometheus** (Metrics collection)
- **Grafana** (Metrics visualization)
- **Spring Boot Actuator** (Health checks)

### Additional Tools
- **Swagger/OpenAPI** (API documentation)
- **JUnit 5 & Mockito** (Testing)
- **TestContainers** (Integration testing)

## 📋 Key Features

### Core Features
- ✅ User authentication and authorization (JWT)
- ✅ Professional profile management
- ✅ Post creation with media support
- ✅ Connection requests and network building
- ✅ Real-time personalized feed
- ✅ Job postings and applications
- ✅ Search functionality
- ✅ Real-time notifications
- ✅ Comments and reactions

### Advanced Features
- ✅ Feed generation with Kafka Streams
- ✅ Personalized recommendations
- ✅ Activity logging and audit trail
- ✅ Content moderation
- ✅ Analytics and insights
- ✅ Inter-service communication (Feign)
- ✅ Distributed tracing (Zipkin)
- ✅ Comprehensive logging (ELK Stack)
- ✅ Scalable architecture for billions of interactions

## 🎯 Skills Demonstrated

- ✅ Microservices architecture
- ✅ Event-driven architecture (Kafka)
- ✅ Real-time stream processing (Kafka Streams)
- ✅ Distributed systems
- ✅ Inter-service communication (Feign)
- ✅ Observability (Tracing, Logging, Metrics)
- ✅ Kubernetes orchestration
- ✅ Scalability patterns
- ✅ Spring Security (JWT, OAuth2)
- ✅ Production-ready features

## 📦 Project Structure

```
linkedme/
├── api-gateway/
│   ├── src/main/java/.../gateway/
│   └── pom.xml
├── user-service/
│   ├── src/main/java/.../user/
│   └── pom.xml
├── profile-service/
│   ├── src/main/java/.../profile/
│   └── pom.xml
├── post-service/
│   ├── src/main/java/.../post/
│   └── pom.xml
├── connection-service/
│   ├── src/main/java/.../connection/
│   └── pom.xml
├── feed-service/
│   ├── src/main/java/.../feed/
│   └── pom.xml
├── recommendation-service/
│   ├── src/main/java/.../recommendation/
│   └── pom.xml
├── job-service/
│   ├── src/main/java/.../job/
│   └── pom.xml
├── notification-service/
│   ├── src/main/java/.../notification/
│   └── pom.xml
├── search-service/
│   ├── src/main/java/.../search/
│   └── pom.xml
├── analytics-service/
│   ├── src/main/java/.../analytics/
│   └── pom.xml
├── docker/
│   ├── docker-compose.yml
│   └── Dockerfile
├── kubernetes/
│   ├── deployments/
│   ├── services/
│   └── helm-charts/
└── pom.xml (Parent POM)
```

## 🔄 Event-Driven Architecture

### Kafka Topics

1. **user-events** - User actions (login, profile update)
2. **post-events** - Post creation, update, delete
3. **connection-events** - Connection requests, accepts
4. **reaction-events** - Likes, comments, shares
5. **job-events** - Job postings, applications
6. **feed-updates** - Feed generation events

### Kafka Streams Processing

```java
// Feed Service - Kafka Streams Example
@Configuration
public class FeedStreamConfig {
    
    @Bean
    public KStream<String, PostEvent> feedStream(StreamsBuilder builder) {
        // Process post events
        // Generate personalized feeds
        // Update feed cache in Redis
    }
}
```

## 🔍 Observability Stack

### Distributed Tracing (Zipkin)
- Track requests across all microservices
- Visualize service dependencies
- Identify performance bottlenecks
- Request flow visualization

### Logging (ELK Stack)
- **Elasticsearch**: Store and index logs
- **Logstash**: Process and transform logs
- **Kibana**: Visualize and analyze logs
- User action auditing
- Content moderation logs

### Metrics (Prometheus + Grafana)
- Service health metrics
- Request rates and latencies
- Error rates
- Resource utilization
- Custom business metrics

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Kubernetes (for production)
- PostgreSQL
- Redis
- Elasticsearch
- Kafka & Zookeeper

### Local Development

```bash
# Start all infrastructure services
docker-compose -f docker/docker-compose.yml up -d

# Start individual services
cd user-service && mvn spring-boot:run
cd profile-service && mvn spring-boot:run
# ... etc
```

### Kubernetes Deployment

```bash
# Apply Kubernetes configurations
kubectl apply -f kubernetes/

# Or use Helm
helm install linkedme ./kubernetes/helm-charts/
```

## 📊 API Documentation

Swagger UI available at: `http://localhost:8080/swagger-ui.html`

## 🔒 Security

- JWT-based authentication
- OAuth2 integration (Google, GitHub)
- Role-based access control (RBAC)
- Rate limiting
- Input validation and sanitization
- Secure file uploads

## 📈 Monitoring & Observability

- **Zipkin**: `http://localhost:9411`
- **Kibana**: `http://localhost:5601`
- **Grafana**: `http://localhost:3000`
- **Prometheus**: `http://localhost:9090`

## 🎓 What You Will Learn

1. **Scaling for Billions**: Design patterns for handling massive user base
2. **Kafka Streams**: Real-time feed generation and event processing
3. **Kubernetes**: Container orchestration and deployment
4. **Full Observability**: Tracing, logging, and metrics
5. **Inter-Service Communication**: Feign clients for service calls
6. **Event-Driven Architecture**: Asynchronous event processing
7. **Microservices Best Practices**: Service design and communication

## 📝 Implementation Phases

### Phase 1: Foundation (Week 1-2)
- Project structure setup
- API Gateway configuration
- User Service with JWT
- Database setup
- Docker Compose configuration

### Phase 2: Core Services (Week 2-4)
- Profile Service
- Post Service
- Connection Service
- Basic APIs and testing

### Phase 3: Advanced Features (Week 4-6)
- Feed Service with Kafka Streams
- Recommendation Service with Feign
- Search Service with Elasticsearch
- Notification Service

### Phase 4: Observability (Week 6-7)
- Zipkin integration
- ELK Stack setup
- Prometheus and Grafana
- Logging and monitoring

### Phase 5: Deployment (Week 7-8)
- Kubernetes configurations
- Helm charts
- Production deployment
- Documentation

## 🤝 Contributing

This is a portfolio project demonstrating enterprise-level Spring Boot and microservices skills.

## 🚀 Quick Start

See [SETUP.md](SETUP.md) for detailed setup instructions.

```bash
# Start infrastructure services
docker-compose -f docker/docker-compose.yml up -d

# Build project
mvn clean install

# Run services
cd api-gateway && mvn spring-boot:run
cd user-service && mvn spring-boot:run
# ... and so on
```

## 📚 Documentation

- [High-Level Design (HLD)](HLD.md) - Complete system architecture
- [Setup Guide](SETUP.md) - Detailed setup instructions
- [Project Structure](PROJECT_STRUCTURE.md) - Project organization
- [GitHub Setup](GITHUB_SETUP.md) - Repository initialization guide

## 🛠️ Technology Stack

### Backend
- Spring Boot 3.2.0
- Spring Cloud Gateway
- Spring Security (JWT)
- Spring Data JPA
- Spring Kafka & Kafka Streams
- Spring Cloud OpenFeign

### Infrastructure
- Docker & Docker Compose
- Kubernetes
- PostgreSQL
- Redis
- Apache Kafka
- Elasticsearch

### Observability
- Zipkin (Distributed Tracing)
- ELK Stack (Logging)
- Prometheus & Grafana (Metrics)

## 📝 License

MIT License

## 🙏 Acknowledgments

LinkedMe is a professional networking platform demonstrating enterprise-level Spring Boot microservices architecture.
