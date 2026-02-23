package com.pug.academic.service;

import com.pug.academic.domain.Course;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;
import java.util.List;
import java.util.UUID;

/** Interface for managing Course entities. */
public interface CourseService {

  /**
   * Saves a new Course entity.
   *
   * @param cmd the command containing the data to create the new Course.
   * @return the saved Course entity.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a course with the same name
   *     already exists.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the associated school does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Course save(CourseCreateCommand cmd);

  /**
   * Updates an existing Course entity.
   *
   * @param id the UUID of the course to update.
   * @param cmd the command containing the new data for the course.
   * @return the updated Course entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the course with the given ID
   *     does not exist, or if the new school does not exist.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if a course with the new name
   *     already exists.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails.
   */
  Course update(UUID id, CourseUpdateCommand cmd);

  /**
   * Deletes a Course entity by its ID.
   *
   * @param id the UUID of the course to delete.
   * @return true if the course was successfully deleted, false if the course with the given ID does
   *     not exist.
   */
  boolean delete(UUID id);

  /**
   * Lists all Course entities.
   *
   * @return a list of all Course entities.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if no course is found (or data is
   *     corrupted in DB).
   * @throws com.pug.shared.exceptions.AppValidationException if any Course entity found is
   *     corrupted in the database.
   */
  List<Course> listAll();

  /**
   * Retrieves a Course entity by its ID.
   *
   * @param id the UUID of the course.
   * @return the Course entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the course with the given ID
   *     does not exist (or data is corrupted in DB).
   * @throws com.pug.shared.exceptions.AppValidationException if the course is found but its data is
   *     corrupted in the database.
   */
  Course getById(UUID id);
}
