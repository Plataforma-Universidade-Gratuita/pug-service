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
   * Persist multiple courses.
   *
   * @param entities the courses to persist.
   * @return the persisted courses.
   */
  List<Course> persistAll(Iterable<Course> entities);

  /**
   * Update a course.
   *
   * @param entity the course with updated data.
   */
  void update(Course entity);

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
   * Find a course by its name.
   *
   * @param name the name of the course.
   * @return the found course.
   */
  Optional<Course> findOptionalByName(String name);

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
   * Check if a course exists by name.
   *
   * @param name the name of the course.
   * @return true if a course with the given name exists, false otherwise.
   */
  boolean existsByName(String name);

  /**
   * Check if any courses exist by their names.
   *
   * @param names the names of the courses.
   * @return true if any course with the given names exists, false otherwise.
   */
  boolean existsAnyByNameIn(Iterable<String> names);
}
