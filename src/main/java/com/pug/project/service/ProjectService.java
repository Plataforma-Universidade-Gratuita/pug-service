package com.pug.project.service;

import com.pug.project.domain.Project;
import com.pug.project.service.dtos.ProjectCreateCommand;
import com.pug.project.service.dtos.ProjectUpdateCommand;
import java.util.UUID;

/** Application service interface for managing the state of {@link Project} domain aggregates. */
public interface ProjectService {

  /**
   * Cancels an existing project.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the updated {@link Project}
   */
  Project cancel(UUID id);

  /**
   * Completes an existing project.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the updated {@link Project}
   */
  Project complete(UUID id);

  /**
   * Removes a {@link Project} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the project to delete
   * @return {@code true} if deleted, {@code false} if not found
   */
  boolean delete(UUID id);

  /**
   * Checks if any project exists for a specific entity.
   *
   * @param entityId the unique identifier of the entity
   * @return {@code true} if a project exists
   */
  boolean existsAnyByEntityId(UUID entityId);

  /**
   * Checks if any project was created by a specific staff account.
   *
   * @param accountId the unique identifier of the staff account
   * @return {@code true} if a project exists
   */
  boolean existsByCreatedBy(UUID accountId);

  /**
   * Retrieves a full {@link Project} aggregate by its identifier.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the {@link Project} aggregate
   */
  Project getById(UUID id);

  /**
   * Puts an existing project on hold.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the updated {@link Project}
   */
  Project putOnHold(UUID id);

  /**
   * Resumes a project that is currently on hold.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the updated {@link Project}
   */
  Project retake(UUID id);

  /**
   * Instantiates and persists a new {@link Project} aggregate.
   *
   * @param cmd the command containing project data
   * @return the persisted {@link Project}
   */
  Project save(ProjectCreateCommand cmd);

  /**
   * Starts a planned project, changing its state to IN_PROGRESS.
   *
   * @param id the unique identifier (UUID) of the project
   * @return the updated {@link Project}
   */
  Project start(UUID id);

  /**
   * Updates an existing {@link Project}.
   *
   * @param id the unique identifier of the project
   * @param cmd the update command
   * @return the updated {@link Project}
   */
  Project update(UUID id, ProjectUpdateCommand cmd);
}
