package com.pug.projects.domain.vos;

import com.pug.projects.domain.enums.ProjectsFieldErrorCodes;
import com.pug.shared.domain.DomainError;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

import java.util.UUID;

/**
 * Immutable Value Object (VO) representing the composite natural identifier for an Enrollment.
 * <p>
 * Extends {@link DomainError} to encapsulate and accumulate domain validation rules,
 * ensuring an enrollment is always tied to both a specific student and a specific project.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class EnrollmentIdentifier extends DomainError {

  /**
   * The unique identifier (Account ID) of the enrolled student.
   */
  UUID studentId;

  /**
   * The unique identifier of the project the student is enrolled in.
   */
  UUID projectId;

  /**
   * Constructs an {@code EnrollmentIdentifier} instance.
   *
   * @param studentId the unique identifier of the student
   * @param projectId the unique identifier of the project
   */
  @Builder(toBuilder = true)
  private EnrollmentIdentifier(UUID studentId, UUID projectId) {
    this.studentId = studentId;
    this.projectId = projectId;
  }

  /**
   * Factory method to create a new {@code EnrollmentIdentifier} instance.
   * <p>
   * The instance is created and immediately self-validated.
   *
   * @param studentId the unique identifier of the student
   * @param projectId the unique identifier of the project
   * @return a self-validated {@link EnrollmentIdentifier} instance
   */
  public static EnrollmentIdentifier factory(UUID studentId, UUID projectId) {
    EnrollmentIdentifier id =
            EnrollmentIdentifier.builder().studentId(studentId).projectId(projectId).build();
    id.collectValidationProblems();
    return id;
  }

  /**
   * Evaluates internal constraints and accumulates validation problems.
   * <p>
   * Business rules applied:
   * <ul>
   *   <li>The student ID must not be null (appends {@link ProjectsFieldErrorCodes#INVALID_ENROLLMENT_STUDENT_BLANK}).</li>
   *   <li>The project ID must not be null (appends {@link ProjectsFieldErrorCodes#INVALID_ENROLLMENT_PROJECT_BLANK}).</li>
   * </ul>
   */
  private void collectValidationProblems() {
    if (studentId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_STUDENT_BLANK);
    }
    if (projectId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK);
    }
  }
}