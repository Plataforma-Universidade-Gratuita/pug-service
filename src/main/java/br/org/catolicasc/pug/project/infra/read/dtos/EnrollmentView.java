package br.org.catolicasc.pug.project.infra.read.dtos;

import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a lightweight, read-only view of an Enrollment.
 *
 * <p>Following CQRS principles, this record is intentionally minimal and exposes only identifiers
 * and lifecycle metadata. It is optimized for list and filter operations, allowing clients to
 * resolve detailed project and formerStudent information on demand via dedicated endpoints.
 *
 * @param projectId the unique identifier (UUID) of the associated project
 * @param studentId the unique identifier (UUID) of the associated formerStudent account
 * @param status the current lifecycle {@link EnrollmentStatus} of the enrollment
 * @param createdAt the exact timestamp when the enrollment record was initially created
 * @param updatedAt the exact timestamp when the enrollment record was last modified
 * @param acceptedAt the timestamp when the enrollment was formally approved (may be {@code null})
 * @param closingStatusAt the timestamp when the enrollment reached a terminal state (may be {@code
 *     null})
 */
public record EnrollmentView(
    UUID projectId,
    UUID studentId,
    EnrollmentStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    OffsetDateTime acceptedAt,
    OffsetDateTime closingStatusAt) {
  /**
   * Construtor auxiliar para mapeamento JPA.
   *
   * @param projectId o identificador do projeto
   * @param studentId o identificador do estudante
   * @param status a string representando o status da enrollment para conversão em enum
   * @param createdAt o timestamp de criação
   * @param updatedAt o timestamp da última modificação
   * @param acceptedAt o timestamp de aceitação
   * @param closingStatusAt o timestamp de encerramento
   */
  public EnrollmentView(
      UUID projectId,
      UUID studentId,
      String status,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt,
      OffsetDateTime acceptedAt,
      OffsetDateTime closingStatusAt) {
    this(
        projectId,
        studentId,
        EnrollmentStatus.valueOf(status),
        createdAt,
        updatedAt,
        acceptedAt,
        closingStatusAt);
  }
}

