package com.pug.geo.domain;

import com.pug.geo.infra.persistence.CitiesEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing CitiesEntity objects. */
public interface CitiesRepository {
  /**
   * Persists a city entity.
   *
   * @param city the city entity to persist.
   */
  void persist(CitiesEntity city);

  /**
   * Persists multiple city entities.
   *
   * @param cities the iterable of city entities to persist.
   */
  void persistAll(Iterable<CitiesEntity> cities);

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
   * @param id the unique identifier of the city.
   * @return an Optional containing the city entity if found, otherwise empty.
   */
  Optional<CitiesEntity> findOptionalById(UUID id);

  /**
   * Finds a city entity by its IBGE code.
   *
   * @param ibgeCodeDigits the IBGE code digits of the city.
   * @return an Optional containing the city entity if found, otherwise empty.
   */
  Optional<CitiesEntity> findOptionalByIbgeCode(String ibgeCodeDigits);

  /**
   * Lists all city entities.
   *
   * @return a list of all city entities.
   */
  List<CitiesEntity> listAllCities();

  /**
   * Searches for city entities by name.
   *
   * @param key the search key for the city name.
   * @return a list of city entities matching the given name.
   */
  List<CitiesEntity> searchByName(String key);

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
  boolean existsAnyByIbgeCodeIn(Collection<String> ibges);
}
