package com.pug.academic.service;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.queries.CourseQueries;
import com.pug.academic.presenter.dtos.CourseView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;

/** Service for reading course information. */
@ApplicationScoped
public class CourseReadService {

  @Inject CourseQueries queries;

  /**
   * Retrieves a course view by its ID.
   *
   * @param id the course ID.
   * @return the course view if found.
   * @throws ResourceNotFoundException if the course is not found.
   */
  public CourseView getView(UUID id) {
    return queries
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND));
  }

  /**
   * Lists all course views.
   *
   * @return the list of all course views.
   */
  public List<CourseView> listViews() {
    return queries.listAll();
  }

  /**
   * Lists all course views by school ID.
   *
   * @param schoolId the school ID.
   * @return the list of course views for the specified school.
   */
  public List<CourseView> listViewsBySchoolId(UUID schoolId) {
    return queries.listAllBySchoolId(schoolId);
  }
}
