package com.pug.academic.infra.read;

import com.pug.academic.infra.read.dtos.CourseView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Queries related to courses. */
public interface ICourseQueries {
  /**
   * Find a course by its ID.
   *
   * @param id the ID of the course.
   * @return the found course.
   */
  Optional<CourseView> findOptionalById(UUID id);

  /**
   * Find a course by its name.
   *
   * @param name the name of the course.
   * @return an Optional containing the found CourseView, or empty if not found.
   */
  Optional<CourseView> findOptionalByName(String name);

  /**
   * List all courses.
   *
   * @return the list of all courses.
   */
  List<CourseView> listAllCourses();

  /**
   * List all courses by school ID.
   *
   * @param schoolId the school ID.
   * @return the list of courses for the given school ID.
   */
  List<CourseView> listAllBySchoolId(UUID schoolId);

  /**
   * Search courses by name.
   *
   * @param query the search query.
   * @return the list of matching courses.
   */
  List<CourseView> searchByName(String query);
}
