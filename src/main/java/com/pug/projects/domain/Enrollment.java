package com.pug.projects.domain;

import com.pug.academic.domain.Student;
import com.pug.projects.domain.enums.EnrollmentStatus;
import com.pug.projects.domain.enums.ProjectsErrorCodes;
import com.pug.projects.domain.vos.EnrollmentInfo;
import com.pug.shared.domain.DomainError;
import com.pug.shared.exceptions.AppValidationException;
import com.pug.shared.time.TimeProvider;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/** Domain entity representing an Enrollment. */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class Enrollment extends DomainError {
  private final Student student;
  private final Project project;
  private final EnrollmentStatus status;
  private final EnrollmentInfo enrollmentInfo;

  /**
   * Factory method to create a new Enrollment with PENDING status.
   *
   * @param student the student enrolling
   * @param project the project to enroll in
   * @param time the time provider for current time
   * @return a new Enrollment instance
   */
  public static Enrollment factory(Student student, Project project, TimeProvider time) {
    var now = OffsetDateTime.now(time.clock());
    Enrollment enrollment =
        Enrollment.builder()
            .student(student)
            .project(project)
            .status(EnrollmentStatus.PENDING)
            .enrollmentInfo(EnrollmentInfo.factory(now, null, null))
            .build();

    enrollment.collectValidationProblems();
    return enrollment;
  }

  /**
   * Change the status of the enrollment, updating relevant timestamps.
   *
   * @param newStatus the new status to set
   * @param time the time provider for current time
   * @return a new Enrollment instance with updated status
   */
  public Enrollment changeStatus(EnrollmentStatus newStatus, TimeProvider time) {
    if (this.status == newStatus) {
      return this;
    }

    var now = OffsetDateTime.now(time.clock());
    OffsetDateTime accepted = this.enrollmentInfo.getAcceptedAt();
    OffsetDateTime closing = this.enrollmentInfo.getClosingStatusAt();

    if (newStatus == EnrollmentStatus.APPROVED) {
      accepted = now;
      closing = null;
    } else if (isClosingStatus(newStatus)) {
      closing = now;
    }

    Enrollment updated =
        this.toBuilder()
            .status(newStatus)
            .enrollmentInfo(
                EnrollmentInfo.factory(enrollmentInfo.getRequestAt(), accepted, closing))
            .build();

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

  /** Validates the Enrollment entity and accumulates errors if any. */
  private void collectValidationProblems() {
    if (student == null) {
      addError(
          new AppValidationException.Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_STUDENT_BLANK));
    }
    if (project == null) {
      addError(
          new AppValidationException.Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK));
    }
    if (status == null) {
      addError(
          new AppValidationException.Problem(ProjectsErrorCodes.INVALID_ENROLLMENT_STATUS_BLANK));
    }
    if (enrollmentInfo != null) {
      if (enrollmentInfo.getRequestAt() == null) {
        addError(
            new AppValidationException.Problem(
                ProjectsErrorCodes.INVALID_ENROLLMENT_REQUEST_AT_BLANK));
      }
      if (enrollmentInfo.getAcceptedAt() != null
          && enrollmentInfo.getAcceptedAt().isBefore(enrollmentInfo.getRequestAt())) {
        addError(
            new AppValidationException.Problem(
                ProjectsErrorCodes.INVALID_ENROLLMENT_DATES_INVALID));
      }
    }
  }
}
