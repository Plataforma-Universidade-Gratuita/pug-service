package com.pug.geo.domain;

import com.pug.geo.infra.persistence.CitiesEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing CitiesEntity objects.
 */
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
  Optional<CitiesEntity> findByIbgeCode(String ibgeCodeDigits);

  /**
   * Lists all city entities.
   *
   * @return a list of all city entities.
   */
  List<CitiesEntity> listAllCities();

  /**
   * Searches for city entities by name.
   *
   * @param name the name of the city to search for.
   * @return a list of city entities matching the given name.
   */
  List<CitiesEntity> searchByName(String name);
}
