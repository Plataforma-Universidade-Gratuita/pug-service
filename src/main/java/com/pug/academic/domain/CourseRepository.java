package com.pug.academic.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository for Course aggregate. */
public interface CourseRepository {
  /**
   * Persist a course.
   *
   * @param entity the course to persist.
   * @return the persisted course.
   */
  Course persist(Course entity);

  /**
   * Update a course.
   *
   * @param entity the course with updated data.
   */
  void update(Course entity);

  /**
   * Delete a course by its ID.
   *
   * @param id the ID of the course to delete.
   * @return true if the course was deleted, false if no course with the given ID was found.
   */
  boolean deleteById(UUID id);

  /**
   * Find a course by its ID.
   *
   * <p>Note: The returned Course may contain validation errors (check {@code course.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @param id the ID of the course.
   * @return the found course.
   */
  Optional<Course> findOptionalById(UUID id);

  /**
   * List all courses.
   *
   * <p>Note: The returned Courses may contain validation errors (check {@code course.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @return the list of all courses.
   */
  List<Course> listAllCourses();

  /**
   * List all courses by school ID.
   *
   * <p>Note: The returned Courses may contain validation errors (check {@code course.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @param schoolId the school ID.
   * @return the list of courses for the given school ID.
   */
  List<Course> listAllBySchoolId(UUID schoolId);

  /**
   * Check if a course exists by name.
   *
   * @param name the name of the course.
   * @return true if a course with the given name exists, false otherwise.
   */
  boolean existsByName(String name);
}
