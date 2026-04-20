package br.org.catolicasc.pug.project.service.utils;

import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.project.domain.ProjectBySchool;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw command data into pure {@link
 * ProjectBySchool} Domain Aggregates.
 *
 * <p>This processor centralizes the orchestration of the {@link ProjectBySchool} factory, ensuring
 * that the application service layer remains focused on coordination and error handling rather than
 * on low-level aggregate construction details.
 */
public final class ProjectBySchoolProcessor {

  /** Private constructor to prevent instantiation of utility class. */
  private ProjectBySchoolProcessor() {}

  /**
   * Processes raw inputs and constructs a new {@link ProjectBySchool} domain aggregate.
   *
   * <p>This method delegates to {@link ProjectBySchool#factory(UUID, UUID)}, which immediately
   * self-validates the created aggregate. The caller is responsible for inspecting {@link
   * ProjectBySchool#hasFieldErrors()} and handling any collected validation problems (for example,
   * by throwing an {@link AppValidationException}).
   *
   * @param projectId the unique identifier of the project
   * @param schoolId the unique identifier of the school
   * @return a fully instantiated {@link ProjectBySchool} domain aggregate, potentially containing
   *     validation errors
   */
  public static ProjectBySchool processCreateInput(UUID projectId, UUID schoolId) {
    return ProjectBySchool.factory(projectId, schoolId);
  }
}
