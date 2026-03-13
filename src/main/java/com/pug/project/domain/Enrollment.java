package com.pug.project.domain;

import com.pug.academic.domain.Student;
import com.pug.project.domain.enums.EnrollmentStatus;
import com.pug.project.domain.enums.ProjectsFieldErrorCodes;
import com.pug.project.domain.vos.EnrollmentIdentifier;
import com.pug.project.domain.vos.EnrollmentInfo;
import com.pug.shared.domain.DomainError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Immutable Domain Entity representing a Student's Enrollment in a Project.
 *
 * <p>This class maps a student directly to a project and tracks the lifecycle state of that
 * relationship (e.g., PENDING, APPROVED, COMPLETED). It extends {@link DomainError} to accumulate
 * structural validation failures.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Enrollment extends DomainError {

  /** The composite identifier uniquely linking the student to the project. */
  EnrollmentIdentifier identifier;

  /** The current lifecycle status of the enrollment. */
  EnrollmentStatus status;

  /** The metadata tracking critical timestamps (acceptance, closure) of the enrollment. */
  EnrollmentInfo enrollmentInfo;

  /**
   * Factory method to create a new {@code Enrollment} instance in a PENDING state.
   *
   * @param student the student requesting enrollment
   * @param project the project they wish to join
   * @return a new, self-validated {@link Enrollment} instance
   */
  public static Enrollment factory(Student student, Project project) {
    Enrollment enrollment =
        Enrollment.builder()
            .identifier(EnrollmentIdentifier.factory(student.getAccountId(), project.getId()))
            .status(EnrollmentStatus.PENDING)
            .enrollmentInfo(EnrollmentInfo.factory())
            .build();

    enrollment.collectValidationProblems();
    return enrollment;
  }

  /**
   * Transitions the enrollment to a new lifecycle status, updating tracking timestamps accordingly.
   *
   * @param newStatus the target status to transition to
   * @return a new {@link Enrollment} instance reflecting the updated state, or the same instance if
   *     unchanged
   */
  public Enrollment changeStatus(EnrollmentStatus newStatus) {
    if (this.status == newStatus) {
      return this;
    }

    EnrollmentInfo newInfo;

    if (newStatus == EnrollmentStatus.APPROVED) {
      newInfo = this.enrollmentInfo.accept();
    } else if (isClosingStatus(newStatus)) {
      newInfo = this.enrollmentInfo.closeStatus();
    } else {
      newInfo = this.enrollmentInfo.update();
    }

    Enrollment updated = this.toBuilder().status(newStatus).enrollmentInfo(newInfo).build();

    updated.collectValidationProblems();
    return updated;
  }

  /**
   * Helper method to determine if a status represents a terminal (closing) state for the
   * enrollment.
   */
  private boolean isClosingStatus(EnrollmentStatus s) {
    return s == EnrollmentStatus.REJECTED
        || s == EnrollmentStatus.EXITED
        || s == EnrollmentStatus.REMOVED
        || s == EnrollmentStatus.CANCELED
        || s == EnrollmentStatus.COMPLETED;
  }

  /** Evaluates constraints for the Enrollment aggregate and accumulates any validation problems. */
  private void collectValidationProblems() {
    if (identifier == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_STUDENT_BLANK);
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
