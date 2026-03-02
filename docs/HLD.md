# High-Level Design (HLD) - Professional Networking Platform

## 1. System Overview

### 1.1 Purpose
LinkedMe - A scalable professional networking platform designed to handle billions of interactions with microservices architecture, event-driven design, and full observability.

### 1.2 System Goals
- Handle millions of users and billions of interactions
- Real-time feed generation using Kafka Streams
- Personalized recommendations
- Comprehensive logging and auditing
- High availability and scalability
- Full observability (tracing, logging, metrics)

### 1.3 Non-Functional Requirements
- **Scalability**: Support 100M+ users, 1B+ interactions/day
- **Performance**: <200ms API response time (p95)
- **Availability**: 99.9% uptime
- **Reliability**: Fault-tolerant with circuit breakers
- **Security**: JWT authentication, RBAC, data encryption

## 2. System Architecture

### 2.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Client Applications                       │
│              (Web, Mobile, API Clients)                      │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│                  Load Balancer                                │
│              (Nginx / AWS ALB)                                │
└──────────────────────┬──────────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────────┐
│              API Gateway (Spring Cloud Gateway)               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  - Routing                                            │  │
│  │  - Authentication (JWT Validation)                 │  │
│  │  - Rate Limiting                                     │  │
│  │  - Request/Response Logging                          │  │
│  │  - Circuit Breaker                                    │  │
│  └──────────────────────────────────────────────────────┘  │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  User          │          │  Profile         │
│  Service       │          │  Service         │
│  Port: 8081    │          │  Port: 8082     │
└───────┬────────┘          └─────────┬───────┘
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  Post          │          │  Connection      │
│  Service       │          │  Service         │
│  Port: 8083    │          │  Port: 8084     │
└───────┬────────┘          └─────────┬───────┘
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  Feed          │          │  Recommendation  │
│  Service       │          │  Service         │
│  Port: 8085    │          │  Port: 8086     │
│  Kafka Streams │          │  Feign Clients   │
└───────┬────────┘          └─────────┬───────┘
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  Job           │          │  Notification    │
│  Service       │          │  Service         │
│  Port: 8087    │          │  Port: 8088     │
└───────┬────────┘          └─────────┬───────┘
        │                             │
┌───────▼────────┐          ┌─────────▼────────┐
│  Search        │          │  Analytics       │
│  Service       │          │  Service         │
│  Port: 8089    │          │  Port: 8090     │
│  Elasticsearch │          │  ELK Stack       │
└─────────────────┘          └──────────────────┘
```

### 2.2 Data Flow Architecture

```
User Action → API Gateway → Service → Database
                    │
                    ├──→ Kafka Topic (Event)
                    │
                    └──→ Kafka Streams (Feed Service)
                              │
                              └──→ Redis (Feed Cache)
```

## 3. Component Design

### 3.1 API Gateway Service

**Responsibilities:**
- Route requests to appropriate microservices
- Authenticate requests using JWT
- Rate limiting per user/IP
- Request/response logging
- Circuit breaker for fault tolerance

**Technology:**
- Spring Cloud Gateway
- Spring Security
- Redis (for rate limiting)

**Endpoints:**
- `/api/users/**` → User Service
- `/api/profiles/**` → Profile Service
- `/api/posts/**` → Post Service
- `/api/connections/**` → Connection Service
- `/api/feed/**` → Feed Service
- `/api/jobs/**` → Job Service

### 3.2 User Service

**Responsibilities:**
- User registration and authentication
- JWT token generation and validation
- Password management
- Session management
- User roles and permissions

**Database Schema:**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) DEFAULT 'USER',
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    token VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Key APIs:**
- `POST /api/users/register` - User registration
- `POST /api/users/login` - User login (returns JWT)
- `GET /api/users/me` - Get current user
- `POST /api/users/logout` - Logout

### 3.3 Profile Service

**Responsibilities:**
- User profile management
- Professional experience
- Education history
- Skills and endorsements
- Profile visibility settings

**Database Schema:**
```sql
CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE REFERENCES users(id),
    headline VARCHAR(255),
    summary TEXT,
    location VARCHAR(255),
    industry VARCHAR(100),
    profile_image_url VARCHAR(500),
    background_image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE experiences (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT REFERENCES profiles(id),
    title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE,
    is_current BOOLEAN DEFAULT false,
    description TEXT
);

CREATE TABLE educations (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT REFERENCES profiles(id),
    school VARCHAR(255) NOT NULL,
    degree VARCHAR(255),
    field_of_study VARCHAR(255),
    start_date DATE,
    end_date DATE
);

CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT REFERENCES profiles(id),
    skill_name VARCHAR(100) NOT NULL,
    endorsements_count INT DEFAULT 0
);
```

### 3.4 Post Service

**Responsibilities:**
- Post creation, update, deletion
- Post comments
- Post reactions (like, celebrate, support)
- Post sharing
- Media upload handling

**Database Schema:**
```sql
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    content TEXT NOT NULL,
    media_urls TEXT[], -- Array of media URLs
    visibility VARCHAR(50) DEFAULT 'PUBLIC', -- PUBLIC, CONNECTIONS, PRIVATE
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    shares_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE post_reactions (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES posts(id),
    user_id BIGINT REFERENCES users(id),
    reaction_type VARCHAR(50), -- LIKE, CELEBRATE, SUPPORT, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(post_id, user_id)
);

CREATE TABLE post_comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT REFERENCES posts(id),
    user_id BIGINT REFERENCES users(id),
    content TEXT NOT NULL,
    parent_comment_id BIGINT REFERENCES post_comments(id), -- For nested comments
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Event Publishing:**
- Post created → `post-events` Kafka topic
- Post updated → `post-events` Kafka topic
- Reaction added → `reaction-events` Kafka topic
- Comment added → `reaction-events` Kafka topic

### 3.5 Connection Service

**Responsibilities:**
- Send connection requests
- Accept/reject connections
- Follow/unfollow users
- Get connection network
- Mutual connections

**Database Schema:**
```sql
CREATE TABLE connections (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT REFERENCES users(id),
    addressee_id BIGINT REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(requester_id, addressee_id),
    CHECK(requester_id != addressee_id)
);

CREATE TABLE follows (
    id BIGSERIAL PRIMARY KEY,
    follower_id BIGINT REFERENCES users(id),
    following_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(follower_id, following_id),
    CHECK(follower_id != following_id)
);
```

**Event Publishing:**
- Connection request → `connection-events` Kafka topic
- Connection accepted → `connection-events` Kafka topic

### 3.6 Feed Service (Kafka Streams)

**Responsibilities:**
- Real-time feed generation using Kafka Streams
- Personalized feed algorithm
- Feed ranking and relevance scoring
- Feed caching in Redis

**Kafka Streams Processing:**
```java
@Configuration
public class FeedStreamConfig {
    
    @Bean
    public KStream<String, FeedItem> feedStream(StreamsBuilder builder) {
        // 1. Read from post-events topic
        KStream<String, PostEvent> postEvents = builder.stream("post-events");
        
        // 2. Read from connection-events topic
        KStream<String, ConnectionEvent> connectionEvents = builder.stream("connection-events");
        
        // 3. Join posts with connections
        KStream<String, FeedItem> feedItems = postEvents
            .join(connectionEvents, 
                (post, connection) -> createFeedItem(post, connection),
                JoinWindows.of(Duration.ofMinutes(5)),
                StreamJoined.with(Serdes.String(), postEventSerde, connectionEventSerde)
            );
        
        // 4. Apply ranking algorithm
        KStream<String, FeedItem> rankedFeeds = feedItems
            .mapValues(this::applyRankingAlgorithm);
        
        // 5. Write to feed-updates topic
        rankedFeeds.to("feed-updates");
        
        // 6. Update Redis cache
        rankedFeeds.foreach((key, value) -> updateFeedCache(key, value));
        
        return rankedFeeds;
    }
}
```

**Feed Generation Algorithm:**
1. Get user's connections
2. Fetch posts from connections
3. Apply relevance scoring (recency, engagement, user preferences)
4. Rank and sort posts
5. Cache in Redis with TTL
6. Return paginated feed

**Redis Cache Structure:**
```
Key: feed:user:{userId}
Value: List of FeedItem (JSON)
TTL: 5 minutes
```

### 3.7 Recommendation Service

**Responsibilities:**
- Job recommendations
- Connection recommendations
- Content recommendations
- Skill recommendations

**Inter-Service Communication (Feign):**
```java
@FeignClient(name = "profile-service", url = "${services.profile.url}")
public interface ProfileServiceClient {
    
    @GetMapping("/api/profiles/{userId}")
    ProfileDTO getProfile(@PathVariable Long userId);
    
    @GetMapping("/api/profiles/{userId}/skills")
    List<SkillDTO> getSkills(@PathVariable Long userId);
}

@FeignClient(name = "connection-service", url = "${services.connection.url}")
public interface ConnectionServiceClient {
    
    @GetMapping("/api/connections/{userId}/network")
    List<Long> getNetwork(@PathVariable Long userId);
    
    @GetMapping("/api/connections/{userId}/mutual/{otherUserId}")
    List<Long> getMutualConnections(@PathVariable Long userId, @PathVariable Long otherUserId);
}
```

**Recommendation Algorithm:**
1. Fetch user profile and skills
2. Get user's connection network
3. Find similar profiles (skill matching, industry)
4. Calculate recommendation scores
5. Return top recommendations

### 3.8 Job Service

**Responsibilities:**
- Job posting creation and management
- Job search and filtering
- Job applications
- Application tracking

**Database Schema:**
```sql
CREATE TABLE job_postings (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255),
    employment_type VARCHAR(50), -- FULL_TIME, PART_TIME, CONTRACT
    salary_range VARCHAR(100),
    required_skills TEXT[],
    posted_by BIGINT REFERENCES users(id),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE job_applications (
    id BIGSERIAL PRIMARY KEY,
    job_posting_id BIGINT REFERENCES job_postings(id),
    applicant_id BIGINT REFERENCES users(id),
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, REVIEWED, ACCEPTED, REJECTED
    cover_letter TEXT,
    resume_url VARCHAR(500),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3.9 Notification Service

**Responsibilities:**
- Real-time notifications (WebSocket)
- Email notifications
- Push notifications
- Notification preferences

**WebSocket Implementation:**
```java
@Controller
public class NotificationController {
    
    @MessageMapping("/notifications/{userId}")
    @SendTo("/topic/notifications/{userId}")
    public NotificationDTO sendNotification(@DestinationVariable Long userId, 
                                          NotificationDTO notification) {
        return notification;
    }
}
```

**Notification Types:**
- Connection request
- Connection accepted
- Post liked/commented
- Job application status
- New job recommendations

### 3.10 Search Service

**Responsibilities:**
- Full-text search using Elasticsearch
- User search
- Post search
- Job search
- Company search

**Elasticsearch Indexes:**
- `users` - User profiles
- `posts` - Post content
- `jobs` - Job postings
- `companies` - Company profiles

**Search Implementation:**
```java
@Service
public class SearchService {
    
    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;
    
    public SearchResult searchUsers(String query, int page, int size) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.multiMatchQuery(query, "firstName", "lastName", "headline"))
            .withPageable(PageRequest.of(page, size))
            .build();
        
        return elasticsearchTemplate.search(searchQuery, UserDocument.class);
    }
}
```

### 3.11 Analytics Service

**Responsibilities:**
- Log all user actions
- Content moderation logging
- Engagement metrics
- User behavior analytics

**ELK Stack Integration:**
- **Elasticsearch**: Store logs
- **Logstash**: Process and transform logs
- **Kibana**: Visualize logs

**Action Logging:**
```java
@Service
public class AnalyticsService {
    
    public void logUserAction(Long userId, String action, Map<String, Object> metadata) {
        ActionLog log = ActionLog.builder()
            .userId(userId)
            .action(action)
            .metadata(metadata)
            .timestamp(LocalDateTime.now())
            .build();
        
        // Send to Kafka topic for ELK Stack
        kafkaTemplate.send("action-logs", log);
    }
}
```

## 4. Database Design

### 4.1 Database Strategy
- **PostgreSQL**: Primary database for transactional data
- **Redis**: Caching and session storage
- **Elasticsearch**: Search functionality
- **MongoDB**: Optional for analytics data

### 4.2 Data Partitioning Strategy
- **Users**: Partition by user_id hash
- **Posts**: Partition by user_id (posts by user)
- **Connections**: Partition by user_id (connections per user)
- **Feed Cache**: Partition by user_id in Redis

### 4.3 Indexing Strategy
- Primary keys on all tables
- Foreign key indexes
- Composite indexes for common queries
- Full-text indexes in Elasticsearch

## 5. Event-Driven Architecture

### 5.1 Kafka Topics

| Topic Name | Purpose | Partition Count |
|------------|---------|------------------|
| `user-events` | User actions (login, profile update) | 10 |
| `post-events` | Post creation, update, delete | 20 |
| `connection-events` | Connection requests, accepts | 10 |
| `reaction-events` | Likes, comments, shares | 20 |
| `job-events` | Job postings, applications | 10 |
| `feed-updates` | Feed generation events | 20 |
| `action-logs` | User action logs for ELK | 10 |
| `notifications` | Notification events | 10 |

### 5.2 Event Schema

```json
{
  "eventId": "uuid",
  "eventType": "POST_CREATED",
  "userId": 123,
  "timestamp": "2024-01-01T10:00:00Z",
  "payload": {
    "postId": 456,
    "content": "Post content..."
  }
}
```

## 6. Inter-Service Communication

### 6.1 Synchronous Communication (Feign)
- Profile Service → User Service (get user details)
- Recommendation Service → Profile Service (get profile)
- Recommendation Service → Connection Service (get network)
- Feed Service → Post Service (get post details)

### 6.2 Asynchronous Communication (Kafka)
- Post Service → Feed Service (post events)
- Connection Service → Feed Service (connection events)
- All services → Analytics Service (action logs)

### 6.3 Circuit Breaker Pattern
```java
@FeignClient(name = "profile-service", 
             fallback = ProfileServiceFallback.class)
public interface ProfileServiceClient {
    // Feign client methods
}
```

## 7. Security Design

### 7.1 Authentication
- JWT-based authentication
- Token expiration and refresh
- OAuth2 integration (Google, GitHub)

### 7.2 Authorization
- Role-based access control (RBAC)
- Resource-level permissions
- API-level rate limiting

### 7.3 Data Security
- Password encryption (BCrypt)
- HTTPS for all communications
- Input validation and sanitization
- SQL injection prevention

## 8. Scalability Design

### 8.1 Horizontal Scaling
- Stateless services for easy scaling
- Database read replicas
- Redis cluster for caching
- Kafka partitions for parallel processing

### 8.2 Caching Strategy
- Redis for feed cache (5 min TTL)
- Redis for user sessions
- Redis for frequently accessed data
- CDN for static assets

### 8.3 Load Balancing
- API Gateway load balancing
- Service-level load balancing
- Database connection pooling

## 9. Observability

### 9.1 Distributed Tracing (Zipkin)
- Track requests across all services
- Service dependency visualization
- Performance bottleneck identification

### 9.2 Logging (ELK Stack)
- Centralized logging
- User action auditing
- Content moderation logs
- Error tracking

### 9.3 Metrics (Prometheus + Grafana)
- Request rates and latencies
- Error rates
- Resource utilization
- Business metrics

## 10. Deployment Architecture

### 10.1 Kubernetes Deployment
- Each service as a Kubernetes Deployment
- Service discovery using Kubernetes Services
- ConfigMaps for configuration
- Secrets for sensitive data
- Horizontal Pod Autoscaling (HPA)

### 10.2 Helm Charts
- Template-based deployment
- Environment-specific configurations
- Easy rollback capabilities

## 11. API Design

### 11.1 RESTful APIs
- RESTful conventions
- Versioning (`/api/v1/...`)
- Pagination
- Filtering and sorting

### 11.2 API Documentation
- Swagger/OpenAPI documentation
- Interactive API explorer

## 12. Testing Strategy

### 12.1 Unit Testing
- JUnit 5 for unit tests
- Mockito for mocking
- Test coverage > 80%

### 12.2 Integration Testing
- TestContainers for database testing
- Kafka test containers
- API integration tests

### 12.3 End-to-End Testing
- API endpoint testing
- Workflow testing
- Performance testing

## 13. Monitoring and Alerting

### 13.1 Health Checks
- Spring Boot Actuator endpoints
- Database connectivity checks
- Kafka connectivity checks
- Redis connectivity checks

### 13.2 Alerts
- Service down alerts
- High error rate alerts
- Performance degradation alerts
- Resource exhaustion alerts

## 14. Disaster Recovery

### 14.1 Backup Strategy
- Daily database backups
- Point-in-time recovery
- Cross-region backups

### 14.2 Failover Strategy
- Multi-region deployment
- Database replication
- Automatic failover

## 15. Performance Optimization

### 15.1 Database Optimization
- Query optimization
- Index optimization
- Connection pooling
- Read replicas

### 15.2 Caching
- Redis caching for feeds
- CDN for static assets
- Application-level caching

### 15.3 Async Processing
- Kafka for async operations
- Background job processing
- Batch operations

---

This HLD provides a comprehensive design for building LinkedMe platform with Spring Boot microservices, Kafka Streams, and full observability.
