package br.org.catolicasc.pug.project.domain;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.project.domain.enums.AttendanceStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsErrorCodes;
import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.project.domain.vos.AttendanceInfo;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.domain.vos.QrValidationInfo;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import com.github.f4b6a3.uuid.UuidCreator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Immutable Domain Entity representing a FormerStudent's Attendance record for a Project.
 *
 * <p>This class acts as an aggregate containing the unique identifier, the linked enrollment, the
 * QR validation data, and the staff validation metadata. It extends {@link DomainError} to
 * accumulate structural validation failures.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Attendance extends DomainError {

  /** The unique identifier for the attendance record (UUIDv7). */
  UUID id;

  /** The composite identifier linking this attendance to a specific formerStudent and project. */
  EnrollmentIdentifier enrollmentIdentifier;

  /** The temporal data and unique QR validation hash recorded. */
  QrValidationInfo qrValidationInfo;

  /** The metadata tracking which staff member validated the attendance and when. */
  AttendanceInfo attendanceInfo;

  /** The current validation status of the attendance (e.g., WAITING, PRESENT). */
  AttendanceStatus status;

  /**
   * Factory method to create a new, unvalidated {@code Attendance} instance.
   *
   * <p>Initializes the attendance in a {@code WAITING} state with empty validation info.
   *
   * @param project the associated project
   * @param formerStudent the associated formerStudent
   * @param duration the duration of time the formerStudent spent on the project
   * @param qrHash the unique hash of the QR code being registered
   * @return a newly created and self-validated {@link Attendance} instance
   */
  public static Attendance factory(
      Project project, FormerStudent formerStudent, BigDecimal duration, String qrHash) {
    UUID studentId = (formerStudent != null) ? formerStudent.getAccountId() : null;
    UUID projectId = (project != null) ? project.getId() : null;

    Attendance att =
        Attendance.builder()
            .id(UuidCreator.getTimeOrderedEpoch())
            .enrollmentIdentifier(EnrollmentIdentifier.factory(studentId, projectId))
            .qrValidationInfo(QrValidationInfo.factory(duration, qrHash))
            .attendanceInfo(AttendanceInfo.factory(null, null))
            .status(AttendanceStatus.WAITING)
            .build();

    att.collectValidationProblems();
    return att;
  }

  /**
   * Validates the attendance record, transitioning its status and recording validation metadata.
   *
   * <p>This allows marking the attendance as either PRESENT (valid) or ABSENT (rejected) by a staff
   * member.
   *
   * @param validatorId the unique identifier of the staff account performing the action
   * @param newStatus the target status (must be PRESENT or ABSENT)
   * @return a new {@link Attendance} instance reflecting the updated state
   * @throws BusinessRuleException if the status is not a valid transition state
   */
  public Attendance validatePresence(UUID validatorId, AttendanceStatus newStatus) {
    if (newStatus != AttendanceStatus.PRESENT && newStatus != AttendanceStatus.ABSENT) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_PROJECT_STATUS_UPDATE_START);
    }

    AttendanceInfo newInfo =
        this.attendanceInfo.toBuilder()
            .validatedBy(validatorId)
            .validatedAt(OffsetDateTime.now())
            .auditInfo(this.attendanceInfo.getAuditInfo().update())
            .build();

    Attendance updated = this.toBuilder().attendanceInfo(newInfo).status(newStatus).build();

    updated.collectValidationProblems();
    return updated;
  }

  /** Evaluates constraints for the Attendance aggregate and accumulates any validation problems. */
  private void collectValidationProblems() {
    validateIdField(id);
    if (enrollmentIdentifier == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_PROJECT_BLANK);
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_STUDENT_BLANK);
    } else if (identifierHasFieldErrors()) {
      addFieldErrors(enrollmentIdentifier.getFieldErrors());
    }
    if (status == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK);
    }
    if (qrValidationInfo != null && qrValidationInfo.hasFieldErrors()) {
      addFieldErrors(qrValidationInfo.getFieldErrors());
    }
    if (attendanceInfo != null && attendanceInfo.hasFieldErrors()) {
      addFieldErrors(attendanceInfo.getFieldErrors());
    }
  }

  private boolean identifierHasFieldErrors() {
    return enrollmentIdentifier != null && enrollmentIdentifier.hasFieldErrors();
  }
}
