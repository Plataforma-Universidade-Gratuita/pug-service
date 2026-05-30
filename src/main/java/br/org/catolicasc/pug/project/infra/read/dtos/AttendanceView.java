package br.org.catolicasc.pug.project.infra.read.dtos;

import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.shared.domain.enums.Campi;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Read-side projection representing the data required by attendance list, detail, and complex
 * search endpoints.
 *
 * @param id the unique identifier of the attendance record
 * @param projectId the unique identifier of the associated project
 * @param projectName the display name of the associated project
 * @param studentId the unique identifier of the associated former student account
 * @param studentName the display name of the associated former student
 * @param studentEmail the email address of the associated former student account
 * @param academicRegistration the academic registration of the associated former student
 * @param campus the campus of the associated former student
 * @param duration the recorded attendance duration
 * @param qrValidationHash the QR validation hash of the attendance
 * @param status the current attendance lifecycle status
 * @param validatedById the unique identifier of the validator account
 * @param validatedByName the display name of the validator account
 * @param validatedByEmail the email address of the validator account
 * @param validatedAt the validation timestamp
 * @param createdAt the creation timestamp
 * @param updatedAt the update timestamp
 */
public record AttendanceView(
    UUID id,
    UUID projectId,
    String projectName,
    UUID studentId,
    String studentName,
    String studentEmail,
    String academicRegistration,
    Campi campus,
    BigDecimal duration,
    String qrValidationHash,
    AttendanceStatus status,
    UUID validatedById,
    String validatedByName,
    String validatedByEmail,
    OffsetDateTime validatedAt,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {

  /**
   * Auxiliary constructor used by JPA constructor expressions when the persisted status is still
   * projected as its raw string representation.
   */
  public AttendanceView(
      UUID id,
      UUID projectId,
      String projectName,
      UUID studentId,
      String studentName,
      String studentEmail,
      String academicRegistration,
      Campi campus,
      BigDecimal duration,
      String qrValidationHash,
      String status,
      UUID validatedById,
      String validatedByName,
      String validatedByEmail,
      OffsetDateTime validatedAt,
      OffsetDateTime createdAt,
      OffsetDateTime updatedAt) {
    this(
        id,
        projectId,
        projectName,
        studentId,
        studentName,
        studentEmail,
        academicRegistration,
        campus,
        duration,
        qrValidationHash,
        AttendanceStatus.valueOf(status),
        validatedById,
        validatedByName,
        validatedByEmail,
        validatedAt,
        createdAt,
        updatedAt);
  }
}
