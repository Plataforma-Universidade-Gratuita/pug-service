package com.pug.academic.infra.queries;

import com.pug.academic.presenter.dtos.CourseView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to courses. */
public interface CourseQueries {
  /**
   * Finds a course by its ID.
   *
   * @param id the course ID.
   * @return the course view if found, otherwise empty.
   */
  Optional<CourseView> findById(UUID id);

  /**
   * Lists all courses.
   *
   * @return the list of all course views.
   */
  List<CourseView> listAll();

  /**
   * Lists all courses by school ID.
   *
   * @param schoolId the school ID.
   * @return the list of course views for the specified school.
   */
  List<CourseView> listAllBySchoolId(UUID schoolId);
}
