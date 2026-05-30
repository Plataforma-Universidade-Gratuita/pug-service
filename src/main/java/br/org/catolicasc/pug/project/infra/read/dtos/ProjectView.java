package br.org.catolicasc.pug.project.infra.read.dtos;

import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of a Project.
 *
 * <p>Following CQRS principles, this record is used exclusively for returning queried data to the
 * client. It provides a lightweight projection of the project, including the minimal partner entity
 * data required by the presenter contract, while still avoiding full aggregate hydration.
 *
 * @param id the unique identifier (UUIDv7) of the project
 * @param name the title or name of the project
 * @param entityId the unique identifier of the partner organization offering the project
 * @param entityName the registered name of the partner organization offering the project
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
    String entityName,
    String description,
    UUID creatorId,
    Integer maxParticipants,
    BigDecimal offeredHours,
    BigDecimal completedHours,
    ProjectStatus status,
    OffsetDateTime closedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {
  /**
   * Convenience constructor used by JPQL constructor expressions that still project status as a raw
   * database string.
   *
   * @param id the unique identifier (UUIDv7) of the project
   * @param name the title or name of the project
   * @param entityId the unique identifier of the partner organization offering the project
   * @param entityName the registered name of the partner organization offering the project
   * @param description the detailed description of the project
   * @param creatorId the unique identifier of the creator account
   * @param maxParticipants the maximum number of students allowed to enroll
   * @param offeredHours the total counterpart hours the project offers
   * @param completedHours the total counterpart hours that have been completed to date
   * @param status the raw project-status value returned by the database
   * @param closedAt the exact timestamp when the project reached a terminal state
   * @param createdAt the exact timestamp when the project record was created
   * @param updatedAt the exact timestamp when the project record was last modified
   */
  public ProjectView(
      UUID id,
      String name,
      UUID entityId,
      String entityName,
      String description,
      UUID creatorId,
      Integer maxParticipants,
      BigDecimal offeredHours,
      BigDecimal completedHours,
      String status,
      OffsetDateTime closedAt,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    this(
        id,
        name,
        entityId,
        entityName,
        description,
        creatorId,
        maxParticipants,
        offeredHours,
        completedHours,
        ProjectStatus.valueOf(status),
        closedAt,
        createdAt,
        updatedAt);
  }
}
