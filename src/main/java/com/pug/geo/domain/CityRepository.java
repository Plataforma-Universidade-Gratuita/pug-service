package com.pug.geo.domain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for querying {@link City} aggregate roots.
 *
 * <p>Because Cities are treated as a static geographic dictionary populated via database
 * migrations, this repository exposes strictly read-only domain operations.
 */
public interface CityRepository {

  /**
   * Retrieves a {@link City} by its unique identifier.
   *
   * @param id the unique identifier (UUID) of the city
   * @return an {@link Optional} containing the {@link City} if found, or {@link Optional#empty()}
   */
  Optional<City> findOptionalById(UUID id);
}
