package br.org.catolicasc.pug.project.service.utils;

import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertise;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import java.util.UUID;

/**
 * Stateless utility class responsible for mapping raw command data into pure {@link
 * ProjectAreaOfExpertise} Domain Aggregates.
 *
 * <p>This processor centralizes the orchestration of the {@link ProjectAreaOfExpertise} factory,
 * ensuring that the application service layer remains focused on coordination and error handling
 * rather than on low-level aggregate construction details.
 */
public final class ProjectAreaOfExpertiseProcessor {

  private ProjectAreaOfExpertiseProcessor() {}

  /**
   * Processes raw inputs and constructs a new {@link ProjectAreaOfExpertise} domain aggregate.
   *
   * <p>This method delegates to {@link ProjectAreaOfExpertise#factory(UUID, UUID)}, which
   * immediately self-validates the created aggregate. The caller is responsible for inspecting
   * {@link ProjectAreaOfExpertise#hasFieldErrors()} and handling any collected validation problems
   * (for example, by throwing an {@link AppValidationException}).
   *
   * @param projectId the unique identifier of the project
   * @param areaOfExpertiseId the unique identifier of the areaOfExpertise
   * @return a fully instantiated {@link ProjectAreaOfExpertise} domain aggregate, potentially
   *     containing validation errors
   */
  public static ProjectAreaOfExpertise processCreateInput(UUID projectId, UUID areaOfExpertiseId) {
    return ProjectAreaOfExpertise.factory(projectId, areaOfExpertiseId);
  }
}
