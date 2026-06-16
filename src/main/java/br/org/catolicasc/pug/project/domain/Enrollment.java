package br.org.catolicasc.pug.project.domain;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.domain.enums.ProjectsErrorCodes;
import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentInfo;
import br.org.catolicasc.pug.shared.domain.DomainError;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Immutable Domain Entity representing a FormerStudent's Enrollment in a Project.
 *
 * <p>This class maps a specific {@link FormerStudent} directly to a {@link Project} and tracks the
 * lifecycle state of that relationship (e.g., {@link EnrollmentStatus#PENDING}, {@link
 * EnrollmentStatus#APPROVED}, {@link EnrollmentStatus#COMPLETED}). It extends {@link DomainError}
 * to accumulate structural validation failures and bubble up problems from nested value objects.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Enrollment extends DomainError {

  EnrollmentIdentifier identifier;

  EnrollmentStatus status;

  EnrollmentInfo enrollmentInfo;

  /**
   * Factory method to create a new {@code Enrollment} instance in a {@link
   * EnrollmentStatus#PENDING} state.
   *
   * <p>The returned aggregate is initialized with a freshly created {@link EnrollmentIdentifier}
   * based on the provided {@link FormerStudent} and {@link Project}, and a default {@link
   * EnrollmentInfo} with no acceptance or closing timestamps set. The instance is immediately
   * self-validated; any problems are accumulated internally and can be inspected via {@link
   * #hasFieldErrors()} and {@link #getFieldErrors()}.
   *
   * @param formerStudent the {@link FormerStudent} requesting enrollment
   * @param project the {@link Project} the formerStudent wishes to join
   * @return a new, self-validated {@link Enrollment} instance in {@link EnrollmentStatus#PENDING}
   *     state
   */
  public static Enrollment factory(FormerStudent formerStudent, Project project) {
    UUID formerStudentId = (formerStudent != null) ? formerStudent.getAccountId() : null;
    UUID projectId = (project != null) ? project.getId() : null;

    Enrollment enrollment =
        Enrollment.builder()
            .identifier(EnrollmentIdentifier.factory(formerStudentId, projectId))
            .status(EnrollmentStatus.PENDING)
            .enrollmentInfo(EnrollmentInfo.factory())
            .build();

    enrollment.collectValidationProblems();
    return enrollment;
  }

  /**
   * Transitions the enrollment to a new lifecycle status, updating tracking timestamps accordingly.
   *
   * <p>Business rules applied:
   *
   * <ul>
   *   <li>If the requested {@code newStatus} is equal to the current {@code status}, this method is
   *       idempotent and returns {@code this} without changes.
   *   <li>If the current {@code status} is already a closing state (see {@link
   *       #isClosingStatus(EnrollmentStatus)}), no further transitions are allowed and a {@link
   *       BusinessRuleException} is thrown with {@link
   *       ProjectsErrorCodes#INVALID_ENROLLMENT_STATUS_UPDATE}.
   *   <li>A transition to {@link EnrollmentStatus#APPROVED} is only allowed when the current {@code
   *       status} is {@link EnrollmentStatus#PENDING} or {@link EnrollmentStatus#ON_HOLD}. When
   *       moving from {@code PENDING}, {@link EnrollmentInfo#accept()} is applied, stamping {@code
   *       acceptedAt}. When moving from {@code ON_HOLD}, the existing acceptance timestamp is
   *       preserved and only the audit metadata is refreshed through {@link
   *       EnrollmentInfo#update()}.
   *   <li>A transition to {@link EnrollmentStatus#ON_HOLD} is only allowed when the current {@code
   *       status} is {@link EnrollmentStatus#APPROVED}. On success, {@link EnrollmentInfo#update()}
   *       is applied so the enrollment keeps its lifecycle timestamps while still tracking the
   *       state change.
   *   <li>A transition to {@link EnrollmentStatus#CANCELED} is allowed when the current {@code
   *       status} is {@link EnrollmentStatus#PENDING}, {@link EnrollmentStatus#APPROVED}, or {@link
   *       EnrollmentStatus#ON_HOLD}. On success, {@link EnrollmentInfo#closeStatus()} is applied,
   *       stamping {@code closingStatusAt}.
   *   <li>Transitions to the remaining closing statuses (i.e., {@link EnrollmentStatus#COMPLETED},
   *       {@link EnrollmentStatus#EXITED}, {@link EnrollmentStatus#REMOVED}) are only allowed when
   *       the current {@code status} is {@link EnrollmentStatus#APPROVED} or {@link
   *       EnrollmentStatus#ON_HOLD}. On success, {@link EnrollmentInfo#closeStatus()} is applied,
   *       stamping {@code closingStatusAt}.
   *   <li>A transition to {@link EnrollmentStatus#REJECTED} is allowed from both {@link
   *       EnrollmentStatus#PENDING} and {@link EnrollmentStatus#APPROVED}. On success, {@link
   *       EnrollmentInfo#closeStatus()} is applied, stamping {@code closingStatusAt}.
   *   <li>All other transitions (for example, attempting to go from {@code PENDING} directly to a
   *       closing status, or attempting to revert from a closing status back to {@code PENDING} or
   *       {@code APPROVED}) are rejected with a {@link BusinessRuleException} using {@link
   *       ProjectsErrorCodes#INVALID_ENROLLMENT_STATUS_UPDATE}.
   * </ul>
   *
   * @param newStatus the target {@link EnrollmentStatus} to transition to (must not be {@code null}
   *     for a valid transition)
   * @return a new {@link Enrollment} instance reflecting the updated status and lifecycle metadata,
   *     or {@code this} if the status is unchanged
   * @throws BusinessRuleException if the requested transition violates the enrollment lifecycle
   *     rules and {@link ProjectsErrorCodes#INVALID_ENROLLMENT_STATUS_UPDATE} is raised
   */
  public Enrollment changeStatus(EnrollmentStatus newStatus) {
    if (status == newStatus) {
      return this;
    }

    if (isClosingStatus(status)) {
      throw new BusinessRuleException(ProjectsErrorCodes.INVALID_ENROLLMENT_STATUS_UPDATE);
    }

    EnrollmentInfo newInfo = resolveNextEnrollmentInfo(Objects.requireNonNull(newStatus));

    Enrollment updated = buildUpdatedEnrollment(newStatus, newInfo);
    updated.collectValidationProblems();
    return updated;
  }

  public Enrollment changeStatus(EnrollmentStatus newStatus, Project project) {
    Enrollment updated = changeStatus(newStatus);
    if (shouldPauseApprovedPendingEnrollment(newStatus, project)) {
      return updated.changeStatus(EnrollmentStatus.ON_HOLD);
    }
    return updated;
  }

  private Enrollment buildUpdatedEnrollment(EnrollmentStatus newStatus, EnrollmentInfo newInfo) {
    return toBuilder().status(newStatus).enrollmentInfo(newInfo).build();
  }

  private EnrollmentInfo resolveNextEnrollmentInfo(EnrollmentStatus newStatus) {
    return switch (newStatus) {
      case APPROVED -> resolveApprovedInfo();
      case ON_HOLD -> resolveOnHoldInfo();
      case REJECTED -> resolveRejectedInfo();
      default -> resolveClosingInfo(newStatus);
    };
  }

  private EnrollmentInfo resolveApprovedInfo() {
    ensureCurrentStatusIs(EnrollmentStatus.PENDING, EnrollmentStatus.ON_HOLD);
    return status == EnrollmentStatus.PENDING ? enrollmentInfo.accept() : enrollmentInfo.update();
  }

  private EnrollmentInfo resolveOnHoldInfo() {
    ensureCurrentStatusIs(EnrollmentStatus.APPROVED);
    return enrollmentInfo.update();
  }

  private EnrollmentInfo resolveRejectedInfo() {
    ensureCurrentStatusIs(EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED);
    return enrollmentInfo.closeStatus();
  }

  private EnrollmentInfo resolveClosingInfo(EnrollmentStatus newStatus) {
    if (!isClosingStatus(newStatus)) {
      throwInvalidStatusTransition();
    }

    if (newStatus == EnrollmentStatus.CANCELED) {
      ensureCurrentStatusIs(
          EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED, EnrollmentStatus.ON_HOLD);
      return enrollmentInfo.closeStatus();
    }

    ensureCurrentStatusIs(EnrollmentStatus.APPROVED, EnrollmentStatus.ON_HOLD);
    return enrollmentInfo.closeStatus();
  }

  private void ensureCurrentStatusIs(EnrollmentStatus... allowedStatuses) {
    for (EnrollmentStatus allowedStatus : allowedStatuses) {
      if (status == allowedStatus) {
        return;
      }
    }
    throwInvalidStatusTransition();
  }

  private void throwInvalidStatusTransition() {
    throw new BusinessRuleException(ProjectsErrorCodes.INVALID_ENROLLMENT_STATUS_UPDATE);
  }

  private boolean shouldPauseApprovedPendingEnrollment(
      EnrollmentStatus newStatus, Project project) {
    return status == EnrollmentStatus.PENDING
        && newStatus == EnrollmentStatus.APPROVED
        && project != null
        && project.getProjectStatus() == ProjectStatus.ON_HOLD;
  }

  private boolean isClosingStatus(EnrollmentStatus s) {
    return s == EnrollmentStatus.REJECTED
        || s == EnrollmentStatus.EXITED
        || s == EnrollmentStatus.REMOVED
        || s == EnrollmentStatus.CANCELED
        || s == EnrollmentStatus.COMPLETED;
  }

  private void collectValidationProblems() {
    if (identifier == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_FORMER_STUDENT_BLANK);
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK);
    } else if (identifier.hasFieldErrors()) {
      addFieldErrors(identifier.getFieldErrors());
    }

    if (status == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_STATUS_BLANK);
    }

    if (enrollmentInfo != null && enrollmentInfo.hasFieldErrors()) {
      addFieldErrors(enrollmentInfo.getFieldErrors());
    }
  }
}
