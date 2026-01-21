package com.pug.academic.service;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.StudentQueries;
import com.pug.academic.infra.read.dtos.StudentView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Service class for reading Student views.
 */
@ApplicationScoped
public class StudentReadService {

  @Inject
  StudentQueries queries;

  /**
   * Retrieves a StudentView by its unique identifier (Account ID).
   *
   * @param accountId the UUID of the Student's account
   * @return the StudentView
   * @throws ResourceNotFoundException if no Student with the given ID is found
   */
  public StudentView getView(UUID accountId) {
    Objects.requireNonNull(accountId, "accountId");
    return queries
            .findOptionalById(accountId)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("accountId", accountId)));
  }

  /**
   * Retrieves a StudentView by its academic registration.
   *
   * @param academicRegistration the academic registration of the Student
   * @return the StudentView
   * @throws ResourceNotFoundException if no Student with the given academic registration is found
   */
  public StudentView getViewByAcademicRegistration(String academicRegistration) {
    if (StringUtils.isEmpty(academicRegistration)) {
      throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("academicRegistration", academicRegistration));
    }
    return queries
            .findOptionalByAcademicRegistration(academicRegistration)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("academicRegistration", academicRegistration)));
  }

  /**
   * Lists all StudentViews.
   *
   * @return a list of all StudentViews
   */
  public List<StudentView> listViews() {
    try {
      return queries.listAllStudents();
    } catch (Exception e) {
      throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("detail", "Error listing all students."));
    }
  }

  /**
   * Lists all StudentViews by course ID.
   *
   * @param courseId the UUID of the course
   * @return a list of StudentViews enrolled in the specified course
   */
  public List<StudentView> listViewsByCourseId(UUID courseId) {
    Objects.requireNonNull(courseId, "courseId");
    try {
      return queries.listAllByCourseId(courseId);
    } catch (Exception e) {
      throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("courseId", courseId, "detail", "Error listing students by course."));
    }
  }

  /**
   * Lists all StudentViews by a collection of account IDs.
   *
   * @param accountIds an iterable of UUIDs representing account IDs
   * @return a list of StudentViews corresponding to the provided account IDs
   */
  public List<StudentView> listViewsByIds(Iterable<UUID> accountIds) {
    Objects.requireNonNull(accountIds, "accountIds");
    try {
      return queries.listAllByIds(accountIds);
    } catch (Exception e) {
      throw new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND, Map.of("detail", "Error listing students by IDs."));
    }
  }

  /**
   * Searches for StudentView objects by name (of the associated user).
   *
   * @param query the search query.
   * @return a list of StudentView objects matching the search key.
   */
  public List<StudentView> searchByName(String query) {
    if (StringUtils.isEmpty(query)) {
      return List.of();
    }
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}