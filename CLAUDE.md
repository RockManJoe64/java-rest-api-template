# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java SpringBoot REST API template demonstrating best practices for building REST APIs. The domain is based on the movie industry, with integration to the TMDB API. It uses an embedded H2 database for development and testing.

**Tech Stack:**
- Java 21
- Spring Boot 3.3.+
- H2 Database (embedded)
- Lombok for boilerplate reduction
- MapStruct for object mapping
- Gradle with custom build configurations

## Common Commands

### Building and Running
```bash
# Build the project
./gradlew build

# Run the application (with local profile)
./gradlew bootRun

# Run without tests
./gradlew build -x test
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run integration tests (separate from unit tests)
./gradlew integrationTest

# Run all tests
./gradlew test integrationTest

# Generate coverage report (JaCoCo)
./gradlew test jacocoTestReport
# Report location: build/reports/jacoco/test/html/index.html
```

### Code Quality
```bash
# Run Spotless formatting check
./gradlew spotlessCheck

# Apply Spotless formatting
./gradlew spotlessApply

# Run PMD static analysis
./gradlew pmdMain pmdTest

# Run SpotBugs
./gradlew spotbugsMain spotbugsTest

# Run all quality checks
./gradlew spotlessCheck pmdMain spotbugsMain test integrationTest
```

## Architecture

This project enforces **Onion Architecture** (Clean Architecture variant) using ArchUnit tests. The enforcement is in `src/test/java/com/rangerforge/movieman/CleanArchitectureTests.java`.

### Layer Structure (from innermost to outermost):

1. **Domain Models** (`domain.entity`): Core business entities (Movie, Actor, etc.)
   - JPA entities with relationships
   - No external dependencies

2. **Domain Services** (`domain.repository`): Repository interfaces
   - Spring Data JPA repositories
   - Define data access contracts

3. **Application Services** (`usecase`): Business logic orchestration
   - Use case implementations (e.g., `GetMovieByNameUseCase`)
   - Contains models for use case inputs/outputs
   - Uses MapStruct mappers to transform between layers

4. **Adapters:**
   - **HTTP Adapter** (`tmdb`): External API integration for TMDB
     - Uses WebClient for reactive HTTP calls
     - Models for TMDB API responses
   - **REST Adapter** (`web`): REST controllers
     - Spring MVC controllers
     - Request/response DTOs

5. **Configuration** (`config`): Spring configuration classes

### Dependency Rules (ArchUnit enforced):
- Inner layers CANNOT depend on outer layers
- Domain entities should be dependency-free
- Use cases can access domain but not adapters
- Adapters can access use cases and domain

### Constructor Injection Pattern
This project uses **constructor injection exclusively**. Lombok's `@RequiredArgsConstructor` generates constructors, and `lombok.config` is configured to copy Spring annotations (like `@Qualifier`) from fields to constructor parameters.

## Integration Tests

Integration tests are in a **separate source set** (`src/integration/java`) with its own resources directory. This is configured in `gradle/integration.gradle`.

**Wiremock Configuration:**
- Stubs located in: `src/integration/resources/wiremock`
- Custom path configured because integration tests aren't in `src/test`
- Example: `MovieControllerIntegrationTests` uses Wiremock JUnit extension to mock TMDB API

Run integration tests independently with `./gradlew integrationTest`.

## Testing Utilities

- **Instancio** (`org.instancio:instancio-junit`): Used for creating test fixtures/fake data
- **MockWebServer** (`com.squareup.okhttp3:mockwebserver`): For mocking WebClient HTTP calls
- **Wiremock**: For integration test HTTP mocking
- **Mockito**: Standard mocking framework

## Code Quality Configuration

**Spotless (Linting):**
- Uses Google Java Format
- Custom import ordering: `java|javax` → `com.rangerforge` → others → static imports
- Auto-applies license header: `/* (C) $YEAR */`
- Includes CleanThat refactoring

**JaCoCo (Coverage):**
- Excludes: Application main class, MapStruct implementations, exceptions, config, entities, repositories, models
- Coverage report required for test task
- XML report enabled (for CI/CD integration)

**PMD & SpotBugs:**
- Custom rulesets: `pmd-ruleset.xml`, `spotbugs-exclude.xml`
- FindSecurityBugs plugin currently disabled (TODO in build.gradle:74)

## OpenAPI Documentation

Auto-generated via SpringDoc. After running the app:
- OpenAPI spec: http://localhost:8080/v3/api-docs
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Environment Configuration

- `sample.env`: Template for environment variables
- `compose.yaml`: Docker Compose setup (development only)
- Spring profiles: `--spring.profiles.active=local` is default for bootRun
- `.sdkmanrc`: SDKMAN configuration for Java version management
