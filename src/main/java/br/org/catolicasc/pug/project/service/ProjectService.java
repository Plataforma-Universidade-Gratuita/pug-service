/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Project;
import br.org.catolicasc.pug.project.domain.enums.ProjectStatus;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectCreateCommand;
import br.org.catolicasc.pug.project.service.dtos.projects.ProjectUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Project} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles command-side project use cases, including
 * lifecycle transitions, progress updates, creation, updates, deletion, and strict aggregate
 * retrieval.
 */
public interface ProjectService {

  /**
   * Adds completed hours to a project's accumulated progress.
   *
   * <p>If the new completed-hours total reaches the amount of offered hours, the project may be
   * automatically concluded as part of the same workflow.
   *
   * @param id the unique identifier of the project
   * @param hours the amount of hours to add
   * @return the updated {@link Project} aggregate
   * @throws ResourceNotFoundException if the project does not exist
   * @throws AppValidationException if the resulting project state violates domain constraints
   */
  Project addCompletedHours(UUID id, BigDecimal hours);

  /**
   * Removes completed hours from a project's accumulated progress.
   *
   * @param id the unique identifier of the project
   * @param hours the amount of hours to remove
   * @return the updated {@link Project} aggregate
   * @throws ResourceNotFoundException if the project does not exist
   * @throws AppValidationException if the resulting project state violates domain constraints
   */
  Project removeCompletedHours(UUID id, BigDecimal hours);

  /**
   * Removes a {@link Project} from the system by its unique identifier.
   *
   * @param id the unique identifier of the project to delete
   * @return {@code true} if deleted, or {@code false} when the project does not exist
   * @throws BusinessRuleException if the project still has linked enrollments and cannot be deleted
   */
  boolean delete(UUID id);

  /**
   * Checks if any project was created by a specific account.
   *
   * @param accountId the unique identifier of the creator account
   * @return {@code true} if at least one project was created by the account, or {@code false}
   *     otherwise
   */
  boolean existsByCreatedBy(UUID accountId);

  /**
   * Checks if any project exists for a specific partner entity.
   *
   * @param entityId the unique identifier of the partner entity
   * @return {@code true} if at least one project is linked to the entity, or {@code false}
   *     otherwise
   */
  boolean existsAnyByEntityId(UUID entityId);

  /**
   * Validates whether the given project is currently in progress.
   *
   * @param projectId the unique identifier of the project
   * @throws BusinessRuleException if the project is not currently in progress
   */
  void validateIsInProgress(UUID projectId);

  /**
   * Retrieves a full {@link Project} aggregate by its identifier.
   *
   * @param id the unique identifier of the project
   * @return the matching {@link Project} aggregate
   * @throws ResourceNotFoundException if the project does not exist
   */
  Project getById(UUID id);

  /**
   * Instantiates and persists a new {@link Project} aggregate.
   *
   * @param cmd the command containing the project data to persist
   * @return the persisted {@link Project} aggregate
   * @throws ResourceNotFoundException if the referenced partner entity does not exist
   * @throws DuplicateResourceException if another project with the same name already exists for the
   *     entity
   * @throws AppValidationException if the requested project state violates domain constraints
   */
  Project save(ProjectCreateCommand cmd);

  /**
   * Transitions a project to a new lifecycle status.
   *
   * @param id the unique identifier of the project
   * @param status the target {@link ProjectStatus}
   * @return the updated {@link Project} aggregate
   * @throws ResourceNotFoundException if the project does not exist
   * @throws BusinessRuleException if the requested lifecycle transition is invalid
   */
  Project transitionStatus(UUID id, ProjectStatus status);

  /**
   * Updates an existing {@link Project} without changing its lifecycle status.
   *
   * @param id the unique identifier of the project to update
   * @param cmd the update command containing the mutable project data
   * @return the updated {@link Project} aggregate
   * @throws ResourceNotFoundException if the project does not exist
   * @throws DuplicateResourceException if another project with the requested name already exists
   *     for the same entity
   * @throws AppValidationException if the requested update violates domain constraints
   */
  Project update(UUID id, ProjectUpdateCommand cmd);
}
