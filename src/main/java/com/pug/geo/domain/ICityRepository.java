package com.pug.geo.domain;

import com.pug.shared.exceptions.AppValidationException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for managing City domain objects. */
public interface ICityRepository {
  /**
   * Persists a city entity.
   *
   * @param city the city entity to persist.
   * @return the persisted city entity.
   * @throws AppValidationException if the persisted entity cannot be converted back to a valid
   *     domain object (indicating data integrity issue).
   */
  City persist(City city) throws AppValidationException;

  /**
   * Persists multiple city entities.
   *
   * @param cities the iterable of city entities to persist.
   * @return the list of persisted city entities.
   * @throws AppValidationException if any persisted entity cannot be converted back to a valid
   *     domain object (indicating data integrity issue).
   */
  List<City> persistAll(Iterable<City> cities) throws AppValidationException;

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
   * @param id the unique identifier of the city.
   * @return an Optional containing the city entity if found and valid, otherwise empty.
   * @throws AppValidationException if a CityEntity is found but its data is inconsistent with
   *     domain rules, preventing the creation of a valid domain object.
   */
  Optional<City> findOptionalById(UUID id) throws AppValidationException;

  /**
   * Finds a city entity by its IBGE code.
   *
   * @param ibgeCodeDigits the IBGE code digits of the city.
   * @return an Optional containing the city entity if found and valid, otherwise empty.
   * @throws AppValidationException if a CityEntity is found but its data is inconsistent with
   *     domain rules, preventing the creation of a valid domain object.
   */
  Optional<City> findOptionalByIbgeCode(String ibgeCodeDigits) throws AppValidationException;

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
