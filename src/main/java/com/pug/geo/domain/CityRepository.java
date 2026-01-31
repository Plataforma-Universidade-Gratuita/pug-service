package com.pug.geo.domain;

import java.util.List;
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
   * Persists multiple city entities.
   *
   * @param cities the iterable of city entities to persist.
   * @return the list of persisted city entities.
   */
  List<City> persistAll(Iterable<City> cities);

  /**
   * Updates an existing city entity.
   *
   * @param updated the city entity with updated information.
   */
  void update(City updated);

  /**
   * Deletes city entities by their unique identifiers.
   *
   * @param ids the iterable of unique identifiers of the cities to delete.
   * @return the number of entities deleted.
   */
  long deleteByIds(Iterable<UUID> ids);

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
   * Finds a city entity by its IBGE code.
   *
   * <p>Note: The returned City may contain validation errors (check {@code city.hasErrors()}) if
   * the stored data is inconsistent with current domain rules.
   *
   * @param ibgeCodeDigits the IBGE code digits of the city.
   * @return an Optional containing the city entity if found.
   */
  Optional<City> findOptionalByIbgeCode(String ibgeCodeDigits);

  /**
   * Checks if a city exists by its IBGE code.
   *
   * @param ibgeCodeDigits the IBGE code digits of the city.
   * @return true if a city with the given IBGE code exists, false otherwise.
   */
  boolean existsByIbgeCode(String ibgeCodeDigits);

  /**
   * Checks if any city exists with the given IBGE codes.
   *
   * @param ibges a collection of IBGE code digits.
   * @return true if any city with the given IBGE code exists, false otherwise.
   */
  boolean existsAnyByIbgeCodeIn(Iterable<String> ibges);
}
