package com.pug.academic.infra.read;

import com.pug.academic.infra.read.dtos.StudentView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Interface for querying Student data. */
public interface StudentQueries {
  /**
   * Finds a Student by their Account ID.
   *
   * @param accountId the ID of the Student's account to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<StudentView> findOptionalById(UUID accountId);

  /**
   * Finds a Student by their Academic Registration.
   *
   * @param academicRegistration the Academic Registration of the Student to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<StudentView> findOptionalByAcademicRegistration(String academicRegistration);

  /**
   * Finds a Student by the email of their associated account.
   *
   * @param email the email of the Student's account to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<StudentView> findOptionalByEmail(String email);

  /**
   * Finds a Student by the CPF of their associated account.
   *
   * @param cpf the CPF of the Student's account to find
   * @return an Optional containing the Student if found, or empty if not found
   */
  Optional<StudentView> findOptionalByCpf(String cpf);

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

  /**
   * Searches for StudentView objects by name (of the associated account).
   *
   * @param key the name key to search for.
   * @return a list of StudentView objects matching the search key.
   */
  List<StudentView> searchByName(String key);
}
