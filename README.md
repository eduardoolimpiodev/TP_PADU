# User Data Processor

System for processing and storing user data in multiple formats (CSV, JSON, XML) built with Spring Boot.

## Technologies

- Java 17
- Spring Boot 3.2
- Spring Data JPA
- PostgreSQL
- Maven
- OpenCSV
- Jackson (JSON/XML)

## Features

- Upload and processing of CSV, JSON and XML files
- Storage in PostgreSQL database
- REST API for upload and query operations
- Output formatting in JSON, CSV or XML
- Robust data and format validation
- Extensible architecture with Strategy Pattern

## Setup

1. Configure PostgreSQL and create the `user_processor` database
2. Adjust credentials in `application.properties`
3. Run: `mvn spring-boot:run`

## API Endpoints

- `POST /api/users/upload` - File upload
- `GET /api/users` - Query users (supports ?format=json|csv|xml)
