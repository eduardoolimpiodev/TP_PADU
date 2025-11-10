# Installation Guide

This guide provides step-by-step instructions for setting up the User Data Processor application in different environments.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Development Environment Setup](#development-environment-setup)
3. [Production Environment Setup](#production-environment-setup)
4. [Docker Setup](#docker-setup)
5. [Database Configuration](#database-configuration)
6. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### System Requirements
- **Operating System**: Windows 10/11, macOS 10.15+, or Linux (Ubuntu 18.04+)
- **Memory**: Minimum 2GB RAM (4GB recommended)
- **Storage**: At least 1GB free space
- **Network**: Internet connection for downloading dependencies

### Required Software
- **Java Development Kit (JDK) 17 or higher**
- **PostgreSQL 12 or higher**
- **Maven 3.6 or higher**
- **Git** (for cloning the repository)

### Optional Software
- **Docker** (for containerized deployment)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)
- **Postman** or **curl** (for API testing)

---

## Development Environment Setup

### Step 1: Install Java JDK 17

#### Windows
1. Download OpenJDK 17 from [Adoptium](https://adoptium.net/)
2. Run the installer and follow the setup wizard
3. Verify installation:
   ```cmd
   java -version
   javac -version
   ```

#### macOS
```bash
# Using Homebrew
brew install openjdk@17

# Add to PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify installation
java -version
```

#### Linux (Ubuntu/Debian)
```bash
# Update package index
sudo apt update

# Install OpenJDK 17
sudo apt install openjdk-17-jdk

# Verify installation
java -version
javac -version
```

### Step 2: Install PostgreSQL

#### Windows
1. Download PostgreSQL from [official website](https://www.postgresql.org/download/windows/)
2. Run the installer
3. Set password for `postgres` user
4. Note the port (default: 5432)

#### macOS
```bash
# Using Homebrew
brew install postgresql@14
brew services start postgresql@14

# Create database user
createuser -s postgres
```

#### Linux (Ubuntu/Debian)
```bash
# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Set password for postgres user
sudo -u postgres psql
\password postgres
\q
```

### Step 3: Install Maven

#### Windows
1. Download Maven from [Apache Maven](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH environment variable
4. Verify: `mvn -version`

#### macOS
```bash
# Using Homebrew
brew install maven

# Verify installation
mvn -version
```

#### Linux (Ubuntu/Debian)
```bash
# Install Maven
sudo apt install maven

# Verify installation
mvn -version
```

### Step 4: Setup Database

1. **Connect to PostgreSQL**:
   ```bash
   psql -U postgres -h localhost
   ```

2. **Create Database**:
   ```sql
   CREATE DATABASE user_processor;
   ```

3. **Create Application User** (optional but recommended):
   ```sql
   CREATE USER app_user WITH PASSWORD 'your_secure_password';
   GRANT ALL PRIVILEGES ON DATABASE user_processor TO app_user;
   ```

4. **Verify Database**:
   ```sql
   \l
   \q
   ```

### Step 5: Clone and Configure Application

1. **Clone Repository**:
   ```bash
   git clone <repository-url>
   cd user-data-processor
   ```

2. **Configure Database Connection**:
   Edit `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/user_processor
   spring.datasource.username=app_user
   spring.datasource.password=your_secure_password
   spring.datasource.driver-class-name=org.postgresql.Driver
   
   # JPA Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=false
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   
   # Server Configuration
   server.port=8080
   ```

3. **Build Application**:
   ```bash
   mvn clean compile
   ```

4. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```

5. **Verify Installation**:
   ```bash
   curl http://localhost:8080/api/health
   ```

---

## Production Environment Setup

### Step 1: Environment Preparation

1. **Create Application User**:
   ```bash
   sudo useradd -m -s /bin/bash appuser
   sudo mkdir -p /opt/user-data-processor
   sudo chown appuser:appuser /opt/user-data-processor
   ```

2. **Install Java** (same as development setup)

3. **Configure PostgreSQL for Production**:
   ```bash
   # Edit PostgreSQL configuration
   sudo vim /etc/postgresql/14/main/postgresql.conf
   ```
   
   Update settings:
   ```
   listen_addresses = 'localhost'
   max_connections = 100
   shared_buffers = 256MB
   ```

### Step 2: Application Configuration

1. **Create Production Properties**:
   ```bash
   sudo -u appuser vim /opt/user-data-processor/application-prod.properties
   ```

   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/user_processor
   spring.datasource.username=app_user
   spring.datasource.password=${DB_PASSWORD}
   
   # JPA Configuration
   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.show-sql=false
   
   # Server Configuration
   server.port=8080
   
   # Logging Configuration
   logging.level.com.userprocessor=INFO
   logging.level.org.springframework.web=WARN
   logging.file.name=/var/log/user-data-processor/app.log
   
   # Processing Configuration
   app.processing.max-file-size=10485760
   app.processing.max-records-per-file=10000
   ```

2. **Set Environment Variables**:
   ```bash
   sudo -u appuser vim /opt/user-data-processor/.env
   ```
   
   ```bash
   export DB_PASSWORD=your_secure_password
   export JAVA_OPTS="-Xmx1024m -Xms512m"
   export SPRING_PROFILES_ACTIVE=prod
   ```

### Step 3: Build and Deploy

1. **Build Application**:
   ```bash
   mvn clean package -DskipTests
   ```

2. **Copy JAR to Production**:
   ```bash
   sudo cp target/user-data-processor-1.0.0.jar /opt/user-data-processor/
   sudo chown appuser:appuser /opt/user-data-processor/user-data-processor-1.0.0.jar
   ```

3. **Create Systemd Service**:
   ```bash
   sudo vim /etc/systemd/system/user-data-processor.service
   ```

   ```ini
   [Unit]
   Description=User Data Processor
   After=network.target postgresql.service
   
   [Service]
   Type=simple
   User=appuser
   WorkingDirectory=/opt/user-data-processor
   Environment=SPRING_PROFILES_ACTIVE=prod
   EnvironmentFile=/opt/user-data-processor/.env
   ExecStart=/usr/bin/java $JAVA_OPTS -jar user-data-processor-1.0.0.jar
   Restart=always
   RestartSec=10
   
   [Install]
   WantedBy=multi-user.target
   ```

4. **Start Service**:
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable user-data-processor
   sudo systemctl start user-data-processor
   sudo systemctl status user-data-processor
   ```

### Step 4: Configure Reverse Proxy (Optional)

**Nginx Configuration**:
```bash
sudo vim /etc/nginx/sites-available/user-data-processor
```

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # File upload configuration
        client_max_body_size 10M;
    }
}
```

Enable site:
```bash
sudo ln -s /etc/nginx/sites-available/user-data-processor /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

---

## Docker Setup

### Step 1: Create Dockerfile

```dockerfile
FROM openjdk:17-jre-slim

# Create app directory
WORKDIR /app

# Copy JAR file
COPY target/user-data-processor-1.0.0.jar app.jar

# Create non-root user
RUN addgroup --system appgroup && adduser --system --group appuser
RUN chown -R appuser:appgroup /app
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Step 2: Create Docker Compose

```yaml
version: '3.8'

services:
  database:
    image: postgres:14
    environment:
      POSTGRES_DB: user_processor
      POSTGRES_USER: app_user
      POSTGRES_PASSWORD: your_secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U app_user -d user_processor"]
      interval: 30s
      timeout: 10s
      retries: 3

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://database:5432/user_processor
      SPRING_DATASOURCE_USERNAME: app_user
      SPRING_DATASOURCE_PASSWORD: your_secure_password
    depends_on:
      database:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
```

### Step 3: Build and Run

```bash
# Build and start services
docker-compose up --build -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

---

## Database Configuration

### Performance Tuning

**PostgreSQL Configuration** (`postgresql.conf`):
```
# Memory settings
shared_buffers = 256MB
effective_cache_size = 1GB
work_mem = 4MB

# Connection settings
max_connections = 100

# Checkpoint settings
checkpoint_completion_target = 0.9
wal_buffers = 16MB

# Query planner settings
random_page_cost = 1.1
effective_io_concurrency = 200
```

### Backup Configuration

**Daily Backup Script**:
```bash
#!/bin/bash
BACKUP_DIR="/var/backups/postgresql"
DB_NAME="user_processor"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

pg_dump -U app_user -h localhost $DB_NAME | gzip > $BACKUP_DIR/backup_${DATE}.sql.gz

# Keep only last 7 days
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete
```

**Crontab Entry**:
```bash
0 2 * * * /path/to/backup-script.sh
```

---

## Troubleshooting

### Common Issues

#### 1. Application Won't Start

**Check Java Version**:
```bash
java -version
# Should show version 17 or higher
```

**Check Database Connection**:
```bash
psql -U app_user -h localhost -d user_processor
```

**Check Application Logs**:
```bash
# If using systemd
sudo journalctl -u user-data-processor -f

# If running directly
tail -f logs/application.log
```

#### 2. Database Connection Issues

**Verify PostgreSQL is Running**:
```bash
sudo systemctl status postgresql
```

**Check Database Exists**:
```bash
psql -U postgres -l | grep user_processor
```

**Test Connection**:
```bash
psql -U app_user -h localhost -d user_processor -c "SELECT 1;"
```

#### 3. File Upload Issues

**Check File Permissions**:
```bash
ls -la /tmp/
# Ensure application can write to temp directory
```

**Check Disk Space**:
```bash
df -h
```

**Verify File Size Limits**:
- Application limit: 10MB (configurable)
- Web server limit: Check nginx/apache configuration

#### 4. Performance Issues

**Check Memory Usage**:
```bash
free -h
ps aux | grep java
```

**Monitor Database Performance**:
```sql
SELECT * FROM pg_stat_activity WHERE state = 'active';
```

**Check Application Metrics**:
```bash
curl http://localhost:8080/api/statistics
```

### Log Analysis

**Application Logs Location**:
- Development: `logs/application.log`
- Production: `/var/log/user-data-processor/app.log`

**Important Log Patterns**:
```bash
# Error patterns
grep -i "error\|exception" /var/log/user-data-processor/app.log

# Performance issues
grep -i "slow\|timeout" /var/log/user-data-processor/app.log

# Database issues
grep -i "connection\|sql" /var/log/user-data-processor/app.log
```

### Getting Help

1. **Check Application Health**:
   ```bash
   curl http://localhost:8080/api/health
   ```

2. **Review Configuration**:
   - Verify database connection settings
   - Check file upload limits
   - Confirm Java version compatibility

3. **Collect Information**:
   - Application version
   - Java version
   - PostgreSQL version
   - Operating system
   - Error messages and stack traces

4. **Contact Support**:
   - Include all collected information
   - Provide steps to reproduce the issue
   - Share relevant log excerpts
