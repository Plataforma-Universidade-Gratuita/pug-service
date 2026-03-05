package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsFieldErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.SharedFieldErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
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
public class AttendanceInfo extends DomainError {

  /** The unique identifier (Account ID) of the staff member who validated the attendance. */
  UUID validatedBy;

  /** The exact timestamp when the attendance was explicitly validated. */
  OffsetDateTime validatedAt;

  /** The audit tracking information (creation and update timestamps). */
  AuditInfo auditInfo;

  /**
   * Constructs an {@code AttendanceInfo} instance.
   *
   * @param validatedBy the unique identifier of the validating staff member
   * @param validatedAt the timestamp of validation
   * @param auditInfo the audit tracking VO
   */
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
    AttendanceInfo updated = toBuilder().auditInfo(this.auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>Either both {@code validatedBy} and {@code validatedAt} are provided, or neither are.
   *       (appends {@link ProjectsFieldErrorCodes#INVALID_ATTENDANCE_STATUS_BLANK} if mismatched).
   *   <li>The validation timestamp cannot chronologically precede the creation timestamp (appends
   *       {@link ProjectsFieldErrorCodes#INVALID_CREATED_AT_FUTURE}).
   *   <li>Ensures the {@code auditInfo} is not null and bubbles up any internal errors.
   * </ul>
   */
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
