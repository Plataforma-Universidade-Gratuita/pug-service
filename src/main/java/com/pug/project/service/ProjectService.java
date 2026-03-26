package com.pug.project.service;

import com.pug.project.domain.Project;
import com.pug.project.domain.enums.ProjectStatus;
import com.pug.project.service.dtos.ProjectCreateCommand;
import com.pug.project.service.dtos.ProjectUpdateCommand;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Project} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) and strict domain-level retrievals. It manages the project lifecycle transitions and
 * enforces business invariants.
 */
public interface ProjectService {

  /**
   * Removes a {@link Project} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project to delete
   * @return {@code true} if deleted, {@code false} if not found
   */
  boolean delete(UUID id);

  /**
   * Checks if any project was created by a specific account.
   *
   * @param accountId the unique identifier of the account
   * @return {@code true} if a project exists
   */
  boolean existsByCreatedBy(UUID accountId);

  /**
   * Checks if any project exists for a specific entity.
   *
   * @param entityId the unique identifier of the entity
   * @return {@code true} if a project exists
   */
  boolean existsAnyByEntityId(UUID entityId);

  /**
   * Retrieves a full {@link Project} aggregate by its identifier.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the {@link Project} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if not found
   */
  Project getById(UUID id);

  /**
   * Instantiates and persists a new {@link Project} aggregate.
   *
   * @param cmd the command containing project data
   * @return the persisted {@link Project}
   */
  Project save(ProjectCreateCommand cmd);

  /**
   * Transitions a project to a new status.
   *
   * @param id the unique identifier (UUID) of the project
   * @param status the target {@link ProjectStatus}
   * @return the updated {@link Project}
   * @throws com.pug.shared.exceptions.BusinessRuleException if the status transition is invalid
   */
  Project transitionStatus(UUID id, ProjectStatus status);

  /**
   * Updates an existing {@link Project}.
   *
   * @param id the unique identifier of the project
   * @param cmd the update command
   * @return the updated {@link Project}
   */
  Project update(UUID id, ProjectUpdateCommand cmd);
}
