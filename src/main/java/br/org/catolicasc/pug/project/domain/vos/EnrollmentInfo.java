/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.domain.vos;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing the lifecycle metadata of an Enrollment.
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate validations relating to
 * chronological integrity, ensuring status change timestamps remain consistent.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class EnrollmentInfo extends DomainError {

  OffsetDateTime acceptedAt;

  OffsetDateTime closingStatusAt;

  AuditInfo auditInfo;

  @Builder(toBuilder = true)
  private EnrollmentInfo(
      OffsetDateTime acceptedAt, OffsetDateTime closingStatusAt, AuditInfo auditInfo) {
    this.acceptedAt = acceptedAt;
    this.closingStatusAt = closingStatusAt;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a new {@code EnrollmentInfo} instance in its initial state.
   *
   * <p>Automatically initializes standard audit tracking information with null values for the
   * acceptance and closing timestamps.
   *
   * @return a newly created and self-validated {@link EnrollmentInfo} instance
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
   * Refreshes the internal audit information to reflect a state update.
   *
   * @return a new {@link EnrollmentInfo} instance with updated audit info
   */
  public EnrollmentInfo update() {
    EnrollmentInfo updated = toBuilder().auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Updates the enrollment info to reflect an accepted status, marking the timestamp.
   *
   * @return a new {@link EnrollmentInfo} instance with {@code acceptedAt} set to the current time
   */
  public EnrollmentInfo accept() {
    EnrollmentInfo accepted =
        toBuilder().acceptedAt(OffsetDateTime.now()).auditInfo(auditInfo.update()).build();
    accepted.collectValidationProblems();
    return accepted;
  }

  /**
   * Updates the enrollment info to reflect a closed status, marking the timestamp.
   *
   * @return a new {@link EnrollmentInfo} instance with {@code closingStatusAt} set to the current
   *     time
   */
  public EnrollmentInfo closeStatus() {
    EnrollmentInfo closed =
        toBuilder().closingStatusAt(OffsetDateTime.now()).auditInfo(auditInfo.update()).build();
    closed.collectValidationProblems();
    return closed;
  }

  private void collectValidationProblems() {
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else {
      addFieldErrors(auditInfo.getFieldErrors());
      if (acceptedAt != null && acceptedAt.isBefore(auditInfo.getCreatedAt())) {
        addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_DATES_INVALID);
      }
      if (closingStatusAt != null && closingStatusAt.isBefore(auditInfo.getCreatedAt())) {
        addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_DATES_INVALID);
      }
    }
  }
}
