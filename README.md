# Personal Finance Tracker Backend

Spring Boot backend for a university project using a 3-layer architecture (Controller -> Service -> Repository).

## Prerequisites

- Java 25
- PostgreSQL

## Setup

Set environment variables for the database connection:

```powershell
$env:DB_HOST="db.auwfqzjrirfvcuumhcbs.supabase.co"
$env:DB_PORT="5432"
$env:DB_NAME="postgres"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
```

Database schema is defined in:

- src/main/resources/schema.sql

## Run

```powershell
./mvnw spring-boot:run
```

Swagger UI:

- http://localhost:8080/swagger-ui/index.html

Notes:

- JPA schema generation is disabled (validate)
- SQL init runs on startup
- Add amount endpoint: PATCH /v1/goals/{id}/amount
