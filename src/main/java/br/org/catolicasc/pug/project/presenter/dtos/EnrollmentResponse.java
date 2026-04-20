package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.academic.presenter.dtos.StudentResponse;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Enrollment records.
 *
 * <p>This record provides a lightweight, identifier-centric view of an enrollment. Instead of
 * nesting the full {@link ProjectResponse} or {@link
 * StudentResponse}, it exposes only the {@code projectId} and
 * {@code studentId}, allowing API clients to resolve detailed project and student information on
 * demand via dedicated endpoints.
 *
 * @param projectId the unique identifier (UUIDv7) of the associated project
 * @param studentId the unique identifier (UUIDv7) of the associated student account
 * @param status the current lifecycle {@link EnrollmentStatus} of the enrollment
 * @param statusFormatted the localized, human-readable enrollment status (e.g., "Aprovado",
 *     "Concluído")
 * @param acceptedAt the exact timestamp when the enrollment was formally approved (may be {@code
 *     null} if still pending)
 * @param acceptedAtFormatted a localized, human-readable string representing {@code acceptedAt}
 *     (empty if {@code acceptedAt} is {@code null})
 * @param closingStatusAt the exact timestamp when the enrollment reached a terminal state (e.g.,
 *     Canceled, Completed), or {@code null} if it is not yet closed
 * @param closingStatusAtFormatted a localized, human-readable string representing {@code
 *     closingStatusAt} (empty if {@code closingStatusAt} is {@code null})
 * @param auditInfo the nested audit information containing creation and last update timestamps
 */
public record EnrollmentResponse(
    UUID projectId,
    UUID studentId,
    EnrollmentStatus status,
    String statusFormatted,
    OffsetDateTime acceptedAt,
    String acceptedAtFormatted,
    OffsetDateTime closingStatusAt,
    String closingStatusAtFormatted,
    AuditInfoResponse auditInfo) {}
