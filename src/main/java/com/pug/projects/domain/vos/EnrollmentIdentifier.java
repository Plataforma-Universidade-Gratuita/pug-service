package com.pug.projects.domain.vos;

import com.pug.shared.domain.DomainError;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/** Value object representing the composite identifier for an Enrollment. */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
public class EnrollmentIdentifier extends DomainError {

  UUID studentId;
  UUID projectId;

  @Builder(toBuilder = true)
  private EnrollmentIdentifier(UUID studentId, UUID projectId) {
    this.studentId = studentId;
    this.projectId = projectId;
  }

  /**
   * Factory method to create an EnrollmentIdentifier.
   *
   * @param studentId The ID of the student.
   * @param projectId The ID of the project.
   * @return A validated EnrollmentIdentifier instance.
   */
  public static EnrollmentIdentifier factory(UUID studentId, UUID projectId) {
    EnrollmentIdentifier id =
        EnrollmentIdentifier.builder().studentId(studentId).projectId(projectId).build();
    id.collectValidationProblems();
    return id;
  }

  /** Validates that both IDs are present. */
  private void collectValidationProblems() {
    validateForeignKeyField(studentId, "studentId");
    validateForeignKeyField(projectId, "projectId");
  }
}
