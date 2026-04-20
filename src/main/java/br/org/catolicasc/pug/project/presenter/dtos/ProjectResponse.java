package br.org.catolicasc.pug.project.presenter.dtos;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.shared.presenter.dtos.AuditInfoResponse;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) used as the standardized API JSON response for Projects.
 *
 * @param id the unique identifier (UUIDv7) of the project
 * @param name the title or name of the project
 * @param entityId the unique identifier of the partner organization
 * @param description the project description
 * @param createdBy the unique identifier of the creator
 * @param maxParticipants the maximum number of students allowed
 * @param offeredHours the total counterpart hours the project offers
 * @param completedHours the total counterpart hours already completed
 * @param status the current execution state
 * @param statusFormatted the localized project status string
 * @param closedAt the exact timestamp when the project was closed
 * @param closedAtFormatted the localized closure date
 * @param auditInfo the nested audit information
 */
public record ProjectResponse(
    UUID id,
    String name,
    UUID entityId,
    String description,
    UUID createdBy,
    Integer maxParticipants,
    BigDecimal offeredHours,
    BigDecimal completedHours,
    ProjectStatus status,
    String statusFormatted,
    OffsetDateTime closedAt,
    String closedAtFormatted,
    AuditInfoResponse auditInfo) {}
