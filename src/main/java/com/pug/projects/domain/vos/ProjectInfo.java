package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import com.pug.shared.domain.enums.SharedErrorCodes;
import com.pug.shared.domain.vos.AuditInfo;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Value object representing comprehensive project information including creator, timeline, capacity
 * and hours.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class ProjectInfo extends DomainError {

  UUID createdBy;
  Integer maxParticipants;
  BigDecimal offeredHours;
  OffsetDateTime closedAt;
  AuditInfo auditInfo;

  /** Private constructor for ProjectInfo. */
  @Builder(toBuilder = true)
  private ProjectInfo(
      UUID createdBy,
      Integer maxParticipants,
      BigDecimal offeredHours,
      OffsetDateTime closedAt,
      AuditInfo auditInfo) {
    this.createdBy = createdBy;
    this.maxParticipants = maxParticipants;
    this.offeredHours = offeredHours;
    this.closedAt = closedAt;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a ProjectInfo instance with validation.
   *
   * @param createdBy the UUID of the creator
   * @param maxParticipants the maximum number of participants
   * @param offeredHours Hours offered for the project
   * @return a validated ProjectInfo instance
   */
  public static ProjectInfo factory(
      UUID createdBy, Integer maxParticipants, BigDecimal offeredHours) {
    ProjectInfo vo =
        ProjectInfo.builder()
            .createdBy(createdBy)
            .maxParticipants(maxParticipants)
            .offeredHours(offeredHours)
            .closedAt(null)
            .auditInfo(AuditInfo.factory())
            .build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Behavior: closes the project by setting the closedAt timestamp. If the project is already
   * closed, it returns the same instance.
   *
   * @return a new ProjectInfo instance with closedAt set, or the same instance if already closed
   */
  public ProjectInfo closeProject() {
    if (closedAt != null) {
      return this;
    }
    ProjectInfo updated =
        toBuilder().closedAt(OffsetDateTime.now()).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: changes the offered hours for the project. If the new offered hours are the same as
   * the current value, it returns the same instance.
   *
   * @param newOfferedHours the new offered hours to set
   * @return a new ProjectInfo instance with updated offered hours, or the same instance if no
   *     change
   */
  public ProjectInfo changeOfferedHours(BigDecimal newOfferedHours) {
    if (offeredHours != null && offeredHours.equals(newOfferedHours)) {
      return this;
    }
    ProjectInfo updated =
        toBuilder().offeredHours(newOfferedHours).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: changes the maximum number of participants allowed for the project. If the new value
   * is the same as the current value, it returns the same instance.
   *
   * @param newMaxParticipants the new maximum number of participants to set
   * @return a new ProjectInfo instance with updated max participants, or the same instance if no
   *     change
   */
  public ProjectInfo changeMaxParticipantsAllowed(Integer newMaxParticipants) {
    if (maxParticipants != null && maxParticipants.equals(newMaxParticipants)) {
      return this;
    }
    ProjectInfo updated =
        toBuilder().maxParticipants(newMaxParticipants).auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Behavior: updates the audit information (e.g., updatedAt timestamp) without changing other
   * fields.
   *
   * @return a new ProjectInfo instance with updated audit information
   */
  public ProjectInfo update() {
    ProjectInfo updated = toBuilder().auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /** Validates the ProjectInfo fields and adds errors if any validation fails. */
  private void collectValidationProblems() {
    validateForeignKeyField(createdBy, "createdBy");
    validateBigDecimalField(offeredHours, "offeredHours", false, false);

    if (maxParticipants != null && maxParticipants < 0) {
      addError(new Problem(ProjectsErrorCodes.INVALID_MAX_PARTICIPANTS_NEGATIVE));
    }

    if (auditInfo == null) {
      addError(new Problem(SharedErrorCodes.INVALID_AUDIT_INFO_BLANK));
    } else {
      addErrors(auditInfo.getProblems());
    }
  }
}
