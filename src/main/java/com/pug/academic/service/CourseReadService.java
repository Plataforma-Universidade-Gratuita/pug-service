package com.pug.academic.service;

import com.pug.academic.infra.read.dtos.CourseView;
import java.util.List;
import java.util.UUID;

/** Interface for reading course information. */
public interface CourseReadService {

  /**
   * Retrieves a CourseView by its ID.
   *
   * @param id the UUID of the course
   * @return the CourseView
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the course is not found
   */
  CourseView getViewById(UUID id);

  /**
   * Retrieves a CourseView by its name.
   *
   * @param name the name of the course.
   * @return the CourseView corresponding to the given name.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no course is found with the
   *     given name.
   */
  CourseView getByName(String name);

  /**
   * Lists all CourseViews.
   *
   * @return a list of CourseViews
   */
  List<CourseView> listViews();

  /**
   * Lists CourseViews by school ID.
   *
   * @param schoolId the UUID of the school
   * @return a list of CourseViews
   */
  List<CourseView> listViewsBySchoolId(UUID schoolId);

  /**
   * Searches CourseViews by name.
   *
   * @param query the search query
   * @return a list of CourseViews matching the query
   */
  List<CourseView> searchByName(String query);
}
