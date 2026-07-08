package br.org.catolicasc.pug.project.domain.vos;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Immutable Value Object (VO) representing the composite natural identifier for an Enrollment.
 *
 * <p>Extends {@link DomainError} to encapsulate and accumulate domain validation rules, ensuring an
 * enrollment is always tied to both a specific formerStudent and a specific project.
 */
@Getter
@Value
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class EnrollmentIdentifier extends DomainError {

  UUID formerStudentId;

  UUID projectId;

  @Builder(toBuilder = true)
  private EnrollmentIdentifier(UUID formerStudentId, UUID projectId) {
    this.formerStudentId = formerStudentId;
    this.projectId = projectId;
  }

  /**
   * Factory method to create a new {@code EnrollmentIdentifier} instance.
   *
   * <p>The instance is created and immediately self-validated.
   *
   * @param formerStudentId the unique identifier of the formerStudent
   * @param projectId the unique identifier of the project
   * @return a self-validated {@link EnrollmentIdentifier} instance
   */
  public static EnrollmentIdentifier factory(UUID formerStudentId, UUID projectId) {
    EnrollmentIdentifier id =
        EnrollmentIdentifier.builder()
            .formerStudentId(formerStudentId)
            .projectId(projectId)
            .build();
    id.collectValidationProblems();
    return id;
  }

  private void collectValidationProblems() {
    if (formerStudentId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_FORMER_STUDENT_BLANK);
    }
    if (projectId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK);
    }
  }
}
