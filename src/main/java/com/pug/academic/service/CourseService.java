package com.pug.academic.service;

import com.pug.academic.domain.Course;
import com.pug.academic.service.dtos.CourseCreateCommand;
import com.pug.academic.service.dtos.CourseUpdateCommand;

import java.util.List;
import java.util.Map;
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
   * Saves multiple new Course entities.
   *
   * @param cmds an iterable of commands for course creation.
   * @return a list of saved Course entities.
   * @throws com.pug.shared.exceptions.DuplicateResourceException if any course with the same name
   *     already exists, or if there are duplicate names in the input commands.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if any associated school does not
   *     exist.
   * @throws com.pug.shared.exceptions.AppValidationException if input validation fails for any
   *     course in the bulk.
   */
  List<Course> saveAll(Iterable<CourseCreateCommand> cmds);

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
   * Deletes Course entities by their IDs.
   *
   * @param ids an iterable of UUIDs representing the course IDs to delete.
   * @return a map containing the count of deleted entities for each DeleteKeys.
   * @throws DataIntegrityException if any course is still referenced
   *     by students.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if a student's account is not found
   *     during cascade deletion.
   */
  Map<DeleteKeys, Long> deleteAll(Iterable<UUID> ids);

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
   * Lists all Course entities associated with a specific school ID.
   *
   * @param schoolId the UUID of the school.
   * @return a list of Course entities associated with the given school ID.
   * @throws com.pug.shared.exceptions.AppValidationException if any Course entity found is
   *     corrupted in the database.
   */
  List<Course> listAllBySchoolId(UUID schoolId);

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

  /**
   * Retrieves a Course entity by its name.
   *
   * @param name the name of the course.
   * @return the Course entity.
   * @throws com.pug.shared.exceptions.ResourceNotFoundException if the course with the given name
   *     does not exist (or data is corrupted in DB).
   * @throws com.pug.shared.exceptions.AppValidationException if the course is found but its data is
   *     corrupted in the database.
   */
  Course getByName(String name);

  /**
   * Checks if a Course entity exists with the given name.
   *
   * @param name the name of the course to check.
   * @return true if a Course entity exists with the given name, false otherwise.
   */
  boolean existsByName(String name);

  /**
   * Checks if any Course entities exist with names in the provided iterable.
   *
   * @param names an iterable of course names to check.
   * @return true if any Course entities exist with the given names, false otherwise.
   */
  boolean existsAnyByNameIn(Iterable<String> names);
}
