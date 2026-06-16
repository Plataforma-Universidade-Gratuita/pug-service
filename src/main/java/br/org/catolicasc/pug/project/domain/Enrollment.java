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
   * Transitions the enrollment to the {@link EnrollmentStatus#APPROVED} state if valid.
   *
   * <p>If the enrollment is already approved, it returns itself. Otherwise, it checks that the
   * current status allows for approval (i.e., it must be either {@link EnrollmentStatus#PENDING} or
   * {@link EnrollmentStatus#ON_HOLD}) and that it is not in a closed state. If valid, it creates a
   * new instance with the updated status and an updated {@link EnrollmentInfo} reflecting the
   * approval timestamp. The new instance is self-validated before being returned.
   *
   * @return a new {@link Enrollment} instance in {@link EnrollmentStatus#APPROVED} state if the
   *     transition is valid; otherwise, throws a {@link BusinessRuleException}
   */
  public Enrollment approve() {
    if (status == EnrollmentStatus.APPROVED) {
      return this;
    }

    ensureNotClosed();
    ensureCurrentStatusIs(EnrollmentStatus.PENDING, EnrollmentStatus.ON_HOLD);
    EnrollmentInfo newInfo =
        status == EnrollmentStatus.PENDING ? enrollmentInfo.accept() : enrollmentInfo.update();
    return buildUpdatedEnrollment(EnrollmentStatus.APPROVED, newInfo);
  }

  /**
   * Approves the enrollment and immediately places it on hold when the linked project is on hold.
   *
   * <p>This preserves the enrollment lifecycle rule while also honoring the project lifecycle
   * context passed by the application service. If the current enrollment is pending and the project
   * is currently {@link ProjectStatus#ON_HOLD}, the returned enrollment ends in {@link
   * EnrollmentStatus#ON_HOLD}; otherwise it ends in {@link EnrollmentStatus#APPROVED}.
   *
   * @param project the linked {@link Project} whose current status influences the result
   * @return a new {@link Enrollment} instance reflecting the resulting lifecycle state
   */
  public Enrollment approve(Project project) {
    Enrollment approved = approve();
    if (status == EnrollmentStatus.PENDING && isProjectOnHold(project)) {
      return approved.putOnHold();
    }
    return approved;
  }

  /**
   * Transitions the enrollment to {@link EnrollmentStatus#ON_HOLD}.
   *
   * <p>This transition is only valid from {@link EnrollmentStatus#APPROVED}. The acceptance
   * timestamp is preserved while the audit metadata is refreshed.
   *
   * @return a new {@link Enrollment} instance in {@link EnrollmentStatus#ON_HOLD} state
   */
  public Enrollment putOnHold() {
    if (status == EnrollmentStatus.ON_HOLD) {
      return this;
    }

    ensureNotClosed();
    ensureCurrentStatusIs(EnrollmentStatus.APPROVED);
    return buildUpdatedEnrollment(EnrollmentStatus.ON_HOLD, enrollmentInfo.update());
  }

  /**
   * Transitions the enrollment to {@link EnrollmentStatus#REJECTED}.
   *
   * <p>This transition is only valid from {@link EnrollmentStatus#PENDING} or {@link
   * EnrollmentStatus#APPROVED}. On success, the enrollment is closed and its closing timestamp is
   * recorded.
   *
   * @return a new {@link Enrollment} instance in {@link EnrollmentStatus#REJECTED} state
   */
  public Enrollment reject() {
    if (status == EnrollmentStatus.REJECTED) {
      return this;
    }

    ensureNotClosed();
    ensureCurrentStatusIs(EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED);
    return buildUpdatedEnrollment(EnrollmentStatus.REJECTED, enrollmentInfo.closeStatus());
  }

  /**
   * Transitions the enrollment to {@link EnrollmentStatus#CANCELED}.
   *
   * <p>This transition is only valid from {@link EnrollmentStatus#PENDING}, {@link
   * EnrollmentStatus#APPROVED}, or {@link EnrollmentStatus#ON_HOLD}. On success, the enrollment is
   * closed and its closing timestamp is recorded.
   *
   * @return a new {@link Enrollment} instance in {@link EnrollmentStatus#CANCELED} state
   */
  public Enrollment cancel() {
    if (status == EnrollmentStatus.CANCELED) {
      return this;
    }

    ensureNotClosed();
    ensureCurrentStatusIs(
        EnrollmentStatus.PENDING, EnrollmentStatus.APPROVED, EnrollmentStatus.ON_HOLD);
    return buildUpdatedEnrollment(EnrollmentStatus.CANCELED, enrollmentInfo.closeStatus());
  }

  /**
   * Transitions the enrollment to {@link EnrollmentStatus#COMPLETED}.
   *
   * <p>This transition is only valid from {@link EnrollmentStatus#APPROVED} or {@link
   * EnrollmentStatus#ON_HOLD}. On success, the enrollment is closed and its closing timestamp is
   * recorded.
   *
   * @return a new {@link Enrollment} instance in {@link EnrollmentStatus#COMPLETED} state
   */
  public Enrollment complete() {
    if (status == EnrollmentStatus.COMPLETED) {
      return this;
    }

    ensureNotClosed();
    ensureCurrentStatusIs(EnrollmentStatus.APPROVED, EnrollmentStatus.ON_HOLD);
    return buildUpdatedEnrollment(EnrollmentStatus.COMPLETED, enrollmentInfo.closeStatus());
  }

  /**
   * Transitions the enrollment to {@link EnrollmentStatus#EXITED}.
   *
   * <p>This transition is only valid from {@link EnrollmentStatus#APPROVED} or {@link
   * EnrollmentStatus#ON_HOLD}. On success, the enrollment is closed and its closing timestamp is
   * recorded.
   *
   * @return a new {@link Enrollment} instance in {@link EnrollmentStatus#EXITED} state
   */
  public Enrollment exit() {
    if (status == EnrollmentStatus.EXITED) {
      return this;
    }

    ensureNotClosed();
    ensureCurrentStatusIs(EnrollmentStatus.APPROVED, EnrollmentStatus.ON_HOLD);
    return buildUpdatedEnrollment(EnrollmentStatus.EXITED, enrollmentInfo.closeStatus());
  }

  /**
   * Transitions the enrollment to {@link EnrollmentStatus#REMOVED}.
   *
   * <p>This transition is only valid from {@link EnrollmentStatus#APPROVED} or {@link
   * EnrollmentStatus#ON_HOLD}. On success, the enrollment is closed and its closing timestamp is
   * recorded.
   *
   * @return a new {@link Enrollment} instance in {@link EnrollmentStatus#REMOVED} state
   */
  public Enrollment remove() {
    if (status == EnrollmentStatus.REMOVED) {
      return this;
    }

    ensureNotClosed();
    ensureCurrentStatusIs(EnrollmentStatus.APPROVED, EnrollmentStatus.ON_HOLD);
    return buildUpdatedEnrollment(EnrollmentStatus.REMOVED, enrollmentInfo.closeStatus());
  }

  /**
   * Validates whether this enrollment is eligible to receive new attendance records.
   *
   * <p>Attendances may only be registered for enrollments currently in the {@link
   * EnrollmentStatus#APPROVED} state.
   *
   * @throws BusinessRuleException if the enrollment is not approved
   */
  public void validateCanCreateAttendance() {
    if (status != EnrollmentStatus.APPROVED) {
      throw new BusinessRuleException(ProjectsErrorCodes.ATTENDANCE_ENROLLMENT_NOT_APPROVED);
    }
  }

  private Enrollment buildUpdatedEnrollment(EnrollmentStatus newStatus, EnrollmentInfo newInfo) {
    Enrollment updated = toBuilder().status(newStatus).enrollmentInfo(newInfo).build();
    updated.collectValidationProblems();
    return updated;
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

  private void ensureNotClosed() {
    if (isClosingStatus(status)) {
      throwInvalidStatusTransition();
    }
  }

  private boolean isProjectOnHold(Project project) {
    return project != null && project.getProjectStatus() == ProjectStatus.ON_HOLD;
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
