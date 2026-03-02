package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Value object representing attendance information. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class AttendanceInfo extends DomainError {

  UUID validatedBy;
  OffsetDateTime validatedAt;
  AuditInfo auditInfo;

  /**
   * Private constructor for AttendanceInfo.
   *
   * @param validatedBy UUID of the account who validated the attendance
   * @param validatedAt Date and time when the attendance was validated
   * @param auditInfo Audit info containing creation and update timestamps
   */
  @Builder(toBuilder = true)
  private AttendanceInfo(UUID validatedBy, OffsetDateTime validatedAt, AuditInfo auditInfo) {
    this.validatedBy = validatedBy;
    this.validatedAt = validatedAt;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create and validate an AttendanceInfo instance.
   *
   * @param validatedBy UUID of the account who validated the attendance
   * @param validatedAt Date and time when the attendance was validated
   * @return A validated AttendanceInfo instance
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

  /** Helper to update the audit info (updatedAt) without changing other fields. */
  public AttendanceInfo update() {
    AttendanceInfo updated = toBuilder().auditInfo(this.auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Validates the AttendanceInfo instance. Adds validation errors to the domain error list if any
   * validation rules are violated.
   */
  private void collectValidationProblems() {
    if (auditInfo == null) {
      addFieldError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else {
      addFieldErrors(auditInfo.getFieldErrors());
    }

    boolean hasValidator = validatedBy != null;
    boolean hasDate = validatedAt != null;

    if (hasValidator != hasDate) {
      addFieldError(new Problem(ProjectsErrorCodes.INVALID_ATTENDANCE_STATUS_BLANK));
    }

    if (hasDate
        && auditInfo != null
        && auditInfo.getCreatedAt() != null
        && validatedAt.isBefore(auditInfo.getCreatedAt())) {
      addFieldError(new Problem(ProjectsErrorCodes.INVALID_CREATED_AT_FUTURE));
    }
  }
}
