package br.org.catolicasc.pug.project.domain;

import br.org.catolicasc.pug.project.domain.enums.ProjectsFieldErrorCodes;
import br.org.catolicasc.pug.shared.domain.DomainError;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Domain aggregate representing the association between a Project and a School.
 *
 * <p>This aggregate ensures that a project is strictly linked to a valid areaOfExpertise. It provides
 * methods to manage and transition this association, extending {@link DomainError} for structural
 * validation.
 */
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ProjectAreaOfExpertise extends DomainError {

  /** The unique identifier of the project. */
  private final UUID projectId;

  /** The unique identifier of the areaOfExpertise. */
  private final UUID areaOfExpertiseId;

  /**
   * Factory method to create a new {@code ProjectsBySchool} instance.
   *
   * @param projectId the unique identifier of the project
   * @param areaOfExpertiseId the unique identifier of the areaOfExpertise
   * @return a self-validated {@link ProjectAreaOfExpertise} instance
   */
  public static ProjectAreaOfExpertise factory(UUID projectId, UUID areaOfExpertiseId) {
    ProjectAreaOfExpertise association =
        ProjectAreaOfExpertise.builder().projectId(projectId).areaOfExpertiseId(areaOfExpertiseId).build();
    association.collectValidationProblems();
    return association;
  }

  /** Evaluates constraints for the aggregate and accumulates any validation problems. */
  private void collectValidationProblems() {
    if (projectId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_ENROLLMENT_PROJECT_BLANK);
    }
    if (areaOfExpertiseId == null) {
      addFieldError(ProjectsFieldErrorCodes.INVALID_AREA_OF_EXPERTISE_ID_BLANK);
    }
  }
}
