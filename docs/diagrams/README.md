# Employee Management System - Architecture Diagrams

This directory contains various architecture and design diagrams for the Employee Management System.

## Diagram Types

### UML Diagrams

1. **uml-class-diagram.puml** - Complete class diagram showing:
   - Entity classes (Users, Employees, Roles, etc.)
   - Service layer (UserService, EmployeeService, etc.)
   - Repository layer (JPA Repositories)
   - Controller layer (REST Controllers)
   - Relationships between all components

2. **sequence-create-employee.puml** - Sequence diagram showing:
   - Flow of creating an employee with user account
   - Database interactions
   - Caching operations
   - Email notifications

3. **entity-relationship-diagram.puml** - Database ERD showing:
   - All database tables
   - Primary and foreign keys
   - Indexes and constraints
   - Entity relationships (1:1, 1:N, M:N)

### C4 Model Diagrams

The C4 model provides a hierarchical view of the system architecture:

1. **c4-context-diagram.puml** (Level 1 - System Context)
   - Shows the system as a black box
   - Interactions with users and external systems
   - High-level view for non-technical stakeholders

2. **c4-container-diagram.puml** (Level 2 - Containers)
   - Shows the applications and data stores
   - Web application, REST API, Database, Cache
   - Technology choices for each container

3. **c4-component-diagram.puml** (Level 3 - Components)
   - Breaks down the API into components
   - Controllers, Services, Repositories
   - Security layer, Infrastructure components

### Deployment Diagram

4. **deployment-diagram.puml**
   - Physical deployment architecture
   - Docker containers and their interactions
   - Port mappings and network connections
   - External service integrations

## How to View These Diagrams

### Option 1: PlantUML Online Server
Visit [PlantUML Online Server](http://www.plantuml.com/plantuml/) and paste the content of any `.puml` file.

### Option 2: VS Code Extension
Install the "PlantUML" extension in VS Code:
1. Open the `.puml` file
2. Press `Alt+D` (Windows/Linux) or `Option+D` (Mac) to preview

### Option 3: PlantUML CLI
```bash
# Install PlantUML (requires Java)
java -jar plantuml.jar uml-class-diagram.puml

# Or use Docker
docker run -v $(pwd):/workspace plantuml/plantuml /workspace/uml-class-diagram.puml
```

### Option 4: IntelliJ IDEA Plugin
Install the "PlantUML integration" plugin from JetBrains marketplace.

## Architecture Overview

### Technology Stack
- **Backend**: Spring Boot 3.x, Java 21
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Database**: MySQL 8.x with JPA/Hibernate
- **Caching**: Ehcache 3.x
- **Security**: Spring Security with Role-Based Access Control
- **Monitoring**: Spring Boot Actuator, Prometheus, Grafana
- **Email**: JavaMailSender with Gmail SMTP

### Key Architectural Patterns

1. **Layered Architecture**
   - Controller Layer (REST API endpoints)
   - Service Layer (Business logic with caching)
   - Repository Layer (Data access with JPA)
   - Model Layer (Entities and DTOs)

2. **Security Architecture**
   - Form-based authentication for web
   - HTTP Basic auth for actuator endpoints
   - Method-level security with @PreAuthorize
   - Role-based access control (RBAC)

3. **Caching Strategy**
   - Ehcache for application-level caching
   - @Cacheable on read operations
   - @CacheEvict on write operations
   - Separate cache regions for different entities

4. **Database Design**
   - One-to-One: Users ↔ Employees
   - One-to-Many: Employees → LeaveRequests
   - Many-to-Many: Users ↔ Roles ↔ Authorities

## System Features

### Core Features
- Employee management (CRUD operations)
- User management with role-based access
- Leave request management with approval workflow
- Email verification and password reset
- CSV import/export for employees

### Security Features
- BCrypt password hashing
- Session management with concurrent session control
- Rate limiting on authentication endpoints
- CSRF protection for web forms
- Security headers (HSTS, X-Frame-Options, etc.)

### Monitoring & Observability
- Prometheus metrics endpoint (/actuator/prometheus)
- Health checks and application info
- Custom business metrics
- Grafana dashboards for visualization

## Viewing the Diagrams

To render all diagrams at once:

```bash
cd docs/diagrams

# Using PlantUML CLI
for file in *.puml; do
    plantuml "$file"
done

# This will generate PNG/SVG files
```

## Maintaining Diagrams

When making changes to the codebase:

1. Update UML class diagram when adding/modifying entities
2. Update ERD when changing database schema
3. Update C4 diagrams when architectural changes occur
4. Update sequence diagrams when workflow changes

## Diagram Legend

### Colors Used
- **Blue (#E8F4FD)**: Entity/Model classes
- **Green (#F0F9E8)**: Service layer
- **Orange (#FEF5E7)**: Controller layer
- **Purple (#F5EEF8)**: Repository layer

### Relationships
- Solid line: Direct dependency/association
- Dashed line: Indirect/implement relationship
- Arrowhead: Direction of dependency

## Additional Resources

- [C4 Model Documentation](https://c4model.com/)
- [PlantUML Guide](https://plantuml.com/guide)
- [Spring Boot Architecture](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
