package com.pug.geo.service;

import com.pug.geo.infra.read.dtos.CityView;
import com.pug.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Application service interface dedicated exclusively to querying Geographic data.
 * <p>
 * Following CQRS principles, this service handles the "Query" operations. It bypasses
 * complex domain logic and instantiates lightweight {@link CityView} Data Transfer Objects
 * directly from the underlying data store or search indices. This is heavily optimized
 * for fast, read-only API responses.
 */
public interface CityReadService {

  /**
   * Retrieves a read-only projection of a city based on its unique identifier.
   *
   * @param id the unique identifier (UUID) of the requested city
   * @return the populated {@link CityView} DTO
   * @throws ResourceNotFoundException if no city matches the provided ID
   */
  CityView getViewById(UUID id);

  /**
   * Retrieves a read-only projection of a city based on its natural natural key (IBGE code).
   *
   * @param ibgeCode the exact 7-digit IBGE code of the requested city
   * @return the populated {@link CityView} DTO
   * @throws ResourceNotFoundException if no city matches the provided IBGE code
   */
  CityView getViewByIbgeCode(String ibgeCode);

  /**
   * Retrieves a comprehensive list of all cities registered in the system.
   * <p>
   * <i>Note:</i> This method returns the entire dataset. It should be used judiciously
   * in contexts where the dataset size is known to be safely bounded.
   *
   * @return a {@link List} containing all available {@link CityView} entries
   */
  List<CityView> listViews();

  /**
   * Executes a robust full-text search against the names of registered cities.
   * <p>
   * Leverages advanced text analysis (e.g., Elasticsearch via Hibernate Search) to provide
   * fuzzy matching, accent-insensitivity, and predictive autocomplete capabilities.
   * The results are automatically sorted by relevance score.
   *
   * @param q the raw search string or partial name provided by the client
   * @return a scored and sorted {@link List} of matching {@link CityView} entries
   */
  List<CityView> search(String q);
}