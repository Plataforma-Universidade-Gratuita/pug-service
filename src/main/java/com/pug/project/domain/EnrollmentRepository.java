package com.pug.project.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link Enrollment} aggregate roots.
 *
 * <p>This interface defines the contract for persisting and retrieving student enrollments in
 * projects. It utilizes a composite natural key (Project ID + Student ID).
 */
public interface EnrollmentRepository {

  Enrollment persist(Enrollment entity);

  void update(Enrollment entity);

  boolean deleteByIds(UUID projectId, UUID studentId);

  Optional<Enrollment> findOptionalByIds(UUID projectId, UUID studentId);

  /**
   * Checks whether a specific student is already enrolled in a specific project.
   *
   * @param projectId the unique identifier of the project
   * @param studentId the unique identifier of the student
   * @return {@code true} if the enrollment exists, {@code false} otherwise
   */
  boolean existsByIds(UUID projectId, UUID studentId);

  boolean existsByStudentId(UUID studentId);

  boolean existsByProjectId(UUID projectId);
}
