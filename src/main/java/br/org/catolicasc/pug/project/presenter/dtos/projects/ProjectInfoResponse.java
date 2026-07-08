package br.org.catolicasc.pug.project.presenter.dtos.projects;

import br.org.catolicasc.pug.identity.presenter.dtos.accounts.AccountSimpleComplexSearchResponse;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Nested response DTO that groups the operational metadata associated with a project.
 *
 * @param createdBy the unique identifier of the account that created the project
 * @param maxParticipants the maximum number of participants accepted by the project
 * @param currentParticipants the current number of participants in the project
 * @param offeredHours the total counterpart hours offered by the project
 * @param completedHours the total counterpart hours completed so far
 * @param closedAt the exact timestamp when the project reached a terminal state
 * @param closedAtFormatted the localized closure timestamp
 * @param auditInfo the nested audit information for the project row
 */
public record ProjectInfoResponse(
    AccountSimpleComplexSearchResponse createdBy,
    Integer maxParticipants,
    Long currentParticipants,
    BigDecimal offeredHours,
    BigDecimal completedHours,
    OffsetDateTime closedAt,
    String closedAtFormatted,
    AuditInfoResponse auditInfo) {}
