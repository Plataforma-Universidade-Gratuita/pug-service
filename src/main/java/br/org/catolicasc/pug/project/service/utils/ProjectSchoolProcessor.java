package br.org.catolicasc.pug.project.service.utils;

import br.org.catolicasc.pug.project.domain.ProjectSchool;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw command data into pure {@link ProjectSchool}
 * Domain Aggregates.
 *
 * <p>This processor centralizes the orchestration of the {@link ProjectSchool} factory, ensuring
 * that the application service layer remains focused on coordination and error handling rather than
 * on low-level aggregate construction details.
 */
public final class ProjectSchoolProcessor {

  /** Private constructor to prevent instantiation of utility class. */
  private ProjectSchoolProcessor() {}

  /**
   * Processes raw inputs and constructs a new {@link ProjectSchool} domain aggregate.
   *
   * <p>This method delegates to {@link ProjectSchool#factory(UUID, UUID)}, which immediately
   * self-validates the created aggregate. The caller is responsible for inspecting {@link
   * ProjectSchool#hasFieldErrors()} and handling any collected validation problems (for example, by
   * throwing an {@link AppValidationException}).
   *
   * @param projectId the unique identifier of the project
   * @param areaOfExpertiseId the unique identifier of the areaOfExpertise
   * @return a fully instantiated {@link ProjectSchool} domain aggregate, potentially containing
   *     validation errors
   */
  public static ProjectSchool processCreateInput(UUID projectId, UUID areaOfExpertiseId) {
    return ProjectSchool.factory(projectId, areaOfExpertiseId);
  }
}
