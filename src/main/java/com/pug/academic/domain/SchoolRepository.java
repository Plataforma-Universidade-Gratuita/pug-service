package com.pug.academic.domain;

import com.pug.shared.exceptions.AppValidationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Schools.
 */
public interface SchoolRepository {
  /**
   * Persist a school entity.
   *
   * @param entity the school entity to persist
   * @return the persisted school entity
   * @throws AppValidationException if the persisted entity cannot be converted back.
   */
  School persist(School entity) throws AppValidationException;

  /**
   * Persist multiple school entities.
   *
   * @param entities the iterable of school entities to persist
   * @return the list of persisted school entities
   * @throws AppValidationException if any persisted entity cannot be converted back.
   */
  List<School> persistAll(Iterable<School> entities) throws AppValidationException;

  /**
   * Update a school entity.
   *
   * @param entity the school entity with updated information
   */
  void update(School entity);

  /**
   * Delete schools by their IDs.
   *
   * @param ids the iterable of UUIDs of the schools to delete
   * @return the number of schools deleted
   */
  long deleteByIds(Iterable<UUID> ids);

  /**
   * Find a school by its ID.
   *
   * @param id the UUID of the school to find
   * @return an Optional containing the found school or empty if not found
   * @throws AppValidationException if the found entity cannot be converted.
   */
  Optional<School> findOptionalById(UUID id) throws AppValidationException;

  /**
   * Find a school by its name.
   *
   * @param name the name of the school to find
   * @return an Optional containing the found school or empty if not found
   * @throws AppValidationException if the found entity cannot be converted.
   */
  Optional<School> findOptionalByName(String name) throws AppValidationException;

  /**
   * List all schools.
   *
   * @return a list of all schools
   * @throws AppValidationException if any found entity cannot be converted.
   */
  List<School> listAllSchools() throws AppValidationException;

  /**
   * Check if a school exists by its name.
   *
   * @param name the name of the school to check
   * @return true if a school with the given name exists, false otherwise
   */
  boolean existsByName(String name);

  /**
   * Check if any schools exist by their names.
   *
   * @param names the iterable of school names to check
   * @return true if any schools with the given names exist, false otherwise
   */
  boolean existsAnyByNameIn(Iterable<String> names);
}