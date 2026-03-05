package com.pug.academic.infra.read;

import com.pug.academic.infra.read.dtos.StudentView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Read-only interface for executing Student profile queries.
 * <p>
 * This interface represents the "Query" side of a CQRS architecture. It defines
 * operations for retrieving consolidated student profiles directly into lightweight
 * {@link StudentView} projections. These views aggregate data across the Identity
 * and Academic contexts for optimized API delivery.
 */
public interface StudentQueries {

  /**
   * Retrieves a read-only view of a student based on their linked account ID.
   *
   * @param accountId the unique identifier (UUID) of the student's account
   * @return an {@link Optional} containing the {@link StudentView} if found, otherwise empty
   */
  Optional<StudentView> findOptionalById(UUID accountId);

  /**
   * Retrieves a read-only view of a student based on their academic registration identifier.
   *
   * @param academicRegistration the exact academic registration string of the student
   * @return an {@link Optional} containing the {@link StudentView} if found, otherwise empty
   */
  Optional<StudentView> findOptionalByAcademicRegistration(String academicRegistration);

  /**
   * Retrieves a read-only view of a student based on their registered email address.
   *
   * @param email the exact email address of the student
   * @return an {@link Optional} containing the {@link StudentView} if found, otherwise empty
   */
  Optional<StudentView> findOptionalByEmail(String email);

  /**
   * Retrieves a read-only view of a student based on their registered CPF.
   *
   * @param cpf the exact 11-digit numeric CPF string of the student
   * @return an {@link Optional} containing the {@link StudentView} if found, otherwise empty
   */
  Optional<StudentView> findOptionalByCpf(String cpf);

  /**
   * Retrieves a comprehensive list of all students registered in the system.
   * <p>
   * <i>Note:</i> Use with caution if the dataset grows significantly, as this method
   * does not implement pagination.
   *
   * @return a {@link List} of all {@link StudentView} records
   */
  List<StudentView> listAllStudents();

  /**
   * Retrieves a list of all students enrolled in a specific course.
   *
   * @param courseId the unique identifier (UUID) of the course
   * @return a {@link List} of {@link StudentView} records associated with the given course
   */
  List<StudentView> listAllByCourseId(UUID courseId);

  /**
   * Executes a robust full-text search against the names of the associated student users.
   * <p>
   * This method typically leverages underlying indexing engines (e.g., Elasticsearch via Hibernate Search).
   *
   * @param key the raw search string or partial name of the student
   * @return a sorted {@link List} of matching {@link StudentView} records
   */
  List<StudentView> searchByName(String key);
}