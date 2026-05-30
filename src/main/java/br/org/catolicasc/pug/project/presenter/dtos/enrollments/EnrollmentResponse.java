package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import java.util.UUID;

/** Canonical response payload returned by enrollment read endpoints. */
public record EnrollmentResponse(
    UUID projectId,
    UUID formerStudentId,
    EnrollmentStatusResponse status,
    EnrollmentInfoResponse enrollmentInfo) {}
