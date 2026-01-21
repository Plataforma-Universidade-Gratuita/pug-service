package com.pug.geo.infra.read;

import com.pug.geo.infra.read.dtos.CityView;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Queries related to City.
 */
public interface ICityQueries {
  /**
   * Finds a CityView by its ID.
   *
   * @param id the UUID of the city.
   * @return an Optional containing the CityView if found, or empty if not found.
   */
  Optional<CityView> findOptionalById(UUID id);

  /**
   * Finds a CityView by its IBGE code.
   *
   * @param ibgeCode the IBGE code of the city.
   * @return an Optional containing the CityView if found, or empty if not found.
   */
  Optional<CityView> findOptionalByIbgeCode(String ibgeCode);

  /**
   * Lists all CityView entries.
   *
   * @return a list of all CityView entries.
   */
  List<CityView> listAllCities();

  /**
   * Searches for CityView entries by name.
   *
   * @param key the search key for the city name.
   * @return a list of CityView entries matching the search key.
   */
  List<CityView> searchByName(String key);
}