package br.org.catolicasc.pug.academic.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Student} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting student
 * enrollments. It abstracts the underlying data storage mechanism to maintain a pure,
 * infrastructure-agnostic domain model.
 */
public interface StudentRepository {

  /**
   * Removes a {@link Student} from the repository based on their linked account ID.
   *
   * @param id the unique identifier (UUID) of the student's linked account
   * @return {@code true} if the student was successfully deleted, {@code false} if not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether any of the specified academic registrations already exist in the repository.
   *
   * @param registrations a {@link List} of academic registration strings to check
   * @return {@code true} if at least one registration exists, {@code false} otherwise
   */
  boolean existsAnyByRegistrations(List<String> registrations);

  /**
   * Checks whether any {@link Student} associated with the specified course identifier exists in
   * the repository.
   *
   * <p>This query enforces relational integrity, ensuring academic courses are not deleted if they
   * still have students actively enrolled in them.
   *
   * @param courseId the unique identifier (UUID) of the enrolled course
   * @return {@code true} if at least one student is enrolled in the course, {@code false} otherwise
   */
  boolean existsByCourseId(UUID courseId);

  /**
   * Checks whether a {@link Student} with the specified academic registration already exists.
   *
   * @param registration the raw academic registration string to check
   * @return {@code true} if a student with the given registration exists, {@code false} otherwise
   */
  boolean existsByRegistration(String registration);

  /**
   * Retrieves a {@link Student} by their linked account identifier.
   *
   * <p>When a student is reconstituted from the persistence layer, it might contain validation
   * errors (verifiable via {@link Student#hasFieldErrors()}) if the stored data is inconsistent
   * with current domain rules.
   *
   * @param id the unique identifier (UUID) of the student's linked account
   * @return an {@link Optional} containing the found {@link Student}, or {@link Optional#empty()}
   *     if not found
   */
  Optional<Student> findOptionalById(UUID id);

  /**
   * Persists a newly created {@link Student} aggregate into the repository.
   *
   * @param entity the {@link Student} aggregate to persist
   * @return the fully persisted {@link Student} instance
   */
  Student persist(Student entity);

  /**
   * Persists a collection of newly created {@link Student} aggregates in a single batch.
   *
   * @param students a {@link List} of {@link Student} aggregates to persist
   * @return the fully persisted {@link List} of {@link Student} instances
   */
  List<Student> persistAll(List<Student> students);

  /**
   * Updates the state of an existing {@link Student} aggregate in the repository.
   *
   * @param entity the {@link Student} instance containing the updated state
   */
  void update(Student entity);
}
