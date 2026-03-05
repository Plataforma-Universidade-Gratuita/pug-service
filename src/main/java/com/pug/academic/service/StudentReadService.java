package com.pug.academic.service;

import com.pug.academic.infra.read.dtos.StudentView;

import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Student data.
 * <p>
 * Following CQRS principles, this service handles the "Query" operations. It bypasses
 * complex domain logic and retrieves lightweight, fully resolved {@link StudentView} Data
 * Transfer Objects directly from the underlying data store or search indices.
 */
public interface StudentReadService {

  /**
   * Retrieves a read-only projection of an enrolled student based on their linked account ID.
   *
   * @param accountId the unique identifier (UUID) of the student's account
   * @return the populated {@link StudentView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no student matches the provided ID
   */
  StudentView getViewByAccountId(UUID accountId);

  /**
   * Retrieves a read-only projection of a student based on their exact academic registration number.
   *
   * @param academicRegistration the exact academic registration string
   * @return the populated {@link StudentView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no student matches the provided registration
   */
  StudentView getViewByAcademicRegistration(String academicRegistration);

  /**
   * Retrieves a read-only projection of a student based on their registered email address.
   *
   * @param email the exact email address of the student
   * @return the populated {@link StudentView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no student matches the provided email
   */
  StudentView getViewByEmail(String email);

  /**
   * Retrieves a read-only projection of a student based on their registered CPF.
   *
   * @param cpf the exact 11-digit numeric CPF string of the student
   * @return the populated {@link StudentView} DTO
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no student matches the provided CPF
   */
  StudentView getViewByCpf(String cpf);

  /**
   * Retrieves a comprehensive list of all students registered in the system.
   * <p>
   * <i>Note:</i> This method returns the entire dataset. It should be used judiciously
   * in contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link StudentView} entries
   */
  List<StudentView> listViews();

  /**
   * Retrieves a list of all students currently enrolled in a specific course.
   *
   * @param courseId the unique identifier (UUID) of the course
   * @return a {@link List} of matching {@link StudentView} entries
   */
  List<StudentView> listViewsByCourseId(UUID courseId);

  /**
   * Executes a robust full-text search against the names of the associated student users.
   * <p>
   * Leverages advanced text analysis (e.g., Elasticsearch via Hibernate Search) to provide
   * fuzzy matching, accent-insensitivity, and predictive autocomplete capabilities.
   *
   * @param query the raw search string or partial name provided by the client
   * @return a sorted {@link List} of matching {@link StudentView} entries
   */
  List<StudentView> searchByName(String query);
}