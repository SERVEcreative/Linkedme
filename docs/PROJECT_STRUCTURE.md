# Project Structure

```
linkedme/
├── api-gateway/                    # API Gateway Service
│   ├── src/main/java/.../gateway/
│   ├── src/main/resources/
│   └── pom.xml
│
├── user-service/                   # User Service
│   ├── src/main/java/.../user/
│   ├── src/main/resources/
│   └── pom.xml
│
├── profile-service/                # Profile Service
│   ├── src/main/java/.../profile/
│   ├── src/main/resources/
│   └── pom.xml
│
├── post-service/                   # Post Service
│   ├── src/main/java/.../post/
│   ├── src/main/resources/
│   └── pom.xml
│
├── connection-service/             # Connection Service
│   ├── src/main/java/.../connection/
│   ├── src/main/resources/
│   └── pom.xml
│
├── feed-service/                   # Feed Service (Kafka Streams)
│   ├── src/main/java/.../feed/
│   ├── src/main/resources/
│   └── pom.xml
│
├── recommendation-service/         # Recommendation Service
│   ├── src/main/java/.../recommendation/
│   ├── src/main/resources/
│   └── pom.xml
│
├── job-service/                    # Job Service
│   ├── src/main/java/.../job/
│   ├── src/main/resources/
│   └── pom.xml
│
├── notification-service/           # Notification Service
│   ├── src/main/java/.../notification/
│   ├── src/main/resources/
│   └── pom.xml
│
├── search-service/                 # Search Service
│   ├── src/main/java/.../search/
│   ├── src/main/resources/
│   └── pom.xml
│
├── analytics-service/              # Analytics Service
│   ├── src/main/java/.../analytics/
│   ├── src/main/resources/
│   └── pom.xml
│
├── docker/                         # Docker configurations
│   ├── docker-compose.yml
│   ├── prometheus/
│   │   └── prometheus.yml
│   └── logstash/
│       └── config/
│
├── kubernetes/                     # Kubernetes deployments
│   ├── deployments/
│   ├── services/
│   └── helm-charts/
│
├── scripts/                        # Utility scripts
│   └── create-service.sh
│
├── pom.xml                         # Parent POM
├── README.md                       # Project documentation
├── HLD.md                          # High-Level Design
├── SETUP.md                        # Setup guide
├── CONTRIBUTING.md                 # Contribution guidelines
├── LICENSE                         # License file
└── .gitignore                      # Git ignore rules
```

## Service Ports

| Service | Port |
|---------|------|
| API Gateway | 8080 |
| User Service | 8081 |
| Profile Service | 8082 |
| Post Service | 8083 |
| Connection Service | 8084 |
| Feed Service | 8085 |
| Recommendation Service | 8086 |
| Job Service | 8087 |
| Notification Service | 8088 |
| Search Service | 8089 |
| Analytics Service | 8090 |

## Standard Service Structure

Each microservice follows this structure:

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/linkedme/service/
│   │   │       ├── ServiceApplication.java
│   │   │       ├── controller/        # REST controllers
│   │   │       ├── service/           # Business logic
│   │   │       ├── repository/        # Data access
│   │   │       ├── model/             # Entities/DTOs
│   │   │       ├── config/            # Configuration classes
│   │   │       └── exception/         # Exception handlers
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   └── test/
│       └── java/
│           └── com/linkedin/platform/service/
└── pom.xml
```
