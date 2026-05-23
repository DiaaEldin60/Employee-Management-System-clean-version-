# Employee Management System (EMS)

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green?logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![License](https://img.shields.io/badge/License-MIT-yellow)

**A production-ready employee management system with role-based access control, caching, monitoring, and comprehensive API documentation.**

[Features](#-features) • [Quick Start](#-quick-start) • [Documentation](#-documentation) • [API Reference](#-api-reference)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Security](#-security)
- [Monitoring](#-monitoring)
- [Documentation](#-documentation)
- [Contributing](#-contributing)

---

## 🎯 Overview

The **Employee Management System** is a comprehensive Spring Boot application designed for organizations to efficiently manage their workforce. It provides a secure, scalable, and feature-rich platform with role-based access control, caching for optimal performance, and extensive monitoring capabilities.

### Key Highlights

- ✅ **Secure Authentication** - Session-based with BCrypt password hashing
- ✅ **Role-Based Access Control** - ADMIN, MANAGER, and EMPLOYEE roles
- ✅ **High Performance** - Ehcache integration for reduced database load
- ✅ **Comprehensive Monitoring** - Prometheus metrics and health checks
- ✅ **RESTful API** - OpenAPI 3.0 specification with Swagger UI
- ✅ **Data Import/Export** - CSV support for bulk operations

---

## ✨ Features

### Employee Management
- Complete CRUD operations for employee records
- CSV import and export functionality
- Advanced filtering and pagination
- Partial updates via PATCH
- Audit trail with creation/modification timestamps

### User Management
- Secure user registration and authentication
- Temporary password generation for new accounts
- Email verification workflow
- Password reset functionality
- Role and authority assignment
- CSV bulk user import

### Leave Management
- Leave request submission and tracking
- Automated leave balance calculation
- Manager approval/rejection workflow
- Support for annual, sick, and emergency leave

### Security Features
- **Authentication**: Session-based with Spring Security
- **Authorization**: Method-level security with `@PreAuthorize`
- **Password Security**: BCrypt hashing with configurable strength
- **Rate Limiting**: Custom annotation-based rate limiting
- **CSRF Protection**: Enabled for web endpoints
- **Security Headers**: HSTS, X-Frame-Options, X-Content-Type-Options

### Performance & Monitoring
- **Caching**: Ehcache 3.x with multiple cache regions
- **Metrics**: Spring Boot Actuator with Prometheus endpoint (public access via Docker network)
- **Health Checks**: Database and application health indicators
- **Management Port**: Internal port (8081) for actuator endpoints (Docker network only)
- **Grafana Dashboard**: Pre-configured dashboards for monitoring
- **Brevo API**: REST API for reliable email delivery

---

## 🛠 Technology Stack

### Core Framework
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Programming language |
| Spring Boot | 3.2.x | Application framework |
| Spring Security | 6.x | Authentication & authorization |
| Spring Data JPA | 3.x | Data persistence |
| Hibernate | 6.x | ORM framework |

### Database & Caching
| Technology | Version | Purpose |
|------------|---------|---------|
| MySQL | 8.0 | Primary database |
| Ehcache | 3.9.7 | Application caching |
| HikariCP | - | Connection pooling |

### Additional Libraries
| Technology | Purpose |
|------------|---------|
| Thymeleaf | Server-side templating |
| MapStruct | DTO/Entity mapping |
| Brevo API | Email services via REST API |
| OpenAPI/Swagger | API documentation |
| Prometheus | Metrics collection |
| Grafana | Monitoring dashboards |

### Development Tools
| Tool | Purpose |
|------|---------|
| Maven | Build automation |
| JUnit 5 | Unit testing |
| Mockito | Mocking framework |
| Docker | Containerization |

---

## 🏗 Architecture

### C4 Model

The system follows the **C4 Model** for architecture documentation:

1. **Context** - System interactions with users and external services
2. **Container** - Web app, REST API, database, and cache
3. **Component** - Controllers, services, repositories breakdown
4. **Code** - Class-level implementation details

### Design Patterns

- **Layered Architecture**: Controller → Service → Repository
- **Repository Pattern**: Spring Data JPA repositories
- **DTO Pattern**: MapStruct for entity/DTO conversion
- **Aspect-Oriented Programming**: Rate limiting via custom aspects

### Security Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Web Client    │────▶│  Spring Security│────▶│   Controllers   │
│   (Browser)     │     │  Filter Chain   │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                                          │
                         ┌─────────────────┐              ▼
                         │  Actuator Port  │◀────┌─────────────────┐
                         │    :8081        │     │    Services     │
                         └─────────────────┘     └─────────────────┘
                               │                           │
                               ▼                           ▼
                         ┌─────────────────┐     ┌─────────────────┐
                         │  Basic Auth     │     │   Repositories  │
                         │  (ADMIN only)   │     └─────────────────┘
                         └─────────────────┘              │
                                                          ▼
                                                   ┌─────────────────┐
                                                   │     MySQL       │
                                                   └─────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- MySQL 8.0
- Maven 3.8+
- (Optional) Docker & Docker Compose

### Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/employee-management-system.git
cd employee-management-system
```

#### 2. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE EMS_Project CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/EMS_Project
spring.datasource.username=your_username
spring.datasource.password=your_password
```

#### 3. Configure Environment Variables

Create a `.env` file in the project root:

```bash
# Required: Brevo REST API Configuration
BREVO_API_KEY=your_brevo_api_key
BREVO_FROM_EMAIL=your-email@example.com

# Optional: Database credentials (defaults provided)
DB_USERNAME=diaa_60
DB_PASSWORD=change_me
DB_NAME=EMS_Project

# Optional: App port (default: 8080)
APP_PORT=8080
```

Get your Brevo API key from [brevo.com](https://app.brevo.com/settings/keys/api)

#### 4. Build and Run

**Option A: Local Development**
```bash
# Build the application
mvn clean package

# Run the application
mvn spring-boot:run
```

**Option B: Docker Compose (Full Stack - Recommended)**
```bash
# Start all services (MySQL, App, Prometheus, Grafana)
docker-compose up -d

# View logs
docker-compose logs -f app

# Rebuild after code changes
docker-compose up -d --build app
```

#### 5. Access the Application

**Production:** https://employee-management-system-production-2a1a.up.railway.app

**Local Development:**

| Service | URL | Notes |
|---------|-----|-------|
| Web Application | http://localhost:8080 | Main application |
| Swagger UI | http://localhost:8080/swagger-ui.html | API documentation |
| Grafana | http://localhost:3000 | Monitoring dashboards (admin/admin) |
| Prometheus | http://localhost:9090 | Metrics (if external port enabled) |

### Default Credentials

```
Username: admin
Password: change_me
Role: ADMIN
```

### Docker Services

| Service | Internal Port | External Port | Purpose |
|---------|--------------|---------------|---------|
| MySQL | 3306 | 3306 | Database |
| EMS App | 8080, 8081 | 8080 | Web app (8080), Actuator (8081 internal) |
| Prometheus | 9090 | - | Metrics collection (internal-only) |
| Grafana | 3000 | 3000 | Monitoring dashboards |

### Docker Commands

```bash
# Start all services
docker-compose up -d

# Stop all services
docker-compose down

# View app logs
docker-compose logs -f app

# Rebuild app only
docker-compose up -d --build app

# Full reset (includes database)
docker-compose down -v
docker-compose up -d
```

---

## 📚 API Documentation

### OpenAPI Specification

The API is fully documented with OpenAPI 3.0:

- **Swagger UI (Production)**: `https://employee-management-system-production-2a1a.up.railway.app/swagger-ui.html`
- **Swagger UI (Local)**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `https://employee-management-system-production-2a1a.up.railway.app/v3/api-docs`

### API Endpoints Overview

| Category | Base Path | Description |
|----------|-----------|-------------|
| Authentication | `/api/v1/auth` | Login, registration |
| Employees | `/api/v1/employees` | CRUD, CSV operations |
| Users | `/api/v1/users` | User management |
| Leave | `/api/v1/leave` | Leave requests |
| Password Reset | `/api/v1/password-reset` | Reset flow |
| Email Verification | `/api/v1/email-verification` | Email verification |

### Example Request

```bash
# Production API Base URL
BASE_URL=https://employee-management-system-production-2a1a.up.railway.app/api/v1
# Or for local development: BASE_URL=http://localhost:8080/api/v1

# Login
curl -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"change_me"}'

# Get employees
curl -X GET $BASE_URL/employees \
  -H "Cookie: JSESSIONID=your_session_id"

# Create employee
curl -X POST $BASE_URL/employees \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=your_session_id" \
  -d '{
    "employee": {
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "department": "IT",
      "jobTitle": "Developer",
      "salary": 75000.00
    },
    "username": "john.doe",
    "temporaryPassword": "change_me",
    "roles": ["EMPLOYEE"]
  }'
```

### Postman Collection

Import the collection from `docs/api/postman-collection.json` for ready-to-use requests.

---

## 🗄 Database Schema

### Entity Relationships

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   Users     │◀────▶│  Employees  │       │    Roles    │
├─────────────┤   1:1 ├─────────────┤       ├─────────────┤
│ id (PK)     │       │ id (PK)     │       │ id (PK)     │
│ username    │       │ first_name  │       │ role        │
│ password    │       │ last_name   │       └─────────────┘
│ enabled     │       │ email       │            │
│ ...         │       │ ...         │            │
└─────────────┘       └─────────────┘            ▼
      │                                   ┌─────────────┐
      │                                   │ Authorities │
      ▼                                   ├─────────────┤
┌─────────────┐                         │ id (PK)     │
│ user_roles  │◀───────────────────────▶│ name        │
├─────────────┤       role_authorities  └─────────────┘
│ user_id(FK) │
│ role_id(FK) │
└─────────────┘
```

### Tables

- **users** - User accounts and authentication
- **employees** - Employee personal and employment data
- **roles** - Role definitions (ADMIN, MANAGER, EMPLOYEE)
- **authorities** - Fine-grained permissions
- **user_roles** - Many-to-many relationship
- **role_authorities** - Role-permission mapping
- **leave_requests** - Leave request records
- **leave_balances** - Employee leave balances
- **email_verification_tokens** - Email verification tokens
- **password_reset_tokens** - Password reset tokens

---

## 🔒 Security

### Authentication

- Session-based authentication with Spring Security
- Custom `CustomUserDetailsService` for user loading
- Caching of user details for performance

### Authorization

Role hierarchy:
```
ADMIN > MANAGER > EMPLOYEE
```

Method-level security with `@PreAuthorize`:
```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
```

### Rate Limiting

Custom `@RateLimited` annotation protects sensitive endpoints:
- Login: 5 attempts per minute
- Password reset: 3 requests per hour
- Registration: 3 requests per hour

### Actuator Security

Actuator endpoints on internal port (8081), accessible only within Docker network:

| Endpoint | Access | Purpose |
|----------|--------|---------|
| `/actuator/health` | ✅ Public | Health checks for load balancers |
| `/actuator/info` | ✅ Public | Application info |
| `/actuator/prometheus` | ✅ Public (internal) | Prometheus metrics scraping |
| `/actuator/metrics/**` | ✅ Public (internal) | Detailed metrics access |
| Other `/actuator/**` | 🔒 Requires ADMIN | Admin operations (Basic Auth) |

> **Security Note**: Port 8081 is not exposed externally. Only accessible within Docker network.

---

## 📊 Monitoring

### Prometheus Metrics

Metrics are scraped from `http://app:8081/actuator/prometheus` (internal Docker network).

Available metrics:
- JVM memory usage
- Garbage collection statistics
- HTTP request counts and durations (`http_server_requests_seconds_*`)
- Database connection pool status
- Custom business metrics
- Application status (`up` metric for health monitoring)

### Health Checks

Health endpoint at `/actuator/health` (publicly accessible within Docker network).

Checks include:
- Database connectivity
- Disk space
- Application status (UP/DOWN)

View in Grafana: **Application Status** panel shows real-time health.

### Grafana Dashboard

Pre-configured dashboards available at `http://localhost:3000`:
- **Application Overview**: Health, uptime, version, request rates
- **System Resources**: CPU, memory, threads, GC statistics
- **JVM Metrics**: Heap usage, non-heap, classes loaded
- **HTTP Requests**: Request rate, duration (p95/p99), errors
- **Business Metrics**: Employee counts by department

Data source: Prometheus (`http://prometheus:9090`)

> **Note**: Grafana is pre-configured with Prometheus data source and EMS dashboard.

---

## 📖 Documentation

Comprehensive documentation is available in the `docs/` directory:

| Document | Description |
|----------|-------------|
| [API README](docs/api/API-README.md) | Complete API reference |
| [OpenAPI Spec](docs/api/openapi-spec.yaml) | OpenAPI 3.0 specification |
| [Postman Collection](docs/api/postman-collection.json) | Test requests |
| [Javadoc Guide](docs/JAVADOC-GUIDE.md) | Code documentation guide |
| [Diagrams README](docs/diagrams/README.md) | Architecture diagrams |

### UML & C4 Diagrams

Visual documentation in `docs/diagrams/`:
- UML Class Diagram
- C4 Context, Container, Component Diagrams
- Entity Relationship Diagram
- Sequence Diagrams
- Deployment Diagram

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run only unit tests
mvn test -Dtest=*ServiceTest

# Run with coverage
mvn jacoco:report
```

### Test Structure

```
src/test/
├── java/
│   └── com/example/
│       ├── service/        # Service unit tests
│       ├── repository/     # Repository tests
│       └── integration/    # Integration tests
└── resources/              # Test resources
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Code Style

- Follow Google Java Style Guide
- Write comprehensive Javadoc for public APIs
- Maintain test coverage above 80%
- Use meaningful variable and method names

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - Application framework
- [Spring Security](https://spring.io/projects/spring-security) - Security framework
- [Ehcache](https://www.ehcache.org/) - Caching solution
- [Prometheus](https://prometheus.io/) - Monitoring and alerting

---

## 📞 Support

For support, email support@example.com or join our Slack channel.

---

<div align="center">

**[⬆ Back to Top](#employee-management-system-ems)**

Built with ❤️ by the EMS Development Team

</div>
