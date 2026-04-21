# Test Suite — PUG Service

> Base package: `br.org.catolicasc.pug`  
> Java 21 · Quarkus 3.14 · JUnit 5 · Coverage target: **≥ 85 % per module**

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Test Categories](#test-categories)
  - [Unit Tests](#unit-tests)
  - [Integration Tests (QuarkusTest)](#integration-tests-quarkustest)
- [Package & Layer Conventions](#package--layer-conventions)
  - [Domain — Aggregates & Entities](#domain--aggregates--entities)
  - [Domain — Value Objects (VOs)](#domain--value-objects-vos)
  - [Service Layer](#service-layer)
  - [Service Processors / Utils](#service-processors--utils)
  - [Infrastructure — Mappers](#infrastructure--mappers)
  - [Infrastructure — Read Queries](#infrastructure--read-queries)
  - [Infrastructure — Audit System](#infrastructure--audit-system)
  - [Shared — Utilities, Exceptions, Presenters & Validation](#shared--utilities-exceptions-presenters--validation)
- [Test Support Classes](#test-support-classes)
  - [Builders](#builders)
  - [MongoTestInitializer](#mongotestinitializer)
- [Test Infrastructure & Databases](#test-infrastructure--databases)
- [Libraries & Dependencies](#libraries--dependencies)
- [Code Quality Gates](#code-quality-gates)
- [Running Tests](#running-tests)

---

## Architecture Overview

The project follows a **modular monolith** layout where each bounded context (`identity`, `academic`, `geo`, `partner`, `project`) has its own `domain`, `service`, `infra`, and `presenter` layers. The test tree mirrors this structure exactly:

```
src/test/java/br/org/catolicasc/pug/
├── builders/               # Reusable test builders (Builder pattern)
├── MongoTestInitializer.java
├── identity/
│   ├── domain/             # Aggregate & VO unit tests
│   │   └── vos/
│   ├── service/
│   │   ├── impl/           # Service unit tests (Mockito)
│   │   └── utils/          # Processor / factory unit tests
│   ├── infra/
│   │   ├── *MapperTest     # Mapper round-trip unit tests
│   │   └── read/impl/      # Query integration tests (@QuarkusTest)
│   └── presenter/mappers/  # REST presenter mapper tests
├── shared/
│   ├── domain/             # Domain base-class & enum tests
│   ├── exceptions/         # Custom exception tests
│   ├── http/               # Filter tests (e.g. CorrelationFilter)
│   ├── i18n/               # Internationalization tests
│   ├── infra/
│   │   ├── audit/          # Audit system integration tests
│   │   └── search/         # Hibernate Search integration tests
│   ├── presenter/          # Shared data presenter & exception mapper tests
│   ├── utils/              # Utility class tests
│   └── validation/         # Custom constraint validator tests
└── ...                     # (academic, geo, partner, project follow the same layout)
```

---

## Test Categories

### Unit Tests

| Aspect | Convention |
|---|---|
| **Runner** | `@ExtendWith(MockitoExtension.class)` — pure JUnit 5 + Mockito, **no** Quarkus container |
| **Naming** | `<ClassUnderTest>Test.java` |
| **Annotations** | Every class gets `@DisplayName("… Tests")`; methods get descriptive `@DisplayName` |
| **Grouping** | Related scenarios are grouped with `@Nested` inner classes (e.g. `class SaveTests`, `class FactoryTests`) |
| **Assertions** | **AssertJ** (`assertThat(…)`) for fluent, readable assertions |
| **Mocking** | `@Mock` + `@InjectMocks` from Mockito; `when(…).thenReturn(…)` / `verify(…)` |

Applies to: **domain**, **service/impl**, **service/utils**, **infra/mapper**, **shared/utils**, **shared/exceptions**, **shared/presenter**, **shared/validation**.

### Integration Tests (QuarkusTest)

| Aspect | Convention |
|---|---|
| **Runner** | `@QuarkusTest` — boots the full CDI container with test profile |
| **Injection** | `@Inject` for real beans; no mocking unless `@Mock @ApplicationScoped` alternative bean |
| **Database** | Real PostgreSQL, MongoDB, and Elasticsearch from **docker-compose** test containers (see [Test Infrastructure](#test-infrastructure--databases)) |
| **Async verification** | **Awaitility** (`await().atMost(…).untilAsserted(…)`) for event-driven flows (e.g. audit persistence) |

Applies to: **infra/read/impl** (query projections), **shared/infra/audit** (audit pub/sub), **shared/infra/search** (Hibernate Search).

---

## Package & Layer Conventions

### Domain — Aggregates & Entities

**Files:** `AccountTest`, `UserTest`, `AdminTest`

- Test the **factory method** (`Entity.factory(…)`) which is the only way to create domain objects.
- Assert that valid inputs produce an entity with `hasFieldErrors() == false`.
- Assert that **invalid / missing inputs** populate `getFieldErrors()` with the correct `FieldErrorCode` enum values.
- Test **domain behaviour methods** (e.g. `account.deactivate()`) and verify state mutations + audit timestamps.

```java
@Test
void shouldCollectErrors() {
    Account account = Account.factory(null, null, null, "");
    assertThat(account.hasFieldErrors()).isTrue();
    assertThat(account.getFieldErrors())
        .contains(INVALID_USER_ID_BLANK, INVALID_EMAIL_BLANK, ...);
}
```

### Domain — Value Objects (VOs)

**Files:** `CpfTest`, `EmailTest`

- Grouped under `domain/vos/`.
- Test the **factory static method** for valid creation, sanitization/normalization, and rejection of invalid input.
- Use `@Nested` inner classes to separate *Factory and Validation* scenarios.
- Each VO self-validates and reports errors via `hasFieldErrors()` / `getFieldErrors()` (no exceptions thrown).

### Service Layer

**Files:** `AccountServiceImplTest`, `UserServiceImplTest`, `AdminServiceImplTest`, `AccountReadServiceImplTest`, etc.

- Pure unit tests with `@ExtendWith(MockitoExtension.class)`.
- Repositories and collaborating services are `@Mock`; the service under test is `@InjectMocks`.
- Scenarios grouped by method: `@Nested class SaveTests`, `@Nested class DeleteTests`, `@Nested class GetTests`.
- Verifies:
  - Correct delegation to repository (`verify(repo).persist(…)`).
  - Audit event firing (`verify(audit).fireCreate(…)`).
  - Exception throwing for business rules (`assertThrows(DuplicateResourceException.class, …)`).
  - Exception throwing for missing resources (`assertThrows(ResourceNotFoundException.class, …)`).

### Service Processors / Utils

**Files:** `AccountProcessorTest`, `UserProcessorTest`, `AdminProcessorTest`

- Test static processor/factory methods that translate raw input into validated domain objects.
- Verify both **create** and **update** paths produce correctly built entities.

### Infrastructure — Mappers

**Files:** `AccountMapperTest`, `UserMapperTest`, `AdminMapperTest`

- **Round-trip mapping** tests: `Domain → Entity → Domain`.
- Assert that all meaningful fields survive the conversion without data loss.
- Pure unit tests (no container required).

```java
AccountEntity entity = AccountMapper.toEntity(account);
Account mapped = AccountMapper.toDomain(entity);
assertThat(mapped.getId()).isEqualTo(account.getId());
```

### Infrastructure — Read Queries

**Files:** `AccountQueriesImplTest`, `UserQueriesImplTest`, `AdminQueriesImplTest`

- **Integration tests** annotated with `@QuarkusTest`.
- Use `@Inject` to get the real query implementation wired to the test PostgreSQL database.
- Flyway migrations + `clean-at-start=true` ensure a fresh schema with seed data on every run.
- Test read projections (DTOs/views) and sorted list queries.

### Infrastructure — Audit System

**File:** `AuditSystemTest`

- `@QuarkusTest` integration test.
- Publishes an audit event via `AuditPublisher` and asserts it was persisted by `AuditListener` in MongoDB.
- Uses **Awaitility** to handle async CDI event propagation.
- `@AfterEach` cleanup deletes all audit documents.

### Shared — Utilities, Exceptions, Presenters & Validation

Located under `shared/`:

| Sub-package | What is tested |
|---|---|
| `utils/` | `StringUtilsTest`, `CollectionUtilsTest`, `DiffUtilsTest`, `PresenterUtilsTest` — pure function tests |
| `exceptions/` | `AppValidationExceptionTest` — custom exception construction |
| `http/` | `CorrelationFilterTest` — request correlation ID filter |
| `i18n/` | `I18nTest` — message bundle resolution for `en_US` and `pt_BR` |
| `validation/` | `UuidV7Test` — custom Bean Validation constraint |
| `presenter/mappers/` | `SharedDataPresenterTest` — shared DTO mapping |
| `presenter/rest/mappers/` | Exception-to-HTTP-response mapper tests (`BusinessRuleExceptionMapperTest`, `ConstraintViolationMapperTest`, `DuplicateResourceExceptionMapperTest`, `NotAuthorizedExceptionMapperTest`, `ResourceNotFoundExceptionMapperTest`, `SystemExceptionMappersTest`) |
| `domain/` | `DomainErrorAndAuditInfoTest` — base domain class behaviour |
| `domain/enums/` | `EnumBundleKeysTest` — enum i18n key consistency |

---

## Test Support Classes

### Builders

Located at `br.org.catolicasc.pug.builders`:

| Builder | Purpose |
|---|---|
| `AccountBuilder` | Fluent builder for `Account` domain objects with sensible defaults |
| `UserBuilder` | Fluent builder for `User` domain objects |
| `AdminBuilder` | Fluent builder for `Admin` domain objects |

Usage:
```java
Account acc = AccountBuilder.anAccount()
    .forUser(userId)
    .withEmail("custom@pug.com")
    .withType(AccountType.ADMIN)
    .build();
```

### MongoTestInitializer

A `@Mock @ApplicationScoped` alternative bean that observes `StartupEvent` and clears all MongoDB audit documents before each test suite run, ensuring test isolation.

---

## Test Infrastructure & Databases

Tests connect to **real containerized databases** defined in `docker-compose.yml` (no Testcontainers auto-provisioning — Quarkus Dev Services are explicitly **disabled**):

| Service | Test Port | Database / Index |
|---|---|---|
| **PostgreSQL** | `5434` | `pug_test` (user: `pug_test`) |
| **MongoDB** | `27019` | `pug_test_audit` (user: `pug_test`) |
| **Elasticsearch** | `9202` | Managed by Hibernate Search (`strategy=create`) |

Key `application.properties` (test profile) settings:

- `quarkus.flyway.migrate-at-start=true` + `clean-at-start=true` — fresh schema every run.
- `quarkus.hibernate-search-orm.schema-management.strategy=create` — recreates ES indexes.
- Isolated JWT/security config with test-only keys and pepper.

> **Pre-requisite:** Run `docker-compose up -d` before executing integration tests.

---

## Libraries & Dependencies

| Library | Version | Role |
|---|---|---|
| **JUnit 5** (`quarkus-junit5`) | Quarkus BOM | Test runner & lifecycle |
| **Mockito** (`mockito-core`, `mockito-junit-jupiter`, `quarkus-junit5-mockito`) | Quarkus BOM | Mocking & stubbing for unit tests |
| **AssertJ** (`assertj-core`) | 3.26.3 | Fluent assertions |
| **REST Assured** (`rest-assured`) | Quarkus BOM | HTTP endpoint testing (integration tests) |
| **Hamcrest** (`hamcrest`) | 2.2 | Matcher library (used with REST Assured) |
| **Awaitility** (`awaitility`) | 4.2.2 | Async assertion polling (audit events) |
| **JaCoCo** (`quarkus-jacoco`, `jacoco-maven-plugin` 0.8.14) | — | Code coverage collection & reporting |

---

## Code Quality Gates

| Tool | Phase | Scope |
|---|---|---|
| **JaCoCo** | `verify` | Merges unit + Quarkus coverage (`jacoco-merged.exec`); **minimum target: 85 % per module** |
| **Spotless** (Google Java Format) | `validate` | Auto-formats all Java files |
| **Checkstyle** (`google_checks.xml`) | `verify` | Enforces Google style; fails build on violation |
| **SpotBugs** (effort=max, threshold=Low) | `verify` | Static bug detection; fails build on any finding |
| **Maven Enforcer** | — | Requires Java ≥ 21 |

JaCoCo merges two execution data files to combine:
1. **`jacoco.exec`** — collected during Surefire (unit tests).
2. **`jacoco-quarkus.exec`** — collected during `@QuarkusTest` runs.

---

## Running Tests

```bash
# Start test infrastructure
docker-compose up -d

# Run all tests (unit + integration)
./mvnw verify

# Run only unit tests (Surefire)
./mvnw test

# Run a specific test class
./mvnw test -Dtest=AccountServiceImplTest

# Generate coverage report (after verify)
# open target/jacoco-report/index.html
```

