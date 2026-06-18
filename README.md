# PUG Service

> 📦 Release: **v1.0.0**

`pug-service` is the backend service for the PUG platform. It is a single Quarkus application that centralizes authentication, partner management, academic records, project execution, geo reference data, and shared infrastructure such as auditing, localization, validation, and API error handling. The repository exposes the platform’s business API and persists operational data in PostgreSQL with audit data in MongoDB.

## 🚀 Release 1.0.0

Release `1.0.0` marks the current stable backend surface implemented in the repository. This version provides the main PUG business modules, JWT-based identity flows, PostgreSQL-backed domain persistence, MongoDB-backed audit logging, Flyway migrations, package-level coverage enforcement, and image/verification workflows for CI.

Main capabilities present in this release:

- shared Quarkus backend for the full PUG domain surface
- JWT login, refresh-token sessions, and identity administration
- academic, geo, partner, and project management APIs
- PostgreSQL persistence with Flyway migrations
- MongoDB audit logging
- CI verification, container image build validation, and GHCR publish workflow

## ✨ Features

### Shared platform

- standardized API envelope and exception mapping
- correlation IDs and structured error handling
- localization support
- audit persistence to MongoDB
- pagination and request validation helpers

### Identity

- auth login and refresh-token flows
- accounts
- admins
- users

### Geo

- read-only city catalog

### Partner

- partner entities
- partner staff

### Academic

- areas of expertise
- courses
- former students

### Project

- projects
- project-area associations
- enrollments
- attendances

## 🏗️ Architecture overview

At a high level, `pug-service` is a single-module Quarkus application organized by domain package. HTTP requests enter JAX-RS resources, flow through service/query layers inside the domain packages, and persist to PostgreSQL while audit events are stored in MongoDB.

Core layers:

- **HTTP/API layer:** `src/main/java/br/org/catolicasc/pug/**/resources`
- **Domain services and queries:** `src/main/java/br/org/catolicasc/pug/**`
- **Persistence:** Hibernate ORM + Panache + Flyway
- **Audit store:** MongoDB + Panache
- **Shared infrastructure:** `shared` package

```mermaid
flowchart LR
    Clients[Clients]
    Api[REST resources]
    Domains[shared + geo + identity + partner + academic + project]
    Pg[(PostgreSQL)]
    Mg[(MongoDB audit)]

    Clients --> Api
    Api --> Domains
    Domains --> Pg
    Domains --> Mg
```

Important architectural properties:

- all business modules live inside one Quarkus application
- `shared` contains cross-cutting infrastructure rather than a separate deployable service
- PostgreSQL stores the main relational domain data
- MongoDB is used as the audit boundary in the repository
- JaCoCo coverage checks are enforced per package in `verify`

## 🧰 Tech stack

- **Language:** Java 21
- **Framework:** Quarkus 3.14.4
- **Build tool:** Maven Wrapper (`mvnw`)
- **Primary database:** PostgreSQL 16
- **Audit store:** MongoDB 7
- **ORM:** Hibernate ORM + Panache
- **Migrations:** Flyway
- **Auth:** SmallRye JWT
- **Validation:** Hibernate Validator
- **Observability:** SmallRye OpenAPI, SmallRye Health, Micrometer, JSON logging
- **Testing:** JUnit 5, Quarkus Test, Mockito, RestAssured, AssertJ, Awaitility, JaCoCo
- **Containerization:** Docker multi-stage build
- **CI/CD tooling:** GitHub Actions

## ▶️ Getting started

### Prerequisites

- Java `21`
- Docker
- a shell capable of running `./mvnw`

### Setup

Start the local infrastructure declared in [docker-compose.yml](docker-compose.yml):

```bash
docker compose up -d postgres mongodb
```

Then start the application in dev mode:

```bash
./mvnw quarkus:dev
```

### Environment and local runtime notes

Dedicated `.env` files are not part of the repository.

The current dev profile uses fixed local settings in [src/main/resources/application-dev.properties](src/main/resources/application-dev.properties), including:

| Setting | Current local value |
| --- | --- |
| PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5433/pug` |
| PostgreSQL user | `pug` |
| PostgreSQL password | `pug` |
| MongoDB connection | `mongodb://pug:pug@localhost:27018` |
| MongoDB database | `pug_audit` |
| Swagger UI path | `/swagger-ui` |

Useful local commands:

```bash
./mvnw test
./mvnw clean verify
./mvnw clean package
./mvnw package -Dnative
```

## 📦 Version 1.0.0 Notes

- **Initial stable release:** this README documents the current `pug-service` repository as release `1.0.0`
- **Main delivered modules/features:** shared infrastructure, identity, geo, partner, academic, and project modules running inside one Quarkus service
- **Known limitations visible in the repo:**
  - the `geo` module is read-only in the repository
  - dedicated `@QuarkusIntegrationTest` classes are not part of the repository
  - downstream deployment automation was not found; current workflows stop at verify/build/publish
- **Compatibility/runtime expectations:**
  - Java `21` is required by the build
  - the local dev profile expects PostgreSQL on `5433` and MongoDB on `27018`
  - the packaged application runs on port `8080`

## 🗂️ Project structure

```text
pug-service/
├── .github/
│   └── workflows/
├── requests/
├── src/
│   ├── main/
│   │   ├── java/br/org/catolicasc/pug/
│   │   │   ├── shared/
│   │   │   ├── geo/
│   │   │   ├── identity/
│   │   │   ├── partner/
│   │   │   ├── academic/
│   │   │   └── project/
│   │   └── resources/
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── mvnw
└── pom.xml
```

## 🔗 Links to deeper documentation

- [Expanded workspace documentation](https://github.com/Plataforma-Universidade-Gratuita/pug-docs/blob/main/pug-service/README.md)
- [Architecture notes](https://github.com/Plataforma-Universidade-Gratuita/pug-docs/blob/main/pug-service/ARCHITECTURE.md)
- [Development notes](https://github.com/Plataforma-Universidade-Gratuita/pug-docs/blob/main/pug-service/DEVELOPMENT.md)
- [Testing notes](https://github.com/Plataforma-Universidade-Gratuita/pug-docs/blob/main/pug-service/TESTS.md)
- [CI/CD notes](https://github.com/Plataforma-Universidade-Gratuita/pug-docs/blob/main/pug-service/CICD.md)

