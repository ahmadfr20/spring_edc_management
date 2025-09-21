# EDC Management API

Aplikasi Spring Boot untuk manajemen terminal EDC dengan fitur echo, validasi signature HMAC-SHA256, dan logging.

## Teknologi yang Digunakan

- **Java 1.8**
- **Spring Boot 2.7.18**
- **PostgreSQL** (Database)
- **Hibernate** (ORM)
- **Spring Security** (Authentication)
- **Logback** (Logging dengan rolling file appender)

## Fitur Utama

### 1. CRUD Operations untuk Terminal EDC
- Create, Read, Update, Delete terminal EDC
- Search berdasarkan status dan lokasi
- Paginasi dan sorting
- Autentikasi dengan basic auth (username/password)

### 2. Echo Functionality
- Endpoint `/api/edc/echo` untuk menerima request dari terminal
- Validasi signature HMAC-SHA256
- Logging semua request echo ke database
- Update last ping terminal

### 3. Signature Validation
- HMAC-SHA256 dengan key: `dateTime|EDCmgmt2025!.?`
- Tolerance 2 menit untuk perbedaan waktu
- Header `Signature` wajib untuk echo requests

### 4. Logging System
- Rolling file appender setiap jam
- Kompresi otomatis (.gz)
- Retention 72 jam (3 hari)
- Log ke console dan file

## Setup dan Instalasi

### 1. Prerequisites
- Java 1.8+
- PostgreSQL 12+
- Maven 3.6+

### 2. Database Setup
```sql
-- Jalankan script database-setup.sql
-- Atau manual:
CREATE DATABASE edc_management;
CREATE USER edc_user WITH PASSWORD 'edc_password';
GRANT ALL PRIVILEGES ON DATABASE edc_management TO edc_user;
```

### 3. Configuration
Edit `application.properties`:
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/edc_management
spring.datasource.username=edc_user
spring.datasource.password=edc_password

# Security
spring.security.user.name=admin
spring.security.user.password=admin123

# HMAC Secret
app.hmac.secret=EDCmgmt2025!.?
```

### 4. Build dan Run
```bash
# Build
mvn clean package

# Run
java -jar target/edc-management-1.0.0.jar

# Atau dengan Maven
mvn spring-boot:run
```

## API Endpoints

### Authentication
Semua endpoint (kecuali `/api/edc/echo`) memerlukan Basic Authentication:
- Username: `admin`
- Password: `admin123`

### Terminal EDC Management

#### 1. Create Terminal
```http
POST /api/edc/terminals
Content-Type: application/json
Authorization: Basic YWRtaW46YWRtaW4xMjM=

{
    "terminalId": "EDC006",
    "location": "Jakarta Barat",
    "status": "ACTIVE",
    "merchantName": "Merchant F",
    "ipAddress": "192.168.1.105",
    "port": 8080
}
```

#### 2. Get All Terminals (with pagination)
```http
GET /api/edc/terminals?page=0&size=10&sortBy=id&sortDir=desc
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### 3. Get Terminal by ID
```http
GET /api/edc/terminals/1
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### 4. Get Terminal by Terminal ID
```http
GET /api/edc/terminals/terminal/EDC001
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### 5. Update Terminal
```http
PUT /api/edc/terminals/1
Content-Type: application/json
Authorization: Basic YWRtaW46YWRtaW4xMjM=

{
    "terminalId": "EDC001",
    "location": "Jakarta Pusat Updated",
    "status": "MAINTENANCE",
    "merchantName": "Merchant A Updated",
    "ipAddress": "192.168.1.100",
    "port": 8080
}
```

#### 6. Delete Terminal
```http
DELETE /api/edc/terminals/1
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### 7. Get Terminals by Status
```http
GET /api/edc/terminals/status/ACTIVE
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### 8. Search Terminals by Location
```http
GET /api/edc/terminals/search?location=Jakarta
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

### Echo Functionality

#### 1. Echo Request (dari Terminal)
```http
POST /api/edc/echo
Content-Type: application/json
Signature: [HMAC-SHA256 signature]

{
    "terminalId": "EDC001"
}
```

#### 2. Get All Echo Logs
```http
GET /api/edc/echo-logs?page=0&size=10
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### 3. Get Echo Logs by Terminal
```http
GET /api/edc/echo-logs/terminal/EDC001
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### 4. Get Today's Echo Logs
```http
GET /api/edc/echo-logs/today
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

#### 5. Get Echo Log Statistics
```http
GET /api/edc/echo-logs/stats
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

## Testing Utilities

Aktifkan dengan menambahkan ke `application.properties`:
```properties
test.endpoints.enabled=true
```

### Generate Signature untuk Testing
```http
GET /api/test/generate-signature?terminalId=EDC001
```

### Validate Signature
```http
POST /api/test/validate-signature?terminalId=EDC001&signature=[signature]
```

## HMAC Signature Generation

Untuk membuat signature yang valid:

1. **Key format**: `{dateTime}|EDCmgmt2025!.?`
   - dateTime format: `yyyy-MM-dd HH:mm:ss`
   - Contoh: `2025-01-15 14:30:00|EDCmgmt2025!.?`

2. **Message**: `terminalId`

3. **Algorithm**: HMAC-SHA256

4. **Output**: Hex string

### Contoh dengan curl:
```bash
# Generate signature dengan endpoint test
curl -X GET "http://localhost:8080/api/test/generate-signature?terminalId=EDC001"

# Gunakan signature yang dihasilkan
curl -X POST http://localhost:8080/api/edc/echo \
  -H "Content-Type: application/json" \
  -H "Signature: [generated_signature]" \
  -d '{"terminalId": "EDC001"}'
```

## Logging

Log files tersimpan di direktori `logs/`:
- `edc-management.log` - Current log file
- `edc-management-{date}_{hour}.{index}.log.gz` - Archived logs

Konfigurasi logging:
- Rolling setiap jam
- Kompresi otomatis
- Maksimal file size: 10MB
- Retention: 72 jam
- Total size cap: 1GB

## Error Handling

API menggunakan format response standar:
```json
{
    "success": true/false,
    "message": "Description",
    "data": {...},
    "timestamp": "2025-01-15T14:30:00"
}
```

### HTTP Status Codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request (validation error)
- `401` - Unauthorized (invalid signature/auth)
- `404` - Not Found
- `500` - Internal Server Error

## Security Features

1. **Basic Authentication** untuk management endpoints
2. **HMAC-SHA256 signature validation** untuk echo endpoint
3. **HTTPS headers** (HSTS, XSS Protection, etc.)
4. **Request logging** dengan IP tracking
5. **Time-based signature tolerance** (2 menit)

## Monitoring

- Health check: `GET /api/test/health` (jika test endpoints enabled)
- Echo statistics: `GET /api/edc/echo-logs/stats`
- Database dan log file monitoring

## Production Deployment

1. Update `application.properties` untuk production:
   ```properties
   spring.profiles.active=