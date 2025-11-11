package com.pug.academic.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository for Course aggregate. */
public interface CourseRepository {
  /**
   * Persist a course.
   *
   * @param course the course to persist.
   * @return the persisted course.
   */
  Course persist(Course course);

  /**
   * Persist multiple courses.
   *
   * @param courses the courses to persist.
   * @return the persisted courses.
   */
  List<Course> persistAll(Iterable<Course> courses);

  /**
   * Delete courses by their IDs.
   *
   * @param ids the IDs of the courses to delete.
   * @return the number of deleted courses.
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Find a course by its ID.
   *
   * @param id the ID of the course.
   * @return the found course.
   */
  Optional<Course> findOptionalById(UUID id);

  /**
   * List all courses.
   *
   * @return the list of all courses.
   */
  List<Course> listAllCourses();

  /**
   * List all courses by school ID.
   *
   * @param schoolId the school ID.
   * @return the list of courses for the given school ID.
   */
  List<Course> listAllBySchoolId(UUID schoolId);

  /**
   * List all courses by their IDs.
   *
   * @param ids the IDs of the courses.
   * @return the list of courses with the given IDs.
   */
  List<Course> listAllByIds(Iterable<UUID> ids);

  /**
   * Check if a course exists by name.
   *
   * @param name the name of the course.
   * @return true if a course with the given name exists, false otherwise.
   */
  boolean existsByName(String name);

  /**
   * Update a course.
   *
   * @param updated the course with updated data.
   */
  void update(Course updated);
}
