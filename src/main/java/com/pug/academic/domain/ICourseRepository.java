package com.pug.academic.domain;

import com.pug.shared.exceptions.AppValidationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Course aggregate.
 */
public interface ICourseRepository {
  /**
   * Persist a course.
   *
   * @param entity the course to persist.
   * @return the persisted course.
   * @throws AppValidationException if the persisted entity cannot be converted back.
   */
  Course persist(Course entity) throws AppValidationException;

  /**
   * Persist multiple courses.
   *
   * @param entities the courses to persist.
   * @return the persisted courses.
   * @throws AppValidationException if any persisted entity cannot be converted back.
   */
  List<Course> persistAll(Iterable<Course> entities) throws AppValidationException;

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
   * @throws AppValidationException if the found entity cannot be converted.
   */
  Optional<Course> findOptionalById(UUID id) throws AppValidationException;

  /**
   * Find a course by its name.
   *
   * @param name the name of the course.
   * @return the found course.
   * @throws AppValidationException if the found entity cannot be converted.
   */
  Optional<Course> findOptionalByName(String name) throws AppValidationException;

  /**
   * List all courses.
   *
   * @return the list of all courses.
   * @throws AppValidationException if any found entity cannot be converted.
   */
  List<Course> listAllCourses() throws AppValidationException;

  /**
   * List all courses by school ID.
   *
   * @param schoolId the school ID.
   * @return the list of courses for the given school ID.
   * @throws AppValidationException if any found entity cannot be converted.
   */
  List<Course> listAllBySchoolId(UUID schoolId) throws AppValidationException;

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