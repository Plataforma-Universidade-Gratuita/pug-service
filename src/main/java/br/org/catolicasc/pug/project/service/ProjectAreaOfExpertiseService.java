package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertise;
import br.org.catolicasc.pug.project.domain.ProjectAreaOfExpertiseRepository;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface for managing the association between {@link Project} and academic
 * areas of expertise via {@link ProjectAreaOfExpertise}.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations for the
 * project–areaOfExpertise link: creation and deletion of associations. It orchestrates validation and
 * delegates persistence concerns to the {@link ProjectAreaOfExpertiseRepository}.
 */
public interface ProjectAreaOfExpertiseService {

  /**
   * Creates new associations between a project and one or more areaOfExpertises.
   *
   * <p>This operation:
   *
   * <ul>
   *   <li>Constructs a {@link ProjectAreaOfExpertise} aggregate for each provided areaOfExpertise ID via its factory
   *       method,
   *   <li>validates structural constraints for every association (non-null IDs), and
   *   <li>persists all valid associations using the underlying repository.
   * </ul>
   *
   * @param projectId the unique identifier of the project to link
   * @param areaOfExpertiseIds the unique identifiers of the areaOfExpertises to link to the project
   * @return a {@link List} of fully instantiated and persisted {@link ProjectAreaOfExpertise} aggregates
   *     corresponding to the provided areaOfExpertise IDs
   * @throws AppValidationException if any created association violates domain constraints (e.g.,
   *     null IDs)
   */
  List<ProjectAreaOfExpertise> save(UUID projectId, List<UUID> areaOfExpertiseIds);

  /**
   * Removes a specific association between a project and a areaOfExpertise.
   *
   * <p>This operation is expected to be idempotent: if the association does not exist, the
   * implementation should simply return {@code false} without raising an exception.
   *
   * @param projectId the unique identifier of the project
   * @param areaOfExpertiseId the unique identifier of the areaOfExpertise
   * @return {@code true} if an association was deleted, {@code false} otherwise
   */
  boolean delete(UUID projectId, UUID areaOfExpertiseId);

  /**
   * Removes all associations for the specified project.
   *
   * <p>This is typically used when deleting or restructuring a project and needing to clean up all
   * of its areaOfExpertise links in a single operation.
   *
   * @param projectId the unique identifier of the project
   * @return the number of associations deleted (zero if {@code projectId} is {@code null} or no
   *     associations exist)
   */
  long deleteAllByProjectId(UUID projectId);

  /**
   * Removes all associations for the specified areaOfExpertise.
   *
   * <p>This is typically used when deleting or restructuring a areaOfExpertise and needing to clean up all
   * of its project links in a single operation.
   *
   * @param areaOfExpertiseId the unique identifier of the areaOfExpertise
   * @return the number of associations deleted (zero if {@code areaOfExpertiseId} is {@code null}
   *     or no associations exist)
   */
  long deleteAllByAreaOfExpertiseId(UUID areaOfExpertiseId);
}
