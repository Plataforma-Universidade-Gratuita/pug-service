package com.pug.academic.infra.read;

import com.pug.academic.infra.read.dtos.StudentView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Interface for querying Student data. */
public interface StudentQueries {
  /**
   * Finds a Student by their ID.
   *
   * @param userId the ID of the Student to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<StudentView> findOptionalById(UUID userId);

  /**
   * Finds a Student by their Academic Registration.
   *
   * @param academicRegistration the Academic Registration of the Student to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<StudentView> findOptionalByAcademicRegistration(String academicRegistration);

  /**
   * Lists all Students by their IDs.
   *
   * @param userIds the IDs of the Students to list
   * @return a list of Students with the specified IDs
   */
  List<StudentView> listAllByIds(Iterable<UUID> userIds);

  /**
   * Lists all Students.
   *
   * @return a list of all Students
   */
  List<StudentView> listAllStudents();

  /**
   * Lists all Students by Course ID.
   *
   * @param courseId the Course ID to filter Students
   * @return a list of Students enrolled in the specified Course
   */
  List<StudentView> listAllByCourseId(UUID courseId);
}
