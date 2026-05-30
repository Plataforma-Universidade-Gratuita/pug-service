package br.org.catolicasc.pug.academic.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Course} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting
 * academic courses. It abstracts the underlying data storage mechanism to maintain a pure,
 * infrastructure-agnostic domain model.
 */
public interface CourseRepository {

  /**
   * Removes a {@link Course} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the course to delete
   * @return {@code true} if the course was successfully deleted, {@code false} if it was not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether a {@link Course} with the specified name already exists in the repository.
   *
   * @param name the exact name of the course
   * @return {@code true} if a course with the given name exists, {@code false} otherwise
   */
  boolean existsByName(String name);

  /**
   * Checks whether any {@link Course} associated with the specified areaOfExpertise identifier exists in the
   * repository.
   *
   * <p>This query is crucial for enforcing relational integrity, ensuring areasOfExpertise are not
   * deleted if they still offer active courses.
   *
   * @param areaOfExpertiseId the unique identifier (UUID) of the associated areaOfExpertise
   * @return {@code true} if at least one course is linked to the areaOfExpertise, {@code false} otherwise
   */
  boolean existsByAreaOfExpertiseId(UUID areaOfExpertiseId);

  /**
   * Retrieves a {@link Course} by its unique identifier.
   *
   * <p>When a course is reconstituted from the persistence layer, it typically undergoes the same
   * domain validations as a newly created aggregate. Therefore, the returned {@link Course} might
   * contain validation errors (verifiable via {@link Course#hasFieldErrors()}) if the stored data
   * violates current domain rules.
   *
   * @param id the unique identifier (UUID) of the course
   * @return an {@link Optional} containing the found {@link Course}, or {@link Optional#empty()} if
   *     not found
   */
  Optional<Course> findOptionalById(UUID id);

  /**
   * Persists a newly created {@link Course} aggregate into the repository.
   *
   * @param entity the {@link Course} aggregate to persist
   * @return the fully persisted {@link Course} instance
   */
  Course persist(Course entity);

  /**
   * Updates the state of an existing {@link Course} aggregate in the repository.
   *
   * @param entity the {@link Course} instance containing the updated state
   */
  void update(Course entity);
}
