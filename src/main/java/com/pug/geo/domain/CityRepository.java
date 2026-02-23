package com.pug.geo.domain;

import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing City domain objects. */
public interface CityRepository {

  /**
   * Persists a city entity.
   *
   * @param city the city entity to persist.
   * @return the persisted city entity.
   */
  City persist(City city);

  /**
   * Updates an existing city entity.
   *
   * @param updated the city entity with updated information.
   */
  void update(City updated);

  /**
   * Deletes a city entity by its unique identifier.
   *
   * @param id the unique identifier of the city to delete.
   * @return true if the city was successfully deleted, false if the city was not found.
   */
  boolean deleteById(UUID id);

  /**
   * Finds a city entity by its unique identifier.
   *
   * <p>Note: The returned City may contain validation errors (check {@code city.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @param id the unique identifier of the city.
   * @return an Optional containing the city entity if found.
   */
  Optional<City> findOptionalById(UUID id);

  /**
   * Checks if a city exists by its IBGE code.
   *
   * @param ibgeCodeDigits the IBGE code digits of the city.
   * @return true if a city with the given IBGE code exists, false otherwise.
   */
  boolean existsByIbgeCode(String ibgeCodeDigits);
}
