package br.org.catolicasc.pug.project.infra.read.dtos;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of a Project.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It provides a lightweight projection of the project, holding only the necessary
 * identifiers for related resources (Entity and Creator Account) to keep the response payload
 * optimized for JSON serialization.
 *
 * @param id the unique identifier (UUIDv7) of the project
 * @param name the title or name of the project
 * @param entityId the unique identifier of the partner organization offering the project
 * @param description the detailed description of the project
 * @param creatorId the unique identifier of the staff account who created the project
 * @param maxParticipants the maximum number of students allowed to enroll
 * @param offeredHours the total counterpart hours the project offers
 * @param completedHours the total counterpart hours that have been completed to date
 * @param status the current execution state of the project
 * @param closedAt the exact timestamp when the project reached a terminal state
 * @param createdAt the exact timestamp when the project record was created
 * @param updatedAt the exact timestamp when the project record was last modified
 */
public record ProjectView(
    UUID id,
    String name,
    UUID entityId,
    String description,
    UUID creatorId,
    Integer maxParticipants,
    BigDecimal offeredHours,
    BigDecimal completedHours,
    ProjectStatus status,
    OffsetDateTime closedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {}
