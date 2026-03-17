package com.pug.project.service;

import com.pug.project.domain.Enrollment;
import com.pug.project.service.dtos.EnrollmentCreateCommand;
import java.util.UUID;

/**
 * Application service interface for managing the state of {@link Enrollment} domain aggregates.
 *
 * <p>Following CQRS principles, this service handles the "Command" operations (Create, Update,
 * Delete) and strict domain-level retrievals. It manages the complex lifecycle states of an
 * enrollment (Pending, Approved, Completed, etc.).
 */
public interface EnrollmentService {

  /**
   * Approves a pending enrollment.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return the updated {@link Enrollment}
   */
  Enrollment accept(UUID projectId, UUID studentId);

  /**
   * Cancels an active or pending enrollment.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return the updated {@link Enrollment}
   */
  Enrollment cancel(UUID projectId, UUID studentId);

  /**
   * Marks a student's participation in a project as completed.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return the updated {@link Enrollment}
   */
  Enrollment complete(UUID projectId, UUID studentId);

  /**
   * Physically removes an {@link Enrollment} record from the system.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return {@code true} if deleted, {@code false} if not found
   */
  boolean delete(UUID projectId, UUID studentId);

  /**
   * Checks if a student is enrolled in any project.
   *
   * @param studentId the unique identifier of the student
   * @return {@code true} if any enrollment exists
   */
  boolean existsAnyByStudentId(UUID studentId);

  /**
   * Records a student's voluntary exit from a project.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return the updated {@link Enrollment}
   */
  Enrollment exit(UUID projectId, UUID studentId);

  /**
   * Retrieves a full {@link Enrollment} aggregate by its composite identifier.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return the {@link Enrollment} aggregate
   */
  Enrollment getByIds(UUID projectId, UUID studentId);

  /**
   * Rejects a pending enrollment request.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return the updated {@link Enrollment}
   */
  Enrollment reject(UUID projectId, UUID studentId);

  /**
   * Administratively removes a student from a project.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return the updated {@link Enrollment}
   */
  Enrollment remove(UUID projectId, UUID studentId);

  /**
   * Instantiates and requests a new {@link Enrollment}.
   *
   * @param cmd the command containing enrollment data
   * @return the persisted {@link Enrollment}
   */
  Enrollment save(EnrollmentCreateCommand cmd);
}
