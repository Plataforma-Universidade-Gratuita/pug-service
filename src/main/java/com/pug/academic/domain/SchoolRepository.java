package com.pug.academic.domain;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for Schools. */
public interface SchoolRepository {
  /**
   * Persist a school entity.
   *
   * @param school the school entity to persist
   * @return the persisted school entity
   */
  School persist(School school);

  /**
   * Persist multiple school entities.
   *
   * @param schools the iterable of school entities to persist
   * @return the list of persisted school entities
   */
  List<School> persistAll(Iterable<School> schools);

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
   */
  Optional<School> findOptionalById(UUID id);

  /**
   * List all schools.
   *
   * @return a list of all schools
   */
  List<School> listAllSchools();

  /**
   * Search for schools by name.
   *
   * @param key the search key for the school name
   * @return a list of schools matching the search key
   */
  List<School> searchByName(String key);

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
   * @param names the collection of school names to check
   * @return true if any schools with the given names exist, false otherwise
   */
  boolean existsAnyByNameIn(Collection<String> names);

  /**
   * Update a school entity.
   *
   * @param updated the school entity with updated information
   */
  void update(School updated);
}
