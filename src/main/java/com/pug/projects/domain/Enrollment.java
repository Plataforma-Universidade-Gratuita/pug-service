package com.pug.projects.domain;

import com.pug.academic.domain.Student;
import com.pug.projects.domain.enums.EnrollmentStatus;
import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.projects.domain.vos.EnrollmentIdentifier;
import com.pug.projects.domain.vos.EnrollmentInfo;
import com.pug.shared.domain.DomainError;
import com.pug.shared.domain.Problem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Domain entityId representing an Enrollment. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Enrollment extends DomainError {

  EnrollmentIdentifier identifier;
  EnrollmentStatus status;
  EnrollmentInfo enrollmentInfo;

  /**
   * Factory method to create a new Enrollment with PENDING status.
   *
   * @param student the student enrolling
   * @param project the project to enroll in
   * @return a new Enrollment instance
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
   * Change the status of the enrollment, updating relevant timestamps via EnrollmentInfo.
   *
   * @param newStatus the new status to set
   * @return a new Enrollment instance with updated status
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

  /** Helper method to determine if a status is a closing status. */
  private boolean isClosingStatus(EnrollmentStatus s) {
    return s == EnrollmentStatus.REJECTED
        || s == EnrollmentStatus.EXITED
        || s == EnrollmentStatus.REMOVED
        || s == EnrollmentStatus.CANCELED
        || s == EnrollmentStatus.COMPLETED;
  }

  /** Validates the Enrollment entityId and accumulates errors if any. */
  private void collectValidationProblems() {
    if (identifier == null) {
      addError(new Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_STUDENT_BLANK));
      addError(new Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK));
    } else if (identifier.hasErrors()) {
      addErrors(identifier.getProblems());
    }

    if (status == null) {
      addError(new Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_STATUS_BLANK));
    }

    if (enrollmentInfo != null && enrollmentInfo.hasErrors()) {
      addErrors(enrollmentInfo.getProblems());
    }
  }
}
