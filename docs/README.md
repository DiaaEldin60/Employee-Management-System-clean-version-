# Employee Management System - Documentation

This directory contains comprehensive documentation for the Employee Management System (EMS).

## 📚 Documentation Structure

```
docs/
├── README.md                    # This file
├── ARCHITECTURE.md             # C4 Architecture Documentation
├── JAVADOC-GUIDE.md            # Javadoc documentation guide
├── api/                         # API Documentation
│   ├── API-README.md           # Complete API reference guide
│   ├── openapi-spec.yaml       # OpenAPI 3.0 specification
│   └── postman-collection.json # Postman collection for testing
└── diagrams/                    # Architecture Diagrams
    ├── README.md               # Diagrams documentation
    ├── uml-class-diagram.puml          # UML Class Diagram
    ├── c4-context-diagram.puml         # C4 Context (Level 1)
    ├── c4-container-diagram.puml       # C4 Container (Level 2)
    ├── c4-component-diagram.puml       # C4 Component (Level 3)
    ├── sequence-create-employee.puml   # Sequence Diagram
    ├── entity-relationship-diagram.puml # Database ERD
    └── deployment-diagram.puml          # Deployment Architecture
```

## 🌐 API Documentation

### Quick Start

1. **API Reference Guide** (`api/API-README.md`)
   - Complete endpoint documentation
   - Request/response examples
   - Authentication guide
   - Error codes and handling

2. **OpenAPI Specification** (`api/openapi-spec.yaml`)
   - Import into Swagger UI
   - Generate client SDKs
   - View at: `http://localhost:8080/swagger-ui.html`

3. **Postman Collection** (`api/postman-collection.json`)
   - Import into Postman
   - Pre-configured requests
   - Environment variables included

### Base URL

**Production:** `https://employee-management-system-production-2a1a.up.railway.app/api/v1`

**Local Development:** `http://localhost:8080/api/v1`

### Default Credentials
- **Username:** `admin`
- **Password:** `change_me`
- **Role:** ADMIN

### API Endpoints Overview

| Category | Base Path | Endpoints |
|----------|-----------|-----------|
| Authentication | `/auth` | Login, Register |
| Employee Management | `/employees` | CRUD, CSV Import/Export |
| User Management | `/users` | CRUD, Role Management |
| Leave Management | `/leave` | Requests, Approvals, Balance |
| Password Reset | `/password-reset` | Reset Flow |
| Email Verification | `/email-verification` | Verify Email |

## 🏗️ Architecture Documentation

### C4 Model Architecture

The system architecture is documented using the **C4 Model** approach, which provides four levels of abstraction:

| Level | Diagram | Purpose | File |
|-------|---------|---------|------|
| **Level 1** | Context | System as a black box with users and external systems | `c4-context-diagram.puml` |
| **Level 2** | Container | Applications, databases, and their interactions | `c4-container-diagram.puml` |
| **Level 3** | Component | Internal components within the API application | `c4-component-diagram.puml` |
| **Level 4** | Code | Implementation details (classes, interfaces) | Javadoc + Source Code |

📖 **[Complete Architecture Guide →](ARCHITECTURE.md)**

The architecture documentation includes:
- System Context and business overview
- Container architecture and technology choices
- Component breakdown with responsibilities
- Architecture Decision Records (ADRs)
- Security architecture explanation
- Data flow diagrams

### Additional Architecture Diagrams

- **UML Class Diagram** - Object-oriented class structure
- **Entity Relationship Diagram** - Database schema
- **Sequence Diagram** - Employee creation workflow
- **Deployment Diagram** - Docker containers and infrastructure

## 📖 Javadoc (Code Documentation)

The project includes comprehensive **Javadoc** comments for:

- **Main Application** - Entry point with feature overview
- **Service Layer** - Business logic documentation with caching notes
- **Entity/Model Layer** - Database entity descriptions
- **Controllers** - REST API endpoint documentation
- **Exception Handlers** - Error handling documentation

### Quick Commands

```bash
# Generate Javadoc HTML
mvn javadoc:javadoc

# View generated documentation
open target/site/apidocs/index.html        # Mac
start target/site/apidocs/index.html       # Windows
xdg-open target/site/apidocs/index.html    # Linux
```

### IDE Integration

- **IntelliJ IDEA**: `Ctrl+Q` for quick docs, `Ctrl+P` for parameter info
- **VS Code**: Hover for documentation
- **Eclipse**: `F2` for focusable tooltip

See [JAVADOC-GUIDE.md](JAVADOC-GUIDE.md) for complete guide on Javadoc tags, best practices, and generation options.

### Deployment Architecture

Shows the physical deployment architecture with Docker containers, port mappings, and network connections.

## 🚀 Quick Reference

### Viewing Diagrams

#### Option 1: PlantUML Online Server
1. Visit [PlantUML Server](http://www.plantuml.com/plantuml/)
2. Copy content from any `.puml` file
3. Paste and view the rendered diagram

#### Option 2: VS Code
1. Install "PlantUML" extension
2. Open any `.puml` file
3. Press `Alt+D` (Windows/Linux) or `Option+D` (Mac)

#### Option 3: IntelliJ IDEA
1. Install "PlantUML integration" plugin
2. Open any `.puml` file
3. Diagram renders automatically

### Testing API

#### Using Swagger UI
When the application is running:
```
http://localhost:8080/swagger-ui.html
```

#### Using Postman
1. Import `api/postman-collection.json`
2. Set environment variable `base_url` to `https://employee-management-system-production-2a1a.up.railway.app/api/v1` (or `http://localhost:8080/api/v1` for local testing)
3. Run requests

#### Using curl
```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"change_me"}'

# Get employees
curl -X GET http://localhost:8080/api/v1/employees \
  -H "Cookie: JSESSIONID=your_session_id"
```

## 📊 System Architecture

### Technology Stack
- **Backend:** Spring Boot 3.x, Java 21
- **Frontend:** Thymeleaf, HTML5, CSS3, JavaScript
- **Database:** MySQL 8.x with JPA/Hibernate
- **Caching:** Ehcache 3.x
- **Security:** Spring Security (RBAC)
- **Monitoring:** Spring Boot Actuator, Prometheus, Grafana
- **Documentation:** OpenAPI 3.0, Swagger UI

### Key Features
- Employee CRUD operations
- User management with role-based access
- Leave request workflow
- Email verification and password reset
- CSV import/export
- Rate limiting and caching
- Comprehensive monitoring

### Security
- Session-based authentication
- Role-based access control (ADMIN, MANAGER, EMPLOYEE)
- BCrypt password hashing
- CSRF protection
- Rate limiting on sensitive endpoints
- Security headers (HSTS, X-Frame-Options, etc.)

## 📝 Additional Resources

### For Developers
- OpenAPI spec for client generation
- Postman collection for manual testing
- UML diagrams for understanding domain model
- C4 diagrams for architectural decisions

### For DevOps
- Deployment diagram for infrastructure setup
- Monitoring configuration with Prometheus/Grafana
- Docker container relationships

### For Business Analysts
- Context diagram for system boundaries
- Sequence diagram for key workflows
- API documentation for integration planning

## 🔄 Maintenance

When making changes to the system:

1. **API Changes:** Update `api/openapi-spec.yaml` and `api/API-README.md`
2. **Database Changes:** Update `entity-relationship-diagram.puml`
3. **Architecture Changes:** Update `ARCHITECTURE.md` and relevant C4 diagrams
4. **New Features:** Add to Postman collection
5. **Code Changes:** Update Javadoc comments for new classes/methods

## 📞 Support

For questions or issues with the documentation, please contact the development team.

---

## Quick Links

- [Architecture Guide](ARCHITECTURE.md) - C4 Model documentation
- [API Documentation](api/API-README.md) - REST API reference
- [OpenAPI Spec](api/openapi-spec.yaml) - OpenAPI specification
- [Diagrams README](diagrams/README.md) - All architecture diagrams
- [Javadoc Guide](JAVADOC-GUIDE.md) - Code documentation guide
- [PlantUML Guide](https://plantuml.com/guide) - PlantUML syntax
- [C4 Model Guide](https://c4model.com/) - C4 Model methodology
- [OpenAPI Specification](https://spec.openapis.org/oas/v3.0.3)
- [Javadoc Tool Guide](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html)
