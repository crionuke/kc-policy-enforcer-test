# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This is a multi-module Maven project built with Quarkus framework using Java 21. The project follows a microservices architecture:

- **Root module**: Parent POM managing dependencies and build configuration
- **tenants module**: Main application service handling tenant, project, and stage management
- **localtesting module**: Local testing utilities and configurations

## Key Technologies

- **Quarkus 3.26.2**: Supersonic Subatomic Java Framework
- **Java 21**: Required JDK version
- **PostgreSQL**: Primary database with Flyway migrations
- **Hibernate ORM with Panache**: Data persistence
- **Jakarta REST**: RESTful API implementation
- **Docker**: Container support with multiple build profiles

## Development Commands

### Running the application
```bash
./mvnw quarkus:dev                    # Start in development mode with live reload
./mvnw quarkus:dev -pl tenants        # Start specific module in dev mode
```

### Building the application
```bash
./mvnw package                        # Standard build
./mvnw package -Dquarkus.package.jar.type=uber-jar  # Build uber-jar
./mvnw package -Dnative               # Build native executable
./mvnw package -Dnative -Dquarkus.native.container-build=true  # Native build in container
```

### Testing
```bash
./mvnw test                           # Run unit tests
./mvnw test -Dtest=TenantResourceTest  # Run specific test class
./mvnw verify                         # Run integration tests
```

## Architecture Overview

The application implements a hierarchical multi-tenancy model:
- **Tenants**: Top-level organizational units with configuration and status management
- **Projects**: Tenant-owned project containers with their own configuration
- **Stages**: Project environments (dev, staging, prod) with independent configurations

Each entity uses:
- UUIDv7 identifiers for ordered, time-based unique IDs
- Configuration versioning system for change tracking
- Status-based state management with validation
- REST API with OpenAPI documentation

## Database

- **PostgreSQL** database with Flyway migrations
- **Hibernate ORM** with Panache for simplified data access
- Migrations run automatically at startup (`quarkus.flyway.migrate-at-start: true`)
- Database schema generation disabled (managed via Flyway)

## API Documentation

- Swagger UI available at `http://localhost:8080/q/dev/swagger-ui/` in dev mode
- OpenAPI schema stored in `target/openapi/` directory
- Always includes Swagger UI (`quarkus.swagger-ui.always-include: true`)

## Container Images

Multiple Dockerfile variants available in `tenants/src/main/docker/`:
- `Dockerfile.jvm`: Standard JVM-based container
- `Dockerfile.native`: Native executable container
- `Dockerfile.native-micro`: Minimal native container
- `Dockerfile.legacy-jar`: Legacy JAR format container

## Java Tips

- No comments are required in the code.
- The desirable way to define a local variable is final var name = ...