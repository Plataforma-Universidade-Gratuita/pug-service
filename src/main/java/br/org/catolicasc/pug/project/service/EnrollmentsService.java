package br.org.catolicasc.pug.project.service;

import br.org.catolicasc.pug.project.domain.Enrollment;
import br.org.catolicasc.pug.project.domain.enums.EnrollmentStatus;
import br.org.catolicasc.pug.project.domain.vos.EnrollmentIdentifier;
import br.org.catolicasc.pug.project.service.dtos.enrollments.EnrollmentCreateCommand;
import br.org.catolicasc.pug.shared.exceptions.AppValidationException;
import br.org.catolicasc.pug.shared.exceptions.BusinessRuleException;
import br.org.catolicasc.pug.shared.exceptions.DuplicateResourceException;
import br.org.catolicasc.pug.shared.exceptions.ResourceNotFoundException;
import java.util.UUID;

/**
 * Application-layer command contract for enrollment lifecycle operations.
 *
 * <p>This boundary centralizes the command-side rules around enrollment creation, deletion,
 * explicit status transitions, and bulk propagation flows triggered by project or former-student
 * lifecycle changes.
 */
public interface EnrollmentsService {

  /**
   * Transitions a single enrollment to the requested lifecycle status.
   *
   * @param identifier the composite identifier of the enrollment to transition
   * @param status the target enrollment status
   * @return the updated {@link Enrollment} aggregate
   * @throws ResourceNotFoundException if the referenced enrollment does not exist
   * @throws BusinessRuleException if the requested transition is not allowed by enrollment rules
   * @throws AppValidationException if the resulting aggregate violates domain constraints
   */
  Enrollment changeStatus(EnrollmentIdentifier identifier, EnrollmentStatus status);

  /**
   * Attempts to transition every enrollment linked to a project to the same target status.
   *
   * <p>Enrollments whose lifecycle does not allow the requested transition are skipped.
   *
   * @param projectId the unique identifier of the project whose enrollments should be transitioned
   * @param targetStatus the status that should be applied
   * @return the number of enrollments successfully transitioned
   */
  long changeStatusByProjectId(UUID projectId, EnrollmentStatus targetStatus);

  /**
   * Attempts to transition every enrollment linked to a project from a specific current status to a
   * new target status.
   *
   * <p>Only enrollments currently in {@code currentStatus} are considered. Invalid transitions are
   * skipped.
   *
   * @param projectId the unique identifier of the project whose enrollments should be inspected
   * @param currentStatus the required current enrollment status
   * @param targetStatus the target status to apply
   * @return the number of enrollments successfully transitioned
   */
  long changeStatusByProjectId(
      UUID projectId, EnrollmentStatus currentStatus, EnrollmentStatus targetStatus);

  /**
   * Completes every approved enrollment linked to the provided former student.
   *
   * @param formerStudentId the unique identifier of the former student account
   * @return the number of enrollments successfully completed
   */
  long completeAllByFormerStudentId(UUID formerStudentId);

  /**
   * Permanently deletes a single enrollment.
   *
   * @param identifier the composite identifier of the enrollment to delete
   * @return {@code true} when the enrollment was deleted, or {@code false} when it does not exist
   */
  boolean delete(EnrollmentIdentifier identifier);

  /**
   * Checks whether at least one enrollment exists for the provided project.
   *
   * @param projectId the unique identifier of the project
   * @return {@code true} when at least one enrollment exists, or {@code false} otherwise
   */
  boolean existsAnyByProjectId(UUID projectId);

  /**
   * Checks whether at least one enrollment exists for the provided former student.
   *
   * @param formerStudentId the unique identifier of the former student account
   * @return {@code true} when at least one enrollment exists, or {@code false} otherwise
   */
  boolean existsAnyByFormerStudentId(UUID formerStudentId);

  /**
   * Retrieves a fully reconstituted enrollment aggregate by its composite identifier.
   *
   * @param identifier the composite identifier of the enrollment to retrieve
   * @return the matching {@link Enrollment} aggregate
   * @throws ResourceNotFoundException if the referenced enrollment does not exist
   */
  Enrollment getByIds(EnrollmentIdentifier identifier);

  /**
   * Creates and persists a new enrollment.
   *
   * @param cmd the command containing the project and former-student identifiers involved in the
   *     enrollment request
   * @return the persisted {@link Enrollment} aggregate
   * @throws ResourceNotFoundException if the referenced project or former student does not exist
   * @throws DuplicateResourceException if the enrollment already exists
   * @throws BusinessRuleException if the project or former student cannot participate in a new
   *     enrollment, or if their areas of expertise do not match
   * @throws AppValidationException if the requested enrollment state violates domain constraints
   */
  Enrollment save(EnrollmentCreateCommand cmd);
}
