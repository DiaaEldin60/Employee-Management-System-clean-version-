# Employee Management System - API Documentation

## Overview

The Employee Management System provides a RESTful API for managing employees, users, leave requests, and authentication. The API follows REST conventions and uses JSON for request and response bodies.

## Base URL

### Production
```
https://employee-management-system-production-2a1a.up.railway.app/api/v1
```

### Local Development
```
http://localhost:8080/api/v1
```

## Authentication

The API uses **Session-based authentication** with Spring Security. Most endpoints require authentication via login.

### Default Admin Credentials
- Username: `admin`
- Password: `change_me`

### Roles
- **ADMIN** - Full access to all endpoints
- **MANAGER** - Can manage employees, approve leave requests
- **EMPLOYEE** - Can view own profile and submit leave requests

### Rate Limiting
Some endpoints have rate limiting to prevent abuse:
- Authentication endpoints: 5 requests per minute
- Password reset: 3 requests per hour
- Email verification: 5 requests per hour

## Response Format

All responses follow a consistent structure:

### Success Response
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 200,
  "message": "Success message",
  "data": { ... }
}
```

### Error Response
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/v1/employees"
}
```

## API Endpoints

### Table of Contents

1. [Authentication API](#authentication-api)
2. [Employee API](#employee-api)
3. [User API](#user-api)
4. [Leave Management API](#leave-management-api)
5. [Password Reset API](#password-reset-api)
6. [Email Verification API](#email-verification-api)

---

## Authentication API

### Base Path: `/api/v1/auth`

#### 1. Register User

**Endpoint:** `POST /api/v1/auth/register`

Register a new user with an existing employee ID.

**Request Body:**
```json
{
  "username": "john.doe",
  "password": "change_me"
  "employeeId": 1
}
```

**Response (200 OK):**
```json
{
  "message": "User registered with default EMPLOYEE role",
  "success": true
}
```

**Rate Limit:** 3 requests per hour

**Error Responses:**
- `400 Bad Request` - Invalid input data
- `409 Conflict` - Username already exists

---

#### 2. Login

**Endpoint:** `POST /api/v1/auth/login`

Authenticate a user and create a session.

**Request Body:**
```json
{
  "username": "admin",
  "email": "admin@example.com",
  "password": "change_me"
}
```

**Response (200 OK):**
```json
{
  "message": "Login successful",
  "success": true
}
```

**Rate Limit:** 5 requests per minute

**Error Responses:**
- `401 Unauthorized` - Invalid credentials

---

## Employee API

### Base Path: `/api/v1/employees`

#### 1. Get All Employees

**Endpoint:** `GET /api/v1/employees`

Retrieve paginated list of employees with optional filtering.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | int | No | 0 | Page number (0-indexed) |
| size | int | No | 10 | Page size (max 20) |
| sortBy | string | No | "id" | Sort field |
| firstName | string | No | - | Filter by first name |
| lastName | string | No | - | Filter by last name |
| email | string | No | - | Filter by email |
| department | string | No | - | Filter by department |
| jobTitle | string | No | - | Filter by job title |
| minSalary | float | No | - | Minimum salary |
| maxSalary | float | No | - | Maximum salary |

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phoneNumber": "1234567890",
      "hireDate": "2023-01-15",
      "jobTitle": "Software Engineer",
      "salary": 75000.00,
      "department": "IT"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 50,
  "totalPages": 5
}
```

**Roles Required:** ADMIN, MANAGER, or EMPLOYEE

---

#### 2. Create Employee

**Endpoint:** `POST /api/v1/employees`

Create a new employee with user credentials.

**Request Body:**
```json
{
  "employee": {
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "phoneNumber": "0987654321",
    "hireDate": "2024-01-15",
    "jobTitle": "Project Manager",
    "salary": 85000.00,
    "department": "Management"
  },
  "username": "jane.smith",
  "temporaryPassword": "tempPass123",
  "roles": ["MANAGER"],
  "authorities": ["EMPLOYEE_READ", "EMPLOYEE_WRITE"]
}
```

**Response (201 Created):**
```json
{
  "id": 2,
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com",
  "phoneNumber": "0987654321",
  "hireDate": "2024-01-15",
  "jobTitle": "Project Manager",
  "salary": 85000.00,
  "department": "Management"
}
```

**Location Header:** `/api/v1/employees/2`

**Roles Required:** ADMIN only

**Error Responses:**
- `400 Bad Request` - Invalid input data
- `403 Forbidden` - Insufficient permissions

---

#### 3. Get Employee by ID

**Endpoint:** `GET /api/v1/employees/{id}`

Retrieve a specific employee by ID.

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | int | Yes | Employee ID |

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "hireDate": "2023-01-15",
  "jobTitle": "Software Engineer",
  "salary": 75000.00,
  "department": "IT"
}
```

**Error Responses:**
- `404 Not Found` - Employee not found

**Roles Required:** ADMIN, MANAGER, or EMPLOYEE

---

#### 4. Update Employee (Full)

**Endpoint:** `PUT /api/v1/employees/{id}`

Completely update an employee record.

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | int | Yes | Employee ID |

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "hireDate": "2023-01-15",
  "jobTitle": "Senior Software Engineer",
  "salary": 90000.00,
  "department": "IT"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "jobTitle": "Senior Software Engineer",
  "salary": 90000.00
}
```

**Roles Required:** ADMIN or MANAGER

---

#### 5. Update Employee (Partial)

**Endpoint:** `PATCH /api/v1/employees/{id}`

Partially update an employee record.

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | int | Yes | Employee ID |

**Request Body:**
```json
{
  "salary": 95000.00,
  "jobTitle": "Lead Software Engineer"
}
```

**Response (200 OK):**
Same as PUT response with updated fields.

**Roles Required:** ADMIN or MANAGER

---

#### 6. Delete Employee

**Endpoint:** `DELETE /api/v1/employees/{id}`

Delete an employee record.

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | int | Yes | Employee ID |

**Response (204 No Content)**

**Roles Required:** ADMIN only

---

#### 7. Upload Employees from CSV

**Endpoint:** `POST /api/v1/employees/upload`

Bulk upload employees from a CSV file.

**Request:**
- Content-Type: `multipart/form-data`
- Parameter: `file` (CSV file)

**CSV Format:**
```
firstName,lastName,email,department,jobTitle,phoneNumber,salary,hireDate
John,Doe,john@example.com,IT,Developer,1234567890,50000,2023-01-15
```

**Response (200 OK):**
```json
{
  "message": "Employees uploaded successfully",
  "count": 2,
  "employees": [ ... ]
}
```

**Roles Required:** ADMIN only

---

#### 8. Validate Employees CSV

**Endpoint:** `POST /api/v1/employees/validate`

Validate a CSV file without importing.

**Request:**
- Content-Type: `multipart/form-data`
- Parameter: `file` (CSV file)

**Response (200 OK):**
```json
{
  "message": "CSV validated successfully",
  "count": 2,
  "employees": [
    {
      "lineNumber": 1,
      "firstName": "John",
      "valid": true
    },
    {
      "lineNumber": 2,
      "firstName": "Jane",
      "valid": false,
      "error": "Invalid salary format"
    }
  ]
}
```

**Roles Required:** ADMIN only

---

#### 9. Export Employees to CSV

**Endpoint:** `GET /api/v1/employees/export`

Export all employees to a CSV file.

**Response (200 OK):**
- Content-Type: `text/csv`
- Content-Disposition: `attachment; filename=employees.csv`

**CSV Output:**
```
firstName,lastName,email,department,jobTitle,phoneNumber,salary,hireDate
John,Doe,john@example.com,IT,Developer,1234567890,50000.0,2023-01-15
```

**Roles Required:** ADMIN, MANAGER, or EMPLOYEE

---

## User API

### Base Path: `/api/v1/users`

#### 1. Create User with Temporary Password

**Endpoint:** `POST /api/v1/users/create-with-temp-password`

Create a new user account for an employee with a temporary password.

**Request Body:**
```json
{
  "employeeId": 1,
  "username": "john.doe",
  "temporaryPassword": "change_me",
  "roles": ["EMPLOYEE"],
  "authorities": ["EMPLOYEE_READ"]
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "userName": "john.doe",
  "enabled": true,
  "temporaryPassword": true,
  "roles": ["EMPLOYEE"]
}
```

**Roles Required:** ADMIN only

---

#### 2. Get All Users

**Endpoint:** `GET /api/v1/users`

Retrieve paginated list of users.

**Query Parameters:**
| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| page | int | No | 0 | Page number |
| size | int | No | 10 | Page size (max 20) |
| sortBy | string | No | "id" | Sort field |

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "userName": "admin",
      "enabled": true,
      "roles": ["ADMIN"]
    }
  ],
  "totalElements": 10,
  "totalPages": 1
}
```

**Roles Required:** ADMIN only

---

#### 3. Get User by ID

**Endpoint:** `GET /api/v1/users/{id}`

Retrieve a specific user by ID.

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | int | Yes | User ID |

**Response (200 OK):**
```json
{
  "id": 1,
  "userName": "john.doe",
  "enabled": true,
  "roles": ["EMPLOYEE"],
  "emailVerified": false
}
```

**Roles Required:** ADMIN only

---

#### 4. Update User

**Endpoint:** `PUT /api/v1/users/{id}`

Update user information.

**Request Body:**
```json
{
  "userName": "john.doe.updated",
  "enabled": true
}
```

**Roles Required:** ADMIN only

---

#### 5. Delete User

**Endpoint:** `DELETE /api/v1/users/{id}`

Delete a user account.

**Response (204 No Content)**

**Roles Required:** ADMIN only

---

#### 6. Update User Roles

**Endpoint:** `PUT /api/v1/users/{id}/roles`

Update roles assigned to a user.

**Request Body:**
```json
{
  "roleNames": ["MANAGER", "EMPLOYEE"]
}
```

**Roles Required:** ADMIN only

---

#### 7. Upload Users from CSV

**Endpoint:** `POST /api/v1/users/upload`

Bulk upload users from CSV.

**Request:**
- Content-Type: `multipart/form-data`
- Parameter: `file` (CSV file)

**CSV Format:**
```
username,password,enabled,isTemporaryPassword,isEmailVerified
john.doe,encodedPass,true,true,false
```

**Roles Required:** ADMIN only

---

#### 8. Export Users to CSV

**Endpoint:** `GET /api/v1/users/export`

Export all users to CSV.

**Response (200 OK):**
- Content-Type: `text/csv`
- Content-Disposition: `attachment; filename=users.csv`

**Roles Required:** ADMIN only

---

## Leave Management API

### Base Path: `/api/v1/leave`

#### 1. Get Leave Balance

**Endpoint:** `GET /api/v1/leave/balance/{employeeId}`

Get leave balance for an employee.

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| employeeId | int | Yes | Employee ID |

**Response (200 OK):**
```json
{
  "id": 1,
  "annualLeave": 20,
  "emergencyLeave": 5,
  "sickLeave": 10
}
```

**Roles Required:** ADMIN, MANAGER, or EMPLOYEE (own record only)

---

#### 2. Create Leave Request

**Endpoint:** `POST /api/v1/leave/request`

Submit a new leave request.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| employeeId | int | Yes | Employee ID |
| leaveType | enum | Yes | ANNUAL, SICK, or EMERGENCY |
| startDate | date | Yes | Start date (ISO format) |
| endDate | date | Yes | End date (ISO format) |
| reason | string | No | Reason for leave |

**Response (200 OK):**
```json
{
  "id": 1,
  "employeeId": 1,
  "leaveType": "ANNUAL",
  "startDate": "2024-06-01",
  "endDate": "2024-06-05",
  "daysCount": 5,
  "reason": "Vacation",
  "status": "PENDING"
}
```

**Error Responses:**
- `400 Bad Request` - Insufficient leave balance

---

#### 3. Get Leave Requests by Employee

**Endpoint:** `GET /api/v1/leave/requests/{employeeId}`

Get all leave requests for an employee.

**Roles Required:** ADMIN, MANAGER, or EMPLOYEE (own record only)

---

#### 4. Get Pending Leave Requests

**Endpoint:** `GET /api/v1/leave/requests/pending`

Get all pending leave requests awaiting approval.

**Roles Required:** ADMIN or MANAGER

---

#### 5. Get All Leave Requests

**Endpoint:** `GET /api/v1/leave/requests`

Get all leave requests in the system.

**Roles Required:** ADMIN only

---

#### 6. Approve Leave Request

**Endpoint:** `POST /api/v1/leave/requests/{requestId}/approve`

Approve a pending leave request.

**Path Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| requestId | int | Yes | Leave request ID |

**Response (200 OK):**
```json
{
  "id": 1,
  "status": "APPROVED",
  "approvedBy": "manager",
  "approvedAt": "2024-05-15T10:30:00"
}
```

**Roles Required:** ADMIN or MANAGER

**Note:** Manager leave requests can only be approved by ADMIN.

---

#### 7. Reject Leave Request

**Endpoint:** `POST /api/v1/leave/requests/{requestId}/reject`

Reject a pending leave request.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| rejectionReason | string | No | Reason for rejection |

**Response (200 OK):**
```json
{
  "id": 1,
  "status": "REJECTED",
  "approvedBy": "manager",
  "approvedAt": "2024-05-15T10:30:00",
  "rejectionReason": "Insufficient leave balance"
}
```

**Roles Required:** ADMIN or MANAGER

---

## Password Reset API

### Base Path: `/api/v1/password-reset`

#### 1. Request Password Reset

**Endpoint:** `POST /api/v1/password-reset/request`

Request a password reset email.

**Request Body:**
```json
{
  "email": "john.doe@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset email sent successfully",
  "success": true
}
```

**Rate Limit:** 3 requests per hour

---

#### 2. Validate Reset Code

**Endpoint:** `POST /api/v1/password-reset/validate`

Validate a password reset code.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| code | string | Yes | Reset code from email |

**Response (200 OK):**
```json
{
  "message": "Reset code is valid",
  "success": true
}
```

**Rate Limit:** 10 requests per minute

---

#### 3. Confirm Password Reset

**Endpoint:** `POST /api/v1/password-reset/confirm`

Reset password using the code.

**Request Body:**
```json
{
  "code": "abc123xyz",
  "newPassword": "NewSecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset successfully",
  "success": true
}
```

**Rate Limit:** 5 requests per minute

---

## Email Verification API

### Base Path: `/api/v1/email-verification`

#### 1. Send Verification Email

**Endpoint:** `POST /api/v1/email-verification/send`

Send email verification link to user.

**Request Body:**
```json
{
  "username": "john.doe"
}
```

**Rate Limit:** 5 requests per hour

---

#### 2. Send Verification Email

**Endpoint:** `POST /api/v1/email-verification/resend`

Resend email verification link.

**Rate Limit:** 3 requests per hour

---

#### 3. Verify Email

**Endpoint:** `POST /api/v1/email-verification/verify`

Verify email using token from email.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| code | string | Yes | Verification code |

**Response (200 OK):**
```json
{
  "message": "Email verified successfully",
  "success": true
}
```

**Rate Limit:** 10 requests per minute

---

#### 4. Check Verification Status

**Endpoint:** `GET /api/v1/email-verification/status`

Check if user email is verified.

**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| username | string | Yes | Username to check |

**Response (200 OK):**
```json
{
  "message": "Email is verified",
  "success": true
}
```

**Rate Limit:** 20 requests per minute

---

## Swagger/OpenAPI Documentation

The API documentation is also available via Swagger UI when the application is running:

```
http://localhost:8080/swagger-ui.html
```

Or in JSON format:

```
http://localhost:8080/v3/api-docs
```

## HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 OK | Request succeeded |
| 201 Created | Resource created successfully |
| 204 No Content | Request succeeded, no content returned |
| 400 Bad Request | Invalid request parameters |
| 401 Unauthorized | Authentication required |
| 403 Forbidden | Insufficient permissions |
| 404 Not Found | Resource not found |
| 409 Conflict | Resource conflict (e.g., duplicate) |
| 429 Too Many Requests | Rate limit exceeded |
| 500 Internal Server Error | Server error |

## Data Types

### Enums

**LeaveType:**
- `ANNUAL` - Annual/vacation leave
- `SICK` - Sick leave
- `EMERGENCY` - Emergency leave

**LeaveStatus:**
- `PENDING` - Awaiting approval
- `APPROVED` - Approved by manager/admin
- `REJECTED` - Rejected by manager/admin

---

## Support

For API support or questions, please contact the development team.
