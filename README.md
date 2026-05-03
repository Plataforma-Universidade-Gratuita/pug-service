# DEVELOPMENT GUIDANCE

This file is the working development contract for `pug-service`.

Use it for day-to-day implementation rules, repo conventions, validation steps, endpoint rules, and service-layer constraints. Keep broader architectural overview, module overview, and test reporting documentation in `../pug-docs/pug-service`.

## Project baseline

- `pug-service` is a Quarkus 3.14.4 modular monolith built on Java 21.
- It is the canonical backend for the PUG platform.
- The public HTTP contract is versioned under `/v1`.
- The current bounded contexts are:
  - `academic`
  - `geo`
  - `identity`
  - `partner`
  - `project`
  - `shared`
- Primary infrastructure:
  - PostgreSQL for transactional data
  - MongoDB for audit-related persistence
  - Elasticsearch through Hibernate Search for indexed queries
- Core platform concerns already present in the codebase:
  - JWT authentication and refresh tokens
  - role-based authorization
  - CQRS-style split between read and write services
  - validation and i18n-backed error messaging
  - Flyway schema migrations
  - JaCoCo, Spotless, Checkstyle, SpotBugs, and Maven Enforcer quality gates

## Non-negotiable rules

- Keep broader system and module overview docs in `../pug-docs/pug-service`, not in source folders here.
- Keep canonical module root endpoint strings under `<module>/constants/<Module>ApiPaths.java`.
- Keep resource method-level `@Path(...)` fragments as literals such as `"/{id}"`, `"/me"`, `"/bulk"`, `"/logout"`.
- Do not move method-level path fragments into constants. IDE endpoint tooling and path inspection degrade when that indirection is pushed too far.
- Use `shared/constants/ApiVersions.java` only when the version prefix is needed outside endpoint annotations, such as URI building.
- Treat the presenter layer as the public contract boundary. If a route, payload, or response changes, update:
  - presenter resource
  - presenter DTOs and mappers
  - relevant tests
  - Bruno requests under `requests/`
  - documentation in `../pug-docs/pug-service`
- Use `ApiEnvelope` for response bodies that return content.
- Use `204 No Content` for endpoints whose contract is void.
- Prefer query parameters on collection `GET` endpoints over ad hoc `by-*` route proliferation, except where the existing contract intentionally preserves a special route such as `geo/cities/by-ibge/{ibgeCode}`.
- Prefer nested routes for relationship-oriented operations, for example project enrollments and project-school associations.
- Do not bypass module boundaries by reaching directly into another module's infrastructure layer.
- Keep changes scoped. Avoid opportunistic cross-module refactors unless the task actually requires them.

## Current tech and runtime facts

- Build tool: Maven wrapper (`./mvnw`)
- Runtime baseline:
  - Java 21
  - Quarkus 3.14.4
- Main Quarkus capabilities in use:
  - `quarkus-rest`
  - `quarkus-rest-jackson`
  - `quarkus-hibernate-validator`
  - `quarkus-smallrye-jwt`
  - `quarkus-hibernate-orm-panache`
  - `quarkus-jdbc-postgresql`
  - `quarkus-flyway`
  - `quarkus-mongodb-client`
  - `quarkus-mongodb-panache`
  - `quarkus-hibernate-search-orm-elasticsearch`
  - `quarkus-smallrye-openapi`
  - `quarkus-smallrye-health`
  - `quarkus-micrometer`
  - `quarkus-logging-json`
  - `quarkus-scheduler`
- Frequently used commands:
  - `./mvnw quarkus:dev`
  - `./mvnw compile`
  - `./mvnw test`
  - `./mvnw verify`
  - `./mvnw spotless:apply`
- Local development infrastructure currently configured in the checked-in profiles:
  - dev PostgreSQL: `localhost:5433`
  - dev MongoDB: `localhost:27018`
  - dev Elasticsearch: `localhost:9201`
  - test PostgreSQL: `localhost:5434`
  - test MongoDB: `localhost:27019`
  - test Elasticsearch: `localhost:9202`

## High-level folder contract

- `src/main/java/br/org/catolicasc/pug/academic`: academic bounded context
- `src/main/java/br/org/catolicasc/pug/geo`: geo bounded context
- `src/main/java/br/org/catolicasc/pug/identity`: identity bounded context
- `src/main/java/br/org/catolicasc/pug/partner`: partner bounded context
- `src/main/java/br/org/catolicasc/pug/project`: project bounded context
- `src/main/java/br/org/catolicasc/pug/shared`: cross-cutting support code
- `src/main/resources`: Quarkus config, Flyway migrations, i18n bundles, scripts
- `src/test`: tests and test resources
- `requests`: Bruno requests grouped by domain
- `generate-test-report.py`: coverage report post-processing for the docs repo

## Module structure contract

Each bounded context should stay close to this pattern:

- `constants/`
  - canonical module-level route roots and related contract constants
- `domain/`
  - aggregate roots
  - value objects
  - enums
  - repository interfaces
  - business invariants
- `service/`
  - application services
  - command-side orchestration
  - query-side orchestration
  - service DTOs and processors
- `infra/`
  - persistence entities
  - repository implementations
  - read-model implementations
  - domain/entity mappers
- `presenter/`
  - JAX-RS resources
  - request/response DTOs
  - presenter mappers

Do not collapse these layers casually. The separation is intentional and already reflected across tests, requests, and docs.

## HTTP and presenter conventions

### Endpoint path rules

- Class-level `@Path(...)` should use the root constants from the module `*ApiPaths` classes.
- Method-level path fragments stay literal in the resource classes.
- Keep the canonical version prefix as `/v1`.
- Keep collection bases plural and stable.
- Prefer these route styles:
  - collection filters through query params
  - item lookup through `/{id}`
  - authenticated self routes through `/me`
  - relationship operations through nested routes

### Response rules

- `GET` returns `200` with `ApiEnvelope.ok(...)`.
- `POST` that creates resources returns `201` with `ApiEnvelope.created(...)` and a `Location` header.
- `PUT` returns the updated representation unless the existing contract explicitly says otherwise.
- `PATCH` follows the actual endpoint contract:
  - some routes return `204`
  - some routes return the updated representation
- `DELETE` returns `204` when the contract is void.

### `@Consumes` rules

- Do not blindly place `@Consumes(MediaType.APPLICATION_JSON)` at class level when a resource mixes:
  - body-based endpoints
  - no-body endpoints
- For mixed resources, prefer method-level `@Consumes` on the methods that actually need JSON.
- This matters for route matching on no-body operations such as nested enrollment creation.

### Security rules

- Put authorization close to the resource methods through `@RolesAllowed`, `@Authenticated`, or `@PermitAll`.
- Keep role intent explicit in the presenter layer.
- Do not hide authorization decisions inside helper abstractions when a plain annotation states the contract clearly.

## DTO and mapper conventions

- Request and response contracts live under `presenter/dtos`.
- Presenter transformations live under `presenter/mappers`.
- Presenter DTOs should match the public HTTP contract, not the internal persistence model.
- Use record-style DTOs where the project already follows that pattern.
- If a route changes shape:
  - update the request DTO
  - update the response DTO if needed
  - update presenter mapping
  - update tests and Bruno payloads
- Do not leak JPA entities or read-side view types directly into HTTP responses.

## Domain and service conventions

### Domain

- Put business rules in the domain model whenever they are domain rules, not transport concerns.
- Keep domain objects free from presenter concerns and HTTP details.
- Use domain enums and value objects for constrained concepts such as statuses, CPF, CNPJ, email, and identifiers where the module already follows that pattern.
- Let the domain own lifecycle transitions where possible.

### Services

- Keep write orchestration in write services.
- Keep query orchestration in read services.
- Do not turn resources into service substitutes.
- Do not move domain transition logic into resources just because the endpoint is a `PATCH`.
- Service-layer DTOs belong under `service/dtos` when the application layer needs its own command objects.
- Processor or translator helpers belong in service utility packages when they encapsulate reusable validation or exception translation behavior.

### CQRS shape

- Reads should prefer read services backed by read models under `infra/read`.
- Writes should go through the command side and domain aggregates.
- Keep this split visible in naming:
  - `*Service` for commands
  - `*ReadService` for queries

## Persistence and read-model conventions

- JPA entities live under `infra/persistence`.
- Repository implementations stay in infrastructure.
- Keep explicit mapper layers between:
  - domain and persistence
  - read view and presenter response
- Read-model DTOs under `infra/read/dtos` are query-side shapes, not public API shapes.
- Search-backed query behavior belongs on the read side.
- Flyway migrations are the source of truth for schema evolution under `src/main/resources/db/migration`.
- Do not patch schema manually and leave migrations behind. If the schema changes, add or update the migration path accordingly.

## Validation, errors, and i18n

- Bean validation belongs on request DTOs and relevant method inputs.
- Domain validation belongs in the domain or application layer, not only in HTTP annotations.
- Use shared exception types from `shared.exceptions` for consistent error mapping.
- Keep message resolution aligned with the existing i18n flow:
  - `messages_en_US.properties`
  - `messages_pt_BR.properties`
  - `ValidationMessages_en_US.properties`
  - `ValidationMessages_pt_BR.properties`
- Avoid scattering hardcoded user-facing validation text across resources and services when an existing error code and bundle key pattern already exists.
- Locale-aware presenter mapping should keep using the established helper flow with request headers and `I18n`.

## Endpoint contract and path-constant rules

- Keep the canonical root strings in:
  - `AcademicApiPaths`
  - `GeoApiPaths`
  - `IdentityApiPaths`
  - `PartnerApiPaths`
  - `ProjectApiPaths`
- Keep `ApiVersions.V1` in `shared/constants/ApiVersions.java`.
- Use `ApiVersions.V1` for non-annotation cases such as URI construction.
- Do not reintroduce module-local `VERSION` constants unless there is a clear need.
- Do not centralize every tiny subpath fragment. The current rule is:
  - root paths in `*ApiPaths`
  - method fragments inline in the resources

## Testing and request collections

- Resource integration tests are the contract guard for endpoint behavior.
- When an endpoint changes, update:
  - resource tests
  - request builders if affected
  - Bruno requests
  - docs in `pug-docs`
- Bruno collections live under `requests/` and are grouped by domain.
- Keep the request collection aligned with the backend contract even when old filenames still describe the previous action wording.
- Coverage reporting flow:
  - JaCoCo HTML/XML/CSV are produced under `target/jacoco-report`
  - `generate-test-report.py` transforms coverage output into the docs report
  - generated Markdown output now belongs in `../pug-docs/pug-service/tests/coverage_report.md`

For broader testing guidance, use `../pug-docs/pug-service/tests/README.md`.

## Documentation contract

- Broad docs belong in `../pug-docs/pug-service`.
- Current target layout there is:
  - `README.md`
  - `m-academic/README.md`
  - `m-geo/README.md`
  - `m-identity/README.md`
  - `m-partner/README.md`
  - `m-project/README.md`
  - `m-shared/README.md`
  - `tests/README.md`
  - `tests/coverage_report.md`
- This repository root `README.md` is for development guidance only.
- Do not rebuild architectural overviews here after they were intentionally moved out.

## Common change checklists

### When adding or changing an endpoint

1. Update the resource method and security annotations.
2. Update or add request/response DTOs.
3. Update presenter mappers.
4. Update Bruno requests under `requests/`.
5. Update resource tests and any affected builders.
6. Update the relevant docs in `../pug-docs/pug-service`.
7. If the route root changed, update the module `*ApiPaths` constant.

### When changing a domain rule

1. Update the domain object first.
2. Adjust write-service orchestration only if needed.
3. Update tests closest to the rule:
   - domain tests first
   - then service/resource tests if the change propagates outward
4. Review read-side projections only if the rule changes observable query behavior.

### When changing path constants

1. Keep module root constants in `constants/`.
2. Do not push method fragments into constants just for uniformity.
3. Check URI builders that may rely on the version prefix.
4. Update docs and Bruno requests in the same pass.

### When moving documentation

1. Keep source-code guidance here.
2. Keep broader docs in `pug-docs`.
3. Fix relative links when files move.
4. Update generators that emit Markdown artifacts to avoid writing back into `pug-service`.

## Practical standard for changes

When working in this repository, the expected level of finish is:

- contract updated
- code updated
- tests updated
- Bruno updated
- docs updated

If one of those is intentionally skipped, it should be an explicit choice, not an accidental gap.
