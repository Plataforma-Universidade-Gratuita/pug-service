package br.org.catolicasc.pug.project.domain.vos;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing the validation metadata for an Attendance record.
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate domain validation rules regarding
 * the timestamp and the identity of the staff member who validated the attendance.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class AttendanceInfo extends DomainError {

  UUID validatedBy;

  OffsetDateTime validatedAt;

  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private AttendanceInfo(UUID validatedBy, OffsetDateTime validatedAt, AuditInfo auditInfo) {
    this.validatedBy = validatedBy;
    this.validatedAt = validatedAt;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a new {@code AttendanceInfo} instance.
   *
   * <p>Initializes standard audit tracking information and immediately validates the state.
   *
   * @param validatedBy the unique identifier of the validating staff member
   * @param validatedAt the timestamp of validation
   * @return a self-validated {@link AttendanceInfo} instance
   */
  public static AttendanceInfo factory(UUID validatedBy, OffsetDateTime validatedAt) {
    AttendanceInfo vo =
        AttendanceInfo.builder()
            .validatedBy(validatedBy)
            .validatedAt(validatedAt)
            .auditInfo(AuditInfo.factory())
            .build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Refreshes the internal audit information to reflect a state update.
   *
   * @return a new {@link AttendanceInfo} instance with updated audit info
   */
  public AttendanceInfo update() {
    AttendanceInfo updated = toBuilder().auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  private void collectValidationProblems() {
    boolean hasValidator = validatedBy != null;
    boolean hasDate = validatedAt != null;

    if (hasValidator != hasDate) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK);
    }

    if (hasDate
        && auditInfo != null
        && auditInfo.getCreatedAt() != null
        && validatedAt.isBefore(auditInfo.getCreatedAt())) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_CREATED_AT_FUTURE);
    }

    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}
