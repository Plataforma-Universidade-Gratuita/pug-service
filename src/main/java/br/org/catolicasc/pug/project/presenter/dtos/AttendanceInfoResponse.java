package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object describing attendance validation metadata and audit history.
 *
 * @param validatedBy the unique identifier of the validating account
 * @param validatedAt the timestamp when the attendance was validated
 * @param validatedAtFormatted the localized, human-readable validation timestamp
 * @param auditInfo the nested audit metadata payload
 */
public record AttendanceInfoResponse(
    UUID validatedBy,
    OffsetDateTime validatedAt,
    String validatedAtFormatted,
    AuditInfoResponse auditInfo) {}
