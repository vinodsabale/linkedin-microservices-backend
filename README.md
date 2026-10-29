# LinkedIn Microservices Backend

A LinkedIn-style professional networking backend built with **Java 17 and Spring Boot microservices**. It supports user registration/login, profiles, connections, posts, likes, comments, a personalized feed, search, and event-driven notifications.

## Architecture

```
                    ┌───────────────┐
   Client ────────► │  API Gateway  │ :8080  (JWT validation)
                    └───────┬───────┘
        ┌──────────┬────────┼─────────┬──────────┐
        ▼          ▼        ▼         ▼          
   User Service  Post    Feed      Search     Notification
      :8081      :8082   :8083      :8084         :8085
        │          │       │          │             ▲
      MySQL      MySQL   Redis   Elasticsearch      │
        └──────────┴───────┴──── Kafka ──────────────┘
```

## Services

| Service | Port | Responsibility | Storage |
|---|---|---|---|
| api-gateway | 8080 | Routing and JWT authentication filter | Redis |
| user-service | 8081 | Auth, profiles, profile photo (S3), connections | MySQL, AWS S3 |
| post-service | 8082 | Posts, likes, comments, media upload | MySQL, AWS S3 |
| feed-service | 8083 | Personalized feed built from Kafka events | Redis |
| search-service | 8084 | Search for people, posts and skills | Elasticsearch |
| notification-service | 8085 | Consumes Kafka events to send notifications | – |

## Tech Stack

- **Language/Framework:** Java 17, Spring Boot, Spring Data JPA, Spring Cloud Gateway
- **Security:** JWT (access + refresh tokens), BCrypt password hashing
- **Messaging:** Apache Kafka
- **Databases:** MySQL 8, Redis, Elasticsearch 8
- **Cloud:** AWS S3 (profile photos and post media)
- **Tools:** Maven, Docker Compose, Lombok, JUnit

## Key Features

- User registration and login with JWT authentication
- Profile management and profile photo upload to S3
- Send, accept and reject connection requests
- Create posts, like and comment
- Feed generated asynchronously through Kafka events and cached in Redis
- Full-text search across users, posts and skills using Elasticsearch
- Event-driven notifications (`user.created`, `connection.requested`, `connection.accepted`, `post.liked`, `post.commented`)
- Centralized JWT validation at the API Gateway

## Kafka Topics

`user.created` · `user.updated` · `post.created` · `connection.requested` · `connection.accepted` · `post.liked` · `post.commented`

## API Endpoints (via Gateway `http://localhost:8080`)

**Auth**
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

**Users**
- `GET /api/v1/users/{userId}`
- `PUT /api/v1/users/{userId}/profile`
- `POST /api/v1/users/{userId}/profile-photo`
- `POST /api/v1/users/{targetUserId}/connect`
- `PUT /api/v1/users/connection/{connectionId}/accept`
- `PUT /api/v1/users/connection/{connectionId}/reject`
- `GET /api/v1/users/{userId}/connections`
- `GET /api/v1/users/{userId}/pending-requests`

**Posts**
- `POST /api/v1/posts`
- `GET /api/v1/posts/{postId}`
- `GET /api/v1/posts/user/{userId}`
- `DELETE /api/v1/posts/{postId}`
- `POST /api/v1/posts/{postId}/like`
- `POST /api/v1/posts/{postId}/comments`
- `GET /api/v1/posts/{postId}/comments`

**Feed**
- `GET /api/v1/feed/{userId}`
- `DELETE /api/v1/feed/{userId}/cache`

**Search**
- `GET /api/v1/search/people`
- `GET /api/v1/search/posts`
- `GET /api/v1/search/skills`

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker and Docker Compose
- An AWS account with an S3 bucket (for image uploads)

### 1. Start infrastructure
```bash
docker-compose up -d
```
This starts MySQL, Redis, Kafka, Zookeeper and Elasticsearch.

### 2. Configure environment
Set these values in `user-service` and `post-service` (`application.yaml`) or as environment variables. **Never commit real credentials.**

```yaml
jwt:
  secret: <your-jwt-secret>      # user-service and api-gateway (must match)
aws:
  access-key: <your-access-key>
  secret-key: <your-secret-key>
  region: <your-region>
  s3:
    bucket-name: <your-bucket>
spring:
  datasource:
    password: <your-mysql-password>
```

### 3. Run the services
Start each service in this order:
```bash
cd user-service/user-service && ./mvnw spring-boot:run
cd post-service/post-service && ./mvnw spring-boot:run
cd feed-service/feed-service && ./mvnw spring-boot:run
cd search-service/search-service && ./mvnw spring-boot:run
cd notification-service/notification-service && ./mvnw spring-boot:run
cd api-gateway/api-gateway && ./mvnw spring-boot:run
```

### 4. Test
Register and log in through the gateway, then pass the token:
```
Authorization: Bearer <token>
```

## Project Structure

```
linkedin-microservices-backend/
├── api-gateway/
├── user-service/
├── post-service/
├── feed-service/
├── search-service/
├── notification-service/
├── docker-compose.yml
└── README.md
```

## Future Improvements

- Service discovery (Eureka) and centralized config
- Dockerfiles for every service and full containerized deployment
- Frontend (React) client
- Real email/push delivery in notification service
- Unit and integration test coverage with Mockito and Testcontainers
