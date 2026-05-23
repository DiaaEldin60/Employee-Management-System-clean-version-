# Javadoc Guide - Employee Management System

## Overview

This project includes comprehensive **Javadoc** documentation for all major classes and methods. Javadoc is a documentation tool that generates API documentation in HTML format from Java source code comments.

## What is Javadoc?

Javadoc is the standard documentation generator for Java. It parses special `/** ... */` comments and generates professional HTML documentation.

### Key Benefits
- **IDE Integration** - Hover for docs in IntelliJ, Eclipse, VS Code
- **API Contracts** - Documents how to use classes and methods
- **Maintainability** - Code and documentation stay in sync
- **Industry Standard** - Expected in professional Java development

## Project Documentation Coverage

The following classes have comprehensive Javadoc:

### Main Application
- ✅ `EmployeeManagementSystemApplication` - Entry point with feature overview

### Service Layer
- ✅ `EmployeeService` - Employee CRUD, CSV operations, caching
- ✅ `UserService` - User management, roles, password handling

### Entity/Model Layer
- ✅ `Employees` - Employee entity with database constraints
- ✅ `Users` - User entity with security features

### Exception Handling
- ✅ `GlobalExceptionHandler` - Centralized exception handling

### Controllers
- ✅ `EmployeeController` - REST endpoints with security docs

## How to Generate Javadoc

### Option 1: Maven Plugin

```bash
# Generate Javadoc
mvn javadoc:javadoc

# View generated docs
open target/site/apidocs/index.html
```

### Option 2: IntelliJ IDEA

1. **Tools > Generate Javadoc...**
2. Select the scope (usually whole project)
3. Set output directory: `target/site/apidocs`
4. Click **OK**

### Option 3: Command Line (JDK Tool)

```bash
# Basic generation
javadoc -d docs/api -sourcepath src/main/java -subpackages com.example

# With custom title
javadoc -d docs/api \
  -doctitle "Employee Management System API" \
  -windowtitle "EMS API" \
  -author \
  -version \
  -sourcepath src/main/java \
  com.example.employee_management_system
```

## Javadoc Tags Reference

| Tag | Purpose | Example |
|-----|---------|---------|
| `@author` | Author name | `@author John Doe` |
| `@version` | Version number | `@version 1.0.0` |
| `@since` | When added | `@since 2024-01-15` |
| `@param` | Parameter description | `@param id the employee ID` |
| `@return` | Return value | `@return the employee entity` |
| `@throws` | Exception thrown | `@throws ResourceNotFoundException if not found` |
| `@see` | Reference to other code | `@see EmployeeService` |
| `@link` | Inline link | `{@link EmployeeService}` |
| `@deprecated` | Mark as deprecated | `@deprecated use newMethod() instead` |

## Writing Good Javadoc

### Class-Level Documentation

```java
/**
 * Short description of the class.
 * 
 * <p>Detailed description with HTML formatting.</p>
 * 
 * <p>Feature list:</p>
 * <ul>
 *   <li>Feature 1</li>
 *   <li>Feature 2</li>
 * </ul>
 * 
 * @author Your Name
 * @version 1.0.0
 * @since 2024-01-15
 * @see RelatedClass
 */
public class MyClass {
```

### Method-Level Documentation

```java
/**
 * Brief description of what the method does.
 * 
 * <p>More detailed explanation if needed.</p>
 * 
 * @param param1 description of first parameter
 * @param param2 description of second parameter
 * @return description of return value
 * @throws SomeException when this exception occurs
 * @see RelatedMethod
 */
public ReturnType myMethod(ParamType1 param1, ParamType2 param2) {
```

### Field Documentation

```java
/**
 * The employee's unique identifier in the database.
 */
private int id;
```

## Best Practices

### 1. Document Public APIs
Always document:
- Public classes
- Public methods
- Protected methods in abstract classes

### 2. Be Concise but Complete
```java
// Good
/**
 * Retrieves an employee by their unique identifier.
 * 
 * @param id the unique identifier of the employee
 * @return the employee entity
 * @throws ResourceNotFoundException if employee not found
 */

// Bad - too vague
/**
 * Get employee.
 */
```

### 3. Use HTML for Formatting
```java
/**
 * <p>First paragraph</p>
 * <p>Second paragraph</p>
 * <ul>
 *   <li>Item 1</li>
 *   <li>Item 2</li>
 * </ul>
 * <code>inline code</code>
 * <pre>
 *   code block
 * </pre>
 */
```

### 4. Cross-Reference Related Classes
```java
/**
 * @see com.example.service.EmployeeService
 * @see com.example.model.Employees
 * @see org.springframework.stereotype.Service
 */
```

## Viewing Javadoc in IDE

### IntelliJ IDEA
- **Quick Documentation**: `Ctrl+Q` (Windows/Linux) or `Ctrl+J` (Mac)
- **Parameter Info**: `Ctrl+P`
- **External Documentation**: `Shift+F1`

### VS Code
- **Hover** over any class/method name
- Install "Java Extension Pack" for full support

### Eclipse
- **Hover** for quick view
- **F2** for focusable tooltip
- **Shift+F2** for external browser

## Continuous Integration

Add to your CI/CD pipeline:

```yaml
# GitHub Actions example
- name: Generate Javadoc
  run: mvn javadoc:javadoc

- name: Deploy to GitHub Pages
  uses: peaceiris/actions-gh-pages@v3
  with:
    github_token: ${{ secrets.GITHUB_TOKEN }}
    publish_dir: ./target/site/apidocs
```

## Additional Resources

- [Official Javadoc Guide](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html)
- [How to Write Doc Comments](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)
- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-index.html)

## Maintenance

When adding new classes/methods:

1. ✅ Add class-level Javadoc explaining purpose
2. ✅ Add method-level Javadoc for public methods
3. ✅ Document all parameters with `@param`
4. ✅ Document return values with `@return`
5. ✅ Document exceptions with `@throws`
6. ✅ Update `@version` if making breaking changes

## Example from This Project

```java
/**
 * Service class for managing employee operations.
 * 
 * <p>This service provides comprehensive CRUD operations for employees, including:
 * <ul>
 *   <li>Creating, reading, updating, and deleting employee records</li>
 *   <li>Creating employees with associated user accounts</li>
 *   <li>CSV import and export functionality</li>
 * </ul>
 * </p>
 * 
 * <p>The service uses Spring's caching abstraction with Ehcache to improve performance.</p>
 * 
 * @author EMS Development Team
 * @version 1.0.0
 * @since 2024-01-15
 * @see org.springframework.stereotype.Service
 */
@Service
public class EmployeeService {
```

---

## Quick Commands

```bash
# Generate Javadoc
mvn javadoc:javadoc

# Generate with custom options
mvn javadoc:javadoc -Dshow=protected

# View the documentation
open target/site/apidocs/index.html        # Mac
start target/site/apidocs/index.html       # Windows
xdg-open target/site/apidocs/index.html    # Linux
```
