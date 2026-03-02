package com.pug.geo.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for managing {@link City} aggregate roots.
 * <p>
 * This interface defines the contract for persisting, retrieving, updating, and deleting
 * city entities. It abstracts the underlying data storage mechanism (e.g., database)
 * to maintain a pure, infrastructure-agnostic domain model.
 */
public interface CityRepository {

  /**
   * Persists a newly created {@link City} aggregate into the repository.
   *
   * @param city the {@link City} aggregate to persist
   * @return the fully persisted {@link City} instance
   */
  City persist(City city);

  /**
   * Updates the state of an existing {@link City} aggregate in the repository.
   *
   * @param updated the {@link City} instance containing the updated state
   */
  void update(City updated);

  /**
   * Removes a {@link City} from the repository based on its unique identifier.
   * <p>
   * <i>Note:</i> Certain default cities (e.g., Jaraguá do Sul and Joinville) are protected
   * from deletion by business rules and database constraints to maintain system integrity.
   *
   * @param id the unique identifier (UUIDv7) of the city to delete
   * @return {@code true} if the city was successfully deleted, {@code false} if the city was not found
   */
  boolean deleteById(UUID id);

  /**
   * Retrieves a {@link City} by its unique identifier.
   * <p>
   * When a city is reconstituted from the persistence layer, it typically undergoes
   * the same domain validations as a newly created entity. Therefore, the returned {@link City}
   * might contain validation errors (verifiable via {@link City#hasFieldErrors()})
   * if the stored data violates current domain rules.
   *
   * @param id the unique identifier (UUID) of the city
   * @return an {@link Optional} containing the {@link City} if found, or {@link Optional#empty()} if not
   */
  Optional<City> findOptionalById(UUID id);

  /**
   * Checks whether a {@link City} with the specified IBGE code already exists in the repository.
   * <p>
   * This is primarily used by domain services or use cases to enforce natural key
   * uniqueness constraints before persisting a new city or updating an existing one's code.
   *
   * @param ibgeCodeDigits the 7-digit raw IBGE code string
   * @return {@code true} if a city with the given IBGE code exists, {@code false} otherwise
   */
  boolean existsByIbgeCode(String ibgeCodeDigits);
}