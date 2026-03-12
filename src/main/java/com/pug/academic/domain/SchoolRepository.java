package com.pug.academic.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link School} aggregate roots.
 *
 * <p>This interface defines the contract for persisting, retrieving, updating, and deleting
 * academic schools (or departments). It abstracts the underlying data storage mechanism to maintain
 * a pure, infrastructure-agnostic domain model.
 */
public interface SchoolRepository {

  /**
   * Removes a {@link School} from the repository based on its unique identifier.
   *
   * @param id the unique identifier (UUIDv7) of the school to delete
   * @return {@code true} if the school was successfully deleted, {@code false} if it was not found
   */
  boolean deleteById(UUID id);

  /**
   * Checks whether a {@link School} with the specified name already exists in the repository.
   *
   * @param name the exact name of the school
   * @return {@code true} if a school with the given name exists, {@code false} otherwise
   */
  boolean existsByName(String name);

  /**
   * Retrieves a {@link School} by its unique identifier.
   *
   * <p>When a school is reconstituted from the persistence layer, it typically undergoes the same
   * domain validations as a newly created aggregate. Therefore, the returned {@link School} might
   * contain validation errors (verifiable via {@link School#hasFieldErrors()}) if the stored data
   * violates current domain rules.
   *
   * @param id the unique identifier (UUID) of the school
   * @return an {@link Optional} containing the found {@link School}, or {@link Optional#empty()} if
   *     not found
   */
  Optional<School> findOptionalById(UUID id);

  /**
   * Persists a newly created {@link School} aggregate into the repository.
   *
   * @param entity the {@link School} aggregate to persist
   * @return the fully persisted {@link School} instance
   */
  School persist(School entity);

  /**
   * Updates the state of an existing {@link School} aggregate in the repository.
   *
   * @param entity the {@link School} instance containing the updated state
   */
  void update(School entity);
}
