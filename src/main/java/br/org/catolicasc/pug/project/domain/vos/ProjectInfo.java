package br.org.catolicasc.pug.project.domain.vos;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.domain.enums.SharedFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.vos.AuditInfo;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing the comprehensive logistical details of a Project.
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate validations relating to project
 * constraints, ensuring values like participant capacity, offered hours, and completion progress
 * remain within logically valid bounds.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProjectInfo extends DomainError {

  /** The unique identifier (Account ID) of the staff member who created the project. */
  UUID createdBy;

  /** The maximum number of formerStudents allowed to enroll in the project. */
  Integer maxParticipants;

  /** The total amount of counterpart hours the project offers to its participants. */
  BigDecimal offeredHours;

  /** The total amount of counterpart hours already completed in the project. */
  BigDecimal completedHours;

  /** The exact timestamp when the project was officially closed. */
  OffsetDateTime closedAt;

  /** The audit tracking information (creation and update timestamps). */
  AuditInfo auditInfo;

  /**
   * Constructs a {@code ProjectInfo} instance.
   *
   * @param createdBy the unique identifier of the creator
   * @param maxParticipants the maximum number of participants
   * @param offeredHours the hours offered by the project
   * @param completedHours the hours completed in the project
   * @param closedAt the timestamp when the project was closed
   * @param auditInfo the audit tracking VO
   */
  @Builder(toBuilder = true)
  private ProjectInfo(
      UUID createdBy,
      Integer maxParticipants,
      BigDecimal offeredHours,
      BigDecimal completedHours,
      OffsetDateTime closedAt,
      AuditInfo auditInfo) {
    this.createdBy = createdBy;
    this.maxParticipants = maxParticipants;
    this.offeredHours = offeredHours;
    this.completedHours = completedHours;
    this.closedAt = closedAt;
    this.auditInfo = auditInfo;
  }

  /**
   * Factory method to create a new {@code ProjectInfo} instance.
   *
   * <p>The instance is initialized as open (closedAt is null) with standard audit tracking
   * information. It is immediately self-validated.
   *
   * @param createdBy the unique identifier of the creator
   * @param maxParticipants the maximum number of participants
   * @param offeredHours the hours offered for the project
   * @param completedHours the starting hours completed (usually 0)
   * @return a self-validated {@link ProjectInfo} instance
   */
  public static ProjectInfo factory(
      UUID createdBy, Integer maxParticipants, BigDecimal offeredHours, BigDecimal completedHours) {
    ProjectInfo vo =
        ProjectInfo.builder()
            .createdBy(createdBy)
            .maxParticipants(maxParticipants)
            .offeredHours(offeredHours != null ? offeredHours : BigDecimal.ZERO)
            .completedHours(completedHours != null ? completedHours : BigDecimal.ZERO)
            .closedAt(null)
            .auditInfo(AuditInfo.factory())
            .build();
    vo.collectValidationProblems();
    return vo;
  }

  /**
   * Updates the project info to reflect a closed status by setting the {@code closedAt} timestamp.
   *
   * @return a new {@link ProjectInfo} instance with {@code closedAt} set, or the same instance if
   *     already closed
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
   * Updates the offered hours for the project.
   *
   * @param newOfferedHours the new offered hours to set
   * @return a new {@link ProjectInfo} instance with updated hours, or the same instance if
   *     unchanged
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
   * Updates the maximum number of participants allowed to enroll in the project.
   *
   * @param newMaxParticipants the new participant capacity limit
   * @return a new {@link ProjectInfo} instance with updated capacity, or the same instance if
   *     unchanged
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
   * Refreshes the internal audit information to reflect a state update.
   *
   * @return a new {@link ProjectInfo} instance with updated audit info
   */
  public ProjectInfo update() {
    ProjectInfo updated = toBuilder().auditInfo(auditInfo.update()).build();
    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>The creator ID must not be null.
   *   <li>The participant capacity, if provided, must not be negative.
   *   <li>The offered hours must not be negative.
   *   <li>{@code completedHours} must not be negative.
   *   <li>{@code completedHours} cannot exceed {@code offeredHours}.
   *   <li>Ensures the {@code auditInfo} is not null.
   * </ul>
   */
  private void collectValidationProblems() {
    if (createdBy == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_PROJECT_CREATED_BY_BLANK);
    }
    if (maxParticipants != null && maxParticipants < 0) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_MAX_PARTICIPANTS_NEGATIVE);
    }
    if (offeredHours != null && offeredHours.signum() < 0) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_PROJECT_OFFERED_HOURS_NEGATIVE);
    }
    if (completedHours != null && completedHours.compareTo(BigDecimal.ZERO) < 0) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_PROJECT_COMPLETED_HOURS_NEGATIVE);
    }
    if (offeredHours != null
        && completedHours != null
        && completedHours.compareTo(offeredHours) > 0) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_PROJECT_COMPLETED_HOURS_EXCEEDS);
    }
    if (auditInfo == null) {
      addFieldError(SharedFieldErrorCodes.INVALID_AUDIT_INFO_BLANK);
    } else {
      addFieldErrors(auditInfo.getFieldErrors());
    }
  }
}
