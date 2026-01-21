package com.pug.academic.domain;

import com.pug.shared.exceptions.AppValidationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Student entities. */
public interface IStudentRepository {
  /**
   * Persists a Student entity.
   *
   * @param entity the Student to persist
   * @return the persisted Student
   * @throws AppValidationException if the persisted entity cannot be converted back.
   */
  Student persist(Student entity) throws AppValidationException;

  /**
   * Persists multiple Student entities.
   *
   * @param entities the Students to persist
   * @return the list of persisted Students
   * @throws AppValidationException if any persisted entity cannot be converted back.
   */
  List<Student> persistAll(Iterable<Student> entities) throws AppValidationException;

  /**
   * Updates a Student entity.
   *
   * @param entity the Student to update
   */
  void update(Student entity);

  /**
   * Deletes Students by their account IDs.
   *
   * @param ids the account IDs of the Students to delete
   * @return the number of Students deleted
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds a Student by their account ID.
   *
   * @param id the account ID of the Student to find
   * @return an Optional containing the Student if found, or empty if not found
   * @throws AppValidationException if the found entity cannot be converted.
   */
  Optional<Student> findOptionalById(UUID id) throws AppValidationException;

  /**
   * Lists all Students.
   *
   * @return a list of all Students
   * @throws AppValidationException if any found entity cannot be converted.
   */
  List<Student> listAllStudents() throws AppValidationException;

  /**
   * Lists all Students by Course ID.
   *
   * @param courseId the Course ID to filter Students
   * @return a list of Students enrolled in the specified Course
   * @throws AppValidationException if any found entity cannot be converted.
   */
  List<Student> listAllByCourseId(UUID courseId) throws AppValidationException;

  /**
   * Checks if any Student exists for a collection of registrations.
   *
   * @param registrations the registrations to check
   * @return true if any Student with the given registrations exists, false otherwise
   */
  boolean existsAnyByRegistrationIn(Iterable<String> registrations);

  /**
   * Checks if any Student exists for a collection of account IDs.
   *
   * @param accountIds the account IDs to check
   * @return true if any Student with the given account IDs exists, false otherwise
   */
  boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds);

  /**
   * Checks if any Student exists for a collection of course IDs.
   *
   * @param courseIds the course IDs to check.
   * @return true if any Student with the given course IDs exists, false otherwise.
   */
  boolean existsAnyByCourseIdIn(Iterable<UUID> courseIds);
}
