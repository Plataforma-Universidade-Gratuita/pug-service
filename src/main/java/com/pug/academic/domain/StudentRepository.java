package com.pug.academic.domain;

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
   * Checks whether a Student with the given academic registration already exists.
   *
   * @param registration the academic registration string to check for existence
   * @return true if a Student with the given registration exists, false otherwise
   */
  boolean existsByRegistration(String registration);
}
