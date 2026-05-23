# API Documentation Generation

## Using SpringDoc OpenAPI

### Access Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

### Generate Static Documentation
```bash
# Download OpenAPI spec
curl -o api-docs.json http://localhost:8080/v3/api-docs

# Generate HTML with swagger-ui
docker run -p 80:8080 \
  -e SWAGGER_JSON=/api-docs.json \
  -v $(pwd)/api-docs.json:/api-docs.json \
  swaggerapi/swagger-ui
```

### Using swagger-codegen
```bash
# Install swagger-codegen
npm install -g @swagger-api/swagger-codegen-cli

# Generate HTML documentation
swagger-codegen-cli generate -i api-docs.json -l html2 -o docs/

# Generate PDF
swagger-codegen-cli generate -i api-docs.json -l html2 -o docs/ && \
cd docs && wkhtmltopdf index.html api-documentation.pdf
```

## Maven Plugin for Static Docs

Add to pom.xml:
```xml
<plugin>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-maven-plugin</artifactId>
    <version>2.2.0</version>
    <executions>
        <execution>
            <id>generate-api-docs</id>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Then run:
```bash
mvn springdoc-openapi:generate
```
