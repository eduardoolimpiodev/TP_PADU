# User Data Processor

A robust Spring Boot application for processing and storing user data from multiple file formats (CSV, JSON, XML) with comprehensive validation, error handling, and flexible output formatting.

## Features

- **Multi-format File Processing**: Support for CSV, JSON, and XML files
- **PostgreSQL Storage**: Reliable data persistence with JPA/Hibernate
- **REST API**: Complete CRUD operations with standardized responses
- **Flexible Output**: Query results in JSON, CSV, or XML formats
- **Data Validation**: Comprehensive validation with custom validators
- **Error Handling**: Global exception handling with detailed error messages
- **Rate Limiting**: Protection against abuse with configurable limits
- **Request Logging**: Complete request/response logging with performance metrics
- **Extensible Architecture**: Strategy pattern for easy addition of new file formats

## Technologies

- **Java 17** - Modern Java features and performance
- **Spring Boot 3.2** - Enterprise-grade framework
- **Spring Data JPA** - Data access layer with Hibernate
- **PostgreSQL** - Robust relational database
- **Maven** - Dependency management and build tool
- **OpenCSV** - CSV file processing
- **Jackson** - JSON/XML serialization
- **Bean Validation** - Data validation framework

## Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

## Setup and Installation

### 1. Database Setup
```sql
-- Create database
CREATE DATABASE user_processor;

-- Create user (optional)
CREATE USER processor_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE user_processor TO processor_user;
```

### 2. Application Configuration
Update `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/user_processor
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run
```bash
# Clone the repository
git clone <repository-url>
cd user-data-processor

# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Interactive Documentation (Swagger UI)
Once the application is running, you can access the interactive API documentation at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

The Swagger UI provides:
- Interactive API testing
- Complete endpoint documentation
- Request/response examples
- Schema definitions
- Try-it-out functionality

### REST API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
No authentication required for this version.

### Rate Limiting
- Upload endpoint: 60 requests per minute per IP
- Other endpoints: No rate limiting

## Endpoints

### File Upload
```http
POST /api/users/upload
Content-Type: multipart/form-data

Parameters:
- file: MultipartFile (required) - The file to upload
- fileType: String (required) - File type: "csv", "json", or "xml"
```

**Example:**
```bash
curl -X POST \
  -F "file=@users.csv" \
  -F "fileType=csv" \
  http://localhost:8080/api/users/upload
```

**Response:**
```json
{
  "success": true,
  "message": "File processed successfully",
  "data": {
    "totalRecords": 100,
    "processedRecords": 95,
    "skippedRecords": 3,
    "errorRecords": 2,
    "errors": ["Invalid email at line 15"],
    "warnings": ["Duplicate email skipped: john@example.com"]
  },
  "timestamp": "2024-11-07T15:30:00"
}
```

### Query Users
```http
GET /api/users?format={format}&page={page}&size={size}

Parameters:
- format: String (optional) - Output format: "json" (default), "csv", "xml"
- page: Integer (optional) - Page number (default: 0)
- size: Integer (optional) - Page size (default: 10)
```

**Examples:**
```bash
# Get users as JSON with pagination
curl "http://localhost:8080/api/users?format=json&page=0&size=10"

# Download users as CSV
curl "http://localhost:8080/api/users?format=csv" -o users.csv

# Download users as XML
curl "http://localhost:8080/api/users?format=xml" -o users.xml
```

### Get User by ID
```http
GET /api/users/{id}
```

### Get User by Email
```http
GET /api/users/email/{email}
```

### Get Users by Source
```http
GET /api/users/source/{source}?page={page}&size={size}

Parameters:
- source: String - File source: "csv", "json", "xml"
```

### Statistics
```http
GET /api/statistics
GET /api/statistics/sources
GET /api/statistics/sources/{source}
GET /api/statistics/detailed
```

### System Information
```http
GET /api/health
GET /api/info
GET /api/supported-types
```

## File Formats

### CSV Format
```csv
name,email
John Doe,john.doe@example.com
Jane Smith,jane.smith@example.com
```

### JSON Format
```json
[
  {
    "name": "John Doe",
    "email": "john.doe@example.com"
  },
  {
    "name": "Jane Smith",
    "email": "jane.smith@example.com"
  }
]
```

### XML Format
```xml
<users>
  <user>
    <name>John Doe</name>
    <email>john.doe@example.com</email>
  </user>
  <user>
    <name>Jane Smith</name>
    <email>jane.smith@example.com</email>
  </user>
</users>
```

## Architecture

The application follows a layered architecture with clear separation of concerns:

```
├── Controller Layer    - REST endpoints and request handling
├── Service Layer      - Business logic and orchestration
├── Repository Layer   - Data access and persistence
├── Entity Layer       - JPA entities and database mapping
└── Configuration      - Application configuration and beans
```

### Design Patterns Used
- **Strategy Pattern**: File processors for different formats
- **Factory Pattern**: File processor creation
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer objects for API communication

## Configuration

### Application Properties
```properties
# Processing Configuration
app.processing.max-file-size=10485760          # 10MB
app.processing.max-records-per-file=10000      # Maximum records per file
app.processing.skip-duplicate-emails=true      # Skip duplicate emails
app.processing.validate-email-format=true     # Validate email format
app.processing.allow-empty-fields=false       # Allow empty name/email fields

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Error Handling

The application provides comprehensive error handling with standardized responses:

### Error Response Format
```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error message",
  "timestamp": "2024-11-07T15:30:00"
}
```

### HTTP Status Codes
- `200 OK` - Success
- `400 Bad Request` - Validation or processing error
- `404 Not Found` - Resource not found
- `413 Payload Too Large` - File size exceeds limit
- `429 Too Many Requests` - Rate limit exceeded
- `500 Internal Server Error` - Unexpected server error

## Monitoring and Logging

### Request Logging
All API requests are logged with:
- HTTP method and URI
- Client IP address
- Response status code
- Request duration
- Query parameters

### Error Tracking
- All exceptions are logged with stack traces
- Processing errors include line numbers for file issues
- Validation errors provide field-specific messages

## Testing

Run the test suite:
```bash
mvn test
```

## Deployment

### Production Configuration
1. Update database credentials for production
2. Configure appropriate logging levels
3. Set up database connection pooling
4. Configure CORS for your domain
5. Set up monitoring and alerting

### Docker Deployment (Optional)
```dockerfile
FROM openjdk:17-jre-slim
COPY target/user-data-processor-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For questions or issues, please:
1. Check the existing documentation
2. Search existing issues
3. Create a new issue with detailed information

## Version History

- **v1.0.0** - Initial release with full functionality
  - Multi-format file processing (CSV, JSON, XML)
  - PostgreSQL integration
  - REST API with comprehensive endpoints
  - Validation and error handling
  - Rate limiting and logging
