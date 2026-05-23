# Documentation Generation Guide

## Quick Start

### 1. API Documentation (Recommended)
```bash
# Start the application
mvn spring-boot:run

# Access interactive API docs
# Open: http://localhost:8080/swagger-ui.html
```

### 2. JavaDoc Generation
```bash
# Generate with warnings suppressed
mvn javadoc:javadoc -Ddoclint=none

# View generated docs
# Open: target/site/apidocs/index.html
```

### 3. Project Documentation
```bash
# Generate project site
mvn site

# View site
# Open: target/site/index.html
```

### 4. PDF Documentation Generation
```bash
# Generate JavaDoc first
mvn javadoc:javadoc -Ddoclint=none

# Convert HTML to PDF (multiple options)
# Option 1: Using wkhtmltopdf (recommended)
wkhtmltopdf target/site/apidocs/index.html documentation.pdf

# Option 2: Using pandoc
pandoc target/site/apidocs/overview-summary.html -o documentation.pdf

# Option 3: Using headless Chrome
google-chrome --headless --disable-gpu --print-to-pdf=documentation.pdf \
  target/site/apidocs/index.html
```

## Documentation Types

### A. API Documentation
- **Format**: OpenAPI 3.0 (Swagger)
- **Access**: `http://localhost:8080/swagger-ui.html`
- **Features**: Interactive testing, schema validation
- **Best for**: API consumers, developers

### B. JavaDoc Documentation
- **Format**: HTML JavaDoc
- **Access**: `target/site/apidocs/index.html`
- **Features**: Class/method documentation, cross-references
- **Best for**: Code understanding, IDE integration

### C. Project Documentation
- **Format**: Maven Site
- **Access**: `target/site/index.html`
- **Features**: Reports, dependencies, plugins
- **Best for**: Project overview, build information

### D. PDF Documentation
- **Format**: Portable Document Format
- **Generation**: Multiple tools available
- **Features**: Offline access, printable, shareable
- **Best for**: Distribution, documentation packages

## PDF Generation Tools & Installation

### 1. wkhtmltopdf (Recommended)
**Installation:**
```bash
# Windows (using Chocolatey)
choco install wkhtmltopdf

# Windows (manual download)
# Download from: https://wkhtmltopdf.org/downloads.html

# Ubuntu/Debian
sudo apt-get install wkhtmltopdf

# macOS (using Homebrew)
brew install wkhtmltopdf
```

### 2. Pandoc
**Installation:**
```bash
# Windows (using Chocolatey)
choco install pandoc

# Windows (manual download)
# Download from: https://pandoc.org/installing.html

# Ubuntu/Debian
sudo apt-get install pandoc

# macOS (using Homebrew)
brew install pandoc
```

### 3. Google Chrome (Headless)
**Already installed on most systems**

## Advanced PDF Generation

### Complete PDF Generation Script
```bash
#!/bin/bash
# generate-pdf.sh

echo "Generating complete documentation PDF..."

# Step 1: Generate JavaDoc
mvn javadoc:javadoc -Ddoclint=none

# Step 2: Generate API docs JSON
mvn spring-boot:run &
sleep 30
curl -o api-docs.json http://localhost:8080/v3/api-docs
pkill -f "spring-boot:run"

# Step 3: Convert to PDF using wkhtmltopdf
wkhtmltopdf --page-size A4 --margin-top 1cm --margin-right 1cm \
  --margin-bottom 1cm --margin-left 1cm --encoding UTF-8 \
  --title "Employee Management System Documentation" \
  target/site/apidocs/index.html employee-management-system-docs.pdf

echo "PDF generated: employee-management-system-docs.pdf"
```

### Maven Plugin for PDF Generation
Add to `pom.xml`:
```xml
<plugin>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>2.2.4</version>
    <configuration>
        <sourceDirectory>src/main/asciidoc</sourceDirectory>
        <outputDirectory>${project.build.directory}/docs</outputDirectory>
        <attributes>
            <pdf-fontsdir>fonts</pdf-fontsdir>
            <pdf-stylesdir>themes</pdf-stylesdir>
            <pdf-style>default</pdf-style>
        </attributes>
    </configuration>
    <executions>
        <execution>
            <id>generate-pdf-doc</id>
            <phase>generate-resources</phase>
            <goals>
                <goal>process-asciidoc</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Automated Documentation Generation

### Maven Configuration
Add to `pom.xml`:
```xml
<reporting>
    <plugins>
        <!-- JavaDoc -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-javadoc-plugin</artifactId>
            <version>3.5.0</version>
            <configuration>
                <doclint>none</doclint>
            </configuration>
        </plugin>
        
        <!-- Project Site -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-site-plugin</artifactId>
            <version>4.0.0-M4</version>
        </plugin>
        
        <!-- OpenAPI -->
        <plugin>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-maven-plugin</artifactId>
            <version>2.2.0</version>
        </plugin>
    </plugins>
</reporting>
```

### Generate All Documentation
```bash
# Generate complete documentation site
mvn clean site site:stage

# Deploy to GitHub Pages (optional)
mvn site:deploy -Dgithub.global.server=github
```

## Documentation Best Practices

### 1. Code Comments
```java
/**
 * Service for managing employee operations.
 * 
 * <p>Provides CRUD operations with caching and transaction support.</p>
 * 
 * @author Diaa Eldin
 * @version 1.0.0
 * @since 2025-12-02
 * 
 * @see EmployeeRepository
 * @see Cacheable
 */
@Service
public class EmployeeService {
    
    /**
     * Creates a new employee.
     * 
     * @param employee the employee to create
     * @return the created employee
     * @throws IllegalArgumentException if employee data is invalid
     * @throws DataIntegrityViolationException if email already exists
     */
    public Employees createEmployee(Employees employee) {
        // implementation
    }
}
```

### 2. API Documentation
```java
@Operation(summary = "Create new employee", description = "Adds a new employee to the system")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Employee created successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid input data"),
    @ApiResponse(responseCode = "409", description = "Employee with email already exists")
})
@PostMapping
public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
    // implementation
}
```

### 3. README Structure
```markdown
# Project Name

## Quick Start
## Features
## API Documentation
## Setup
## Usage
## Contributing
## License
```

## Tools & Resources

### Documentation Tools
- **JavaDoc**: Built-in Java documentation
- **Swagger/OpenAPI**: API documentation standard
- **Maven Site**: Project documentation
- **AsciiDoctor**: Advanced documentation generation

### Automation Scripts
```bash
#!/bin/bash
# docs-generator.sh

echo "Generating documentation..."

# Generate JavaDoc
mvn javadoc:javadoc -Ddoclint=none

# Generate API docs
curl -o docs/api.json http://localhost:8080/v3/api-docs

# Generate HTML from API docs
docker run --rm -v $(pwd)/docs:/docs swaggerapi/swagger-codegen \
    generate -i /docs/api.json -l html2 -o /docs/api/

echo "Documentation generated in docs/ directory"
```

## Continuous Integration

### GitHub Actions
```yaml
name: Generate Documentation

on:
  push:
    branches: [ main ]

jobs:
  docs:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Generate JavaDoc
      run: mvn javadoc:javadoc -Ddoclint=none
    
    - name: Generate API docs
      run: |
        mvn spring-boot:run &
        sleep 30
        curl -o docs/api.json http://localhost:8080/v3/api-docs
        pkill -f "spring-boot:run"
    
    - name: Deploy to GitHub Pages
      uses: peaceiris/actions-gh-pages@v3
      with:
        github_token: ${{ secrets.GITHUB_TOKEN }}
        publish_dir: ./target/site/apidocs
```

## Troubleshooting

### Common Issues
1. **JavaDoc Warnings**: Use `-Ddoclint=none` to suppress
2. **Missing Comments**: Add proper JavaDoc comments
3. **API Docs Not Loading**: Ensure application is running
4. **Encoding Issues**: Set UTF-8 encoding in Maven config

### Validation
```bash
# Validate JavaDoc
mvn javadoc:validate

# Check API docs
curl -f http://localhost:8080/v3/api-docs

# Test documentation generation
mvn clean compile site
```

## PDF Generation Troubleshooting

### Common Issues & Solutions

#### 1. wkhtmltopdf Issues
**Problem**: "wkhtmltopdf: cannot connect to X server"
**Solution**: Use `--disable-smart-width` or run in headless mode
```bash
wkhtmltopdf --disable-smart-width target/site/apidocs/index.html docs.pdf
```

**Problem**: "wkhtmltopdf: command not found"
**Solution**: Add to PATH or use full path
```bash
# Windows
"C:\Program Files\wkhtmltopdf\bin\wkhtmltopdf.exe" input.html output.pdf

# Linux/macOS
/usr/local/bin/wkhtmltopdf input.html output.pdf
```

#### 2. Pandoc Issues
**Problem**: "pandoc: unrecognized option `--pdf-engine'"
**Solution**: Update pandoc to latest version
```bash
# Check version
pandoc --version

# Update (if needed)
# Windows: choco upgrade pandoc
# Linux: sudo apt-get update && sudo apt-get upgrade pandoc
# macOS: brew upgrade pandoc
```

#### 3. Chrome Headless Issues
**Problem**: "Failed to launch Chrome headless"
**Solution**: Add additional flags
```bash
google-chrome --headless --disable-gpu --no-sandbox \
  --disable-setuid-sandbox --print-to-pdf=documentation.pdf \
  target/site/apidocs/index.html
```

#### 4. PDF Quality Issues
**Problem**: Poor formatting or missing content
**Solutions**:
```bash
# Better quality with wkhtmltopdf
wkhtmltopdf --page-size A4 --dpi 300 --image-quality 100 \
  --margin-top 0.5cm --margin-bottom 0.5cm \
  --encoding UTF-8 --disable-smart-width \
  target/site/apidocs/index.html documentation.pdf

# With pandoc for better LaTeX support
pandoc -f html -t latex -o documentation.tex \
  target/site/apidocs/index.html && \
pdflatex documentation.tex
```

## Quick PDF Generation Commands

### One-Liner Commands
```bash
# Fast PDF generation (wkhtmltopdf)
mvn javadoc:javadoc -Ddoclint=none && wkhtmltopdf target/site/apidocs/index.html docs.pdf

# PDF with API docs included
mvn spring-boot:run & sleep 30 && curl -o api.json http://localhost:8080/v3/api-docs && pkill -f "spring-boot:run" && wkhtmltopdf target/site/apidocs/index.html docs.pdf

# Using Chrome (cross-platform)
mvn javadoc:javadoc -Ddoclint=none && google-chrome --headless --print-to-pdf=docs.pdf target/site/apidocs/index.html
```

### Windows Batch Script
```batch
@echo off
echo Generating PDF documentation...

REM Generate JavaDoc
call mvn javadoc:javadoc -Ddoclint=none

REM Convert to PDF (adjust path to wkhtmltopdf)
"C:\Program Files\wkhtmltopdf\bin\wkhtmltopdf.exe" target/site/apidocs/index.html employee-management-system-docs.pdf

echo PDF generated: employee-management-system-docs.pdf
pause
```

### Linux/macOS Shell Script
```bash
#!/bin/bash
echo "Generating PDF documentation..."

# Generate JavaDoc
mvn javadoc:javadoc -Ddoclint=none

# Convert to PDF
wkhtmltopdf --page-size A4 --margin-top 1cm --margin-right 1cm \
  --margin-bottom 1cm --margin-left 1cm --encoding UTF-8 \
  --title "Employee Management System Documentation" \
  target/site/apidocs/index.html employee-management-system-docs.pdf

echo "PDF generated: employee-management-system-docs.pdf"
```
