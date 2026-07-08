package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
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
 * <p>Following CQRS principles, this service handles command-side operations for the
 * project-to-area-of-expertise link, including creation, write-side listing, and deletion of
 * associations. It orchestrates validation and delegates persistence concerns to the {@link
 * ProjectAreaOfExpertiseRepository}.
 */
public interface ProjectAreaOfExpertiseService {

  /**
   * Lists all academic areas of expertise linked to the given project.
   *
   * @param projectId the unique identifier of the project
   * @return the linked {@link AreaOfExpertise} aggregates, or an empty list when none exist
   */
  List<AreaOfExpertise> listByProjects(UUID projectId);

  /**
   * Creates new associations between a project and one or more areas of expertise.
   *
   * <p>This operation constructs a {@link ProjectAreaOfExpertise} aggregate for each provided area
   * identifier, validates every association, and persists the valid ones.
   *
   * @param projectId the unique identifier of the project to link
   * @param areaOfExpertiseIds the unique identifiers of the areas of expertise to associate with
   *     the project
   * @return the persisted {@link ProjectAreaOfExpertise} aggregates corresponding to the new
   *     associations
   * @throws AppValidationException if any created association violates domain constraints
   */
  List<ProjectAreaOfExpertise> save(UUID projectId, List<UUID> areaOfExpertiseIds);

  /**
   * Removes a specific association between a project and an area of expertise.
   *
   * @param projectId the unique identifier of the project
   * @param areaOfExpertiseId the unique identifier of the area of expertise
   * @return {@code true} if an association was deleted, or {@code false} otherwise
   */
  boolean delete(UUID projectId, UUID areaOfExpertiseId);

  /**
   * Removes all associations for the specified project.
   *
   * @param projectId the unique identifier of the project
   * @return the number of associations deleted, or {@code 0} when none exist
   */
  long deleteAllByProjectId(UUID projectId);

  /**
   * Removes all associations for the specified area of expertise.
   *
   * @param areaOfExpertiseId the unique identifier of the area of expertise
   * @return the number of associations deleted, or {@code 0} when none exist
   */
  long deleteAllByAreaOfExpertiseId(UUID areaOfExpertiseId);
}
