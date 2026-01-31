package com.pug.academic.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for Schools. */
public interface SchoolRepository {
  /**
   * Persist a school entity.
   *
   * @param entity the school entity to persist
   * @return the persisted school entity
   */
  School persist(School entity);

  /**
   * Persist multiple school entities.
   *
   * @param entities the iterable of school entities to persist
   * @return the list of persisted school entities
   */
  List<School> persistAll(Iterable<School> entities);

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
   * <p>Note: The returned School may contain validation errors (check {@code school.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @param id the UUID of the school to find
   * @return an Optional containing the found school or empty if not found
   */
  Optional<School> findOptionalById(UUID id);

  /**
   * Find a school by its name.
   *
   * <p>Note: The returned School may contain validation errors (check {@code school.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @param name the name of the school to find
   * @return an Optional containing the found school or empty if not found
   */
  Optional<School> findOptionalByName(String name);

  /**
   * List all schools.
   *
   * <p>Note: The returned Schools may contain validation errors (check {@code school.hasErrors()})
   * if the stored data is inconsistent with current domain rules.
   *
   * @return a list of all schools
   */
  List<School> listAllSchools();

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
