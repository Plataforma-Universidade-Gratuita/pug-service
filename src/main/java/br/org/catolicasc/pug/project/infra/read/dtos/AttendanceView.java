package br.org.catolicasc.pug.project.infra.read.dtos;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) representing a read-only view of an Attendance record.
 *
 * <p>Following CQRS principles, this view is flattened to provide essential identifiers, minimizing
 * data payload size.
 *
 * @param id the unique identifier (UUIDv7) of the attendance record
 * @param projectId the unique identifier of the associated project
 * @param studentId the unique identifier of the associated formerStudent account
 * @param duration the recorded time duration
 * @param qrValidationHash the unique hash of the QR code
 * @param status the current validation status
 * @param validatedById the unique identifier of the staff who validated the record
 * @param validatedAt the timestamp of validation
 * @param createdAt the timestamp of creation
 * @param updatedAt the timestamp of last modification
 */
public record AttendanceView(
    UUID id,
    UUID projectId,
    UUID studentId,
    BigDecimal duration,
    String qrValidationHash,
    AttendanceStatus status,
    UUID validatedById,
    OffsetDateTime validatedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {
  /**
   * Construtor auxiliar para mapeamento JPA.
   *
   * @param id o identificador único (UUIDv7) da attendance
   * @param projectId o identificador do projeto associado
   * @param studentId o identificador da conta do estudante
   * @param duration a duração registrada
   * @param qrValidationHash o hash de validação do QR code
   * @param status a string representando o status da attendance para conversão em enum
   * @param validatedById o identificador de quem validou
   * @param validatedAt o timestamp da validação
   * @param createdAt o timestamp de criação
   * @param updatedAt o timestamp da última modificação
   */
  public AttendanceView(
      UUID id,
      UUID projectId,
      UUID studentId,
      BigDecimal duration,
      String qrValidationHash,
      String status,
      UUID validatedById,
      OffsetDateTime validatedAt,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    this(
        id,
        projectId,
        studentId,
        duration,
        qrValidationHash,
        AttendanceStatus.valueOf(status),
        validatedById,
        validatedAt,
        createdAt,
        updatedAt);
  }
}
