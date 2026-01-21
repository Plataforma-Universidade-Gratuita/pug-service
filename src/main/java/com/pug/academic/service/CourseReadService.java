package com.pug.academic.service;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.CourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import com.pug.shared.utils.StringUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for reading course information.
 */
@ApplicationScoped
public class CourseReadService {

  @Inject
  CourseQueries queries;

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
   * Retrieves a CourseView by its name.
   *
   * @param name the name of the course.
   * @return the CourseView corresponding to the given name.
   * @throws ResourceNotFoundException if no course is found with the given name.
   */
  public CourseView getByName(String name) {
    if (StringUtils.isEmpty(name)) {
      throw new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", name));
    }
    return queries
            .findOptionalByName(name)
            .orElseThrow(
                    () ->
                            new ResourceNotFoundException(
                                    AcademicErrorCodes.COURSE_NOT_FOUND, Map.of("name", name)));
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
    String key = StringUtils.fold(query);
    return queries.searchByName(key);
  }
}