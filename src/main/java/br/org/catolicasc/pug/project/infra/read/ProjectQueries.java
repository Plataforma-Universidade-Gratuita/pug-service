package br.org.catolicasc.pug.project.infra.read;

import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing queries against Projects.
 *
 * <p>This interface represents the "Query" side of a CQRS architecture, defining operations for
 * retrieving project data directly into lightweight {@link ProjectView} projections.
 */
public interface ProjectQueries {

  /**
   * Retrieves a read-only view of a project based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project to find
   * @return an {@link Optional} containing the found {@link ProjectView}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<ProjectView> findOptionalById(UUID id);

  /**
   * Retrieves a comprehensive list of all projects registered in the system.
   *
   * @return a {@link List} containing all available {@link ProjectView} entries
   */
  List<ProjectView> listAllProjects();

  /**
   * Retrieves a list of all projects created by a specific staff account.
   *
   * @param accountId the unique identifier (UUID) of the staff account
   * @return a {@link List} of projects created by the staff member
   */
  List<ProjectView> listByCreatedBy(UUID accountId);

  /**
   * Retrieves a list of projects offered by a specific partner entity.
   *
   * @param entityId the unique identifier (UUID) of the partner entity
   * @return a {@link List} of matching {@link ProjectView} entries
   */
  List<ProjectView> listByEntityId(UUID entityId);

  /**
   * Retrieves a list of projects matching the provided list of unique identifiers.
   *
   * @param ids a {@link List} of project UUIDs to retrieve
   * @return a {@link List} of {@link ProjectView} entries corresponding to the provided IDs
   */
  List<ProjectView> listByIds(List<UUID> ids);

  /**
   * Executes a robust name-based search against the names of projects.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of {@link ProjectView} entries matching the search criteria
   */
  List<ProjectView> searchByName(String query);
}
