package com.pug.academic.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing Student entities. */
public interface StudentRepository {
  /**
   * Persists a Student entity.
   * @param student the Student to persist
   * @return the persisted Student
   */
  Student persist(Student student);

  /**
   * Persists multiple Student entities.
   * @param students the Students to persist
   * @return the list of persisted Students
   */
  List<Student> persistAll(Iterable<Student> students);

  /**
   * Deletes Students by their IDs.
   * @param userIds the IDs of the Students to delete
   * @return the number of Students deleted
   */
  long deleteByIds(Iterable<UUID> userIds);

  /**
   * Finds a Student by their ID.
   * @param userId the ID of the Student to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<Student> findOptionalById(UUID userId);

  /**
   * Lists all Students.
   * @return a list of all Students
   */
  List<Student> listAllStudents();

  /**
   * Lists all Students by Course ID.
   * @param courseId the Course ID to filter Students
   * @return a list of Students enrolled in the specified Course
   */
  List<Student> listAllByCourseId(UUID courseId);

  /**
   * Checks if a Student exists by their registration.
   * @param registration the registration to check
   * @return true if a Student with the given registration exists, false otherwise
   */
  boolean existsByRegistration(String registration);
}
