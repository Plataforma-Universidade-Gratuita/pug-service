package com.pug.academic.service;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.StudentQueries;
import com.pug.academic.infra.read.dtos.StudentView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Service class for reading Student views. */
@ApplicationScoped
public class StudentReadService {

  @Inject StudentQueries queries;

  /**
   * Retrieves a StudentView by its unique identifier.
   *
   * @param id the UUID of the Student
   * @return the StudentView
   * @throws ResourceNotFoundException if no Student with the given ID is found
   */
  public StudentView getView(UUID id) {
    Objects.requireNonNull(id, "id");
    return queries
        .findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND));
  }

  /**
   * Retrieves a StudentView by its academic registration.
   *
   * @param academicRegistration the academic registration of the Student
   * @return the StudentView
   * @throws ResourceNotFoundException if no Student with the given academic registration is found
   */
  public StudentView getViewByAcademicRegistration(String academicRegistration) {
    Objects.requireNonNull(academicRegistration, "academicRegistration");
    return queries
        .findOptionalByAcademicRegistration(academicRegistration)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.STUDENT_NOT_FOUND));
  }

  /**
   * Lists all StudentViews.
   *
   * @return a list of all StudentViews
   */
  public List<StudentView> listViews() {
    return queries.listAllStudents();
  }

  /**
   * Lists all StudentViews by course ID.
   *
   * @param courseId the UUID of the course
   * @return a list of StudentViews enrolled in the specified course
   */
  public List<StudentView> listViewsByCourseId(UUID courseId) {
    Objects.requireNonNull(courseId, "courseId");
    return queries.listAllByCourseId(courseId);
  }

  /**
   * Lists all StudentViews by a collection of user IDs.
   *
   * @param userIds an iterable of UUIDs representing user IDs
   * @return a list of StudentViews corresponding to the provided user IDs
   */
  public List<StudentView> listViewsByIds(Iterable<UUID> userIds) {
    Objects.requireNonNull(userIds, "userIds");
    return queries.listAllByIds(userIds);
  }
}
