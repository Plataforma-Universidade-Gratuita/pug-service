package br.org.catolicasc.pug.project.domain;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Domain aggregate representing the association between a Project and a School.
 *
 * <p>This aggregate ensures that a project is strictly linked to a valid school. It provides
 * methods to manage and transition this association, extending {@link DomainError} for structural
 * validation.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class ProjectBySchool extends DomainError {

  /** The unique identifier of the project. */
  private final UUID projectId;

  /** The unique identifier of the school. */
  private final UUID schoolId;

  /**
   * Factory method to create a new {@code ProjectsBySchool} instance.
   *
   * @param projectId the unique identifier of the project
   * @param schoolId the unique identifier of the school
   * @return a self-validated {@link ProjectBySchool} instance
   */
  public static ProjectBySchool factory(UUID projectId, UUID schoolId) {
    ProjectBySchool association =
        ProjectBySchool.builder().projectId(projectId).schoolId(schoolId).build();
    association.collectValidationProblems();
    return association;
  }

  /** Evaluates constraints for the aggregate and accumulates any validation problems. */
  private void collectValidationProblems() {
    if (projectId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK);
    }
    if (schoolId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_SCHOOL_ID_BLANK);
    }
  }
}
