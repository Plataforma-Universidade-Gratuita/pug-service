package com.pug.academic.service;

import com.pug.academic.domain.enums.AcademicErrorCodes;
import com.pug.academic.infra.read.CourseQueries;
import com.pug.academic.infra.read.dtos.CourseView;
import com.pug.shared.exceptions.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Objects;
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
  public CourseView getView(UUID id) {
    Objects.requireNonNull(id, "id");
    return queries
        .findOptionalById(id)
        .orElseThrow(() -> new ResourceNotFoundException(AcademicErrorCodes.COURSE_NOT_FOUND));
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
    Objects.requireNonNull(schoolId, "schoolId");
    return queries.listAllBySchoolId(schoolId);
  }

  /**
   * Lists CourseViews by their IDs.
   *
   * @param ids an iterable of UUIDs
   * @return a list of CourseViews
   */
  public List<CourseView> listViewsByIds(Iterable<UUID> ids) {
    Objects.requireNonNull(ids, "ids");
    return queries.listAllByIds(ids);
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
