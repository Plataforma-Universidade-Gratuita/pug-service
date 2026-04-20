package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Attendance data.
 *
 * @param id the unique identifier (UUIDv7) of the attendance record
 * @param projectId the unique identifier (UUIDv7) of the associated project
 * @param studentId the unique identifier (UUIDv7) of the student account
 * @param duration the recorded time duration
 * @param qrValidationHash the unique cryptographic hash of the QR code used
 * @param status the current validation status (enum)
 * @param statusFormatted the localized, human-readable attendance status
 * @param validatedById the unique identifier (UUIDv7) of the staff who validated the record
 * @param validatedAt the exact timestamp when the attendance was validated
 * @param validatedAtFormatted the human-readable, localized string of the validation date
 * @param auditInfo the nested audit information
 */
public record AttendanceResponse(
    UUID id,
    UUID projectId,
    UUID studentId,
    BigDecimal duration,
    String qrValidationHash,
    AttendanceStatus status,
    String statusFormatted,
    UUID validatedById,
    OffsetDateTime validatedAt,
    String validatedAtFormatted,
    AuditInfoResponse auditInfo) {}
