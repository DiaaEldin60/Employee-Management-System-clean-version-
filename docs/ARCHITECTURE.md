# Architecture Documentation - Employee Management System

## Overview

This document describes the architecture of the Employee Management System (EMS) using the **C4 Model** (Context, Containers, Components, Code). The C4 Model is a lean approach to visualizing software architecture at different levels of abstraction.

## Table of Contents

- [C4 Model Overview](#c4-model-overview)
- [Level 1: System Context](#level-1-system-context)
- [Level 2: Containers](#level-2-containers)
- [Level 3: Components](#level-3-components)
- [Level 4: Code (Implementation Details)](#level-4-code-implementation-details)
- [Architecture Decisions](#architecture-decisions)
- [Security Architecture](#security-architecture)
- [Data Flow](#data-flow)
- [Viewing the Diagrams](#viewing-the-diagrams)

---

## C4 Model Overview

The C4 Model consists of four levels of abstraction:

1. **Context** (Level 1) - Shows the system as a box in the center, surrounded by its users and other systems it interacts with
2. **Containers** (Level 2) - Zooms into the system boundary to show the high-level technology choices and how responsibilities are distributed
3. **Components** (Level 3) - Zooms into an individual container to show the components inside it
4. **Code** (Level 4) - Optional level showing how components are implemented (classes, interfaces)

---

## Level 1: System Context

### Purpose
Shows the EMS as a black box and its interactions with users and external systems.

### Diagram
**File**: `diagrams/c4-context-diagram.puml`

### Scope
- **System**: Employee Management System (EMS)
- **Users**:
  - **Administrator**: Manages employees, users, and system configuration
  - **Employee**: Views own profile, submits leave requests
  - **Manager**: Manages team, approves leave requests, views reports
- **External Systems**:
  - **Email Service (Brevo API)**: Sends verification emails, password resets, notifications
  - **Monitoring & Observability (Prometheus + Grafana)**: Metrics collection, dashboards, alerting

### Key Interactions

| Actor | Interaction | Protocol |
|-------|-------------|----------|
| Administrator | Manages employees and users | HTTPS |
| Employee | Views profile and requests leave | HTTPS |
| Manager | Manages team and approves requests | HTTPS |
| EMS → Email Service | Sends emails | HTTPS |
| Monitoring → EMS | Scrapes metrics | HTTP/8081 |

### Business Context

The EMS serves as a central hub for workforce management, connecting:
- HR administrators who manage the system
- Employees who interact with their own data
- Managers who oversee team operations
- External services for communication and monitoring

---

## Level 2: Containers

### Purpose
Shows the high-level technology choices and how the EMS is divided into deployable/executable units.

### Diagram
**File**: `diagrams/c4-container-diagram.puml`

### Container Overview

| Container | Technology | Responsibility |
|-----------|------------|----------------|
| **Web Application** | Thymeleaf + HTML/CSS/JS | Server-side rendered UI for browser users |
| **API Application** | Spring Boot 3.x, Java 21 | RESTful API endpoints |
| **Security Layer** | Spring Security | Authentication & Authorization |
| **Cache Layer** | Ehcache 3.x | Application caching for performance |
| **Database** | MySQL 8.x | Persistent storage for all data |
| **Actuator** | Spring Boot Actuator | Health checks, metrics on port 8081 |

### Container Details

#### Web Application (Thymeleaf)
- **Technology**: Thymeleaf templating engine with HTML/CSS/JavaScript
- **Port**: 8080
- **Purpose**: Provides server-side rendered web interface
- **Users**: All roles (Admin, Manager, Employee)

#### REST API Application
- **Technology**: Spring Boot 3.x, Java 21
- **Port**: 8080 (shared with web)
- **Protocols**: HTTPS, JSON
- **Purpose**: Provides RESTful endpoints for all operations
- **Architecture**: Layered (Controller → Service → Repository)

#### Security Layer
- **Technology**: Spring Security 6.x
- **Features**:
  - Session-based authentication
  - Role-based access control (RBAC)
  - Method-level security with `@PreAuthorize`
  - CSRF protection
  - Security headers (HSTS, X-Frame-Options)

#### Cache Layer
- **Technology**: Ehcache 3.x
- **Cache Regions**:
  - `users` - User data caching
  - `employees` - Employee data caching
  - `paginated-employees` - Paginated query results
  - `roles` - Role definitions
- **Purpose**: Reduce database load and improve response times

#### Database
- **Technology**: MySQL 8.0
- **Connection Pool**: HikariCP
- **ORM**: Hibernate 6.x (via Spring Data JPA)
- **Schema**: 10+ tables with proper indexing

#### Actuator
- **Technology**: Spring Boot Actuator
- **Port**: 8081 (separate from application)
- **Features**:
  - Health checks
  - Prometheus metrics endpoint
  - Application info
  - Environment details
- **Security**: Basic authentication (ADMIN only)

### External Systems

| System | Technology | Purpose |
|--------|------------|---------|
| Email Service | Brevo API | Send transactional emails |
| Prometheus | Prometheus | Metrics collection |
| Grafana | Grafana | Visualization & dashboards |

### Inter-Container Communication

```
Browser ──HTTPS──▶ Web Application ──REST/JSON──▶ API Application
                                                        │
                        ┌───────────────────────────────┼───────────────┐
                        ▼                               ▼               ▼
                  Security Layer                  Cache Layer      Database
                        │                               │               │
                        └───────────────────────────────┴───────────────┘
                                                        │
                                                        ▼
                                               Actuator (Port 8081)
                                                        │
                                                        ▼
                                                Prometheus/Grafana
```

---

## Level 3: Components

### Purpose
Decomposes the API Application container into its constituent components.

### Diagram
**File**: `diagrams/c4-component-diagram.puml`

### Component Layers

#### 1. Controller Layer (Spring MVC)

| Component | Responsibility | Security |
|-----------|----------------|----------|
| `AuthController` | Login, registration endpoints | Rate limited |
| `UserController` | User CRUD operations | ADMIN only |
| `EmployeeController` | Employee management | Role-based |
| `LeaveController` | Leave request management | Role-based |
| `DashboardController` | Dashboard statistics | All authenticated |
| `PasswordResetController` | Password reset flow | Rate limited |
| `EmailVerificationController` | Email verification | Rate limited |

**Key Responsibilities**:
- Handle HTTP requests and responses
- Input validation
- Call appropriate services
- Return proper HTTP status codes

#### 2. Service Layer (Business Logic)

| Component | Responsibility | Caching |
|-----------|----------------|---------|
| `UserService` | User management, roles, password handling | `@Cacheable`, `@CacheEvict` |
| `EmployeeService` | Employee CRUD, CSV import/export | `@Cacheable`, `@CacheEvict` |
| `LeaveService` | Leave requests, balance management | Configurable |
| `AuthenticationService` | Authentication logic | User details caching |
| `EmailService` | Email sending | None |
| `PasswordResetService` | Password reset workflow | Token storage |
| `EmailVerificationService` | Email verification workflow | Token storage |

**Key Responsibilities**:
- Business logic implementation
- Transaction management (`@Transactional`)
- Caching operations
- Integration with repositories
- CSV processing

#### 3. Security Layer

| Component | Type | Responsibility |
|-----------|------|----------------|
| `SecurityConfig` | `@Configuration` | Filter chains, basic auth for actuator |
| `CustomUserDetailsService` | `UserDetailsService` | User loading with caching |
| `CustomAccessDeniedHandler` | `AccessDeniedHandler` | 403 error handling |
| `RateLimitAspect` | AspectJ | Rate limiting annotation processing |

**Security Flow**:
```
Request → SecurityFilterChain → Authentication → Authorization → Controller
```

#### 4. Data Access Layer (JPA Repositories)

| Component | Extends | Purpose |
|-----------|---------|---------|
| `UserRepository` | `JpaRepository` | User data access, custom queries |
| `EmployeeRepository` | `JpaRepository` | Employee data access |
| `RoleRepository` | `JpaRepository` | Role definitions |
| `AuthorityRepository` | `JpaRepository` | Authority/permission definitions |
| `LeaveRequestRepository` | `JpaRepository` | Leave request queries |
| `LeaveBalanceRepository` | `JpaRepository` | Leave balance management |

**Features**:
- Spring Data JPA derived queries
- Custom `@Query` methods
- Pagination support

#### 5. Infrastructure Layer

| Component | Type | Responsibility |
|-----------|------|----------------|
| `CacheConfig` | `@Configuration` | Ehcache configuration |
| `MailConfig` | `@Configuration` | JavaMailSender configuration |
| `DataInitializer` | `ApplicationRunner` | Initial data seeding on startup |
| `GlobalExceptionHandler` | `@ControllerAdvice` | Global exception handling |

#### 6. Mapping Layer (MapStruct)

| Component | Technology | Purpose |
|-----------|------------|---------|
| `UserMapper` | MapStruct | User entity ↔ DTO conversion |
| `EmployeeMapper` | MapStruct | Employee entity ↔ DTO conversion |

**Features**:
- Compile-time code generation
- Singleton pattern (`INSTANCE`)
- Update methods for partial updates

### Component Interactions

```
┌─────────────────────────────────────────────────────────────┐
│                       Controller Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐  │
│  │   AuthCtrl  │ │  UserCtrl   │ │      EmployeeCtrl       │  │
│  └──────┬──────┘ └──────┬──────┘ └───────────┬─────────────┘  │
└─────────┼───────────────┼─────────────────────┼───────────────┘
          │               │                     │
          ▼               ▼                     ▼
┌─────────────────────────────────────────────────────────────┐
│                        Service Layer                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────────┐  │
│  │  AuthSvc    │ │   UserSvc   │ │       EmployeeSvc       │  │
│  └─────────────┘ └──────┬──────┘ └───────────┬─────────────┘  │
│                         │                     │               │
│                    ┌────┴────┐           ┌────┴────┐          │
│                    │UserRepo │           │EmpRepo  │          │
│                    └────┬────┘           └────┬────┘          │
└─────────────────────────┼─────────────────────┼─────────────┘
                          │                     │
                          ▼                     ▼
                   ┌───────────────────────────────────┐
                   │          Database (MySQL)           │
                   └───────────────────────────────────┘
```

---

## Level 4: Code (Implementation Details)

### Purpose
Shows how individual components are implemented at the code level.

### Key Design Patterns

#### 1. Layered Architecture
```
Controller Layer (REST API)
    ↓
Service Layer (Business Logic + Caching)
    ↓
Repository Layer (Data Access)
    ↓
Database (MySQL)
```

#### 2. Dependency Injection
- **Constructor Injection**: Used throughout for mandatory dependencies
- **Spring IoC Container**: Manages bean lifecycle

#### 3. Repository Pattern
```java
public interface EmployeeRepository extends JpaRepository<Employees, Integer> {
    // Derived query methods
    List<Employees> findByDepartment(String department);
    
    // Custom queries
    @Query("SELECT e FROM Employees e WHERE e.salary > :minSalary")
    List<Employees> findHighEarners(@Param("minSalary") Float minSalary);
}
```

#### 4. DTO Pattern with MapStruct
```java
@Mapper
public interface EmployeeMapper {
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);
    
    EmployeeDto toDto(Employees employee);
    Employees toEntity(EmployeeDto dto);
    
    @MappingTarget
    void updateEntityFromDto(EmployeeDto dto, @MappingTarget Employees entity);
}
```

#### 5. Aspect-Oriented Programming (AOP)

**Rate Limiting Aspect**:
```java
@Aspect
@Component
public class RateLimitAspect {
    @Around("@annotation(rateLimited)")
    public Object around(ProceedingJoinPoint point, RateLimited rateLimited) {
        // Rate limiting logic
    }
}
```

### Transaction Boundaries

- **Read Operations**: `@Transactional(readOnly = true)`
- **Write Operations**: `@Transactional`
- **Cache Eviction**: `@CacheEvict` on writes
- **Cache Population**: `@Cacheable` on reads

---

## Architecture Decisions

### ADR 1: Session-Based Authentication vs JWT
**Decision**: Use session-based authentication  
**Rationale**: 
- Simpler revocation (just delete session)
- Better fit for server-side rendered UI
- No token refresh complexity

### ADR 2: Ehcache vs Redis
**Decision**: Use Ehcache  
**Rationale**:
- Single instance deployment (no distributed cache needed)
- Simpler configuration
- No additional infrastructure required

### ADR 3: Separate Actuator Port
**Decision**: Run actuator on port 8081  
**Rationale**:
- Security isolation from main application
- Different authentication mechanism (Basic Auth)
- Easier firewall rules

### ADR 4: MapStruct vs Manual Mapping
**Decision**: Use MapStruct  
**Rationale**:
- Compile-time type safety
- No runtime reflection overhead
- Less boilerplate code

### ADR 5: Thymeleaf vs React/Vue
**Decision**: Use Thymeleaf  
**Rationale**:
- Simpler deployment (single artifact)
- No separate frontend build
- Server-side rendering for SEO

---

## Security Architecture

### Authentication Flow
```
1. User submits credentials (username/password)
2. AuthenticationFilter intercepts request
3. CustomUserDetailsService loads user details
4. BCryptPasswordEncoder verifies password
5. SecurityContextHolder stores authentication
6. User receives JSESSIONID cookie
```

### Authorization Flow
```
1. Request reaches Controller
2. @PreAuthorize annotation checked
3. SecurityExpressionHandler evaluates roles
4. Access granted or denied
5. If denied, CustomAccessDeniedHandler processes
```

### Security Layers

| Layer | Mechanism | Purpose |
|-------|-----------|---------|
| Network | HTTPS | Transport encryption |
| Application | Spring Security | Authentication & authorization |
| Method | @PreAuthorize | Fine-grained access control |
| Data | BCrypt | Password hashing |
| Transport | Rate Limiting | DDoS/brute force protection |

---

## Data Flow

### Employee Creation Flow
```
1. Admin submits employee data via Web UI
2. EmployeeController receives request
3. EmployeeService.createEmployeeWithUser() called
4. UserService creates user with temporary password
5. Employee record created with user reference
6. Cache evicted for "employees" region
7. EmailService sends welcome email
8. Response returned with 201 Created
```

### Leave Request Flow
```
1. Employee submits leave request
2. LeaveController validates request
3. LeaveService checks leave balance
4. If sufficient balance, request saved as PENDING
5. Manager receives notification
6. Manager approves/rejects via LeaveController
7. If approved, leave balance updated
8. Employee notified via email
```

---

## Viewing the Diagrams

### Option 1: PlantUML Online Server
1. Visit [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/)
2. Copy content from `.puml` files in `docs/diagrams/`
3. View rendered diagram

### Option 2: VS Code
1. Install "PlantUML" extension
2. Open any `.puml` file
3. Press `Alt+D` to preview

### Option 3: IntelliJ IDEA
1. Install "PlantUML integration" plugin
2. Open any `.puml` file
3. Diagram renders automatically

### Option 4: PlantUML CLI
```bash
# Install PlantUML (requires Java)
java -jar plantuml.jar docs/diagrams/c4-context-diagram.puml

# Or use Docker
docker run -v $(pwd):/workspace plantuml/plantuml /workspace/docs/diagrams/c4-context-diagram.puml
```

---

## Related Documentation

- [API Documentation](api/API-README.md) - REST API reference
- [Diagrams README](diagrams/README.md) - All architecture diagrams
- [Javadoc Guide](JAVADOC-GUIDE.md) - Code documentation
- [Main README](../README.md) - Project overview

---

<div align="center">

**[⬆ Back to Top](#architecture-documentation---employee-management-system)**

Built with ❤️ by the EMS Development Team

</div>
