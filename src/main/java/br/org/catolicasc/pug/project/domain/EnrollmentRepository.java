package br.org.catolicasc.pug.project.domain;

import br.org.catolicasc.pug.academic.domain.FormerStudent;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Enrollment} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting
 * formerStudent enrollments in projects. It utilizes a composite natural key represented by {@link
 * EnrollmentIdentifier} (Project ID + FormerStudent ID).
 */
public interface EnrollmentRepository {

  /**
   * Removes an {@link Enrollment} from the repository based on its composite identifier.
   *
   * <p>This operation is expected to be idempotent: if the enrollment does not exist, the
   * implementation should simply return {@code false} without raising an exception.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + formerStudent)
   * @return {@code true} if the enrollment was successfully deleted, {@code false} if it was not
   *     found
   */
  boolean deleteById(EnrollmentIdentifier identifier);

  /**
   * Checks whether a specific enrollment exists in the repository.
   *
   * <p>The lookup is performed using the composite natural key encapsulated by {@link
   * EnrollmentIdentifier}.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + formerStudent)
   * @return {@code true} if an enrollment with the given identifier exists, {@code false} otherwise
   */
  boolean existsById(EnrollmentIdentifier identifier);

  /**
   * Checks whether any {@link Enrollment} associated with the specified project identifier exists
   * in the repository.
   *
   * <p>This query is commonly used to enforce relational integrity, ensuring that a {@link Project}
   * cannot be deleted while it still has formerStudents enrolled.
   *
   * @param projectId the unique identifier (UUID) of the project
   * @return {@code true} if at least one enrollment is linked to the project, {@code false}
   *     otherwise
   */
  boolean existsByProjectId(UUID projectId);

  /**
   * Checks whether any {@link Enrollment} associated with the specified formerStudent identifier
   * exists in the repository.
   *
   * <p>This query is commonly used to enforce relational integrity, ensuring that a {@link
   * FormerStudent} cannot be deleted while they still have project enrollments.
   *
   * @param formerStudentId the unique identifier (UUID) of the formerStudent account
   * @return {@code true} if at least one enrollment is linked to the formerStudent, {@code false}
   *     otherwise
   */
  boolean existsByFormerStudentId(UUID formerStudentId);

  /**
   * Retrieves an {@link Enrollment} by its composite identifier.
   *
   * <p>When an enrollment is reconstituted from the persistence layer, it typically undergoes the
   * same domain validations as a newly created aggregate. Therefore, the returned {@link
   * Enrollment} might contain validation errors (verifiable via {@link
   * Enrollment#hasFieldErrors()}) if the stored data violates current domain rules.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + formerStudent)
   * @return an {@link Optional} containing the found {@link Enrollment}, or {@link
   *     Optional#empty()} if not found
   */
  Optional<Enrollment> findOptionalById(EnrollmentIdentifier identifier);

  /**
   * Lists every enrollment linked to the provided project.
   *
   * @param projectId the unique identifier of the project
   * @return the enrollments linked to the project, or an empty list when none exist
   */
  List<Enrollment> listAllByProjectId(UUID projectId);

  /**
   * Lists every enrollment linked to the provided former student.
   *
   * @param formerStudentId the unique identifier of the former student account
   * @return the enrollments linked to the former student, or an empty list when none exist
   */
  List<Enrollment> listAllByFormerStudentId(UUID formerStudentId);

  /**
   * Persists a newly created {@link Enrollment} aggregate into the repository.
   *
   * <p>The returned instance should reflect the fully persisted state, including any persistence
   * layer side effects (such as database-generated timestamps).
   *
   * @param entity the {@link Enrollment} aggregate to persist
   * @return the fully persisted {@link Enrollment} instance, or {@code null} if the input is {@code
   *     null}
   */
  Enrollment persist(Enrollment entity);

  /**
   * Updates the state of an existing {@link Enrollment} aggregate in the repository.
   *
   * <p>Implementations are expected to safely ignore {@code null} entities. If the enrollment does
   * not exist, the operation should be a no-op rather than throwing an exception; existence checks
   * should be performed beforehand in the application service layer when necessary.
   *
   * @param entity the {@link Enrollment} instance containing the updated state
   */
  void update(Enrollment entity);
}
