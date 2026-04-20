package br.org.catolicasc.pug.project.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Project} and {@link ProjectBySchool} aggregate
 * roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting
 * projects and their school associations. It abstracts the underlying data storage mechanism to
 * maintain a pure, infrastructure-agnostic domain model.
 */
public interface ProjectRepository {

  /**
   * Removes a {@link Project} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the project to delete
   * @return {@code true} if the project was successfully deleted, {@code false} if it was not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether any project was created by a specific staff member's account.
   *
   * @param accountId the unique identifier (UUID) of the staff account
   * @return {@code true} if at least one project was created by the account, {@code false}
   *     otherwise
   */
  boolean existsByCreatedBy(UUID accountId);

  /**
   * Checks whether any project exists that is associated with a specific partner entity.
   *
   * @param entityId the unique identifier of the partner entity to check
   * @return {@code true} if at least one project belongs to the given entity, {@code false}
   *     otherwise
   */
  boolean existsByEntityId(UUID entityId);

  /**
   * Checks whether a project with the specified name already exists for a given partner entity.
   *
   * @param name the exact name of the project
   * @param entityId the unique identifier of the partner organization
   * @return {@code true} if a matching project exists, {@code false} otherwise
   */
  boolean existsByNameAndEntityId(String name, UUID entityId);

  /**
   * Retrieves a {@link Project} by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project
   * @return an {@link Optional} containing the {@link Project} if found, or {@link
   *     Optional#empty()} if not
   */
  Optional<Project> findOptionalById(UUID id);

  /**
   * Persists a newly created {@link Project} aggregate into the repository.
   *
   * @param entity the {@link Project} aggregate to persist
   * @return the fully persisted {@link Project} instance
   */
  Project persist(Project entity);

  /**
   * Updates the state of an existing {@link Project} aggregate in the repository.
   *
   * @param entity the {@link Project} instance containing the updated state
   */
  void update(Project entity);
}
