package com.pug.academic.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Student entities. */
public interface StudentRepository {
  /**
   * Persists a Student entity.
   *
   * @param entity the Student to persist
   * @return the persisted Student
   */
  Student persist(Student entity);

  /**
   * Persists multiple Student entities.
   *
   * @param entities the Students to persist
   * @return the list of persisted Students
   */
  List<Student> persistAll(Iterable<Student> entities);

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
   * <p>Note: The returned Student may contain validation errors (check {@code student.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @param id the account ID of the Student to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<Student> findOptionalById(UUID id);

  /**
   * Lists all Students.
   *
   * <p>Note: The returned Students may contain validation errors (check {@code
   * student.hasErrors()}) if the stored data is inconsistent with current domain rules.
   *
   * @return a list of all Students
   */
  List<Student> listAllStudents();

  /**
   * Lists all Students by Course ID.
   *
   * <p>Note: The returned Students may contain validation errors (check {@code
   * student.hasErrors()}) if the stored data is inconsistent with current domain rules.
   *
   * @param courseId the Course ID to filter Students
   * @return a list of Students enrolled in the specified Course
   */
  List<Student> listAllByCourseId(UUID courseId);

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
