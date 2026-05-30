package br.org.catolicasc.pug.project.presenter.dtos.enrollments;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.time.OffsetDateTime;

/** Nested response describing enrollment lifecycle timestamps and audit metadata. */
public record EnrollmentInfoResponse(
    OffsetDateTime acceptedAt,
    String acceptedAtFormatted,
    OffsetDateTime closingStatusAt,
    String closingStatusAtFormatted,
    AuditInfoResponse auditInfo) {}
