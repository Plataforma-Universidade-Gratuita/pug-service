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
   * Updates a Student entity.
   *
   * @param entity the Student to update
   */
  void update(Student entity);

  /**
   * Deletes a Student by their account ID.
   *
   * @param id the account ID of the Student to delete
   * @return true if the Student was deleted, false if no Student with the given ID was found
   */
  boolean deleteById(UUID id);

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
}
