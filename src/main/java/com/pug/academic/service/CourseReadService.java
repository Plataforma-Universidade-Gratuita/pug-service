package com.pug.academic.service;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.CourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Service for reading course information. */
@ApplicationScoped
public class CourseReadService {

  @Inject CourseQueries queries;

  /**
   * Retrieves a CourseView by its ID.
   *
   * @param id the UUID of the course
   * @return the CourseView
   * @throws ResourceNotFoundException if the course is not found
   */
  public CourseView getViewById(UUID id) {
    return queries
        .findOptionalById(id)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("id", id)));
  }

  /**
   * Lists all CourseViews.
   *
   * @return a list of CourseViews
   */
  public List<CourseView> listViews() {
    return queries.listAllCourses();
  }

  /**
   * Lists CourseViews by school ID.
   *
   * @param schoolId the UUID of the school
   * @return a list of CourseViews
   */
  public List<CourseView> listViewsBySchoolId(UUID schoolId) {
    return queries.listAllBySchoolId(schoolId);
  }

  /**
   * Searches CourseViews by name.
   *
   * @param query the search query
   * @return a list of CourseViews matching the query
   */
  public List<CourseView> searchByName(String query) {
    return queries.searchByName(query);
  }
}
