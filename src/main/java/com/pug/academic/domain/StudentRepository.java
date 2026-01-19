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
   * Deletes Students by their IDs.
   *
   * @param ids the IDs of the Students to delete
   * @return the number of Students deleted
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Finds a Student by their ID.
   *
   * @param id the ID of the Student to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<Student> findOptionalById(UUID id);

  /**
   * Lists all Students.
   *
   * @return a list of all Students
   */
  List<Student> listAllStudents();

  /**
   * Lists all Students by Course ID.
   *
   * @param courseId the Course ID to filter Students
   * @return a list of Students enrolled in the specified Course
   */
  List<Student> listAllByCourseId(UUID courseId);

  /**
   * Checks if a Student exists by their registration.
   *
   * @param registration the registration to check
   * @return true if a Student with the given registration exists, false otherwise
   */
  boolean existsByRegistration(String registration);

  /**
   * Checks if any Student exists by a collection of user IDs.
   *
   * @param accountIds the user IDs to check
   * @return true if any Student with the given user IDs exists, false otherwise
   */
  boolean existsAnyByAccountIdIn(Iterable<UUID> accountIds);
}
