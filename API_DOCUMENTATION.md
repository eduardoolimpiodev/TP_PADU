# API Documentation

## Overview

The User Data Processor API provides endpoints for uploading, processing, and querying user data from multiple file formats (CSV, JSON, XML).

## Base Information

- **Base URL**: `http://localhost:8080/api`
- **Version**: 1.0.0
- **Content-Type**: `application/json` (except file uploads)

## Authentication

No authentication is required for this version of the API.

## Rate Limiting

- **Upload Endpoint**: 60 requests per minute per IP address
- **Other Endpoints**: No rate limiting applied

## Error Handling

All API responses follow a consistent format:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2024-11-07T15:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "error": "Detailed error message",
  "timestamp": "2024-11-07T15:30:00"
}
```

## HTTP Status Codes

| Code | Description |
|------|-------------|
| 200  | OK - Request successful |
| 400  | Bad Request - Invalid request parameters or validation error |
| 404  | Not Found - Resource not found |
| 413  | Payload Too Large - File size exceeds limit |
| 429  | Too Many Requests - Rate limit exceeded |
| 500  | Internal Server Error - Unexpected server error |

---

## Endpoints

### 1. File Upload

Upload and process user data files.

**Endpoint**: `POST /api/users/upload`

**Content-Type**: `multipart/form-data`

**Parameters**:
- `file` (required): The file to upload (MultipartFile)
- `fileType` (required): File type - must be one of: "csv", "json", "xml"

**File Size Limit**: 10MB

**Request Example**:
```bash
curl -X POST \
  -F "file=@users.csv" \
  -F "fileType=csv" \
  http://localhost:8080/api/users/upload
```

**Response Example**:
```json
{
  "success": true,
  "message": "File processed successfully",
  "data": {
    "totalRecords": 100,
    "processedRecords": 95,
    "skippedRecords": 3,
    "errorRecords": 2,
    "errors": [
      "Invalid email format at line 15: invalid-email",
      "Name is required at line 23"
    ],
    "warnings": [
      "User with email john@example.com already exists - skipped"
    ],
    "processedUsers": [
      {
        "id": 1,
        "name": "John Doe",
        "email": "john.doe@example.com",
        "source": "csv",
        "createdAt": "2024-11-07T15:30:00",
        "updatedAt": "2024-11-07T15:30:00"
      }
    ]
  },
  "timestamp": "2024-11-07T15:30:00"
}
```

---

### 2. Query Users

Retrieve users with optional formatting and pagination.

**Endpoint**: `GET /api/users`

**Parameters**:
- `format` (optional): Output format - "json" (default), "csv", "xml"
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**JSON Response Example**:
```bash
curl "http://localhost:8080/api/users?format=json&page=0&size=5"
```

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "source": "csv",
      "createdAt": "2024-11-07T15:30:00",
      "updatedAt": "2024-11-07T15:30:00"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 5,
    "totalElements": 100,
    "totalPages": 20
  },
  "timestamp": "2024-11-07T15:30:00"
}
```

**CSV Download Example**:
```bash
curl "http://localhost:8080/api/users?format=csv" -o users.csv
```

**XML Download Example**:
```bash
curl "http://localhost:8080/api/users?format=xml" -o users.xml
```

---

### 3. Get User by ID

Retrieve a specific user by their ID.

**Endpoint**: `GET /api/users/{id}`

**Parameters**:
- `id` (path): User ID (Long)

**Request Example**:
```bash
curl http://localhost:8080/api/users/1
```

**Response Example**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "source": "csv",
    "createdAt": "2024-11-07T15:30:00",
    "updatedAt": "2024-11-07T15:30:00"
  },
  "timestamp": "2024-11-07T15:30:00"
}
```

---

### 4. Get User by Email

Retrieve a user by their email address.

**Endpoint**: `GET /api/users/email/{email}`

**Parameters**:
- `email` (path): User email address

**Request Example**:
```bash
curl http://localhost:8080/api/users/email/john.doe@example.com
```

---

### 5. Get Users by Source

Retrieve users filtered by file source type.

**Endpoint**: `GET /api/users/source/{source}`

**Parameters**:
- `source` (path): File source - "csv", "json", or "xml"
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Request Example**:
```bash
curl "http://localhost:8080/api/users/source/csv?page=0&size=10"
```

---

### 6. Delete User

Delete a user by their ID.

**Endpoint**: `DELETE /api/users/{id}`

**Parameters**:
- `id` (path): User ID (Long)

**Request Example**:
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

**Response Example**:
```json
{
  "success": true,
  "message": "User deleted successfully",
  "timestamp": "2024-11-07T15:30:00"
}
```

---

## Statistics Endpoints

### 7. General Statistics

Get overall system statistics.

**Endpoint**: `GET /api/statistics`

**Response Example**:
```json
{
  "success": true,
  "data": {
    "totalUsers": 1500,
    "usersBySource": {
      "csv": 800,
      "json": 450,
      "xml": 250
    },
    "sourcePercentages": {
      "csv": 53.33,
      "json": 30.0,
      "xml": 16.67
    }
  },
  "timestamp": "2024-11-07T15:30:00"
}
```

### 8. Source Statistics

Get statistics by file source.

**Endpoint**: `GET /api/statistics/sources`

**Response Example**:
```json
{
  "success": true,
  "data": {
    "csv": 800,
    "json": 450,
    "xml": 250
  },
  "timestamp": "2024-11-07T15:30:00"
}
```

### 9. Count by Specific Source

Get user count for a specific source.

**Endpoint**: `GET /api/statistics/sources/{source}`

**Response Example**:
```json
{
  "success": true,
  "data": {
    "source": "csv",
    "count": 800
  },
  "timestamp": "2024-11-07T15:30:00"
}
```

---

## System Endpoints

### 10. Health Check

Check if the API is running.

**Endpoint**: `GET /api/health`

**Response Example**:
```json
{
  "status": "UP",
  "message": "User Data Processor API is running",
  "timestamp": "2024-11-07T15:30:00",
  "version": "1.0.0"
}
```

### 11. System Information

Get system information and capabilities.

**Endpoint**: `GET /api/info`

**Response Example**:
```json
{
  "application": "User Data Processor",
  "version": "1.0.0",
  "description": "System for processing and storing user data from CSV, JSON, and XML files",
  "supportedFileTypes": ["csv", "json", "xml"],
  "supportedOutputFormats": ["json", "csv", "xml"],
  "timestamp": "2024-11-07T15:30:00"
}
```

### 12. Supported Types

Get supported file and output types.

**Endpoint**: `GET /api/supported-types`

**Response Example**:
```json
{
  "success": true,
  "data": {
    "inputFormats": ["csv", "json", "xml"],
    "outputFormats": ["json", "csv", "xml"]
  },
  "timestamp": "2024-11-07T15:30:00"
}
```

---

## File Format Specifications

### CSV Format Requirements
- Must have headers: `name` and `email`
- Headers can be in any order
- UTF-8 encoding
- Maximum 10,000 records per file

**Example**:
```csv
name,email
John Doe,john.doe@example.com
Jane Smith,jane.smith@example.com
```

### JSON Format Requirements
- Must be an array of objects
- Each object must have `name` and `email` properties
- UTF-8 encoding
- Maximum 10,000 records per file

**Example**:
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

### XML Format Requirements
- Root element must be `<users>`
- Each user must be in a `<user>` element
- Must contain `<name>` and `<email>` child elements
- UTF-8 encoding
- Maximum 10,000 records per file

**Example**:
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

---

## Validation Rules

### File Validation
- Maximum file size: 10MB
- Supported extensions: .csv, .json, .xml
- File must not be empty
- Valid file structure required

### Data Validation
- **Name**: Required, maximum 255 characters
- **Email**: Required, valid email format, maximum 255 characters, must be unique
- **Source**: Automatically set based on file type

### Processing Rules
- Duplicate emails are skipped (not inserted)
- Invalid records are reported but don't stop processing
- Empty name or email fields cause record rejection
- Processing continues even if some records fail

---

## Error Examples

### Validation Error
```json
{
  "success": false,
  "message": "Validation error",
  "error": "Invalid file type. Supported types: csv, json, xml",
  "timestamp": "2024-11-07T15:30:00"
}
```

### File Too Large Error
```json
{
  "success": false,
  "message": "File size exceeds maximum allowed size",
  "error": "Maximum file size is 10MB",
  "timestamp": "2024-11-07T15:30:00"
}
```

### Rate Limit Error
```json
{
  "success": false,
  "message": "Rate limit exceeded",
  "error": "Too many requests. Maximum 60 requests per minute allowed.",
  "timestamp": "2024-11-07T15:30:00"
}
```

### Not Found Error
```json
{
  "success": false,
  "message": "User not found",
  "timestamp": "2024-11-07T15:30:00"
}
```
