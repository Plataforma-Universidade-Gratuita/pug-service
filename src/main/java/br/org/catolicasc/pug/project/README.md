# Project Module

## Overview

The `project` bounded context handles:

- community service projects offered by partner entities
- project-school associations
- student enrollments in projects
- attendance registration and validation

It is the operational core of the platform and depends on `identity`, `academic`, `partner`, and `shared`.

## Main aggregates

- `Project`
- `ProjectSchool`
- `Enrollment`
- `Attendance`

Important enums:

- `ProjectStatus`: `PLANNED`, `IN_PROGRESS`, `ON_HOLD`, `COMPLETED`, `CANCELED`
- `EnrollmentStatus`: `PENDING`, `APPROVED`, `COMPLETED`, `CANCELED`, `EXITED`, `REJECTED`, `REMOVED`
- `AttendanceStatus`: `WAITING`, `PRESENT`, `ABSENT`

## Internal structure

```text
presenter/
  ProjectResource
  ProjectSchoolResource
  SchoolProjectResource
  EnrollmentResource
  AttendanceResource
domain/
  Project, ProjectSchool, Enrollment, Attendance
service/
  ProjectService
  ProjectSchoolService
  EnrollmentService
  AttendanceService
  *ReadService
infra/
  persistence/
  read/
  *Mapper
```

## HTTP contract

### Projects - `/projects`

- `GET /projects`
  - list all
  - supports `?q=`, `?entityId=`, `?createdBy=`
- `GET /projects/{id}`
- `POST /projects`
- `PUT /projects/{id}`
- `PATCH /projects/{id}`
  - supports partial field updates
  - supports lifecycle transitions through body `status`
- `DELETE /projects/{id}`

Examples of lifecycle patches:

- `{ "status": "IN_PROGRESS" }`
- `{ "status": "ON_HOLD" }`
- `{ "status": "COMPLETED" }`
- `{ "status": "CANCELED" }`

`retake` is represented by setting status back to `IN_PROGRESS`, not by a dedicated route.

### Project-school associations

Project side:

- `GET /projects/{projectId}/schools`
- `POST /projects/{projectId}/schools`
- `DELETE /projects/{projectId}/schools/{schoolId}`
- `DELETE /projects/{projectId}/schools`

School side:

- `GET /academic/schools/{schoolId}/projects`
- `DELETE /academic/schools/{schoolId}/projects`

Association creation body:

```json
{
  "schoolIds": [
    "00000000-0000-0000-0000-000000000001"
  ]
}
```

### Enrollments

Collection and self routes:

- `GET /projects/enrollments`
  - supports `?projectId=` and `?studentId=`
- `GET /projects/enrollments/me`

Nested project routes:

- `GET /projects/{projectId}/enrollments/{studentId}`
- `GET /projects/{projectId}/enrollments/me`
- `POST /projects/{projectId}/enrollments`
- `PATCH /projects/{projectId}/enrollments/{studentId}`
- `PATCH /projects/{projectId}/enrollments/me`
- `DELETE /projects/{projectId}/enrollments/{studentId}`

Enrollment creation does not use a JSON body. The project identifier comes from the route.

Enrollment transitions are driven by request body:

```json
{
  "status": "APPROVED"
}
```

Supported transition payloads depend on caller and current state, but the public contract uses one `PATCH` route with statuses such as:

- `APPROVED`
- `REJECTED`
- `COMPLETED`
- `CANCELED`
- `REMOVED`
- `EXITED`

### Attendances - `/projects/attendances`

- `GET /projects/attendances`
  - supports `?projectId=` and `?studentId=`
- `GET /projects/attendances/{id}`
- `POST /projects/attendances`
- `PATCH /projects/attendances/{id}/validate`
- `DELETE /projects/attendances/{id}`

## Business rules

- project names must be unique per partner entity
- a project cannot be deleted while dependent data still blocks the operation
- project status changes follow the domain lifecycle rules
- enrollment status changes follow a strict state machine
- attendance QR hashes must remain unique
- attendance validation records who validated and when

## Persistence model

Core relational tables:

- `projects`
- `projects_by_schools`
- `enrollments`
- `attendances`

The read side uses dedicated query services and Hibernate Search where applicable.
