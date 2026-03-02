package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Value object representing enrollment information. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class EnrollmentInfo extends DomainError {

  OffsetDateTime acceptedAt;
  OffsetDateTime closingStatusAt;
  AuditInfo auditInfo;

  /** Private constructor to enforce the use of the factory method. */
  @Builder(toBuilder = true)
  private EnrollmentInfo(
      OffsetDateTime acceptedAt, OffsetDateTime closingStatusAt, AuditInfo auditInfo) {
    this.acceptedAt = acceptedAt;
    this.closingStatusAt = closingStatusAt;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create and validate an EnrollmentInfo instance.
   *
   * @return A validated EnrollmentInfo instance.
   */
  public static EnrollmentInfo factory() {
    EnrollmentInfo vo =
        EnrollmentInfo.builder()
            .acceptedAt(null)
            .closingStatusAt(null)
            .auditInfo(AuditInfo.factory())
            .build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Creates an updated EnrollmentInfo instance with refreshed audit information.
   *
   * @return A new EnrollmentInfo instance with updated audit info.
   */
  public EnrollmentInfo update() {
    EnrollmentInfo updated = toBuilder().auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Creates an updated EnrollmentInfo instance with the acceptedAt timestamp set to now.
   *
   * @return A new EnrollmentInfo instance with acceptedAt set to the current time.
   */
  public EnrollmentInfo accept() {
    EnrollmentInfo accepted =
        toBuilder().acceptedAt(OffsetDateTime.now()).auditInfo(auditInfo.update()).build();
    accepted.collectValidationProblems();
    return accepted;
  }

  /**
   * Creates an updated EnrollmentInfo instance with the closingStatusAt timestamp set to now.
   *
   * @return A new EnrollmentInfo instance with closingStatusAt set to the current time.
   */
  public EnrollmentInfo closeStatus() {
    EnrollmentInfo closed =
        toBuilder().closingStatusAt(OffsetDateTime.now()).auditInfo(auditInfo.update()).build();
    closed.collectValidationProblems();
    return closed;
  }

  /** Validates the EnrollmentInfo instance. */
  private void collectValidationProblems() {
    if (auditInfo == null) {
      addFieldError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else {
      addFieldErrors(auditInfo.getFieldErrors());
      if (acceptedAt != null && acceptedAt.isBefore(auditInfo.getCreatedAt())) {
        addFieldError(new Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_DATES_INVALID));
      }

      if (closingStatusAt != null && closingStatusAt.isBefore(auditInfo.getCreatedAt())) {
        addFieldError(new Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_DATES_INVALID));
      }
    }
  }
}
