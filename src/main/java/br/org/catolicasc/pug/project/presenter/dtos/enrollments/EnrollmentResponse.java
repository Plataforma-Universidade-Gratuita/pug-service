package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import java.util.UUID;

/**
 * Canonical response payload returned by enrollment read endpoints.
 *
 * <p>This response identifies the enrollment through its composite key and nests the status and
 * lifecycle metadata instead of flattening every field at the top level.
 */
public record EnrollmentResponse(
    UUID projectId,
    UUID formerStudentId,
    EnrollmentStatusResponse status,
    EnrollmentInfoResponse enrollmentInfo) {}
