package com.pug.academic.service;

import com.pug.academic.infra.read.dtos.StudentView;
import java.util.List;
import java.util.UUID;

/** Interface for reading Student data. */
public interface StudentReadService {

  /**
   * Retrieves a StudentView by its unique identifier (Account ID).
   *
   * @param accountId the UUID of the Student's account
   * @return the StudentView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no Student with the given ID is
   *     found
   */
  StudentView getViewByAccountId(UUID accountId);

  /**
   * Retrieves a StudentView by its academic registration.
   *
   * @param academicRegistration the academic registration of the Student
   * @return the StudentView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no Student with the given
   *     academic registration is found
   */
  StudentView getViewByAcademicRegistration(String academicRegistration);

  /**
   * Retrieves a StudentView by the email of the associated account.
   *
   * @param email the email of the Student's account
   * @return the StudentView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no Student with the given email
   *     is found
   */
  StudentView getViewByEmail(String email);

  /**
   * Retrieves a StudentView by the CPF of the associated account.
   *
   * @param cpf the CPF of the Student's account
   * @return the StudentView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no Student with the given CPF is
   *     found
   */
  StudentView getViewByCpf(String cpf);

  /**
   * Lists all StudentViews.
   *
   * @return a list of all StudentViews
   */
  List<StudentView> listViews();

  /**
   * Lists all StudentViews by course ID.
   *
   * @param courseId the UUID of the course
   * @return a list of StudentViews enrolled in the specified course
   */
  List<StudentView> listViewsByCourseId(UUID courseId);

  /**
   * Searches for StudentView objects by name (of the associated account).
   *
   * @param query the search query.
   * @return a list of StudentView objects matching the search key.
   */
  List<StudentView> searchByName(String query);
}
