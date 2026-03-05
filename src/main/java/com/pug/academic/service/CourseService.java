package com.pug.academic.service;

import com.pug.academic.domain.Course;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;

import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Course} domain aggregates.
 * <p>
 * Following CQRS principles, this service handles the "Command" operations (Create, Update, Delete)
 * and strict domain-level retrievals. It enforces cross-cutting business rules (e.g., ensuring
 * course names are unique) and coordinates with the {@link SchoolService} to validate relational
 * integrity before persisting changes.
 */
public interface CourseService {

  /**
   * Instantiates and persists a new {@link Course} aggregate based on the provided command.
   * <p>
   * This method ensures the referenced {@link com.pug.academic.domain.School} exists
   * prior to persisting the new course to prevent orphaned references.
   *
   * @param cmd the structured command containing the data to create the new course
   * @return the fully instantiated and persisted {@link Course} aggregate
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a course with the exact same name already exists
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the associated school does not exist
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails (e.g., blank name)
   */
  Course save(CourseCreateCommand cmd);

  /**
   * Updates an existing {@link Course} using the provided data.
   * <p>
   * This method applies partial updates. If a new school ID is provided, the service
   * verifies the existence of the new school before permitting the move.
   *
   * @param id  the unique identifier (UUIDv7) of the course to update
   * @param cmd the structured command containing the new data for the course
   * @return the mutated and persisted {@link Course} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException  if the course does not exist, or if the new school does not exist
   * @throws com.pug.shared.exceptions.DuplicateResourceException if the updated name conflicts with an existing course
   * @throws com.pug.shared.exceptions.AppValidationException     if input validation fails
   */
  Course update(UUID id, CourseUpdateCommand cmd);

  /**
   * Removes a {@link Course} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the course to delete
   * @return {@code true} if the course was successfully deleted, {@code false} if it was not found
   */
  boolean delete(UUID id);

  /**
   * Retrieves a full {@link Course} domain aggregate by its unique identifier.
   * <p>
   * <b>Note:</b> This method is intended strictly for internal domain orchestration
   * (e.g., validating a course before enrolling a Student). For API responses,
   * use {@link CourseReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the course
   * @return the fully reconstituted {@link Course} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the course does not exist
   * @throws com.pug.shared.exceptions.AppValidationException    if the course exists but its stored state
   *                                                             violates strict domain invariants (data corruption)
   */
  Course getById(UUID id);
}