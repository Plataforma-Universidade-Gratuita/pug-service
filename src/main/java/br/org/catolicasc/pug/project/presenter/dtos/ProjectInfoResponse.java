package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Nested response DTO that groups the operational metadata associated with a project.
 *
 * @param createdBy the unique identifier of the account that created the project
 * @param maxParticipants the maximum number of participants accepted by the project
 * @param offeredHours the total counterpart hours offered by the project
 * @param completedHours the total counterpart hours completed so far
 * @param closedAt the exact timestamp when the project reached a terminal state
 * @param closedAtFormatted the localized closure timestamp
 * @param auditInfo the nested audit information for the project row
 */
public record ProjectInfoResponse(
    UUID createdBy,
    Integer maxParticipants,
    BigDecimal offeredHours,
    BigDecimal completedHours,
    OffsetDateTime closedAt,
    String closedAtFormatted,
    AuditInfoResponse auditInfo) {}
