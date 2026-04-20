package br.org.catolicasc.pug.academic.service;

import br.org.catolicasc.pug.academic.domain.Course;
import br.org.catolicasc.pug.academic.domain.School;
import br.org.catolicasc.pug.academic.service.dtos.CourseCreateCommand;
import br.org.catolicasc.pug.academic.service.dtos.CourseUpdateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Course} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) and strict domain-level retrievals. It enforces cross-cutting business rules (e.g.,
 * ensuring course names are unique) and coordinates with the {@link SchoolService} to validate
 * relational integrity before persisting changes.
 */
public interface CourseService {

  /**
   * Removes a {@link Course} from the system by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the course to delete
   * @return {@code true} if the course was successfully deleted, {@code false} if it was not found
   */
  boolean delete(UUID id);

  /**
   * Checks whether any academic course associated with the specified school identifier exists.
   *
   * <p>This method is utilized to enforce relational integrity, ensuring that a {@link School}
   * cannot be deleted if it still has active courses linked to it.
   *
   * @param schoolId the unique identifier (UUID) of the school to check
   * @return {@code true} if at least one course is linked to the school, {@code false} otherwise
   */
  boolean existsAnyBySchoolId(UUID schoolId);

  /**
   * Retrieves a full {@link Course} domain aggregate by its unique identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration (e.g.,
   * validating a course before enrolling a Student). For API responses, use {@link
   * CourseReadService#getViewById(UUID)} instead.
   *
   * @param id the unique identifier (UUID) of the course
   * @return the fully reconstituted {@link Course} aggregate
   * @throws ResourceNotFoundException if the course does not exist
   * @throws AppValidationException if the course exists but its stored state violates strict domain
   *     invariants (data corruption)
   */
  Course getById(UUID id);

  /**
   * Instantiates and persists a new {@link Course} aggregate based on the provided command.
   *
   * <p>This method ensures the referenced {@link School} exists prior to persisting the new course
   * to prevent orphaned references.
   *
   * @param cmd the structured command containing the data to create the new course
   * @return the fully instantiated and persisted {@link Course} aggregate
   * @throws DuplicateResourceException if a course with the exact same name already exists
   * @throws ResourceNotFoundException if the associated school does not exist
   * @throws AppValidationException if input validation fails (e.g., blank name)
   */
  Course save(CourseCreateCommand cmd);

  /**
   * Updates an existing {@link Course} using the provided data.
   *
   * <p>This method applies partial updates. If a new school ID is provided, the service verifies
   * the existence of the new school before permitting the move.
   *
   * @param id the unique identifier (UUIDv7) of the course to update
   * @param cmd the structured command containing the new data for the course
   * @return the mutated and persisted {@link Course} aggregate
   * @throws ResourceNotFoundException if the course does not exist, or if the new school does not
   *     exist
   * @throws DuplicateResourceException if the updated name conflicts with an existing course
   * @throws AppValidationException if input validation fails
   */
  Course update(UUID id, CourseUpdateCommand cmd);
}
