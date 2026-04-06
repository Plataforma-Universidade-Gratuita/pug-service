package com.pug.project.service;

import com.pug.project.domain.Enrollment;
import com.pug.project.domain.enums.EnrollmentStatus;
import com.pug.project.domain.vos.EnrollmentIdentifier;
import com.pug.project.service.dtos.EnrollmentCreateCommand;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Enrollment} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) and strict domain-level retrievals. It orchestrates the complex lifecycle states of an
 * enrollment ({@link EnrollmentStatus#PENDING}, {@link EnrollmentStatus#APPROVED}, {@link
 * EnrollmentStatus#COMPLETED}, etc.), delegating pure state transitions to the aggregate itself.
 */
public interface EnrollmentService {

  /**
   * Approves a pending enrollment.
   *
   * <p>This operation transitions the underlying {@link Enrollment} from {@link
   * EnrollmentStatus#PENDING} to {@link EnrollmentStatus#APPROVED}, stamping the {@code acceptedAt}
   * timestamp via {@link com.pug.project.domain.vos.EnrollmentInfo#accept()}. Any attempt to
   * approve an enrollment that is not currently pending is rejected at the domain level.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @return the updated {@link Enrollment} aggregate in {@link EnrollmentStatus#APPROVED} state
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment does not exist
   * @throws com.pug.shared.exceptions.BusinessRuleException if the current status does not allow a
   *     transition to {@code APPROVED}
   */
  Enrollment accept(EnrollmentIdentifier identifier);

  /**
   * Cancels an active enrollment.
   *
   * <p>This operation transitions the underlying {@link Enrollment} from {@link
   * EnrollmentStatus#APPROVED} to {@link EnrollmentStatus#CANCELED}, stamping the {@code
   * closingStatusAt} timestamp. Once canceled, the enrollment becomes a terminal record and cannot
   * transition to any other status.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @return the updated {@link Enrollment} aggregate in {@link EnrollmentStatus#CANCELED} state
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment does not exist
   * @throws com.pug.shared.exceptions.BusinessRuleException if the current status does not allow a
   *     transition to {@code CANCELED}
   */
  Enrollment cancel(EnrollmentIdentifier identifier);

  /**
   * Marks a student's participation in a project as completed.
   *
   * <p>This operation transitions the underlying {@link Enrollment} from {@link
   * EnrollmentStatus#APPROVED} to {@link EnrollmentStatus#COMPLETED}, stamping the {@code
   * closingStatusAt} timestamp. Once completed, the enrollment becomes a terminal record and cannot
   * transition to any other status.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @return the updated {@link Enrollment} aggregate in {@link EnrollmentStatus#COMPLETED} state
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment does not exist
   * @throws com.pug.shared.exceptions.BusinessRuleException if the current status does not allow a
   *     transition to {@code COMPLETED}
   */
  Enrollment complete(EnrollmentIdentifier identifier);

  /**
   * Physically removes an {@link Enrollment} record from the system.
   *
   * <p>This operation executes a hard delete against the persistence layer using the composite key
   * (project + student). It is expected to be idempotent: if the enrollment does not exist, the
   * implementation should simply return {@code false} without raising an exception.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @return {@code true} if the enrollment was successfully deleted, {@code false} if it was not
   *     found
   */
  boolean delete(EnrollmentIdentifier identifier);

  /**
   * Checks if a student is enrolled in any project.
   *
   * <p>This query is commonly used to enforce relational integrity, ensuring that a {@link
   * com.pug.academic.domain.Student} cannot be deleted while they still have historical enrollments
   * registered in projects.
   *
   * @param studentId the unique identifier (UUID) of the student account
   * @return {@code true} if at least one {@link Enrollment} exists for the given student, {@code
   *     false} otherwise
   */
  boolean existsAnyByStudentId(UUID studentId);

  /**
   * Checks whether any {@link Enrollment} exists for the specified project identifier.
   *
   * <p>This query is utilized by higher-level services (such as {@link ProjectService}) to enforce
   * relational integrity, ensuring that a {@link com.pug.project.domain.Project} cannot be deleted
   * while there are students enrolled in it.
   *
   * @param projectId the unique identifier (UUID) of the project to check
   * @return {@code true} if at least one enrollment is linked to the given project, {@code false}
   *     otherwise
   */
  boolean existsAnyByProjectId(UUID projectId);

  /**
   * Records a student's voluntary exit from a project.
   *
   * <p>This operation transitions the underlying {@link Enrollment} from {@link
   * EnrollmentStatus#APPROVED} to {@link EnrollmentStatus#EXITED}, stamping the {@code
   * closingStatusAt} timestamp. Once exited, the enrollment becomes a terminal record and cannot
   * transition to any other status.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @return the updated {@link Enrollment} aggregate in {@link EnrollmentStatus#EXITED} state
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment does not exist
   * @throws com.pug.shared.exceptions.BusinessRuleException if the current status does not allow a
   *     transition to {@code EXITED}
   */
  Enrollment exit(EnrollmentIdentifier identifier);

  /**
   * Retrieves a full {@link Enrollment} aggregate by its composite identifier.
   *
   * <p><b>Note:</b> This method is intended strictly for internal domain orchestration (e.g., prior
   * to invoking {@link Enrollment#changeStatus(com.pug.project.domain.enums.EnrollmentStatus)}).
   * For API responses, prefer using the {@link com.pug.project.service.EnrollmentReadService}
   * projections.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @return the fully reconstituted {@link Enrollment} aggregate
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the stored enrollment violates
   *     strict domain invariants (e.g., invalid timestamps or identifiers)
   */
  Enrollment getByIds(EnrollmentIdentifier identifier);

  /**
   * Rejects a pending enrollment request.
   *
   * <p>This operation transitions the underlying {@link Enrollment} from {@link
   * EnrollmentStatus#APPROVED} to {@link EnrollmentStatus#REJECTED}, stamping the {@code
   * closingStatusAt} timestamp. Once rejected as a closing state, the enrollment cannot transition
   * to any other status.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @return the updated {@link Enrollment} aggregate in {@link EnrollmentStatus#REJECTED} state
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment does not exist
   * @throws com.pug.shared.exceptions.BusinessRuleException if the current status does not allow a
   *     transition to {@code REJECTED}
   */
  Enrollment reject(EnrollmentIdentifier identifier);

  /**
   * Administratively removes a student from a project.
   *
   * <p>This operation transitions the underlying {@link Enrollment} from {@link
   * EnrollmentStatus#APPROVED} to {@link EnrollmentStatus#REMOVED}, stamping the {@code
   * closingStatusAt} timestamp. It is typically used for administrative actions (e.g., disciplinary
   * removal). Once removed, the enrollment becomes a terminal record and cannot transition to any
   * other status.
   *
   * @param identifier the composite {@link EnrollmentIdentifier} uniquely identifying the
   *     enrollment (project + student)
   * @return the updated {@link Enrollment} aggregate in {@link EnrollmentStatus#REMOVED} state
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the enrollment does not exist
   * @throws com.pug.shared.exceptions.BusinessRuleException if the current status does not allow a
   *     transition to {@code REMOVED}
   */
  Enrollment remove(EnrollmentIdentifier identifier);

  /**
   * Instantiates and requests a new {@link Enrollment}.
   *
   * <p>This method verifies the existence of the associated {@link com.pug.project.domain.Project}
   * and {@link com.pug.academic.domain.Student}, checks for duplicate enrollments for the same
   * (project, student) pair, and delegates the aggregate initialization to {@link
   * com.pug.project.service.utils.EnrollmentProcessor}. The resulting aggregate is self-validated
   * before being persisted.
   *
   * @param cmd the structured {@link EnrollmentCreateCommand} containing enrollment data
   * @return the fully instantiated and persisted {@link Enrollment} aggregate
   * @throws com.pug.shared.exceptions.DuplicateResourceException if an enrollment for the given
   *     project and student already exists
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the associated project or
   *     student does not exist
   * @throws com.pug.shared.exceptions.AppValidationException if the created enrollment violates
   *     domain constraints
   */
  Enrollment save(EnrollmentCreateCommand cmd);
}
