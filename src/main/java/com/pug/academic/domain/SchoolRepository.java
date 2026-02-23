package com.pug.academic.domain;

import java.util.Optional;
import java.util.UUID;

/** Repository interface for Schools. */
public interface SchoolRepository {
  /**
   * Persist a school entityId.
   *
   * @param entity the school entityId to persist
   * @return the persisted school entityId
   */
  School persist(School entity);

  /**
   * Update a school entityId.
   *
   * @param entity the school entityId with updated information
   */
  void update(School entity);

  /**
   * Delete a school by its ID.
   *
   * @param id the UUID of the school to delete
   * @return true if the school was successfully deleted, false if it was not found
   */
  boolean deleteById(UUID id);

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
   * Check if a school exists by its name.
   *
   * @param name the name of the school to check
   * @return true if a school with the given name exists, false otherwise
   */
  boolean existsByName(String name);
}
