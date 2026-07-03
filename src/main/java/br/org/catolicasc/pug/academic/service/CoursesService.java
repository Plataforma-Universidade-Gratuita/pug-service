/*
 * Copyright (c) 2026 Mateus Fernandes and Plataforma Universidade Gratuita.
 * All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized use,
 * copying, modification, distribution, or deployment is prohibited.
 */

package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.domain.AreaOfExpertise;
import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.courses.CourseUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Course} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the command-side operations and strict
 * domain-level retrievals for academic courses.
 */
public interface CoursesService {

  /**
   * Removes a {@link Course} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the course to delete
   * @return {@code true} if the course was successfully deleted, {@code false} if it was not found
   * @throws BusinessRuleException if the course still has linked former students
   */
  boolean delete(UUID id);

  /**
   * Checks whether any academic course associated with the specified areaOfExpertise identifier
   * exists.
   *
   * <p>This method is utilized to enforce relational integrity, ensuring that a {@link
   * AreaOfExpertise} cannot be deleted if it still has active courses linked to it.
   *
   * @param areaOfExpertiseId the unique identifier (UUID) of the areaOfExpertise to check
   * @return {@code true} if at least one course is linked to the areaOfExpertise, {@code false}
   *     otherwise
   */
  boolean existsAnyByAreaOfExpertiseId(UUID areaOfExpertiseId);

  /**
   * Retrieves a full {@link Course} domain aggregate by its unique identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration. For API
   * responses, use {@link CoursesReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the course
   * @return the fully reconstituted {@link Course} aggregate
   * @throws ResourceNotFoundException if the course does not exist
   * @throws AppValidationException if the course exists but its stored state violates strict domain
   *     invariants
   */
  Course getById(UUID id);

  /**
   * Instantiates and persists a new {@link Course} aggregate based on the provided command.
   *
   * @param cmd the structured command containing the data to create the new course
   * @return the fully instantiated and persisted {@link Course} aggregate
   * @throws DuplicateResourceException if a course with the exact same name already exists
   * @throws ResourceNotFoundException if the associated areaOfExpertise does not exist
   * @throws AppValidationException if input validation fails
   */
  Course save(CourseCreateCommand cmd);

  /**
   * Updates an existing {@link Course} using the provided data.
   *
   * @param id the unique identifier (UUIDv7) of the course to update
   * @param cmd the structured command containing the new data for the course
   * @return the mutated and persisted {@link Course} aggregate
   * @throws ResourceNotFoundException if the course does not exist, or if the new areaOfExpertise
   *     does not exist
   * @throws DuplicateResourceException if the updated name conflicts with an existing course
   * @throws AppValidationException if input validation fails
   */
  Course update(UUID id, CourseUpdateCommand cmd);
}
