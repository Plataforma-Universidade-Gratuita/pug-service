package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.infra.read.dtos.ProjectView;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Project data.
 *
 * <p>Following CQRS principles, this service handles the "Query" operations. It bypasses complex
 * domain logic and retrieves lightweight {@link ProjectView} Data Transfer Objects directly from
 * the underlying data store or search indices.
 */
public interface ProjectReadService {

  /**
   * Retrieves a read-only projection of a project based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the populated {@link ProjectView} DTO
   * @throws ResourceNotFoundException if no project matches the provided ID
   */
  ProjectView getViewById(UUID id);

  /**
   * Retrieves a comprehensive list of all projects registered in the system.
   *
   * @return a {@link List} containing all available {@link ProjectView} entries
   */
  List<ProjectView> listViews();

  /**
   * Retrieves a list of projects created by a specific member.
   *
   * @param accountId the unique identifier of the account
   * @return a {@link List} of projects created by the member
   */
  List<ProjectView> listViewsByCreatedBy(UUID accountId);

  /**
   * Retrieves a list of projects offered by a specific partner entity.
   *
   * @param entityId the unique identifier of the partner entity
   * @return a {@link List} of matching {@link ProjectView} entries
   */
  List<ProjectView> listViewsByEntityId(UUID entityId);

  /**
   * Executes a robust name-based search against the names of projects.
   *
   * <p>Leverages database-backed filtering to provide flexible matching, accent-insensitivity, and
   * name matching.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link ProjectView} entries
   */
  List<ProjectView> searchByName(String query);
}
